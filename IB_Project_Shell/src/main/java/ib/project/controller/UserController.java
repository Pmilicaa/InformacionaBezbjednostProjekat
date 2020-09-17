package ib.project.controller;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.Principal;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.bouncycastle.cert.X509CRLHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import java.security.Principal;

import ib.project.certificate.CRLManager;
import ib.project.certificate.CertificateGenerator;
import ib.project.certificate.CertificateWriter;
import ib.project.model.Authority;
import ib.project.model.IssuerData;
import ib.project.model.SubjectData;
import ib.project.model.User;
import ib.project.repos.AuthorityRepository;
import ib.project.repos.UserRepository;
import keystore.KeyStoreReader;
import keystore.KeyStoreWriter;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;
    
	@Autowired
	private PasswordEncoder bCryptPasswordEncoder;

	
	@GetMapping(path = "users/all")
    public @ResponseBody Iterable<User> getAllUsers(){
        return userRepository.findAll();
    }

	@PostMapping(value="/create/{email}/{password}")
	public void SignedCertificateGenerator(@PathVariable("email") String email,@PathVariable("password") String password,KeyStoreReader keyStoreReader) {
		
		System.out.println(email);
		System.out.println(password);
		CertificateGenerator cg = new CertificateGenerator();


		try {
			//Kreiranje CA sertifikata, za kojeg je vezana CRL
			SimpleDateFormat iso8601Formater = new SimpleDateFormat("yyyy-MM-dd");
			Date startDate = iso8601Formater.parse("2019-03-10");
			Date endDate = iso8601Formater.parse("2021-03-10");
			KeyPair keyPairCA = cg.generateKeyPair();
			
			//osnovni podaci za issuer
			IssuerData issuerData = new IssuerData("FTN", "Fakultet tehnickih nauka", "KzI", "RS",  "ftnmail@uns.ac.rs", "123445", keyPairCA.getPrivate());
			SubjectData subjectData1 = new SubjectData(keyPairCA.getPublic(), issuerData.getX500name(), "1", startDate, endDate); 
			
			
			X509Certificate certCA = CertificateGenerator.generateCertificate(issuerData, subjectData1);
			X509CRLHolder crlHolder = CRLManager.createCRL(certCA, keyPairCA.getPrivate());
			
			//Kreiranje sertifikata potpisanog od strane CA
			startDate = iso8601Formater.parse("2017-03-31");
			endDate = iso8601Formater.parse("2021-03-31");
			
			KeyPair keyPair2 = cg.generateKeyPair();
			
			SubjectData subjectData2 = new SubjectData(keyPair2.getPublic(), email, email, email, email, email, email, "1", startDate, endDate); 
			X509Certificate cert = CertificateGenerator.generateCertificate(issuerData, subjectData2);
			String certPath = "./data/" + email + ".cer";
			CertificateWriter.saveCertificateBase64Encoded(cert, certPath);
			KeyStoreWriter keyStoreWriter = new KeyStoreWriter();
			KeyStore keyStore = keyStoreWriter.loadKeyStore(null, (email + "1").toCharArray());
			keyStoreWriter.write(keyStore,email, keyPair2.getPrivate(), (email + "10").toCharArray(), cert);
			keyStoreWriter.saveKeyStore(keyStore ,"C:\\Users\\Milica\\Desktop\\ibproj\\IB_Project_Shell\\data\\" + email + ".jks", (email + "10").toCharArray());
			
			
			System.out.println("ISSUER: " + cert.getIssuerX500Principal().getName());
			System.out.println("SUBJECT: " + cert.getSubjectX500Principal().getName());
			System.out.println("Sertifikat:");
			System.out.println("-------------------------------------------------------");
			System.out.println(cert);
			System.out.println("-------------------------------------------------------");
			
			/*
			 * crlHolder = CRLManager.updateCRL(crlHolder, certCA, keyPairCA.getPrivate(),
			 * cert.getSerialNumber(),
			 * org.bouncycastle.asn1.x509.CRLReason.privilegeWithdrawn);
			 * 
			 * X509CRL crl = CRLManager.CRLFromCrlHolder(crlHolder);
			 * System.out.println(crl);
			 */
			
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}

    @PostMapping(path = "users/register")
    public @ResponseBody ResponseEntity<?> registrovaniUser(@RequestParam String email, @RequestParam String password) {
    	Authority authority = authorityRepository.findByName("REGULAR").get();
		
		if(email==null | password == null | email.equals("") | password.equals("") ) {
			return new ResponseEntity("Polja ne mogu biti prazna", HttpStatus.BAD_REQUEST);
		}else {
			Optional<User> userSaProslijedjenimEmailom = userRepository.findByEmail(email);
			if(userSaProslijedjenimEmailom.isPresent()) {
				return new ResponseEntity("Vec postoji korisnik sa tim emailom", HttpStatus.BAD_REQUEST);
			}else {
				User user = new User();
				user.setActive(false);
				user.setEmail(email);
				user.setPassword(bCryptPasswordEncoder.encode(password));
				user.setCertificate("");
				user.getUserAuthorities().add(authority);
				
				user.setCertificate("C:\\Users\\Milica\\Desktop\\IB_Project_Shell\\data\\" + user.getEmail() + ".cer");
				
				userRepository.save(user);
				/*
				 * String idKorisnika = user.getId().toString();
				 * kreiranjeDirektorijuma(idKorisnika);
				 */
				return new ResponseEntity("Uspjesna registracija", HttpStatus.OK);
				
			}
		}
    }
    @PostMapping(path = "activate/{id}")
    public @ResponseBody ResponseEntity<?> updatovanjeLicnihPodataka(@PathVariable(value="id")Long id) {
		
    	if(id==null) {
    		return new ResponseEntity("Id ne moze biti prazan u pathu", HttpStatus.BAD_REQUEST);
    	}
    	Optional<User> korisnik = userRepository.findById(id);
    	boolean aktivnost = korisnik.get().isActive();
    	if(!aktivnost) {
    		korisnik.get().setActive(true);
    	}else {
    		System.out.println("vec je aktivan ");
    	}
    	
    	userRepository.save(korisnik.get());
		String poruka = "Uspjesna promjena licnih podataka";
		return new ResponseEntity(poruka, HttpStatus.OK);

    }
    
    @GetMapping(value = "/inactive")
	public ResponseEntity<List<User>>getInactive(){
		List<User> inactive = new ArrayList<>();
		List<User> users = userRepository.findByActiveFalse();
		for (User user : users) {
			inactive.add(user);
		}
		return new ResponseEntity<>(inactive,HttpStatus.OK);
	}


	@GetMapping(value = "/active")
	public ResponseEntity<List<User>> getActive(){
		List<User> active = new ArrayList<>();
		List<User> users = userRepository.findByActiveTrue();
		for (User user : users) {
			active.add(user);
		}
		return new ResponseEntity<>(active,HttpStatus.OK);
	}
	
	private void kreiranjeDirektorijuma(String id) {

		java.nio.file.Path korisnickiDirek = Paths.get("data/" + id);
		
		try {
			Files.createDirectory(korisnickiDirek);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}
    @GetMapping("/user/{userId}")
	public Optional<User> loadById(@PathVariable Long userId) {
		return this.userRepository.findById(userId);
	}

	@GetMapping("/user/all")
	public List<User> loadAll() {
		return this.userRepository.findAll();
	}

	
}
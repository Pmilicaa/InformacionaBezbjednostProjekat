package ib.project.certificate;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CRLConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import ib.project.model.IssuerData;
import ib.project.model.SubjectData;

import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.X509CRLHolder;

/**
 * Klasa koja sluzi za generisanje Self-Signed sertifikata (vlasnik sam sebi potpisuje sertifikat).
 *
 */
public class CertificateGenerator {

	// static blok - sluzi sa staticku inicijalizaciju klase. 
	// Ovaj kod se izvrsava samo jednom - pre prvog instanciranja objekta klase CertificateGenerator.
	// Vise informacija: https://www.geeksforgeeks.org/g-fact-79/ i https://www.edureka.co/blog/static-block-in-java/

	// registracija providera - koristimo BouncyCastle provider
	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	/**
	 * Metoda koja sluzi za generisanje sertifikata.
	 * 
	 * @param issuerData - podaci o izdavaocu sertifikata
	 * @param subjectData - podaci o fizickom ili pravnom licu kojem se izdjae sertifikat
	 * 
	 * @return X509 sertifikat
	 * 
	 */
	public static X509Certificate generateCertificate(ib.project.model.IssuerData issuerData, ib.project.model.SubjectData subjectData) {
		try {
			 // Posto klasa za generisanje sertifiakta ne moze da primi direktno privatni
			 // kljuc, pravi se builder za objekat koji ce sadrzati privatni kljuc. 	
			 // Objekat ce se ce se koristitit za potpisivanje sertifikata. 
			 // Parametar konstruktora je definicija algoritma koji ce se koristi za potpisivanje sertifiakta.
			JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");

			// postavljanje provider-a koji se koristi - BouncyCastleProvider
			builder = builder.setProvider("BC");

			// objekat koji sadrzi privatni kljuc i koji se koristi za potpisivanje sertifikata.
			// Koristimo privatni kljuc izdavaca sertifikata!!!
			ContentSigner contentSigner = builder.build(issuerData.getPrivateKey());

			// postavljaju se podaci za generisanje sertifiakta
			X509v3CertificateBuilder certtificateBuilder = new JcaX509v3CertificateBuilder(
					issuerData.getX500name(),						// ime izdavaca sertifikata
					new BigInteger(subjectData.getSerialNumber()), 	// serijski broj sertifikata
					subjectData.getStartDate(), 						// od kog trenutka sertifikat vazi (pre ovog datuma sertifikat ne vazi)
					subjectData.getEndDate(),						// do kog trenutka sertifikat vazi (posle ovog datuma sertifikat ne vazi)
					subjectData.getX500name(), 						// ime lica kojem se sertifikat izdaje
					subjectData.getPublicKey());						// javni kljuc koji se vezuje za sertifikat
			
			// generisanje sertifikata...
			// koristimo certtificateBuilder objekat koji smo inicijalizovali sa svim potrebnim informacijama. Kao parametr build metode
			// prosledjujemo contentSigner objekat koji sadrzi privatni kljuc izdavaca sertifikata kojim se vrsi potpisivanje sertifikata 
			X509CertificateHolder certificateHolder = certtificateBuilder.build(contentSigner);

			// certtificateBuilder generise sertifikat kao objekat klase X509CertificateHolder.
			// Sada je potrebno certificateHolder konvertovati u X509 sertifikat -> za to se koristi certificateConverter objekat
			JcaX509CertificateConverter certificateConverter = new JcaX509CertificateConverter();
			
			// postavljanje provider-a koji se koristi - BouncyCastleProvider
			certificateConverter = certificateConverter.setProvider("BC");

			// konvertuje objekat u X509 sertifikat i vraca ga kao pobvratnu vrednost metode
			return certificateConverter.getCertificate(certificateHolder);

		} catch (IllegalArgumentException | IllegalStateException | OperatorCreationException | CertificateException e){
			e.printStackTrace();
		}
		
		return null;
	}

	/**
	 * Metoda sluzi za generisanje para kljuceva - privatni i javni kljuc.
	 * 
	 * @return - Par kljuceva privatni i javni
	 * 
	 * Napomena: voditi racuna da se privatni i javni kljuc UVEK moraju koristiti u paru, u suprotnom kriptografski algoritmi 
	 * koji koriste privatne i javne kljuceve nece ispravno raditi.
	 */
	public KeyPair generateKeyPair() {
		try {
			// generator para kljuceva
			KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
			
			// inicijalizacija generatora, 1024 bitni kljuc
			keyGenerator.initialize(1024);

			// generise par kljuceva
			KeyPair pair = keyGenerator.generateKeyPair();

			return pair;

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	public  X509Certificate generateSelfSignedCertificate(KeyPair keyPair) {
		//self signed sertifikat
		SimpleDateFormat iso8601Formater = new SimpleDateFormat("yyyy-MM-dd");

		Date startDate = new Date();
		Date endDate = new Date();
		try {
			
			//datumi
			startDate = iso8601Formater.parse("2020-05-15");
			endDate = iso8601Formater.parse("2021-12-31");
		}
		 catch (ParseException e) {
				e.printStackTrace();
			}

			
			//podaci o vlasniku i izdavacu posto je self signed 
			//klasa X500NameBuilder pravi X500Name objekat koji nam treba
			X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
		    builder.addRDN(BCStyle.CN, "Milica Pajic");
		    builder.addRDN(BCStyle.SURNAME, "Pajic");
		    builder.addRDN(BCStyle.GIVENNAME, "Milica");
		    builder.addRDN(BCStyle.O, "UNS-FTN");
		    builder.addRDN(BCStyle.OU, "Katedra za informatiku");
		    builder.addRDN(BCStyle.C, "RS");
		    builder.addRDN(BCStyle.E, "pmilica@uns.ac.rs");
		    //UID (USER ID) je ID korisnika
		    builder.addRDN(BCStyle.UID, "123445");
			
			
			//Serijski broj sertifikata
			String serialNumber="1";
			// postavljanje podataka o izdavacu sertifikata 
			IssuerData issuerData = new IssuerData(keyPair.getPrivate(), builder.build());
			//kreiraju se podaci za vlasnika
			SubjectData subjectData = new SubjectData(keyPair.getPublic(), builder.build(), serialNumber, startDate, endDate);
			
			//generise se CA sertifikat
			X509Certificate cert = generateCertificate(issuerData, subjectData);
			
			System.out.println("ISSUER: " + cert.getIssuerX500Principal().getName());
			System.out.println("SUBJECT: " + cert.getSubjectX500Principal().getName());
			System.out.println("Sertifikat:");
			System.out.println("-------------------------------------------------------");
			System.out.println(cert);
			System.out.println("-------------------------------------------------------");
			
			
		return cert;
	}
	

	/**
	 * Metoda koja sluzi za proveru da li je sertifikat ispravan.
	 * 
	 * @param certificate - Sertifikat koji se validira
	 * @param publicKey - javni kljuc koji se vrsi validacija sertifikata
	 * 
	 * Sertifikat se potpisuje privatnim kljucem izdavaca. Sertifikat jedino uspeÅ¡no moÅ¾e da se validira javnim kljucem izdavaca!
	 */
	private static boolean validateCertificate(X509Certificate certificate, PublicKey publicKey) {

		// ako validacija nije uspesna desice se exception
		try {
			certificate.verify(publicKey);
			System.out.println("\nVALIDACIJA SERTIFIKATA USPESNA!");
			return true;
		} catch (InvalidKeyException | CertificateException | NoSuchAlgorithmException | NoSuchProviderException
				| SignatureException e) {
			System.out.println("\nVALIDACIJA SERTIFIKATA NIJE USPESNA!");
			return false;
		}
	}
	
}

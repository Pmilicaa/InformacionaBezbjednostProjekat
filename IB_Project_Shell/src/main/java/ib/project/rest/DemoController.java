package ib.project.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ib.project.certificate.CertificateReader;
import ib.project.controller.UserController;
import ib.project.model.User;
import ib.project.repos.UserRepository;

@RestController
@RequestMapping(value = "/api/demo", produces = MediaType.APPLICATION_JSON_VALUE)
public class DemoController {

	@Value("${dataDir}")
	private String DATA_DIR_PATH;

	@Autowired
	private UserRepository userRepository;
	
	@PostMapping(path = "createFile")
	public ResponseEntity<String> createAFileInResources() throws IOException {

		byte[] content = "Content".getBytes();
		
		String directoryPath = getResourceFilePath(DATA_DIR_PATH).getAbsolutePath();
		Path path = Paths.get(directoryPath + File.separator + "demo.txt");
		
		Files.write(path, content);
		return new ResponseEntity<String>(path.toString(), HttpStatus.OK);
	}

	@GetMapping(path = "download/{filename}")
	public ResponseEntity<byte[]> download(@AuthenticationPrincipal User userDetails,@PathVariable("filename") String filename) {
		
		Optional<User> korisnikovFile = userRepository.findByEmail(filename);
		System.out.println("uuuuuuuuuuuuuuder" +userDetails.getEmail());
		System.out.println(korisnikovFile.get().getEmail());
		if(userDetails.getEmail().contains(korisnikovFile.get().getEmail())) {
			String directoryPath = getResourceFilePath(DATA_DIR_PATH).getAbsolutePath();
			Path path = Paths.get(directoryPath + File.separator + "demo.txt");
			ClassLoader classloader = Thread.currentThread().getContextClassLoader();


			String myUrl = "C:\\Users\\Milica\\Desktop" + "/" + filename+".jks";
			System.out.println(myUrl);
			URL urlPath = classloader.getResource(myUrl);

			System.out.println("urlPath " + urlPath);

			File file = null;
			try {
				file = new File(myUrl);
			}
			catch (Exception e) {
				System.out.println("uuuuuuuuuuuuuuder" +userDetails.getEmail());

				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			} 
			
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("filename", korisnikovFile.get().getEmail() + ".jks");

			byte[] bFile = readBytesFromFile(file.toString());

			return ResponseEntity.ok().headers(headers).body(bFile);
		}else {
			Certificate certificate = CertificateReader.getCertificatesFromBase64EncFile("C:\\Users\\Milica\\Desktop\\" + korisnikovFile.get().getEmail() + ".cer");
			
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("filename", korisnikovFile.get().getEmail() + ".cer");

			byte[] bFile = new byte[0];
			try {
				bFile = certificate.getEncoded();
				return ResponseEntity.ok().headers(headers).body(bFile);
			} catch (CertificateEncodingException e) {
				e.printStackTrace();
			}
			System.out.println("uuuuuuuuuuuuuuder" +userDetails.getEmail());

			return new ResponseEntity<byte[]>(HttpStatus.BAD_REQUEST);
			
		}
		
		
	
	}
	
	
	private static byte[] readBytesFromFile(String filePath) {

		FileInputStream fileInputStream = null;
		byte[] bytesArray = null;
		try {

			File file = new File(filePath);
			bytesArray = new byte[(int) file.length()];

			// read file into bytes[]
			fileInputStream = new FileInputStream(file);
			fileInputStream.read(bytesArray);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return bytesArray;
	}

	public File getResourceFilePath(String path) {
		
		URL url = this.getClass().getClassLoader().getResource(path);
		File file = null;

		try {
			
			file = new File(url.toURI());
		} catch (Exception e) {
			file = new File(url.getPath());
		}

		return file;
	}
}

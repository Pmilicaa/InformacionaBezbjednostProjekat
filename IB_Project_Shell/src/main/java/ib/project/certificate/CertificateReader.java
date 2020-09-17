package ib.project.certificate;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Klasa koja sluzi za citanje sertifikata iz fajla
 *
 */
public class CertificateReader {

	/**
	 * Metoda sluzi za citanje sertifikata iz Base64 enkodovanog formata.
	 * 
	 * @param filePath - putanja do fajla
	 * 
	 * @return Lista sertifikata koji su procitani
	 * 
	 * cita se sertifikat po sertifikat i vrsi se pozicioniranje na pocetak sledeceg.
	 * svaki certifikat se nalazi izmedju
	 * -----BEGIN CERTIFICATE-----,
	 * i
	 * -----END CERTIFICATE-----.
	 */
	public static Certificate getCertificatesFromBase64EncFile(String path) {
		try {
			BufferedInputStream stream = new BufferedInputStream(new FileInputStream(path));
			CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
			
			if (stream.available() > 0) {
				Certificate certificate = certFactory.generateCertificate(stream);
				return certificate;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	
	/**
	 * Metoda sluzi za citanje sertifikata iz binarnog formata.
	 * 
	 * @param filePath - putanja do fajla
	 * 
	 * @return Lista sertifikata koji su procitani
	 */
	public List<Certificate> getCertificatesFromBinEncFile(String filePath) {
		List<Certificate> certificates = new ArrayList<>();
		
		try {
			FileInputStream fis = new FileInputStream(filePath);
			
			// instanciranje factory objekta i postavljamo tip sertifikata da je X509.
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			
			// vade se svi sertifikati iz bajtovs i dodaju se u kolekciju
			Collection<?> c = cf.generateCertificates(fis);
			
			Iterator<?> i = c.iterator();
			while (i.hasNext()) {
				// kastovanje u tip koji nam treba
				Certificate certificate = (Certificate) i.next();
				
				// dodavanje sertifikata u listu
				certificates.add(certificate);
			}
		} catch (FileNotFoundException | CertificateException e) {
			e.printStackTrace();
		}
		
		return certificates;
	}
	
}

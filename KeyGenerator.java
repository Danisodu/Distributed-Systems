import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Program to read and write asymmetric key files
 */
public class KeyGenerator {

	public static void main(String[] args) throws Exception {

	    //como pomos o caminho relativo??

		final String privateKeyPath = "./Privatekey.txt";
		final String publicKeyPath= "./Publickey.txt";

	
		System.out.println("Generate and save keys");
		write(publicKeyPath, privateKeyPath);
		
	
	
	}

      	public static void write(String publicKeyPath, String privateKeyPath) throws Exception {

		// generate RSA key pair

		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");

		//o tamanho da chave gerada e 1024
		keyGen.initialize(1024);
		KeyPair key = keyGen.generateKeyPair();

		//chave publica para ficheiro
		byte[] pubEncoded = key.getPublic().getEncoded();  //getEncoded -> a chave no formato desejado a ser guardado no ficheiro
		writeFile(publicKeyPath, pubEncoded);

		//chave privada para ficheiro
		byte[] privEncoded = key.getPrivate().getEncoded();
		writeFile(privateKeyPath, privEncoded);
	}
	
	private static void writeFile(String path, byte[] content) throws FileNotFoundException, IOException {
		FileOutputStream fos = new FileOutputStream(path);
		fos.write(content);
		fos.close();
	}


}
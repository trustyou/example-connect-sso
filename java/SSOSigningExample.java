import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class SSOSigningExample {

	public static void main(String[] args) throws Exception {
		String privateKeyPath = "...full path to private .der key"; //Private key in der (Java reds .pem file in form of der)
		String publicKeyPath ="...full path to public .der key";	// Public Key (Java reds .pem file in form of der)
		
		// If you have a PEM file, you can convert it to a DER file that java needs
	    // This is the answer illustrating how we did it:
	    // https://stackoverflow.com/questions/8290435/convert-pem-traditional-private-key-to-pkcs8-private-key
		// 
		// This is the command from the stackoverflow post that did it
		// $ openssl pkcs8 -topk8 -inform PEM -outform DER -in ./my_private_key.pem -out ./my_private.der -nocrypt
		PrivateKey privateKey = getPrivateKey(privateKeyPath);
		// To get a public der file, use this command on the private .pem file
		// openssl pkcs8 -topk8 -inform PEM -outform DER -in ./private.pem -out ./private.der -nocrypt
	 	PublicKey publicKey = getPublicKey(publicKeyPath);
	 	
		String partner_id="your-partner-id";
		String oauth_token="the-hotel-id";
		String oauth_signature_method="RSA-SHA1";
		long oauth_timestamp = new java.util.Date().getTime();

		String oauth_nonce = java.util.UUID.randomUUID().toString();  // use any string here, it doesn't have to be a UUID, it just needs to be unique for each call
		String oauth_version="1.0";
		
		String login_endpoint = "https://analytics.staging.trustyou.com/connect/log_in?";  // For production code, a different URL will be provided by TrustYou
		
		String parameter_string =  // The ordering here is absolutely critical for constructing the signature
    			"oauth_consumer_key="+partner_id+
    			"&oauth_nonce="+oauth_nonce+
    			"&oauth_signature_method="+oauth_signature_method+
    			"&oauth_timestamp="+oauth_timestamp+
    			"&oauth_token="+oauth_token+
				"&oauth_version="+oauth_version+
				"&partner_id="+partner_id
				;
    	
    	String baseString = 
    			"GET&" +
    			URLEncoder.encode(login_endpoint, "utf-8")+ "&" +
    			URLEncoder.encode(parameter_string, "utf-8");
    
    	// Sing the base string with the private key, and encode the result according to the oauth specification
    	String oauth_signature = sign(privateKey, baseString);
		
    	// This will print a success or error message if the verification succeeded
		verify(baseString, oauth_signature, publicKey);
		
		// The order is NOT important here.
		String finalUrl = login_endpoint +
				"partner_id="+partner_id+
				"&oauth_token="+oauth_token+
				"&oauth_signature_method="+oauth_signature_method+
				"&oauth_signature="+oauth_signature+
				"&oauth_timestamp="+oauth_timestamp+
				"&oauth_nonce="+oauth_nonce+
				"&oauth_consumer_key="+partner_id+
				"&oauth_version="+oauth_version;
		
		// Use this URL to log in with TrustYou Connect
		System.out.println("The final url that you can use for logging in: " + finalUrl);
	}
	
	/**
	 * Helper for retrieving and parsing the private key file
	 * @param filename
	 * @return
	 * @throws Exception
	 */
	 public static PrivateKey getPrivateKey(String filename)
	    throws Exception {
	    
	    File f = new File(filename);
	    FileInputStream fis = new FileInputStream(f);
	    DataInputStream dis = new DataInputStream(fis);
	    byte[] keyBytes = new byte[(int)f.length()];
	    dis.readFully(keyBytes);
	    dis.close();
	    
	    PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
	    KeyFactory kf = KeyFactory.getInstance("RSA");
	    
	    return kf.generatePrivate(spec);
	  }
	 
	 /**
	  * Helper for testing whether the encryption worked and is verifiable
	  * @param signatureBaseString
	  * @param signature
	  * @param publicKeyBytes
	  * @throws Exception
	  */
	 public static void verify(String signatureBaseString, String signature, PublicKey publicKeyBytes) throws Exception {

		if (publicKeyBytes == null) {
			throw new UnsupportedOperationException("A public key must be provided to verify signatures.");
		}

		try {
			byte[] signatureBytes = java.util.Base64.getDecoder().decode(signature.getBytes("ascii"));
			Signature verifier = Signature.getInstance("SHA1withRSA");
			verifier.initVerify(publicKeyBytes);
			verifier.update(signatureBaseString.getBytes("ascii"));

			if (!verifier.verify(signatureBytes)) {
				System.err.println("Invalid signature for signature method ");
			} else {
				System.out.println("The Signature is Valid");
			}
		}

		catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
			
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
			
		} catch (InvalidKeyException e) {
			throw new IllegalStateException(e);
			
		} catch (SignatureException e) {
			throw new IllegalStateException(e);
		}
	}
	 
	public static PublicKey getPublicKey(String filename) throws Exception {

		File f = new File(filename);
		FileInputStream fis = new FileInputStream(f);
		DataInputStream dis = new DataInputStream(fis);
		byte[] keyBytes = new byte[(int) f.length()];
		dis.readFully(keyBytes);
		dis.close();

		X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePublic(spec);
	}

	/**
	 * This is the oauth signing algorithm. It's implemented as per the specification in the link below
	 * https://oauth.net/core/1.0/#signing_process
	 * 
	 * @param privateKey
	 * @param string
	 * @return
	 * @throws GeneralSecurityException
	 * @throws UnsupportedEncodingException
	 */
	public static String sign(PrivateKey privateKey, String string)
			throws GeneralSecurityException, UnsupportedEncodingException {

		Signature signer = Signature.getInstance("SHA1withRSA");
		signer.initSign(privateKey);
		byte[] stringBytes = string.getBytes("ascii");

		signer.update(stringBytes);

		// Step 1: sign
		byte[] unencodedSignature = signer.sign();
		// byte[] base64EncodedSignature = Base64.encodeBase64(unencodedSignature); //
		// For me this line throws a really weird error...

		// Step 2: base64 encode
		byte[] base64EncodedSignature = java.util.Base64.getEncoder().encode(unencodedSignature);

		// Step 3: make an ascii string
		String base64EncodedSignatureString = new String(base64EncodedSignature, "ascii");

		// Step 4: urlencode that string
		String signature = URLEncoder.encode(base64EncodedSignatureString, "ascii");

		return signature;
	}

}

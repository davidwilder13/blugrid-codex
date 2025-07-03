import java.io.FileInputStream;
import java.io.FileWriter;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

public class PublicKeyExtractor {

    public static void main(String[] args) throws Exception {
        String certificateFile = "publickey.cert";
        String keyId = "test-key-id"; // Use a unique key ID

        // Load the public key certificate
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        FileInputStream fis = new FileInputStream(certificateFile);
        X509Certificate cert = (X509Certificate) certFactory.generateCertificate(fis);
        RSAPublicKey publicKey = (RSAPublicKey) cert.getPublicKey();

        // Extract the modulus and exponent
        String modulus = Base64.getUrlEncoder().encodeToString(publicKey.getModulus().toByteArray());
        String exponent = Base64.getUrlEncoder().encodeToString(publicKey.getPublicExponent().toByteArray());

        // Create the JWKS JSON string
        String jwkTemplate = "{\n" +
                "  \"keys\": [\n" +
                "    {\n" +
                "      \"kty\": \"RSA\",\n" +
                "      \"use\": \"sig\",\n" +
                "      \"kid\": \"%s\",\n" +
                "      \"n\": \"%s\",\n" +
                "      \"e\": \"%s\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        String jwks = String.format(jwkTemplate, keyId, modulus, exponent);

        // Write the JWKS JSON string to a file
        try (FileWriter file = new FileWriter("jwks.json")) {
            file.write(jwks);
        }

        System.out.println("JWKS JSON file created successfully: jwks.json");
    }
}

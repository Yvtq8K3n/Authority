package pt.ipleiria.authority.model;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Connection {
    private Contact contact;
    private byte[] secretKey;

    public Connection(Contact c){
        this.contact =  c;
        //generateKey();

    }

    public boolean hasSecretKey() {
        return this.secretKey != null;
    }

    public void generateKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            SecureRandom secureRandom = new SecureRandom();

            keyGenerator.init(256, secureRandom);
            SecretKey secretKey = keyGenerator.generateKey();

            this.secretKey = Base64.getEncoder().encode(secretKey.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public byte[] getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(byte[] secretKey) {
        this.secretKey = secretKey;
    }

    public SecretKey getSecretKeyClass() {
        SecretKeySpec keySpec = new SecretKeySpec(Base64.getDecoder().decode(secretKey), "AES");
        return keySpec;
    }

    public Contact getContact() {
        return contact;
    }
}

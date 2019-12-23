package pt.ipleiria.authority.model;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.*;
import java.util.Base64;

public class KeyPairGen {
    private PrivateKey privateKey;
    private PublicKey publicKey;

    public KeyPairGen(String type, int keySize) throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(type);
        keyGen.initialize(keySize);
        KeyPair pair = keyGen.generateKeyPair();
        this.privateKey = pair.getPrivate();
        this.publicKey = pair.getPublic();
    }

    public void writeKeyToFile(String path) throws IOException {
        File f = new File(path);
        if(!f.exists()) {
            System.out.println("!EXISTS");
            f.getParentFile().mkdirs();
        }

        FileOutputStream fos = new FileOutputStream(f, true);
        fos.write(this.getPublicKeyB64());
        fos.write("\n".getBytes());
        fos.flush();
        fos.close();
    }

    public byte[] readKeyFromFile(String path) throws IOException {
        File f = new File(path);

        FileInputStream fis = new FileInputStream(f);
        byte[] key = fis.readAllBytes();
        fis.close();

        return key;
    }

    public byte[] encrypt(byte[] data, PublicKey key) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    public byte[] decrypt(byte[] data, PrivateKey key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public byte[] getPrivateKeyB64() {
        return Base64.getEncoder().encode(privateKey.getEncoded());
    }

    public byte[] getPublicKeyB64() {
        return Base64.getEncoder().encode(publicKey.getEncoded());
    }
}

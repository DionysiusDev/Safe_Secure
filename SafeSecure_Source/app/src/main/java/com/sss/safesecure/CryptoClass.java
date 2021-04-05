package com.sss.safesecure;

import android.util.Base64;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Purpose: this class allows for encrypting and decrypting data before saving into the database.
 */
public class CryptoClass {

    /**
     * Purpose: instantiates a boolean to check if the data is encrypted.
     * boolean iEncrypted.
     */
    boolean iEncrypted = false;
    /**
     * Purpose: instantiates a boolean to check if the secret key is encoded.
     * boolean iEncoded.
     */
    boolean iEncoded = false;
    /**
     * Purpose: instantiates a boolean to check if the secret key is decoded.
     * boolean iDecoded.
     */
    boolean iDecoded = false;

    /**
     * Purpose: returns boolean value based on if the data
     * - has run through the encrypt data method or not.
     * @return iEncrypted
     */
    public boolean dataEncrypted(){
        return iEncrypted;
    }

    /**
     * Purpose: returns boolean value based on if the key
     * - has run through the encode key method or not.
     * @return iEncoded
     */
    public boolean encodedKey(){
        return iEncoded;
    }

    /**
     * Purpose: returns boolean value based on if the key
     * - has run through the decode key method or not.
     * @return iDecoded
     */
    public boolean decodedKey(){
        return iDecoded;
    }

    /**
     * Purpose: counts the bytes of a string and sets the isEncoded boolean value.
     * @param stringToCount the string to count.
     */
    public byte[] countBytesOfString(String stringToCount) {

        try {
            // Check encoded sizes
            final byte[] utf8Bytes = stringToCount.getBytes("UTF-8");

            if(utf8Bytes.length == 32 ){
                iEncoded = true;
                return utf8Bytes;
            }
        }catch(UnsupportedEncodingException e) {

            e.getLocalizedMessage();
        }

        return null;
    }

    /**
     * Purpose: this method encrypts the data passed to it with the secret key.
     * @param data the data to encrypt.
     * @param key the secret key.
     */
    public String encrypt(final String data,
                          final String key) {

        final byte[] symKeyData = Base64.decode(key,Base64.DEFAULT);

        final byte[] encodedMessage = data.getBytes(Charset
                .forName("UTF-8"));
        try {
            final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            final int blockSize = cipher.getBlockSize();

            // create the key
            final SecretKeySpec symKey = new SecretKeySpec(symKeyData, "AES");

            // generate random IV using block size (possibly create a method for
            // this)
            final byte[] ivData = new byte[blockSize];
            final SecureRandom rnd = SecureRandom.getInstance("SHA1PRNG");
            rnd.nextBytes(ivData);
            final IvParameterSpec iv = new IvParameterSpec(ivData);

            cipher.init(Cipher.ENCRYPT_MODE, symKey, iv);

            final byte[] encryptedMessage = cipher.doFinal(encodedMessage);

            // concatenate IV and encrypted message
            final byte[] ivAndEncryptedMessage = new byte[ivData.length
                    + encryptedMessage.length];
            System.arraycopy(ivData, 0, ivAndEncryptedMessage, 0, blockSize);
            System.arraycopy(encryptedMessage, 0, ivAndEncryptedMessage,
                    blockSize, encryptedMessage.length);

            final String ivAndEncryptedMessageBase64 = Base64.encodeToString(ivAndEncryptedMessage,Base64.DEFAULT);

            iEncrypted = true;

            return ivAndEncryptedMessageBase64;
        } catch (InvalidKeyException e) {
            throw new IllegalArgumentException(
                    "key argument does not contain a valid AES key");
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(
                    "Unexpected exception during encryption", e);
        }
    }

    /**
     * Purpose: this method decrypts the encrypted data passed to it with the secret key.
     * @param encryptedData the data to decrypt.
     * @param key the secret key.
     */
    public String decrypt(final String encryptedData,
                          final String key) {
        final byte[] symKeyData = Base64.decode((key),Base64.DEFAULT);

        final byte[] ivAndEncryptedMessage = Base64.decode(encryptedData,Base64.DEFAULT);
        try {
            final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            final int blockSize = cipher.getBlockSize();

            // create the key
            final SecretKeySpec symKey = new SecretKeySpec(symKeyData, "AES");

            // retrieve random IV from start of the received message
            final byte[] ivData = new byte[blockSize];
            System.arraycopy(ivAndEncryptedMessage, 0, ivData, 0, blockSize);
            final IvParameterSpec iv = new IvParameterSpec(ivData);

            // retrieve the encrypted message itself
            final byte[] encryptedMessage = new byte[ivAndEncryptedMessage.length
                    - blockSize];
            System.arraycopy(ivAndEncryptedMessage, blockSize,
                    encryptedMessage, 0, encryptedMessage.length);

            cipher.init(Cipher.DECRYPT_MODE, symKey, iv);

            final byte[] encodedMessage = cipher.doFinal(encryptedMessage);

            // concatenate IV and encrypted message
            final String message = new String(encodedMessage,
                    Charset.forName("UTF-8"));

            iDecoded = true;

            return message;
        } catch (InvalidKeyException e) {
            throw new IllegalArgumentException(
                    "key argument does not contain a valid AES key");
        } catch (BadPaddingException e) {
            // you'd better know about padding oracle attacks
            return null;
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(
                    "Unexpected exception during decryption", e);
        }
    }
}
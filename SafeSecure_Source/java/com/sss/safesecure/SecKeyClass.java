package com.sss.safesecure;

/**
 * Purpose: this class handles the temporary storage and getting / setting of the secret key -
 * used for encryption and decryption.
 */
public class SecKeyClass {

    /**
     * Purpose: String for storing secret key.
     *
     * */
    private String secretKey;

    /**
     * Purpose: A constructor for the class: SecKeyClass.
     *
     * @param key (String secretKey)
     * ----------------------------------------------------------
     */
    public SecKeyClass(final String key) {
        secretKey = key;
    }

    /**
     * Purpose: A method that will allow the calling class to set the secretKey
     * property.
     */
    public final void setKey() {
        String key = secretKey;
    }

    /**
     * Purpose: A method that will allow this SecKeyClass class to provide the calling class
     * with the secretKey data.
     *
     * @return secretKey.
     * ----------------------------------------------------------
     */
    public final String getKey() {
        return secretKey;
    }
}


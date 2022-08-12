public class Constant {
    public static final int MAX_ARR_INDEX = 2147483600; //Maximum length a standard array can have with some Error Margin for different jdks
    public static final int HEADER_LENGTH_NORMAL = 29; //cipher + nonce + salt (in bytes)
    public static final int HEADER_LENGTH_NORMAL_HMAC = 45; //cipher + nonce + salt + hmac (in bytes)
    public static final int HMAC_LENGTH = 20; //Length of an hmac in bytes
    public static final int NONCE_IV_LENGTH = 12; //Lenght of a nonce in bytes
    public static final int SALT_LENGTH = 16; // Length of a salt in bytes
    public static final int ANSWER_TO_EVERYTHING = 42; // always true
    public static final String  MAGIC_FILE_NAME = "./gunhgGNKIwNGZDjsaDWUZngdJZSAd278t4n27nesdwndwO()ETD8ww.encData"; // Name for the magic file
    public static final int NUM_OF_CIPHERS = 5; // the number of available ciphers in normal and largefilemode
    public static final int SYM_CIPHER_KEYLENGTH = 256;//keylength of all ciphers
    public static final int KEY_ITERATIONS = 20000;//Iterations, that will be used for deriving the cipher key
    public static final int MAC_KEY_LENGTH = 128;//length of the hmac key
    public static final int MAC_KEY_ITERATIONS = 10000;//Iterations, that will be used for deriving the hmac key
}

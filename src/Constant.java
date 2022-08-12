public class Constant {

    public static final int HEADER_LENGTH = 51; //magic string + nonce + salt + hmac (in bytes)
    public static final int HMAC_LENGTH = 20; //Length of an hmac in bytes
    public static final int NONCE_IV_LENGTH = 12; //Lenght of a nonce in bytes
    public static final int SALT_LENGTH = 16; // Length of a salt in bytes
    public static final int ANSWER_TO_EVERYTHING = 42; // always true
    public static final String MAGIC_STRING = "jghsvbfhnsdkbjgfhvnkdgb";
    public static final byte[] MAGIC_STRING_BYTES = "jghsvbfhnsdkbjgfhvnkdgb".getBytes();
    public static final int MAGIC_STRING_LENGTH = 23;
    public static final int CHACHA_KEY_LENGTH = 256;//keylength of all ciphers
    public static final int KEY_ITERATIONS = 20000;//Iterations, that will be used for deriving the cipher key
    public static final int MAC_KEY_LENGTH = 128;//length of the hmac key
    public static final int MAC_KEY_ITERATIONS = 10000;//Iterations, that will be used for deriving the hmac key
}

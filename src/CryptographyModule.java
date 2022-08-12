import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class CryptographyModule {

    //TODO
    public byte[] PKDF2(char[] password, byte[] salt, int i, int i1) {
        return new byte[0];
    }

    //TODO
    public byte[] getIV_Nonce() {
        return new byte[0];
    }

    //TODO
    public byte[] getSalt() {
        return new byte[0];
    }
    //TODO
    public byte[] encrypt(String path, char[] password, byte[] nonce_iv, byte[] salt, Mac mac) {
        return new byte[0];
    }

    public byte[] decrypt(String path, char[] password, byte[] nonce_iv, byte[] salt, Mac mac) {
        return new byte[0];
    }
}

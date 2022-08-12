import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public class Worker extends Thread{
    private final char[] password;
    private final CryptographyModule crypt;
    private final Workerpool pool;
    private final String[] paths;
    private final boolean encrypted;

    public Worker(char[] password, String[] paths, Workerpool pool, boolean encrypted) {
        this.paths = paths;
        this.pool = pool;
        this.encrypted = true;
        this.password = password;
        this.crypt = new CryptographyModule();
    }
    //TODO
    @Override
    public void run(){

        int currentFileIndex = pool.getFreePlace();
        while(currentFileIndex != -1) {
            if (!encrypted) {//--------------------------------if the folder is not encrypted
                //----------------create salt, nonce, macKey, mac
                Mac mac = null;
                try {
                    mac = Mac.getInstance("HMACSHA1");
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
                byte[] nonce_iv = crypt.getIV_Nonce();
                byte[] salt = crypt.getSalt();
                byte[] macBytes;

                //derive macKey from password with 10000 iterations
                Key macKey = new SecretKeySpec(crypt.PKDF2(password, salt, Constant.MAC_KEY_LENGTH, Constant.MAC_KEY_ITERATIONS), "HMACSHA1");

                try {
                    mac.init(macKey);
                } catch (InvalidKeyException e) {
                    throw new RuntimeException(e);
                }
                //----------------write nonce salt and hmac to Header with .temp extension
                IOModule.writeHeader(nonce_iv, salt, macBytes, paths[currentFileIndex]);
                //----------------r/encrypt/w file and get hmac
                macBytes = crypt.encrypt(paths[currentFileIndex], password, nonce_iv, salt, mac);
                //overwrite original and change name
                IOModule.safelyDeleteOriginal(paths[currentFileIndex]);
                IOModule.rename(paths[currentFileIndex].concat("temp"), paths[currentFileIndex]);
            } else {//         --------------------------------if the folder is encrypted
                //----------------get nonce, salt, hmac, hmacKey
                byte[][] encData = IOModule.readHeader(paths[currentFileIndex]);//
                byte[] nonce_iv = encData[0];
                byte[] salt = encData[1];
                byte[] macBytes = encData[2];
                byte[] newMacBytes;
                Mac mac = null;
                try {
                    mac = Mac.getInstance("HMACSHA1");
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
                Key macKey = new SecretKeySpec(crypt.PKDF2(password, salt, 128, 10000), "HMACSHA1");
                try {
                    mac.init(macKey);
                } catch (InvalidKeyException e) {
                    throw new RuntimeException(e);
                }
                //----------------decrypt file and get hmac
                newMacBytes = crypt.decrypt(paths[currentFileIndex], password, nonce_iv, salt, mac);
                //----------------check integrity
                for(int i = 0; i < Constant.HMAC_LENGTH; i++){
                    if(newMacBytes[i] != macBytes[i]){
                        System.out.println("Integrität verletzt:\n"+paths[currentFileIndex]);

                    }
                }
                //----------------delete .encData file
                try {
                    Files.delete(Path.of(paths[currentFileIndex].concat(".encData")));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            currentFileIndex = pool.getFreePlace();
        }
    }
}

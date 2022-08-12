import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.channels.FileChannel;
import java.security.*;

public class CryptographyModule {
    SecureRandom rand;
    public CryptographyModule(){
        rand = new SecureRandom();
    }
    //returns the pkdf2 hash of the argument password with the specified length and iterations
    //!!the parameter length is in bit-format
    public byte[] PKDF2(char[] password, byte[] salt, int length, int iterations) {

        PBEParametersGenerator gen = new PKCS5S2ParametersGenerator(new SHA256Digest());
        gen.init(PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(password), salt, iterations);
        return ((KeyParameter) gen.generateDerivedParameters(length)).getKey();
    }

    //returns a random generated nonce
    public byte[] getIV_Nonce() {
        byte[] result = new byte[Constant.NONCE_IV_LENGTH];
        rand.nextBytes(result);
        return result;
    }

    //returns a random generated salt
    public byte[] getSalt() {
        byte[] result = new byte[Constant.SALT_LENGTH];
        rand.nextBytes(result);
        return result;
    }
    //r/encrypt/w
    public byte[] encrypt(String path, char[] password, byte[] iv_nonce, byte[] salt, Mac mac) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, InvalidKeyException {

        Cipher cipher = Cipher.getInstance("ChaCha20", "BC");
        cipher.init(
                    Cipher.ENCRYPT_MODE,
                    new SecretKeySpec(PKDF2(password, salt, Constant.CHACHA_KEY_LENGTH, Constant.KEY_ITERATIONS), "ChaCha20"),
                    new IvParameterSpec(iv_nonce)
        );
        try(BufferedInputStream in = new BufferedInputStream(
                new FileInputStream(path));
            BufferedOutputStream out = new BufferedOutputStream(new CipherOutputStream(
                    new FileOutputStream(path.concat(".temp"), true), cipher)
            )){
            int read;
            byte[] buffer = new byte[1];//reading buffer

            while((read = in.read(buffer, 0, buffer.length)) != -1) {// stops at the end of the file
                mac.update(buffer);//update hmac
                out.write(buffer, 0, buffer.length);//write the buffer
            }

        }catch (Exception e){
            System.out.println("An Exception occured while encrypting:\n"+path);
        }
        return mac.doFinal();
    }
    //r/decrypt/w
    public byte[] decrypt(String path, char[] password, byte[] nonce_iv, byte[] salt, Mac mac) throws InvalidAlgorithmParameterException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException {
        long fileSize = 0;
        //get file size
        try{
            FileInputStream inputStream = new FileInputStream(path);
            FileChannel channel = inputStream.getChannel();
            fileSize = channel.size();
            channel.close();
            inputStream.close();

        }catch (Exception e){
            System.out.println("An Exception occured while reading the hmac of:\n"+path);
        }

        byte[] key = PKDF2(password,salt,Constant.CHACHA_KEY_LENGTH, Constant.KEY_ITERATIONS);
        Cipher cipher = Cipher.getInstance("ChaCha20", "BC");

        cipher.init(
                Cipher.DECRYPT_MODE,
                new SecretKeySpec(key, "ChaCha20"),
                new IvParameterSpec(nonce_iv)
        );
        //read/write/decrypt
        try(BufferedInputStream in = new BufferedInputStream(new CipherInputStream(
                new FileInputStream(path), cipher));
            BufferedOutputStream out = new BufferedOutputStream(
                    new FileOutputStream(path.concat(".tempDec"), true))
        ){
            in.skip(Constant.HEADER_LENGTH);

            int read;
            long counter = 0;
            byte[] buffer = new byte[1]; //read buffer

            while(counter < fileSize-Constant.HMAC_LENGTH) { // stops at the end of the file - the hmac
                read = in.read(buffer, 0, buffer.length);
                mac.update(buffer);//update hmac
                out.write(buffer,0,read);//write buffer
                counter++;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mac.doFinal();
    }
}

import java.io.*;
import java.nio.Buffer;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class IOModule {
    private final String path;
    private String[] paths;
    private boolean encrypted;
    public IOModule(String path) {
        this.path = path;
        paths = getPaths(path, new ArrayList<String>());
        this.encrypted = true;
        setEncrypted();
    }
    //Writes Header to .temp file
    public static void writeHeader(byte[] nonce_iv, byte[] salt, String path){

        //Write MagicString to .temp
        try(BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(path.concat(".temp")))){

            out.write(Constant.MAGIC_STRING_BYTES);

        }catch(Exception e){
            System.out.println("Exception occured while writing MagicString of:\n"+path);
        }
        //Append the rest to .temp
        try(BufferedOutputStream out2 = new BufferedOutputStream(new FileOutputStream(path.concat(".temp"),true))){

            out2.write(nonce_iv);
            out2.write(salt);

        }catch(Exception e){
            System.out.println("Exception occured while writing header of:\n"+path);
        }
    }
    //reads the header and returns it in the format {nonce, salt, hmac}
    public static byte[][] readHeader(String path) {
        //init

        byte[]
                serializedHeader,
                nonce = new byte[Constant.NONCE_IV_LENGTH],
                salt = new byte[Constant.SALT_LENGTH];

        //read the serialized Header without Magic-String
        serializedHeader = new byte[Constant.HEADER_LENGTH-Constant.MAGIC_STRING_LENGTH];
        try(BufferedInputStream in = new BufferedInputStream(new FileInputStream(path))){

            in.skip(Constant.MAGIC_STRING_LENGTH);
            serializedHeader = in.readNBytes(
                        Constant.NONCE_IV_LENGTH+
                            Constant.SALT_LENGTH);
            
        }catch(Exception e){
            System.out.println("Exception occured while reading header of:\n"+path);
        }

        //serialized Header to 2 dimensional byte arr
        System.arraycopy(serializedHeader, 0,
                nonce,0 ,Constant.NONCE_IV_LENGTH);
        System.arraycopy(serializedHeader, Constant.NONCE_IV_LENGTH,
                salt,0 ,Constant.SALT_LENGTH);

        
        //return header
        return new byte[][]{nonce,salt};
    }
    public static void safelyDeleteOriginal(String path) {
        byte[] zerobytes = new byte[1000];
        //get filesize
        long fileSize = 0;
        try {
            FileInputStream in = new FileInputStream(path);
            FileChannel channel = in.getChannel();
            fileSize = channel.size();
            in.close();
            channel.close();
        }catch(Exception e){
            System.out.println("An Exception occured while overwriting the original file:\n"+path);
        }
        //write initial byte
        try(BufferedOutputStream out1 = new BufferedOutputStream(
                new FileOutputStream(path))){
            out1.write((byte)0x00);

        }catch(Exception e){
            System.out.println("An Exception occured while overwriting the original file:\n"+path);
        }
        //append the needed bytes
        try(BufferedOutputStream out2 = new BufferedOutputStream(
                new FileOutputStream(path, true))){
            long counter = 0;
            while(counter < fileSize){
                out2.write(zerobytes);
                counter++;
            }

        }catch(Exception e){
            System.out.println("An Exception occured while overwriting the original file:\n"+path);
        }
        //delete File
        try {
            Files.delete(Path.of(path));
        } catch (IOException e) {
            System.out.println("An Exception occured while overwriting the original file:\n"+path);
        }
    }
    //renames oldName to newName
    public static void rename(String oldName, String newName) {
        File oldFile = new File(oldName);
        File newFile = new File(newName);
        oldFile.renameTo(newFile);
    }
    //appends the mac to the .temp file
    public static void writeHMac(byte[] macBytes, String path) {
        try(BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(path.concat(".temp"), true))){
            out.write(macBytes);
        }catch(Exception e){
            System.out.println("An Exception occured while appending mac to temp file of:\n"+path);
        }
    }
    //TODO
    public static byte[] readMac(String path) {
        long fileSize = 0;
        //init
        byte[] hmac = new byte[Constant.HMAC_LENGTH];
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
        //read
        try(BufferedInputStream in = new BufferedInputStream(new FileInputStream(path))){
            in.skip(fileSize - Constant.HMAC_LENGTH);
            hmac = in.readNBytes(Constant.HMAC_LENGTH);
        }catch (Exception e){
            System.out.println("An Exception occured while reading the hmac of:\n"+path);
        }
        return hmac;
    }

    //return the number of Files in the folder
    public int getNumOfFiles() {
        return paths.length;
    }

    //returns all paths of the folder and its subfolders
    public String[] getAllPaths() {
        return paths;
    }

    //returns true if the folder is already encrypted
    public boolean getEnc() {
        return encrypted;
    }

    //Sets the encrypted variable to false if the folder is not encrypted
    private void setEncrypted(){
        try(BufferedInputStream in = new BufferedInputStream(new FileInputStream(paths[0]))){
            byte[] magicString = in.readNBytes(Constant.MAGIC_STRING_LENGTH);
            for(int i = 0; i < Constant.MAGIC_STRING_LENGTH;i++){
                if(magicString[i] != Constant.MAGIC_STRING_BYTES[i]){
                    this.encrypted = false;
                    break;
                }
            }
        }catch(Exception e){
            this.encrypted = false;
        }
    }

    private String[] getPaths(String path, ArrayList<String> tempResult) {

        File dir = new File(path);

        for(File file : dir.listFiles()) {// for all files in this folder

            if(file.isDirectory()) {// if 'file' is a folder

                getPaths(file.getAbsolutePath(), tempResult);// recursion

            }else {
                if(!file.getAbsolutePath().contains(".encData")){//ignore .encData files

                    tempResult.add(file.getAbsolutePath());//add to the result

                }else {
                    this.encrypted = true; //set encrypted-flag to true
                }


            }

        }
        //conversion to string array
        String[] paths = new String[tempResult.size()];

        for(int i = 0; i < paths.length; i++) {

            paths[i] = tempResult.get(i);

        }

        return paths;

    }
}

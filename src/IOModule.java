import java.io.File;
import java.util.ArrayList;

public class IOModule {
    private final String path;
    private String[] paths;
    private boolean encrypted;
    public IOModule(String path) {
        this.path = path;
        paths = getPaths(path, new ArrayList<String>());
    }
    //TODO
    public static void writeHeader(byte[] nonce_iv, byte[] salt, byte[] macBytes, String path) {
    }
    //TODO
    public static byte[][] readHeader(String path) {
        return new byte[0][0];
    }

    //return the number of Files in the folder
    public int getNumOfFiles() {
        return paths.length;
    }

    //returns all paths of the folder and its subfolders
    public String[] getAllPaths() {
        return paths;
    }

    //TODO
    //returns true if the folder is already encrypted
    public boolean getEnc() {
        return encrypted;
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

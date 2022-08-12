import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.Scanner;

public class Mainprogram {
    public static void main(String[]args) throws Exception {
        //Add BC Provider;
        Security.addProvider(new BouncyCastleProvider());

        String path = "/home/robin/Dokumente/eclipse-workspace";
        IOModule io = new IOModule(path);
        int numOfFiles = io.getNumOfFiles();
        String[] filePaths = io.getAllPaths();
        int numOfWorkers = Runtime.getRuntime().availableProcessors()/2;
        char[] password;
        Scanner scan = new Scanner(System.in);
        boolean encrypted = io.getEnc();
        String seperator =
                "--------------------------------------------------------------------";

        String banner =
            "888b.                 w            8888       8    8\n"+
                "8   8 .d88 8d8b. .d8b w 8d8b. .d88 8www .d8b. 8 .d88 .d88b 8d8b d88b\n"+
            "8   8 8  8 8P Y8 8    8 8P Y8 8  8 8    8' .8 8 8  8 8.dP' 8P   `Yb.\n"+
            "888P' `Y88 8   8 `Y8P 8 8   8 `Y88 8    `Y8P' 8 `Y88 `Y88P 8    Y88P\n"+
            "                              wwdP\n" +
                    "by Robin Kroker";

        System.out.println(banner);
        System.out.println(seperator);
        System.out.println("Folder encrypted:  " + encrypted);
        if(path.length() > 27){
            System.out.println("Current Path:      " + "..." +path.substring(path.length() - 27));
        }else{
            System.out.println("Current Path:      " + path);
        }
        System.out.println("Number of Workers: " + numOfWorkers);
        System.out.println("Number of Files:   " + numOfFiles);
        System.out.println("Folder encrypted:  " + encrypted);
        System.out.println(seperator);
        System.out.println("Please enter your password:");
        System.out.print(">");
        password = scan.nextLine().toCharArray();
        Workerpool pool = new Workerpool(filePaths, numOfWorkers, encrypted);
        pool.work(password);
        for(String i : filePaths){
            System.out.println(i);
        }

    }
}

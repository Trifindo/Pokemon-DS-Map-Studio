
package editor.nsbtx2;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import utils.Utils;

/**
 *
 * @author Trifindo
 */
public class NsbtxWriter {

    //TODO: improve all of this by not using the converter
    
    public static byte[] writeNsbtx(Nsbtx2 nsbtx, String fileName) throws IOException{

        String path = System.getProperty("user.dir") + File.separator + fileName;
        path = Utils.removeExtensionFromPath(path);
        path = Utils.addExtensionToPath(path, "imd");

        NsbtxImd imd = new NsbtxImd(nsbtx);

        try {
            imd.saveToFile(path);

            return imdToNsbmd(path);
        } catch (IOException | ParserConfigurationException | TransformerException ex) {
            throw new IOException();
        }
    }

    private static byte[] imdToNsbmd(String imdPath) throws IOException {
        File file = new File(imdPath);
        byte[] data = null;
        if (file.exists()) {
            String filename = new File(imdPath).getName();
            filename = Utils.removeExtensionFromPath(filename);
            String converterPath = "converter/g3dcvtr.exe";
            String[] cmd;
            if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
                cmd = new String[]{converterPath, imdPath, "-etex", "-o", filename};
            } else {
                cmd = new String[]{"wine", converterPath, imdPath, "-etex", "-o", filename};
                // NOTE: wine call works only with relative path
            }

            if (!Files.exists(Paths.get(converterPath))) {
                throw new IOException();
            }

            Process p = new ProcessBuilder(cmd).start();

            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            String outputString = "";
            String line = null;
            while ((line = stdError.readLine()) != null) {
                outputString += line + "\n";
            }

            try{
                p.waitFor();
            }catch(InterruptedException ex){
                throw new IOException();
            }
            
            p.destroy();

            String nsbPath = Utils.removeExtensionFromPath(imdPath);
            nsbPath = Utils.addExtensionToPath(nsbPath, "nsbtx");

            filename = Utils.removeExtensionFromPath(filename);
            filename = Utils.addExtensionToPath(filename, "nsbtx");

            File srcFile = new File(System.getProperty("user.dir") + File.separator + filename);
            if (srcFile.exists()) {
                data = Files.readAllBytes(srcFile.toPath());

                if (file.exists()) {
                    Files.delete(file.toPath());
                }
                if (srcFile.exists()) {
                    Files.delete(srcFile.toPath());
                }
            } else {
                throw new IOException();
            }
        }
        
        return data;
    }

}

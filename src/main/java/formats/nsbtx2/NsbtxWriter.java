
package formats.nsbtx2;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import utils.Utils;

/**
 * @author Trifindo
 */
public class NsbtxWriter {

    //TODO: improve all of this by not using the converter

    public static byte[] writeNsbtx(Nsbtx2 nsbtx, String fileName) throws IOException {
        System.out.println("EXPORTING NSBTX IMD!");

        String path = System.getProperty("user.dir") + File.separator + fileName;
        System.out.println(path);
        //path = Utils.removeExtensionFromPath(path);
        //System.out.println(path);
        path = Utils.addExtensionToPath(path, "imd");
        System.out.println(path);

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
        System.out.println("EXPORTING NSBMD");
        if (!file.exists()) {
            return null;
        }

        System.out.println("File exists!");
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
            System.out.println("Converter not found!");
            throw new IOException();
        }

        Process p = new ProcessBuilder(cmd).start();

        BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

        StringBuilder outputString = new StringBuilder();
        String line;
        while ((line = stdError.readLine()) != null) {
            outputString.append(line).append("\n");
        }

        try {
            p.waitFor();
        } catch (InterruptedException ex) {
            throw new IOException();
        }

        p.destroy();

        String nsbPath = Utils.removeExtensionFromPath(imdPath);
        nsbPath = Utils.addExtensionToPath(nsbPath, "nsbtx");

        filename = Utils.removeExtensionFromPath(filename);
        filename = Utils.addExtensionToPath(filename, "nsbtx");

        File srcFile = new File(System.getProperty("user.dir") + File.separator + filename);
        byte[] data;
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
        return data;
    }
}

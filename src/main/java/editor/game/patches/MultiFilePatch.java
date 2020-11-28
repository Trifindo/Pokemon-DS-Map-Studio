package editor.game.patches;

import formats.narc2.Narc;
import formats.narc2.NarcFile;
import formats.narc2.NarcIO;
import utils.BinaryArrayReader;
import utils.BinaryReader;
import utils.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class MultiFilePatch {

    private static final String headerPath = "header.bin";
    private String gameCode;
    private List<FilePatch> patches;

    public MultiFilePatch(String gameCode, List<FilePatch> patches) {
        this.gameCode = gameCode;
        this.patches = patches;
    }

    public boolean canApplyPatch(String gameFolderPath) throws Exception{
        byte[] headerData = Files.readAllBytes(new File(gameFolderPath + File.separator + headerPath).toPath());
        if (!Utils.containsArray(headerData, gameCode.getBytes(), 12)) {
            return false;
        }

        for(FilePatch patch : patches){
            byte[] gameData = loadGameData(gameFolderPath + File.separator + patch.getFilePath());
            //byte[] gameData = Files.readAllBytes(new File(patch.getFilePath()).toPath());
            if(!patch.canApplyPatch(gameData)){
                return false;
            }
        }
        return true;
    }

    public boolean isPatched(String gameFolderPath) throws Exception{
        for(FilePatch patch : patches){
            //byte[] gameData = Files.readAllBytes(new File(patch.getFilePath()).toPath());
            byte[] gameData = loadGameData(gameFolderPath + File.separator + patch.getFilePath());
            if(!Utils.containsArray(gameData, patch.getNewData(), patch.getDataOffset())){
                return false;
            }
        }
        return false;
    }

    public void applyPatch(String gameFolderPath) throws Exception{
        for(FilePatch patch : patches){
            //byte[] gameData = Files.readAllBytes(new File(patch.getFilePath()).toPath());
            byte[] gameData = loadGameData(gameFolderPath + File.separator + patch.getFilePath());
            System.arraycopy(patch.getNewData(), 0, gameData, patch.getDataOffset(), patch.getNewData().length);
        }
    }

    private static byte[] loadGameData(String path) throws IOException {
        if(new File(path).exists()){
            return Files.readAllBytes(new File(path).toPath());
        }else{
            String[] splitPath = path.split(File.separator);
            String fullPath = splitPath[0];

            for(int i = 1; i < splitPath.length; i++){
                fullPath += File.separator + splitPath[i];
                if(new File(fullPath).isFile()){
                    try {
                        Narc narc = NarcIO.loadNarc(fullPath);
                        NarcFile narcFile = narc.getFileByPath(path.substring(fullPath.length()));
                        if(narcFile != null){
                            return narcFile.getData();
                        }else{
                            throw new IOException("NARC internal file not found Exception");
                        }
                    }catch(Exception ex){
                        throw new IOException("File and NARC not found Exception");
                    }
                }
            }
            throw new IOException("File and NARC not found Exception");
        }
    }

    private static boolean isNarc(String filePath) {
        try {
            byte[] data = Files.readAllBytes(new File(filePath).toPath());
            return BinaryReader.readString(data, 0, 4).equals("NARC");
        }catch(Exception ex){
            return false;
        }
    }

}

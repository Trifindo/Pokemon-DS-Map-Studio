package formats.mapbin;

public class MissingMapBinFileException extends Exception{

    public static final int MISSING_BGS = 0;
    public static final int MISSING_PER = 1;
    public static final int MISSING_BLD = 2;
    public static final int MISSING_NSBMD = 3;
    public static final int MISSING_BDHC = 4;

    public final int missingFileCode;

    public MissingMapBinFileException(int missingFileCode){
        this.missingFileCode = missingFileCode;
    }


}

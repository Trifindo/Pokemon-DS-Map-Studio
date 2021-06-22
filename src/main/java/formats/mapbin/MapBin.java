package formats.mapbin;

import java.io.IOException;

public abstract class MapBin {

    public abstract void saveToFile(String path) throws IOException;
}


package editor.game;

import java.io.File;

/**
 * @author Trifindo
 */
public abstract class GameFileSystem {

    protected static String getPath(String[] splitPath) {
        String path = "";
        for (int i = 0; i < splitPath.length - 1; i++) {
            path += splitPath[i] + File.separator;
        }
        path += splitPath[splitPath.length - 1];
        return path;
    }

}

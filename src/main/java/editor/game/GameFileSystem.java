
package editor.game;

import java.io.File;

/**
 * @author Trifindo
 */
public abstract class GameFileSystem {

    protected static String getPath(String[] splittedPath) {
        String path = "";
        for (int i = 0; i < splittedPath.length - 1; i++) {
            path += splittedPath[i] + File.separator;
        }
        path += splittedPath[splittedPath.length - 1];
        return path;
    }

}

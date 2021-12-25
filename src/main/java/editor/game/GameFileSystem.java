
package editor.game;

import java.io.File;
import java.util.StringJoiner;

/**
 * @author Trifindo
 */
public abstract class GameFileSystem {

    protected static String getPath(String[] splitPath) {
        StringJoiner path = new StringJoiner(File.separator);
        for (String s : splitPath) {
            path.add(s);
        }
        return path.toString();
    }
}

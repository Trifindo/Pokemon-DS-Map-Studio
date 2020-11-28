
package formats.nsbtx2.exceptions;

import formats.nsbtx2.Nsbtx2;

/**
 * @author Trifindo
 */
public class NsbtxTextureFormatException extends Exception {
    public NsbtxTextureFormatException(int format) {
        super("The NSBTX texture format " + Nsbtx2.formatNames[format] + " is not supported");
    }
}

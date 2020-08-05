/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.nsbtx2.exceptions;

import editor.nsbtx2.Nsbtx2;

/**
 *
 * @author Trifindo
 */
public class NsbtxTextureFormatException extends Exception{
    public NsbtxTextureFormatException(int format) {
        super("The NSBTX texture format " + Nsbtx2.formatNames[format] + " is not supported");
    }
}

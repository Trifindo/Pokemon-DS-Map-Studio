/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.nsbtx2;

import editor.nsbtx2.exceptions.NsbtxTextureFormatException;
import utils.Utils;

/**
 *
 * @author Trifindo
 */
public class NsbtxTexture {

    private String name;
    private int colorFormat;
    private boolean isTransparent;
    private int width;
    private int height;
    private byte[] data;

    public NsbtxTexture(byte[] nsbtxData, int offsetTextureInfo,
            int textureDataOffset, int offsetTextureName) throws
            NsbtxTextureFormatException, IndexOutOfBoundsException {
        byte b1 = nsbtxData[offsetTextureInfo + 0x03];
        byte b2 = nsbtxData[offsetTextureInfo + 0x02];

        int offsetTextureData = ((nsbtxData[offsetTextureInfo + 0x01] & 0xFF) << 8 | (nsbtxData[offsetTextureInfo] & 0xFF)) << 3;

        //Read color format
        colorFormat = (b1 & 0x1C) >> 2;
        if (colorFormat == Nsbtx2.FORMAT_NO_TEXTURE
                || colorFormat == Nsbtx2.FORMAT_4X4_TEXEL
                || colorFormat == Nsbtx2.FORMAT_DIRECT_TEXTURE) {
            throw new NsbtxTextureFormatException(colorFormat);
        }

        //Read image properites
        isTransparent = ((b1 & 0x20) >> 5) == 1;
        height = 8 << (((b1 & 0x03) << 1) | ((b2 & 0x80) >> 7));
        width = 8 << ((b2 & 0x70) >> 4);
        int dataSize = (width * height * Nsbtx2.bitDepth[colorFormat]) / 8;

        //Read data
        data = new byte[dataSize];
        System.arraycopy(nsbtxData, textureDataOffset + offsetTextureData, data, 0, dataSize);

        //Read texture name
        name = Utils.removeLastOcurrences(new String(nsbtxData, offsetTextureName, 16), '\u0000');
    }

    public NsbtxTexture(String name, int colorFormat, boolean isTransparent, 
            int width, int height) {
        this.name = name;
        this.colorFormat = colorFormat;
        this.isTransparent = isTransparent;
        this.width = width;
        this.height = height;
        this.data = new byte[(width * height * Nsbtx2.bitDepth[colorFormat]) / 8];
    }

    public byte[] getColorIndices() {
        int bitDepth = getBitDepth();
        int pixelsPerByte = 8 / bitDepth;
        int mask = 0xFF >> (8 - bitDepth);
        int pixelIndex = 0;
        byte[] colorIndices = new byte[width * height];
        for (int i = 0; i < data.length; i++) {
            byte dataByte = data[i];
            for (int j = 0; j < pixelsPerByte; j++) {
                byte colorIndex = (byte) ((dataByte >> bitDepth * j) & mask);
                colorIndices[pixelIndex] = colorIndex;
                pixelIndex++;
            }
        }
        return colorIndices;
    }
    
    public String getDataAsHexStringImd() {
        String hexString = "";
        for (int i = 0; i < data.length; i += 2) {
            hexString += " ";
            hexString += String.format("%02x", data[i + 1]);
            hexString += String.format("%02x", data[i]);
        }
        return hexString;
    }

    public int getBitDepth() {
        return Nsbtx2.bitDepth[colorFormat];
    }

    public int getNumColors() {
        return Nsbtx2.numColors[colorFormat];
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColorFormat() {
        return colorFormat;
    }

    public boolean isTransparent() {
        return isTransparent;
    }

    public void setTransparent(boolean isTransparent) {
        this.isTransparent = isTransparent;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setData(byte[] data){
        this.data = data;
    }
    
    public int getDataSizeImd(){
        return data.length / 2;
    }
    
}

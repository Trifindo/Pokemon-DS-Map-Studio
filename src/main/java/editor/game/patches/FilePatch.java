package editor.game.patches;

import utils.Utils;


public class FilePatch {

    private String filePath;
    private int dataOffset;
    private byte[] oldData;
    private byte[] newData;

    public FilePatch(String filePath, int dataOffset, byte[] oldData, byte[] newData) {
        this.dataOffset = dataOffset;
        this.filePath = filePath;
        this.oldData = oldData;
        this.newData = newData;
    }

    public boolean canApplyPatch(byte[] gameData) {
        if (dataOffset + newData.length > gameData.length) {
            return false;
        }
        if (dataOffset + oldData.length > gameData.length) {
            return false;
        }

        return Utils.containsArray(gameData, oldData, dataOffset);
    }

    public boolean isPatched(byte[] gameData) {
        return Utils.containsArray(gameData, newData, dataOffset);
    }

    public void applyPatch(byte[] gameData) {
        System.arraycopy(newData, 0, gameData, dataOffset, newData.length);
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getDataOffset() {
        return dataOffset;
    }

    public void setDataOffset(int dataOffset) {
        this.dataOffset = dataOffset;
    }

    public byte[] getOldData() {
        return oldData;
    }

    public void setOldData(byte[] oldData) {
        this.oldData = oldData;
    }

    public byte[] getNewData() {
        return newData;
    }

    public void setNewData(byte[] newData) {
        this.newData = newData;
    }
}

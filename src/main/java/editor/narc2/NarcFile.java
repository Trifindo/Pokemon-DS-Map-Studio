
package editor.narc2;


public class NarcFile {

    private String name = "";
    private NarcFolder parent;
    private byte[] data;

    public NarcFile(String name, NarcFolder parent) {
        this.name = name;
        this.parent = parent;
    }

    public NarcFile(String name, NarcFolder parent, byte[] data) {
        this.name = name;
        this.parent = parent;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public byte[] getData() {
        return data;
    }
    
}


package editor.state;

/**
 * @author Trifindo
 */
public abstract class State {

    public String name;

    public State(String name) {
        this.name = name;
    }

    public abstract void revertState();
}

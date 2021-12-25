
package editor.state;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Trifindo
 */
public class StateHandler {

    private static final int maxNumStates = 20;
    private int stateIndex;
    private boolean stateAdded = false;

    private final List<State> states;

    public StateHandler() {
        states = new ArrayList<>(maxNumStates + 1);
        stateIndex = 0;
    }

    public void addState(State state) {
        states.add(stateIndex, state);
        stateIndex++;
        stateAdded = true;
        for (int i = stateIndex, size = states.size(); i < size; i++) {
            states.remove(stateIndex);
        }
        if (states.size() > maxNumStates) {
            states.remove(0);
            stateIndex--;
        }
    }

    public State getPreviousState(State state) {
        if (stateAdded) {
            states.add(stateIndex, state);
        }
        stateAdded = false;
        stateIndex--;
        return states.get(stateIndex);
    }

    public State getNextState() {
        stateIndex++;
        return states.get(stateIndex);
    }

    public boolean canGetPreviousState() {
        return stateIndex > 0;
    }

    public boolean canGetNextState() {
        return stateIndex < states.size() - 1;
    }

    public int size() {
        return states.size();
    }

    public State getLastState() {
        try {
            return states.get(stateIndex - 1);
        } catch (Exception ex) {
            return null;
        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.state;

/**
 *
 * @author Trifindo
 */
public abstract class State {
    
    public String name;
    
    public State(String name){
        this.name = name;
    }

    public abstract void revertState();
    
}

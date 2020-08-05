/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.ArrayList;
import java.util.Comparator;

/**
 *
 * @author Trifindo
 */
public class CrossedList<Object>{

    protected ArrayList<Object> data;

    public CrossedList(int size) {
        final int internalSize = (size * (size - 1)) / 2;
        data = new ArrayList<>(internalSize);
        
    }

    public void add(Object o){
        data.add(o);
    }
    
    public Object get(int index1, int index2) {
        if (index2 > index1) {
            int temp = index2;
            index2 = index1;
            index1 = temp;
        }
        return data.get(index1 * (index1 - 1) / 2 + index2);
    }

    public void remove(int index) {
        int internalIndex = index * (index - 1) / 2;
        for (int i = 0; i < index; i++) {
            data.remove(internalIndex);
        }
    }

    public int getInternalSize(){
        return data.size();
    }
    
}

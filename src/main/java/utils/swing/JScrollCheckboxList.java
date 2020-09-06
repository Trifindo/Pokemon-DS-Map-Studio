/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils.swing;

import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;

/**
 *
 * @author Trifindo
 */
public class JScrollCheckboxList extends JScrollPane {

    JCheckboxList checkboxList = new JCheckboxList();

    public JScrollCheckboxList() {
        super();

        checkboxList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        add(checkboxList);
        DefaultListModel model = new DefaultListModel<JCheckBox>();
        checkboxList.setModel(model);
        setViewportView(checkboxList);
    }

    public JCheckboxList getCheckboxList() {
        return checkboxList;
    }
    
    @Override
    public void setEnabled(boolean enabled){
        super.setEnabled(enabled);
        
        checkboxList.setEnabled(enabled);
        ListModel<JCheckBox> model = checkboxList.getModel();
        for(int i = 0; i < model.getSize(); i++){
            model.getElementAt(i).setEnabled(enabled);
        }
    }
    
}

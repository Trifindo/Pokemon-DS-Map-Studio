
package editor.backsound;

import editor.handler.MapEditorHandler;

import java.util.ArrayList;

/**
 * @author Trifindo
 */
public class BacksoundHandler {

    private MapEditorHandler handler;
    //private Backsound backsound;

    private BacksoundEditorDialog dialog;

    private int indexSelected = 0;

    public BacksoundHandler(MapEditorHandler handler, BacksoundEditorDialog dialog) {
        this.handler = handler;
        this.dialog = dialog;

        indexSelected = 0;
    }

    public int getIndexSelected() {
        return indexSelected;
    }

    public void setIndexSelected(int index) {
        this.indexSelected = index;
    }

    public Backsound getBacksound() {
        return handler.getBacksound();
    }

    public Soundplate getSelectedSoundplate() {
        return handler.getBacksound().getSoundplate(indexSelected);
    }

    public ArrayList<Soundplate> getSoundplates() {
        return handler.getBacksound().getSoundplates();
    }

    public BacksoundEditorDialog getDialog() {
        return dialog;
    }

    public void addSoundplate() {
        handler.getBacksound().getSoundplates().add(new Soundplate());
    }

    public void removeSelectedSoundplate() {
        handler.getBacksound().getSoundplates().remove(indexSelected);
        if (indexSelected > 0) {
            indexSelected--;
        } else {
            indexSelected = 0;
        }
    }
}

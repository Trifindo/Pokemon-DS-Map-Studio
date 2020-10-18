
package editor.animationeditor;

import utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * @author Trifindo, JackHack96
 */
public class AnimationFramesDisplay extends JPanel {

    public static final int cellSize = 64;
    public static final int labelSize = 16;
    private AnimationHandler animHandler;

    private final Color cellBorderColor = Color.gray;
    private final Color highlihgtColor = Color.red;

    private BufferedImage backImg;

    /**
     * Creates new form AnimationFramesDisplay
     */
    public AnimationFramesDisplay() {
        initComponents();
        backImg = Utils.generateTransparentBackground(cellSize, 8);
    }

    private void formMousePressed(MouseEvent evt) {
        if (animHandler != null) {
            if (animHandler.getAnimationFile() != null) {
                if (!animHandler.isAnimationRunning()) {
                    int index = evt.getX() / cellSize;
                    if (index >= 0 && index < animHandler.getAnimationSelected().size()) {
                        animHandler.setCurrentFrameIndex(index);
                        animHandler.repaintDialog();
                    }
                }
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (animHandler != null) {
            if (animHandler.getAnimationSelected() != null) {
                Animation anim = animHandler.getAnimationSelected();

                for (int i = 0, size = anim.size(); i < size; i++) {
                    drawCell(g, animHandler.getFrameImage(i), anim.getDelay(i), animHandler.getTextureName(i), i);
                }

                int index = animHandler.getCurrentFrameIndex();
                Graphics2D g2 = (Graphics2D) g;
                g2.setStroke(new BasicStroke(2));
                g.setColor(highlihgtColor);
                g.drawRect(index * cellSize + 1, 1, cellSize - 1, cellSize + labelSize * 2 - 2);
                g2.setStroke(new BasicStroke(1));
            }
        }

    }

    public void init(AnimationHandler animHandler) {
        this.animHandler = animHandler;
    }

    public void updateSize() {
        if (animHandler != null) {
            if (animHandler.getAnimationSelected() != null) {
                this.setPreferredSize(
                        new Dimension(cellSize * animHandler.getAnimationSelected().size(), cellSize + labelSize * 2));
                this.revalidate();
            }
        }
    }

    public void drawCell(Graphics g, BufferedImage img, int delay, String frameName, int cellIndex) {
        if (backImg != null) {
            g.drawImage(backImg, cellIndex * cellSize, 0, null);
        }

        if (img != null) {
            int x = cellSize / 2 - img.getWidth() / 2;
            int y = cellSize / 2 - img.getHeight() / 2;
            g.drawImage(img, cellIndex * cellSize + x, y, null);
        }

        g.setColor(cellBorderColor);
        g.drawRect(cellIndex * cellSize, 0, cellSize - 1, cellSize - 1);

        drawTextBox(g, cellIndex * cellSize, 0, String.valueOf(cellIndex));

        drawLabel(g, cellIndex * cellSize, cellSize, "Delay: " + String.valueOf(delay));

        drawLabel(g, cellIndex * cellSize, cellSize + labelSize, frameName);
    }

    public void drawLabel(Graphics g, int x, int y, String text) {
        int margin = 6;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g.setColor(Color.white);
        g.fillRect(x, y, cellSize, labelSize - 1);

        g.setColor(Color.black);
        g2d.drawString(text, x + margin, y + labelSize - margin / 2);

        g.setColor(cellBorderColor);
        g.drawRect(x, y, cellSize, labelSize - 1);

    }

    public void drawTextBox(Graphics g, int x, int y, String text) {
        int width = g.getFontMetrics().stringWidth(text);
        int height = 12;
        int margin = 6;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g.setColor(Color.white);
        g.fillRect(x, y, width + margin, height + margin / 2);

        g.setColor(Color.black);
        g2d.drawString(text, x + margin / 2, y + height);

        g.setColor(cellBorderColor);
        g.drawRect(x, y, width + margin, height + margin / 2);
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents

        //======== this ========
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                formMousePressed(e);
            }
        });

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addGap(0, 300, Short.MAX_VALUE)
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }
}

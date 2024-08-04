package view.containers;

import view.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.List;

import static controller.constants.DimensionConstants.SCREEN_SIZE;
import static controller.constants.FilePaths.MENU_BACKGROUND_PATH;
import static view.Utils.*;
import static view.containers.GlassFrame.getGlassFrame;

public class PanelB extends JPanel {
    private static final boolean BACKGROUND_RESIZE=false;
    private static final BufferedImage defaultImage=toBufferedImage(MENU_BACKGROUND_PATH.getValue());
    transient BufferedImage imageSave;
    transient BufferedImage currentImage;
    private final GridBagConstraints constraints = new GridBagConstraints();

    public PanelB(Dimension dimension) {
        this(dimension, defaultImage);
    }

    public PanelB(Dimension dimension, BufferedImage image) {
        setImage(image);
        setSize(dimension);
        setBackground(new Color(0, 0, 0, 0));
        setLayout(new GridBagLayout());
        setBorder(null);
        getGlassFrame().add(this);
        setVisible(false);

        getConstraints().insets = new Insets(10, 10, 10, 10);
        getConstraints().fill = GridBagConstraints.BOTH;
        getConstraints().anchor = GridBagConstraints.CENTER;
        getConstraints().gridx = 0;
        getConstraints().gridy = 0;
    }

    public PanelB(int width, int height, BufferedImage image) {
        this(new Dimension(width, height), image);
    }

    public void add(Component component, boolean nextHorizontal, boolean nextVertical) {
        if (nextHorizontal) getConstraints().gridx++;
        if (nextVertical) {
            getConstraints().gridy++;
            getConstraints().gridx = 0;
        }
        add(component, getConstraints());
    }

    public void bulkAdd(List<Component> components, int itemsPerRow) {
        while (!components.isEmpty()) {
            List<Component> rowItems = components.subList(0, Math.min(components.size(), itemsPerRow));
            float preferredAspectRatio = (float) rowItems.get(0).getPreferredSize().height / rowItems.get(0).getPreferredSize().width;
            PanelB temp = new PanelB(new Dimension(getWidth(), (int) (preferredAspectRatio * getWidth())));
            temp.setVisible(true);
            temp.horizontalBulkAdd(rowItems);
            add(temp, false, false);
            components.removeAll(rowItems);
            getConstraints().gridx = 0;
            getConstraints().gridy++;
        }
    }

    public void verticalBulkAdd(Collection<Component> components) {
        getConstraints().gridy = -1;
        for (Component component : components) add(component, false, true);
    }

    public void horizontalBulkAdd(Collection<Component> components) {
        getConstraints().gridx = -1;
        for (Component component : components) add(component, true, false);
    }

    public void togglePanel() {
        setVisible(!isVisible());
    }

    public void setImage(BufferedImage image){
        this.imageSave = bufferedImageClone(image);
        this.currentImage = bufferedImageClone(image);
    }

    @Override
    public void setSize(int width, int height) {
        if (imageSave != null && BACKGROUND_RESIZE) currentImage = Utils.toBufferedImage(imageSave.getScaledInstance(width, height, Image.SCALE_SMOOTH));
        setBounds((SCREEN_SIZE.getValue().width - width) / 2, (SCREEN_SIZE.getValue().height - height) / 2,width,height);
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        if (imageSave!=null && !BACKGROUND_RESIZE) currentImage=cropImage(new Point(x,y),new Dimension(width,height),imageSave,BACKGROUND_RESIZE);
    }

    public GridBagConstraints getConstraints() {
        return constraints;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (currentImage != null) g.drawImage(currentImage, 0, 0, getWidth(), getHeight(), null);
    }
}

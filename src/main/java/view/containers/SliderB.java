package view.containers;

import view.Utils;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;
import java.awt.image.BufferedImage;

import static controller.constants.FilePaths.SLIDER_UI_PATH;
import static controller.constants.UIConstants.*;
import static view.Utils.*;

public class SliderB extends JSlider {
    private transient BufferedImage imageSave;
    private final Container container;
    private JLabel backgroundLabel;
    private ButtonB labelButton;

    public SliderB(Container container, float minimum, float maximum, float current, String name) {
        imageSave=toBufferedImage(SLIDER_UI_PATH.getValue());
        this.container = container;
        setFocusable(false);
        setOpaque(false);
        setMinimum((int) (SLIDER_PRECISION_SCALE.getValue() * minimum));
        setMaximum((int) (SLIDER_PRECISION_SCALE.getValue() * maximum));
        setValue((int) (SLIDER_PRECISION_SCALE.getValue() * current));
        setupLabel(name);
        setMinorTickSpacing((int) ((getMaximum() - getMinimum()) / SLIDER_MINOR_SPACINGS_NUMBER.getValue()));
        setMajorTickSpacing((int) (getMaximum() - getMinimum() / SLIDER_MAJOR_SPACINGS_NUMBER.getValue()));
        addChangeListener(e -> repaint());
    }

    public void setupLabel(String name) {
        labelButton = new ButtonB(ButtonB.ButtonType.SMALL_FIELD_BUTTON, name, (int) SLIDER_LABEL_WIDTH.getValue(),
                SLIDER_LABEL_FONT_SIZE.getValue(), false, true);
    }

    public void setupSliderB() {
        float scale = (float) getWidth() / imageSave.getWidth();
        Dimension desiredDimension = new Dimension((int) (imageSave.getWidth() * scale), (int) (imageSave.getHeight() * scale));
        BufferedImage resized = Utils.toBufferedImage(imageSave.getScaledInstance(desiredDimension.width, desiredDimension.height, Image.SCALE_SMOOTH));
        this.imageSave = bufferedImageClone(resized);
        backgroundLabel = new JLabel(new ImageIcon(resized));
        setUI(new CustomSliderUI(this));
        fireStateChanged();
    }

    public void progress(float progress) {
        BufferedImage progressBar = bufferedImageClone(imageSave);
        Rectangle rectangle = new Rectangle((int) (progress * progressBar.getWidth()), 0,
                (int) ((1 - progress) * progressBar.getWidth()), progressBar.getHeight());
        darkenImage(progressBar, rectangle);
        backgroundLabel.setIcon(new ImageIcon(progressBar));
    }

    public float getPreciseValue() {
        return super.getValue() / SLIDER_PRECISION_SCALE.getValue();
    }

    @Override
    public void repaint() {
        super.repaint();
        if (container != null && container.getLayout() instanceof GridBagLayout gridBagLayout) {
            GridBagConstraints constraints = gridBagLayout.getConstraints(this);
            if (constraints.gridx >= 0 || constraints.gridy >= 0) {
                if (backgroundLabel != null) {
                    progress((float) (getValue() - getMinimum()) / (getMaximum() - getMinimum()));
                    container.add(backgroundLabel, constraints);
                } else setupSliderB();
            }
        }
        revalidate();
    }

    public ButtonB getLabelButton() {
        return labelButton;
    }

    public static class CustomSliderUI extends BasicSliderUI {

        public CustomSliderUI(JSlider sliderB) {
            super(sliderB);
            this.thumbRect = new Rectangle(5, 10);
        }

        @Override
        public void paintThumb(Graphics g) {
            Graphics2D graphics2D = (Graphics2D) g.create();
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2D.translate(thumbRect.x, thumbRect.y);
            graphics2D.setColor(SCI_FI_BLUE.darker().darker());
            graphics2D.fillRect(0, 0, thumbRect.width, thumbRect.height);
            graphics2D.dispose();
        }
    }
}

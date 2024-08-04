package view.containers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import static controller.constants.FilePaths.UI_ELEMENTS_PATH;
import static controller.constants.UIConstants.*;
import static view.Utils.toBufferedImage;
import static view.containers.GlassFrame.getGlassFrame;

public class ButtonB extends JButton {
    private Font plainFont = ORBITRON_FONT.deriveFont(Font.PLAIN, PLAIN_FONT_SIZE.getValue());
    private Font boldFont = ORBITRON_FONT.deriveFont(Font.BOLD, BOLD_FONT_SIZE.getValue());
    boolean dummyButton;
    boolean alignToRight;

    public ButtonB(ButtonType type, String text, int desiredWidth, float fontSizeScale, boolean alignToRight) {
        this(type, text, desiredWidth, fontSizeScale, alignToRight, false);
    }

    public ButtonB(ButtonType type, String text, int desiredWidth, boolean alignToRight) {
        this(type, text, desiredWidth, 1, alignToRight);
    }

    public ButtonB(ButtonType type, String text, int desiredWidth, float fontSizeScale, boolean alignToRight, boolean dummyButton) {
        this.dummyButton = dummyButton;
        this.alignToRight = alignToRight;
        plainFont = plainFont.deriveFont(plainFont.getSize() * fontSizeScale);
        boldFont = boldFont.deriveFont(boldFont.getSize() * fontSizeScale);
        if (dummyButton) {
            setBorderPainted(false);
            setFocusPainted(false);
        }

        String imagePath = UI_ELEMENTS_PATH.getValue() + type.name() + ".png";
        BufferedImage image=toBufferedImage(imagePath);
        float scale = (float) desiredWidth / image.getWidth();
        Dimension desiredDimension = new Dimension((int) (image.getWidth() * scale), (int) (image.getHeight() * scale));
        BufferedImage resized = toBufferedImage(image.getScaledInstance(desiredDimension.width, desiredDimension.height, Image.SCALE_SMOOTH));

        setFocusable(false);
        setOpaque(true);
        setFont(plainFont);
        setBorderPainted(false);
        setForeground(SCI_FI_BLUE);
        setPreferredSize(desiredDimension);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setDoubleBuffered(true);

        setText(text);
        setIcon(new ImageIcon(resized));
        setHorizontalTextPosition(SwingConstants.CENTER);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!dummyButton) {
                    toggleBold();
                    setText(text);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!dummyButton) {
                    toggleBold();
                    setText(text);
                }
            }
        });
    }

    @Override
    public void setText(String text) {
        if (!dummyButton && alignToRight) text = alignToRight(text);
        super.setText(text);
    }

    public String alignToRight(String text) {
        String out = text;
        while ((float) getGlassFrame().getGraphics().getFontMetrics(getFont()).stringWidth(out) / getPreferredSize().width < TEXT_SCALE.getValue()) out = " " + out;
        return out;
    }
    public void toggleBold(){setFont(getFont().equals(plainFont) ? boldFont : plainFont);}

    public enum ButtonType {
        MENU_BUTTON, SMALL_MENU_BUTTON, SMALL_FIELD_BUTTON, TYPE0, TYPE1, TYPE2, TYPE3, ACQUIRED_SKILL, UNACQUIRED_SKILL, ACTIVE_SKILL
    }
}

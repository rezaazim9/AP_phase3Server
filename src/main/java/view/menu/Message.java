package view.menu;

import controller.constants.DefaultMethods;
import view.containers.TopElement;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static controller.UserInterfaceController.playCountdownEffect;
import static controller.constants.DimensionConstants.SCREEN_SIZE;
import static controller.constants.UIConstants.*;
import static view.Utils.changeColorOpacity;
import static view.containers.GlassFrame.getGlassFrame;

public class Message extends JLabel implements TopElement {
    private final JFrame frame;
    private final Color foregroundColor = BLOOD_RED.darker().darker();
    private final float exactLength;
    private float opacity = 1f;

    public Message(MessageType type) {
        this(type, getGlassFrame());
    }

    public Message(MessageType type, JFrame frame) {
        this.frame = frame;
        setBackground(new Color(0, 0, 0, opacity));
        setFont(MANTINIA_FONT.deriveFont(MESSAGE_FONT_SIZE.getValue()));
        setForeground(foregroundColor);
        setText(type.getValue());
        setSize(SCREEN_SIZE.getValue().width, (int) MESSAGE_HEIGHT.getValue());
        setLocation((SCREEN_SIZE.getValue().width - getWidth()) / 2, (SCREEN_SIZE.getValue().height - getHeight()) / 2);
        setHorizontalAlignment(SwingConstants.CENTER);
        setOpaque(true);
        PauseMenu.setPauseAccess(false);
        float length = 2 + playCountdownEffect(Arrays.stream(MessageType.values()).toList().indexOf(type));
        exactLength = (long) (TimeUnit.SECONDS.toNanos(1) * length);
        long startTime = System.nanoTime();
        Timer fadeTimer = new Timer((int) MESSAGE_FADE_INTERVAL.getValue(), null);
        fadeTimer.addActionListener(e -> {
            long elapsedTime = System.nanoTime() - startTime;
            pinOnTop();
            opacity = DefaultMethods.fadeCurve(elapsedTime / getExactLength());
            setBackground(new Color(0, 0, 0, opacity));
            setForeground(changeColorOpacity(foregroundColor, opacity));
            if (elapsedTime > getExactLength()) {
                frame.getContentPane().remove(this);
                this.setVisible(false);
                PauseMenu.setPauseAccess(true);
                fadeTimer.stop();

            }
        });
        fadeTimer.start();
        frame.getContentPane().add(this);
    }

    @Override
    public JFrame getFrame() {
        return frame;
    }

    public float getExactLength() {
        return exactLength;
    }

    public enum MessageType {
        NIHIL, UNUS, DUO, TRES, QUATTUOR, GAME_OVER;

        public String getValue() {
            if (this==GAME_OVER) return "GAME OVER";
            return name();
        }
    }
}

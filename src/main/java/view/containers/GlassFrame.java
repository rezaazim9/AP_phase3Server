package view.containers;

import controller.UserInterfaceController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

import static controller.constants.DimensionConstants.FPS_COUNTER_DIMENSION;
import static controller.constants.DimensionConstants.SCREEN_SIZE;
import static controller.constants.FilePaths.ICON_PATH;
import static controller.constants.ShrinkConstants.MINIMIZE_DELAY;
import static controller.constants.UIConstants.*;
import static view.Utils.changeColorOpacity;

public final class GlassFrame extends JFrame {
    private static GlassFrame INSTANCE;

    private GlassFrame() throws HeadlessException {
        super();
        try {minimizeAll();}
        catch (AWTException e) {throw new UnsupportedOperationException("Failed to minimize");}
        setUndecorated(true);
        setBackground(new Color(1, 0, 0, 1));
        setSize(SCREEN_SIZE.getValue().width, SCREEN_SIZE.getValue().height);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(null);
        setVisible(true);
        setIgnoreRepaint(true);
        setIconImage(Toolkit.getDefaultToolkit().getImage(ICON_PATH.getValue()));
        setupFpsCounter();
    }

    public static void minimizeAll() throws AWTException {
        Robot r = new Robot();
        r.setAutoDelay((int) MINIMIZE_DELAY.getValue());
        r.keyPress(KeyEvent.VK_WINDOWS);
        r.keyPress(KeyEvent.VK_D);
        r.keyRelease(KeyEvent.VK_D);
        r.keyRelease(KeyEvent.VK_WINDOWS);
    }

    public void setupFpsCounter() {
        JLabel fpsCounter=new JLabel();
        fpsCounter.setFont(MANTINIA_FONT.deriveFont(Font.BOLD,FPS_COUNTER_FONT_SIZE.getValue()));
        fpsCounter.setForeground(BLOOD_RED);
        fpsCounter.setLocation(0,0);
        fpsCounter.setSize(200,300);
        fpsCounter.setBackground(changeColorOpacity(SCI_FI_DARK_BLUE, FPS_COUNTER_OPACITY.getValue()));
        fpsCounter.setOpaque(true);
        new Timer(10,e -> {
            fpsCounter.setText(UserInterfaceController.getInfo());
            fpsCounter.setVisible(!fpsCounter.getText().isEmpty());
        }).start();
        add(fpsCounter);
    }

    public static GlassFrame getGlassFrame() {
        if (INSTANCE == null) INSTANCE = new GlassFrame();
        return INSTANCE;
    }
}

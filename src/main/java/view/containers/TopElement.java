package view.containers;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public interface TopElement{
    JFrame getFrame();

    default void pinOnTop() {
        if (this instanceof Component component && getFrame() != null && Arrays.stream(getFrame().getContentPane().getComponents()).toList().contains(component)) {
                for (Component component1 : getFrame().getContentPane().getComponents()) {
                    if (getFrame().getContentPane().getComponentZOrder(component1) < getFrame().getContentPane().getComponentZOrder((Component) this)) {
                        getFrame().getContentPane().setComponentZOrder(component1, getFrame().getContentPane().getComponentZOrder(component1) + 1);
                    }
                }
                getFrame().getContentPane().setComponentZOrder((Component) this, 0);
                getFrame().repaint();
            }

    }
}

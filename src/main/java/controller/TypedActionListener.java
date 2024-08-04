package controller;

import java.awt.event.ActionListener;

public abstract class TypedActionListener implements ActionListener {
    private final ActionListenerType type;
    private Side side=null;
    protected TypedActionListener(ActionListenerType type) {
        this.type = type;
    }

    protected TypedActionListener(ActionListenerType type, Side side) {
        this.type = type;
        this.setSide(side);
    }

    public ActionListenerType getType() {
        return type;
    }

    public Side getSide() {
        return side;
    }

    public void setSide(Side side) {
        this.side = side;
    }

    public enum ActionListenerType {SHRINK, MOVE, ROTATE, DECELERATE, IMPACT}
    public enum Side {LEFT, RIGHT, TOP, BOTTOM, CENTER}
}

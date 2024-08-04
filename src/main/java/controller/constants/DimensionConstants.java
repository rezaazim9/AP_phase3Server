package controller.constants;

import java.awt.*;
import java.awt.geom.Point2D;

import static controller.constants.ViewConstants.*;

public enum DimensionConstants {
    SCREEN_SIZE, EPSILON_DIMENSION, TRIGORATH_DIMENSION, SQUARANTINE_DIMENSION,PORTAL_DIMENSION ,BULLET_DIMENSION, LOGIN_PAGE_DIMENSION,
    MAIN_MENU_DIMENSION, SETTINGS_MENU_DIMENSION, SKILL_TREE_DIMENSION, PAUSE_MENU_DIMENSION,FPS_COUNTER_DIMENSION;

    public Dimension getValue() {
        return switch (this) {
            case SCREEN_SIZE -> Toolkit.getDefaultToolkit().getScreenSize();
            case PORTAL_DIMENSION -> new Dimension((int) PORTAL_FACTOR.getValue(), (int) PORTAL_FACTOR.getValue());
            case EPSILON_DIMENSION -> new Dimension((int) EPSILON_FACTOR.getValue(), (int) EPSILON_FACTOR.getValue());
            case TRIGORATH_DIMENSION -> new Dimension((int) (TRIGORATH_FACTOR.getValue() * Math.sqrt(3) / 2), (int) TRIGORATH_FACTOR.getValue());
            case SQUARANTINE_DIMENSION -> new Dimension((int) SQUARANTINE_FACTOR.getValue(), (int) SQUARANTINE_FACTOR.getValue());
            case BULLET_DIMENSION -> new Dimension((int) BULLET_FACTOR.getValue(), (int) BULLET_FACTOR.getValue());
            case LOGIN_PAGE_DIMENSION -> new Dimension(750,400);
            case MAIN_MENU_DIMENSION -> new Dimension(700, 800);
            case SETTINGS_MENU_DIMENSION -> new Dimension(900, 400);
            case SKILL_TREE_DIMENSION -> new Dimension(900, 750);
            case PAUSE_MENU_DIMENSION -> new Dimension(900, 750);
            case FPS_COUNTER_DIMENSION -> new Dimension(100,55);
        };
    }

    public enum Dimension2DConstants {
        DEFORM_DIMENSION, MAIN_MOTIONPANEL_DIMENSION;

        public Point2D getValue() {
            return switch (this) {

                case DEFORM_DIMENSION -> new Point2D.Float(400, 400);
                case MAIN_MOTIONPANEL_DIMENSION -> new Point2D.Float(800, 800);
            };
        }
    }
}


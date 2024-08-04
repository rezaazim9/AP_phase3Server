package controller.constants;

import controller.GameLoop;
import view.characters.GeoShapeView;
import view.characters.SquarantineView;
import view.characters.TrigorathView;

import javax.sound.sampled.Clip;
import java.awt.*;

import static controller.AudioHandler.clips;
import static controller.constants.FilePaths.SQUARANTINE_IMAGEPATH;
import static controller.constants.FilePaths.TRIGORATH_IMAGEPATH;
import static view.Utils.averageTone;
import static view.characters.GeoShapeView.getRawImage;

public class DefaultMethods {
    private DefaultMethods() {}

    //SIN,COS PRE-PROCESSING//
    public static final double[] sinTable = new double[361];
    public static final double[] cosTable = new double[361];
    public static  final double[] radianTable = new double[361];

    static {
        for (int i = 0; i < 360; i++) {
            double radian = Math.toRadians(i);
            DefaultMethods.sinTable[i] = Math.sin(radian);
            DefaultMethods.cosTable[i] = Math.cos(radian);
            DefaultMethods.radianTable[i] = radian;
        }
    }

    public static float getCenterOffset(float radius) {
        return (radius + 1) / 2;
    }

    public static float getVolumeDB(Clip clip) {
        return switch (clips.get(clip)) {
            case MENU_THEME -> 0.1f;
            case GAME_THEME -> 0.082f;
            case SHOOT -> 0.05f;
            case COUNTDOWN -> 0.235f;
            default -> 0.172f;
        };
    }

    public static Color GET_AVERAGE_TONE_OF_CHARACTER(GeoShapeView geoShapeView) {
        Color color;
        if (geoShapeView.getClass().equals(SquarantineView.class)) color = averageTone(getRawImage(SQUARANTINE_IMAGEPATH.getValue()));
        else if (geoShapeView.getClass().equals(TrigorathView.class)) color = averageTone(getRawImage(TRIGORATH_IMAGEPATH.getValue()));
        else color = new Color(0, 0, 0, 0);
        return color;
    }

    public static String ABILITY_ACTIVATE_MESSAGE(int cost) {
        return "Do you want to activate this skill for " + cost + " XP?";
    }

    public static String PURCHASE_MESSAGE(int cost) {
        return "Do you want to purchase this skill for " + cost + " XP?";
    }
    public static String PORTAL_MESSAGE() {
        return "Do you want to save for "+ GameLoop.getPR() + " XP?"+"or continue?" ;
    }
    public static String INSUFFICIENT_XP_MESSAGE() {
        return "You don't have enough XP.";
    }

    public static String SUCCESSFUL_PURCHASE_MESSAGE(String name) {
        return "You learned " + name + ".";
    }

    public static String SUCCESSFUL_ACTIVATE_MESSAGE(String name) {
        return "You activated " + name + ".";
    }

    public static String UNSUCCESSFUL_PURCHASE_MESSAGE(String name) {
        return "You don't have enough XP to learn " + name + ".";
    }

    public static String UNSUCCESSFUL_ACTIVATE_MESSAGE(String name) {
        return "You don't have enough XP to activate " + name + ".";
    }

    public static String SKILL_ACTIVATE_MESSAGE(String name) {
        return "Do you want to choose \"" + name + "\" as your active skill?";
    }

    /**
     * A map [0,1]->[0,1] to demonstrate the fade process
     *
     * @return momentary opacity
     */
    public static float fadeCurve(float x) {
        if (x < 0 || x > 1) return 0;
        return (float) (0.3f * Math.sqrt(1 - x * x) + 0.7f * (1 - Math.pow(x, 5)));
    }
}

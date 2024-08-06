package controller;

import model.Profile;
import model.characters.EpsilonModel;
import model.characters.GeoShapeModel;
import model.entities.Ability;
import model.entities.Skill;
import model.movement.Movable;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import static model.characters.GeoShapeModel.allShapeModelsList;


public abstract class UserInterfaceController {




    public static void fireSkill() {
        if (Skill.getActiveSkill() != null) Skill.getActiveSkill().fire();
    }

    public static boolean isGameOn() {
        return GameLoop.getINSTANCE().isOn();
    }

    public static boolean isGameRunning() {return GameLoop.getINSTANCE().isRunning();}

    public static void toggleGameRunning() {GameLoop.getINSTANCE().toggleGameLoop();}
    public static void exitGame() {
        GameLoop.getINSTANCE().forceExitGame();
        GameLoop.getINSTANCE().toggleGameLoop();
        GameLoop.getINSTANCE().setRunning(false);
        EpsilonModel.flushINSTANCE();

        Movable.movables.clear();
    }

    public static void playGameTheme(Container container) {
        AudioHandler.playSoundEffect(AudioHandler.SoundEffectType.GAME_THEME, container);
    }



    public static void playHitSoundEffect() {
        AudioHandler.playSoundEffect(AudioHandler.SoundEffectType.HIT);
    }

    public static void playDownSoundEffect() {
        AudioHandler.playSoundEffect(AudioHandler.SoundEffectType.DOWN);
    }

    public static void playXPSoundEffect() {
        AudioHandler.playSoundEffect(AudioHandler.SoundEffectType.XP);
    }

    public static void playShootSoundEffect() {
        AudioHandler.playSoundEffect(AudioHandler.SoundEffectType.SHOOT);
    }



    public static boolean canActiveAbility(String abilityName) {
        Ability ability = findAbility(abilityName);
        if (ability == null) return false;
        return Profile.getCurrent().getCurrentGameXP() >= ability.getCost();
    }


    public static Skill findSkill(String name) {
        for (Skill.SkillType type : Skill.SkillType.values()) {
            for (Skill skill : Skill.values()) {
                if (skill.getType()==type && skill.getName().equals(name)) return skill;
            }
        }
        return null;
    }

    public static Ability findAbility(String name) {
        for (Ability ability : Ability.values()) if (ability.getName().equals(name)) return ability;
        return null;
    }

    public static List<Point> getGeoShapeVertices(String viewId) {
        GeoShapeModel model = findModel(viewId);
        if (model == null) {
            return new CopyOnWriteArrayList<>();
        }
        return null;
    }
    public static float getHealthScale(String viewId) {
        GeoShapeModel model = findModel(viewId);
        if (model != null && model.isVulnerable()) {
            if (model.getFullHealth() == 0) return 1;
            return (float) model.getHealth() / model.getFullHealth();
        }
        return 1;
    }


    public static synchronized GeoShapeModel findModel(String viewId) {
        for (GeoShapeModel shapeModel : allShapeModelsList) {
            if (Objects.equals(viewId, shapeModel.getModelId())) return shapeModel;
        }
        return null;
    }

    public static synchronized String findMotionPanelModel(String motionPanelId) {
        return null;
    }



    public static String getMainMotionPanelId() {
        return null;
    }

    public static Point2D getMotionPanelCenterLocation(String motionPanelId) {
        return null;
    }

    public static float showMessage() {
        return 0;
    }
}
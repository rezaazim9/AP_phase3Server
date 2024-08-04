package controller;

import model.JsonOperator;
import model.MotionPanelModel;
import model.Profile;
import model.WaveManager;
import model.characters.EpsilonModel;
import model.characters.GeoShapeModel;
import model.collision.Collidable;
import model.entities.Ability;
import model.entities.Skill;
import model.movement.Movable;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import view.characters.*;
import view.containers.MotionPanelView;
import view.menu.MainMenu;
import view.menu.Message;

import javax.sound.sampled.Clip;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

import static controller.AudioHandler.clips;
import static model.MotionPanelModel.*;
import static model.Utils.*;
import static model.characters.GeoShapeModel.allShapeModelsList;
import static view.characters.GeoShapeView.allShapeViewsList;
import static view.containers.MotionPanelView.allMotionPanelViewsList;
import static view.containers.MotionPanelView.setMainMotionPanelView;

public abstract class UserInterfaceController {
    private UserInterfaceController(){}
    public static void createEpsilon(String modelId, Point anchor, String motionPanelId) {
        EpsilonView view = new EpsilonView(anchor, findMotionPanelView(motionPanelId));
        view.setViewId(modelId);
    }
    public static void createPortal(String modelId, Point anchor, String motionPanelId) {
        PortalView view = new PortalView(anchor, findMotionPanelView(motionPanelId));
        view.setViewId(modelId);
    }
    public static void createTrigorath(String modelId, Point anchor, String motionPanelId) {
        TrigorathView view = new TrigorathView(anchor, findMotionPanelView(motionPanelId));
        view.setViewId(modelId);
    }

    public static void createSquarantine(String modelId, Point anchor, String motionPanelId) {
        SquarantineView view = new SquarantineView(anchor, findMotionPanelView(motionPanelId));
        view.setViewId(modelId);
    }

    public static void createCollectible(String modelId, String ancestorId, int value, Point anchor, String motionPanelId) {
        CollectibleView collectibleView = new CollectibleView(anchor, value, findView(ancestorId), findMotionPanelView(motionPanelId));
        collectibleView.setViewId(modelId);
    }

    public static void createBullet(String modelId, Point referenceAnchor, String motionPanelId) {
        BulletView view = new BulletView(referenceAnchor, findMotionPanelView(motionPanelId));
        view.setViewId(modelId);
    }

    public static void createMotionPanel(String modelId, Point2D dimension, Point2D location) {
        MotionPanelView view = new MotionPanelView(pointToDimension(dimension), roundPoint(location));
        view.setVisible(true);
        view.setViewId(modelId);
    }
    public static String getInfo(){return GameLoop.getInfo();}

    public static void eliminateView(String modelId, String motionPanelId) {
        GeoShapeView shapeView = findView(modelId);
        assert shapeView != null;
        allShapeViewsList.remove(shapeView);
        MotionPanelView motionPanelView = findMotionPanelView(motionPanelId);
        if (motionPanelView != null) motionPanelView.shapeViews.remove(shapeView);
    }

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
        for (MotionPanelView motionPanelView : allMotionPanelViewsList) {
            motionPanelView.shapeViews.clear();
            motionPanelView.setVisible(false);
        }
        MainMenu.spawn.interrupt();
        WaveManager.waveEntities.clear();
        setMainMotionPanelModel(null);
        setMainMotionPanelView(null);
        allMotionPanelViewsList.clear();
        allMotionPanelModelsList.clear();
        allShapeModelsList.clear();
        allShapeViewsList.clear();
        Collidable.collidables.clear();
        Movable.movables.clear();
    }

    public static void playGameTheme(Container container) {
        AudioHandler.playSoundEffect(AudioHandler.SoundEffectType.GAME_THEME, container);
    }

    public static void playMenuTheme() {
        AtomicReference<Boolean> atomicReference = new AtomicReference<>(false);
        ActionListener actionListener = e -> atomicReference.set(isGameRunning());
        AudioHandler.playSoundEffect(AudioHandler.SoundEffectType.MENU_THEME, actionListener, atomicReference);
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

    public static float playCountdownEffect(int i) {
        return AudioHandler.playSoundEffect(AudioHandler.SoundEffectType.COUNTDOWN, i);
    }

    public static void safeExitApplication() {
        for (Clip clip : clips.keySet()) {
            clip.stop();
            clips.remove(clip);
        }
        JsonOperator.setProceedToSaveLoad(false);
    }

    /**
     * @return a thread-safe hashmap mapping to every skill category name (as key), a list of triples
     * (as value) of name,cost,acquired status of all skills in that category
     */
    public static ConcurrentMap<String, List<Triple<String,Integer,Boolean>>> getSkillTypesData() {
        ConcurrentMap<String, List<Triple<String,Integer,Boolean>>> out = new ConcurrentHashMap<>();
        for (Skill.SkillType type : Skill.SkillType.values()) {
            List<Triple<String,Integer,Boolean>> skills = new CopyOnWriteArrayList<>();
            for (Skill skill : Skill.values()) {
                if (skill.getType()==type)  skills.add(new MutableTriple<>(skill.getName(),skill.getCost(), skill.isAcquired()));
            }
            out.put(type.name(),skills);
        }
        return out;
    }

    /**
     * @return a thread-safe hashmap mapping to every ability name (as key) its activation cost (as value)
     */
    public static ConcurrentMap<String, Integer> getAbilitiesData() {
        ConcurrentHashMap<String, Integer> out = new ConcurrentHashMap<>();
        for (Ability ability : Ability.values()) out.put(ability.getName(), ability.getCost());
        return out;
    }

    public static String getActiveSkill() {
        if (Skill.getActiveSkill() == null) return null;
        return Skill.getActiveSkill().getName();
    }

    public static void setActiveSkill(String skillName) {
        if (Objects.requireNonNull(findSkill(skillName)).isAcquired()) Skill.setActiveSkill(findSkill(skillName));
    }

    public static boolean purchaseSkill(String skillName) {
        Skill skill = findSkill(skillName);
        if (skill == null) return false;
        if (Profile.getCurrent().getTotalXP() < skill.getCost()) return false;
        Profile.getCurrent().setTotalXP(Profile.getCurrent().getTotalXP() - skill.getCost());
        skill.setAcquired(true);
        return true;
    }
    public static boolean canActiveAbility(String abilityName) {
        Ability ability = findAbility(abilityName);
        if (ability == null) return false;
        return Profile.getCurrent().getCurrentGameXP() >= ability.getCost();
    }
    public static boolean activateAbility(String abilityName) {
        Ability ability = findAbility(abilityName);
        if (ability == null) return false;
        if (Profile.getCurrent().getCurrentGameXP() < ability.getCost()) return false;
        Profile.getCurrent().setCurrentGameXP(Profile.getCurrent().getCurrentGameXP() - ability.getCost());
        ability.getAction().actionPerformed(new ActionEvent(new Object(), ActionEvent.ACTION_PERFORMED, null));
        return true;
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

        MotionPanelModel motionPanelModel = findMotionPanelModel(model.getMotionPanelId());
        if (motionPanelModel == null) {
            return new CopyOnWriteArrayList<>();
        }

        Point2D motionPanelLocation = motionPanelModel.getLocation();
        if (motionPanelLocation == null) {
            return new CopyOnWriteArrayList<>();
        }

        CopyOnWriteArrayList<Point> out = new CopyOnWriteArrayList<>();
        for (Point2D point2D : model.getVertices()) {
            out.add(roundPoint(relativeLocation(point2D, motionPanelLocation)));
        }
        return out;
    }

    public static int[] getMotionPanelProperties(String motionPanelId) {
        MotionPanelModel model = findMotionPanelModel(motionPanelId);
        assert model != null;
        Point location = roundPoint(model.getLocation());
        Point dimension = roundPoint(model.getDimension());
        return new int[]{location.x, location.y, dimension.x, dimension.y};
    }

    public static float getHealthScale(String viewId) {
        GeoShapeModel model = findModel(viewId);
        if (model != null && model.isVulnerable()) {
            if (model.getFullHealth() == 0) return 1;
            return (float) model.getHealth() / model.getFullHealth();
        }
        return 1;
    }

    public static synchronized GeoShapeView findView(String modelId) {
        for (GeoShapeView shapeView : allShapeViewsList) {
            if (modelId.equals(shapeView.getViewId())) return shapeView;
        }
        return null;
    }

    public static synchronized GeoShapeModel findModel(String viewId) {
        for (GeoShapeModel shapeModel : allShapeModelsList) {
            if (viewId==(shapeModel.getModelId())) return shapeModel;
        }
        return null;
    }

    public static synchronized MotionPanelModel findMotionPanelModel(String motionPanelId) {
        for (MotionPanelModel motionPanelModel : allMotionPanelModelsList) {
            if (motionPanelId.equals(motionPanelModel.getModelId())) return motionPanelModel;
        }
        return null;
    }

    public static synchronized MotionPanelView findMotionPanelView(String motionPanelId) {
        for (MotionPanelView motionPanelView : allMotionPanelViewsList) {
            if (motionPanelId.equals(motionPanelView.getViewId())) return motionPanelView;
        }
        return null;
    }

    public static void moveGeoShape(String modelId, Point2D newAnchorLocation) {
        GeoShapeView view = findView(modelId);
        if (view != null) view.moveShapeView(roundPoint(newAnchorLocation));
    }

    public static void rotateGeoShape(String modelId, float angle) {
        GeoShapeView view = findView(modelId);
        if (view != null) view.rotateShapeView(angle);
    }

    public static String getMainMotionPanelId() {
        return getMainMotionPanelModel().getModelId();
    }

    public static Point2D getMotionPanelCenterLocation(String motionPanelId) {
        MotionPanelModel motionPanelModel = findMotionPanelModel(motionPanelId);
        assert motionPanelModel != null;
        return addUpPoints(motionPanelModel.getLocation(), multiplyPoint(motionPanelModel.getDimension(), 1 / 2F));
    }

    public static float showMessage(int i) {
        if (i >= 0 && i < Message.MessageType.values().length) return new Message(Message.MessageType.values()[i]).getExactLength();
        if (i == -1) return new Message(Message.MessageType.GAME_OVER).getExactLength();
        return 0;
    }
}
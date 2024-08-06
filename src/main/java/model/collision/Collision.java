package model.collision;

import controller.GameLoop;
import controller.constants.DefaultMethods;
import model.Profile;
import model.characters.CollectibleModel;
import model.characters.EpsilonModel;
import model.characters.GeoShapeModel;
import model.characters.PortalModel;
import model.entities.AttackTypes;
import model.entities.Entity;
import model.projectiles.BulletModel;
import org.apache.commons.lang3.tuple.Pair;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static controller.AudioHandler.random;
import static controller.UserInterfaceController.*;
import static controller.constants.UIMessageConstants.PURCHASE_TITLE;
import static model.characters.GeoShapeModel.allShapeModelsList;

public final class Collision implements Runnable {
    private static Collision INSTANCE = null;

    public static Collision getINSTANCE() {
        if (INSTANCE == null) INSTANCE = new Collision();
        return INSTANCE;
    }

    /**
     * @return a thread-safe hashmap of post-collision data of all GeoShapes (direction,scale,rotation)
     */
    public static List<MovementState.ShapeMovementState> evaluateMovementEffects(MovementState.CollisionState state, boolean artificialImpact) {
        CopyOnWriteArrayList<MovementState.ShapeMovementState> out = new CopyOnWriteArrayList<>();
        for (GeoShapeModel shapeModel : allShapeModelsList) {
            if (shapeModel instanceof BulletModel || (shapeModel instanceof CollectibleModel collectibleModel &&
                    state.stateOf1.collidable != collectibleModel.ancestor && state.stateOf2.collidable != collectibleModel.ancestor));

        }
        return out;
    }

    public static void emitImpactWave(MovementState.CollisionState state, boolean artificialImpact, float wavePower) {
        List<MovementState.ShapeMovementState> collisionData = evaluateMovementEffects(state, artificialImpact);
        for (MovementState.ShapeMovementState movementState : collisionData) {
            movementState.geoShapeModel.getMovement().impact(movementState.direction, wavePower * movementState.scale);
            if (movementState.torque != 0)
                movementState.geoShapeModel.getMovement().setAngularSpeed(movementState.torque);
        }
    }


    public static void evaluatePhysicalEffects(MovementState.CollisionState state) {
        if (state.stateOf1.collidable instanceof Entity entity1 && state.stateOf2.collidable instanceof Entity entity2 && state.collisionPoint != null) {
            Pair<Boolean, Boolean> meleePair = checkMelee();
            boolean epsilonMelee = random.nextFloat() < Profile.getCurrent().getEpsilonMeleeDamageProbability();

            if (meleePair.getLeft() && meleePair.getRight()) return;
            if (entity1.isVulnerable() && (state.stateOf2.collidable instanceof BulletModel || state.stateOf1.collidable instanceof CollectibleModel || meleePair.getRight())) {
                if (entity2 instanceof EpsilonModel)
                    EpsilonModel.getINSTANCE().healEpsilon();
                if (epsilonMelee || !(entity1 instanceof EpsilonModel)) {
                    entity2.damage(entity1, AttackTypes.MELEE);
                }
            }
            if (entity2.isVulnerable() && (state.stateOf1.collidable instanceof BulletModel || state.stateOf2.collidable instanceof CollectibleModel || meleePair.getLeft())) {
                if (entity1 instanceof EpsilonModel)
                    EpsilonModel.getINSTANCE().healEpsilon();
                if (epsilonMelee || !(entity2 instanceof EpsilonModel))
                    entity1.damage(entity2, AttackTypes.MELEE);
            }
        }
    }

    public static Pair<Boolean, Boolean> checkMelee() {
        return null;
    }

    public static void resolveCollectiblePickup(MovementState.CollisionState state) {
        if (state.stateOf1.collidable instanceof EpsilonModel && state.stateOf2.collidable instanceof CollectibleModel) {
            Profile.getCurrent().setCurrentGameXP(Profile.getCurrent().getCurrentGameXP() + ((CollectibleModel) state.stateOf2.collidable).getValue());
        }
        if (state.stateOf2.collidable instanceof EpsilonModel && state.stateOf1.collidable instanceof CollectibleModel) {
            Profile.getCurrent().setCurrentGameXP(Profile.getCurrent().getCurrentGameXP() + ((CollectibleModel) state.stateOf1.collidable).getValue());
        }
    }

    @Override
    public void run() {
        Collidable.CreateAllGeometries();
        List<MovementState.CollisionState> collisionStates = getAllMomentaryCollisions();
        for (MovementState.CollisionState state : collisionStates) {
            boolean notNull = state.stateOf1 != null && state.stateOf2 != null;
            if (!notNull) continue;
            if (state.stateOf1.collidable instanceof PortalModel portalModel && state.stateOf2.collidable instanceof EpsilonModel) {
                portalHandler(portalModel);
            }
            if (state.stateOf2.collidable instanceof PortalModel portalModel && state.stateOf1.collidable instanceof EpsilonModel) {
                portalHandler(portalModel);
            }
            if (state.stateOf1.collidable instanceof BulletModel bulletModel) bulletModel.eliminate();
            if (state.stateOf2.collidable instanceof BulletModel bulletModel) bulletModel.eliminate();
            if (state.stateOf1.collidable instanceof PortalModel || state.stateOf2.collidable instanceof PortalModel)
                continue;
            evaluatePhysicalEffects(state);
            resolveCollectiblePickup(state);

        }
    }


    public void portalHandler(PortalModel portalModel) {
        toggleGameRunning();
        GameLoop.getINSTANCE().setRunning(false);
        EpsilonModel.getINSTANCE().deactivateMovement();
        int action = JOptionPane.showConfirmDialog(new JOptionPane(), DefaultMethods.PORTAL_MESSAGE(), PURCHASE_TITLE.getValue(), JOptionPane.YES_NO_OPTION);
        if (JOptionPane.YES_OPTION == action) {
            if (Profile.getCurrent().getCurrentGameXP() >= GameLoop.getPR()) {

            } else {
                JOptionPane.showOptionDialog(new JOptionPane(), DefaultMethods.INSUFFICIENT_XP_MESSAGE(), PURCHASE_TITLE.getValue(), JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new Object[]{}, null);
                portalHandler(portalModel);
            }
        } else {
            toggleGameRunning();
            EpsilonModel.getINSTANCE().activateMovement();
            Profile.getCurrent().setCurrentGameXP(Profile.getCurrent().getCurrentGameXP() + GameLoop.getPR() / 10);
            GameLoop.getINSTANCE().setRunning(true);
        }
        portalModel.eliminate();
    }

    public List<MovementState.CollisionState> getAllMomentaryCollisions() {
        CopyOnWriteArrayList<MovementState.CollisionState> collisionStates = new CopyOnWriteArrayList<>();
        for (int i = 0; i < Collidable.collidables.size(); i++) {
            for (int j = i + 1; j < Collidable.collidables.size(); j++) {
                MovementState.CollisionState state = null;
                if (Collidable.collidables.size() > i && Collidable.collidables.size() > j) {
                    state = Collidable.collidables.get(i).checkCollision(Collidable.collidables.get(j));
                }
                if (state != null) collisionStates.add(state);
            }
        }
        return collisionStates;
    }
}
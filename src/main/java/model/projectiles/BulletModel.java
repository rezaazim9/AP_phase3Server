package model.projectiles;

import model.characters.GeoShapeModel;
import model.entities.AttackTypes;

import java.awt.*;

import static controller.UserInterfaceController.createBullet;
import static controller.constants.EntityConstants.BULLET_HEALTH;
import static controller.constants.EntityConstants.EntityVertices.BULLET_VERTICES;
import static controller.constants.EntityConstants.PointConstants.BULLET_CENTER;
import static controller.constants.MovementConstants.BULLET_SPEED;
import static model.Utils.deepClone;
import static model.Utils.roundPoint;

public class BulletModel extends GeoShapeModel {
    public BulletModel(Point anchor, String motionPanelId, int damage) {
        super(new Point(0, 0), BULLET_VERTICES.getValue(), BULLET_HEALTH.getValue());
        this.setCircular(true);
        setMotionPanelId(motionPanelId);
        this.setAnchorSave(deepClone(BULLET_CENTER.getValue()));
        assert getAnchorSave() != null;
        createBullet(getModelId(), roundPoint(getAnchorSave()), motionPanelId);
        moveShapeModel(anchor);
        getMovement().setAnchor(anchor);
        getMovement().setAngularSpeed(0);
        getMovement().setSpeed(BULLET_SPEED.getValue());
        getMovement().setSpeedSave(BULLET_SPEED.getValue());
        getDamageSize().put(AttackTypes.MELEE, damage);
    }
}

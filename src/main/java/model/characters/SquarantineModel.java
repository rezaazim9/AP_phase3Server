package model.characters;

import model.entities.AttackTypes;

import java.awt.*;

import static controller.UserInterfaceController.createSquarantine;
import static controller.constants.EntityConstants.EntityVertices.SQUARANTINE_VERTICES;
import static controller.constants.EntityConstants.SQUARANTINE_HEALTH;
import static controller.constants.EntityConstants.SQUARANTINE_MELEE_DAMAGE;
import static model.Utils.roundPoint;

public class SquarantineModel extends GeoShapeModel {

    public SquarantineModel(Point anchor, String motionPanelId) {
        super(new Point(0, 0), SQUARANTINE_VERTICES.getValue(), SQUARANTINE_HEALTH.getValue());
        this.setCircular(false);
        setMotionPanelId(motionPanelId);
        this.setAnchorSave(getCenter());
        getDamageSize().put(AttackTypes.MELEE, SQUARANTINE_MELEE_DAMAGE.getValue());
        createSquarantine(getModelId(), roundPoint(getAnchorSave()), motionPanelId);
        moveShapeModel(anchor);
        getMovement().setAnchor(anchor);
        setNumberOfCollectibles(1);
        setCollectibleValue(5);
    }
}
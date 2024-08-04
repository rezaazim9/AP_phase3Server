package model.characters;

import model.entities.AttackTypes;

import java.awt.*;

import static controller.UserInterfaceController.createTrigorath;
import static controller.constants.EntityConstants.EntityVertices.TRIGORATH_VERTICES;
import static controller.constants.EntityConstants.TRIGORATH_HEALTH;
import static controller.constants.EntityConstants.TRIGORATH_MELEE_DAMAGE;
import static model.Utils.roundPoint;

public class TrigorathModel extends GeoShapeModel {
    public TrigorathModel(Point anchor, String motionPanelId) {
        super(new Point(0, 0), TRIGORATH_VERTICES.getValue(), TRIGORATH_HEALTH.getValue());
        this.setCircular(false);
        setMotionPanelId(motionPanelId);
        this.setAnchorSave(getCenter());
        getDamageSize().put(AttackTypes.MELEE, TRIGORATH_MELEE_DAMAGE.getValue());
        createTrigorath(getModelId(), roundPoint(getAnchorSave()), motionPanelId);
        moveShapeModel(anchor);
        getMovement().setAnchor(anchor);
        setNumberOfCollectibles(2);
        setCollectibleValue(5);
    }
}

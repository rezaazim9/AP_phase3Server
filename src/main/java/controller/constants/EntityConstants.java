package controller.constants;

import model.Profile;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static controller.constants.DimensionConstants.*;

public enum EntityConstants {
    EPSILON_HEALTH, SHOTS_PER_SECOND, SKILL_COOLDOWN_IN_MINUTES,   COLLECTIBLE_LIFE_TIME, EPSILON_RAPID_SHOOTING_DELAY, EPSILON_SHOOTING_RAPIDITY,
    TRIGORATH_HEALTH, TRIGORATH_MELEE_DAMAGE, SQUARANTINE_HEALTH, SQUARANTINE_MELEE_DAMAGE, BULLET_HEALTH, COLLECTIBLE_HEALTH, PORTAL_HEALTH, PORTAL_LIFE_TIME;

    public int getValue() {
        return switch (this) {
            case EPSILON_SHOOTING_RAPIDITY -> Profile.getCurrent().getEpsilonShootingRapidity();
            case EPSILON_HEALTH -> 100;
            case SHOTS_PER_SECOND -> 2;
            case SKILL_COOLDOWN_IN_MINUTES -> 1;
            case EPSILON_RAPID_SHOOTING_DELAY -> 50;
            case TRIGORATH_HEALTH -> 15;
            case TRIGORATH_MELEE_DAMAGE -> 10;
            case SQUARANTINE_HEALTH -> 10;
            case SQUARANTINE_MELEE_DAMAGE -> 6;
            case BULLET_HEALTH, COLLECTIBLE_HEALTH -> 0;
            case PORTAL_HEALTH -> Integer.MAX_VALUE;
            case COLLECTIBLE_LIFE_TIME -> 8;
            case PORTAL_LIFE_TIME ->7;
        };
    }

    public enum EntityVertices {
        TRIGORATH_VERTICES, SQUARANTINE_VERTICES, BULLET_VERTICES, EPSILON_VERTICES, PORTAL_VERTICES,COLLECTIBLE_VERTICES;

        public List<Point2D> getValue() {
            return switch (this) {
                case TRIGORATH_VERTICES -> new CopyOnWriteArrayList<>(
                        List.of(new Point2D.Float(0, 0), new Point2D.Float(0, TRIGORATH_DIMENSION.getValue().height),
                                new Point2D.Float(TRIGORATH_DIMENSION.getValue().width, TRIGORATH_DIMENSION.getValue().height / 2F)));
                case SQUARANTINE_VERTICES -> new CopyOnWriteArrayList<>(
                        List.of(new Point2D.Float(0, 0), new Point2D.Float(0, SQUARANTINE_DIMENSION.getValue().height),
                                new Point2D.Float(SQUARANTINE_DIMENSION.getValue().width, SQUARANTINE_DIMENSION.getValue().height),
                                new Point2D.Float(SQUARANTINE_DIMENSION.getValue().width, 0)));
                case BULLET_VERTICES, EPSILON_VERTICES, COLLECTIBLE_VERTICES,PORTAL_VERTICES -> new CopyOnWriteArrayList<>();
            };
        }
    }

    public enum PointConstants {
        EPSILON_CENTER, BULLET_CENTER, PORTAL_CENTER;

        public Point2D getValue() {
            return switch (this) {
                case PORTAL_CENTER -> new Point2D.Float(PORTAL_DIMENSION.getValue().width / 2F, PORTAL_DIMENSION.getValue().height / 2F);
                case EPSILON_CENTER -> new Point2D.Float(EPSILON_DIMENSION.getValue().width / 2F, EPSILON_DIMENSION.getValue().height / 2F);
                case BULLET_CENTER -> new Point2D.Float(BULLET_DIMENSION.getValue().width / 2F, BULLET_DIMENSION.getValue().height / 2F);
            };
        }
    }
}

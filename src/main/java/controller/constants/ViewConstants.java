package controller.constants;

import model.Profile;

public enum ViewConstants {
    VERTEX_RADIUS, BASE_PAINT_OPACITY, EPSILON_FACTOR, TRIGORATH_FACTOR,PORTAL_FACTOR,
    SQUARANTINE_FACTOR, BULLET_FACTOR, COLLECTIBLE_SIZE_OFFSET, ;

    public float getValue() {
        return switch (this) {

            case VERTEX_RADIUS -> Profile.getCurrent().getSizeScale() * 6;
            case BASE_PAINT_OPACITY -> 0.5f;
            case EPSILON_FACTOR -> Profile.getCurrent().getSizeScale() * 50;
            case TRIGORATH_FACTOR -> Profile.getCurrent().getSizeScale() * 70;
            case SQUARANTINE_FACTOR -> Profile.getCurrent().getSizeScale() * 60;
            case BULLET_FACTOR -> Profile.getCurrent().getSizeScale() * 20;
            case COLLECTIBLE_SIZE_OFFSET -> 9;
            case PORTAL_FACTOR -> Profile.getCurrent().getSizeScale() * 75;
        };
    }
}

package controller.constants;

import model.Profile;

public enum ShrinkConstants {
    SHRINK_DELAY, SHRINK_SCALE, EXTEND_SPEED_SCALE, DEFORM_SPEED_SENSITIVITY, SHRINK_THRESHOLD_SENSITIVITY,
    DEFORM_DECELERATION, DEFORM_VELOCITY, DEFORM_SENSITIVITY, EXTENSION_LENGTH, MINIMIZE_DELAY, DEFORM_DECAY;

    public float getValue() {
        return switch (this) {
            case SHRINK_DELAY -> (int) (900 / Profile.getCurrent().getGameSpeed());
            case SHRINK_SCALE -> 0.0028F * Profile.getCurrent().getGameSpeed();
            case EXTEND_SPEED_SCALE -> 0.2F * Profile.getCurrent().getGameSpeed();
            case DEFORM_SPEED_SENSITIVITY -> 0.01f;
            case SHRINK_THRESHOLD_SENSITIVITY -> 0.01F;
            case DEFORM_DECELERATION -> -0.0001f * Profile.getCurrent().getGameSpeed();
            case DEFORM_VELOCITY -> 0.03F * Profile.getCurrent().getGameSpeed();
            case DEFORM_SENSITIVITY -> 5;
            case EXTENSION_LENGTH -> 30;
            case MINIMIZE_DELAY -> 250;
            case DEFORM_DECAY -> 1;
        };
    }
}

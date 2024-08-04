package controller.constants;

import model.Profile;

import java.util.concurrent.TimeUnit;

public enum MovementConstants {
    EPSILON_SPEED, ANGULAR_SPEED_BOUND, DEFAULT_ANGULAR_DECAY,
    MAX_SAFE_ROTATION, DEFAULT_SPEED, BULLET_SPEED, DEFAULT_DECELERATION,
    DECELERATION_DECAY, DECELERATION_SENSITIVITY, DAMPEN_FACTOR, POSITION_UPDATE_INTERVAL;

    public float getValue() {
        return switch (this) {
            case EPSILON_SPEED -> Profile.getCurrent().getSizeScale() * Profile.getCurrent().getGameSpeed() * 80f / Profile.getCurrent().getUps();
            case ANGULAR_SPEED_BOUND -> 400f / Profile.getCurrent().getUps();
            case DEFAULT_ANGULAR_DECAY -> 1 - (0.5f * Profile.getCurrent().getSizeScale() * Profile.getCurrent().getGameSpeed() / Profile.getCurrent().getUps());
            case MAX_SAFE_ROTATION -> 240f / Profile.getCurrent().getUps();
            case DEFAULT_SPEED -> Profile.getCurrent().getSizeScale() * Profile.getCurrent().getGameSpeed() * 30f / Profile.getCurrent().getUps();
            case BULLET_SPEED -> Profile.getCurrent().getSizeScale() * Profile.getCurrent().getGameSpeed() * 300f / Profile.getCurrent().getUps();
            case DEFAULT_DECELERATION ->
                    Profile.getCurrent().getSizeScale() * Profile.getCurrent().getGameSpeed() * (-10f) / (Profile.getCurrent().getUps() * Profile.getCurrent().getUps());
            case DECELERATION_DECAY -> 1 - (2f * Profile.getCurrent().getSizeScale() * Profile.getCurrent().getGameSpeed() / Profile.getCurrent().getUps());
            case DECELERATION_SENSITIVITY -> 0.000001f / Profile.getCurrent().getUps();
            case DAMPEN_FACTOR -> 1 + Profile.getCurrent().getGameSpeed() * 0.015f;
            case POSITION_UPDATE_INTERVAL -> TimeUnit.SECONDS.toNanos(1) / (Profile.getCurrent().getGameSpeed() * Profile.getCurrent().getUps());
        };
    }
}

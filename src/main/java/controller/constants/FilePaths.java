package controller.constants;

public enum FilePaths {
    UI_ELEMENTS_PATH, SLIDER_UI_PATH, ORBITRON_FONT_PATH, MANTINIA_FONT_PATH, GAME_THEME_PATH, MENU_THEME_PATH, ICON_PATH,
    COUNTDOWN_EFFECTS_PATH, SHOOT_SOUND_EFFECTS_PATH, DOWN_SOUND_EFFECTS_PATH, HIT_SOUND_EFFECTS_PATH, XP_SOUND_EFFECTS_PATH,
    SQUARANTINE_IMAGEPATH,PORTAL_IMAGEPATH, TRIGORATH_IMAGEPATH, EPSILON_IMAGEPATH, BULLET_IMAGEPATH, MENU_BACKGROUND_PATH, SAVE_FILES_FOLDER_PATH,
    GAME_BACKGROUND_PATH;

    public String getValue() {
        return switch (this) {

            case UI_ELEMENTS_PATH -> "./src/main/resources/ui elements/";
            case SLIDER_UI_PATH -> UI_ELEMENTS_PATH.getValue()+"SLIDER.png";
            case ICON_PATH -> UI_ELEMENTS_PATH.getValue()+"ICON.png";
            case MENU_BACKGROUND_PATH -> UI_ELEMENTS_PATH.getValue()+"MENU.png";
            case GAME_BACKGROUND_PATH -> UI_ELEMENTS_PATH.getValue()+"LOADING_BACKGROUND.png";
            case ORBITRON_FONT_PATH -> "./src/main/resources/fonts/Orbitron.ttf";
            case MANTINIA_FONT_PATH -> "./src/main/resources/fonts/Mantinia.otf";
            case GAME_THEME_PATH -> "./src/main/resources/effects/backgrounds/BG0.ogg";
            case MENU_THEME_PATH -> "./src/main/resources/effects/backgrounds/BG1.ogg";
            case COUNTDOWN_EFFECTS_PATH -> "./src/main/resources/effects/countdown/";
            case PORTAL_IMAGEPATH -> "./src/main/resources/character sprites/portal.png";
            case SHOOT_SOUND_EFFECTS_PATH -> "./src/main/resources/effects/shoot effects/";
            case DOWN_SOUND_EFFECTS_PATH -> "./src/main/resources/effects/down effects/";
            case HIT_SOUND_EFFECTS_PATH -> "./src/main/resources/effects/hit effects/";
            case XP_SOUND_EFFECTS_PATH -> "./src/main/resources/effects/xp effects/";
            case SQUARANTINE_IMAGEPATH -> "./src/main/resources/character sprites/squarantine.png";
            case TRIGORATH_IMAGEPATH -> "./src/main/resources/character sprites/trigorath.png";
            case BULLET_IMAGEPATH -> "./src/main/resources/character sprites/bullet.png";
            case EPSILON_IMAGEPATH -> "./src/main/resources/character sprites/epsilon.png";
            case SAVE_FILES_FOLDER_PATH -> "./src/main/Saves/";
        };
    }

}

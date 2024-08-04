package controller.constants;

public enum UIMessageConstants {
    GAME_SPEED_SLIDER_NAME, VOLUME_SLIDER_NAME, EXIT_MESSAGE, EXIT_TITLE, PORTAL_TITLE,PURCHASE_TITLE, SUCCESSFUL_PURCHASE_TITLE,
    UNSUCCESSFUL_PURCHASE_TITLE, ABILITY_ACTIVATION_CONFIRMATION, SUCCESSFUL_ABILITY_ACTIVATION_TITLE, UNSUCCESSFUL_ABILITY_ACTIVATION_TITLE, ACTIVATE_TITLE,
    TUTORIAL_MESSAGE, TUTORIAL_TITLE, EXIT_GAME_MESSAGE, EXIT_GAME_TITLE, INVALID_PROFILE_ID_MESSAGE, SAVE_FILE_EXTENSION,
    PROFILE_ID_REGEX, SIGNIN_SUCCESSFUL_MESSAGE, SIGNUP_SUCCESSFUL_MESSAGE,INVALID_PROFILE_ID_TITLE, SIGNIN_SUCCESSFUL_TITLE, SIGNUP_SUCCESSFUL_TITLE,
    SIGNIN_INSTRUCTIONS;

    public String getValue() {
        return switch (this) {
            case PORTAL_TITLE -> "Save or Continue";
            case GAME_SPEED_SLIDER_NAME -> "Game Speed";
            case VOLUME_SLIDER_NAME -> "Master Volume";
            case EXIT_MESSAGE -> "Are you sure to exit the game?";
            case EXIT_TITLE -> "Confirm Exit";
            case PURCHASE_TITLE -> "Confirm Purchase";
            case SUCCESSFUL_PURCHASE_TITLE -> "Skill Acquired";
            case UNSUCCESSFUL_PURCHASE_TITLE -> "Purchase Unsuccessful";
            case ABILITY_ACTIVATION_CONFIRMATION -> "Ability Activation";
            case SUCCESSFUL_ABILITY_ACTIVATION_TITLE -> "Ability Activated";
            case UNSUCCESSFUL_ABILITY_ACTIVATION_TITLE -> "Activation Unsuccessful";
            case ACTIVATE_TITLE -> "Confirm Activation";
            case TUTORIAL_MESSAGE -> """
                    Use WASD to move your character\s
                    Use LMB to shoot at your enemies\s
                    Each enemy has a certain amount of HP\s
                    Entities fade in color as they lose HP\s
                    Nearby collisions emit impact waves. Use them in your favor\s
                    Enemies become more and more as time passes. Be fast to survive\s
                    Use SHIFT to use your selected skill. Press Esc to pause the game
                    """;
            case TUTORIAL_TITLE -> "Game Tutorial";
            case EXIT_GAME_MESSAGE -> "Are you sure to exit the game?\nAll progress will be lost.";
            case EXIT_GAME_TITLE -> "Exit Game";
            case SAVE_FILE_EXTENSION -> ".json";
            case PROFILE_ID_REGEX -> "[@\\-#_A-Za-z0-9]*";
            case INVALID_PROFILE_ID_MESSAGE -> "Invalid profile name. You can only use letters,numbers and symbols \n \"@ , _ , - , #\" in your id. Your id shall have at least 4 characters";
            case SIGNIN_SUCCESSFUL_MESSAGE -> "You have successfully signed in your profile";
            case SIGNUP_SUCCESSFUL_MESSAGE -> "You have successfully created a new profile \n Use the same profile name to sign in every time";
            case INVALID_PROFILE_ID_TITLE -> "Invalid profile Id";
            case SIGNIN_SUCCESSFUL_TITLE -> "Sign in successful";
            case SIGNUP_SUCCESSFUL_TITLE -> "Sign up successful";
            case SIGNIN_INSTRUCTIONS -> "ENTER YOUR PROFILE ID TO SIGN IN/SIGN UP";
        };
    }
}

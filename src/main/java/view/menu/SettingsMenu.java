package view.menu;

import model.Profile;
import view.containers.ButtonB;
import view.containers.PanelB;
import view.containers.SliderB;

import java.util.List;

import static controller.AudioHandler.setAllVolumes;
import static controller.constants.DimensionConstants.SETTINGS_MENU_DIMENSION;
import static controller.constants.UIConstants.*;
import static controller.constants.UIMessageConstants.GAME_SPEED_SLIDER_NAME;
import static controller.constants.UIMessageConstants.VOLUME_SLIDER_NAME;

public class SettingsMenu extends PanelB {
    private static SettingsMenu INSTANCE;

    private SettingsMenu() {
        super(SETTINGS_MENU_DIMENSION.getValue());
        SliderB gameSpeedSlider = new SliderB(this, MIN_GAME_SPEED.getValue(), MAX_GAME_SPEED.getValue(), Profile.getCurrent().getGameSpeed(), GAME_SPEED_SLIDER_NAME.getValue());
        gameSpeedSlider.addChangeListener(e -> Profile.getCurrent().setGameSpeed(gameSpeedSlider.getPreciseValue()));

        SliderB volumeSlider = new SliderB(this, MIN_VOLUME.getValue(), MAX_VOLUME.getValue(), Profile.getCurrent().getSoundScale(), VOLUME_SLIDER_NAME.getValue());
        volumeSlider.addChangeListener(e -> {
            Profile.getCurrent().setSoundScale(volumeSlider.getPreciseValue());
            setAllVolumes();
        });

        ButtonB back = new ButtonB(ButtonB.ButtonType.SMALL_MENU_BUTTON, "BACK", (int) BACK_BUTTON_WIDTH.getValue(), BACK_BUTTON_FONT_SCALE.getValue(), false);
        back.addActionListener(e -> {
                SettingsMenu.getINSTANCE().togglePanel();
                MainMenu.getINSTANCE().togglePanel();
        });

        getConstraints().gridwidth = 1;
        horizontalBulkAdd(List.of(gameSpeedSlider.getLabelButton(), gameSpeedSlider));
        getConstraints().gridy++;
        getConstraints().gridx = 0;
        horizontalBulkAdd(List.of(volumeSlider.getLabelButton(), volumeSlider));
        getConstraints().gridwidth = 2;
        add(back, false, true);
    }

    public static SettingsMenu getINSTANCE() {
        if (INSTANCE == null) INSTANCE = new SettingsMenu();
        return INSTANCE;
    }
}

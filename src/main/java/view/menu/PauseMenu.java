package view.menu;

import controller.GameLoop;
import controller.constants.DefaultMethods;
import model.Profile;
import model.WaveManager;
import model.characters.EpsilonModel;
import view.containers.ButtonB;
import view.containers.PanelB;
import view.containers.SliderB;
import view.containers.TopElement;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static controller.AudioHandler.setAllVolumes;
import static controller.UserInterfaceController.*;
import static controller.constants.DefaultMethods.ABILITY_ACTIVATE_MESSAGE;
import static controller.constants.DimensionConstants.PAUSE_MENU_DIMENSION;
import static controller.constants.UIConstants.*;
import static controller.constants.UIMessageConstants.*;
import static view.containers.GlassFrame.getGlassFrame;

public class PauseMenu extends PanelB implements TopElement {
    static CopyOnWriteArrayList<Component> abilities = new CopyOnWriteArrayList<>();
    private static PauseMenu INSTANCE;
    private static boolean pauseAccess=true;

    private PauseMenu() {
        super(PAUSE_MENU_DIMENSION.getValue());
        Profile.getCurrent().setPaused(true);
        ButtonB xp = new ButtonB(ButtonB.ButtonType.SMALL_MENU_BUTTON, Profile.getCurrent().getCurrentGameXP() + " XP",
                (int) BACK_BUTTON_WIDTH.getValue(), BACK_BUTTON_FONT_SCALE.getValue(), false, true);
        xp.toggleBold();
        ButtonB resume = new ButtonB(ButtonB.ButtonType.SMALL_MENU_BUTTON, "RESUME", (int) BACK_BUTTON_WIDTH.getValue(), BACK_BUTTON_FONT_SCALE.getValue(), false);
        resume.addActionListener(e -> {
            PauseMenu.getINSTANCE().togglePanel();
            EpsilonModel.getINSTANCE().activateMovement();
            Profile.getCurrent().setPaused(false);
        });
        SliderB volumeSlider = new SliderB(this, MIN_VOLUME.getValue(), MAX_VOLUME.getValue(), Profile.getCurrent().getSoundScale(), VOLUME_SLIDER_NAME.getValue());
        volumeSlider.addChangeListener(e -> {
            Profile.getCurrent().setSoundScale(volumeSlider.getPreciseValue());
            setAllVolumes();
        });
        setupAbilityButtons();
        ButtonB exit = new ButtonB(ButtonB.ButtonType.SMALL_MENU_BUTTON, "EXIT", (int) BACK_BUTTON_WIDTH.getValue(), BACK_BUTTON_FONT_SCALE.getValue(), false);
        exit.addActionListener(e -> {
            int action = JOptionPane.showConfirmDialog(getINSTANCE(), EXIT_GAME_MESSAGE.getValue(), EXIT_GAME_TITLE.getValue()
                    , JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (action == JOptionPane.YES_OPTION) {
                GameLoop.setPR(0);
                Profile.getCurrent().setCurrentGameXP(0);
                exitGame();
                Profile.getCurrent().setPaused(true);
                PauseMenu.getINSTANCE().togglePanel(true);
                MainMenu.flushINSTANCE();
                MainMenu.getINSTANCE().togglePanel();
            }
        });
        
        getConstraints().gridwidth = 2;
        add(xp, false, true);
        add(resume, false, true);
        getConstraints().gridy++;
        bulkAdd(abilities, 3);
        getConstraints().gridx = 0;
        getConstraints().gridy++;
        getConstraints().gridwidth = 1;
        horizontalBulkAdd(java.util.List.of(volumeSlider.getLabelButton(), volumeSlider));
        getConstraints().gridy++;
        getConstraints().gridx = 0;
        getConstraints().gridwidth = 2;
        add(exit, false, true);
    }
    public static void setupAbilityButtons(){
        ConcurrentMap<String, Integer> abilitiesData = getAbilitiesData();
        for (Map.Entry<String,Integer> abilityData : abilitiesData.entrySet()) {
            JButton abilityButton=new ButtonB(ButtonB.ButtonType.UNACQUIRED_SKILL, abilityData.getKey(), (int) SKILL_BUTTON_WIDTH.getValue(), ABILITY_FONT_SIZE_SCALE.getValue(), false, false);
            abilityButton.addActionListener(e -> {
                int action = JOptionPane.showConfirmDialog(getINSTANCE(), ABILITY_ACTIVATE_MESSAGE(abilityData.getValue()), ABILITY_ACTIVATION_CONFIRMATION.getValue(), JOptionPane.YES_NO_OPTION);
                if (action == JOptionPane.YES_OPTION) {
                    if (activateAbility(abilityData.getKey())) {
                        int confirmAction = JOptionPane.showOptionDialog(getINSTANCE(), DefaultMethods.SUCCESSFUL_ACTIVATE_MESSAGE(abilityData.getKey()),
                                SUCCESSFUL_ABILITY_ACTIVATION_TITLE.getValue(), JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new Object[]{}, null);
                        if (confirmAction == JOptionPane.CLOSED_OPTION) {
                            PauseMenu.getINSTANCE().togglePanel();
                            EpsilonModel.getINSTANCE().activateMovement();
                            Profile.getCurrent().setPaused(false);
                        }
                    } else {
                        JOptionPane.showOptionDialog(getINSTANCE(), DefaultMethods.UNSUCCESSFUL_ACTIVATE_MESSAGE(abilityData.getKey()), UNSUCCESSFUL_PURCHASE_TITLE.getValue(),
                                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new Object[]{}, null);
                    }
                }
            });
            abilities.add(abilityButton);
        }
    }

    public static PauseMenu getINSTANCE() {
        if (INSTANCE==null || !INSTANCE.isVisible()) {
            INSTANCE=new PauseMenu();
        }
        return INSTANCE;
    }

    public static void setPauseAccess(boolean pauseAccess) {
        PauseMenu.pauseAccess = pauseAccess;
    }

    @Override
    public void togglePanel() {
        EpsilonModel.getINSTANCE().deactivateMovement();
        togglePanel(false);
    }
    public void togglePanel(boolean exit) {
        if (!exit && pauseAccess) {
            super.togglePanel();
            toggleGameRunning();
        }
    }

    @Override
    public void repaint() {
        super.repaint();
        pinOnTop();
    }

    @Override
    public JFrame getFrame() {
        return getGlassFrame();
    }
}

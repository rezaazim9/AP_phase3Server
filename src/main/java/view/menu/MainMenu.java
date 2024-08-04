package view.menu;

import model.SpawnThread;
import model.Profile;
import view.containers.ButtonB;
import view.containers.PanelB;

import javax.swing.*;

import java.util.List;

import static controller.UserInterfaceController.*;
import static controller.constants.DimensionConstants.MAIN_MENU_DIMENSION;
import static controller.constants.UIConstants.MENU_BUTTON_WIDTH;
import static controller.constants.UIMessageConstants.*;

public final class MainMenu extends PanelB {

    private static MainMenu INSTANCE;
    public static SpawnThread spawn = new SpawnThread();


    private MainMenu() {
        super(MAIN_MENU_DIMENSION.getValue());

        playMenuTheme();
        ButtonB start = new ButtonB(ButtonB.ButtonType.MENU_BUTTON, "START", (int) MENU_BUTTON_WIDTH.getValue(), true);
        start.addActionListener(e -> {
            MainMenu.getINSTANCE().togglePanel();
            toggleGameRunning();
            spawn = new SpawnThread();
            Profile.getCurrent().setPaused(false);
            spawn.start();
        });
        ButtonB settings = new ButtonB(ButtonB.ButtonType.MENU_BUTTON, "SETTINGS", (int) MENU_BUTTON_WIDTH.getValue(), true);
        settings.addActionListener(e -> {
            MainMenu.getINSTANCE().togglePanel();
            SettingsMenu.getINSTANCE().togglePanel();
        });
        ButtonB skillTree = new ButtonB(ButtonB.ButtonType.MENU_BUTTON, "SKILL TREE", (int) MENU_BUTTON_WIDTH.getValue(), true);
        skillTree.addActionListener(e -> {
            MainMenu.getINSTANCE().togglePanel();
            SkillTree.getINSTANCE(true).togglePanel();
        });
        ButtonB tutorial = new ButtonB(ButtonB.ButtonType.MENU_BUTTON, "TUTORIAL", (int) MENU_BUTTON_WIDTH.getValue(), true);
        tutorial.addActionListener(e -> JOptionPane.showMessageDialog(getINSTANCE(), TUTORIAL_MESSAGE.getValue(), TUTORIAL_TITLE.getValue(), JOptionPane.PLAIN_MESSAGE));
        ButtonB exit = new ButtonB(ButtonB.ButtonType.MENU_BUTTON, "EXIT", (int) MENU_BUTTON_WIDTH.getValue(), true);
        exit.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(getINSTANCE(), EXIT_MESSAGE.getValue(), EXIT_TITLE.getValue(), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                System.exit(0);
        });
        verticalBulkAdd(List.of(start, settings, skillTree, tutorial, exit));
    }

    public static void flushINSTANCE() {
        PauseMenu.getINSTANCE().setVisible(false);
        INSTANCE = null;
    }

    public static MainMenu getINSTANCE() {
        if (INSTANCE == null) INSTANCE = new MainMenu();
        return INSTANCE;
    }

}

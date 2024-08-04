package view.menu;

import model.JsonOperator;
import model.entities.Skill;
import view.containers.ButtonB;
import view.containers.PanelB;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;

import static controller.constants.DimensionConstants.LOGIN_PAGE_DIMENSION;
import static controller.constants.UIConstants.*;
import static controller.constants.UIMessageConstants.*;
import static model.JsonOperator.loadState;

public class LoginPage extends PanelB {
    private static LoginPage INSTANCE;
    private LoginPage() {
        super(LOGIN_PAGE_DIMENSION.getValue());
        ButtonB instructions=new ButtonB(ButtonB.ButtonType.SMALL_MENU_BUTTON,SIGNIN_INSTRUCTIONS.getValue(), (int) MENU_BUTTON_WIDTH.getValue(),LOGIN_PAGE_FONT_SIZE.getValue(), false, true);

        JTextField idField=new JTextField();
        idField.setFont(ORBITRON_FONT.deriveFont(LOGIN_PAGE_ID_FIELD_FONT_SIZE.getValue()));
        idField.setHorizontalAlignment(SwingConstants.CENTER);
        idField.setBackground(SCI_FI_DARK_BLUE);
        idField.setForeground(Color.WHITE);
        idField.setBorder(null);

        ButtonB loginButton=new ButtonB(ButtonB.ButtonType.SMALL_MENU_BUTTON,"ENTER YOUR PROFILE", (int) MENU_BUTTON_WIDTH.getValue(),LOGIN_PAGE_FONT_SIZE.getValue(),false);
        loginButton.addActionListener(e -> {
            String entry=idField.getText();
            if (!entry.matches(PROFILE_ID_REGEX.getValue()) || entry.length() < MINIMUM_PROFILE_ID_LENGTH.getValue()) {
                JOptionPane.showOptionDialog(getINSTANCE(), INVALID_PROFILE_ID_MESSAGE.getValue(), INVALID_PROFILE_ID_TITLE.getValue(),
                        JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new Object[]{}, null);
            }
            else {
                boolean login;
                try {login=loadState(entry);}
                catch (IOException ex) {throw new UnsupportedOperationException("Failed to check login");}
                if (login){
                    JOptionPane.showOptionDialog(getINSTANCE(), SIGNIN_SUCCESSFUL_MESSAGE.getValue(), SIGNIN_SUCCESSFUL_TITLE.getValue(),
                            JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new Object[]{}, null);
                }
                else {
                    JOptionPane.showOptionDialog(getINSTANCE(), SIGNUP_SUCCESSFUL_MESSAGE.getValue(), SIGNUP_SUCCESSFUL_TITLE.getValue(),
                            JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new Object[]{}, null);
                }
                JsonOperator.JsonInitiate();
                Skill.initializeSkills();
                LoginPage.getINSTANCE().togglePanel();
                MainMenu.getINSTANCE().togglePanel();
            }
        });
        ButtonB exit = new ButtonB(ButtonB.ButtonType.SMALL_MENU_BUTTON, "EXIT", (int) MENU_BUTTON_WIDTH.getValue(), false);
        exit.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(getINSTANCE(), EXIT_MESSAGE.getValue(), EXIT_TITLE.getValue(), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) System.exit(0);
        });
        verticalBulkAdd(List.of(instructions,idField,loginButton,exit));
        requestFocus();
    }
    public static LoginPage getINSTANCE(){
        if (INSTANCE==null) INSTANCE=new LoginPage();
        return INSTANCE;
    }
}

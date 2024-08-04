package view.menu;

import controller.constants.DefaultMethods;
import model.Profile;
import org.apache.commons.lang3.tuple.Triple;
import view.containers.ButtonB;
import view.containers.PanelB;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static controller.UserInterfaceController.*;
import static controller.constants.DimensionConstants.SKILL_TREE_DIMENSION;
import static controller.constants.UIConstants.*;
import static controller.constants.UIMessageConstants.*;

public class SkillTree extends PanelB {
    private static SkillTree INSTANCE;
    private static final List<Component> skillTypeButtons = new CopyOnWriteArrayList<>();
    private static final List<CopyOnWriteArrayList<Component>> skillButtons = new CopyOnWriteArrayList<>();
    private static ConcurrentMap<String, List<Triple<String, Integer, Boolean>>> skillTypesData = getSkillTypesData();

    private SkillTree() {
        super(SKILL_TREE_DIMENSION.getValue());
        ButtonB xp = new ButtonB(ButtonB.ButtonType.SMALL_MENU_BUTTON, Profile.getCurrent().getTotalXP() + " XP",
                (int) BACK_BUTTON_WIDTH.getValue(), BACK_BUTTON_FONT_SCALE.getValue(), false, true);
        xp.toggleBold();
        updateSkillTree();
        ButtonB back = new ButtonB(ButtonB.ButtonType.SMALL_MENU_BUTTON, "BACK", (int) BACK_BUTTON_WIDTH.getValue(), BACK_BUTTON_FONT_SCALE.getValue(), false);
        back.addActionListener(e -> {
            SkillTree.getINSTANCE(false).togglePanel();
            MainMenu.getINSTANCE().togglePanel();
        });

        getConstraints().gridwidth = skillTypeButtons.size();
        add(xp, false, true);
        getConstraints().gridx = 0;
        getConstraints().gridy++;
        getConstraints().gridwidth = 1;
        horizontalBulkAdd(skillTypeButtons);
        getConstraints().gridy++;
        for (CopyOnWriteArrayList<Component> levelSkills : skillButtons) {
            getConstraints().gridy++;
            getConstraints().gridx = 0;
            horizontalBulkAdd(levelSkills);
        }
        getConstraints().gridx = 0;
        getConstraints().gridwidth = skillTypeButtons.size();
        add(back, false, true);
    }

    public static void updateSkillTree() {
        skillTypeButtons.clear();
        skillButtons.clear();
        skillTypesData = getSkillTypesData();
        setupSkillTypeButtons();

        int level = 0;
        boolean finished = false;
        while (!finished) {
            finished = true;
            CopyOnWriteArrayList<Component> levelSkills = new CopyOnWriteArrayList<>();
            for (Map.Entry<String,List<Triple<String,Integer,Boolean>>> skillTypeInfo : skillTypesData.entrySet()) {
                if (skillTypeInfo.getValue().size() > level) {
                    finished = false;
                    Triple<String,Integer,Boolean> skillInfo=skillTypeInfo.getValue().get(level);
                    String name = skillInfo.getLeft();
                    ButtonB.ButtonType type=getButtonType(name,skillInfo.getRight());
                    ButtonB skill = new ButtonB(type, name, (int) SKILL_BUTTON_WIDTH.getValue(), SKILL_FONT_SIZE_SCALE.getValue(), false, type== ButtonB.ButtonType.ACTIVE_SKILL);
                    skill.addActionListener(e -> setupSkillButtonAction(type,name,skillInfo.getMiddle()));
                    levelSkills.add(skill);
                }
                else levelSkills.add(null);
            }
            if (!finished) {
                skillButtons.add(levelSkills);
                level++;
            }
        }
    }

    private static ButtonB.ButtonType getButtonType(String name,boolean acquired){
        String activeSkillName = getActiveSkill();
        boolean isActive=name.equals(activeSkillName);
        ButtonB.ButtonType type = acquired ? ButtonB.ButtonType.ACQUIRED_SKILL : ButtonB.ButtonType.UNACQUIRED_SKILL;
        if (isActive) type = ButtonB.ButtonType.ACTIVE_SKILL;
        return type;
    }
    private static void setupSkillTypeButtons() {
        for (Map.Entry<String, List<Triple<String, Integer, Boolean>>> skillTypeData : skillTypesData.entrySet()) {
            int numberOfAcquiredSkills=0;
            for (Triple<String,Integer,Boolean> skillData: skillTypeData.getValue()) if (skillData.getRight()) numberOfAcquiredSkills++;
            ButtonB.ButtonType type = switch (numberOfAcquiredSkills){
                case 0 -> ButtonB.ButtonType.TYPE0;
                case 1 -> ButtonB.ButtonType.TYPE1;
                case 2 -> ButtonB.ButtonType.TYPE2;
                case 3 -> ButtonB.ButtonType.TYPE3;
                default -> null;
            };
            ButtonB skillTypeButton = new ButtonB(type, skillTypeData.getKey(), (int) SKILL_BUTTON_WIDTH.getValue(), SKILL_FONT_SIZE_SCALE.getValue(), false, true);
            skillTypeButton.setIconTextGap((int) SKILL_TEXT_OFFSET.getValue());
            skillTypeButton.setVerticalTextPosition(SwingConstants.TOP);
            skillTypeButtons.add(skillTypeButton);
        }
    }
    private static void setupSkillButtonAction(ButtonB.ButtonType type, String name, int cost){
        if (type == ButtonB.ButtonType.UNACQUIRED_SKILL) unacquiredButtonAction(name,cost);
        else if (type == ButtonB.ButtonType.ACQUIRED_SKILL) acquiredButtonAction(name);
    }
    private static void unacquiredButtonAction(String name,int cost){
        int action = JOptionPane.showConfirmDialog(getINSTANCE(false), DefaultMethods.PURCHASE_MESSAGE(cost), PURCHASE_TITLE.getValue(), JOptionPane.YES_NO_OPTION);
        if (action == JOptionPane.YES_OPTION) {
            if (purchaseSkill(name)) {
                int actionConfirm = JOptionPane.showOptionDialog(getINSTANCE(false),
                        DefaultMethods.SUCCESSFUL_PURCHASE_MESSAGE(name), SUCCESSFUL_PURCHASE_TITLE.getValue(),
                        JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new Object[]{}, null);
                if (actionConfirm == JOptionPane.CLOSED_OPTION) getINSTANCE(true).togglePanel();
            } else {
                int actionConfirm = JOptionPane.showOptionDialog(getINSTANCE(false), DefaultMethods.UNSUCCESSFUL_PURCHASE_MESSAGE(name), UNSUCCESSFUL_PURCHASE_TITLE.getValue(),
                        JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new Object[]{}, null);
                if (actionConfirm == JOptionPane.CLOSED_OPTION) getINSTANCE(true).togglePanel();
            }
        }
    }
    private static void acquiredButtonAction(String name){
        int action = JOptionPane.showConfirmDialog(getINSTANCE(false), DefaultMethods.SKILL_ACTIVATE_MESSAGE(name), ACTIVATE_TITLE.getValue(), JOptionPane.YES_NO_OPTION);
        if (action == JOptionPane.YES_OPTION) {
            setActiveSkill(name);
            getINSTANCE(true).togglePanel();
        }
    }

    public static SkillTree getINSTANCE(boolean renew) {
        if (renew && INSTANCE!=null) {
            INSTANCE.setVisible(false);
            INSTANCE = new SkillTree();
        }
        else if (INSTANCE==null) INSTANCE=new SkillTree();
        return INSTANCE;
    }
}

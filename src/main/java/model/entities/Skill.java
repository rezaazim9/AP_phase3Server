package model.entities;

import model.Profile;
import model.characters.EpsilonModel;

import javax.swing.*;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import static controller.UserInterfaceController.*;
import static controller.constants.AbilityConstants.*;
import static controller.constants.EntityConstants.*;
import static model.Utils.*;

public enum Skill {
    ARES, ASTRAPE, CERBERUS, ACESO, MELAMPUS, CHIRON, PROTEUS, EMPUSA, DOLUS;

    private static Skill activeSkill = null;
    private boolean acquired = false;
    private long lastSkillTime = 0;

    public static void initializeSkills() {
        setActiveSkill(findSkill(Profile.getCurrent().getActiveSkillSaveName()));
        CopyOnWriteArrayList<Skill> acquiredSkillSave = new CopyOnWriteArrayList<>();
        for (String skillName : Profile.getCurrent().getAcquiredSkillsNames())
            acquiredSkillSave.add(findSkill(skillName));
        for (Skill skill : acquiredSkillSave) skill.setAcquired(true);
    }

    public static Skill getActiveSkill() {
        return activeSkill;
    }

    public static void setActiveSkill(Skill activeSkill) {
        Skill.activeSkill = activeSkill;
    }

    public String getName() {
        return "WRIT OF " + name();
    }

    public int getCost() {
        return switch (this) {

            case ARES -> 750;
            case ASTRAPE -> 1000;
            case CERBERUS -> 1500;
            case ACESO -> 500;
            case MELAMPUS -> 750;
            case CHIRON -> 900;
            case PROTEUS -> 1000;
            case EMPUSA -> 750;
            case DOLUS -> 1500;
        };
    }

    public SkillType getType() {
        return switch (this) {

            case ARES -> SkillType.ATTACK;
            case ASTRAPE -> SkillType.ATTACK;
            case CERBERUS -> SkillType.ATTACK;
            case ACESO -> SkillType.GUARD;
            case MELAMPUS -> SkillType.GUARD;
            case CHIRON -> SkillType.GUARD;
            case PROTEUS -> SkillType.POLYMORPHIA;
            case EMPUSA -> SkillType.POLYMORPHIA;
            case DOLUS -> SkillType.POLYMORPHIA;
        };
    }

    public ActionListener getAction() {
        return switch (this) {

            case ARES -> e -> {
                EpsilonModel.getINSTANCE().getDamageSize().put(AttackTypes.MELEE, (int) (Profile.getCurrent().getEpsilonMeleeDamage() + WRIT_OF_ARES_BUFF_AMOUNT.getValue()));
                EpsilonModel.getINSTANCE().getDamageSize().put(AttackTypes.RANGED, (int) (Profile.getCurrent().getEpsilonRangedDamage() + WRIT_OF_ARES_BUFF_AMOUNT.getValue()));
            };
            case ASTRAPE -> e -> {
                EpsilonModel.getINSTANCE().getDamageSize().put(AttackTypes.COLLISION, (int) (Profile.getCurrent().getEpsilonCollisionDamage() + WRIT_OF_ASTRAPE_BUFF_AMOUNT.getValue()));
            };
            case CERBERUS -> e -> {
                // TODO: Implement Cerberus skill
            };
            case ACESO -> e -> {
                Timer healthTimer = new Timer((int) WRIT_OF_ACESO_HEALING_FREQUENCY.getValue(), null);
                healthTimer.addActionListener(e1 -> {
                    if (isGameRunning())
                        EpsilonModel.getINSTANCE().addHealth((int) WRIT_OF_ACESO_HEALING_AMOUNT.getValue());
                    if (!isGameOn()) healthTimer.stop();
                });
                healthTimer.start();
            };
            case MELAMPUS -> e -> {
                Profile.getCurrent().setEpsilonMeleeDamageProbability(Profile.getCurrent().getEpsilonMeleeDamageProbability() - WRIT_OF_MELAMPUS_BUFF_AMOUNT.getValue());
            };
            case CHIRON -> e -> {
                Profile.getCurrent().setEpsilonHealingAmount((int) WRIT_OF_CHIRON_BUFF_AMOUNT.getValue());
            };
            case PROTEUS -> e -> EpsilonModel.getINSTANCE().addVertex();
            case EMPUSA -> e -> {
                // TODO
            };
            case DOLUS -> e -> {
                if (Profile.getCurrent().getRandomAcquiredSkillsNames().isEmpty()) {
                    Random random = new Random();
                    if (Profile.getCurrent().getAcquiredSkillsNames().size() > 2) {
                        int randomIndex = random.nextInt(Profile.getCurrent().getAcquiredSkillsNames().size());
                        String randomSkill = Profile.getCurrent().getAcquiredSkillsNames().get(randomIndex);
                        Profile.getCurrent().getRandomAcquiredSkillsNames().add(randomSkill);
                        int randomIndex2 = random.nextInt(Profile.getCurrent().getAcquiredSkillsNames().size());
                        while (randomIndex2 == randomIndex)
                            randomIndex2 = random.nextInt(Profile.getCurrent().getAcquiredSkillsNames().size());
                        String randomSkill2 = Profile.getCurrent().getAcquiredSkillsNames().get(randomIndex2);
                        Profile.getCurrent().getRandomAcquiredSkillsNames().add(randomSkill2);
                    }
                }
                for (String skill : Profile.getCurrent().getRandomAcquiredSkillsNames()) {
                    findSkill(skill).fire();
                }
            };
        };
    }

    public void fire() {
        long now = System.nanoTime();
        if (now - getLastSkillTime() >= TimeUnit.MINUTES.toNanos(SKILL_COOLDOWN_IN_MINUTES.getValue())) {
            getAction().actionPerformed(new ActionEvent(new Object(), ActionEvent.ACTION_PERFORMED, null));
            setLastSkillTime(now);
        }
    }

    public boolean isAcquired() {
        return acquired;
    }

    public void setAcquired(boolean acquired) {
        this.acquired = acquired;
    }

    public long getLastSkillTime() {
        return lastSkillTime;
    }

    public void setLastSkillTime(long lastSkillTime) {
        this.lastSkillTime = lastSkillTime;
    }

    public enum SkillType {
        ATTACK, GUARD, POLYMORPHIA
    }
}

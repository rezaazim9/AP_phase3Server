package model.entities;

import model.Profile;
import model.characters.EpsilonModel;

import javax.swing.*;

import controller.GameLoop;

import java.awt.event.ActionListener;

import static controller.constants.AbilityConstants.*;
import static controller.constants.EntityConstants.EPSILON_SHOOTING_RAPIDITY;
import static model.collision.Collision.emitImpactWave;

public enum Ability {
    HEPHAESTUS, ATHENA, APOLLO, DEIMOS, HYPNOS, PHONOI;

    public String getName() {
        return switch (this) {

            case HEPHAESTUS -> "O' HEPHAESTUS, BANISH";
            case ATHENA -> "O' ATHENA, EMPOWER";
            case APOLLO -> "O' APOLLO, HEAL";
            case DEIMOS -> "O' DEIMOS, DISMAY";
            case HYPNOS -> "O' HYPNOS, SLUMBER";
            case PHONOI -> "O' PHONOI, SLAUGHTER";
        };
    }

    public int getCost() {
        return switch (this) {

            case HEPHAESTUS -> 100;
            case ATHENA -> 75;
            case APOLLO -> 50;
            case DEIMOS -> 120;
            case HYPNOS -> 150;
            case PHONOI -> 200;
        };
    }

    public ActionListener getAction() {
        Profile.getCurrent().setPaused(false);
        return switch (this) {

            case HEPHAESTUS -> e -> {
                if (!Profile.getCurrent().isPaused()) {
                    emitImpactWave(EpsilonModel.getINSTANCE().getAnchor(), HEPHAESTUS_ABILITY_WAVE_POWER.getValue());
                }
            };
            case ATHENA -> e -> {
                if (!Profile.getCurrent().isPaused()) {
                    EpsilonModel.getINSTANCE().setShootingRapidity((int) ATHENA_ABILITY_SHOOTING_RAPIDITY.getValue());
                    Timer timer=new Timer((int) ATHENA_ABILITY_TIME.getValue(), e1 -> EpsilonModel.getINSTANCE().setShootingRapidity(EPSILON_SHOOTING_RAPIDITY.getValue()));
                    timer.setRepeats(false);
                    timer.start();
                }
            };
            case APOLLO -> e -> {
                if (!Profile.getCurrent().isPaused()) {
                    EpsilonModel.getINSTANCE().addHealth((int) APOLLO_ABILITY_HEALING_AMOUNT.getValue());
                }
            };
            case DEIMOS -> e -> {
                if (!Profile.getCurrent().isPaused()) {
                    // TODO
                }
            };
            case HYPNOS -> e -> {
                if (!Profile.getCurrent().isPaused()) {
                    Profile.getCurrent().setPaused(true);
                    GameLoop.getINSTANCE().getWaveManager().stopEnemies();
                    Timer timer=new Timer((int) HYPNOS_ABILITY_TIME.getValue(), e1 -> {
                        Profile.getCurrent().setPaused(false);
                        GameLoop.getINSTANCE().getWaveManager().releaseEnemies();

                    });
                    timer.setRepeats(false);
                    timer.start();
                }
            };
            case PHONOI -> e -> {
                if (!Profile.getCurrent().isPaused() && !Profile.getCurrent().isInCooldown()) {
                    Profile.getCurrent().setInCooldown(true);
                    EpsilonModel.getINSTANCE().getDamageSize().put(AttackTypes.RANGED, (int) PHONOI_ABILITY_RANGED_DAMAGE.getValue());

                    Timer timer=new Timer((int) PHONOI_ABILITY_TIME.getValue(), e1 -> {
                        Profile.getCurrent().setInCooldown(false);
                    });
                    timer.setRepeats(false);
                    timer.start();
                }
            };
        };
    }
}

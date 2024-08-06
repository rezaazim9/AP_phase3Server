package model.entities;

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

}

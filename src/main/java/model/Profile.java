package model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import model.entities.Skill;

import static view.menu.MainMenu.spawn;

import java.awt.event.KeyEvent;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
public class Profile {
    private static Profile current = new Profile("");
    public static final int UP_KEYCODE = KeyEvent.VK_W;
    public static final int DOWN_KEYCODE = KeyEvent.VK_S;
    public static final int LEFT_KEYCODE = KeyEvent.VK_A;
    public static final int RIGHT_KEYCODE = KeyEvent.VK_D;
    public static final int SKILL_KEYCODE = KeyEvent.VK_SPACE;
    public static final int PAUSE_KEYCODE = KeyEvent.VK_ESCAPE;
    private String profileId;
    private int ups = 800;
    private int wave=0;
    private int fps = 80;
    private float soundScale = 6;
    private float sizeScale = 0.75f;
    private float gameSpeed = 1.8f;
    private int epsilonShootingRapidity = 1;
    private String activeSkillSaveName = "";
    private List<String> acquiredSkillsNames = new CopyOnWriteArrayList<>();
    private List<String> randomAquiredSkillNames = new CopyOnWriteArrayList<>();
    private int totalXP = 800;
    private int currentGameXP = 1300;
    private int epsilonMeleeDamage = 10;
    private int epsilonRangedDamage = 5;
    private int epsilonCollisionDamage = 0;
    private float epsilonMeleeDamageProbability = 1;
    private int epsilonHealingAmount = 0;
    private boolean isPaused = false;
    private boolean isInCooldown = false;

    @JsonCreator
    public Profile(@JsonProperty("profileId") String profileId,@JsonProperty("wave")int wave, @JsonProperty("ups") int ups,
                   @JsonProperty("fps") int fps, @JsonProperty("soundScale") float soundScale,
                   @JsonProperty("sizeScale") float sizeScale, @JsonProperty("gameSpeed") float gameSpeed, 
                   @JsonProperty("epsilonShootingRapidity") int epsilonShootingRapidity, @JsonProperty("activeSkillSaveName") String activeSkillSaveName,
                   @JsonProperty("acquiredSkillsNames") List<String> acquiredSkillsNames, @JsonProperty("randomAquiredSkillNames") List<String> randomAquiredSkillNames,
                   @JsonProperty("totalXP") int totalXP, @JsonProperty("currentGameXP") int currentGameXP, @JsonProperty("epsilonMeleeDamage") int epsilonMeleeDamage,
                   @JsonProperty("epsilonRangedDamage") int epsilonRangedDamage, @JsonProperty("epsilonCollisionDamage") int epsilonCollisionDamage,
                   @JsonProperty("epsilonMeleeDamageProbability") float epsilonMeleeDamageProbability, @JsonProperty("epsilonHealingAmount") int epsilonHealingAmount,
                   @JsonProperty("isPaused") boolean isPaused, @JsonProperty("isInCooldown") boolean isInCooldown) {
        this.profileId = profileId;
        this. epsilonHealingAmount=epsilonHealingAmount;
        this.epsilonMeleeDamageProbability=epsilonMeleeDamageProbability;
        this.ups = ups;
        this.fps = fps;
        this.epsilonMeleeDamage = epsilonMeleeDamage;
        this.epsilonRangedDamage = epsilonRangedDamage;
        this.epsilonCollisionDamage=epsilonCollisionDamage;
        this.soundScale = soundScale;
        this.sizeScale = sizeScale;
        this.gameSpeed = gameSpeed;
        this.epsilonShootingRapidity = epsilonShootingRapidity;
        this.activeSkillSaveName = activeSkillSaveName;
        this.acquiredSkillsNames = acquiredSkillsNames;
        this.randomAquiredSkillNames = randomAquiredSkillNames;
        this.totalXP = totalXP;
        this.wave=wave;
        this.currentGameXP = currentGameXP;
        this.isPaused = isPaused;
        this.isInCooldown = isInCooldown;
    }

    public Profile(String profileId) {
        this.profileId = String.valueOf(profileId.hashCode());
    }

    public static Profile getCurrent() {
        return current;
    }

    public static void setCurrent(Profile current) {
        Profile.current = current;
    }

    public void updateINSTANCE() {
        this.setActiveSkillSaveName(Skill.getActiveSkill() != null ? Skill.getActiveSkill().getName() : "");
        this.getAcquiredSkillsNames().clear();
    }

    public void saveXP() {
        setTotalXP(getTotalXP() + Profile.getCurrent().getCurrentGameXP());
        setCurrentGameXP(0);
    }

    public String getProfileId() {
        return String.valueOf(profileId);
    }


    public int getUps() {
        return ups;
    }

    public int getFps() {
        return fps;
    }

    public int getEpsilonMeleeDamage() {
        return epsilonMeleeDamage;
    }

    public void setEpsilonMeleeDamage(int epsilonMeleeDamage) {
        this.epsilonMeleeDamage = epsilonMeleeDamage;
    }

    public int getEpsilonRangedDamage() {
        return epsilonRangedDamage;
    }

    public void setEpsilonRangedDamage(int epsilonRangedDamage) {
        this.epsilonRangedDamage = epsilonRangedDamage;
    }

    public int getEpsilonCollisionDamage() {
        return epsilonCollisionDamage;
    }

    public int getWave() {
        return wave;
    }

    public void setWave(int wave) {
        this.wave = wave;
    }

    public void setEpsilonCollisionDamage(int epsilonCollisionDamage) {
        this.epsilonCollisionDamage = epsilonCollisionDamage;
    }

    public float getEpsilonMeleeDamageProbability() {
        return epsilonMeleeDamageProbability;
    }

    public void setEpsilonMeleeDamageProbability(float epsilonMeleeDamageProbability) {
        this.epsilonMeleeDamageProbability = epsilonMeleeDamageProbability;
    }

    public int getEpsilonHealingAmount() {
        return epsilonHealingAmount;
    }

    public void setEpsilonHealingAmount(int epsilonHealingAmount) {
        this.epsilonHealingAmount = epsilonHealingAmount;
    }

    public float getSoundScale() {
        return soundScale;
    }

    public void setSoundScale(float soundScale) {
        this.soundScale = soundScale;
    }

    public float getSizeScale() {
        return sizeScale;
    }

    public float getGameSpeed() {
        return gameSpeed;
    }

    public void setGameSpeed(float gameSpeed) {
        this.gameSpeed = gameSpeed;
    }

    public int getEpsilonShootingRapidity() {
        return epsilonShootingRapidity;
    }

    public String getActiveSkillSaveName() {
        return activeSkillSaveName;
    }

    public void setActiveSkillSaveName(String activeSkillSaveName) {
        this.activeSkillSaveName = activeSkillSaveName;
    }

    public List<String> getAcquiredSkillsNames() {
        return acquiredSkillsNames;
    }

    public void setRandomAcquiredSkillsNames(List<String> randomAquiredSkillNames) {
        this.randomAquiredSkillNames = randomAquiredSkillNames;
    }

    public List<String> getRandomAcquiredSkillsNames() {
        return randomAquiredSkillNames;
    }

    public int getTotalXP() {
        return totalXP;
    }

    public void setTotalXP(int totalXP) {
        this.totalXP = totalXP;
    }

    public int getCurrentGameXP() {
        if (currentGameXP == 0) {
           currentGameXP=300;
        }
        return currentGameXP;
    }

    public void setCurrentGameXP(int currentGameXP) {
        this.currentGameXP = currentGameXP;
    }

    public void setPaused(boolean paused ) {
        spawn.setRunning(!paused) ;
        isPaused = paused;
    }

    public boolean isInCooldown() {
        return isInCooldown;
    }

    public void setInCooldown(boolean inCooldown) {
        isInCooldown = inCooldown;
    }

    public boolean isPaused() {
        return isPaused;
    }
}

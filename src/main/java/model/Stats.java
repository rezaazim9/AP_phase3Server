package model;

public class Stats {
    private long timeSurvived;
    private int xp;
    private String profileId;
    public Stats( ) {
    }

    public Stats(long timeSurvived, String profileId, int xp) {
        this.timeSurvived = timeSurvived;
        this.profileId = profileId;
        this.xp = xp;
    }

    public long getTimeSurvived() {
        return timeSurvived;
    }

    public void setTimeSurvived(long timeSurvived) {
        this.timeSurvived = timeSurvived;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }
}

package edu.hitsz.rank;

/**
 * Game difficulty levels used to separate leaderboard files.
 */
public enum GameDifficulty {
    EASY("easy"),
    NORMAL("normal"),
    HARD("hard");

    private final String fileSuffix;

    GameDifficulty(String fileSuffix) {
        this.fileSuffix = fileSuffix;
    }

    public String getFileSuffix() {
        return fileSuffix;
    }
}

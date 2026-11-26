package edu.hitsz.rank;

public final class ScoreRecord {
    public final long id;
    public final String name;
    public final int score;
    public final String difficulty;
    public final long createdAt;
    public ScoreRecord(long id, String name, int score, String difficulty, long createdAt) {
        this.id = id; this.name = name; this.score = score; this.difficulty = difficulty; this.createdAt = createdAt;
    }
}

package edu.hitsz.rank;
import java.util.List;
public interface ScoreRecordDao {
    long insert(String name, int score, GameDifficulty difficulty, long timestamp);
    List<ScoreRecord> loadAll();
    boolean delete(long id);
}

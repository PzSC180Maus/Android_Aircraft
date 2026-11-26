package edu.hitsz.rank;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public final class SqliteScoreRecordDao extends SQLiteOpenHelper implements ScoreRecordDao {
    public SqliteScoreRecordDao(Context context) { super(context.getApplicationContext(), "rank.db", null, 1); }
    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE rank (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, score INTEGER NOT NULL, difficulty TEXT NOT NULL, created_at INTEGER NOT NULL)");
    }
    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
    @Override public long insert(String name, int score, GameDifficulty difficulty, long timestamp) {
        ContentValues values = new ContentValues();
        String trimmed = name == null ? "" : name.trim();
        values.put("name", trimmed.isEmpty() ? "玩家" : trimmed);
        values.put("score", score); values.put("difficulty", difficulty.name()); values.put("created_at", timestamp);
        return getWritableDatabase().insertOrThrow("rank", null, values);
    }
    @Override public List<ScoreRecord> loadAll() {
        List<ScoreRecord> records = new ArrayList<>();
        try (Cursor c = getReadableDatabase().query("rank", new String[]{"_id", "name", "score", "difficulty", "created_at"}, null, null, null, null, "score DESC, created_at ASC, _id ASC")) {
            while (c.moveToNext()) records.add(new ScoreRecord(c.getLong(0), c.getString(1), c.getInt(2), c.getString(3), c.getLong(4)));
        }
        return records;
    }
    @Override public boolean delete(long id) { return getWritableDatabase().delete("rank", "_id=?", new String[]{Long.toString(id)}) > 0; }
}

package com.example.aircraft_android;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import edu.hitsz.rank.ScoreRecord;
import edu.hitsz.rank.ScoreRecordDao;
import edu.hitsz.rank.SqliteScoreRecordDao;

public class LeaderboardActivity extends AppCompatActivity {
    private SqliteScoreRecordDao database;
    private ScoreRecordDao dao;
    private final List<ScoreRecord> records = new ArrayList<>();
    private RankAdapter adapter;
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        database = new SqliteScoreRecordDao(this); dao = database;
        LinearLayout root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(24, 24, 24, 24);
        TextView title = new TextView(this); title.setText("排行榜"); title.setTextSize(26); title.setGravity(Gravity.CENTER); root.addView(title);
        TextView empty = new TextView(this); empty.setText("暂无成绩，完成游戏后即可上榜"); empty.setGravity(Gravity.CENTER); root.addView(empty);
        ListView list = new ListView(this); root.addView(list, new LinearLayout.LayoutParams(-1, 0, 1));
        list.setEmptyView(empty); adapter = new RankAdapter(); list.setAdapter(adapter);
        Button back = new Button(this); back.setText("返回主菜单"); back.setOnClickListener(v -> finish()); root.addView(back);
        setContentView(root); refresh();
    }
    private void refresh() { records.clear(); records.addAll(dao.loadAll()); adapter.notifyDataSetChanged(); }
    @Override protected void onDestroy() { database.close(); super.onDestroy(); }
    private final class RankAdapter extends BaseAdapter {
        @Override public int getCount() { return records.size(); }
        @Override public ScoreRecord getItem(int i) { return records.get(i); }
        @Override public long getItemId(int i) { return getItem(i).id; }
        @Override public View getView(int position, View convertView, ViewGroup parent) {
            Holder h;
            if (convertView == null) {
                LinearLayout row = new LinearLayout(LeaderboardActivity.this); row.setGravity(Gravity.CENTER_VERTICAL); row.setPadding(8, 12, 8, 12);
                TextView text = new TextView(LeaderboardActivity.this); text.setTextSize(16); row.addView(text, new LinearLayout.LayoutParams(0, -2, 1));
                Button delete = new Button(LeaderboardActivity.this); delete.setText("删除"); row.addView(delete);
                h = new Holder(); h.text = text; h.delete = delete; row.setTag(h); convertView = row;
            } else h = (Holder) convertView.getTag();
            ScoreRecord record = getItem(position);
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date(record.createdAt));
            String difficulty = "EASY".equals(record.difficulty) ? "简单" : "NORMAL".equals(record.difficulty) ? "普通" : "困难";
            h.text.setText((position + 1) + ".  " + record.name + "  " + record.score + " 分\n" + difficulty + "  " + date);
            h.text.setTextColor(position == 0 ? Color.rgb(170, 110, 0) : Color.BLACK);
            h.delete.setOnClickListener(v -> new androidx.appcompat.app.AlertDialog.Builder(LeaderboardActivity.this)
                    .setMessage("删除 " + record.name + " 的成绩？").setNegativeButton("取消", null)
                    .setPositiveButton("删除", (d, w) -> { dao.delete(record.id); refresh(); }).show());
            return convertView;
        }
    }
    private static final class Holder { TextView text; Button delete; }
}

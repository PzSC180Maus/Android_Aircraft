package com.example.aircraft_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.material.button.MaterialButton;

import edu.hitsz.rank.GameDifficulty;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS = "aircraft_settings";
    private static final String KEY_SOUND = "sound_enabled";

    private SwitchCompat musicSwitch;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences(PREFS, MODE_PRIVATE);
        musicSwitch = findViewById(R.id.switch_music);
        musicSwitch.setChecked(preferences.getBoolean(KEY_SOUND, true));
        musicSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                preferences.edit().putBoolean(KEY_SOUND, isChecked).apply());

        MaterialButton easy = findViewById(R.id.btn_easy);
        MaterialButton normal = findViewById(R.id.btn_normal);
        MaterialButton hard = findViewById(R.id.btn_hard);

        easy.setOnClickListener(v -> startGame(GameDifficulty.EASY));
        normal.setOnClickListener(v -> startGame(GameDifficulty.NORMAL));
        hard.setOnClickListener(v -> startGame(GameDifficulty.HARD));
    }

    private void startGame(GameDifficulty difficulty) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(GameActivity.EXTRA_DIFFICULTY, difficulty.name());
        intent.putExtra(GameActivity.EXTRA_SOUND, musicSwitch.isChecked());
        startActivity(intent);
    }
}

package com.example.aircraft_android;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import edu.hitsz.application.GameView;
import edu.hitsz.rank.GameDifficulty;

public class GameActivity extends AppCompatActivity {

    public static final String EXTRA_DIFFICULTY = "extra_difficulty";
    public static final String EXTRA_SOUND = "extra_sound";

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private GameView gameView;
    private GameDifficulty difficulty;
    private boolean dialogShowing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        Intent intent = getIntent();
        difficulty = parseDifficulty(intent.getStringExtra(EXTRA_DIFFICULTY));
        boolean sound = intent.getBooleanExtra(EXTRA_SOUND, true);

        gameView = new GameView(this, difficulty, sound, () -> mainHandler.post(this::showGameOverDialog));
        setContentView(gameView);
    }

    private GameDifficulty parseDifficulty(String value) {
        if (value == null) {
            return GameDifficulty.EASY;
        }
        try {
            return GameDifficulty.valueOf(value);
        } catch (IllegalArgumentException e) {
            return GameDifficulty.EASY;
        }
    }

    private void showGameOverDialog() {
        if (isFinishing() || isDestroyed() || dialogShowing || gameView == null || !gameView.isGameOver()) {
            return;
        }
        dialogShowing = true;
        new AlertDialog.Builder(this)
                .setTitle(R.string.game_over_title)
                .setMessage(getString(R.string.game_over_message,
                        difficultyLabel(), gameView.getScore()))
                .setCancelable(false)
                .setPositiveButton(R.string.play_again, (dialog, which) -> {
                    dialogShowing = false;
                    gameView.restart();
                })
                .setNegativeButton(R.string.back_to_menu, (dialog, which) -> {
                    dialogShowing = false;
                    finish();
                })
                .show();
    }

    private String difficultyLabel() {
        switch (difficulty) {
            case NORMAL:
                return getString(R.string.difficulty_normal);
            case HARD:
                return getString(R.string.difficulty_hard);
            case EASY:
            default:
                return getString(R.string.difficulty_easy);
        }
    }

    @Override
    protected void onDestroy() {
        if (gameView != null) {
            gameView.release();
            gameView = null;
        }
        super.onDestroy();
    }
}

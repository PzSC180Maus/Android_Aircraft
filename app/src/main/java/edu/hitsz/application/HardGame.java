package edu.hitsz.application;

import edu.hitsz.music.GameSound;
import edu.hitsz.rank.GameDifficulty;

/**
 * Hard difficulty game.
 */
public class HardGame extends Game {

    private static final int BOSS_SCORE_STEP = 380;
    private int bossSpawnCount = 0;
    private long nextProgressTimeMillis = 15000;
    private int progressStage = 0;

    public HardGame(Runnable gameOverCallback, GameSound sound) {
        super(GameDifficulty.HARD, gameOverCallback, sound, BOSS_SCORE_STEP);
        this.enemySpawnCycle = 18;
        this.enemyMaxNumber = 6;
        this.enemySpeedFactor = 1.1;
        this.enemyHpFactor = 1.15;
        this.heroShootCycle = 15;
        this.enemyShootCycle = 20;
    }

    @Override
    protected void onDifficultyProgress(long battleTimeMillis) {
        if (battleTimeMillis >= nextProgressTimeMillis) {
            this.enemySpawnCycle = Math.max(9, this.enemySpawnCycle - 1);
            this.enemyMaxNumber = Math.min(10, this.enemyMaxNumber + 1);
            this.enemySpeedFactor = Math.min(2.2, this.enemySpeedFactor + 0.08);
            this.enemyHpFactor = Math.min(2.8, this.enemyHpFactor + 0.12);
            this.heroShootCycle = Math.max(8, this.heroShootCycle - 1);
            this.enemyShootCycle = Math.max(10, this.enemyShootCycle - 1);
            progressStage++;
            nextProgressTimeMillis += 15000;
        }
    }

    @Override
    protected boolean canSpawnBoss() {
        return true;
    }

    @Override
    protected int resolveBossHp(int baseBossHp) {
        int hp = (int) (baseBossHp * (1.0 + 0.25 * bossSpawnCount));
        bossSpawnCount++;
        return hp;
    }

    @Override
    protected int nextBossTriggerScoreStep() {
        return BOSS_SCORE_STEP;
    }
}

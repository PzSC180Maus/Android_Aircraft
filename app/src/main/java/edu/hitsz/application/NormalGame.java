package edu.hitsz.application;

import edu.hitsz.music.GameSound;
import edu.hitsz.rank.GameDifficulty;

/**
 * Normal difficulty game.
 */
public class NormalGame extends Game {

    private static final int BOSS_SCORE_STEP = 450;
    private long nextProgressTimeMillis = 20000;
    private int progressStage = 0;

    public NormalGame(Runnable gameOverCallback, GameSound sound) {
        super(GameDifficulty.NORMAL, gameOverCallback, sound, BOSS_SCORE_STEP);
        this.enemySpawnCycle = 20;
        this.enemyMaxNumber = 5;
        this.enemySpeedFactor = 1.0;
        this.enemyHpFactor = 1.0;
        this.heroShootCycle = 15;
        this.enemyShootCycle = 22;
    }

    @Override
    protected void onDifficultyProgress(long battleTimeMillis) {
        if (battleTimeMillis >= nextProgressTimeMillis) {
            this.enemySpawnCycle = Math.max(12, this.enemySpawnCycle - 1);
            this.enemyMaxNumber = Math.min(8, this.enemyMaxNumber + 1);
            this.enemySpeedFactor = Math.min(1.8, this.enemySpeedFactor + 0.05);
            this.enemyHpFactor = Math.min(2.0, this.enemyHpFactor + 0.08);
            progressStage++;
            nextProgressTimeMillis += 20000;
        }
    }

    @Override
    protected boolean canSpawnBoss() {
        return true;
    }

    @Override
    protected int resolveBossHp(int baseBossHp) {
        return baseBossHp;
    }

    @Override
    protected int nextBossTriggerScoreStep() {
        return BOSS_SCORE_STEP;
    }
}

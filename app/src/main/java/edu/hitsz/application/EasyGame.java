package edu.hitsz.application;

import edu.hitsz.rank.GameDifficulty;

/**
 * Easy difficulty game.
 */
public class EasyGame extends Game {

    public EasyGame(Runnable gameOverCallback) {
        super(GameDifficulty.EASY, gameOverCallback, Integer.MAX_VALUE / 2);
        this.enemySpawnCycle = 24;
        this.enemyMaxNumber = 4;
        this.enemySpeedFactor = 0.9;
        this.enemyHpFactor = 0.9;
        this.heroShootCycle = 15;
        this.enemyShootCycle = 22;
        System.out.println("[EASY] init: enemySpawnCycle=" + enemySpawnCycle
                + ", enemyMaxNumber=" + enemyMaxNumber
                + ", bossSpawn=disabled");
    }

    @Override
    protected void onDifficultyProgress(long battleTimeMillis) {
        // Easy mode keeps stable difficulty.
    }

    @Override
    protected boolean canSpawnBoss() {
        return false;
    }

    @Override
    protected int resolveBossHp(int baseBossHp) {
        return baseBossHp;
    }

    @Override
    protected int nextBossTriggerScoreStep() {
        return Integer.MAX_VALUE / 2;
    }
}

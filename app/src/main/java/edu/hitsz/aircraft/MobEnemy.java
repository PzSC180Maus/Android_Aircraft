package edu.hitsz.aircraft;

import edu.hitsz.strategy.EmptyShootStrategy;

/**
 * Normal enemy, cannot shoot.
 */
public class MobEnemy extends AbstractEnemy {

    private static final int SCORE = 10;

    public MobEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp, SCORE);
        this.setShootStrategy(new EmptyShootStrategy());
    }

    @Override
    public void onFreezeEffect(long durationMillis) {
        // Mob enemy: permanently frozen.
        freezeForDuration(Long.MAX_VALUE / 4);
    }
}

package edu.hitsz.aircraft;

import edu.hitsz.strategy.StraightShootStrategy;

/**
 * Elite enemy with basic single-bullet attack.
 */
public class EliteEnemy extends AbstractEnemy {

    private static final int SCORE = 20;

    public EliteEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp, SCORE);
        this.setShootStrategy(new StraightShootStrategy(15));
    }

    @Override
    public boolean canDropSupply() {
        return false;
    }

    @Override
    public void onFreezeEffect(long durationMillis) {
        // Elite enemy: freeze for 4 seconds then recover.
        freezeForDuration(4000);
    }
}

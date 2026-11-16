package edu.hitsz.aircraft;

import edu.hitsz.strategy.DoubleStraightShootStrategy;

/**
 * ElitePlus enemy (sharp): straight double-bullet attack and can drop supplies.
 */
public class ElitePlusEnemy extends AbstractEnemy {

    private static final int SCORE = 30;

    public ElitePlusEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp, SCORE);
        this.setShootStrategy(new DoubleStraightShootStrategy(18));
    }

    @Override
    public boolean canDropSupply() {
        return true;
    }

    @Override
    public void onFreezeEffect(long durationMillis) {
        // ElitePlus enemy (sharp): freeze for 3 seconds then recover.
        freezeForDuration(3000);
    }
}

package edu.hitsz.aircraft;

import edu.hitsz.strategy.ScatterShootStrategy;

/**
 * ElitePro enemy (ace): fan-shaped 3-bullet attack and can drop supplies.
 */
public class EliteProEnemy extends AbstractEnemy {

    private static final int SCORE = 40;

    public EliteProEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp, SCORE);
        this.setShootStrategy(new ScatterShootStrategy(18));
    }

    @Override
    public boolean canDropSupply() {
        return true;
    }

    @Override
    public void onBombEffect() {
        // Ace enemy: bomb only causes hp loss.
        decreaseHp(35);
    }

    @Override
    public void onFreezeEffect(long durationMillis) {
        // Ace enemy: slow for 5 seconds then recover.
        slowForDuration(0.5, 5000);
    }
}

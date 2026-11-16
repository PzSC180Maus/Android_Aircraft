package edu.hitsz.aircraft;

import edu.hitsz.application.Main;
import edu.hitsz.strategy.CircularShootStrategy;

/**
 * Boss enemy: stays near top and only moves horizontally.
 */
public class BossEnemy extends AbstractEnemy {

    private static final int SCORE = 120;

    public BossEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp, SCORE);
        this.setShootStrategy(new CircularShootStrategy(20, 20, 6));
    }

    @Override
    public void forward() {
        locationX += speedX;
        if (locationX <= 0 || locationX >= Main.WINDOW_WIDTH) {
            speedX = -speedX;
        }
        // Keep floating near top area, no downward movement.
        if (locationY > Main.WINDOW_HEIGHT * 0.2) {
            locationY = (int) (Main.WINDOW_HEIGHT * 0.2);
        }
    }

    @Override
    public boolean canDropSupply() {
        return true;
    }

    @Override
    public void onBombEffect() {
        // Boss is immune to bomb.
    }

    @Override
    public void onFreezeEffect(long durationMillis) {
        // Boss is immune to freeze.
    }
}

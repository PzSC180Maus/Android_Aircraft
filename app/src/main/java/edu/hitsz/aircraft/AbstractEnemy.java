package edu.hitsz.aircraft;

import edu.hitsz.application.Main;
import edu.hitsz.observer.PropEffectObserver;

/**
 * Common base class of all enemy aircraft.
 */
public abstract class AbstractEnemy extends AbstractAircraft implements PropEffectObserver {

    private final int score;
    private volatile long freezeUntilMillis = 0L;
    protected int baseSpeedX;
    protected int baseSpeedY;

    public AbstractEnemy(int locationX, int locationY, int speedX, int speedY, int hp, int score) {
        super(locationX, locationY, speedX, speedY, hp);
        this.score = score;
        this.baseSpeedX = speedX;
        this.baseSpeedY = speedY;
    }

    @Override
    public void forward() {
        if (System.currentTimeMillis() < freezeUntilMillis) {
            return;
        }
        super.forward();
        if (locationY >= Main.WINDOW_HEIGHT) {
            vanish();
        }
    }

    public int getScore() {
        return score;
    }

    public boolean canDropSupply() {
        return false;
    }

    @Override
    public void onBombEffect() {
        decreaseHp(Integer.MAX_VALUE);
    }

    @Override
    public void onFreezeEffect(long durationMillis) {
        freezeUntilMillis = System.currentTimeMillis() + Math.max(500, durationMillis);
    }

    protected void freezeForDuration(long durationMillis) {
        freezeUntilMillis = System.currentTimeMillis() + Math.max(500, durationMillis);
    }

    protected void slowForDuration(double speedRatio, long durationMillis) {
        final int originalSpeedX = this.speedX;
        final int originalSpeedY = this.speedY;
        this.speedX = (int) Math.round(originalSpeedX * speedRatio);
        this.speedY = (int) Math.round(originalSpeedY * speedRatio);
        new Thread(() -> {
            try {
                Thread.sleep(Math.max(500, durationMillis));
            } catch (InterruptedException ignored) {
            }
            this.speedX = originalSpeedX;
            this.speedY = originalSpeedY;
        }, "enemy-slow-recover-thread").start();
    }
}

package edu.hitsz.aircraft;

import edu.hitsz.strategy.StraightShootStrategy;
import edu.hitsz.strategy.ShootStrategy;

/**
 * Hero aircraft controlled by mouse.
 * Implemented as Singleton.
 */
public class HeroAircraft extends AbstractAircraft {

    private static volatile HeroAircraft instance;
    private long temporaryShootStrategyExpireTime = -1L;
    private int explosiveChainRemainingShots = 0;

    private HeroAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.setShootStrategy(new StraightShootStrategy(30));
    }

    public static HeroAircraft getInstance(int locationX, int locationY, int speedX, int speedY, int hp) {
        if (instance == null) {
            synchronized (HeroAircraft.class) {
                if (instance == null) {
                    instance = new HeroAircraft(locationX, locationY, speedX, speedY, hp);
                }
            }
        }
        return instance;
    }

    public synchronized void resetForNewGame(int locationX, int locationY, int hp) {
        this.locationX = locationX;
        this.locationY = locationY;
        this.speedX = 0;
        this.speedY = 0;
        this.maxHp = hp;
        this.hp = hp;
        this.isValid = true;
        this.explosiveChainRemainingShots = 0;
        resetToDefaultShootStrategy();
    }

    public void resetToDefaultShootStrategy() {
        this.setShootStrategy(new StraightShootStrategy(30));
        temporaryShootStrategyExpireTime = -1L;
    }

    public synchronized void activateTemporaryShootStrategy(ShootStrategy strategy, long durationMillis) {
        setShootStrategy(strategy);
        temporaryShootStrategyExpireTime = System.currentTimeMillis() + durationMillis;
    }

    public synchronized void recoverShootStrategyIfNeeded() {
        if (temporaryShootStrategyExpireTime > 0 && System.currentTimeMillis() >= temporaryShootStrategyExpireTime) {
            resetToDefaultShootStrategy();
        }
    }

    public synchronized void addExplosiveChainShots(int count) {
        if (count > 0) {
            explosiveChainRemainingShots += count;
        }
    }

    public synchronized boolean consumeExplosiveShotIfAvailable() {
        if (explosiveChainRemainingShots <= 0) {
            return false;
        }
        explosiveChainRemainingShots--;
        return true;
    }

    public synchronized int getExplosiveChainRemainingShots() {
        return explosiveChainRemainingShots;
    }

    @Override
    public void forward() {
        // Hero movement is controlled by mouse.
    }
}

package edu.hitsz.strategy;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;

import java.util.LinkedList;
import java.util.List;

/**
 * Circular shooting strategy.
 */
public class CircularShootStrategy implements ShootStrategy {

    private final int bulletCount;
    private final int power;
    private final int speedMagnitude;

    public CircularShootStrategy(int bulletCount, int power, int speedMagnitude) {
        this.bulletCount = bulletCount;
        this.power = power;
        this.speedMagnitude = speedMagnitude;
    }

    @Override
    public List<BaseBullet> shoot(AbstractAircraft aircraft) {
        List<BaseBullet> bullets = new LinkedList<>();
        boolean hero = aircraft instanceof HeroAircraft;

        for (int i = 0; i < bulletCount; i++) {
            double angle = 2 * Math.PI * i / bulletCount;
            int speedX = (int) Math.round(speedMagnitude * Math.cos(angle));
            int speedY = (int) Math.round(speedMagnitude * Math.sin(angle));
            if (speedX == 0 && speedY == 0) {
                speedY = hero ? -1 : 1;
            }

            if (hero) {
                HeroAircraft heroAircraft = (HeroAircraft) aircraft;
                boolean explosive = heroAircraft.consumeExplosiveShotIfAvailable();
                bullets.add(new HeroBullet(aircraft.getLocationX(), aircraft.getLocationY(),
                        speedX, speedY, power, explosive,
                        explosive ? HeroBullet.DEFAULT_EXPLOSION_RADIUS : 0,
                        explosive ? HeroBullet.DEFAULT_PROXIMITY_FUSE_RADIUS : 0));
            } else {
                bullets.add(new EnemyBullet(aircraft.getLocationX(), aircraft.getLocationY(), speedX, speedY, power));
            }
        }
        return bullets;
    }
}

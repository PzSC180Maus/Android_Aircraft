package edu.hitsz.strategy;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;

import java.util.LinkedList;
import java.util.List;

/**
 * Single-line straight shooting strategy.
 */
public class StraightShootStrategy implements ShootStrategy {

    private final int power;

    public StraightShootStrategy(int power) {
        this.power = power;
    }

    @Override
    public List<BaseBullet> shoot(AbstractAircraft aircraft) {
        List<BaseBullet> bullets = new LinkedList<>();
        int direction = aircraft instanceof HeroAircraft ? -1 : 1;
        int x = aircraft.getLocationX();
        int y = aircraft.getLocationY() + direction * 2;
        int speedY = aircraft.getSpeedY() + direction * 5;

        if (aircraft instanceof HeroAircraft) {
            HeroAircraft hero = (HeroAircraft) aircraft;
            boolean explosive = hero.consumeExplosiveShotIfAvailable();
            bullets.add(new HeroBullet(x, y, 0, speedY, power, explosive,
                    explosive ? HeroBullet.DEFAULT_EXPLOSION_RADIUS : 0,
                    explosive ? HeroBullet.DEFAULT_PROXIMITY_FUSE_RADIUS : 0));
        } else {
            bullets.add(new EnemyBullet(x, y, 0, speedY, power));
        }
        return bullets;
    }
}

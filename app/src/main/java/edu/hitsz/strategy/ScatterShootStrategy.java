package edu.hitsz.strategy;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;

import java.util.LinkedList;
import java.util.List;

/**
 * Three-bullet fan-shaped scatter strategy.
 */
public class ScatterShootStrategy implements ShootStrategy {

    private final int power;

    public ScatterShootStrategy(int power) {
        this.power = power;
    }

    @Override
    public List<BaseBullet> shoot(AbstractAircraft aircraft) {
        List<BaseBullet> bullets = new LinkedList<>();
        boolean hero = aircraft instanceof HeroAircraft;
        int direction = hero ? -1 : 1;
        int y = aircraft.getLocationY() + direction * 2;
        int speedY = aircraft.getSpeedY() + direction * 6;

        if (hero) {
            HeroAircraft heroAircraft = (HeroAircraft) aircraft;
            boolean ex1 = heroAircraft.consumeExplosiveShotIfAvailable();
            boolean ex2 = heroAircraft.consumeExplosiveShotIfAvailable();
            boolean ex3 = heroAircraft.consumeExplosiveShotIfAvailable();
            bullets.add(new HeroBullet(aircraft.getLocationX() - 12, y, -2, speedY, power, ex1,
                    ex1 ? HeroBullet.DEFAULT_EXPLOSION_RADIUS : 0,
                    ex1 ? HeroBullet.DEFAULT_PROXIMITY_FUSE_RADIUS : 0));
            bullets.add(new HeroBullet(aircraft.getLocationX(), y, 0, speedY, power, ex2,
                    ex2 ? HeroBullet.DEFAULT_EXPLOSION_RADIUS : 0,
                    ex2 ? HeroBullet.DEFAULT_PROXIMITY_FUSE_RADIUS : 0));
            bullets.add(new HeroBullet(aircraft.getLocationX() + 12, y, 2, speedY, power, ex3,
                    ex3 ? HeroBullet.DEFAULT_EXPLOSION_RADIUS : 0,
                    ex3 ? HeroBullet.DEFAULT_PROXIMITY_FUSE_RADIUS : 0));
        } else {
            bullets.add(new EnemyBullet(aircraft.getLocationX() - 12, y, -2, speedY, power));
            bullets.add(new EnemyBullet(aircraft.getLocationX(), y, 0, speedY, power));
            bullets.add(new EnemyBullet(aircraft.getLocationX() + 12, y, 2, speedY, power));
        }
        return bullets;
    }
}

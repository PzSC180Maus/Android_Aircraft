package edu.hitsz.strategy;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;

import java.util.LinkedList;
import java.util.List;

/**
 * Two-line straight shooting strategy.
 */
public class DoubleStraightShootStrategy implements ShootStrategy {

    private final int power;

    public DoubleStraightShootStrategy(int power) {
        this.power = power;
    }

    @Override
    public List<BaseBullet> shoot(AbstractAircraft aircraft) {
        List<BaseBullet> bullets = new LinkedList<>();
        int y = aircraft.getLocationY() + 2;
        int speedY = aircraft.getSpeedY() + 6;

        bullets.add(new EnemyBullet(aircraft.getLocationX() - 12, y, 0, speedY, power));
        bullets.add(new EnemyBullet(aircraft.getLocationX() + 12, y, 0, speedY, power));
        return bullets;
    }
}
package edu.hitsz.strategy;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.bullet.BaseBullet;

import java.util.List;

/**
 * Strategy interface for aircraft shooting behavior.
 */
public interface ShootStrategy {
    List<BaseBullet> shoot(AbstractAircraft aircraft);
}
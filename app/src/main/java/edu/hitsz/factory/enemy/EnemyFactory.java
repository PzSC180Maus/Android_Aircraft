package edu.hitsz.factory.enemy;

import edu.hitsz.aircraft.AbstractEnemy;

/**
 * Factory method interface for enemy creation.
 */
public interface EnemyFactory {
    AbstractEnemy createEnemy();
}
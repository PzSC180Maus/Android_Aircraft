package edu.hitsz.factory.enemy;

import edu.hitsz.aircraft.AbstractEnemy;
import edu.hitsz.aircraft.BossEnemy;

/**
 * Factory of BossEnemy.
 * Boss creation is supported by the pattern, but Boss is not spawned in lab2 gameplay cycle.
 */
public class BossEnemyFactory implements EnemyFactory {

    private final int locationX;
    private final int locationY;
    private final int speedX;
    private final int speedY;
    private final int hp;

    public BossEnemyFactory(int locationX, int locationY, int speedX, int speedY, int hp) {
        this.locationX = locationX;
        this.locationY = locationY;
        this.speedX = speedX;
        this.speedY = speedY;
        this.hp = hp;
    }

    @Override
    public AbstractEnemy createEnemy() {
        return new BossEnemy(locationX, locationY, speedX, speedY, hp);
    }
}
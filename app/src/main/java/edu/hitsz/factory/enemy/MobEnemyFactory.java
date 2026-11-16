package edu.hitsz.factory.enemy;

import edu.hitsz.aircraft.AbstractEnemy;
import edu.hitsz.aircraft.MobEnemy;

/**
 * Factory of MobEnemy.
 */
public class MobEnemyFactory implements EnemyFactory {

    private final int locationX;
    private final int locationY;
    private final int speedX;
    private final int speedY;
    private final int hp;

    public MobEnemyFactory(int locationX, int locationY, int speedX, int speedY, int hp) {
        this.locationX = locationX;
        this.locationY = locationY;
        this.speedX = speedX;
        this.speedY = speedY;
        this.hp = hp;
    }

    @Override
    public AbstractEnemy createEnemy() {
        return new MobEnemy(locationX, locationY, speedX, speedY, hp);
    }
}
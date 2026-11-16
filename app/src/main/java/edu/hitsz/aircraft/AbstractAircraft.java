package edu.hitsz.aircraft;

import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.strategy.EmptyShootStrategy;
import edu.hitsz.strategy.ShootStrategy;

import java.util.List;

/**
 * Base class of all aircraft objects.
 */
public abstract class AbstractAircraft extends AbstractFlyingObject {

    protected int maxHp;
    protected int hp;
    protected ShootStrategy shootStrategy;

    public AbstractAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY);
        this.hp = hp;
        this.maxHp = hp;
        this.shootStrategy = new EmptyShootStrategy();
    }

    public void decreaseHp(int decrease) {
        hp -= decrease;
        if (hp <= 0) {
            hp = 0;
            vanish();
        }
    }

    public void increaseHp(int increase) {
        if (increase <= 0) {
            return;
        }
        hp += increase;
        if (hp > maxHp) {
            hp = maxHp;
        }
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void setShootStrategy(ShootStrategy shootStrategy) {
        if (shootStrategy != null) {
            this.shootStrategy = shootStrategy;
        }
    }

    public ShootStrategy getShootStrategy() {
        return shootStrategy;
    }

    public List<BaseBullet> shoot() {
        return shootStrategy.shoot(this);
    }
}
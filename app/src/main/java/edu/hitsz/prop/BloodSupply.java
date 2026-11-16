package edu.hitsz.prop;

import edu.hitsz.aircraft.HeroAircraft;

/**
 * Blood supply increases hero hp, capped by max hp.
 */
public class BloodSupply extends AbstractProp {

    private static final int INCREASE_HP = 40;

    public BloodSupply(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void activate(HeroAircraft heroAircraft) {
        heroAircraft.increaseHp(INCREASE_HP);
    }
}

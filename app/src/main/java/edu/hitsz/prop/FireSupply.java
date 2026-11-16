package edu.hitsz.prop;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.strategy.ScatterShootStrategy;

/**
 * Fire supply switches hero shooting to scatter strategy.
 */
public class FireSupply extends AbstractProp {

    private static final long EFFECT_DURATION_MS = 6000;

    public FireSupply(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void activate(HeroAircraft heroAircraft) {
        heroAircraft.activateTemporaryShootStrategy(new ScatterShootStrategy(30), EFFECT_DURATION_MS);
        heroAircraft.addExplosiveChainShots(50);
    }
}

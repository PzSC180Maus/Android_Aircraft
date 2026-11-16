package edu.hitsz.prop;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.strategy.CircularShootStrategy;

/**
 * Fire plus supply switches hero shooting to circular strategy.
 */
public class FirePlusSupply extends AbstractProp {

    private static final long EFFECT_DURATION_MS = 6000;

    public FirePlusSupply(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void activate(HeroAircraft heroAircraft) {
        heroAircraft.activateTemporaryShootStrategy(new CircularShootStrategy(20, 30, 8), EFFECT_DURATION_MS);
        heroAircraft.addExplosiveChainShots(50);
    }
}

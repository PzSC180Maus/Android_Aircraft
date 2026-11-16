package edu.hitsz.factory.prop;

import edu.hitsz.prop.AbstractProp;
import edu.hitsz.prop.BloodSupply;
import edu.hitsz.prop.BombSupply;
import edu.hitsz.prop.FirePlusSupply;
import edu.hitsz.prop.FireSupply;
import edu.hitsz.prop.FreezeSupply;

/**
 * Simple factory for all prop objects.
 */
public class PropFactory {

    private PropFactory() {
    }

    public static AbstractProp createProp(PropType propType, int locationX, int locationY, int speedX, int speedY) {
        switch (propType) {
            case BLOOD:
                return new BloodSupply(locationX, locationY, speedX, speedY);
            case FIRE:
                return new FireSupply(locationX, locationY, speedX, speedY);
            case FIRE_PLUS:
                return new FirePlusSupply(locationX, locationY, speedX, speedY);
            case BOMB:
                return new BombSupply(locationX, locationY, speedX, speedY);
            case FREEZE:
                return new FreezeSupply(locationX, locationY, speedX, speedY);
            default:
                throw new IllegalArgumentException("Unknown prop type: " + propType);
        }
    }
}
package edu.hitsz.prop;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.observer.PropEffectObserver;
import edu.hitsz.observer.PropEffectSubject;

/**
 * Freeze supply as concrete subject.
 */
public class FreezeSupply extends AbstractProp {

    private long durationMillis = 4000;

    private final PropEffectSubject freezeSubject = new PropEffectSubject() {
        @Override
        public void notifyObservers() {
            for (PropEffectObserver observer : observers) {
                observer.onFreezeEffect(durationMillis);
            }
        }

        @Override
        public void trigger() {
            notifyObservers();
        }
    };

    public FreezeSupply(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void activate(HeroAircraft heroAircraft) {
        System.out.println("FreezeSupply active!");
    }

    public void setDurationMillis(long durationMillis) {
        this.durationMillis = Math.max(500, durationMillis);
    }

    public void addObserver(PropEffectObserver observer) {
        freezeSubject.addObserver(observer);
    }

    public void removeObserver(PropEffectObserver observer) {
        freezeSubject.removeObserver(observer);
    }

    public void notifyFreezeEffect() {
        freezeSubject.notifyObservers();
    }

    public void triggerFreeze() {
        freezeSubject.trigger();
    }

    public void clearObservers() {
        freezeSubject.clearObservers();
    }
}

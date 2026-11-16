package edu.hitsz.prop;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.observer.PropEffectObserver;
import edu.hitsz.observer.PropEffectSubject;

/**
 * Bomb supply as concrete subject.
 */
public class BombSupply extends AbstractProp {

    private final PropEffectSubject bombSubject = new PropEffectSubject() {
        @Override
        public void notifyObservers() {
            for (PropEffectObserver observer : observers) {
                observer.onBombEffect();
            }
        }

        @Override
        public void trigger() {
            notifyObservers();
        }
    };

    public BombSupply(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void activate(HeroAircraft heroAircraft) {
        System.out.println("BombSupply active!");
    }

    public void addObserver(PropEffectObserver observer) {
        bombSubject.addObserver(observer);
    }

    public void removeObserver(PropEffectObserver observer) {
        bombSubject.removeObserver(observer);
    }

    public void notifyBombEffect() {
        bombSubject.notifyObservers();
    }

    public void triggerBomb() {
        bombSubject.trigger();
    }

    public void clearObservers() {
        bombSubject.clearObservers();
    }
}

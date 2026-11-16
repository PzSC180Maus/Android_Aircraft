package edu.hitsz.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract subject, same structure as AlarmSubject demo.
 */
public abstract class PropEffectSubject {

    protected final List<PropEffectObserver> observers = new ArrayList<>();

    public void addObserver(PropEffectObserver observer) {
        if (observer != null) {
            observers.add(observer);
        }
    }

    public void removeObserver(PropEffectObserver observer) {
        observers.remove(observer);
    }

    public void clearObservers() {
        observers.clear();
    }

    public abstract void notifyObservers();

    public abstract void trigger();
}

package edu.hitsz.observer;

/**
 * Observer for prop effects.
 */
public interface PropEffectObserver {

    void onBombEffect();

    void onFreezeEffect(long durationMillis);
}

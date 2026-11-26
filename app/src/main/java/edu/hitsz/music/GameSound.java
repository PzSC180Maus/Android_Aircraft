package edu.hitsz.music;

/**
 * Audio boundary used by the game logic.
 * Keeping this interface free of Android classes lets the game layer stay
 * independent from the concrete MediaPlayer/SoundPool implementation.
 */
public interface GameSound {

    GameSound SILENT = new GameSound() {
    };

    default void playBgm() {
    }

    default void resumeBgm() {
    }

    default void pauseAll() {
    }

    default void playBossBgm() {
    }

    default void stopBossBgm() {
    }

    default void playBulletHit() {
    }

    default void playBombExplosion() {
    }

    default void playGetSupply() {
    }

    default void playGameOver() {
    }
}

package edu.hitsz.music;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;

import com.example.aircraft_android.R;

/**
 * Independent audio manager.
 *
 * MediaPlayer is used for the looping background music, SoundPool is used for
 * short, frequently played effects. The manager only keeps an application
 * context, so it does not leak an Activity.
 */
public final class AudioManager implements GameSound {

    private static final String TAG = "AudioManager";

    private final Context context;

    private MediaPlayer bgmPlayer;
    private MediaPlayer bossPlayer;
    private SoundPool soundPool;

    private int bulletHitId;
    private int bombExplosionId;
    private int getSupplyId;
    private int gameOverId;

    private boolean enabled;
    private boolean bgmRequested;
    private boolean bossRequested;
    private boolean released;

    public AudioManager(Context context, boolean enabled) {
        this.context = context.getApplicationContext();
        this.enabled = enabled;
        initSoundPool();
        initBgmPlayer();
    }

    private void initSoundPool() {
        try {
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(8)
                    .setAudioAttributes(attributes)
                    .build();
            bulletHitId = soundPool.load(context, R.raw.bullet_hit, 1);
            bombExplosionId = soundPool.load(context, R.raw.bomb_explosion, 1);
            getSupplyId = soundPool.load(context, R.raw.get_supply, 1);
            gameOverId = soundPool.load(context, R.raw.game_over, 1);
        } catch (RuntimeException e) {
            soundPool = null;
        }
    }

    private void initBgmPlayer() {
        try {
            bgmPlayer = MediaPlayer.create(context, R.raw.bgm);
            if (bgmPlayer != null) {
                bgmPlayer.setLooping(true);
            }
        } catch (RuntimeException e) {
            bgmPlayer = null;
        }
    }

    private void ensureBossPlayer() {
        if (bossPlayer != null) {
            return;
        }
        try {
            bossPlayer = MediaPlayer.create(context, R.raw.bgm_boss);
            if (bossPlayer != null) {
                bossPlayer.setLooping(true);
            }
        } catch (RuntimeException e) {
            bossPlayer = null;
        }
    }

    public synchronized void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            pauseAll();
        }
    }

    @Override
    public synchronized void playBgm() {
        if (released) {
            return;
        }
        bgmRequested = true;
        bossRequested = false;
        pausePlayer(bossPlayer);
        if (!enabled) {
            pausePlayer(bgmPlayer);
            Log.d(TAG, "playBgm ignored: sound disabled");
            return;
        }
        startPlayer(bgmPlayer, true);
        Log.d(TAG, "playBgm: player=" + (bgmPlayer != null) + ", playing=" + isPlaying(bgmPlayer));
    }

    @Override
    public synchronized void resumeBgm() {
        if (released || !enabled) {
            return;
        }
        if (bossRequested) {
            ensureBossPlayer();
            startPlayer(bossPlayer, false);
        } else if (bgmRequested) {
            startPlayer(bgmPlayer, false);
        }
    }

    @Override
    public synchronized void pauseAll() {
        Log.d(TAG, "pauseAll: bgmPlaying=" + isPlaying(bgmPlayer) + ", bossPlaying=" + isPlaying(bossPlayer));
        pausePlayer(bgmPlayer);
        pausePlayer(bossPlayer);
    }

    @Override
    public synchronized void playBossBgm() {
        if (released) {
            return;
        }
        bgmRequested = true;
        bossRequested = true;
        pausePlayer(bgmPlayer);
        if (!enabled) {
            return;
        }
        ensureBossPlayer();
        startPlayer(bossPlayer, true);
        Log.d(TAG, "playBossBgm: player=" + (bossPlayer != null) + ", playing=" + isPlaying(bossPlayer));
    }

    @Override
    public synchronized void stopBossBgm() {
        bossRequested = false;
        pausePlayer(bossPlayer);
        if (enabled && bgmRequested) {
            startPlayer(bgmPlayer, false);
        }
        Log.d(TAG, "stopBossBgm: bgmPlaying=" + isPlaying(bgmPlayer));
    }

    @Override
    public synchronized void playBulletHit() {
        playEffect(bulletHitId);
    }

    @Override
    public synchronized void playBombExplosion() {
        playEffect(bombExplosionId);
    }

    @Override
    public synchronized void playGetSupply() {
        playEffect(getSupplyId);
    }

    @Override
    public synchronized void playGameOver() {
        Log.d(TAG, "playGameOver: bgmPlaying=" + isPlaying(bgmPlayer));
        bgmRequested = false;
        bossRequested = false;
        pausePlayer(bgmPlayer);
        pausePlayer(bossPlayer);
        playEffect(gameOverId);
    }

    private void playEffect(int soundId) {
        if (!enabled || released || soundPool == null || soundId == 0) {
            return;
        }
        soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    private void startPlayer(MediaPlayer player, boolean restart) {
        if (player == null) {
            return;
        }
        try {
            if (player.isPlaying()) {
                player.pause();
            }
            if (restart) {
                player.seekTo(0);
            }
            player.start();
        } catch (IllegalStateException e) {
            Log.e(TAG, "startPlayer failed", e);
        }
    }

    private void pausePlayer(MediaPlayer player) {
        if (player == null) {
            return;
        }
        try {
            if (player.isPlaying()) {
                player.pause();
            }
        } catch (IllegalStateException ignored) {
        }
    }

    private boolean isPlaying(MediaPlayer player) {
        if (player == null) {
            return false;
        }
        try {
            return player.isPlaying();
        } catch (IllegalStateException e) {
            return false;
        }
    }

    public synchronized void release() {
        if (released) {
            return;
        }
        released = true;
        bgmRequested = false;
        bossRequested = false;
        if (bgmPlayer != null) {
            bgmPlayer.release();
            bgmPlayer = null;
        }
        if (bossPlayer != null) {
            bossPlayer.release();
            bossPlayer = null;
        }
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }
}

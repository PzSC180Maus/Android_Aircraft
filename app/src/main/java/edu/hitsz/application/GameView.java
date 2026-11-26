package edu.hitsz.application;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;

import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.music.AudioManager;
import edu.hitsz.rank.GameDifficulty;

public class GameView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private final SurfaceHolder holder;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private final Object gameLock = new Object();

    private final GameDifficulty difficulty;
    private final boolean soundEnabled;
    private final Runnable onGameOver;
    private final AudioManager audio;

    private volatile boolean running;
    private Thread thread;
    private Game game;
    private float scale = 1;
    private int backgroundY;
    private boolean firstSurface = true;
    private boolean gameOverNotified;
    private boolean released;

    public GameView(Context context, GameDifficulty difficulty, boolean soundEnabled, Runnable onGameOver) {
        super(context);
        this.difficulty = difficulty == null ? GameDifficulty.EASY : difficulty;
        this.soundEnabled = soundEnabled;
        this.onGameOver = onGameOver;

        ImageManager.init(context.getApplicationContext());
        ImageManager.applyDifficultyBackground(this.difficulty);
        audio = new AudioManager(context, soundEnabled);
        game = createGame();

        holder = getHolder();
        holder.addCallback(this);
        setFocusable(true);
    }

    private Game createGame() {
        switch (difficulty) {
            case NORMAL:
                return new NormalGame(null, audio);
            case HARD:
                return new HardGame(null, audio);
            case EASY:
            default:
                return new EasyGame(null, audio);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder h) {
        if (firstSurface) {
            audio.playBgm();
            firstSurface = false;
        } else {
            audio.resumeBgm();
        }
        running = true;
        thread = new Thread(this, "aircraft-game");
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder h, int format, int w, int hgt) {
        synchronized (gameLock) {
            if (w <= 0 || hgt <= 0) {
                return;
            }
            int oldHeight = Main.WINDOW_HEIGHT;
            scale = w / (float) Main.WINDOW_WIDTH;
            Main.WINDOW_HEIGHT = Math.max(1, Math.round(hgt / scale));
            game.moveHero(game.getHero().getLocationX(),
                    Math.round(game.getHero().getLocationY() * (Main.WINDOW_HEIGHT / (float) oldHeight)));
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder h) {
        audio.pauseAll();
        running = false;
        if (thread != null) {
            boolean interrupted = false;
            while (thread.isAlive()) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    interrupted = true;
                }
            }
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
            thread = null;
        }
    }

    @Override
    public void run() {
        while (running) {
            long start = android.os.SystemClock.uptimeMillis();
            Canvas c = null;
            boolean notifyGameOver = false;
            try {
                c = holder.lockCanvas();
                if (c != null) {
                    synchronized (gameLock) {
                        game.tick();
                        drawGame(c);
                        if (game.isGameOver() && !gameOverNotified) {
                            gameOverNotified = true;
                            notifyGameOver = true;
                        }
                    }
                }
            } finally {
                if (c != null) {
                    holder.unlockCanvasAndPost(c);
                }
            }
            if (notifyGameOver && onGameOver != null) {
                onGameOver.run();
            }
            long delay = 40 - (android.os.SystemClock.uptimeMillis() - start);
            if (delay > 0) {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    private void drawGame(Canvas c) {
        c.drawColor(Color.BLACK);
        // Tile the background at its native aspect ratio, covering taller screens as needed.
        int tileHeight = Math.max(1, Math.round(c.getWidth()
                * (ImageManager.BACKGROUND_IMAGE.getHeight() / (float) ImageManager.BACKGROUND_IMAGE.getWidth())));
        int scroll = Math.round(backgroundY * scale) % tileHeight;
        for (int top = scroll - tileHeight; top < c.getHeight(); top += tileHeight) {
            c.drawBitmap(ImageManager.BACKGROUND_IMAGE, null,
                    new Rect(0, top, c.getWidth(), top + tileHeight), paint);
        }
        c.save();
        c.scale(scale, scale);
        backgroundY = (backgroundY + 1) % Math.max(1, Math.round(tileHeight / scale));

        drawObjects(c, game.getEnemyBullets());
        drawObjects(c, game.getHeroBullets());
        drawObjects(c, game.getEnemies());
        drawObjects(c, game.getSupplies());
        drawObject(c, game.getHero());

        paint.setColor(Color.RED);
        paint.setTextSize(22);
        paint.setFakeBoldText(true);
        c.drawText("SCORE: " + game.getScore(), 10, 25, paint);
        c.drawText("LIFE: " + game.getHp(), 10, 50, paint);
        paint.setTextSize(15);
        c.drawText("MODE: " + game.getDifficulty().name(), 10, 70, paint);

        if (game.isGameOver()) {
            paint.setTextSize(42);
            c.drawText("GAME OVER", 125, Main.WINDOW_HEIGHT / 2f, paint);
        }
        paint.setColor(Color.WHITE);
        paint.setFakeBoldText(false);
        c.restore();
    }

    private void drawObjects(Canvas c, List<? extends AbstractFlyingObject> objects) {
        for (AbstractFlyingObject o : objects) {
            drawObject(c, o);
        }
    }

    private void drawObject(Canvas c, AbstractFlyingObject o) {
        c.drawBitmap(o.getImage(), o.getLocationX() - o.getWidth() / 2f,
                o.getLocationY() - o.getHeight() / 2f, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN
                || event.getActionMasked() == MotionEvent.ACTION_MOVE) {
            synchronized (gameLock) {
                if (!game.isGameOver()) {
                    game.moveHero((int) (event.getX() / scale), (int) (event.getY() / scale));
                }
            }
            return true;
        }
        return true;
    }

    public void restart() {
        synchronized (gameLock) {
            game = createGame();
            backgroundY = 0;
            gameOverNotified = false;
        }
        audio.playBgm();
    }

    public int getScore() {
        synchronized (gameLock) {
            return game.getScore();
        }
    }

    public boolean isGameOver() {
        synchronized (gameLock) {
            return game.isGameOver();
        }
    }

    public void release() {
        if (released) {
            return;
        }
        released = true;
        running = false;
        Thread t = thread;
        if (t != null && t != Thread.currentThread()) {
            try {
                t.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            thread = null;
        }
        audio.release();
    }
}

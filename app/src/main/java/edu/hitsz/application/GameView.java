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

public class GameView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private final SurfaceHolder holder;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private volatile boolean running;
    private Thread thread;
    private final Object gameLock = new Object();
    private Game game;
    private float scale = 1;
    private int backgroundY;

    public GameView(Context context) {
        super(context);
        ImageManager.init(context.getApplicationContext());
        game = new EasyGame(null);
        holder = getHolder();
        holder.addCallback(this);
        setFocusable(true);
    }
    @Override public void surfaceCreated(SurfaceHolder h) {
        running = true;
        thread = new Thread(this, "aircraft-game");
        thread.start();
    }
    @Override public void surfaceChanged(SurfaceHolder h, int format, int w, int hgt) {
        synchronized (gameLock) {
            if (w <= 0 || hgt <= 0) return;
            int oldHeight = Main.WINDOW_HEIGHT;
            scale = w / (float) Main.WINDOW_WIDTH;
            Main.WINDOW_HEIGHT = Math.max(1, Math.round(hgt / scale));
            game.moveHero(game.getHero().getLocationX(),
                    Math.round(game.getHero().getLocationY() * (Main.WINDOW_HEIGHT / (float) oldHeight)));
        }
    }
    @Override public void surfaceDestroyed(SurfaceHolder h) {
        running = false;
        if (thread != null) {
            boolean interrupted = false;
            while (thread.isAlive()) {
                try { thread.join(); } catch (InterruptedException e) { interrupted = true; }
            }
            if (interrupted) Thread.currentThread().interrupt();
            thread = null;
        }
    }
    @Override public void run() {
        while (running) {
            long start = android.os.SystemClock.uptimeMillis();
            Canvas c = null;
            try {
                c = holder.lockCanvas();
                if (c != null) synchronized (gameLock) { game.tick(); drawGame(c); }
            } finally { if (c != null) holder.unlockCanvasAndPost(c); }
            long delay = 40 - (android.os.SystemClock.uptimeMillis() - start);
            if (delay > 0) try { Thread.sleep(delay); } catch (InterruptedException e) { Thread.currentThread().interrupt(); break; }
        }
    }
    private void drawGame(Canvas c) {
        c.drawColor(Color.BLACK);
        // Tile the background at its native aspect ratio, covering taller screens as needed.
        int tileHeight = Math.max(1, Math.round(c.getWidth() *
                (ImageManager.BACKGROUND_IMAGE.getHeight() / (float) ImageManager.BACKGROUND_IMAGE.getWidth())));
        int scroll = Math.round(backgroundY * scale) % tileHeight;
        for (int top = scroll - tileHeight; top < c.getHeight(); top += tileHeight) {
            c.drawBitmap(ImageManager.BACKGROUND_IMAGE, null,
                    new Rect(0, top, c.getWidth(), top + tileHeight), paint);
        }
        c.save(); c.scale(scale, scale);
        backgroundY = (backgroundY + 1) % Math.max(1, Math.round(tileHeight / scale));
        drawObjects(c, game.getEnemyBullets()); drawObjects(c, game.getHeroBullets());
        drawObjects(c, game.getEnemies()); drawObjects(c, game.getSupplies()); drawObject(c, game.getHero());
        paint.setColor(Color.RED); paint.setTextSize(22); paint.setFakeBoldText(true);
        c.drawText("SCORE: " + game.getScore(), 10, 25, paint);
        c.drawText("LIFE: " + game.getHp(), 10, 50, paint);
        if (game.isGameOver()) { paint.setTextSize(42); c.drawText("GAME OVER", 125, Main.WINDOW_HEIGHT / 2f, paint); paint.setTextSize(20); c.drawText("TAP TO RESTART", 166, Main.WINDOW_HEIGHT / 2f + 35, paint); }
        paint.setColor(Color.WHITE); paint.setFakeBoldText(false); c.restore();
    }
    private void drawObjects(Canvas c, List<? extends AbstractFlyingObject> objects) { for (AbstractFlyingObject o : objects) drawObject(c, o); }
    private void drawObject(Canvas c, AbstractFlyingObject o) {
        c.drawBitmap(o.getImage(), o.getLocationX() - o.getWidth()/2f, o.getLocationY() - o.getHeight()/2f, paint);
    }
    @Override public boolean onTouchEvent(MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            synchronized (gameLock) {
                if (game.isGameOver()) { game = new EasyGame(null); backgroundY = 0; return true; }
            }
        }
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN || event.getActionMasked() == MotionEvent.ACTION_MOVE) {
            synchronized (gameLock) { game.moveHero((int)(event.getX()/scale), (int)(event.getY()/scale)); }
            return true;
        }
        return true;
    }
}

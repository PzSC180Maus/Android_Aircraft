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
    private final Game game;
    private float offsetX, offsetY, scale = 1;
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
        synchronized (game) {
            scale = Math.min(w / 512f, hgt / 768f);
            offsetX = (w - 512 * scale) / 2;
            offsetY = (hgt - 768 * scale) / 2;
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
                if (c != null) synchronized (game) { game.tick(); drawGame(c); }
            } finally { if (c != null) holder.unlockCanvasAndPost(c); }
            long delay = 40 - (android.os.SystemClock.uptimeMillis() - start);
            if (delay > 0) try { Thread.sleep(delay); } catch (InterruptedException e) { Thread.currentThread().interrupt(); break; }
        }
    }
    private void drawGame(Canvas c) {
        c.drawColor(Color.BLACK);
        c.save(); c.translate(offsetX, offsetY); c.scale(scale, scale);
        c.drawBitmap(ImageManager.BACKGROUND_IMAGE, null, new Rect(0, backgroundY - 768, 512, backgroundY), paint);
        c.drawBitmap(ImageManager.BACKGROUND_IMAGE, null, new Rect(0, backgroundY, 512, backgroundY + 768), paint);
        backgroundY = (backgroundY + 1) % 768;
        drawObjects(c, game.getEnemyBullets()); drawObjects(c, game.getHeroBullets());
        drawObjects(c, game.getEnemies()); drawObjects(c, game.getSupplies()); drawObject(c, game.getHero());
        paint.setColor(Color.RED); paint.setTextSize(22); paint.setFakeBoldText(true);
        c.drawText("SCORE: " + game.getScore(), 10, 25, paint);
        c.drawText("LIFE: " + game.getHp(), 10, 50, paint);
        if (game.isGameOver()) { paint.setTextSize(42); c.drawText("GAME OVER", 125, 380, paint); }
        paint.setColor(Color.WHITE); paint.setFakeBoldText(false); c.restore();
    }
    private void drawObjects(Canvas c, List<? extends AbstractFlyingObject> objects) { for (AbstractFlyingObject o : objects) drawObject(c, o); }
    private void drawObject(Canvas c, AbstractFlyingObject o) {
        c.drawBitmap(o.getImage(), o.getLocationX() - o.getWidth()/2f, o.getLocationY() - o.getHeight()/2f, paint);
    }
    @Override public boolean onTouchEvent(MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN || event.getActionMasked() == MotionEvent.ACTION_MOVE) {
            synchronized (game) { game.moveHero((int)((event.getX()-offsetX)/scale), (int)((event.getY()-offsetY)/scale)); }
            return true;
        }
        return true;
    }
}

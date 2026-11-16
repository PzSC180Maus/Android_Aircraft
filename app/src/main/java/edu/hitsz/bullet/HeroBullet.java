package edu.hitsz.bullet;

/**
 * 英雄机子弹
 * @Author hitsz
 */
public class HeroBullet extends BaseBullet {

    public static final int DEFAULT_EXPLOSION_RADIUS = 60;
    public static final int DEFAULT_PROXIMITY_FUSE_RADIUS = 30;

    private final boolean explosive;
    private final int explosionRadius;
    private final int proximityFuseRadius;

    public HeroBullet(int locationX, int locationY, int speedX, int speedY, int power) {
        super(locationX, locationY, speedX, speedY, power);
        this.explosive = false;
        this.explosionRadius = 0;
        this.proximityFuseRadius = 0;
    }

    public HeroBullet(int locationX, int locationY, int speedX, int speedY, int power,
                      boolean explosive, int explosionRadius) {
        this(locationX, locationY, speedX, speedY, power, explosive, explosionRadius,
                explosive ? DEFAULT_PROXIMITY_FUSE_RADIUS : 0);
    }

    public HeroBullet(int locationX, int locationY, int speedX, int speedY, int power,
                      boolean explosive, int explosionRadius, int proximityFuseRadius) {
        super(locationX, locationY, speedX, speedY, power);
        this.explosive = explosive;
        this.explosionRadius = explosionRadius;
        this.proximityFuseRadius = proximityFuseRadius;
    }

    public boolean isExplosive() {
        return explosive;
    }

    public int getExplosionRadius() {
        return explosionRadius;
    }

    public int getProximityFuseRadius() {
        return proximityFuseRadius;
    }

}

package edu.hitsz.application;

import edu.hitsz.aircraft.AbstractEnemy;
import edu.hitsz.aircraft.BossEnemy;
import edu.hitsz.aircraft.ElitePlusEnemy;
import edu.hitsz.aircraft.EliteProEnemy;
import edu.hitsz.aircraft.HeroAircraft;

import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.HeroBullet;
import edu.hitsz.factory.enemy.BossEnemyFactory;
import edu.hitsz.factory.enemy.EliteEnemyFactory;
import edu.hitsz.factory.enemy.ElitePlusEnemyFactory;
import edu.hitsz.factory.enemy.EliteProEnemyFactory;
import edu.hitsz.factory.enemy.EnemyFactory;
import edu.hitsz.factory.enemy.MobEnemyFactory;
import edu.hitsz.factory.prop.PropFactory;
import edu.hitsz.factory.prop.PropType;
import edu.hitsz.observer.PropEffectObserver;
import edu.hitsz.prop.AbstractProp;
import edu.hitsz.prop.BombSupply;
import edu.hitsz.prop.FreezeSupply;
import edu.hitsz.rank.GameDifficulty;








import android.graphics.Bitmap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;




/**
 * Game template class. action() is template method for all difficulties.
 */
public abstract class Game  {

    private final int timeInterval = 40;
    private final Random random = new Random();

    protected final HeroAircraft heroAircraft;
    protected final List<AbstractEnemy> enemyAircrafts;
    protected final List<BaseBullet> heroBullets;
    protected final List<BaseBullet> enemyBullets;
    protected final List<AbstractProp> supplies;

    protected int enemyMaxNumber = 5;
    protected double enemySpawnCycle = 20;
    private int enemySpawnCounter = 0;

    protected double heroShootCycle = 20;
    protected double enemyShootCycle = 20;
    private int heroShootCounter = 0;
    private int enemyShootCounter = 0;
    protected double enemySpeedFactor = 1.0;
    protected double enemyHpFactor = 1.0;
    private static final long APS_COOLDOWN_MS = 5000;
    private long apsCooldownUntilMillis = 0;

    private static final int SUPPLY_SPEED_Y = 5;
    private static final int BOSS_SUPPLY_DROP_COUNT = 3;
    private static final int BASE_BOSS_HP = 180;

    private static final double ELITE_PLUS_SUPPLY_DROP_PROBABILITY = 0.35;
    private static final double ELITE_PRO_SUPPLY_DROP_PROBABILITY = 0.45;

    private static final PropType[] ELITE_PLUS_DROP_TYPES = {
            PropType.BLOOD,
            PropType.FIRE,
            PropType.FIRE_PLUS,
            PropType.BOMB
    };

    private static final PropType[] ELITE_PRO_DROP_TYPES = {
            PropType.BLOOD,
            PropType.FIRE,
            PropType.FIRE_PLUS,
            PropType.BOMB,
            PropType.FREEZE,
            PropType.FREEZE,
            PropType.FREEZE
    };

    private static final PropType[] ALL_DROP_TYPES = {
            PropType.BLOOD,
            PropType.FIRE,
            PropType.FIRE_PLUS,
            PropType.BOMB,
            PropType.FREEZE,
            PropType.FREEZE
    };

    protected int score = 0;
    private boolean gameOverFlag = false;
    private final GameDifficulty difficulty;
    private final Runnable gameOverCallback;

    private long battleTimeMillis = 0;
    private int nextBossTriggerScore;
    private long freezeBulletUntilMillis = 0L;

    protected Game(GameDifficulty difficulty, Runnable gameOverCallback, int bossTriggerScoreStep) {
        this.difficulty = difficulty;
        this.gameOverCallback = gameOverCallback;
        this.nextBossTriggerScore = bossTriggerScoreStep;

        heroAircraft = HeroAircraft.getInstance(
                Main.WINDOW_WIDTH / 2,
                Main.WINDOW_HEIGHT - ImageManager.HERO_IMAGE.getHeight(),
                0, 0, 100
        );
        heroAircraft.resetForNewGame(
                Main.WINDOW_WIDTH / 2,
                Main.WINDOW_HEIGHT - ImageManager.HERO_IMAGE.getHeight(),
                100
        );

        enemyAircrafts = new LinkedList<>();
        heroBullets = new LinkedList<>();
        enemyBullets = new LinkedList<>();
        supplies = new LinkedList<>();

    }

    public void tick() {
        if (gameOverFlag) return;
        battleTimeMillis += timeInterval;
        spawnEnemyAction(); shootAction(); bulletsMoveAction(); aircraftsMoveAction();
        suppliesMoveAction(); crashCheckAction(); postProcessAction();
        heroAircraft.recoverShootStrategyIfNeeded(); apsDefenseAction();
        onDifficultyProgress(battleTimeMillis); checkResultAction();
    }

    public static int clamp(int value, int min, int max) { return Math.max(min, Math.min(max, value)); }
    public void moveHero(int x, int y) {
        heroAircraft.setLocation(clamp(x, heroAircraft.getWidth()/2, Main.WINDOW_WIDTH-heroAircraft.getWidth()/2),
            clamp(y, heroAircraft.getHeight()/2, Main.WINDOW_HEIGHT-heroAircraft.getHeight()/2));
    }
    public boolean isGameOver() { return gameOverFlag; }
    public int getHp() { return heroAircraft.getHp(); }
    public HeroAircraft getHero() { return heroAircraft; }
    public List<AbstractEnemy> getEnemies() { return enemyAircrafts; }
    public List<BaseBullet> getHeroBullets() { return heroBullets; }
    public List<BaseBullet> getEnemyBullets() { return enemyBullets; }
    public List<AbstractProp> getSupplies() { return supplies; }

    protected void onGameStart() {
        // hook
    }

    protected abstract void onDifficultyProgress(long battleTimeMillis);

    protected abstract boolean canSpawnBoss();

    protected abstract int resolveBossHp(int baseBossHp);

    protected abstract int nextBossTriggerScoreStep();

    private void spawnEnemyAction() {
        enemySpawnCounter++;
        if (enemySpawnCounter >= enemySpawnCycle) {
            enemySpawnCounter = 0;
            if (enemyAircrafts.size() < enemyMaxNumber) {
                enemyAircrafts.add(createRandomEnemy());
            }
        }

        if (canSpawnBoss() && score >= nextBossTriggerScore && !hasBossAlive()) {
            enemyAircrafts.add(createBossEnemy());

            nextBossTriggerScore += nextBossTriggerScoreStep();
        }
    }

    private boolean hasBossAlive() {
        for (AbstractEnemy enemy : enemyAircrafts) {
            if (!enemy.notValid() && enemy instanceof BossEnemy) {
                return true;
            }
        }
        return false;
    }

    private AbstractEnemy createBossEnemy() {
        int bossSpeedX = scaleSpeed(2);
        EnemyFactory bossFactory = new BossEnemyFactory(
                Main.WINDOW_WIDTH / 2,
                (int) (Main.WINDOW_HEIGHT * 0.12),
                random.nextBoolean() ? bossSpeedX : -bossSpeedX,
                0,
                resolveBossHp(BASE_BOSS_HP)
        );
        return bossFactory.createEnemy();
    }

    private AbstractEnemy createRandomEnemy() {
        EnemyFactory enemyFactory = createRandomEnemyFactory();
        return enemyFactory.createEnemy();
    }

    private EnemyFactory createRandomEnemyFactory() {
        int enemyType = random.nextInt(4);
        if (enemyType == 0) {
            return new MobEnemyFactory(
                    createEnemyLocationX(ImageManager.MOB_ENEMY_IMAGE),
                    createEnemyLocationY(),
                    0,
                    scaleSpeed(8),
                    scaleHp(20)
            );
        }
        if (enemyType == 1) {
            return new EliteEnemyFactory(
                    createEnemyLocationX(ImageManager.ELITE_ENEMY_IMAGE),
                    createEnemyLocationY(),
                    0,
                    scaleSpeed(6),
                    scaleHp(40)
            );
        }
        if (enemyType == 2) {
            return new ElitePlusEnemyFactory(
                    createEnemyLocationX(ImageManager.ELITE_PLUS_ENEMY_IMAGE),
                    createEnemyLocationY(),
                    randomHorizontalSpeed(scaleSpeed(2)),
                    scaleSpeed(5),
                    scaleHp(55)
            );
        }

        return new EliteProEnemyFactory(
                createEnemyLocationX(ImageManager.ELITE_PRO_ENEMY_IMAGE),
                createEnemyLocationY(),
                randomHorizontalSpeed(scaleSpeed(2)),
                scaleSpeed(4),
                scaleHp(70)
        );
    }

    private int randomHorizontalSpeed(int absSpeed) {
        int speed = Math.max(1, absSpeed);
        return random.nextBoolean() ? speed : -speed;
    }

    private int scaleSpeed(int baseSpeed) {
        return Math.max(1, (int) Math.round(baseSpeed * enemySpeedFactor));
    }

    private int scaleHp(int baseHp) {
        return Math.max(1, (int) Math.round(baseHp * enemyHpFactor));
    }

    private int createEnemyLocationX(Bitmap enemyImage) {
        int halfWidth = enemyImage.getWidth() / 2;
        int range = Main.WINDOW_WIDTH - enemyImage.getWidth();
        return random.nextInt(Math.max(1, range + 1)) + halfWidth;
    }

    private int createEnemyLocationY() {
        int upperBound = (int) (Main.WINDOW_HEIGHT * 0.05);
        return random.nextInt(Math.max(1, upperBound + 1));
    }

    private void shootAction() {
        heroShootCounter++;
        if (heroShootCounter >= heroShootCycle) {
            heroShootCounter = 0;
            heroBullets.addAll(heroAircraft.shoot());
        }

        enemyShootCounter++;
        if (enemyShootCounter >= enemyShootCycle) {
            enemyShootCounter = 0;
            for (AbstractEnemy enemyAircraft : enemyAircrafts) {
                enemyBullets.addAll(enemyAircraft.shoot());
            }
        }
    }

    private void bulletsMoveAction() {
        for (BaseBullet bullet : heroBullets) {
            bullet.forward();
        }
        boolean enemyBulletsFrozen = System.currentTimeMillis() < freezeBulletUntilMillis;
        if (!enemyBulletsFrozen) {
            for (BaseBullet bullet : enemyBullets) {
                bullet.forward();
            }
        }
    }

    private void aircraftsMoveAction() {
        for (AbstractEnemy enemyAircraft : enemyAircrafts) {
            enemyAircraft.forward();
        }
    }

    private void suppliesMoveAction() {
        for (AbstractProp supply : supplies) {
            supply.forward();
        }
    }

    private void crashCheckAction() {
        for (BaseBullet enemyBullet : enemyBullets) {
            if (enemyBullet.notValid()) {
                continue;
            }
            if (heroAircraft.crash(enemyBullet)) {
                heroAircraft.decreaseHp(enemyBullet.getPower());
                enemyBullet.vanish();
            }
        }

        for (BaseBullet bullet : heroBullets) {
            if (bullet.notValid()) {
                continue;
            }
            for (AbstractEnemy enemyAircraft : enemyAircrafts) {
                if (enemyAircraft.notValid()) {
                    continue;
                }
                if (enemyAircraft.crash(bullet)) {
                    enemyAircraft.decreaseHp(bullet.getPower());
                    bullet.vanish();

                    if (bullet instanceof HeroBullet) {
                        detonateExplosive((HeroBullet) bullet);
                    }

                    if (enemyAircraft.notValid()) {
                        score += enemyAircraft.getScore();
                        maybeGenerateSupply(enemyAircraft);
                        if (enemyAircraft instanceof BossEnemy) {

                        }
                    }
                    break;
                }
            }
        }

        for (AbstractEnemy enemyAircraft : enemyAircrafts) {
            if (enemyAircraft.notValid()) {
                continue;
            }
            if (enemyAircraft.crash(heroAircraft) || heroAircraft.crash(enemyAircraft)) {
                enemyAircraft.vanish();
                heroAircraft.decreaseHp(Integer.MAX_VALUE);
            }
        }

        for (AbstractProp supply : supplies) {
            if (supply.notValid()) {
                continue;
            }
            if (heroAircraft.crash(supply) || supply.crash(heroAircraft)) {
                supply.activate(heroAircraft);
                supply.vanish();
                if (supply instanceof BombSupply) {
                    triggerBombEffect((BombSupply) supply);

                } else if (supply instanceof FreezeSupply) {
                    triggerFreezeEffect((FreezeSupply) supply, 4000);

                } else {

                }
            }
        }
    }

    private void applyExplosiveDamageAndInterception(HeroBullet heroBullet) {
        detonateExplosive(heroBullet);
    }

    private void detonateExplosive(HeroBullet heroBullet) {
        if (!heroBullet.isExplosive()) {
            return;
        }
        int centerX = heroBullet.getLocationX();
        int centerY = heroBullet.getLocationY();
        int radius = heroBullet.getExplosionRadius();
        int radiusSq = radius * radius;

        for (AbstractEnemy enemy : enemyAircrafts) {
            if (enemy.notValid()) {
                continue;
            }
            int dx = enemy.getLocationX() - centerX;
            int dy = enemy.getLocationY() - centerY;
            if (dx * dx + dy * dy <= radiusSq) {
                enemy.decreaseHp(heroBullet.getPower());
                if (enemy.notValid()) {
                    score += enemy.getScore();
                    maybeGenerateSupply(enemy);
                    if (enemy instanceof BossEnemy) {

                    }
                }
            }
        }

        for (BaseBullet enemyBullet : enemyBullets) {
            if (enemyBullet.notValid()) {
                continue;
            }
            int dx = enemyBullet.getLocationX() - centerX;
            int dy = enemyBullet.getLocationY() - centerY;
            if (dx * dx + dy * dy <= radiusSq) {
                enemyBullet.vanish();
            }
        }
    }

    private void triggerBombEffect(BombSupply bombSupply) {
        // Bomb effect on enemy bullets: disappear immediately.
        for (BaseBullet enemyBullet : enemyBullets) {
            if (!enemyBullet.notValid()) {
                enemyBullet.vanish();
            }
        }

        List<AbstractEnemy> aliveBeforeTrigger = new LinkedList<>();
        for (AbstractEnemy enemy : enemyAircrafts) {
            if (!enemy.notValid()) {
                aliveBeforeTrigger.add(enemy);
                bombSupply.addObserver(enemy);
            }
        }
        bombSupply.triggerBomb();
        bombSupply.clearObservers();

        // Bomb rule: gain score for all enemies crashed by bomb, but no prop drops.
        for (AbstractEnemy enemy : aliveBeforeTrigger) {
            if (enemy.notValid() && !(enemy instanceof BossEnemy)) {
                score += enemy.getScore();
            }
        }
    }

    private void triggerFreezeEffect(FreezeSupply freezeSupply, long durationMillis) {
        // Freeze effect on enemy bullets: stop for 5 seconds then recover.
        freezeSupply.setDurationMillis(5000);
        freezeBulletUntilMillis = System.currentTimeMillis() + 5000;
        for (AbstractEnemy enemy : enemyAircrafts) {
            if (!enemy.notValid()) {
                freezeSupply.addObserver(enemy);
            }
        }
        freezeSupply.triggerFreeze();
        freezeSupply.clearObservers();
    }

    private void maybeGenerateSupply(AbstractEnemy enemyAircraft) {
        if (!enemyAircraft.canDropSupply()) {
            return;
        }

        if (enemyAircraft instanceof BossEnemy) {
            for (int i = 0; i < BOSS_SUPPLY_DROP_COUNT; i++) {
                PropType propType = ALL_DROP_TYPES[random.nextInt(ALL_DROP_TYPES.length)];
                supplies.add(PropFactory.createProp(
                        propType,
                        enemyAircraft.getLocationX() + (i - 1) * 20,
                        enemyAircraft.getLocationY(),
                        0,
                        SUPPLY_SPEED_Y
                ));
            }

            return;
        }

        PropType[] dropTypes;
        double dropProbability;

        if (enemyAircraft instanceof ElitePlusEnemy) {
            dropTypes = ELITE_PLUS_DROP_TYPES;
            dropProbability = ELITE_PLUS_SUPPLY_DROP_PROBABILITY;
        } else if (enemyAircraft instanceof EliteProEnemy) {
            dropTypes = ELITE_PRO_DROP_TYPES;
            dropProbability = ELITE_PRO_SUPPLY_DROP_PROBABILITY;
        } else {
            return;
        }

        if (random.nextDouble() >= dropProbability) {
            return;
        }

        PropType propType = dropTypes[random.nextInt(dropTypes.length)];
        AbstractProp supply = PropFactory.createProp(
                propType,
                enemyAircraft.getLocationX(),
                enemyAircraft.getLocationY(),
                0,
                SUPPLY_SPEED_Y
        );
        supplies.add(supply);
    }

    private void postProcessAction() {
        enemyBullets.removeIf(AbstractFlyingObject::notValid);
        heroBullets.removeIf(AbstractFlyingObject::notValid);
        enemyAircrafts.removeIf(AbstractFlyingObject::notValid);
        supplies.removeIf(AbstractFlyingObject::notValid);
    }

    /**
     * Active protection system (APS):
     * trigger when bullets are detected in a 90-degree sector ahead of hero,
     * then enter 5-second cooldown.
     */
    private void apsDefenseAction() {
        long now = System.currentTimeMillis();
        if (now < apsCooldownUntilMillis) {
            return;
        }
        int heroX = heroAircraft.getLocationX();
        int heroY = heroAircraft.getLocationY();
        boolean hasTarget = false;

        for (BaseBullet bullet : enemyBullets) {
            if (bullet.notValid()) {
                continue;
            }
            int dx = bullet.getLocationX() - heroX;
            int dy = bullet.getLocationY() - heroY;
            if (dy < 0 && Math.abs(dx) <= -dy) {
                hasTarget = true;
                break;
            }
        }

        if (!hasTarget) {
            return;
        }

        for (BaseBullet bullet : enemyBullets) {
            if (bullet.notValid()) {
                continue;
            }
            int dx = bullet.getLocationX() - heroX;
            int dy = bullet.getLocationY() - heroY;
            if (dy < 0 && Math.abs(dx) <= -dy) {
                bullet.vanish();
            }
        }
        apsCooldownUntilMillis = now + APS_COOLDOWN_MS;
    }

    private void checkResultAction() {
        if (heroAircraft.getHp() <= 0 && !gameOverFlag) {
            gameOverFlag = true;
            if (gameOverCallback != null) gameOverCallback.run();
        }
    }

    public GameDifficulty getDifficulty() {
        return difficulty;
    }

    public int getScore() {
        return score;
    }

    public int getExplosiveChainRemainingShots() {
        return heroAircraft.getExplosiveChainRemainingShots();
    }

    public long getApsCooldownRemainingMillis() {
        long remain = apsCooldownUntilMillis - System.currentTimeMillis();
        return Math.max(0, remain);
    }

}
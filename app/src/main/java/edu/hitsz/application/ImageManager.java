package edu.hitsz.application;

import edu.hitsz.aircraft.*;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;
import edu.hitsz.prop.*;
import edu.hitsz.rank.GameDifficulty;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.example.aircraft_android.R;



import java.util.HashMap;
import java.util.Map;

/**
 * Loads and provides image resources.
 */
public class ImageManager {

    private static final Map<String, Bitmap> CLASSNAME_IMAGE_MAP = new HashMap<>();

    public static Bitmap BACKGROUND_IMAGE;
    public static Bitmap HERO_IMAGE;
    public static Bitmap HERO_BULLET_IMAGE;
    public static Bitmap ENEMY_BULLET_IMAGE;

    public static Bitmap MOB_ENEMY_IMAGE;
    public static Bitmap ELITE_ENEMY_IMAGE;
    public static Bitmap ELITE_PRO_ENEMY_IMAGE;
    public static Bitmap ELITE_PLUS_ENEMY_IMAGE;
    public static Bitmap BOSS_ENEMY_IMAGE;

    public static Bitmap BLOOD_SUPPLY_IMAGE;
    public static Bitmap FIRE_SUPPLY_IMAGE;
    public static Bitmap FIRE_PLUS_SUPPLY_IMAGE;
    public static Bitmap BOMB_SUPPLY_IMAGE;
    public static Bitmap FREEZE_SUPPLY_IMAGE;
    public static Bitmap BACKGROUND_IMAGE_EASY;
    public static Bitmap BACKGROUND_IMAGE_NORMAL;
    public static Bitmap BACKGROUND_IMAGE_HARD;

    public static void init(Context context) {
        {
            BACKGROUND_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.bg);
            BACKGROUND_IMAGE_EASY = BitmapFactory.decodeResource(context.getResources(), R.drawable.bg);
            BACKGROUND_IMAGE_NORMAL = BitmapFactory.decodeResource(context.getResources(), R.drawable.bg2);
            BACKGROUND_IMAGE_HARD = BitmapFactory.decodeResource(context.getResources(), R.drawable.bg3);

            HERO_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.hero);
            MOB_ENEMY_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.mob);
            ELITE_ENEMY_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.elite);
            ELITE_PLUS_ENEMY_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.eliteplus);
            ELITE_PRO_ENEMY_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.elitepro);
            BOSS_ENEMY_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.boss);

            HERO_BULLET_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.bullet_hero);
            ENEMY_BULLET_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.bullet_enemy);

            BLOOD_SUPPLY_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.prop_blood);
            FIRE_SUPPLY_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.prop_bullet);
            FIRE_PLUS_SUPPLY_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.prop_bulletplus);
            BOMB_SUPPLY_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.prop_bomb);
            FREEZE_SUPPLY_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.prop_freeze);

            CLASSNAME_IMAGE_MAP.put(HeroAircraft.class.getName(), HERO_IMAGE);

            CLASSNAME_IMAGE_MAP.put(MobEnemy.class.getName(), MOB_ENEMY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(EliteEnemy.class.getName(), ELITE_ENEMY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(EliteProEnemy.class.getName(), ELITE_PRO_ENEMY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(ElitePlusEnemy.class.getName(), ELITE_PLUS_ENEMY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(BossEnemy.class.getName(), BOSS_ENEMY_IMAGE);

            CLASSNAME_IMAGE_MAP.put(HeroBullet.class.getName(), HERO_BULLET_IMAGE);
            CLASSNAME_IMAGE_MAP.put(EnemyBullet.class.getName(), ENEMY_BULLET_IMAGE);

            CLASSNAME_IMAGE_MAP.put(BloodSupply.class.getName(), BLOOD_SUPPLY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(FireSupply.class.getName(), FIRE_SUPPLY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(FirePlusSupply.class.getName(), FIRE_PLUS_SUPPLY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(BombSupply.class.getName(), BOMB_SUPPLY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(FreezeSupply.class.getName(), FREEZE_SUPPLY_IMAGE);

        }
    }

    public static Bitmap get(String className) {
        return CLASSNAME_IMAGE_MAP.get(className);
    }

    public static Bitmap get(Object obj) {
        if (obj == null) {
            return null;
        }
        return get(obj.getClass().getName());
    }

    public static void applyDifficultyBackground(GameDifficulty difficulty) {
        if (difficulty == null) {
            BACKGROUND_IMAGE = BACKGROUND_IMAGE_NORMAL;
            return;
        }
        switch (difficulty) {
            case EASY:
                BACKGROUND_IMAGE = BACKGROUND_IMAGE_EASY;
                break;
            case HARD:
                BACKGROUND_IMAGE = BACKGROUND_IMAGE_HARD;
                break;
            case NORMAL:
            default:
                BACKGROUND_IMAGE = BACKGROUND_IMAGE_NORMAL;
                break;
        }
    }
}

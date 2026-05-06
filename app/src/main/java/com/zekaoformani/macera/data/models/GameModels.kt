package com.zekaoformani.macera.data.models

import com.zekaoformani.macera.R
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

enum class PlayStyle {
    AIR, GROUND, MIXED
}

data class HeroData(
    val id: Int,
    @StringRes val nameRes: Int,
    @StringRes val descRes: Int,
    @DrawableRes val imageRes: Int,
    @DrawableRes val arcadeBadgeRes: Int,
    val speedStars: Int,
    val jumpStars: Int,
    val durabilityStars: Int,
    val initialLives: Int = 3
)

val characters = listOf(
    // 🦊 SWIFT FOX
    HeroData(1, R.string.char_fox_name, R.string.char_fox_desc, R.drawable.fox, R.drawable.fox, 4, 3, 2, 3),

    // 🐒 CRAZY MONKEY
    HeroData(2, R.string.char_monkey_name, R.string.char_monkey_desc, R.drawable.monkey, R.drawable.monkey, 5, 5, 1, 3),

    // 🐯 WILD TIGER
    HeroData(3, R.string.char_tiger_name, R.string.char_tiger_desc, R.drawable.tigger, R.drawable.tigger, 4, 2, 5, 3)
)


enum class WorldType {
    FOREST, BEACH, SKY, CRYSTAL
}

fun WorldType.arcadeIconRes(): Int = when (this) {
    WorldType.FOREST, WorldType.SKY -> R.drawable.ic_arcade_flappy
    WorldType.BEACH, WorldType.CRYSTAL -> R.drawable.ic_arcade_angry
}

enum class CollectibleType {
    ACORN, SHELL, FEATHER, CRYSTAL
}

data class StarTargets(
    val star1: Int,
    val star2: Int,
    val star3: Int
)

data class LevelConfig(
    val id: Int,
    val world: WorldType,
    @StringRes val nameRes: Int,
    val playStyle: PlayStyle = PlayStyle.AIR,
    val speedScale: Float = 1.0f,
    val gapSizeScale: Float = 1.0f, // 1.0 = normal, < 1.0 = harder (smaller gap)
    val backgroundRes: Int,
    val collectibleType: CollectibleType = CollectibleType.ACORN,
    val starTargets: StarTargets = StarTargets(5, 10, 15),
    val totalDistance: Float = 8000f,
    val obstacleFrequency: Int = 1800 // In pixels/units between spawns
)

val levels = listOf(
    // 🌲 DÜNYA 1: ORMAN
    LevelConfig(1, WorldType.FOREST, R.string.level_1_name, PlayStyle.AIR, 1.0f, 1.15f, R.drawable.level1_bg_jungle, CollectibleType.ACORN, StarTargets(4, 7, 10), 5000f),
    LevelConfig(2, WorldType.FOREST, R.string.level_2_name, PlayStyle.GROUND, 1.05f, 1.0f, R.drawable.level2_bg_jungle, CollectibleType.ACORN, StarTargets(5, 9, 13), 6500f),
    LevelConfig(3, WorldType.FOREST, R.string.level_3_name, PlayStyle.MIXED, 1.12f, 0.95f, R.drawable.level3_bg_jungle, CollectibleType.ACORN, StarTargets(6, 10, 15), 7800f),


    // 🏖️ DÜNYA 2: KUMSAL
    LevelConfig(4, WorldType.BEACH, R.string.level_4_name, PlayStyle.GROUND, 1.18f, 0.92f, R.drawable.level4_gold_beach, CollectibleType.SHELL, StarTargets(6, 11, 16), 8800f),
    LevelConfig(5, WorldType.BEACH, R.string.level_5_name, PlayStyle.MIXED, 1.28f, 0.86f, R.drawable.level5_cave_beach, CollectibleType.SHELL, StarTargets(7, 12, 18), 10200f),
    
    // ☁️ DÜNYA 3: GÖKYÜZÜ
    LevelConfig(6, WorldType.SKY, R.string.level_6_name, PlayStyle.AIR, 1.36f, 0.82f, R.drawable.level6_advanture_sky, CollectibleType.FEATHER, StarTargets(7, 13, 20), 11200f),
    LevelConfig(7, WorldType.SKY, R.string.level_7_name, PlayStyle.AIR, 1.48f, 0.76f, R.drawable.level7_storm_sky, CollectibleType.FEATHER, StarTargets(8, 14, 22), 12800f),
    
    // 💎 DÜNYA 4: KRİSTAL MAĞARA
    LevelConfig(8, WorldType.CRYSTAL, R.string.level_8_name, PlayStyle.GROUND, 1.55f, 0.72f, R.drawable.level8_cyristal_cave, CollectibleType.CRYSTAL, StarTargets(8, 15, 24), 14200f),
    LevelConfig(9, WorldType.CRYSTAL, R.string.level_9_name, PlayStyle.MIXED, 1.68f, 0.66f, R.drawable.level9_cyristal_galery, CollectibleType.CRYSTAL, StarTargets(9, 16, 26), 15600f),
    LevelConfig(10, WorldType.CRYSTAL, R.string.level_10_name, PlayStyle.MIXED, 1.82f, 0.62f, R.drawable.level10_colors_hearth, CollectibleType.CRYSTAL, StarTargets(10, 18, 30), 18200f)
)

enum class GameState {
    READY, PLAYING, GAME_OVER, LEVEL_COMPLETE
}

enum class ObstacleType {
    ROCK, LOG, THORNS, STALACTITE, CRAB, BEETLE
}

data class GameObject(
    val x: Float,
    var y: Float,
    val width: Float,
    val height: Float,
    val isFruit: Boolean,
    val isScoreBonus: Boolean = false,
    var isFalling: Boolean = false,
    val type: ObstacleType = ObstacleType.ROCK,
    var velocityY: Float = 0f,
    var velocityX: Float = 0f,
    val initialY: Float = y,
    val initialX: Float = x,
    val moveRange: Float = 0f
)

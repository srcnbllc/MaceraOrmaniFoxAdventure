package com.zekaoformani.macera.data.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.zekaoformani.macera.R

/**
 * Oyundaki seçilebilir karakterlerin temel yetenek ve görsel referanslarını tutar.
 */
data class HeroData(
    val id: Int,
    @field:StringRes val nameRes: Int,
    @field:StringRes val descRes: Int,
    @field:DrawableRes val imageRes: Int,
    @field:DrawableRes val arcadeBadgeRes: Int,
    val speedStars: Int,
    val jumpStars: Int,
    val durabilityStars: Int,
    val initialLives: Int = 3
)

/**
 * Oyunda aktif olarak kullanılan kahramanların ana listesi.
 */
val characters = listOf(
    // 🦊 SWIFT FOX
    HeroData(
        id = 1,
        nameRes = R.string.char_fox_name,
        descRes = R.string.char_fox_desc,
        imageRes = R.drawable.fox,
        arcadeBadgeRes = R.drawable.fox,
        speedStars = 4,
        jumpStars = 3,
        durabilityStars = 2,
        initialLives = 3
    ),

    // 🐒 CRAZY MONKEY
    HeroData(
        id = 2,
        nameRes = R.string.char_monkey_name,
        descRes = R.string.char_monkey_desc,
        imageRes = R.drawable.monkey,
        arcadeBadgeRes = R.drawable.monkey,
        speedStars = 5,
        jumpStars = 5,
        durabilityStars = 1,
        initialLives = 3
    ),

    // 🐯 WILD TIGER
    HeroData(
        id = 3,
        nameRes = R.string.char_tiger_name,
        descRes = R.string.char_tiger_desc,
        imageRes = R.drawable.tigger,
        arcadeBadgeRes = R.drawable.tigger,
        speedStars = 4,
        jumpStars = 2,
        durabilityStars = 5,
        initialLives = 3
    )
)
package com.zekaoformani.macera.data.models

import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import maceraormanifoxadventure.shared.generated.resources.Res
import maceraormanifoxadventure.shared.generated.resources.*

/**
 * Oyundaki seçilebilir karakterlerin temel yetenek ve görsel referanslarını tutar.
 */
data class HeroData(
    val id: Int,
    val nameRes: StringResource,
    val descRes: StringResource,
    val imageRes: DrawableResource,
    val arcadeBadgeRes: DrawableResource,
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
        nameRes = Res.string.char_fox_name,
        descRes = Res.string.char_fox_desc,
        imageRes = Res.drawable.fox,
        arcadeBadgeRes = Res.drawable.fox,
        speedStars = 4,
        jumpStars = 3,
        durabilityStars = 2,
        initialLives = 3
    ),

    // 🐒 CRAZY MONKEY
    HeroData(
        id = 2,
        nameRes = Res.string.char_monkey_name,
        descRes = Res.string.char_monkey_desc,
        imageRes = Res.drawable.monkey,
        arcadeBadgeRes = Res.drawable.monkey,
        speedStars = 5,
        jumpStars = 5,
        durabilityStars = 1,
        initialLives = 3
    ),

    // 🐯 WILD TIGER
    HeroData(
        id = 3,
        nameRes = Res.string.char_tiger_name,
        descRes = Res.string.char_tiger_desc,
        imageRes = Res.drawable.tigger,
        arcadeBadgeRes = Res.drawable.tigger,
        speedStars = 4,
        jumpStars = 2,
        durabilityStars = 5,
        initialLives = 3
    )
)

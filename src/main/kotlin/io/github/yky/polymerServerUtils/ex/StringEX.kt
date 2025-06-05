package io.github.yky.polymerServerUtils.ex

import io.github.yky.polymerServerUtils.Utils
import net.minecraft.util.Identifier

fun String.toModId(): Identifier = Identifier.of(Utils.MOD_ID, this)
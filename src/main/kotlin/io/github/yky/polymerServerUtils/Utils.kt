package io.github.yky.polymerServerUtils

import eu.pb4.polymer.core.api.item.PolymerItemUtils
import io.github.yky.polymerServerUtils.ex.toModId
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import net.minecraft.world.explosion.Explosion
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Utils {
    const val MOD_ID: String = "polymer-server-utils"

    @JvmField
    val Logger: Logger = LoggerFactory.getLogger("PolymerServerUtils")

    private val isCuddlyItemId = "is_cuddly_item".toModId()

    @JvmStatic
    fun isCuddlyItem(itemStack: ItemStack): Boolean {
        val components = PolymerItemUtils.getPolymerComponents(itemStack) ?: return false
        for ((key) in components) if (key == isCuddlyItemId) return true
        return false
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun explosion(world: World, attacker: LivingEntity, x: Double, y: Double, z: Double, power: Float = 50f) {
        world.createExplosion(
            attacker,
            Explosion.createDamageSource(world, attacker),
            null,
            x,
            y,
            z,
            power,
            true,
            World.ExplosionSourceType.MOB
        )
    }

    fun explosion(world: World, attacker: LivingEntity, target: LivingEntity, power: Float = 50f) =
        explosion(world, attacker, target.x, target.y, target.z, power)

    @Suppress("MemberVisibilityCanBePrivate")
    fun explosion(world: World, attacker: LivingEntity, x: Int, y: Int, z: Int, power: Float = 50f) =
        explosion(world, attacker, x.toDouble(), y.toDouble(), z.toDouble(), power)

    @Suppress("MemberVisibilityCanBePrivate", "unused")
    fun explosion(world: World, attacker: LivingEntity, x: Float, y: Float, z: Float, power: Float = 50f) =
        explosion(world, attacker, x.toDouble(), y.toDouble(), z.toDouble(), power)

    fun explosion(world: World, attacker: LivingEntity, hitPos: Vec3d, power: Float = 50f) =
        explosion(world, attacker, hitPos.x, hitPos.y, hitPos.z, power)

    fun explosion(world: World, attacker: LivingEntity, hitPos: BlockPos, power: Float = 50f) =
        explosion(world, attacker, hitPos.x, hitPos.y, hitPos.z, power)
}
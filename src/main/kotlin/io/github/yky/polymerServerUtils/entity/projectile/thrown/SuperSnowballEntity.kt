package io.github.yky.polymerServerUtils.entity.projectile.thrown

import eu.pb4.polymer.core.api.entity.PolymerEntity
import io.github.yky.polymerServerUtils.item.Items
import net.minecraft.entity.EntityStatuses
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.thrown.ThrownItemEntity
import net.minecraft.item.ItemStack
import net.minecraft.particle.ItemStackParticleEffect
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.world.World
import xyz.nucleoid.packettweaker.PacketContext

class SuperSnowballEntity : ThrownItemEntity, PolymerEntity {
    constructor(entityType: EntityType<out SuperSnowballEntity?>, world: World?) : super(entityType, world)

    constructor(world: World?, owner: LivingEntity, stack: ItemStack?) : super(EntityType.SNOWBALL, owner, world, stack)

    constructor(world: World?, x: Double, y: Double, z: Double, stack: ItemStack?) : super(
        EntityType.SNOWBALL, x, y, z, world, stack
    )

    override fun getDefaultItem() = Items.SUPER_SNOWBALL.item

    private val particleParameters: ParticleEffect
        get() {
            val itemStack = stack
            return (if (itemStack.isEmpty) ParticleTypes.ITEM_SNOWBALL else ItemStackParticleEffect(
                ParticleTypes.ITEM, itemStack
            )) as ParticleEffect
        }

    override fun handleStatus(status: Byte) {
        if (status == EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES) {
            val particleEffect = particleParameters

            for (i in 0..7) {
                world.addParticleClient(particleEffect, x, y, z, 0.0, 0.0, 0.0)
            }
        }
    }

    override fun onEntityHit(entityHitResult: EntityHitResult) {
        super.onEntityHit(entityHitResult)
        if (world !is ServerWorld) return
        val serverWorld = world as ServerWorld
        val entity = entityHitResult.entity
        entity.damage(serverWorld, damageSources.thrown(this, owner), 5f)
    }

    override fun onCollision(hitResult: HitResult) {
        super.onCollision(hitResult)
        if (world.isClient) return
        world.sendEntityStatus(this, EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES)
        discard()
    }

    /**
     * This method is used to determine what this entity will look like on client for specific player
     *
     * @return Vanilla/Modded entity type
     */
    override fun getPolymerEntityType(context: PacketContext?): EntityType<*> {
        return EntityType.SNOWBALL
    }
}

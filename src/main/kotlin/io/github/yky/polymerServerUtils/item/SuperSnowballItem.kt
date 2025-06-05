package io.github.yky.polymerServerUtils.item

import eu.pb4.polymer.core.api.item.SimplePolymerItem
import io.github.yky.polymerServerUtils.entity.projectile.thrown.SuperSnowballEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.item.ProjectileItem
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.stat.Stats
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Position
import net.minecraft.world.World

class SuperSnowballItem(settings: Settings) : SimplePolymerItem(settings, Items.SNOWBALL), ProjectileItem {
    companion object {
        var POWER: Float = 2.6f
    }

    override fun use(world: World, user: PlayerEntity, hand: Hand?): ActionResult {
        val itemStack = user.getStackInHand(hand)
        world.playSound(
            null,
            user.x,
            user.y,
            user.z,
            SoundEvents.ENTITY_SNOWBALL_THROW,
            SoundCategory.NEUTRAL,
            0.5f,
            0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f)
        )
        if (world is ServerWorld) {
            ProjectileEntity.spawnWithVelocity(
                { serverWorld, owner, stack -> SuperSnowballEntity(serverWorld, owner, stack) },
                world,
                itemStack,
                user,
                0.0f,
                POWER,
                1.0f
            )
        }

        user.incrementStat(Stats.USED.getOrCreateStat(this))
        itemStack.decrementUnlessCreative(1, user)
        return ActionResult.SUCCESS
    }

    override fun createEntity(
        world: World?, pos: Position, stack: ItemStack?, direction: Direction?
    ): ProjectileEntity {
        return SuperSnowballEntity(world, pos.x, pos.y, pos.z, stack)
    }
}
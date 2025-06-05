package io.github.yky.polymerServerUtils.item

import eu.pb4.polymer.core.api.item.SimplePolymerItem
import io.github.yky.polymerServerUtils.entity.EntityTypes
import net.minecraft.entity.SpawnReason
import net.minecraft.item.ItemUsageContext
import net.minecraft.item.Items
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.ActionResult
import net.minecraft.util.math.Direction
import net.minecraft.world.event.GameEvent

class SuperSnowGolemSpawnerItem(settings: Settings) : SimplePolymerItem(settings, Items.SNOWBALL) {
    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        val world = context.world
        if (world.isClient) {
            return ActionResult.SUCCESS
        } else {
            val itemStack = context.stack
            val blockPos = context.blockPos
            val direction = context.side
            val blockState = world.getBlockState(blockPos)
            val blockPos2 =
                if (blockState.getCollisionShape(world, blockPos).isEmpty) blockPos else blockPos.offset(direction)

            if (EntityTypes.SUPER_SNOW_GOLEM.spawnFromItemStack(
                    world as ServerWorld,
                    itemStack,
                    context.player,
                    blockPos2,
                    SpawnReason.SPAWN_ITEM_USE,
                    true,
                    blockPos != blockPos2 && direction == Direction.UP
                ) != null
            ) {
                itemStack.decrement(1)
                world.emitGameEvent(context.player, GameEvent.ENTITY_PLACE, blockPos)
            }

            return ActionResult.SUCCESS
        }
    }
}
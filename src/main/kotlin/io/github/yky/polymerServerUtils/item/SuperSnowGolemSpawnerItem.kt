package io.github.yky.polymerServerUtils.item

import eu.pb4.polymer.core.api.item.SimplePolymerItem
import io.github.yky.polymerServerUtils.entity.EntityTypes
import net.minecraft.advancement.criterion.Criteria
import net.minecraft.entity.Entity
import net.minecraft.entity.SpawnReason
import net.minecraft.item.ItemUsageContext
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class SuperSnowGolemSpawnerItem(settings: Settings) : SimplePolymerItem(settings, Items.SNOWBALL) {
    companion object {
        private fun spawnEntity(world: World, entity: Entity, pos: BlockPos) {
            entity.refreshPositionAndAngles(pos.x + 0.5, pos.y + 1 + 0.05, pos.z + 0.5, 0.0f, 0.0f)
            world.spawnEntity(entity)

            for (serverPlayerEntity in world.getNonSpectatingEntities(
                ServerPlayerEntity::class.java, entity.boundingBox.expand(5.0)
            )) {
                Criteria.SUMMONED_ENTITY.trigger(serverPlayerEntity, entity)
            }
        }
    }

    override fun useOnBlock(context: ItemUsageContext?): ActionResult {
        if (context == null || context.player == null) return ActionResult.FAIL

        val superSnowGolemEntity =
            EntityTypes.SUPER_SNOW_GOLEM.create(context.world, SpawnReason.TRIGGERED) ?: return ActionResult.FAIL
        spawnEntity(context.world, superSnowGolemEntity, context.blockPos)
        context.stack.decrementUnlessCreative(1, context.player)
        return ActionResult.SUCCESS
    }
}
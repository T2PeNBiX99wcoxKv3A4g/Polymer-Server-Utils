package io.github.yky.polymerServerUtils.item

import eu.pb4.polymer.core.api.item.SimplePolymerItem
import io.github.yky.polymerServerUtils.Utils
import net.minecraft.block.BlockState
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageTypes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.item.Items as MCItems

class HahItem(settings: Settings) : SimplePolymerItem(settings, MCItems.BLAZE_ROD) {
    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        if (context.player == null) return ActionResult.FAIL
        Utils.explosion(context.world, context.player!!, context.hitPos)
        return ActionResult.SUCCESS
    }

    override fun postHit(stack: ItemStack, target: LivingEntity, attacker: LivingEntity) {
        if (!attacker.isAlive) return super.postHit(stack, target, attacker)
        Utils.explosion(attacker.world, attacker, target)
    }

    override fun postMine(
        stack: ItemStack, world: World, state: BlockState, pos: BlockPos, miner: LivingEntity
    ): Boolean {
        if (!miner.isAlive) return super.postMine(stack, world, state, pos, miner)
        Utils.explosion(world, miner, pos)
        return true
    }

    override fun useOnEntity(
        stack: ItemStack, user: PlayerEntity, entity: LivingEntity, hand: Hand
    ): ActionResult {
        if (!entity.isAlive || user.world !is ServerWorld) return ActionResult.FAIL
        val source = entity.damageSources.create(DamageTypes.GENERIC_KILL, user)
        entity.damage(user.world as ServerWorld, source, Float.MAX_VALUE)
        return ActionResult.SUCCESS
    }
}
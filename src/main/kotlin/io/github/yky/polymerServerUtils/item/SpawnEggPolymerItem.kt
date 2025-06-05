package io.github.yky.polymerServerUtils.item

import eu.pb4.polymer.core.api.item.PolymerItem
import net.minecraft.entity.EntityType
import net.minecraft.entity.mob.MobEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.SpawnEggItem
import net.minecraft.util.Identifier
import xyz.nucleoid.packettweaker.PacketContext

class SpawnEggPolymerItem(type: EntityType<out MobEntity>, settings: Settings, private val item: Item) : SpawnEggItem(type, settings), PolymerItem {
    /**
     * Returns main/default item used on client for specific player
     *
     * @param itemStack ItemStack of virtual item
     * @param context    Context for which it's send
     * @return Vanilla (or other) Item instance
     */
    override fun getPolymerItem(itemStack: ItemStack?, context: PacketContext?): Item {
        return item
    }

    override fun getPolymerItemModel(stack: ItemStack?, context: PacketContext?): Identifier? {
        return null
    }
}
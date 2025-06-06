package io.github.yky.polymerServerUtils.item

import eu.pb4.polymer.core.api.item.PolymerItem
import eu.pb4.polymer.core.api.utils.PolymerClientDecoded
import eu.pb4.polymer.core.api.utils.PolymerKeepModel
import io.github.yky.polymerServerUtils.ex.toModId
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import xyz.nucleoid.packettweaker.PacketContext

open class ModPolymerItem(settings: Settings, private val baseModel: Item, private val modelId: String) :
    Item(settings), PolymerItem, PolymerKeepModel, PolymerClientDecoded {

    /**
     * Returns main/default item used on client for specific player
     *
     * @param itemStack ItemStack of virtual item
     * @param context    Context for which it's send
     * @return Vanilla (or other) Item instance
     */
    override fun getPolymerItem(itemStack: ItemStack?, context: PacketContext?): Item {
        // TODO check client
        return baseModel
    }

    override fun getPolymerItemModel(stack: ItemStack?, context: PacketContext?) = modelId.toModId()
}
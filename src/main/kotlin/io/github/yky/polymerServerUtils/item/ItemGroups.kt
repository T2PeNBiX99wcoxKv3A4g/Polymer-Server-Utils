package io.github.yky.polymerServerUtils.item

import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils
import io.github.yky.polymerServerUtils.Utils
import io.github.yky.polymerServerUtils.ex.toModId
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKey
import net.minecraft.text.Text

object ItemGroups {
    @JvmField
    var MOD_GROUP_KEY: RegistryKey<ItemGroup> = RegistryKey.of(Registries.ITEM_GROUP.key, "item-group".toModId())

    @JvmField
    var MOD_GROUP: ItemGroup =
        PolymerItemGroupUtils.builder().displayName(Text.translatable("item-group.${Utils.MOD_ID}.item-group"))
            .icon { ItemStack(Items.COOKED_ROTTEN_FLESH.item) }
            .entries { _: ItemGroup.DisplayContext?, entries: ItemGroup.Entries ->
                Items.entries.forEach {
                    entries.add(it.item)
                }
            }.build()

    internal fun register() {
        PolymerItemGroupUtils.registerPolymerItemGroup(MOD_GROUP_KEY, MOD_GROUP)
        Utils.Logger.info("Register Items")
    }
}
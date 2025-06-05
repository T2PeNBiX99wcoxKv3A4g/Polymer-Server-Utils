package io.github.yky.polymerServerUtils.item

import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils
import io.github.yky.polymerServerUtils.Utils
import io.github.yky.polymerServerUtils.ex.toModId
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemGroups
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKey
import net.minecraft.text.Text
import net.minecraft.item.Items as MCItems

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
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register {
            it.addAfter(MCItems.ROTTEN_FLESH, Items.COOKED_ROTTEN_FLESH.item)
            it.addAfter(MCItems.CARROT, Items.COOKED_CARROT.item)
        }
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register {
            it.addAfter(MCItems.SNOWBALL, Items.SUPER_SNOWBALL.item)
            it.addAfter(MCItems.MACE, Items.HAH.item)
        }
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register {
            it.addAfter(MCItems.SNOWBALL, Items.SUPER_SNOWBALL.item)
        }
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register {
            it.add(Items.SUPER_SNOW_GOLEM_SPAWN_EGG.item)
            it.add(Items.CREEPER_PET_SPAWN_EGG.item)
        }
        Utils.Logger.info("Register Items")
    }
}
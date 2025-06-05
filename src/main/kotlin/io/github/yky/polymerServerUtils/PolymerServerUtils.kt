package io.github.yky.polymerServerUtils

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils
import io.github.yky.polymerServerUtils.entity.EntityTypes
import io.github.yky.polymerServerUtils.item.ItemGroups
import io.github.yky.polymerServerUtils.item.Items
import net.fabricmc.api.ModInitializer

class PolymerServerUtils : ModInitializer {
    override fun onInitialize() {
        Items.entries
        ItemGroups.register()
        EntityTypes.init()

        if (PolymerResourcePackUtils.addModAssets(Utils.MOD_ID)) Utils.Logger.info("Successfully added mod assets for " + Utils.MOD_ID)
        else Utils.Logger.error("Failed to add mod assets for " + Utils.MOD_ID)

        PolymerResourcePackUtils.markAsRequired()
    }
}

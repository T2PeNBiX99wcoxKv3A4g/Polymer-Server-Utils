package io.github.yky.polymerServerUtils.client.datagen

import io.github.yky.polymerServerUtils.item.Items
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.item.Item
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper
import net.minecraft.registry.tag.ItemTags
import java.util.concurrent.CompletableFuture

class ItemTagProvider(output: FabricDataOutput, registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup>) :
    FabricTagProvider<Item>(output, RegistryKeys.ITEM, registriesFuture) {
    /**
     * Implement this method and then use [FabricTagProvider.getOrCreateTagBuilder] to get and register new tag builders.
     */
    override fun configure(wrapperLookup: RegistryWrapper.WrapperLookup?) {
        getOrCreateTagBuilder(ItemTags.WOLF_FOOD).add(Items.COOKED_ROTTEN_FLESH.item)
        getOrCreateTagBuilder(ItemTags.HORSE_FOOD).add(Items.COOKED_CARROT.item)
        getOrCreateTagBuilder(ItemTags.PIG_FOOD).add(Items.COOKED_CARROT.item)
        getOrCreateTagBuilder(ItemTags.RABBIT_FOOD).add(Items.COOKED_CARROT.item)
    }
}
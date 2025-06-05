package io.github.yky.polymerServerUtils.client.datagen

import io.github.yky.polymerServerUtils.item.Items
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.client.data.BlockStateModelGenerator
import net.minecraft.client.data.ItemModelGenerator
import net.minecraft.client.data.Models

class ModelGenerator(output: FabricDataOutput) : FabricModelProvider(output) {
    override fun generateBlockStateModels(blockStateModelGenerator: BlockStateModelGenerator) {
    }

    override fun generateItemModels(itemModelGenerator: ItemModelGenerator) {
        itemModelGenerator.register(Items.COOKED_ROTTEN_FLESH.item, Models.GENERATED)
        itemModelGenerator.register(Items.COOKED_CARROT.item, Models.GENERATED)
    }
}
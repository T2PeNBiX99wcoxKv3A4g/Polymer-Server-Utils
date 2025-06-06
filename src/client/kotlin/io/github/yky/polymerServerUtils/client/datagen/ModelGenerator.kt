package io.github.yky.polymerServerUtils.client.datagen

import io.github.yky.polymerServerUtils.item.Items
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.client.data.*
import net.minecraft.util.Identifier
import java.util.*

class ModelGenerator(output: FabricDataOutput) : FabricModelProvider(output) {
    companion object {
        @Suppress("unused")
        private fun item(parent: String, vararg requiredTextureKeys: TextureKey): Model {
            return Model(
                Optional.of(
                    Identifier.ofVanilla(
                        "item/$parent"
                    )
                ), Optional.empty(), *requiredTextureKeys
            )
        }
    }
    
    override fun generateBlockStateModels(blockStateModelGenerator: BlockStateModelGenerator) {
    }

    override fun generateItemModels(itemModelGenerator: ItemModelGenerator) {
        itemModelGenerator.register(Items.COOKED_ROTTEN_FLESH.item, Models.GENERATED)
        itemModelGenerator.register(Items.COOKED_CARROT.item, Models.GENERATED)
//        itemModelGenerator.register(Items.SUPER_SNOW_GOLEM_SPAWNER.item, item("snow_golem_spawn_egg"))
//        itemModelGenerator.register(Items.CREEPER_PET_SPAWN_EGG.item, item("creeper_spawn_egg"))
    }
}
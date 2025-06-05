package io.github.yky.polymerServerUtils.client

import io.github.yky.polymerServerUtils.client.datagen.*
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator

class PolymerServerUtilsDataGenerator : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
        val pack = fabricDataGenerator.createPack()

        // Languages
        pack.addProvider(::EnglishLangProvider)
        pack.addProvider(::JapaneseLangProvider)
        pack.addProvider(::ChineseLangProvider)
        pack.addProvider(::TaiwaneseLangProvider)

        // Models
        pack.addProvider(::ModelGenerator)

        // Recipes
        pack.addProvider(::ModRecipeProvider)
        
        // Tag
        pack.addProvider(::EntityTypeTagProvider)
        
        // Loot Table
        pack.addProvider(::ModEntityLootTableProvider)
    }
}

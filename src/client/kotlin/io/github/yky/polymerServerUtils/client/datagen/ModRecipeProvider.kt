package io.github.yky.polymerServerUtils.client.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.minecraft.data.recipe.RecipeExporter
import net.minecraft.data.recipe.RecipeGenerator
import net.minecraft.data.recipe.ShapedRecipeJsonBuilder
import net.minecraft.registry.RegistryWrapper
import java.util.concurrent.CompletableFuture

class ModRecipeProvider(output: FabricDataOutput, registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup>) :
    FabricRecipeProvider(output, registriesFuture) {

    override fun getName() = "PolymerServerUtilsRecipeProvider"

    /**
     * Implement this method and then use the range of methods in [RecipeGenerator] or from one of the recipe json factories such as [ShapedRecipeJsonBuilder] or [ShapelessRecipeJsonBuilder].
     */
    override fun getRecipeGenerator(
        registryLookup: RegistryWrapper.WrapperLookup?, exporter: RecipeExporter?
    ): RecipeGenerator? {
        if (registryLookup == null || exporter == null) return null
        return ModRecipeGenerator(registryLookup, exporter)
    }
}
package io.github.yky.polymerServerUtils.client.datagen

import io.github.yky.polymerServerUtils.Utils
import io.github.yky.polymerServerUtils.item.Items
import net.minecraft.data.recipe.CookingRecipeJsonBuilder
import net.minecraft.data.recipe.RecipeExporter
import net.minecraft.data.recipe.RecipeGenerator
import net.minecraft.data.recipe.ShapedRecipeJsonBuilder
import net.minecraft.item.Item
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.book.RecipeCategory
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper
import net.minecraft.util.Identifier
import net.minecraft.item.Items as MCItems


class ModRecipeGenerator(registryLookup: RegistryWrapper.WrapperLookup, exporter: RecipeExporter) :
    RecipeGenerator(registryLookup, exporter) {
    override fun generate() {
        campfireRecipe(exporter, MCItems.ROTTEN_FLESH, Items.COOKED_ROTTEN_FLESH.item, 0.15f)
        smokingRecipe(exporter, MCItems.ROTTEN_FLESH, Items.COOKED_ROTTEN_FLESH.item, 0.15f)
        smeltingRecipe(exporter, MCItems.ROTTEN_FLESH, Items.COOKED_ROTTEN_FLESH.item, 0.15f)

        campfireRecipe(exporter, MCItems.CARROT, Items.COOKED_CARROT.item, 0.35f)
        smokingRecipe(exporter, MCItems.CARROT, Items.COOKED_CARROT.item, 0.35f)
        smeltingRecipe(exporter, MCItems.CARROT, Items.COOKED_CARROT.item, 0.35f)

        blastingRecipe(exporter, MCItems.COBBLESTONE, MCItems.STONE, 0.1f, RecipeCategory.BUILDING_BLOCKS)

        val itemLookup = registries.getOrThrow(RegistryKeys.ITEM)

        ShapedRecipeJsonBuilder.create(itemLookup, RecipeCategory.MISC, Items.SUPER_SNOW_GOLEM_SPAWNER.item)
            .pattern("##").input('#', MCItems.SNOW_BLOCK)
            .criterion(hasItem(MCItems.SNOW_BLOCK), conditionsFromItem(MCItems.SNOW_BLOCK)).offerTo(exporter)
    }

    private fun campfireRecipe(
        exporter: RecipeExporter,
        input: Item,
        output: Item,
        experience: Float,
        recipeCategory: RecipeCategory = RecipeCategory.FOOD,
        cookingTime: Int = 600
    ) {
        CookingRecipeJsonBuilder.createCampfireCooking(
            Ingredient.ofItems(input), recipeCategory, output, experience, cookingTime
        ).criterion(hasItem(input), conditionsFromItem(input)).offerTo(
            exporter,
            Identifier.of(Utils.MOD_ID, "${getRecipeName(output)}_from_campfire_cooking_${getRecipeName(input)}")
                .toString()
        )
    }

    private fun smokingRecipe(
        exporter: RecipeExporter,
        input: Item,
        output: Item,
        experience: Float,
        recipeCategory: RecipeCategory = RecipeCategory.FOOD,
        cookingTime: Int = 100
    ) {
        CookingRecipeJsonBuilder.createSmoking(
            Ingredient.ofItems(input), recipeCategory, output, experience, cookingTime
        ).criterion(hasItem(input), conditionsFromItem(input)).offerTo(
            exporter,
            Identifier.of(Utils.MOD_ID, "${getRecipeName(output)}_from_smoking_${getRecipeName(input)}").toString()
        )
    }

    private fun blastingRecipe(
        exporter: RecipeExporter,
        input: Item,
        output: Item,
        experience: Float,
        recipeCategory: RecipeCategory = RecipeCategory.FOOD,
        cookingTime: Int = 100
    ) {
        CookingRecipeJsonBuilder.createBlasting(
            Ingredient.ofItems(input), recipeCategory, output, experience, cookingTime
        ).criterion(hasItem(input), conditionsFromItem(input)).offerTo(
            exporter,
            Identifier.of(Utils.MOD_ID, "${getRecipeName(output)}_from_blasting_${getRecipeName(input)}").toString()
        )
    }

    private fun smeltingRecipe(
        exporter: RecipeExporter,
        input: Item,
        output: Item,
        experience: Float,
        recipeCategory: RecipeCategory = RecipeCategory.FOOD,
        cookingTime: Int = 200
    ) {
        CookingRecipeJsonBuilder.createSmelting(
            Ingredient.ofItems(input), recipeCategory, output, experience, cookingTime
        ).criterion(hasItem(input), conditionsFromItem(input)).offerTo(
            exporter,
            Identifier.of(Utils.MOD_ID, "${getRecipeName(output)}_from_smelting_${getRecipeName(input)}").toString()
        )
    }
}
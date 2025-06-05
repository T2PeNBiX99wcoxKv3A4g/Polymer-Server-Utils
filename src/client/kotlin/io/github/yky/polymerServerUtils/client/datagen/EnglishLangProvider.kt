package io.github.yky.polymerServerUtils.client.datagen

import io.github.yky.polymerServerUtils.entity.EntityTypes
import io.github.yky.polymerServerUtils.item.Items
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.registry.RegistryWrapper
import java.util.concurrent.CompletableFuture

class EnglishLangProvider(
    dataGenerator: FabricDataOutput, registryLookup: CompletableFuture<RegistryWrapper.WrapperLookup>
) : ModLanguageProvider(dataGenerator, "en_us", registryLookup) {
    override fun generateTranslations(
        registryLookup: RegistryWrapper.WrapperLookup, translationBuilder: TranslationBuilder
    ) {
        translationBuilder.add(Items.COOKED_ROTTEN_FLESH.item, "$cooked Rotten Flesh")
        translationBuilder.add(Items.COOKED_CARROT.item, "$cooked Carrot")
        translationBuilder.add(Items.HAH.item, "HAH?")
        translationBuilder.add(Items.SUPER_SNOWBALL.item, snowBall)
        translationBuilder.add(Items.SUPER_SNOW_GOLEM_SPAWNER.item, "Super Snow Golem Spawner")
        translationBuilder.add(Items.SUPER_SNOW_GOLEM_SPAWN_EGG.item, "Super Snow Golem $spawnEgg")
        translationBuilder.add(Items.CREEPER_PET_SPAWN_EGG.item, "Creeper Pet $spawnEgg")
        
        translationBuilder.add(EntityTypes.SNOWBALL, snowBall)
        translationBuilder.add(EntityTypes.SUPER_SNOW_GOLEM, "Super Snow Golem")
        translationBuilder.add(EntityTypes.CREEPER_PET, "Creeper Pet")

        translationBuilder.add(existingLanguageFilePath())
    }
}
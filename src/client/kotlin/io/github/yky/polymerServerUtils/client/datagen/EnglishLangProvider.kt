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
        translationBuilder.add(Items.COOKED_ROTTEN_FLESH.item, "Cooked Rotten Flesh")
        translationBuilder.add(Items.COOKED_CARROT.item, "Cooked Carrot")
        translationBuilder.add(Items.HAH.item, "HAH?")
        
        val snowBall = "Super Snowball"
        translationBuilder.add(Items.SUPER_SNOWBALL.item, snowBall)
        translationBuilder.add(Items.SUPER_SNOW_GOLEM_SPAWNER.item, "Super Snow Golem Spawner")
        
        translationBuilder.add(EntityTypes.SNOWBALL, snowBall)
        translationBuilder.add(EntityTypes.SUPER_SNOW_GOLEM, "Super Snow Golem")

        translationBuilder.add(existingLanguageFilePath())
    }
}
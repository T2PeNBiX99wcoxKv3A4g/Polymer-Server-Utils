package io.github.yky.polymerServerUtils.client.datagen

import io.github.yky.polymerServerUtils.item.Items
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.registry.RegistryWrapper
import java.util.concurrent.CompletableFuture

class JapaneseLangProvider(
    dataGenerator: FabricDataOutput, registryLookup: CompletableFuture<RegistryWrapper.WrapperLookup>
) : ModLanguageProvider(dataGenerator, "ja_jp", registryLookup) {
    override fun generateTranslations(
        registryLookup: RegistryWrapper.WrapperLookup, translationBuilder: TranslationBuilder
    ) {
        translationBuilder.add(Items.COOKED_ROTTEN_FLESH.item, "焼き腐った肉")
        translationBuilder.add(Items.COOKED_CARROT.item, "焼きニンジン")
        translationBuilder.add(Items.HAH.item, "は？")

        translationBuilder.add(existingLanguageFilePath())
    }
}
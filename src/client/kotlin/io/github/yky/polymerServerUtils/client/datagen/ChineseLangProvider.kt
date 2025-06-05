package io.github.yky.polymerServerUtils.client.datagen

import io.github.yky.polymerServerUtils.item.Items
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.registry.RegistryWrapper
import java.util.concurrent.CompletableFuture

class ChineseLangProvider(
    dataGenerator: FabricDataOutput, registryLookup: CompletableFuture<RegistryWrapper.WrapperLookup>
) : ModLanguageProvider(dataGenerator, "zh_cn", registryLookup) {
    override fun generateTranslations(
        registryLookup: RegistryWrapper.WrapperLookup, translationBuilder: TranslationBuilder
    ) {
        translationBuilder.add(Items.COOKED_ROTTEN_FLESH.item, "熟腐肉")
        translationBuilder.add(Items.COOKED_CARROT.item, "熟胡萝卜")
        translationBuilder.add(Items.HAH.item, "哈?")

        translationBuilder.add(existingLanguageFilePath())
    }
}
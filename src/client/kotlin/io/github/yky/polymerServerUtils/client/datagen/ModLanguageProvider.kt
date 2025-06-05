package io.github.yky.polymerServerUtils.client.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.minecraft.registry.RegistryWrapper
import java.util.concurrent.CompletableFuture

@Suppress("MemberVisibilityCanBePrivate")
abstract class ModLanguageProvider(
    dataOutput: FabricDataOutput,
    protected val languageCode: String,
    registryLookup: CompletableFuture<RegistryWrapper.WrapperLookup>
) : FabricLanguageProvider(dataOutput, languageCode, registryLookup) {
    protected fun existingLanguageFilePath() =
        dataOutput.modContainer.findPath("assets/polymer-server-utils/lang_gen/${languageCode}.existing.json").get()
}
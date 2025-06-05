package io.github.yky.polymerServerUtils.client.datagen

import io.github.yky.polymerServerUtils.entity.EntityTypes
import io.github.yky.polymerServerUtils.item.Items
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.registry.RegistryWrapper
import java.util.concurrent.CompletableFuture

class JapaneseLangProvider(
    dataGenerator: FabricDataOutput, registryLookup: CompletableFuture<RegistryWrapper.WrapperLookup>
) : ModLanguageProvider(dataGenerator, "ja_jp", registryLookup) {
    override val cooked = "焼き"
    override val spawnEgg = "のスポーンエッグ"
    override val snowBall = "スーパー雪玉"

    override fun generateTranslations(
        registryLookup: RegistryWrapper.WrapperLookup, translationBuilder: TranslationBuilder
    ) {
        translationBuilder.add(Items.COOKED_ROTTEN_FLESH.item, "${cooked}腐った肉")
        translationBuilder.add(Items.COOKED_CARROT.item, "${cooked}ニンジン")
        translationBuilder.add(Items.HAH.item, "は？")
        translationBuilder.add(Items.SUPER_SNOWBALL.item, snowBall)
        translationBuilder.add(Items.SUPER_SNOW_GOLEM_SPAWNER.item, "スーパースノウゴーレムのスポナー")
        translationBuilder.add(Items.SUPER_SNOW_GOLEM_SPAWN_EGG.item, "スーパースノウゴーレム${spawnEgg}")
        translationBuilder.add(Items.CREEPER_PET_SPAWN_EGG.item, "ペットクリーパー${spawnEgg}")

        translationBuilder.add(EntityTypes.SNOWBALL, snowBall)
        translationBuilder.add(EntityTypes.SUPER_SNOW_GOLEM, "スーパースノウゴーレム")
        translationBuilder.add(EntityTypes.CREEPER_PET, "ペットクリーパー")

        translationBuilder.add(existingLanguageFilePath())
    }
}
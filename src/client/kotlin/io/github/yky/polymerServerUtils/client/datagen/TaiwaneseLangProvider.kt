package io.github.yky.polymerServerUtils.client.datagen

import io.github.yky.polymerServerUtils.entity.EntityTypes
import io.github.yky.polymerServerUtils.item.Items
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.registry.RegistryWrapper
import java.util.concurrent.CompletableFuture

class TaiwaneseLangProvider(
    dataGenerator: FabricDataOutput, registryLookup: CompletableFuture<RegistryWrapper.WrapperLookup>
) : ModLanguageProvider(dataGenerator, "zh_tw", registryLookup) {
    override val cooked = "烤"
    override val spawnEgg = "生怪蛋"
    override val snowBall = "超級雪球"
    
    override fun generateTranslations(
        registryLookup: RegistryWrapper.WrapperLookup, translationBuilder: TranslationBuilder
    ) {
        translationBuilder.add(Items.COOKED_ROTTEN_FLESH.item, "${cooked}腐肉")
        translationBuilder.add(Items.COOKED_CARROT.item, "${cooked}胡蘿蔔")
        translationBuilder.add(Items.HAH.item, "哈?")
        translationBuilder.add(Items.SUPER_SNOWBALL.item, snowBall)
        translationBuilder.add(Items.SUPER_SNOW_GOLEM_SPAWNER.item, "超級雪人生成器")
        translationBuilder.add(Items.SUPER_SNOW_GOLEM_SPAWN_EGG.item, "超級雪人 $spawnEgg")
        translationBuilder.add(Items.CREEPER_PET_SPAWN_EGG.item, "苦力怕寵物 $spawnEgg")

        translationBuilder.add(EntityTypes.SNOWBALL, snowBall)
        translationBuilder.add(EntityTypes.SUPER_SNOW_GOLEM, "超級雪人")
        translationBuilder.add(EntityTypes.CREEPER_PET, "苦力怕寵物")

        translationBuilder.add(existingLanguageFilePath())
    }
}
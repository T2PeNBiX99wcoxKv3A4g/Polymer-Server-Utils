package io.github.yky.polymerServerUtils.client.datagen

import io.github.yky.polymerServerUtils.entity.EntityTypes
import io.github.yky.polymerServerUtils.item.Items
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricEntityLootTableProvider
import net.minecraft.entity.EntityType
import net.minecraft.loot.LootPool
import net.minecraft.loot.LootTable
import net.minecraft.loot.condition.EntityPropertiesLootCondition
import net.minecraft.loot.context.LootContext
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.loot.entry.TagEntry
import net.minecraft.loot.function.EnchantedCountIncreaseLootFunction
import net.minecraft.loot.function.SetCountLootFunction
import net.minecraft.loot.provider.number.ConstantLootNumberProvider
import net.minecraft.loot.provider.number.UniformLootNumberProvider
import net.minecraft.predicate.entity.EntityPredicate
import net.minecraft.registry.RegistryEntryLookup
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper.WrapperLookup
import net.minecraft.registry.tag.EntityTypeTags
import net.minecraft.registry.tag.ItemTags
import java.util.concurrent.CompletableFuture
import net.minecraft.item.Items as MCItems

class ModEntityLootTableProvider(
    output: FabricDataOutput, registryLookup: CompletableFuture<WrapperLookup>
) : FabricEntityLootTableProvider(output, registryLookup) {
    /**
     * Implement this method to add entity drops.
     *
     *
     * Use the [EntityLootTableGenerator.register] methods to generate entity drops.
     *
     *
     * See [VanillaEntityLootTableGenerator.generate] for examples of vanilla entity loot tables.
     */
    override fun generate() {
        val registryEntryLookup: RegistryEntryLookup<EntityType<*>> = registries.getOrThrow(RegistryKeys.ENTITY_TYPE)

        register(
            EntityTypes.SUPER_SNOW_GOLEM, LootTable.builder().pool(
                LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0f)).with(
                    ItemEntry.builder(Items.SUPER_SNOWBALL.item)
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0f, 15.0f)))
                )
            )
        )

        register(
            EntityTypes.CREEPER_PET, LootTable.builder().pool(
                LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0f)).with(
                    ItemEntry.builder(MCItems.GUNPOWDER)
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0f, 2.0f))).apply(
                            EnchantedCountIncreaseLootFunction.builder(
                                registries, UniformLootNumberProvider.create(0.0f, 1.0f)
                            )
                        )
                )
            ).pool(
                LootPool.builder().with(TagEntry.expandBuilder(ItemTags.CREEPER_DROP_MUSIC_DISCS)).conditionally(
                    EntityPropertiesLootCondition.builder(
                        LootContext.EntityTarget.ATTACKER,
                        EntityPredicate.Builder.create().type(registryEntryLookup, EntityTypeTags.SKELETONS)
                    )
                )
            )
        )
    }
}
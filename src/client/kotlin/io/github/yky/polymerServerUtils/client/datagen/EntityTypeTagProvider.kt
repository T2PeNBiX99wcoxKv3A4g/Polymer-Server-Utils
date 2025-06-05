package io.github.yky.polymerServerUtils.client.datagen

import io.github.yky.polymerServerUtils.entity.EntityTypes
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.entity.EntityType
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper
import net.minecraft.registry.tag.EntityTypeTags
import java.util.concurrent.CompletableFuture

class EntityTypeTagProvider(
    output: FabricDataOutput, registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup>
) : FabricTagProvider<EntityType<*>>(output, RegistryKeys.ENTITY_TYPE, registriesFuture) {
    /**
     * Implement this method and then use [FabricTagProvider.getOrCreateTagBuilder] to get and register new tag builders.
     */
    override fun configure(wrapperLookup: RegistryWrapper.WrapperLookup?) {
        getOrCreateTagBuilder(EntityTypeTags.CAN_BREATHE_UNDER_WATER).add(EntityTypes.SUPER_SNOW_GOLEM)
        getOrCreateTagBuilder(EntityTypeTags.FALL_DAMAGE_IMMUNE).add(EntityTypes.SUPER_SNOW_GOLEM)
    }
}
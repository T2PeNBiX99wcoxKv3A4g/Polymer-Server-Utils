@file:Suppress("SameParameterValue", "MemberVisibilityCanBePrivate")

package io.github.yky.polymerServerUtils.entity

import eu.pb4.polymer.core.api.entity.PolymerEntityUtils
import io.github.yky.polymerServerUtils.entity.passive.CreeperPetEntity
import io.github.yky.polymerServerUtils.entity.passive.SuperSnowGolemEntity
import io.github.yky.polymerServerUtils.entity.projectile.thrown.SuperSnowballEntity
import io.github.yky.polymerServerUtils.ex.toModId
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricDefaultAttributeRegistry
import net.minecraft.block.Blocks
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys

object EntityTypes {
    @JvmField
    val SUPER_SNOW_GOLEM: EntityType<SuperSnowGolemEntity> = register(
        "super_snow_golem",
        EntityType.Builder.create(::SuperSnowGolemEntity, SpawnGroup.MISC).allowSpawningInside(Blocks.POWDER_SNOW)
            .dimensions(0.7f, 1.9f).eyeHeight(1.7f).maxTrackingRange(8)
    )

    val SNOWBALL: EntityType<SuperSnowballEntity> = register(
        "super_snowball",
        EntityType.Builder.create(::SuperSnowballEntity, SpawnGroup.MISC).dropsNothing().dimensions(0.25f, 0.25f)
            .maxTrackingRange(4).trackingTickInterval(10)
    )

    val CREEPER_PET: EntityType<CreeperPetEntity> = register(
        "creeper_pet",
        EntityType.Builder.create(::CreeperPetEntity, SpawnGroup.MONSTER).dimensions(0.6f, 1.7f).maxTrackingRange(8)
    )

    private fun <T : Entity?> register(key: RegistryKey<EntityType<*>>, type: EntityType.Builder<T>): EntityType<T> {
        val entityType = Registry.register(Registries.ENTITY_TYPE, key, type.build(key))
        PolymerEntityUtils.registerType(entityType)
        return entityType
    }

    private fun keyOf(id: String) = RegistryKey.of(RegistryKeys.ENTITY_TYPE, id.toModId())
    private fun <T : Entity?> register(id: String, type: EntityType.Builder<T>) = register(keyOf(id), type)

    internal fun init() {
        FabricDefaultAttributeRegistry.register(SUPER_SNOW_GOLEM, SuperSnowGolemEntity.createSuperSnowGolemAttributes())
        FabricDefaultAttributeRegistry.register(CREEPER_PET, CreeperPetEntity.createCreeperPetAttributes())
    }
}
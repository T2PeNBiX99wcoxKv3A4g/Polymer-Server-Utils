package io.github.yky.polymerServerUtils.item

import io.github.yky.polymerServerUtils.component.FoodComponents
import io.github.yky.polymerServerUtils.data.ItemData
import io.github.yky.polymerServerUtils.entity.EntityTypes
import io.github.yky.polymerServerUtils.ex.toModId
import net.minecraft.component.type.AttributeModifierSlot
import net.minecraft.component.type.AttributeModifiersComponent
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.item.Item
import net.minecraft.item.Item.*
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.item.Items as MCItems

enum class Items(itemData: ItemData) {
    COOKED_ROTTEN_FLESH(
        register(
            "cooked_rotten_flesh", Settings().food(FoodComponents.COOKED_ROTTEN_FLESH)
        ) { settings, id -> ModPolymerItem(settings, MCItems.ROTTEN_FLESH, id) }),
    COOKED_CARROT(register("cooked_carrot", Settings().food(FoodComponents.COOKED_CARROT)) { settings, id ->
        ModPolymerItem(
            settings, MCItems.CARROT, id
        )
    }),
    SUPER_SNOWBALL(
        register(
            "super_snowball", Settings().maxCount(16)
        ) { settings, _ -> SuperSnowballItem(settings) }),
    HAH(
        register(
            "hah", Settings().maxCount(1).fireproof().attributeModifiers(
                AttributeModifiersComponent.builder().add(
                    EntityAttributes.ATTACK_DAMAGE, EntityAttributeModifier(
                        BASE_ATTACK_DAMAGE_MODIFIER_ID, 5.toDouble(), EntityAttributeModifier.Operation.ADD_VALUE
                    ), AttributeModifierSlot.ANY
                ).add(
                    EntityAttributes.ATTACK_SPEED, EntityAttributeModifier(
                        BASE_ATTACK_SPEED_MODIFIER_ID, 0.0, EntityAttributeModifier.Operation.ADD_VALUE
                    ), AttributeModifierSlot.ANY
                ).build()
            )
        ) { settings, _ -> HahItem(settings) }),
    SUPER_SNOW_GOLEM_SPAWNER(
        register(
            "super_snow_golem_spawner", Settings().fireproof().maxCount(16)
        ) { settings, _ -> SuperSnowGolemSpawnerItem(settings) }),
    SUPER_SNOW_GOLEM_SPAWN_EGG(register("super_snow_golem_spawn_egg") { settings, _ ->
        SpawnEggPolymerItem(
            EntityTypes.SUPER_SNOW_GOLEM, settings, MCItems.SNOW_GOLEM_SPAWN_EGG
        )
    }),
    CREEPER_PET_SPAWN_EGG(register("creeper_pet_spawn_egg") { settings, _ ->
        SpawnEggPolymerItem(
            EntityTypes.CREEPER_PET, settings, MCItems.CREEPER_SPAWN_EGG
        )
    });

    val item: Item = itemData.item

    @Suppress("unused")
    val key: RegistryKey<Item> = itemData.key
}

private fun register(
    id: String, settings: Settings, item: (settings: Settings, id: String) -> Item
): ItemData {
    val key = RegistryKey.of(RegistryKeys.ITEM, id.toModId())
    settings.registryKey(key)
    return ItemData(Registry.register(Registries.ITEM, id.toModId(), item(settings, id)), key)
}

private fun register(id: String, item: (settings: Settings, id: String) -> Item) = register(id, Settings(), item)

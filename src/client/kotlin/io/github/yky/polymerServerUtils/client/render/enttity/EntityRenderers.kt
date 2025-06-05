package io.github.yky.polymerServerUtils.client.render.enttity

import io.github.yky.polymerServerUtils.entity.EntityTypes
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry

object EntityRenderers {
    internal fun init() {
        EntityRendererRegistry.register(EntityTypes.SUPER_SNOW_GOLEM, ::SuperSnowGolemEntityRenderer)
    }
}
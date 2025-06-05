package io.github.yky.polymerServerUtils.client

import io.github.yky.polymerServerUtils.client.render.entity.EntityRenderers
import net.fabricmc.api.ClientModInitializer

class PolymerServerUtilsClient : ClientModInitializer {
    override fun onInitializeClient() {
        EntityRenderers.init()
    }
}

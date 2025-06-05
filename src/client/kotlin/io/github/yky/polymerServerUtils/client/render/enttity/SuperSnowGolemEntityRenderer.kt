package io.github.yky.polymerServerUtils.client.render.enttity

import io.github.yky.polymerServerUtils.entity.passive.SuperSnowGolemEntity
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.MobEntityRenderer
import net.minecraft.client.render.entity.feature.SnowGolemPumpkinFeatureRenderer
import net.minecraft.client.render.entity.model.EntityModelLayers
import net.minecraft.client.render.entity.model.SnowGolemEntityModel
import net.minecraft.client.render.entity.state.SnowGolemEntityRenderState
import net.minecraft.util.Identifier

@Environment(EnvType.CLIENT)
class SuperSnowGolemEntityRenderer(context: EntityRendererFactory.Context) :
    MobEntityRenderer<SuperSnowGolemEntity?, SnowGolemEntityRenderState?, SnowGolemEntityModel?>(
        context, SnowGolemEntityModel(context.getPart(EntityModelLayers.SNOW_GOLEM)), 0.5f
    ) {

    companion object {
        private val TEXTURE: Identifier = Identifier.ofVanilla("textures/entity/snow_golem.png")
    }

    init {
        this.addFeature(SnowGolemPumpkinFeatureRenderer(this, context.blockRenderManager))
    }

    override fun getTexture(state: SnowGolemEntityRenderState?) = TEXTURE

    override fun createRenderState() = SnowGolemEntityRenderState()

    override fun updateRenderState(
        superSnowGolemEntity: SuperSnowGolemEntity?,
        snowGolemEntityRenderState: SnowGolemEntityRenderState?,
        f: Float
    ) {
        super.updateRenderState(superSnowGolemEntity, snowGolemEntityRenderState, f)
        if (snowGolemEntityRenderState == null || superSnowGolemEntity == null) return
        snowGolemEntityRenderState.hasPumpkin = superSnowGolemEntity.hasPumpkin()
    }
}
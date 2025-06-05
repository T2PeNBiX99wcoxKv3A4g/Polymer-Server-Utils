package io.github.yky.polymerServerUtils.client.render.entity

import io.github.yky.polymerServerUtils.entity.passive.CreeperPetEntity
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.MobEntityRenderer
import net.minecraft.client.render.entity.feature.CreeperChargeFeatureRenderer
import net.minecraft.client.render.entity.model.CreeperEntityModel
import net.minecraft.client.render.entity.model.EntityModelLayers
import net.minecraft.client.render.entity.state.CreeperEntityRenderState
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper

@Environment(EnvType.CLIENT)
class CreeperPetEntityRenderer(context: EntityRendererFactory.Context) :
    MobEntityRenderer<CreeperPetEntity?, CreeperEntityRenderState?, CreeperEntityModel?>(
        context, CreeperEntityModel(context.getPart(EntityModelLayers.CREEPER)), 0.5f
    ) {
    companion object {
        private val TEXTURE: Identifier = Identifier.ofVanilla("textures/entity/creeper/creeper.png")
    }

    init {
        addFeature(CreeperChargeFeatureRenderer(this, context.entityModels))
    }

    override fun scale(state: CreeperEntityRenderState?, matrixStack: MatrixStack?) {
        if (state == null || matrixStack == null) return
        var f = state.fuseTime
        val g = 1.0f + MathHelper.sin(f * 100.0f) * f * 0.01f
        f = MathHelper.clamp(f, 0.0f, 1.0f)
        f *= f
        f *= f
        val h = (1.0f + f * 0.4f) * g
        val i = (1.0f + f * 0.1f) / g
        matrixStack.scale(h, i, h)
    }

    override fun getAnimationCounter(state: CreeperEntityRenderState?): Float {
        if (state == null) return 0f
        val f = state.fuseTime
        return if ((f * 10.0f).toInt() % 2 == 0) 0.0f else MathHelper.clamp(f, 0.5f, 1.0f)
    }

    override fun getTexture(state: CreeperEntityRenderState?) = TEXTURE

    override fun createRenderState(): CreeperEntityRenderState {
        return CreeperEntityRenderState()
    }

    override fun updateRenderState(entity: CreeperPetEntity?, state: CreeperEntityRenderState?, f: Float) {
        super.updateRenderState(entity, state, f)
        if (entity == null || state == null) return
        state.fuseTime = entity.getLerpedFuseTime(f)
        state.charged = entity.isCharged
    }
}
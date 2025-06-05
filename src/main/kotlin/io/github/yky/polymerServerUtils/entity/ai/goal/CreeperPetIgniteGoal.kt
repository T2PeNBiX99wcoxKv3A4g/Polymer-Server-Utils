package io.github.yky.polymerServerUtils.entity.ai.goal

import io.github.yky.polymerServerUtils.entity.passive.CreeperPetEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.goal.Goal
import java.util.*

class CreeperPetIgniteGoal(private var creeperPet: CreeperPetEntity) : Goal() {
    private var target: LivingEntity? = null

    init {
        controls = EnumSet.of(Control.MOVE)
    }

    override fun canStart(): Boolean {
        val livingEntity = creeperPet.target
        return creeperPet.fuseSpeed > 0 || livingEntity != null && creeperPet.squaredDistanceTo(livingEntity) < 9.0
    }

    override fun start() {
        creeperPet.navigation.stop()
        target = creeperPet.target
    }

    override fun stop() {
        target = null
    }

    override fun shouldRunEveryTick() = true

    override fun tick() {
        when {
            target == null -> creeperPet.fuseSpeed = -1
            creeperPet.squaredDistanceTo(target) > 49.0 -> creeperPet.fuseSpeed = -1
            !creeperPet.visibilityCache.canSee(target) -> creeperPet.fuseSpeed = -1
            else -> creeperPet.fuseSpeed = 1
        }
    }
}
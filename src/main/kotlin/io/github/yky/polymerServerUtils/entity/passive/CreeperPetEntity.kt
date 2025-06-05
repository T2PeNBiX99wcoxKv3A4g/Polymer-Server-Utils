package io.github.yky.polymerServerUtils.entity.passive

import eu.pb4.polymer.core.api.entity.PolymerEntity
import io.github.yky.polymerServerUtils.Utils
import io.github.yky.polymerServerUtils.entity.EntityTypes
import io.github.yky.polymerServerUtils.entity.ai.goal.CreeperPetIgniteGoal
import net.minecraft.component.DataComponentTypes
import net.minecraft.entity.*
import net.minecraft.entity.ai.goal.*
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedData
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.mob.Monster
import net.minecraft.entity.passive.*
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.tag.ItemTags
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import net.minecraft.world.event.GameEvent
import xyz.nucleoid.packettweaker.PacketContext
import java.util.function.Predicate

class CreeperPetEntity(entityType: EntityType<out CreeperPetEntity>, world: World) : TameableEntity(entityType, world),
    PolymerEntity {
    companion object {
        private val FUSE_SPEED: TrackedData<Int> =
            DataTracker.registerData(CreeperPetEntity::class.java, TrackedDataHandlerRegistry.INTEGER)
        private val CHARGED: TrackedData<Boolean> =
            DataTracker.registerData(CreeperPetEntity::class.java, TrackedDataHandlerRegistry.BOOLEAN)
        private val IGNITED: TrackedData<Boolean> =
            DataTracker.registerData(CreeperPetEntity::class.java, TrackedDataHandlerRegistry.BOOLEAN)
        private const val DEFAULT_CHARGED: Boolean = false
        private const val DEFAULT_IGNITED: Boolean = false
        private const val DEFAULT_FUSE: Short = 30
        private const val DEFAULT_EXPLOSION_RADIUS: Byte = 3

        fun createCreeperPetAttributes(): DefaultAttributeContainer.Builder =
            AnimalEntity.createAnimalAttributes().add(EntityAttributes.ATTACK_DAMAGE)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.25)

        private class TemptGoal(
            private val creeperPet: CreeperPetEntity,
            speed: Double,
            foodPredicate: Predicate<ItemStack?>?,
            canBeScared: Boolean
        ) : net.minecraft.entity.ai.goal.TemptGoal(creeperPet, speed, foodPredicate, canBeScared) {
            private var player: PlayerEntity? = null

            override fun tick() {
                super.tick()
                if (player == null && mob.random.nextInt(getTickCount(600)) == 0) {
                    player = closestPlayer
                } else if (mob.random.nextInt(getTickCount(500)) == 0) {
                    player = null
                }
            }

            override fun canBeScared(): Boolean {
                return if (player != null && player == closestPlayer) false else super.canBeScared()
            }

            override fun canStart(): Boolean {
                return super.canStart() && !creeperPet.isTamed
            }
        }
    }

    private var temptGoal: TemptGoal? = null
    private var lastFuseTime = 0
    private var currentFuseTime = 0
    private var fuseTime = DEFAULT_FUSE.toInt()
    private var explosionRadius = DEFAULT_EXPLOSION_RADIUS.toInt()
    private var headsDropped = 0

    override fun initGoals() {
        temptGoal = TemptGoal(this, 0.6, { stack -> stack?.isIn(ItemTags.CAT_FOOD) == true }, true)
        goalSelector.add(1, SwimGoal(this))
        goalSelector.add(2, CreeperPetIgniteGoal(this))
        goalSelector.add(2, SitGoal(this))
        goalSelector.add(3, MeleeAttackGoal(this, 1.0, false))
        goalSelector.add(4, temptGoal)
        goalSelector.add(5, LookAtEntityGoal(this, PlayerEntity::class.java, 8.0f))
        goalSelector.add(6, FollowOwnerGoal(this, 1.0, 10.0f, 5.0f))
        goalSelector.add(6, LookAroundGoal(this))
        goalSelector.add(7, AttackGoal(this))
        goalSelector.add(8, AnimalMateGoal(this, 0.8))
        goalSelector.add(9, WanderAroundFarGoal(this, 0.8, 1.0000001E-5f))
        targetSelector.add(
            1, UntamedActiveTargetGoal(
                this, MobEntity::class.java, false
            ) { entity: LivingEntity?, _: ServerWorld? -> entity is Monster })
        targetSelector.add(2, RevengeGoal(this))
    }

    override fun getSafeFallDistance(): Int {
        return if (target == null) getSafeFallDistance(0.0f) else getSafeFallDistance(health - 1.0f)
    }

    override fun handleFallDamage(
        fallDistance: Double, damagePerDistance: Float, damageSource: DamageSource?
    ): Boolean {
        val ret = super.handleFallDamage(fallDistance, damagePerDistance, damageSource)
        currentFuseTime += (fallDistance * 1.5).toInt()
        if (currentFuseTime > fuseTime - 5) currentFuseTime = fuseTime - 5
        return ret
    }

    override fun initDataTracker(builder: DataTracker.Builder) {
        super.initDataTracker(builder)
        builder.add(FUSE_SPEED, -1)
        builder.add(CHARGED, DEFAULT_CHARGED)
        builder.add(IGNITED, DEFAULT_IGNITED)
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound) {
        super.writeCustomDataToNbt(nbt)
        nbt.putBoolean("powered", isCharged)
        nbt.putShort("Fuse", fuseTime.toShort())
        nbt.putByte("ExplosionRadius", explosionRadius.toByte())
        nbt.putBoolean("ignited", isIgnited)
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound) {
        super.readCustomDataFromNbt(nbt)
        dataTracker.set(CHARGED, nbt.getBoolean("powered", false))
        fuseTime = nbt.getShort("Fuse", 30.toShort()).toInt()
        explosionRadius = nbt.getByte("ExplosionRadius", 3.toByte()).toInt()
        if (nbt.getBoolean("ignited", false)) ignite()
    }

    override fun playEatSound() = playSound(SoundEvents.ENTITY_CAT_EAT, 1.0f, 1.0f)

    override fun tick() {
        if (isAlive) {
            lastFuseTime = currentFuseTime
            if (isIgnited) fuseSpeed = 1

            val i: Int = fuseSpeed
            if (i > 0 && currentFuseTime == 0) {
                playSound(SoundEvents.ENTITY_CREEPER_PRIMED, 1.0f, 0.5f)
                emitGameEvent(GameEvent.PRIME_FUSE)
            }

            currentFuseTime += i
            if (currentFuseTime < 0) {
                currentFuseTime = 0
            }

            if (currentFuseTime >= fuseTime) {
                currentFuseTime = fuseTime
                explode()
            }
        }

        super.tick()

        if (temptGoal != null && temptGoal!!.isActive && !isTamed && age % 100 == 0) {
            playSound(SoundEvents.ENTITY_CAT_BEG_FOR_FOOD, 1.0f, 1.0f)
        }
    }

    override fun createChild(world: ServerWorld?, entity: PassiveEntity?): CreeperPetEntity? {
        val creeperPetEntity = EntityTypes.CREEPER_PET.create(world, SpawnReason.BREEDING)
        if (creeperPetEntity != null && entity is CreeperPetEntity) {
            if (isTamed) {
                creeperPetEntity.setOwner(ownerReference)
                creeperPetEntity.setTamed(true, true)
            }
        }

        return creeperPetEntity
    }

    override fun canBreedWith(other: AnimalEntity?) =
        if (!isTamed) false else if (other !is CatEntity) false else other.isTamed && super.canBreedWith(other)

    override fun setTarget(target: LivingEntity?) {
        if (target !is GoatEntity) super.setTarget(target)
    }

    override fun getHurtSound(source: DamageSource?): SoundEvent = SoundEvents.ENTITY_CREEPER_HURT
    override fun getDeathSound(): SoundEvent = SoundEvents.ENTITY_CREEPER_DEATH

    override fun dropEquipment(world: ServerWorld?, source: DamageSource, causedByPlayer: Boolean) {
        super.dropEquipment(world, source, causedByPlayer)
        val entity = source.attacker
        if (entity !== this && entity is CreeperPetEntity && entity.shouldDropHead()) {
            entity.onHeadDropped()
            dropItem(world, Items.CREEPER_HEAD)
        }
    }

    override fun tryAttack(world: ServerWorld?, target: Entity?) = true

    @Suppress("MemberVisibilityCanBePrivate")
    val isCharged: Boolean get() = dataTracker.get(CHARGED)

    @Suppress("unused")
    fun getLerpedFuseTime(tickProgress: Float): Float {
        return MathHelper.lerp(tickProgress, lastFuseTime.toFloat(), currentFuseTime.toFloat()) / (fuseTime - 2)
    }

    var fuseSpeed: Int
        get() = dataTracker.get(FUSE_SPEED)
        set(value) = dataTracker.set(FUSE_SPEED, value)

    override fun onStruckByLightning(world: ServerWorld?, lightning: LightningEntity?) {
        super.onStruckByLightning(world, lightning)
        dataTracker.set(CHARGED, true)
    }

    override fun interactMob(player: PlayerEntity, hand: Hand?): ActionResult {
        val itemStack = player.getStackInHand(hand)

        when {
            isTamed -> {
                if (isOwner(player)) {
                    if (isBreedingItem(itemStack) && health < maxHealth) {
                        if (!world.isClient()) {
                            eat(player, hand, itemStack)
                            val foodComponent = itemStack.get(DataComponentTypes.FOOD)
                            heal(foodComponent?.nutrition()?.toFloat() ?: 1.0f)
                            playEatSound()
                        }

                        return ActionResult.SUCCESS
                    }

                    val actionResult = super.interactMob(player, hand)
                    if (!actionResult.isAccepted) {
                        isSitting = !isSitting
                        playSound(SoundEvents.ENTITY_CREEPER_HURT)
                        return ActionResult.SUCCESS
                    }

                    return actionResult
                }
            }

            isBreedingItem(itemStack) -> {
                if (!world.isClient()) {
                    eat(player, hand, itemStack)
                    tryTame(player)
                    setPersistent()
                    playEatSound()
                }

                return ActionResult.SUCCESS
            }

            itemStack.isIn(ItemTags.CREEPER_IGNITERS) -> {
                val soundEvent =
                    if (itemStack.isOf(Items.FIRE_CHARGE)) SoundEvents.ITEM_FIRECHARGE_USE else SoundEvents.ITEM_FLINTANDSTEEL_USE
                world.playSound(player, x, y, z, soundEvent, soundCategory, 1.0f, random.nextFloat() * 0.4f + 0.8f)
                if (!world.isClient) {
                    ignite()
                    if (!itemStack.isDamageable) {
                        itemStack.decrement(1)
                    } else {
                        itemStack.damage(1, player, getSlotForHand(hand))
                    }
                }

                return ActionResult.SUCCESS
            }
        }

        val actionResult = super.interactMob(player, hand)
        if (actionResult.isAccepted) setPersistent()
        return actionResult
    }

    override fun isBreedingItem(stack: ItemStack): Boolean {
        return stack.isIn(ItemTags.CAT_FOOD)
    }

    override fun canImmediatelyDespawn(distanceSquared: Double): Boolean {
        return !isTamed && age > 2400
    }

    private fun tryTame(player: PlayerEntity) {
        if (random.nextInt(3) == 0) {
            setTamedBy(player)
            isSitting = true
            world.sendEntityStatus(this, EntityStatuses.ADD_POSITIVE_PLAYER_REACTION_PARTICLES)
        } else {
            world.sendEntityStatus(this, EntityStatuses.ADD_NEGATIVE_PLAYER_REACTION_PARTICLES)
        }
    }

    private fun explode() {
        if (world is ServerWorld) {
            val serverWorld = world as ServerWorld
            val f = if (isCharged) 2.0f else 1.0f
            serverWorld.createExplosion(this, x, y, z, explosionRadius * f, World.ExplosionSourceType.MOB)
            spawnEffectsCloud()
        }
    }

    private fun spawnEffectsCloud() {
        val collection = statusEffects
        if (!collection.isEmpty()) {
            val areaEffectCloudEntity = AreaEffectCloudEntity(world, x, y, z)
            areaEffectCloudEntity.radius = 2.5f
            areaEffectCloudEntity.radiusOnUse = -0.5f
            areaEffectCloudEntity.waitTime = 10
            areaEffectCloudEntity.duration = 300
            areaEffectCloudEntity.setPotionDurationScale(0.25f)
            areaEffectCloudEntity.radiusGrowth = -areaEffectCloudEntity.radius / areaEffectCloudEntity.duration

            for (statusEffectInstance in collection) {
                areaEffectCloudEntity.addEffect(StatusEffectInstance(statusEffectInstance))
            }

            world.spawnEntity(areaEffectCloudEntity)
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    val isIgnited: Boolean get() = dataTracker.get(IGNITED)

    @Suppress("MemberVisibilityCanBePrivate")
    fun ignite() {
        dataTracker.set(IGNITED, true)
    }

    fun shouldDropHead(): Boolean {
        return isCharged && headsDropped < 1
    }

    fun onHeadDropped() {
        headsDropped++
    }

    /**
     * This method is used to determine what this entity will look like on client for specific player
     *
     * @return Vanilla/Modded entity type
     */
    override fun getPolymerEntityType(context: PacketContext?): EntityType<*> {
        return EntityType.CREEPER
    }

    override fun modifyRawTrackedData(
        data: MutableList<DataTracker.SerializedEntry<*>>?, player: ServerPlayerEntity?, initial: Boolean
    ) {
        data?.forEach {
            Utils.Logger.debug("{} {} {} {} {}", FUSE_SPEED.id, CHARGED.id, IGNITED.id, TAMEABLE_FLAGS.id, OWNER_UUID.id)
            Utils.Logger.debug("id {} value {} handler {}", it.id, it.value, it.handler)
        }
    }
}
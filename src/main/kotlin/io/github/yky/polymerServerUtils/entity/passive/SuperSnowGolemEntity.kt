package io.github.yky.polymerServerUtils.entity.passive

import eu.pb4.polymer.core.api.entity.PolymerEntity
import io.github.yky.polymerServerUtils.entity.EntityTypes
import io.github.yky.polymerServerUtils.entity.projectile.thrown.SuperSnowballEntity
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.*
import net.minecraft.entity.ai.RangedAttackMob
import net.minecraft.entity.ai.goal.*
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedData
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.mob.Angerable
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.mob.Monster
import net.minecraft.entity.passive.GolemEntity
import net.minecraft.entity.passive.IronGolemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.entity.projectile.thrown.SnowballEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.TimeHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.intprovider.UniformIntProvider
import net.minecraft.world.World
import net.minecraft.world.event.GameEvent
import xyz.nucleoid.packettweaker.PacketContext
import java.util.*
import kotlin.math.max
import kotlin.math.sqrt

class SuperSnowGolemEntity(entityType: EntityType<out SuperSnowGolemEntity?>, world: World?) :
    GolemEntity(entityType, world), Shearable, RangedAttackMob, PolymerEntity, Angerable {
    companion object {
        val SUPER_SNOW_GOLEM_FLAGS: TrackedData<Byte> =
            DataTracker.registerData(SuperSnowGolemEntity::class.java, TrackedDataHandlerRegistry.BYTE)

        const val HAS_PUMPKIN_FLAG: Byte = 16
        const val DEFAULT_HAS_PUMPKIN: Boolean = true
        const val PUMPKIN_NBT = "Pumpkin"

        private const val HEALTH_PER_SNOWBALL = 25
        private val ANGER_TIME_RANGE: UniformIntProvider = TimeHelper.betweenSeconds(20, 39)

        fun createSuperSnowGolemAttributes(): DefaultAttributeContainer.Builder =
            createMobAttributes().add(EntityAttributes.MAX_HEALTH, 100.0).add(EntityAttributes.MOVEMENT_SPEED, 0.3)
                .add(EntityAttributes.KNOCKBACK_RESISTANCE, 1.0).add(EntityAttributes.ATTACK_DAMAGE, 15.0)
                .add(EntityAttributes.STEP_HEIGHT, 1.0)
    }

    private var attackTicksLeft = 0
    private var angerTime = 0
    private var angryAt: UUID? = null

    override fun initGoals() {
        goalSelector.add(1, MeleeAttackGoal(this, 1.0, true))
        goalSelector.add(1, ProjectileAttackGoal(this, 1.5, 5, 80.0f))
        goalSelector.add(2, WanderAroundFarGoal(this, 1.0, 1.0000001E-5f))
        goalSelector.add(2, WanderNearTargetGoal(this, 0.9, 32.0f))
        goalSelector.add(3, WanderAroundPointOfInterestGoal(this, 0.6, false))
        goalSelector.add(4, LookAtEntityGoal(this, PlayerEntity::class.java, 6.0f))
        goalSelector.add(5, LookAroundGoal(this))
        targetSelector.add(1, RevengeGoal(this))
        targetSelector.add(2, ActiveTargetGoal(this, PlayerEntity::class.java, 10, true, false, ::shouldAngerAt))
        targetSelector.add(
            2, ActiveTargetGoal(
                this, MobEntity::class.java, 10, true, false
            ) { entity: LivingEntity?, _: ServerWorld? -> entity is Monster })
        targetSelector.add(3, UniversalAngerGoal(this, false))
    }

    override fun initDataTracker(builder: DataTracker.Builder) {
        super.initDataTracker(builder)
        builder.add(SUPER_SNOW_GOLEM_FLAGS, HAS_PUMPKIN_FLAG)
    }

    override fun shootAt(target: LivingEntity, pullProgress: Float) {
        val d = target.x - x
        val e = target.eyeY - 1.1f
        val f = target.z - z
        val g = sqrt(d * d + f * f) * 0.2f
        if (world is ServerWorld) {
            val serverWorld = world as ServerWorld
            val itemStack = ItemStack(Items.SNOWBALL)
            ProjectileEntity.spawn(
                SuperSnowballEntity(serverWorld, this, itemStack), serverWorld, itemStack
            ) { entity -> entity.setVelocity(d, e + g - entity.y, f, 2.6f, 0.0f) }
        }

        playSound(SoundEvents.ENTITY_SNOW_GOLEM_SHOOT, 1.0f, 0.4f / (getRandom().nextFloat() * 0.4f + 0.8f))
    }

    /**
     * This method is used to determine what this entity will look like on client for specific player
     *
     * @return Vanilla/Modded entity type
     */
    override fun getPolymerEntityType(context: PacketContext?): EntityType<*> {
        return EntityType.SNOW_GOLEM
    }

    fun hasPumpkin() = (dataTracker.get(SUPER_SNOW_GOLEM_FLAGS).toInt() and 16) != 0

    @Suppress("MemberVisibilityCanBePrivate")
    fun setHasPumpkin(hasPumpkin: Boolean) {
        val b = dataTracker.get(SUPER_SNOW_GOLEM_FLAGS)
        if (hasPumpkin) {
            dataTracker.set(SUPER_SNOW_GOLEM_FLAGS, (b.toInt() or 16).toByte())
        } else {
            dataTracker.set(SUPER_SNOW_GOLEM_FLAGS, (b.toInt() and -17).toByte())
        }
    }

    override fun sheared(world: ServerWorld, shearedSoundCategory: SoundCategory?, shears: ItemStack?) {
        world.playSoundFromEntity(null, this, SoundEvents.ENTITY_SNOW_GOLEM_SHEAR, shearedSoundCategory, 1.0f, 1.0f)
        setHasPumpkin(false)
    }

    override fun isShearable() = isAlive && hasPumpkin()

    override fun pushAway(entity: Entity?) {
        if (entity is Monster && getRandom().nextInt(20) == 0) target = entity as LivingEntity
        super.pushAway(entity)
    }

    override fun tickMovement() {
        super.tickMovement()

        if (attackTicksLeft > 0) attackTicksLeft--
        if (!world.isClient) tickAngerLogic(world as ServerWorld, true)
    }

    override fun shouldSpawnSprintingParticles() =
        velocity.horizontalLengthSquared() > 2.5000003E-7f && random.nextInt(5) == 0

    override fun canTarget(type: EntityType<*>?) =
        type != EntityType.PLAYER && type != EntityTypes.SUPER_SNOW_GOLEM && type != EntityType.IRON_GOLEM

    override fun writeCustomDataToNbt(nbt: NbtCompound) {
        super.writeCustomDataToNbt(nbt)
        nbt.putBoolean(PUMPKIN_NBT, hasPumpkin())
        writeAngerToNbt(nbt)
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound) {
        super.readCustomDataFromNbt(nbt)
        setHasPumpkin(nbt.getBoolean(PUMPKIN_NBT, true))
        readAngerFromNbt(world, nbt)
    }

    override fun chooseRandomAngerTime() {
        angerTime = ANGER_TIME_RANGE[random]
    }

    override fun setAngerTime(angerTime2: Int) {
        angerTime = angerTime2
    }

    override fun getAngerTime() = angerTime

    override fun setAngryAt(angryAt2: UUID?) {
        angryAt = angryAt2
    }

    override fun getAngryAt() = angryAt

    private fun getAttackDamage() = getAttributeValue(EntityAttributes.ATTACK_DAMAGE).toFloat()

    override fun tryAttack(world: ServerWorld, target: Entity): Boolean {
        attackTicksLeft = 10
        world.sendEntityStatus(this, EntityStatuses.PLAY_ATTACK_SOUND)
        val f = getAttackDamage()
        val g = if (f.toInt() > 0) f / 2.0f + random.nextInt(f.toInt()) else f
        val damageSource = damageSources.mobAttack(this)
        val bl = target.damage(world, damageSource, g)
        if (bl) {
            val d = if (target is LivingEntity) target.getAttributeValue(EntityAttributes.KNOCKBACK_RESISTANCE) else 0.0
            val e = max(0.0, 1.0 - d)
            target.velocity = target.velocity.add(0.0, 0.4f * e, 0.0)
            EnchantmentHelper.onTargetDamaged(world, target, damageSource)
        }

        playSound(SoundEvents.ENTITY_IRON_GOLEM_ATTACK, 1.0f, 1.0f)
        return bl
    }

    override fun damage(world: ServerWorld?, source: DamageSource?, amount: Float): Boolean {
        if (world == null || source == null) return false
        if (source.attacker is SuperSnowGolemEntity || source.attacker is IronGolemEntity) return false
        if (source.source is SnowballEntity || (source.source is SuperSnowballEntity && source.attacker !is SuperSnowGolemEntity)) {
            healByPlayer()
            return false
        }
        var changedAmount = amount
        if (source.attacker !is PlayerEntity) changedAmount /= 2
        return super.damage(world, source, changedAmount)
    }

    override fun handleStatus(status: Byte) {
        when (status) {
            EntityStatuses.PLAY_ATTACK_SOUND -> {
                attackTicksLeft = 10
                playSound(SoundEvents.ENTITY_IRON_GOLEM_ATTACK, 1.0f, 1.0f)
            }

            else -> {
                super.handleStatus(status)
            }
        }
    }

    override fun getHurtSound(source: DamageSource?): SoundEvent = SoundEvents.ENTITY_SNOW_GOLEM_HURT
    override fun getDeathSound(): SoundEvent = SoundEvents.ENTITY_SNOW_GOLEM_DEATH

    private fun healByPlayer(): Boolean {
        val oldHealth = health
        heal(HEALTH_PER_SNOWBALL.toFloat())
        if (health == oldHealth) {
            return false
        } else {
            val g = 1.0f + (random.nextFloat() - random.nextFloat()) * 0.2f
            playSound(SoundEvents.ENTITY_IRON_GOLEM_REPAIR, 1.0f, g)
            return true
        }
    }

    override fun interactMob(player: PlayerEntity, hand: Hand?): ActionResult {
        val itemStack = player.getStackInHand(hand)

        when {
            itemStack.isOf(Items.SHEARS) && isShearable -> {
                if (world is ServerWorld) {
                    val serverWorld = world as ServerWorld
                    sheared(serverWorld, SoundCategory.PLAYERS, itemStack)
                    emitGameEvent(GameEvent.SHEAR, player)
                    itemStack.damage(1, player, getSlotForHand(hand))
                }

                return ActionResult.SUCCESS
            }

            itemStack.isOf(Items.SNOWBALL) -> {
                if (healByPlayer()) {
                    itemStack.decrementUnlessCreative(1, player)
                    return ActionResult.SUCCESS
                } else return ActionResult.PASS
            }

            else -> ActionResult.PASS
        }
        return ActionResult.PASS
    }

    override fun getAmbientSound(): SoundEvent = SoundEvents.ENTITY_SNOW_GOLEM_AMBIENT
    override fun getLeashOffset() = Vec3d(0.0, (0.75f * standingEyeHeight).toDouble(), (width * 0.4f).toDouble())
}
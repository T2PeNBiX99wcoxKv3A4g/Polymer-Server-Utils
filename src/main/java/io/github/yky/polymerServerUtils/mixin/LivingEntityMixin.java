package io.github.yky.polymerServerUtils.mixin;

import io.github.yky.polymerServerUtils.item.HahItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
abstract class LivingEntityMixin extends Entity {
    LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    public abstract ItemStack getStackInHand(Hand hand);

    @Inject(method = "canTakeDamage", at = @At("HEAD"), cancellable = true)
    private void canTakeDamage(CallbackInfoReturnable<Boolean> cir) {
        var main = getStackInHand(Hand.MAIN_HAND);
        var off = getStackInHand(Hand.OFF_HAND);

        if (main.getItem() instanceof HahItem || off.getItem() instanceof HahItem) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void damage(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        var main = getStackInHand(Hand.MAIN_HAND);
        var off = getStackInHand(Hand.OFF_HAND);

        if (main.getItem() instanceof HahItem || off.getItem() instanceof HahItem) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}

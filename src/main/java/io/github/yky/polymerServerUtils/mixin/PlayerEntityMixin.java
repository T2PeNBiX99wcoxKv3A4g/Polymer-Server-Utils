package io.github.yky.polymerServerUtils.mixin;

import io.github.yky.polymerServerUtils.item.HahItem;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
abstract class PlayerEntityMixin extends LivingEntity {
    PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    private ItemStack selectedItem;

    @Shadow
    public abstract PlayerAbilities getAbilities();

    @Shadow
    public abstract void sendAbilitiesUpdate();

    @Unique
    private boolean prevAllowFlying;

    @Unique
    private boolean prevFlying;

    @Unique
    private boolean isChangeBefore;

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isSpectator()Z", shift = At.Shift.AFTER))
    private void onTick(CallbackInfo ci) {
        var abilities = getAbilities();

        if (selectedItem.getItem() instanceof HahItem) {
            if (!isChangeBefore) {
                prevAllowFlying = abilities.allowFlying;
                prevFlying = abilities.flying;
            }

            abilities.allowFlying = true;
            sendAbilitiesUpdate();
            isChangeBefore = true;
        } else {
            if (isChangeBefore) {
                abilities.allowFlying = prevAllowFlying;
                abilities.flying = prevFlying;
                sendAbilitiesUpdate();
                isChangeBefore = false;
            }
        }
    }
}

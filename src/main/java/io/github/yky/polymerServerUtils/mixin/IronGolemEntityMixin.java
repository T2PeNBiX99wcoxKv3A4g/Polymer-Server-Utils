package io.github.yky.polymerServerUtils.mixin;

import io.github.yky.polymerServerUtils.entity.EntityTypes;
import io.github.yky.polymerServerUtils.entity.passive.SuperSnowGolemEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.entity.passive.IronGolemEntity;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IronGolemEntity.class)
abstract class IronGolemEntityMixin extends GolemEntity {
    IronGolemEntityMixin(EntityType<? extends GolemEntity> entityType, World world) {
        super(entityType, world);
    }
    
    @Inject(method = "canTarget", at = @At("HEAD"), cancellable = true)
    private void canTarget(EntityType<?> type, CallbackInfoReturnable<Boolean> cir){
        if (type != EntityTypes.SUPER_SNOW_GOLEM) return;
        cir.setReturnValue(false);
        cir.cancel();
    }

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void damage(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.getAttacker() instanceof SuperSnowGolemEntity) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}

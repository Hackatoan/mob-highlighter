package com.hackatoa.mobhighlighter.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import com.hackatoa.mobhighlighter.client.MobHighlightManager;

@Environment(EnvType.CLIENT)
@Mixin(Entity.class)
public abstract class MixinEntity {

    @Inject(method = "isCurrentlyGlowing", at = @At("HEAD"), cancellable = true)
    private void onIsCurrentlyGlowing(CallbackInfoReturnable<Boolean> cir) {
        Entity self = (Entity) (Object) this;
        if (!MobHighlightManager.INSTANCE.isSelectedType(self.getType())) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        double rangeSq = (double) MobHighlightManager.RANGE * MobHighlightManager.RANGE;
        if (self.distanceToSqr(mc.player) > rangeSq) return;
        cir.setReturnValue(true);
    }
}

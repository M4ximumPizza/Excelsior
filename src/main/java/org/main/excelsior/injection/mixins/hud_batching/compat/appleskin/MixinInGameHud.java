package org.main.excelsior.injection.mixins.hud_batching.compat.appleskin;

import net.minecraft.client.gui.hud.InGameHud;
import org.main.excelsior.Excelsior;
import org.main.excelsior.feature.batching.BatchingBuffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = InGameHud.class, priority = 1500)
public abstract class MixinInGameHud {

    @Unique
    private boolean wasHudBatching;

    @Inject(method = "renderStatusBars", at = @At(value = "CONSTANT", args = "stringValue=food", shift = At.Shift.BEFORE))
    private void endHudBatching(CallbackInfo ci) {
        if (Excelsior.runtimeConfig.hud_batching && BatchingBuffers.isTextureBatching()) {
            BatchingBuffers.endHudBatching();
            this.wasHudBatching = true;
        }
    }

    @Inject(method = "renderStatusBars", at = @At(value = "CONSTANT", args = "stringValue=food", shift = At.Shift.BY, by = 2))
    private void beginHudBatching(CallbackInfo ci) {
        if (this.wasHudBatching) {
            BatchingBuffers.beginHudBatching();
            this.wasHudBatching = false;
        }
    }

}
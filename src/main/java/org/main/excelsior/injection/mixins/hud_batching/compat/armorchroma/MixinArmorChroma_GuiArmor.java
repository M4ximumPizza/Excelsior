/*
 *  Copyright (c) 2023 M4ximumpizza - All Rights Reserved.
 */
package org.main.excelsior.injection.mixins.hud_batching.compat.armorchroma;

import org.main.excelsior.Excelsior;
import org.main.excelsior.feature.batching.BatchingBuffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("UnresolvedMixinReference")
@Mixin(targets = "nukeduck.armorchroma.GuiArmor", remap = false)
@Pseudo
public abstract class MixinArmorChroma_GuiArmor {

    @Unique
    private boolean wasHudBatching;

    @Inject(method = "draw", at = @At("HEAD"))
    private void endHudBatching(CallbackInfo ci) {
        if (Excelsior.runtimeConfig.hud_batching && BatchingBuffers.isTextureBatching()) {
            BatchingBuffers.endHudBatching();
            this.wasHudBatching = true;
        }
    }

    @Inject(method = "draw", at = @At("RETURN"))
    private void beginHudBatching(CallbackInfo ci) {
        if (this.wasHudBatching) {
            BatchingBuffers.beginHudBatching();
            this.wasHudBatching = false;
        }
    }

}
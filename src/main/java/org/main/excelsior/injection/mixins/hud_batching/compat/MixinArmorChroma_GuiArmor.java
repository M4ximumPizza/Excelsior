/*
 *  Copyright (c) 2023 M4ximumpizza - All Rights Reserved.
 */
package org.main.excelsior.injection.mixins.hud_batching.compat;

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
    private boolean wasTextureBatching;

    @Inject(method = "draw", at = @At("HEAD"))
    private void if$endTextureBatching(CallbackInfo ci) {
        if (BatchingBuffers.isTextureBatching()) {
            BatchingBuffers.endTextureBatching();
            this.wasTextureBatching = true;
        }
    }

    @Inject(method = "draw", at = @At("RETURN"))
    private void if$beginTextureBatching(CallbackInfo ci) {
        if (this.wasTextureBatching) {
            BatchingBuffers.beginTextureBatching();
            this.wasTextureBatching = false;
        }
    }

}

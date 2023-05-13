/*
 *  Copyright (c) 2023 M4ximumpizza - All Rights Reserved.
 */
package org.main.excelsior.injection.mixins.core;

import net.minecraft.client.gui.hud.DebugHud;
import org.main.excelsior.Excelsior;
import org.main.excelsior.feature.core.BufferBuilderPool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = DebugHud.class, priority = 9999)
public abstract class MixinDebugHud {

    @Inject(method = "getRightText", at = @At("RETURN"))
    private void appendAllocationInfo(CallbackInfoReturnable<List<String>> cir) {
        if (Excelsior.config.dont_add_info_into_debug_hud) return;

        cir.getReturnValue().add("");
        cir.getReturnValue().add("Excelsior");
        cir.getReturnValue().add("Buffer Pool: " + BufferBuilderPool.getAllocatedSize());
    }

}

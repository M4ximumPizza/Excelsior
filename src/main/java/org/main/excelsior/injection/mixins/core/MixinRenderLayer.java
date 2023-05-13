/*
 *  Copyright (c) 2023 M4ximumpizza - All Rights Reserved.
 */
package org.main.excelsior.injection.mixins.core;

import net.minecraft.client.render.RenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = RenderLayer.class, priority = 500)
public abstract class MixinRenderLayer {

    @ModifyArg(method = {
            "method_34834" /*TEXT*/,
            "method_34833" /*TEXT_INTENSITY*/,
            "method_36437" /*TEXT_POLYGON_OFFSET*/,
            "method_36436" /*TEXT_INTENSITY_POLYGON_OFFSET*/,
            "method_37348" /*TEXT_SEE_THROUGH*/,
            "method_37347" /*TEXT_INTENSITY_SEE_THROUGH*/
    }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;of(Ljava/lang/String;Lnet/minecraft/client/render/VertexFormat;Lnet/minecraft/client/render/VertexFormat$DrawMode;IZZLnet/minecraft/client/render/RenderLayer$MultiPhaseParameters;)Lnet/minecraft/client/render/RenderLayer$MultiPhase;"), index = 5)
    private static boolean if$changeTranslucency(boolean value) {
        return false;
    }

}

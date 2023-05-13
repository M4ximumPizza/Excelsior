/*
 *  Copyright (c) 2023 M4ximumpizza - All Rights Reserved.
 */
package org.main.excelsior.injection.mixins.core;

import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(TextureManager.class)
public abstract class MixinTextureManager {

    @Shadow
    @Final
    private Map<Identifier, AbstractTexture> textures;

    @Inject(method = "destroyTexture", at = @At("RETURN"))
    private void removeDestroyedTexture(Identifier id, CallbackInfo ci) {
        this.textures.remove(id);
    }

}

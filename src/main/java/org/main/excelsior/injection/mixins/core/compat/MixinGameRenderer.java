/*
 *  Copyright (c) 2023 M4ximumpizza - All Rights Reserved.
 */
package org.main.excelsior.injection.mixins.core.compat;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.Identifier;
import org.main.excelsior.Excelsior;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {

    @Shadow
    @Final
    MinecraftClient client;

    @Shadow
    @Final
    private Map<String, ShaderProgram> programs;

    @Unique
    private final List<String> cantBeModified = List.of(
            "rendertype_text",
            "rendertype_text_background",
            "rendertype_text_background_see_through",
            "rendertype_text_intensity",
            "rendertype_text_intensity_see_through",
            "rendertype_text_see_through"
    );

    @Inject(method = "loadPrograms", at = @At("RETURN"))
    private void checkForCoreShaderModifications(ResourceFactory factory, CallbackInfo ci) {
        boolean modified = false;
        for (Map.Entry<String, ShaderProgram> shaderProgramEntry : this.programs.entrySet()) {
            if (!this.cantBeModified.contains(shaderProgramEntry.getKey())) continue;

            final Identifier vertexIdentifier = new Identifier("shaders/core/" + shaderProgramEntry.getValue().getVertexShader().getName() + ".vsh");
            final Resource resource = factory.getResource(vertexIdentifier).orElse(null);
            if (resource != null && !resource.getPack().equals(this.client.getDefaultResourcePack())) {
                modified = true;
                break;
            }
        }

        if (modified && !Excelsior.config.experimental_disable_resource_pack_conflict_handling) {
            Excelsior.LOGGER.warn("Core shader modifications detected. Temporarily disabling some parts of Excelsior.");
            Excelsior.runtimeConfig.hud_batching = false;
        } else {
            Excelsior.resetRuntimeConfig();
        }
    }
}
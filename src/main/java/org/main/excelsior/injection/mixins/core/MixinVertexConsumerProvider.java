/*
 *  Copyright (c) 2023 M4ximumpizza - All Rights Reserved.
 */
package org.main.excelsior.injection.mixins.core;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import org.main.excelsior.Excelsior;
import org.main.excelsior.feature.core.BatchableImmediate;
import org.main.excelsior.injection.interfaces.IBufferBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Map;

@Mixin(VertexConsumerProvider.class)
public interface MixinVertexConsumerProvider {

    /**
     * @author Logan Abernathy
     * @reason Universal Batching
     */
    @Overwrite
    static VertexConsumerProvider.Immediate immediate(Map<RenderLayer, BufferBuilder> layerBuffers, BufferBuilder fallbackBuffer) {
        if (Excelsior.config.debug_only_and_not_recommended_disable_universal_batching) {
            return new VertexConsumerProvider.Immediate(fallbackBuffer, layerBuffers);
        }

        if (!fallbackBuffer.equals(Tessellator.getInstance().getBuffer())) {
            ((IBufferBuilder) fallbackBuffer).release();
        }
        return new BatchableImmediate(layerBuffers);
    }

}

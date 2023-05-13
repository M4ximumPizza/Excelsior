/*
 *  Copyright (c) 2023 M4ximumpizza - All Rights Reserved.
 */
package org.main.excelsior.injection.mixins.fast_buffer_upload;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.VertexBuffer;
import org.lwjgl.opengl.GL15C;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.nio.ByteBuffer;

@Mixin(value = VertexBuffer.class, priority = 500)
public abstract class MixinVertexBuffer {

    @Unique
    private int vertexBufferSize;

    @Unique
    private int indexBufferSize;

    @Redirect(method = "uploadVertexBuffer", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;glBufferData(ILjava/nio/ByteBuffer;I)V"))
    private void optimizeVertexDataUploading(int target, ByteBuffer data, int usage) {
        if (data.remaining() > this.vertexBufferSize) {
            this.vertexBufferSize = data.remaining();
            RenderSystem.glBufferData(target, data, GL15C.GL_DYNAMIC_DRAW);
        } else {
            GL15C.glBufferSubData(target, 0, data);
        }
    }

    @Redirect(method = "uploadIndexBuffer", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;glBufferData(ILjava/nio/ByteBuffer;I)V"))
    private void optimizeIndexDataUploading(int target, ByteBuffer data, int usage) {
        if (data.remaining() > this.indexBufferSize) {
            this.indexBufferSize = data.remaining();
            RenderSystem.glBufferData(target, data, GL15C.GL_DYNAMIC_DRAW);
        } else {
            GL15C.glBufferSubData(target, 0, data);
        }
    }

}

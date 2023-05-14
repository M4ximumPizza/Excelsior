/*
 *  Copyright (c) 2023 M4ximumpizza - All Rights Reserved.
 */
package org.main.excelsior.injection.mixins.disable_error_checking;

import org.main.excelsior.Excelsior;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.system.NativeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = GL11.class, remap = false)
public abstract class MixinGL11 {

    /**
     * @author Logan Abernathy
     * @reason Universal Batching
     */
    @NativeType("GLenum")
    @Overwrite
    public static int glGetError() {
        if (Excelsior.config.experimental_disable_error_checking) {
            return GL11C.GL_NO_ERROR;
        }
        return GL11C.glGetError();
    }

}

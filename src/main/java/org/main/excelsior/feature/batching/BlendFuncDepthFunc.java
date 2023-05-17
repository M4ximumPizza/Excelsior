/*
 *  Copyright (c) 2023 M4ximumpizza - All Rights Reserved.
 */
package org.main.excelsior.feature.batching;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import java.util.Stack;

public record BlendFuncDepthFunc(boolean BLEND, boolean DEPTH_TEST, int GL_BLEND_SRC_RGB, int GL_BLEND_SRC_ALPHA, int GL_BLEND_DST_RGB, int GL_BLEND_DST_ALPHA, int GL_DEPTH_FUNC) {

    private static final Stack<BlendFuncDepthFunc> STACK = new Stack<>();

    public static BlendFuncDepthFunc current() {
        return new BlendFuncDepthFunc(
                GlStateManager.BLEND.capState.state,
                GlStateManager.DEPTH.capState.state,
                GlStateManager.BLEND.srcFactorRGB,
                GlStateManager.BLEND.srcFactorAlpha,
                GlStateManager.BLEND.dstFactorRGB,
                GlStateManager.BLEND.dstFactorAlpha,
                GlStateManager.DEPTH.func
        );
    }

    public void apply() {
        STACK.push(current());

        if (BLEND) {
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GL_BLEND_SRC_RGB, GL_BLEND_DST_RGB, GL_BLEND_SRC_ALPHA, GL_BLEND_DST_ALPHA);
        } else {
            RenderSystem.disableBlend();
        }
        if (DEPTH_TEST) {
            RenderSystem.enableDepthTest();
            RenderSystem.depthFunc(GL_DEPTH_FUNC);
        } else {
            RenderSystem.disableDepthTest();
        }
    }

    public void revert() {
        STACK.pop().apply();
    }
}
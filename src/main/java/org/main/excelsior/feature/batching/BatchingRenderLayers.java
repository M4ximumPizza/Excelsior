/*
 *  Copyright (c) 2023 M4ximumpizza - All Rights Reserved.
 */
package org.main.excelsior.feature.batching;

import com.google.common.cache.CacheBuilder;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;

import static net.minecraft.util.Util.memoize;

/**
 * Class which defines how different elements (RenderLayer's) are rendered.
 */
public class BatchingRenderLayers {

    public static final BiFunction<Integer, BlendFuncDepthFunc, RenderLayer> COLORED_TEXTURE = memoize((id, blendFuncDepthFunc) -> new ExcelsiorRenderLayer("texture", VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR, false, () -> {
        RenderSystem.enableBlend();
        RenderSystem.enableTexture();
        blendFuncDepthFunc.apply();
        RenderSystem.setShaderTexture(0, id);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
    }, () -> {
        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.disableTexture();
    }));

    public static final Function<BlendFuncDepthFunc, RenderLayer> FILLED_QUAD = memoize(blendFuncDepthFunc -> new ExcelsiorRenderLayer("filled_quad", VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR, false, () -> {
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        blendFuncDepthFunc.apply();
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
    }, () -> {
        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }));

    public static final RenderLayer GUI_QUAD = new ExcelsiorRenderLayer("gui_quad", VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR, false, () -> {
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.disableDepthTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
        RenderSystem.enableDepthTest();
    });


    public static <A> Function<A, RenderLayer> memoizeTemp(final Function<A, RenderLayer> function) {
        return new Function<>() {
            private final Map<A, RenderLayer> cache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.SECONDS).<A, RenderLayer>build().asMap();

            public RenderLayer apply(final A arg1) {
                return this.cache.computeIfAbsent(arg1, function);
            }
        };
    }

    public static <A, B> BiFunction<A, B, RenderLayer> memoizeTemp(final BiFunction<A, B, RenderLayer> function) {
        return new BiFunction<>() {
            private final Map<Pair<A, B>, RenderLayer> cache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.SECONDS).<Pair<A, B>, RenderLayer>build().asMap();

            public RenderLayer apply(final A arg1, final B arg2) {
                return this.cache.computeIfAbsent(Pair.of(arg1, arg2), (pair) -> function.apply(pair.getLeft(), pair.getRight()));
            }
        };
    }


    private static class ExcelsiorRenderLayer extends RenderLayer {

        private ExcelsiorRenderLayer(final String name, final VertexFormat.DrawMode drawMode, final VertexFormat vertexFormat, final boolean translucent, final Runnable startAction, final Runnable endAction) {
            super("excelsior_" + name, vertexFormat, drawMode, 2048, false, translucent, startAction, endAction);
        }

    }

}

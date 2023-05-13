/*
 *  Copyright (c) 2023 M4ximumpizza - All Rights Reserved.
 */
package org.main.excelsior.feature.batching;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.screen.PlayerScreenHandler;
import org.main.excelsior.feature.core.BatchableImmediate;

public class ItemModelBatchableImmediate extends BatchableImmediate {

    private final boolean guiDepthLighting;

    public ItemModelBatchableImmediate(final boolean guiDepthLighting) {
        super(BatchingBuffers.createLayerBuffers(
                RenderLayer.getArmorGlint(),
                RenderLayer.getArmorEntityGlint(),
                RenderLayer.getGlint(),
                RenderLayer.getDirectGlint(),
                RenderLayer.getGlintTranslucent(),
                RenderLayer.getEntityGlint(),
                RenderLayer.getDirectEntityGlint()
        ));

        this.guiDepthLighting = guiDepthLighting;
    }

    @Override
    public void draw() {
        RenderSystem.getModelViewStack().push();
        RenderSystem.getModelViewStack().loadIdentity();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
        RenderSystem.enableBlend();
        if (this.guiDepthLighting) {
            DiffuseLighting.enableGuiDepthLighting();
        } else {
            DiffuseLighting.disableGuiDepthLighting();
        }
        super.draw();
        if (!this.guiDepthLighting) {
            DiffuseLighting.enableGuiDepthLighting();
        }
        RenderSystem.disableBlend();
        RenderSystem.getModelViewStack().pop();
        RenderSystem.applyModelViewMatrix();
    }

}

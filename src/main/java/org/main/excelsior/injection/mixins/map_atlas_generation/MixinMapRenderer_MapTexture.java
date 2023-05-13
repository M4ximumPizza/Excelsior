/*
 *  Copyright (c) 2023 M4ximumpizza - All Rights Reserved.
 */
package org.main.excelsior.injection.mixins.map_atlas_generation;

import net.minecraft.block.MapColor;
import net.minecraft.client.render.MapRenderer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.item.map.MapState;
import net.minecraft.util.Identifier;
import org.main.excelsior.Excelsior;
import org.main.excelsior.feature.map_atlas_generation.MapAtlasTexture;
import org.main.excelsior.injection.interfaces.IMapRenderer;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.main.excelsior.feature.map_atlas_generation.MapAtlasTexture.ATLAS_SIZE;
import static org.main.excelsior.feature.map_atlas_generation.MapAtlasTexture.MAP_SIZE;

@Mixin(value = MapRenderer.MapTexture.class, priority = 1100) // TODO: Workaround for Porting-Lib which relies on the LVT to be intact
public abstract class MixinMapRenderer_MapTexture {

    @Shadow
    private MapState state;

    @Mutable
    @Shadow
    @Final
    private NativeImageBackedTexture texture;

    @Unique
    private static final NativeImageBackedTexture DUMMY_TEXTURE;

    @Unique
    private int atlasX;

    @Unique
    private int atlasY;

    @Unique
    private MapAtlasTexture atlasTexture;

    static {
        try {
            DUMMY_TEXTURE = (NativeImageBackedTexture) Excelsior.UNSAFE.allocateInstance(NativeImageBackedTexture.class);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/lang/Object;<init>()V", shift = At.Shift.AFTER, remap = false))
    private void initAtlasParameters(MapRenderer mapRenderer, int id, MapState state, CallbackInfo ci) {
        final int packedLocation = ((IMapRenderer) mapRenderer).getAtlasMapping(id);
        if (packedLocation == -1) {
            Excelsior.LOGGER.warn("Map " + id + " is not in an atlas");
            // Leave atlasTexture null to indicate that this map is not in an atlas, and it should use the vanilla system instead
            return;
        }

        this.atlasX = ((packedLocation >> 8) & 0xFF) * MAP_SIZE;
        this.atlasY = (packedLocation & 0xFF) * MAP_SIZE;
        this.atlasTexture = ((IMapRenderer) mapRenderer).getMapAtlasTexture(packedLocation >> 16);
    }

    @Redirect(method = "<init>", at = @At(value = "NEW", target = "net/minecraft/client/texture/NativeImageBackedTexture"))
    private NativeImageBackedTexture dontAllocateTexture(int width, int height, boolean useMipmaps) {
        if (this.atlasTexture != null) {
            return DUMMY_TEXTURE;
        } else {
            return new NativeImageBackedTexture(width, height, useMipmaps);
        }
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/TextureManager;registerDynamicTexture(Ljava/lang/String;Lnet/minecraft/client/texture/NativeImageBackedTexture;)Lnet/minecraft/util/Identifier;"))
    private Identifier getAtlasTextureIdentifier(TextureManager textureManager, String id, NativeImageBackedTexture texture) {
        if (this.atlasTexture != null) {
            this.texture = null; // Don't leave the texture field pointing to the uninitialized dummy texture
            return this.atlasTexture.getIdentifier();
        } else {
            return textureManager.registerDynamicTexture(id, texture);
        }
    }

    @Inject(method = "updateTexture", at = @At("HEAD"), cancellable = true)
    private void updateAtlasTexture(CallbackInfo ci) {
        if (this.atlasTexture != null) {
            ci.cancel();
            final NativeImageBackedTexture atlasTexture = this.atlasTexture.getTexture();
            final NativeImage atlasImage = atlasTexture.getImage();
            if (atlasImage == null) {
                throw new IllegalStateException("Atlas texture has already been closed");
            }

            for (int x = 0; x < MAP_SIZE; x++) {
                for (int y = 0; y < MAP_SIZE; y++) {
                    final int i = x + y * MAP_SIZE;
                    atlasImage.setColor(this.atlasX + x, this.atlasY + y, MapColor.getRenderColor(this.state.colors[i]));
                }
            }
            atlasTexture.bindTexture();
            atlasImage.upload(0, this.atlasX, this.atlasY, this.atlasX, this.atlasY, MAP_SIZE, MAP_SIZE, false, false);
        }
    }

    @Redirect(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumer;texture(FF)Lnet/minecraft/client/render/VertexConsumer;"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumer;vertex(Lorg/joml/Matrix4f;FFF)Lnet/minecraft/client/render/VertexConsumer;", ordinal = 0), to = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumer;next()V", ordinal = 3)))
    private VertexConsumer drawAtlasTexture(VertexConsumer instance, float u, float v) {
        if (this.atlasTexture != null) {
            if (u == 0 && v == 1) {
                u = (float) this.atlasX / ATLAS_SIZE;
                v = (float) (this.atlasY + MAP_SIZE) / ATLAS_SIZE;
            } else if (u == 1 && v == 1) {
                u = (float) (this.atlasX + MAP_SIZE) / ATLAS_SIZE;
                v = (float) (this.atlasY + MAP_SIZE) / ATLAS_SIZE;
            } else if (u == 1 && v == 0) {
                u = (float) (this.atlasX + MAP_SIZE) / ATLAS_SIZE;
                v = (float) this.atlasY / ATLAS_SIZE;
            } else if (u == 0 && v == 0) {
                u = (float) this.atlasX / ATLAS_SIZE;
                v = (float) this.atlasY / ATLAS_SIZE;
            }
        }

        return instance.texture(u, v);
    }

    @Inject(method = "close", at = @At("HEAD"), cancellable = true)
    private void dontCloseDummyTexture(CallbackInfo ci) {
        if (this.atlasTexture != null) {
            ci.cancel();
        }
    }

}

/*
 *  Copyright (c) 2023 M4ximumpizza - All Rights Reserved.
 */
package org.main.excelsior.injection.mixins.map_atlas_generation;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.render.MapRenderer;
import net.minecraft.item.map.MapState;
import org.main.excelsior.feature.map_atlas_generation.MapAtlasTexture;
import org.main.excelsior.injection.interfaces.IMapRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MapRenderer.class)
public abstract class MixinMapRenderer implements IMapRenderer {

    @Unique
    private final Int2ObjectMap<MapAtlasTexture> mapAtlasTextures = new Int2ObjectOpenHashMap<>();

    @Unique
    private final Int2IntMap mapIdToAtlasMapping = new Int2IntOpenHashMap();

    @Inject(method = "clearStateTextures", at = @At("RETURN"))
    private void clearMapAtlasTextures(final CallbackInfo ci) {
        for (MapAtlasTexture texture : this.mapAtlasTextures.values()) {
            texture.close();
        }

        this.mapAtlasTextures.clear();
        this.mapIdToAtlasMapping.clear();
    }

    @Inject(method = "getMapTexture", at = @At("HEAD"))
    private void createMapAtlasTexture(int id, MapState state, CallbackInfoReturnable<MapRenderer.MapTexture> cir) {
        this.mapIdToAtlasMapping.computeIfAbsent(id, k -> {
            for (MapAtlasTexture atlasTexture : this.mapAtlasTextures.values()) {
                final int location = atlasTexture.getNextMapLocation();
                if (location != -1) {
                    return location;
                }
            }

            final MapAtlasTexture atlasTexture = new MapAtlasTexture(this.mapAtlasTextures.size());
            this.mapAtlasTextures.put(atlasTexture.getId(), atlasTexture);
            return atlasTexture.getNextMapLocation();
        });
    }

    @Override
    public MapAtlasTexture getMapAtlasTexture(int id) {
        return this.mapAtlasTextures.get(id);
    }

    @Override
    public int getAtlasMapping(int mapId) {
        return this.mapIdToAtlasMapping.getOrDefault(mapId, -1);
    }

}

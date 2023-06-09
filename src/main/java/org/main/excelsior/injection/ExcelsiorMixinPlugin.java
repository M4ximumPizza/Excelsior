/*
 *  Copyright (c) 2023 M4ximumpizza - All Rights Reserved.
 */
package org.main.excelsior.injection;

import net.fabricmc.loader.api.FabricLoader;
import org.main.excelsior.Excelsior;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class ExcelsiorMixinPlugin implements IMixinConfigPlugin {

    private final String mixinPackage;

    public ExcelsiorMixinPlugin() {
        this.mixinPackage = getClass().getPackage().getName() + ".";
    }

    @Override
    public void onLoad(String mixinPackage) {
        Excelsior.loadConfig();

        if (!Excelsior.config.debug_only_and_not_recommended_disable_mod_conflict_handling && FabricLoader.getInstance().isModLoaded("slight-gui-modifications")) {
            Excelsior.LOGGER.warn("Slight GUI Modifications detected. Force disabling HUD Batching optimization.");
            Excelsior.config.hud_batching = false;
        }
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (!mixinClassName.startsWith(mixinPackage))
            return false;

        String packageName = mixinClassName.substring(mixinPackage.length());

        if (!Excelsior.config.font_atlas_resizing && packageName.startsWith("font_atlas_resizing"))
            return false;
        if (!Excelsior.config.map_atlas_generation && packageName.startsWith("map_atlas_generation"))
            return false;
        if (!Excelsior.config.hud_batching && packageName.startsWith("hud_batching"))
            return false;
        if (!Excelsior.config.fast_text_lookup && packageName.startsWith("fast_text_lookup"))
            return false;
        if (!Excelsior.config.fast_buffer_upload && packageName.startsWith("fast_buffer_upload"))
            return false;
        if (!Excelsior.config.experimental_disable_error_checking && packageName.startsWith("disable_error_checking"))
            return false;

        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

}
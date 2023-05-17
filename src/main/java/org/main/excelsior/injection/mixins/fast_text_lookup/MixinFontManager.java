/*
 *  Copyright (c) 2023 M4ximumpizza - All Rights Reserved.
 */
package org.main.excelsior.injection.mixins.fast_text_lookup;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.FontManager;
import net.minecraft.client.font.FontStorage;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.function.Function;

@Mixin(value = FontManager.class, priority = 500)
public abstract class MixinFontManager {

    @Shadow
    @Final
    private Map<Identifier, FontStorage> fontStorages;

    @Shadow
    private Map<Identifier, Identifier> idOverrides;

    @Shadow
    protected abstract FontStorage method_27542(Identifier par1);

    @Unique
    private final Map<Identifier, FontStorage> overriddenFontStorages = new Object2ObjectOpenHashMap<>();

    @Unique
    private FontStorage defaultFontStorage;

    @Unique
    private FontStorage unicodeFontStorage;

    @Inject(method = "reload(Lnet/minecraft/client/font/FontManager$ProviderIndex;Lnet/minecraft/util/profiler/Profiler;)V", at = @At("RETURN"))
    private void rebuildOverriddenFontStoragesOnReload(CallbackInfo ci) {
        this.rebuildOverriddenFontStorages();
    }

    @Inject(method = "setIdOverrides", at = @At("RETURN"))
    private void rebuildOverriddenFontStoragesOnChange(CallbackInfo ci) {
        this.rebuildOverriddenFontStorages();
    }

    @ModifyArg(method = {"createTextRenderer", "createAdvanceValidatingTextRenderer"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;<init>(Ljava/util/function/Function;Z)V"))
    private Function<Identifier, FontStorage> overrideFontStorage(Function<Identifier, FontStorage> original) {
        return id -> {
            // Fast path for default font
            if (MinecraftClient.DEFAULT_FONT_ID.equals(id) && this.defaultFontStorage != null) {
                return this.defaultFontStorage;
            } else if (MinecraftClient.UNICODE_FONT_ID.equals(id) && this.unicodeFontStorage != null) {
                return this.unicodeFontStorage;
            }

            // Try to get the font storage from the overridden map otherwise
            final FontStorage storage = this.overriddenFontStorages.get(id);
            if (storage != null) {
                return storage;
            }

            // In case some mod is doing cursed stuff call the original function
            return original.apply(id);
        };
    }

    @Unique
    private void rebuildOverriddenFontStorages() {
        this.overriddenFontStorages.clear();
        this.overriddenFontStorages.putAll(this.fontStorages);
        for (Identifier key : this.idOverrides.keySet()) {
            this.overriddenFontStorages.put(key, this.method_27542(key));
        }

        this.defaultFontStorage = this.overriddenFontStorages.get(MinecraftClient.DEFAULT_FONT_ID);
        this.unicodeFontStorage = this.overriddenFontStorages.get(MinecraftClient.UNICODE_FONT_ID);
    }
}

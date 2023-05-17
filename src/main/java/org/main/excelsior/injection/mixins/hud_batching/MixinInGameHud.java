/*
 *  Copyright (c) 2023 M4ximumpizza - All Rights Reserved.
 */
package org.main.excelsior.injection.mixins.hud_batching;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.JumpingMount;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import org.main.excelsior.Excelsior;
import org.main.excelsior.feature.batching.BatchingBuffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;

@Mixin(value = InGameHud.class, priority = 500)
public abstract class MixinInGameHud {

    @WrapOperation(method = "render", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderStatusBars(Lnet/minecraft/client/gui/DrawContext;)V"),
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderMountHealth(Lnet/minecraft/client/gui/DrawContext;)V"),
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderCrosshair(Lnet/minecraft/client/gui/DrawContext;)V"),
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/BossBarHud;render(Lnet/minecraft/client/gui/DrawContext;)V"),
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/SubtitlesHud;render(Lnet/minecraft/client/gui/DrawContext;)V"),
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/SpectatorHud;render(Lnet/minecraft/client/gui/DrawContext;)V"),
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/SpectatorHud;renderSpectatorMenu(Lnet/minecraft/client/gui/DrawContext;)V"),
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHeldItemTooltip(Lnet/minecraft/client/gui/DrawContext;)V"),
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/DebugHud;render(Lnet/minecraft/client/gui/DrawContext;)V"),
    })
    private void batching(@Coerce final Object instance, final DrawContext drawContext, final Operation<Void> operation) {
        if (Excelsior.runtimeConfig.hud_batching) {
            BatchingBuffers.beginHudBatching();
            operation.call(instance, drawContext);
            BatchingBuffers.endHudBatching();
        } else {
            operation.call(instance, drawContext);
        }
    }

    @WrapOperation(method = "render", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderStatusEffectOverlay(Lnet/minecraft/client/gui/DrawContext;)V"),
    })
    private void statusOverlayBatching(@Coerce final Object instance, final DrawContext drawContext, final Operation<Void> operation) {
        if (Excelsior.runtimeConfig.hud_batching) {
            BatchingBuffers.beginHudBatching();
            operation.call(instance, drawContext);
            // https://github.com/A5b84/status-effect-bars draws fill over texture
            BatchingBuffers.endTextureBatching();
            BatchingBuffers.endFillBatching();
            BatchingBuffers.endTextBatching();
        } else {
            operation.call(instance, drawContext);
        }
    }

    @WrapOperation(method = "render", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;render(Lnet/minecraft/client/gui/DrawContext;III)V"),
    })
    private void batching(@Coerce final Object instance, final DrawContext drawContext, final int currentTick, final int mouseX, final int mouseY, final Operation<Void> operation) {
        if (Excelsior.runtimeConfig.hud_batching) {
            BatchingBuffers.beginHudBatching();
            operation.call(instance, drawContext, currentTick, mouseX, mouseY);
            BatchingBuffers.endHudBatching();
        } else {
            operation.call(instance, drawContext, currentTick, mouseX, mouseY);
        }
    }

    @WrapOperation(method = "render", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/PlayerListHud;render(Lnet/minecraft/client/gui/DrawContext;ILnet/minecraft/scoreboard/Scoreboard;Lnet/minecraft/scoreboard/ScoreboardObjective;)V"),
    })
    private void batching(@Coerce final Object instance, final DrawContext drawContext, final int scaledWindowWidth, final Scoreboard scoreboard, final ScoreboardObjective objective, final Operation<Void> operation) {
        if (Excelsior.runtimeConfig.hud_batching) {
            BatchingBuffers.beginHudBatching();
            operation.call(instance, drawContext, scaledWindowWidth, scoreboard, objective);
            BatchingBuffers.endHudBatching();
        } else {
            operation.call(instance, drawContext, scaledWindowWidth, scoreboard, objective);
        }
    }

    @WrapOperation(method = "render", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderMountJumpBar(Lnet/minecraft/entity/JumpingMount;Lnet/minecraft/client/gui/DrawContext;I)V"),
    })
    private void batching(@Coerce final Object instance, final JumpingMount mount, final DrawContext drawContext, final int x, final Operation<Void> operation) {
        if (Excelsior.runtimeConfig.hud_batching) {
            BatchingBuffers.beginHudBatching();
            operation.call(instance, mount, drawContext, x);
            BatchingBuffers.endHudBatching();
        } else {
            operation.call(instance, mount, drawContext, x);
        }
    }

    @WrapOperation(method = "render", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V"),
    })
    private void batching(@Coerce final Object instance, final DrawContext drawContext, final ScoreboardObjective objective, final Operation<Void> operation) {
        if (Excelsior.runtimeConfig.hud_batching) {
            BatchingBuffers.beginHudBatching();
            operation.call(instance, drawContext, objective);
            BatchingBuffers.endHudBatching();
        } else {
            operation.call(instance, drawContext, objective);
        }
    }

    @WrapOperation(method = "render", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbar(FLnet/minecraft/client/gui/DrawContext;)V"),
    })
    private void batching(@Coerce final Object instance, final float tickDelta, final DrawContext drawContext, final Operation<Void> operation) {
        if (Excelsior.config.item_hud_batching && Excelsior.runtimeConfig.hud_batching) {
            BatchingBuffers.beginHudBatching();
            BatchingBuffers.beginItemBatching();
            operation.call(instance, tickDelta, drawContext);
            BatchingBuffers.endHudBatching();
            BatchingBuffers.endItemBatching();
        } else {
            operation.call(instance, tickDelta, drawContext);
        }
    }

    @WrapOperation(method = "render", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderExperienceBar(Lnet/minecraft/client/gui/DrawContext;I)V"),
    })
    private void batching(@Coerce final Object instance, final DrawContext drawContext, final int x, final Operation<Void> operation) {
        if (Excelsior.runtimeConfig.hud_batching) {
            BatchingBuffers.beginHudBatching();
            operation.call(instance, drawContext, x);
            BatchingBuffers.endHudBatching();
        } else {
            operation.call(instance, drawContext, x);
        }
    }

}

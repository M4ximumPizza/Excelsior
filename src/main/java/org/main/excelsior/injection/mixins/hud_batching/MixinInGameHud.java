/*
 *  Copyright (c) 2023 M4ximumpizza - All Rights Reserved.
 */
package org.main.excelsior.injection.mixins.hud_batching;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
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
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderStatusBars(Lnet/minecraft/client/util/math/MatrixStack;)V"),
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderMountHealth(Lnet/minecraft/client/util/math/MatrixStack;)V"),
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderCrosshair(Lnet/minecraft/client/util/math/MatrixStack;)V"),
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/BossBarHud;render(Lnet/minecraft/client/util/math/MatrixStack;)V"),
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/SubtitlesHud;render(Lnet/minecraft/client/util/math/MatrixStack;)V"),
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/SpectatorHud;render(Lnet/minecraft/client/util/math/MatrixStack;)V"),
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/SpectatorHud;renderSpectatorMenu(Lnet/minecraft/client/util/math/MatrixStack;)V"),
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHeldItemTooltip(Lnet/minecraft/client/util/math/MatrixStack;)V"),
    })
    private void if$Batching(@Coerce final Object instance, final MatrixStack matrices, final Operation<Void> operation) {
        BatchingBuffers.beginHudBatching();
        operation.call(instance, matrices);
        BatchingBuffers.endHudBatching();
    }

    @WrapOperation(method = "render", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;render(Lnet/minecraft/client/util/math/MatrixStack;III)V"),
    })
    private void if$Batching(@Coerce final Object instance, final MatrixStack matrices, final int currentTick, final int mouseX, final int mouseY, final Operation<Void> operation) {
        BatchingBuffers.beginHudBatching();
        operation.call(instance, matrices, currentTick, mouseX, mouseY);
        BatchingBuffers.endHudBatching();
    }

    @WrapOperation(method = "render", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderStatusEffectOverlay(Lnet/minecraft/client/util/math/MatrixStack;)V"),
    })
    private void if$StatusOverlayBatching(@Coerce final Object instance, final MatrixStack matrices, final Operation<Void> operation) {
        BatchingBuffers.beginHudBatching();
        operation.call(instance, matrices);
        // https://github.com/A5b84/status-effect-bars draws fill over texture
        BatchingBuffers.endTextureBatching();
        BatchingBuffers.endFillBatching();
        BatchingBuffers.endTextBatching();
    }

    @WrapOperation(method = "render", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/PlayerListHud;render(Lnet/minecraft/client/util/math/MatrixStack;ILnet/minecraft/scoreboard/Scoreboard;Lnet/minecraft/scoreboard/ScoreboardObjective;)V"),
    })
    private void if$Batching(@Coerce final Object instance, final MatrixStack matrices, final int scaledWindowWidth, final Scoreboard scoreboard, final ScoreboardObjective objective, final Operation<Void> operation) {
        BatchingBuffers.beginHudBatching();
        operation.call(instance, matrices, scaledWindowWidth, scoreboard, objective);
        BatchingBuffers.endHudBatching();
    }

    @WrapOperation(method = "render", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderMountJumpBar(Lnet/minecraft/entity/JumpingMount;Lnet/minecraft/client/util/math/MatrixStack;I)V"),
    })
    private void if$Batching(@Coerce final Object instance, final JumpingMount mount, final MatrixStack matrices, final int x, final Operation<Void> operation) {
        BatchingBuffers.beginHudBatching();
        operation.call(instance, mount, matrices, x);
        BatchingBuffers.endHudBatching();
    }

    @WrapOperation(method = "render", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderScoreboardSidebar(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/scoreboard/ScoreboardObjective;)V"),
    })
    private void if$Batching(@Coerce final Object instance, final MatrixStack matrices, final ScoreboardObjective objective, final Operation<Void> operation) {
        BatchingBuffers.beginHudBatching();
        operation.call(instance, matrices, objective);
        BatchingBuffers.endHudBatching();
    }

    @WrapOperation(method = "render", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbar(FLnet/minecraft/client/util/math/MatrixStack;)V"),
    })
    private void if$Batching(@Coerce final Object instance, final float tickDelta, final MatrixStack matrices, final Operation<Void> operation) {
        if (Excelsior.config.experimental_item_hud_batching) {
            BatchingBuffers.beginHudBatching();
            BatchingBuffers.beginItemBatching();
            operation.call(instance, tickDelta, matrices);
            BatchingBuffers.endHudBatching();
            BatchingBuffers.endItemBatching();
        } else {
            operation.call(instance, tickDelta, matrices);
        }
    }

    @WrapOperation(method = "render", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderExperienceBar(Lnet/minecraft/client/util/math/MatrixStack;I)V"),
    })
    private void if$Batching(@Coerce final Object instance, final MatrixStack matrices, final int x, final Operation<Void> operation) {
        BatchingBuffers.beginHudBatching();
        operation.call(instance, matrices, x);
        BatchingBuffers.endHudBatching();
    }

}

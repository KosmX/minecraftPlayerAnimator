package dev.kosmx.playerAnim.mixin;

import dev.kosmx.playerAnim.impl.IPlayerAnimationState;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerRenderState.class)
public class PlayerRenderStateMixin implements IPlayerAnimationState {
    @Unique
    boolean playerAnimator$isLocalPlayer = false;

    @Unique
    boolean playerAnimator$isCameraEntity = false;

    @Unique
    AnimationApplier playerAnimator$animationApplier = new AnimationApplier(null);

    @Override
    public boolean playerAnimator$isLocalPlayer() {
        return playerAnimator$isLocalPlayer;
    }

    @Override
    public void playerAnimator$setLocalPlayer(boolean value) {
        playerAnimator$isLocalPlayer = value;
    }

    @Override
    public boolean playerAnimator$isCameraEntity() {
        return playerAnimator$isCameraEntity;
    }

    @Override
    public void playerAnimator$setCameraEntity(boolean value) {
        playerAnimator$isCameraEntity = value;
    }

    @Override
    public void playerAnimator$setAnimationApplier(AnimationApplier value) {
        playerAnimator$animationApplier = value;
    }

    @Override
    public @NotNull AnimationApplier playerAnimator$getAnimationApplier() {
        return playerAnimator$animationApplier;
    }
}


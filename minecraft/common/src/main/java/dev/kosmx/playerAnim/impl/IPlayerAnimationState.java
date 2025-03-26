package dev.kosmx.playerAnim.impl;

import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import org.jetbrains.annotations.NotNull;

/**
 * Extension of PlayerRenderState
 */
public interface IPlayerAnimationState {

    // bool isLocalPlayer
    boolean playerAnimator$isLocalPlayer();
    void playerAnimator$setLocalPlayer(boolean value);

    boolean playerAnimator$isCameraEntity();
    void playerAnimator$setCameraEntity(boolean value);

    // AnimationApplier animationApplier
    void playerAnimator$setAnimationApplier(AnimationApplier value);
    @NotNull AnimationApplier playerAnimator$getAnimationApplier();
}

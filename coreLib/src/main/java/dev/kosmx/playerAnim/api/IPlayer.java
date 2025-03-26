package dev.kosmx.playerAnim.api;

import dev.kosmx.playerAnim.api.layered.AnimationStack;
import org.jetbrains.annotations.NotNull;

/**
 * Mixed in into PlayerEntity and PlayerRenderState
 */
public interface IPlayer {
    /**
     * @deprecated Potential name conflict while remapping, may be removed without further notice
     * use {@link IPlayer#playerAnimator$getAnimationStack()}
     */
    @Deprecated(forRemoval = true)
    default AnimationStack getAnimationStack() {
        return playerAnimator$getAnimationStack();
    }

    @NotNull AnimationStack playerAnimator$getAnimationStack();
}

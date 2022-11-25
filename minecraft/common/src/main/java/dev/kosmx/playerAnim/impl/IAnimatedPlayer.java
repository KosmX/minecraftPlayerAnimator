package dev.kosmx.playerAnim.impl;

import dev.kosmx.playerAnim.api.IPlayer;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Even if it is marked as internal API, this interface should not change
 */

@ApiStatus.Internal
public interface IAnimatedPlayer extends IPlayer {

    /**
     * @deprecated potential name conflict on mixin interface
     * use {@code IAnimatedPlayer#playerAnimator_getAnimation}
     */
    @Deprecated
    default AnimationApplier getAnimation() {
        return playerAnimator_getAnimation();
    }


    AnimationApplier playerAnimator_getAnimation();

    /**
     * Get an animation associated with the player
     * @param id    Animation identifier, please start with your modid to avoid collision
     * @return      animation or <code>null</code> if not exists
     * @apiNote     This function does <strong>not</strong> register the animation, just store it.
     */
    @Nullable
    IAnimation playerAnimator_getAnimation(@NotNull ResourceLocation id);

    /**
     * Set an animation associated with the player
     *
     * @param id        Animation identifier. Please don't override/remove other mod animations, always use your modid!
     * @param animation animation to store in the player, <code>null</code> to clear stored animation
     * @return          The previously stored animation.
     */
    @Nullable
    IAnimation playerAnimator_setAnimation(@NotNull ResourceLocation id, @Nullable IAnimation animation);
}

package dev.kosmx.playerAnim.core.impl;


import dev.kosmx.playerAnim.api.PartKey;
import dev.kosmx.playerAnim.api.TransformType;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonConfiguration;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import dev.kosmx.playerAnim.api.layered.AnimationStack;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.core.util.Pair;
import dev.kosmx.playerAnim.core.util.Vec3f;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Tool to easily play animation to the player.
 */
public class AnimationProcessor {
    @Nullable
    private final IAnimation animation;
    private float tickDelta = 0f;

    public AnimationProcessor(@Nullable IAnimation animation) {
        this.animation = animation;
    }

    @ApiStatus.Internal
    public void tick() {
        if (animation != null) {
            animation.tick();
        }
    }

    public boolean isActive() {
        return animation != null && animation.isActive();
    }

    public Vec3f get3DTransform(PartKey partKey, TransformType type, Vec3f value0) {
        if (animation == null) return value0;
        return animation.get3DTransform(partKey, type, this.tickDelta, value0);
    }

    @ApiStatus.Internal
    public void setTickDelta(float tickDelta) {
        this.tickDelta = tickDelta;
        if (animation != null) {
            this.animation.setupAnim(tickDelta);
        }
    }

    @ApiStatus.Experimental
    public @Nullable IAnimation getAnimation() {
        return animation;
    }

    public boolean isFirstPersonAnimationDisabled() {
        return animation == null || !animation.getFirstPersonMode(tickDelta).isEnabled();
    }

    public @NotNull FirstPersonMode getFirstPersonMode() {
        return animation == null ? FirstPersonMode.NONE : animation.getFirstPersonMode(tickDelta);
    }

    public @NotNull FirstPersonConfiguration getFirstPersonConfiguration() {
        return animation == null ? new FirstPersonConfiguration() : animation.getFirstPersonConfiguration(tickDelta);
    }

    public @NotNull Pair<Float, Float> getBend(PartKey partKey) {
        if (animation == null) return new Pair<>(0f, 0f);
        Vec3f bendVec = this.get3DTransform(partKey, TransformType.BEND, Vec3f.ZERO);
        return new Pair<>(bendVec.getX(), bendVec.getY());
    }

    /**
     * @return Priority of the currently active animation, returns 0 if there are none.
     */
    public int getPriority() {
        if (animation instanceof AnimationStack animationStack) return animationStack.getPriority();
        return 0;
    }
}

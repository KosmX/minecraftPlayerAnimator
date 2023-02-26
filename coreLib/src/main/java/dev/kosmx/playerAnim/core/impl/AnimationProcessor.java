package dev.kosmx.playerAnim.core.impl;


import dev.kosmx.playerAnim.api.TransformType;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonConfiguration;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.core.util.Pair;
import dev.kosmx.playerAnim.core.util.Vec3f;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Tool to easily play animation to the player.
 * internal, do not use
 */
@ApiStatus.Internal
public class AnimationProcessor {
    private final IAnimation animation;
    private float tickDelta = 0f;

    public AnimationProcessor(IAnimation animation) {
        this.animation = animation;
    }

    public void tick() {
        animation.tick();
    }

    public boolean isActive() {
        return animation.isActive();
    }

    public Vec3f get3DTransform(String modelName, TransformType type, Vec3f value0) {
        return animation.get3DTransform(modelName, type, this.tickDelta, value0);
    }

    public void setTickDelta(float tickDelta) {
        this.tickDelta = tickDelta;
        this.animation.setupAnim(tickDelta);
    }

    public boolean isFirstPersonAnimationDisabled() {
        return !animation.getFirstPersonMode(tickDelta).isEnabled();
    }

    public @NotNull FirstPersonMode getFirstPersonMode() {
        return animation.getFirstPersonMode(tickDelta);
    }

    public @NotNull FirstPersonConfiguration getFirstPersonConfiguration() {
        return animation.getFirstPersonConfiguration(tickDelta);
    }

    public Pair<Float, Float> getBend(String modelName) {
        Vec3f bendVec = this.get3DTransform(modelName, TransformType.BEND, Vec3f.ZERO);
        return new Pair<>(bendVec.getX(), bendVec.getY());
    }

}

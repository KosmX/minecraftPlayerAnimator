package dev.kosmx.playerAnim.api.layered;

import dev.kosmx.playerAnim.api.TransformType;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonConfiguration;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import dev.kosmx.playerAnim.core.util.Vec3f;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A container to make swapping animation object easier
 * It will clone the behaviour of the held animation
 * <p>
 * you can put endless AnimationContainer into each other
 * @param <T> Nullable animation
 */
public class AnimationContainer<T extends IAnimation> implements IAnimation {
    @Nullable
    protected T anim;

    public AnimationContainer(@Nullable T anim) {
        this.anim = anim;
    }

    public AnimationContainer() {
        this.anim = null;
    }

    public void setAnim(@Nullable T newAnim) {
        this.anim = newAnim;
    }

    public @Nullable T getAnim() {
        return this.anim;
    }

    @Override
    public boolean isActive() {
        return anim != null && anim.isActive();
    }

    @Override
    public void tick() {
        if (anim != null) anim.tick();
    }

    @Override
    public @NotNull Vec3f get3DTransform(@NotNull String modelName, @NotNull TransformType type, float tickDelta, @NotNull Vec3f value0) {
        return anim == null ? value0 : anim.get3DTransform(modelName, type, tickDelta, value0);
    }

    @Override
    public void setupAnim(float tickDelta) {
        if (this.anim != null) this.anim.setupAnim(tickDelta);
    }

    @Override
    public @NotNull FirstPersonMode getFirstPersonMode(float tickDelta) {
        return anim != null ? anim.getFirstPersonMode(tickDelta) : FirstPersonMode.NONE;
    }

    // Override candidate
    @Override
    public @NotNull FirstPersonConfiguration getFirstPersonConfiguration(float tickDelta) {
        return anim != null ? anim.getFirstPersonConfiguration(tickDelta) : IAnimation.super.getFirstPersonConfiguration(tickDelta);
    }
}

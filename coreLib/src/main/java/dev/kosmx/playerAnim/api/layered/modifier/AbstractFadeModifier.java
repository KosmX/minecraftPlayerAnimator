package dev.kosmx.playerAnim.api.layered.modifier;

import dev.kosmx.playerAnim.api.TransformType;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.core.util.Ease;
import dev.kosmx.playerAnim.core.util.Vec3f;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Use with ModifierLayer.
 * Set an animation as a fade from.
 * length in ticks
 */
public abstract class AbstractFadeModifier extends AbstractModifier {

    protected int time = 0;
    protected int length;

    /**
     * Animation to move from, null if transparent (easing in)
     * use setBeginAnimation(IAnimation); //generated by Lombok plugin
     */
    @Nullable
    @Setter
    protected IAnimation beginAnimation;

    protected AbstractFadeModifier(int length) {
        this.length = length;
    }

    @Override
    public boolean isActive() {
        return super.isActive() || beginAnimation != null && beginAnimation.isActive();
    }

    @Override
    public boolean canRemove() {
        return this.length <= time;
    }

    @Override
    public void setupAnim(float tickDelta) {
        super.setupAnim(tickDelta);
        if (beginAnimation != null) beginAnimation.setupAnim(tickDelta);
    }

    @Override
    public void tick() {
        super.tick();
        if (beginAnimation != null) beginAnimation.tick();
        this.time++;
    }

    @Override
    public @NotNull Vec3f get3DTransform(@NotNull String modelName, @NotNull TransformType type, float tickDelta, @NotNull Vec3f value0) {
        if (calculateProgress(tickDelta) > 1) {
            return super.get3DTransform(modelName, type, tickDelta, value0);
        }
        Vec3f animatedVec = super.get3DTransform(modelName, type, tickDelta, value0);
        float a = getAlpha(modelName, type, calculateProgress(tickDelta));
        Vec3f source = beginAnimation != null && beginAnimation.isActive() ? beginAnimation.get3DTransform(modelName, type, tickDelta, value0) : value0;
        return animatedVec.scale(a).add(source.scale(1 - a)); //This would look much better in Kotlin... (operator overloading)
    }

    protected float calculateProgress(float f) {
        float actualTime = time + f;
        return actualTime / length;
    }

    /**
     * Get the alpha at the given progress
     * @param modelName modelName if you want to handle parts differently
     * @param type      Transform type
     * @param progress  animation progress, float between 0 and 1
     * @return alpha, float between 0 and 1, lower value means less visible from animation
     */
    protected abstract float getAlpha(String modelName, TransformType type, float progress);

    /**
     * Creates a standard fade with some easing in it.
     * @param length ease length in ticks
     * @param ease   ease function from {@link Ease}
     * @return fade modifier
     */
    public static AbstractFadeModifier standardFadeIn(int length, Ease ease) {
        return new AbstractFadeModifier(length) {
            @Override
            protected float getAlpha(String modelName, TransformType type, float progress) {
                return ease.invoke(progress);
            }
        };
    }

    /**
     * Functional constructor for functional folks
     * @param length   ease length
     * @param function ease function
     * @return fade
     */
    public static AbstractFadeModifier functionalFadeIn(int length, EasingFunction function) {
        return new AbstractFadeModifier(length) {
            @Override
            protected float getAlpha(String modelName, TransformType type, float progress) {
                return function.ease(modelName, type, progress);
            }
        };
    }

    @FunctionalInterface
    public interface EasingFunction {
        float ease(String modelName, TransformType type, float value);
    }

}

package dev.kosmx.playerAnim.api.layered;

import dev.kosmx.playerAnim.api.PartKey;
import dev.kosmx.playerAnim.api.TransformType;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonConfiguration;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractModifier;
import dev.kosmx.playerAnim.api.layered.modifier.FirstPersonModifier;
import dev.kosmx.playerAnim.core.util.Ease;
import dev.kosmx.playerAnim.core.util.Vec3f;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 * Layer to easily swap animations, add modifiers or do other sort of effects
 * Modifiers <b>affect</b> each other. For example if you put a fade modifier after a speed modifier, it will be affected by the modifier.
 *
 * @param <T>
 */
public class ModifierLayer<T extends IAnimation> implements IAnimation {

    private final List<AbstractModifier> modifiers = new ArrayList<>();
    @Nullable
    @Getter
    T animation;


    public ModifierLayer(@Nullable T animation, AbstractModifier... modifiers) {
        this.animation = animation;
        Collections.addAll(this.modifiers, modifiers);
    }

    public ModifierLayer() {
        this(null);
    }

    @Override
    public void tick() {
        for (int i = 0; i < modifiers.size(); i++) {
            if (modifiers.get(i).canRemove()) {
                removeModifier(i--);
            }
        }
        if (modifiers.size() > 0) {
            modifiers.get(0).tick();
        } else if (animation != null) animation.tick();
    }

    public void addModifier(@NotNull AbstractModifier modifier, int idx) {
        modifier.setHost(this);
        modifiers.add(idx, modifier);
        this.linkModifiers();
    }

    public void addModifierBefore(@NotNull AbstractModifier modifier) {
        this.addModifier(modifier, 0);
    }

    public void addModifierLast(@NotNull AbstractModifier modifier) {
        this.addModifier(modifier, modifiers.size());
    }

    public void removeModifier(int idx) {
        modifiers.remove(idx);
        this.linkModifiers();
    }


    public void setAnimation(@Nullable T animation) {
        this.animation = animation;
        this.linkModifiers();
    }

    /**
     * Fade out from current animation into new animation.
     * Does not fade if there is currently no active animation
     *
     * @param fadeModifier Fade modifier, use {@link AbstractFadeModifier#standardFadeIn(int, Ease)} for simple fade.
     * @param newAnimation New animation, can be null to fade into default state.
     */
    public void replaceAnimationWithFade(@NotNull AbstractFadeModifier fadeModifier, @Nullable T newAnimation) {
        replaceAnimationWithFade(fadeModifier, newAnimation, false);
    }

    /**
     * Fade out from current to a new animation
     *
     * @param fadeModifier    Fade modifier, use {@link AbstractFadeModifier#standardFadeIn(int, Ease)} for simple fade.
     * @param newAnimation    New animation, can be null to fade into default state.
     * @param fadeFromNothing Do fade even if we go from nothing. (for KeyframeAnimation, it can be false by default)
     */
    public void replaceAnimationWithFade(@NotNull AbstractFadeModifier fadeModifier, @Nullable T newAnimation, boolean fadeFromNothing) {
        if (fadeFromNothing || getAnimation() != null && getAnimation().isActive()) {
            fadeModifier.setBeginAnimation(this.getAnimation());
            addModifierLast(fadeModifier);
        }
        this.setAnimation(newAnimation);
    }

    public int size() {
        return modifiers.size();
    }

    protected void linkModifiers() {
        Iterator<AbstractModifier> modifierIterator = modifiers.iterator();
        if (modifierIterator.hasNext()) {
            AbstractModifier tmp = modifierIterator.next();
            while (modifierIterator.hasNext()) {
                AbstractModifier tmp2 = modifierIterator.next();
                tmp.setAnim(tmp2);
                tmp = tmp2;
            }
            tmp.setAnim(this.animation);
        }
    }


    @Override
    public boolean isActive() {
        if (!modifiers.isEmpty()) {
            return modifiers.get(0).isActive();
        } else if (animation != null) return animation.isActive();
        return false;
    }

    @Override
    public @NotNull Vec3f get3DTransform(@NotNull String modelName, @NotNull TransformType type, float tickDelta, @NotNull Vec3f value0) {
        if (!modifiers.isEmpty()) {
            return modifiers.get(0).get3DTransform(modelName, type, tickDelta, value0);
        } else if (animation != null) return animation.get3DTransform(modelName, type, tickDelta, value0);
        return value0;
    }

    @Override
    public @NotNull Vec3f get3DTransform(@NotNull PartKey modelName, @NotNull TransformType type, float tickDelta, @NotNull Vec3f value0) {
        if (!modifiers.isEmpty()) {
            return modifiers.get(0).get3DTransform(modelName, type, tickDelta, value0);
        } else if (animation != null) return animation.get3DTransform(modelName, type, tickDelta, value0);
        return value0;
    }

    @Override
    public void setupAnim(float tickDelta) {
        if (!modifiers.isEmpty()) {
            modifiers.get(0).setupAnim(tickDelta);
        } else if (animation != null) animation.setupAnim(tickDelta);
    }

    /**
     * Retrieves the {@link FirstPersonMode} for the current object, based on the provided {@code tickDelta}.
     * <p>
     * The method determines the appropriate {@link FirstPersonMode} by following this logic:
     * 1. It first attempts to retrieve the active {@link AbstractModifier} by calling {@link #getFirstPersonModifierOrDefault()}.
     *    If a modifier is found, it delegates the call to the modifier's {@link AbstractModifier#getFirstPersonMode(float)} method.
     * 2. If no modifier is available or applicable, it checks if there is an active animation.
     *    If an animation exists, it delegates the call to the animation's {@link IAnimation#getFirstPersonMode(float)} method.
     * 3. If neither a modifier nor an animation is present, it falls back to the default implementation
     *    provided by the {@link IAnimation} interface.
     *
     * @param tickDelta A float value representing the partial tick time (used to interpolate between frames).
     *                  This parameter is typically used in rendering calculations.
     * @return The {@link FirstPersonMode} determined by the current modifier, animation, or the default implementation.
     */
    @Override
    public @NotNull FirstPersonMode getFirstPersonMode(float tickDelta) {
        AbstractModifier modifier = getFirstPersonModifierOrDefault();
        if (modifier != null) {
            return modifier.getFirstPersonMode(tickDelta);
        }

        if (animation != null) {
            return animation.getFirstPersonMode(tickDelta);
        }
        return IAnimation.super.getFirstPersonMode(tickDelta);
    }

    /**
     * Retrieves the {@link FirstPersonConfiguration} for the current object, based on the provided {@code tickDelta}.
     * <p>
     * The method determines the appropriate {@link FirstPersonConfiguration} by following this logic:
     * 1. It first attempts to retrieve the active {@link AbstractModifier} by calling {@link #getFirstPersonModifierOrDefault()}.
     *    If a modifier is found, it delegates the call to the modifier's {@link AbstractModifier#getFirstPersonConfiguration(float)} method.
     * 2. If no modifier is available or applicable, it checks if there is an active animation.
     *    If an animation exists, it delegates the call to the animation's {@link IAnimation#getFirstPersonConfiguration(float)} method.
     * 3. If neither a modifier nor an animation is present, it falls back to the default implementation
     *    provided by the {@link IAnimation} interface.
     *
     * @param tickDelta A float value representing the partial tick time (used to interpolate between frames).
     *                  This parameter is typically used in rendering calculations.
     * @return The {@link FirstPersonConfiguration} determined by the current modifier, animation, or the default implementation.
     */
    @Override
    public @NotNull FirstPersonConfiguration getFirstPersonConfiguration(float tickDelta) {
        AbstractModifier modifier = getFirstPersonModifierOrDefault();
        if (modifier != null) {
            return modifier.getFirstPersonConfiguration(tickDelta);
        }

        if (animation != null) {
            return animation.getFirstPersonConfiguration(tickDelta);
        }
        return IAnimation.super.getFirstPersonConfiguration(tickDelta);
    }

    /**
     * Searches for and retrieves the highest-priority {@link AbstractModifier}
     * from the list of modifiers, prioritizing instances of {@link FirstPersonModifier}.
     * <p>
     * The method performs the following:
     * 1. Iterates through the {@code modifiers} list to find the first instance
     *    of {@link FirstPersonModifier}. If found, it is returned immediately.
     * 2. If no {@link FirstPersonModifier} is found, the first modifier in the list
     *    (if it exists) is returned as a fallback.
     * 3. If the list of modifiers is empty, {@code null} is returned.
     *
     * @return The highest-priority {@link AbstractModifier}, or {@code null} if
     *         the modifier list is empty.
     */
    private AbstractModifier getFirstPersonModifierOrDefault() {
        for (AbstractModifier modifier : modifiers) {
            if (modifier instanceof FirstPersonModifier) {
                return modifier;
            }
        }

        return modifiers.isEmpty() ? null : modifiers.get(0);
    }
}

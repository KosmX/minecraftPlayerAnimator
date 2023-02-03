package dev.kosmx.playerAnim.api.layered;

import dev.kosmx.playerAnim.api.TransformType;
import dev.kosmx.playerAnim.api.first_person.FirstPersonAnimation;
import dev.kosmx.playerAnim.api.first_person.IFirstPersonPlayback;
import dev.kosmx.playerAnim.core.util.Pair;
import dev.kosmx.playerAnim.core.util.Vec3f;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Player animation stack, can contain multiple active or passive layers, will always be evaluated from the lowest index.
 * Highest index = it can override everything else
 */
public class AnimationStack implements IAnimation {

    private final List<Pair<Integer, IAnimation>> layers = new ArrayList<>();

    /**
     *
     * @return true if exists level what is active.
     */
    @Override
    public boolean isActive() {
        for (Pair<Integer, IAnimation> layer : layers) {
            if (layer.getRight().isActive()) return true;
        }
        return false;
    }

    @Override
    public void tick() {
        for (Pair<Integer, IAnimation> layer : layers) {
            if (layer.getRight().isActive()) {
                layer.getRight().tick();
            }
        }
    }

    @Override
    public Vec3f get3DTransform(String modelName, TransformType type, float tickDelta, Vec3f value0) {
        for (Pair<Integer, IAnimation> layer : layers) {
            if (layer.getRight().isActive()) {
                value0 = layer.getRight().get3DTransform(modelName, type, tickDelta, value0);
            }
        }
        return value0;
    }

    @Override
    public void setupAnim(float tickDelta) {
        for (Pair<Integer, IAnimation> layer : layers) {
            layer.getRight().setupAnim(tickDelta);
        }
    }


    /**
     * Add an animation layer.
     * If there are multiple with the same priority, the one, added first will have larger priority
     * @param priority priority
     * @param layer    animation layer
     * note: Same priority entries logic is subject to change
     */
    public void addAnimLayer(int priority, IAnimation layer) {
        int search = 0;
        //Insert the layer into the correct slot
        while (layers.size() > search && layers.get(search).getLeft() < priority) {
            search++;
        }
        layers.add(search, new Pair<>(priority, layer));
    }

    /**
     * Remove an animation layer
     * @param layer needle
     * @return true if any elements were removed.
     */
    public boolean removeLayer(IAnimation layer) {
        return layers.removeIf(integerIAnimationPair -> integerIAnimationPair.getRight() == layer);
    }

    /**
     * Remove EVERY layer with priority
     * @param layerLevel search and destroy
     * @return true if any elements were removed.
     */
    public boolean removeLayer(int layerLevel) {
        return layers.removeIf(integerIAnimationPair -> integerIAnimationPair.getLeft() == layerLevel);
    }

    @Nullable
    public FirstPersonAnimation getActiveFirstPersonAnimation(float tickDelta) {
        for (Pair<Integer, IAnimation> layer : layers) {
            IAnimation animation = layer.getRight();
            if (animation instanceof ModifierLayer) {
                animation = ((ModifierLayer)animation).getAnimation();
            }
            if(animation instanceof IFirstPersonPlayback) {
                IFirstPersonPlayback firstPersonAnimation = (IFirstPersonPlayback)animation;
                if (firstPersonAnimation.isActiveInFirstPerson(tickDelta)) {
                    FirstPersonAnimation.Configuration configuration = firstPersonAnimation.getFirstPersonPlaybackConfig();
                    if (configuration == null) {
                        configuration = FirstPersonAnimation.Configuration.defaults();
                    }
                    return new FirstPersonAnimation(animation, configuration);
                }
            }
        }
        return null;
    }
}

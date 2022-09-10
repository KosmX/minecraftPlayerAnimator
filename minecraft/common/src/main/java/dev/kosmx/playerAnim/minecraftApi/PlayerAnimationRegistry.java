package dev.kosmx.playerAnim.minecraftApi;

import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class PlayerAnimationRegistry {

    private static final HashMap<ResourceLocation, KeyframeAnimation> animations = new HashMap<>();

    /**
     * Get an animation from the registry, using Identifier(MODID, animation_name) as key
     * @param identifier identifier
     * @return animation, <code>null</code> if no animation
     */
    @Nullable
    public static KeyframeAnimation getAnimation(@NotNull ResourceLocation identifier) {
        return animations.get(identifier);
    }

    /**
     * Get Optional animation from registry
     * @param identifier identifier
     * @return Optional animation
     */
    @NotNull
    public static Optional<KeyframeAnimation> getAnimationOptional(@NotNull ResourceLocation identifier) {
        return Optional.ofNullable(getAnimation(identifier));
    }

    /**
     * @return an unmodifiable map of all the animations
     */
    public static Map<ResourceLocation, KeyframeAnimation> getAnimations() {
        return (Map<ResourceLocation, KeyframeAnimation>) animations.clone();
    }

    /**
     * Returns the animations of a specific mod/namespace
     * @param modid namespace (assets/modid)
     * @return map of path and animations
     */
    public static Map<String, KeyframeAnimation> getModAnimations(String modid) {
        HashMap<String, KeyframeAnimation> map = new HashMap<>();
        for (Map.Entry<ResourceLocation, KeyframeAnimation> entry: animations.entrySet()) {
            if (entry.getKey().getNamespace().equals(modid)) {
                map.put(entry.getKey().getPath(), entry.getValue());
            }
        }
        return map;
    }

    /**
     * Clear animation registry, INTERNAL, only happens before resource loading
     */
    @ApiStatus.Internal
    public static void clearAnimation() {
        animations.clear();
    }

    /**
     * add animation to the registry, used by resource loader.
     * @param location  animation identifier
     * @param animation animation
     */
    @ApiStatus.Internal
    public static void addAnimation(@NotNull ResourceLocation location, @NotNull KeyframeAnimation animation) {
        animations.put(location, animation);
    }


    /**
     * Helper function to convert animation name to string
     */
    public static String serializeTextToString(String arg) {
        Component component = Component.Serializer.fromJson(arg);
        if (component != null) {
            return component.getString();
        } else {
            return arg.replace("\"", "");
        }
    }
}

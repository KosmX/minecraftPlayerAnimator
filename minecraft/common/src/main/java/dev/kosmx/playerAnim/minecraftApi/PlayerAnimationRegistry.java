package dev.kosmx.playerAnim.minecraftApi;

import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
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

    @ApiStatus.Internal
    public static void clearAnimation() {
        animations.clear();
    }

    @ApiStatus.Internal
    public static void addAnimation(@NotNull ResourceLocation location, @NotNull KeyframeAnimation animation) {
        animations.put(location, animation);
    }


    /**
     * Helper function to convert animation name to string
     */
    public static String serializeTextToString(String arg) {
        var component = Component.Serializer.fromJson(arg);
        if (component != null) {
            return component.getString();
        } else {
            return arg.replace("\"", "");
        }
    }
}

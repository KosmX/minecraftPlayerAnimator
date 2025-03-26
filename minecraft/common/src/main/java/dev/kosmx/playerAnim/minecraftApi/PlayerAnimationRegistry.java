package dev.kosmx.playerAnim.minecraftApi;

import dev.kosmx.playerAnim.api.IPlayable;
import dev.kosmx.playerAnim.minecraftApi.codec.AnimationCodecs;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Load resources from <code>assets/{modid}/player_animations</code>
 * <br>
 * The animation identifier:
 * <table border="1">
 *   <tr>
 *     <td> namespace </td> <td> Mod namespace </td>
 *   </tr>
 *   <tr>
 *     <td> path </td> <td> Animation name, not the filename </td>
 *   </tr>
 * </table>
 * <br>
 * Use {@link PlayerAnimationRegistry#getAnimation(ResourceLocation)} to fetch an animation
 * <br><br>
 * Extra animations can be added by ResourcePack(s) or other mods
 * <br><br>
 * Breaking change with 2.0.0: Registry now returns an IPlayable type instead of a KeyframeAnimation.
 * Feel free to safely cast it into KeyframeAnimation:
 * <br>
 * <code>if (PlayerAnimationRegistry.getAnimation(id) instanceof KeyframeAnimation animation) {...}</code>
 * <br>
 * Or to simply play the result, do<br>
 * <code>PlayerAnimationRegistry.getAnimation(id).playAnimation()</code> <br>
 * This change will allow more animation formats to be supported. (Don't forget, you can still wrap the unknown animation in custom wrappers :D)
 */
@Environment(EnvType.CLIENT)
public final class PlayerAnimationRegistry {

    private static final HashMap<ResourceLocation, IPlayable> animations = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(PlayerAnimationRegistry.class);

    /**
     * Get an animation from the registry, using Identifier(MODID, animation_name) as key
     * @param identifier identifier
     * @return animation, <code>null</code> if no animation
     */
    @Nullable
    public static IPlayable getAnimation(@NotNull ResourceLocation identifier) {
        return animations.get(identifier);
    }

    /**
     * Get Optional animation from registry
     * @param identifier identifier
     * @return Optional animation
     */
    @NotNull
    public static Optional<IPlayable> getAnimationOptional(@NotNull ResourceLocation identifier) {
        return Optional.ofNullable(getAnimation(identifier));
    }

    /**
     * @return an unmodifiable map of all the animations
     */
    public static Map<ResourceLocation, IPlayable> getAnimations() {
        return Map.copyOf(animations);
    }

    /**
     * Returns the animations of a specific mod/namespace
     * @param modid namespace (assets/modid)
     * @return map of path and animations
     */
    @NotNull
    public static Map<String, IPlayable> getModAnimations(@NotNull String modid) {
        HashMap<String, IPlayable> map = new HashMap<>();
        for (Map.Entry<ResourceLocation, IPlayable> entry: animations.entrySet()) {
            if (entry.getKey().getNamespace().equals(modid)) {
                map.put(entry.getKey().getPath(), entry.getValue());
            }
        }
        return map;
    }

    /**
     * Load animations using ResourceManager
     * Internal use only!
     */
    @ApiStatus.Internal
    public static void resourceLoaderCallback(@NotNull ResourceManager manager) {
        animations.clear();

        for (var resource: manager.listResources("player_animations", ignore -> true).entrySet()) {
            var extension = AnimationCodecs.getExtension(resource.getKey().getPath());
            if (extension == null) continue;
            var a = AnimationCodecs.deserialize(extension, () -> {
                try {
                    return resource.getValue().open();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            for(var animation: a) {
                animations.put(ResourceLocation.fromNamespaceAndPath(resource.getKey().getNamespace(), serializeTextToString(animation.getName())), animation);
            }
        }
        for (var resource: manager.listResources("player_animation", ignore -> true).entrySet()) {
            var extension = AnimationCodecs.getExtension(resource.getKey().getPath());
            if (extension == null) continue;
            logger.warn("[WARNING FOR MOD DEVS] Animation {} is in wrong directory: \"player_animation\", please place it in \"player_animations\".", resource.getKey().getPath());
            var a = AnimationCodecs.deserialize(extension, () -> {
                try {
                    return resource.getValue().open();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            for(var animation: a) {
                animations.put(ResourceLocation.fromNamespaceAndPath(resource.getKey().getNamespace(), serializeTextToString(animation.getName())), animation);
            }
        }
    }


    /**
     * Helper function to convert animation name to string
     */
    public static String serializeTextToString(String arg) {
        try {
            var component = Component.Serializer.fromJson(arg, RegistryAccess.EMPTY);
            if (component != null) {
                return component.getString();
            }
        } catch(Exception ignored) { }
        return arg.replace("\"", "");
    }
}

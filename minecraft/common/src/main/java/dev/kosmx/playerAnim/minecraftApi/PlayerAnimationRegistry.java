package dev.kosmx.playerAnim.minecraftApi;

import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.core.data.gson.AnimationSerializing;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Load resources from <code>assets/{modid}/player_animation</code>
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
 */
@Environment(EnvType.CLIENT)
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
     * Load animations using ResourceManager
     * Internal use only!
     */
    @ApiStatus.Internal
    public static void resourceLoaderCallback(@NotNull ResourceManager manager, Logger logger) {
        animations.clear();
        for (ResourceLocation resource: manager.listResources("player_animation", location -> location.endsWith(".json"))) {
            try (InputStream input = manager.getResource(resource).getInputStream()) {

                //Deserialize the animation json. GeckoLib animation json can contain multiple animations.
                for (KeyframeAnimation animation : AnimationSerializing.deserializeAnimation(input)) {

                    //Save the animation for later use.
                    animations.put(new ResourceLocation(resource.getNamespace(), PlayerAnimationRegistry.serializeTextToString((String) animation.extraData.get("name")).toLowerCase(Locale.ROOT)), animation);
                }
            } catch(IOException e) {
                logger.error("Error while loading payer animation: " + resource);
                logger.error(e.getMessage());
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                String sStackTrace = sw.toString(); // stack trace as a string
                logger.error(sStackTrace);
            }
        }
    }


    /**
     * Helper function to convert animation name to string
     */
    public static String serializeTextToString(String arg) {
        try {
            Component component = Component.Serializer.fromJson(arg);
            if (component != null) {
                return component.getString();
            }
        } catch(Exception ignored) { }
        return arg.replace("\"", "");
    }
}

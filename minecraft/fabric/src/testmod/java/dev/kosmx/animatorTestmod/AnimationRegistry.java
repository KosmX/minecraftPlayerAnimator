package dev.kosmx.animatorTestmod;

import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.core.data.gson.AnimationSerializing;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class AnimationRegistry {
    // static final Logger LOGGER = LogUtils.getLogger();
    public static final Map<String, KeyframeAnimation> animations = new HashMap<>();

    public static void load(ResourceManager resourceManager) {
        var dataFolder = "animations";

        for (Map.Entry<ResourceLocation, List<Resource>> entry : resourceManager.listResourceStacks(dataFolder, (resourceLocationx) -> resourceLocationx.getPath().endsWith(".json")).entrySet()) {
            ResourceLocation resourceLocation = entry.getKey();
            String string = resourceLocation.getPath();
            ResourceLocation resourceLocation2 = new ResourceLocation("testmod", string.substring((dataFolder + "/").length(), string.length() - ".json".length()));
            var resource = resourceManager.getResource(resourceLocation2);

//            Resource resource = resourceManager.getResource(resourceLocation2);
//            Reader reader = resource.openAsReader();
        }


        ResourceLocation resourceLocation = new ResourceLocation("testmod", "animations/two_handed_slash_horizontal_right.json");
        var idk = resourceManager.getResource(resourceLocation);
        PlayerAnimTestmod.LOGGER.warn(idk.toString());

        /*
        try (InputStream reader = AnimationRegistry.class.getResourceAsStream("assets\\testmod\\animations\\two_handed_slash_horizontal_right.json")) {
            KeyframeAnimation animation = AnimationSerializing.deserializeAnimation(reader).get(0);
            animations.put("two_handed_slash_horizontal_right", animation);

        } catch(IOException e) {
            e.printStackTrace();
        }
         */
        var bytes = Base64.getDecoder().decode(SomeString.something);
        try (InputStream reader = new ByteArrayInputStream(bytes)) {
            KeyframeAnimation animation = AnimationSerializing.deserializeAnimation(reader).get(0);
            animations.put("two_handed_vertical_right_right", animation);

        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}

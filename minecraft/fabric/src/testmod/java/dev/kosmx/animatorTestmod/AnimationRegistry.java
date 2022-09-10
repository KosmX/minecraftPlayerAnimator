package dev.kosmx.animatorTestmod;

import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.core.data.gson.AnimationSerializing;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class AnimationRegistry {
    // static final Logger LOGGER = LogUtils.getLogger();
    public static final Map<String, KeyframeAnimation> animations = new HashMap<>();

    public static void load(ResourceManager resourceManager) {
        String  dataFolder = "animations";


        /*
        try (InputStream reader = AnimationRegistry.class.getResourceAsStream("assets\\testmod\\animations\\two_handed_slash_horizontal_right.json")) {
            KeyframeAnimation animation = AnimationSerializing.deserializeAnimation(reader).get(0);
            animations.put("two_handed_slash_horizontal_right", animation);

        } catch(IOException e) {
            e.printStackTrace();
        }
         */
        byte[] bytes = Base64.getDecoder().decode(SomeString.something);
        try (InputStream reader = new ByteArrayInputStream(bytes)) {
            KeyframeAnimation animation = AnimationSerializing.deserializeAnimation(reader).get(0);
            KeyframeAnimation.AnimationBuilder modified = animation.mutableCopy();
            modified.getOrCreatePart("torso").fullyEnablePart(true); //crouching workaround
            animations.put("two_handed_vertical_right_right", modified.build());

        } catch(IOException e) {
            e.printStackTrace();
        }



    }
}

package dev.kosmx.animatorTestmod;

import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractModifier;
import dev.kosmx.playerAnim.api.layered.modifier.MirrorModifier;
import dev.kosmx.playerAnim.api.layered.modifier.SpeedModifier;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.player.LocalPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Testmod for testing and demonstration purposes.
 * <hr>
 *
 * In this dev env I use mojmap (the project was remapped to it when I initially began supporting forge)<br>
 * If you want to see what would it like with Yarn,<br>
 * use <code>gradlew migrateMappings --mappings "1.19+build.4"</code> or with the latest mapping<br>
 * More about migrateMappings on <a href="https://fabricmc.net/wiki/tutorial:migratemappings">Fabric wiki</a>
 *
 *
 */
public class PlayerAnimTestmod implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("testmod");
    public static final ModifierLayer<IAnimation> testAnimation = new ModifierLayer<>();

    @Override
    public void onInitializeClient() {
        LOGGER.warn("Testmod is loading :D");

        PlayerAnimationAccess.REGISTER_ANIMATION_EVENT.register((player, animationStack) -> {
            if (player instanceof LocalPlayer) {
                animationStack.addAnimLayer(42, testAnimation);
            }
        });

        testAnimation.addModifierBefore(new SpeedModifier(0.5f));
        testAnimation.addModifierBefore(new MirrorModifier(true));


    }

    public static void playTestAnimation() {

        PlayerAnimTestmod.testAnimation.setAnimation(new KeyframeAnimationPlayer(AnimationRegistry.animations.get("two_handed_vertical_right_right")));
    }
}

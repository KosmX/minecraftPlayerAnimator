package dev.kosmx.playerAnim.compatibility;

import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import mod.azure.azurelib.common.internal.common.cache.object.GeoBone;
import mod.azure.azurelib.neoforge.event.GeoRenderEvent;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;

@SuppressWarnings("deprecation")
public class AzureLibArmorRenderHandler {
    private AzureLibArmorRenderHandler() {
        // []
    }

    @SubscribeEvent
    public static void onPreRender(GeoRenderEvent.Armor.Pre event) {
        if (event.getEntity() instanceof Player player) {
            AnimationApplier emote = ((IAnimatedPlayer) player).playerAnimator_getAnimation();

            if (emote.isActive() && emote.getFirstPersonMode() == FirstPersonMode.THIRD_PERSON_MODEL && FirstPersonMode.isFirstPersonPass()) {
                boolean showRightArm = emote.getFirstPersonConfiguration().isShowRightArm();
                boolean showLeftArm = emote.getFirstPersonConfiguration().isShowLeftArm();
                event.getModel().getTopLevelBones().forEach(coreGeoBone -> coreGeoBone.setHidden(true));

                event.getModel().getTopLevelBones().forEach(coreGeoBone -> {
                    if ((showRightArm && isDescendantOf(coreGeoBone, "bipedRightArm")) ||
                            (showLeftArm && isDescendantOf(coreGeoBone, "bipedLeftArm")) ||
                            (showRightArm && isDescendantOf(coreGeoBone, "armorRightArm")) ||
                            (showLeftArm && isDescendantOf(coreGeoBone, "armorLeftArm"))) {
                        coreGeoBone.setHidden(false);
                    }
                });
            }
        }
    }

    @SubscribeEvent
    public static void onPostRender(GeoRenderEvent.Armor.Post event) {
        if (event.getEntity() instanceof Player player) {
            AnimationApplier emote = ((IAnimatedPlayer) player).playerAnimator_getAnimation();
            if (!emote.isActive() || !(emote.getFirstPersonMode() == FirstPersonMode.THIRD_PERSON_MODEL) || !FirstPersonMode.isFirstPersonPass()) {
                event.getModel().getTopLevelBones().forEach(coreGeoBone -> coreGeoBone.setHidden(false));
            }
        }
    }

    private static boolean isDescendantOf(GeoBone bone, String ancestorName) {
        if (bone == null) {
            return false;
        }
        if (bone.getName().equalsIgnoreCase(ancestorName)) {
            return true;
        }
        return bone.getParent() != null && isDescendantOf(bone.getParent(), ancestorName);
    }
}


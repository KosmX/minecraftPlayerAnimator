package dev.kosmx.playerAnim.minecraftApi.layers;

import dev.kosmx.playerAnim.api.layered.modifier.MirrorModifier;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;

/**
 * Left-handedness helper
 * If enabled, automatically mirror all animation if player is left-handed
 */
public class LeftHandedHelperModifier extends MirrorModifier {
    private final Player player;

    public LeftHandedHelperModifier(Player player) {
        this.player = player;
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled() && player.getMainArm() == HumanoidArm.LEFT;
    }
}

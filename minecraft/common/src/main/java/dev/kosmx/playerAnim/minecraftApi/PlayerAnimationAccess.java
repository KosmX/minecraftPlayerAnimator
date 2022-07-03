package dev.kosmx.playerAnim.minecraftApi;

import dev.kosmx.playerAnim.api.IPlayer;
import dev.kosmx.playerAnim.api.layered.AnimationStack;
import net.minecraft.client.player.AbstractClientPlayer;

public abstract class PlayerAnimationAccess {

    /**
     * Get the animation stack for a player entity on the client.
     *
     * Or you can use {@code ((IPlayer) player).getAnimationStack();}
     *
     * @param player The ClientPlayer object
     * @return The players' animation stack
     */
    public static AnimationStack getPlayerAnimLayer(AbstractClientPlayer player) throws IllegalArgumentException {
        if (player instanceof IPlayer) {
            return ((IPlayer) player).getAnimationStack();
        } else throw new IllegalArgumentException(player + " is not a player or library mixins failed");
    }
}

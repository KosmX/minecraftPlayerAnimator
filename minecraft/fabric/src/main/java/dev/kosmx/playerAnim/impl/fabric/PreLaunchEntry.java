package dev.kosmx.playerAnim.impl.fabric;

import dev.kosmx.playerAnim.impl.Helper;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.asm.mixin.Mixins;

@ApiStatus.Internal
public class PreLaunchEntry implements PreLaunchEntrypoint {
    @Override
    public void onPreLaunch() {
        if (Helper.isBendEnabled()) {
            //Use bend mixin(s) ONLY in bend mode
            Mixins.addConfiguration("playerAnimator-onlyBend.mixins.json");
        }
    }
}

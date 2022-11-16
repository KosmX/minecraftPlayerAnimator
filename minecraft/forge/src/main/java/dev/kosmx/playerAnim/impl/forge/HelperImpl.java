package dev.kosmx.playerAnim.impl.forge;

import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.asm.service.MixinService;

import java.io.IOException;

@ApiStatus.Internal
public class HelperImpl {
    public static boolean isBendyLibPresent() {
        return hasClass("io.github.kosmx.bendylib.impl.ICuboid");
    }


    private static boolean hasClass(String name) {
        try {
            // This does *not* load the class!
            MixinService.getService().getBytecodeProvider().getClassNode(name);
            return true;
        } catch (ClassNotFoundException | IOException e) {
            return false;
        }
    }
}

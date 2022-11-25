package dev.kosmx.playerAnim.impl.forge;

import org.spongepowered.asm.service.MixinService;

import java.io.IOException;

public class HelperImpl {
    public static boolean isBendyLibPresent() {
        try {
            MixinService.getService().getBytecodeProvider().getClassNode("io.github.kosmx.bendylib.IModelPart");
            System.out.println("[playerAnimator] bendy-lib found");
            return true;
        } catch(ClassNotFoundException | IOException e) {
            return false;
        }
    }
}

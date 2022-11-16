package dev.kosmx.playerAnim.impl.forge;

public class HelperImpl {
    public static boolean isBendyLibPresent() {
        try {
            MixinService.getService().getBytecodeProvider().getClassNode("io.github.kosmx.bendylib.IModelPart");
            System.out.println("[playerAnimator] bendy-lib found");
            return true;
        } catch(ClassNotFoundException e) {
            return false;
        }
    }
}

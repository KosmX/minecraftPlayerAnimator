package dev.kosmx.playerAnim.impl.forge;

public class HelperImpl {
    public static boolean isBendyLibPresent() {
        try {
            Class.forName("io.github.kosmx.bendylib.IModelPart").getName();
            System.out.println("[playerAnimator] bendy-lib found");
            return true;
        } catch(ClassNotFoundException e) {
            return false;
        }
    }
}

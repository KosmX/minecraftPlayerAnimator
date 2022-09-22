package dev.kosmx.playerAnim.impl.compat.skinLayers;

import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import dev.kosmx.playerAnim.impl.IUpperPartHelper;
import dev.kosmx.playerAnim.impl.animation.IBendHelper;
import dev.tr7zw.skinlayers.api.LayerFeatureTransformerAPI;

public class SkinLayersTransformer {

    public static void init() {
        LayerFeatureTransformerAPI.setLayerTransformer((player, matrixStack, modelPart) -> {
            if (((IUpperPartHelper)modelPart).isUpperPart()) {
                IBendHelper.rotateMatrixStack(matrixStack, ((IAnimatedPlayer) player).getAnimation().getBend("body"));
            }
        });
    }
}

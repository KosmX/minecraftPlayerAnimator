package dev.kosmx.playerAnim.impl.compat.skinLayers;

import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import dev.kosmx.playerAnim.impl.IUpperPartHelper;
import dev.kosmx.playerAnim.impl.animation.IBendHelper;
import dev.tr7zw.skinlayers.api.LayerFeatureTransformerAPI;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

@ApiStatus.Internal
public class SkinLayersTransformer {

    public static void init(Logger logger) {
        logger.info("Loading 3D skin compat");

        LayerFeatureTransformerAPI.setLayerTransformer((player, matrixStack, modelPart) -> {
            if (((IUpperPartHelper)modelPart).isUpperPart()) {
                IBendHelper.rotateMatrixStack(matrixStack, ((IAnimatedPlayer) player).playerAnimator_getAnimation().getBend("body"));
            }
        });
    }
}

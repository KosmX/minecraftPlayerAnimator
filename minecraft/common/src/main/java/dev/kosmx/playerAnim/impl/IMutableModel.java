package dev.kosmx.playerAnim.impl;

import dev.kosmx.playerAnim.core.impl.AnimationProcessor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public interface IMutableModel {

    void playerAnimator$setAnimation(@NotNull AnimationProcessor emoteSupplier);

    @NotNull AnimationProcessor playerAnimator$getAnimation();

}

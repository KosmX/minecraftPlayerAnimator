package dev.kosmx.playerAnim.impl;

import dev.kosmx.playerAnim.api.IPlayer;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import org.jetbrains.annotations.ApiStatus;


@ApiStatus.Internal
public interface IAnimatedPlayer extends IPlayer {
    AnimationApplier getAnimation();
}

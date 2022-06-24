package dev.kosmx.playerAnim.impl;

import dev.kosmx.playerAnim.api.IPlayer;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;

public interface IAnimatedPlayer extends IPlayer {
    AnimationApplier getAnimation();
}

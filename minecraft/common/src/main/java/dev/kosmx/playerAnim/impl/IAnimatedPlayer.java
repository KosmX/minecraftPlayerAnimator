package dev.kosmx.playerAnim.impl;

import dev.kosmx.playerAnim.api.IPlayer;

public interface IAnimatedPlayer extends IPlayer {
    AnimationApplier getAnimation();
}

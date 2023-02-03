package dev.kosmx.playerAnim.impl.animation.first_person;

import dev.kosmx.playerAnim.api.first_person.FirstPersonAnimation;

import java.util.Optional;

public interface IAnimatedFirstPerson {
    Optional<FirstPersonAnimation> getActiveFirstPersonAnimation(float tickDelta);
}

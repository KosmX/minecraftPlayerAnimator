package dev.kosmx.playerAnim.api.first_person;

import org.jetbrains.annotations.Nullable;

public interface IFirstPersonPlayback {
    boolean isActiveInFirstPerson(float tickDelta);
    @Nullable FirstPersonAnimation.Configuration getFirstPersonPlaybackConfig();
}

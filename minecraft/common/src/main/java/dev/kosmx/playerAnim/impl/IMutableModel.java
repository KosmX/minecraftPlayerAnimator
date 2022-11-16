package dev.kosmx.playerAnim.impl;

import dev.kosmx.playerAnim.core.impl.AnimationProcessor;
import dev.kosmx.playerAnim.core.util.SetableSupplier;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface IMutableModel {
    IBendHelper getTorso();

    IBendHelper getRightArm();

    IBendHelper getLeftArm();

    IBendHelper getRightLeg();

    IBendHelper getLeftLeg();


    void setEmoteSupplier(SetableSupplier<AnimationProcessor> emoteSupplier);

    SetableSupplier<AnimationProcessor> getEmoteSupplier();

    void setTorso(IBendHelper v);
    void setRightArm(IBendHelper v);
    void setLeftArm(IBendHelper v);
    void setRightLeg(IBendHelper v);
    void setLeftLeg(IBendHelper v);

}

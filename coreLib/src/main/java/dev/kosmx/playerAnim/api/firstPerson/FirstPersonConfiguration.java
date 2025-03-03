package dev.kosmx.playerAnim.api.firstPerson;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class FirstPersonConfiguration {
    boolean showRightArm = false;
    boolean showLeftArm = false;
    boolean showRightItem = true;
    boolean showLeftItem = true;
    boolean showArmor = false;

    public FirstPersonConfiguration(boolean showRightArm, boolean showLeftArm, boolean showRightItem, boolean showLeftItem) {
        this.showRightArm = showRightArm;
        this.showLeftArm = showLeftArm;
        this.showRightItem = showRightItem;
        this.showLeftItem = showLeftItem;
    }
}

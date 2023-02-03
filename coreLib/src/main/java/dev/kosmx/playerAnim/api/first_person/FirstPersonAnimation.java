package dev.kosmx.playerAnim.api.first_person;

import dev.kosmx.playerAnim.api.layered.IAnimation;

public final class FirstPersonAnimation {
    private final IAnimation animation;
    private final Configuration config;

    public FirstPersonAnimation(IAnimation animation, Configuration config) {
        this.animation = animation;
        this.config = config;
    }

    public IAnimation animation() {
        return animation;
    }

    public Configuration config() {
        return config;
    }

    public static final class Configuration {
        private final boolean showRightArm;
        private final boolean showLeftArm;
        private final boolean showRightItem;
        private final boolean showLeftItem;

        public Configuration(boolean showRightArm, boolean showLeftArm,
                             boolean showRightItem, boolean showLeftItem) {
            this.showRightArm = showRightArm;
            this.showLeftArm = showLeftArm;
            this.showRightItem = showRightItem;
            this.showLeftItem = showLeftItem;
        }

        public static Configuration defaults() {
            return new Configuration(false, false, true, true);
        }

        public boolean showRightArm() {
            return showRightArm;
        }

        public boolean showLeftArm() {
            return showLeftArm;
        }

        public boolean showRightItem() {
            return showRightItem;
        }

        public boolean showLeftItem() {
            return showLeftItem;
        }
    }
}
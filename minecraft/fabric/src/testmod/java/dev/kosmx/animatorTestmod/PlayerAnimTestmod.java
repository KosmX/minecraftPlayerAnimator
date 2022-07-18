package dev.kosmx.animatorTestmod;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PlayerAnimTestmod implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("testmod");
    @Override
    public void onInitializeClient() {
        LOGGER.warn("Testmod is loading :D");

    }
}

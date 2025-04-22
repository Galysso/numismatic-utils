package galysso.codicraft.numismaticutils;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import galysso.codicraft.numismaticutils.screen.BankerScreen;

public class NumismaticUtilsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HandledScreens.register(NumismaticUtilsMain.BANKER_SCREEN_HANDLER, BankerScreen::new);
    }
}

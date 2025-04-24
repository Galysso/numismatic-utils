package galysso.codicraft.numismaticutils;

import galysso.codicraft.numismaticutils.network.NetworkUtilClient;
import net.fabricmc.api.ClientModInitializer;

public class NumismaticUtilsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        NetworkUtilClient.init();
    }
}

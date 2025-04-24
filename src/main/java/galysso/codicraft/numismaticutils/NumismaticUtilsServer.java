package galysso.codicraft.numismaticutils;

import galysso.codicraft.numismaticutils.utils.BankerUtils;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

public class NumismaticUtilsServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        System.out.println("[Numismatic Utils] NumismaticUtils Server Registered Events");
        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
        ServerLifecycleEvents.SERVER_STOPPING.register(this::onServerStopping);
    }

    private void onServerStarted(MinecraftServer server) {
        System.out.println("[Numismatic Utils] NumismaticUtils Server Started");
        BankerUtils.loadData();
    }

    private void onServerStopping(MinecraftServer server) {
        System.out.println("[Numismatic Utils] NumismaticUtils Server Started");
        BankerUtils.saveData();
    }
}
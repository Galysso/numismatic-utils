package galysso.codicraft.numismaticutils.utils;

import net.minecraft.server.MinecraftServer;

public class ServerUtil {
    private static MinecraftServer serverInstance;

    public static void setServerInstance(MinecraftServer server) {
        serverInstance = server;
    }

    public static MinecraftServer getServer() {
        return serverInstance;
    }

    public static long getServerTicks() {
        if (serverInstance != null) {
            return serverInstance.getTicks();
        }
        return 0;
    }
}
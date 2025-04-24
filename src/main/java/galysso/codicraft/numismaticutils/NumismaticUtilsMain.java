package galysso.codicraft.numismaticutils;

import galysso.codicraft.numismaticutils.utils.BankerUtils;
import galysso.codicraft.numismaticutils.network.NetworkUtil;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

public class NumismaticUtilsMain implements ModInitializer {
	public static final String MOD_ID = "numismatic_utils";

	@Override
	public void onInitialize() {
		// Screen handler
		NetworkUtil.init();
		System.out.println("[Numismatic Utils] NumismaticUtils Server Registered Events");
		ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
		ServerLifecycleEvents.SERVER_STOPPING.register(this::onServerStopping);
	}

	public static Identifier identifier(String path) {
		return Identifier.of(MOD_ID + ":" + path);
	}

	private void onServerStarted(MinecraftServer server) {
		System.out.println("[Numismatic Utils] NumismaticUtils Server Started");
		BankerUtils.loadData();
	}

	private void onServerStopping(MinecraftServer server) {
		System.out.println("[Numismatic Utils] NumismaticUtils Server Stopping");
		BankerUtils.saveData();
	}
}
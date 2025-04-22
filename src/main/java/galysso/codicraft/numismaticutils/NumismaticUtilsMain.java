package galysso.codicraft.numismaticutils;

import galysso.codicraft.numismaticutils.screen.BankerScreenHandler;
import net.fabricmc.api.ModInitializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class NumismaticUtilsMain implements ModInitializer {
	/** Screen handler */
	public static final ScreenHandlerType<BankerScreenHandler> BANKER_SCREEN_HANDLER = new ScreenHandlerType<>(
		BankerScreenHandler::new, FeatureFlags.DEFAULT_ENABLED_FEATURES
	);

	@Override
	public void onInitialize() {
		// Screen handler
		Registry.register(Registries.SCREEN_HANDLER, identifier("repairsmith"), BANKER_SCREEN_HANDLER);
	}

	public static Identifier identifier(String path) {
		return Identifier.of("numismatic_utils:" + path);
	}
}
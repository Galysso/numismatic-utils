package galysso.codicraft.numismaticutils.network;

import galysso.codicraft.numismaticutils.network.responses.AccountBalancePayload;
import galysso.codicraft.numismaticutils.network.responses.AccountsListPayload;
import galysso.codicraft.numismaticutils.network.responses.PlayerInfoPayload;
import galysso.codicraft.numismaticutils.screen.BankerScreen;
import galysso.codicraft.numismaticutils.screen.BankerScreenHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.screen.ScreenHandler;

public class NetworkUtilClient {
    public static void init () {
        HandledScreens.register(NetworkUtil.BANKER_SCREEN_HANDLER, BankerScreen::new);


        /* ----- Actions ----- */
        // On response account balance
        ClientPlayNetworking.registerGlobalReceiver(AccountBalancePayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                ScreenHandler screenHandler = context.client().player.currentScreenHandler;
                if (payload.syncId() == screenHandler.syncId && screenHandler instanceof BankerScreenHandler) {
                    BankerScreenHandler bankerScreenHandler = (BankerScreenHandler) screenHandler;
                    bankerScreenHandler.setBalance(payload.accountId(), payload.balance());
                }
            });
        });

        // On response accounts list
        ClientPlayNetworking.registerGlobalReceiver(AccountsListPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                ScreenHandler screenHandler = context.client().player.currentScreenHandler;
                if (payload.syncId() == screenHandler.syncId && screenHandler instanceof BankerScreenHandler) {
                    System.out.println("Account list is received");
                    BankerScreenHandler bankerScreenHandler = (BankerScreenHandler) screenHandler;
                    bankerScreenHandler.setAccountsList(payload.accountsIds(), payload.accountsNames(), payload.accountsRights(), payload.accountsIcons());
                }
            });
        });

        // On response player info
        ClientPlayNetworking.registerGlobalReceiver(PlayerInfoPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                ScreenHandler screenHandler = context.client().player.currentScreenHandler;
                if (payload.syncId() == screenHandler.syncId && screenHandler instanceof BankerScreenHandler) {
                    BankerScreenHandler bankerScreenHandler = (BankerScreenHandler) screenHandler;
                    bankerScreenHandler.setPlayerInfo(payload.mainAccountId(), payload.canCreateNewAccount());
                }
            });
        });
    }
}

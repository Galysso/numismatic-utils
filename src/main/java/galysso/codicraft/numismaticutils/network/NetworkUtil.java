package galysso.codicraft.numismaticutils.network;

import com.glisco.numismaticoverhaul.ModComponents;
import galysso.codicraft.numismaticutils.NumismaticUtilsMain;
import galysso.codicraft.numismaticutils.network.requests.*;
import galysso.codicraft.numismaticutils.network.responses.*;
import galysso.codicraft.numismaticutils.utils.BankerUtils;
import galysso.codicraft.numismaticutils.banking.NumismaticAccount;
import galysso.codicraft.numismaticutils.screen.BankerScreenHandler;
import galysso.codicraft.numismaticutils.utils.NumismaticUtils;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class NetworkUtil {
    // Requests
    public static final Identifier RequestAccountBalance = NumismaticUtilsMain.identifier("request_account_balance");
    public static final Identifier RequestAccountCreation = NumismaticUtilsMain.identifier("request_account_creation");
    public static final Identifier RequestAccountInfo = NumismaticUtilsMain.identifier("request_account_info");
    public static final Identifier RequestAccountList = NumismaticUtilsMain.identifier("request_account_list");
    public static final Identifier RequestPlayerInfo = NumismaticUtilsMain.identifier("request_player_info");
    public static final Identifier RequestPlayersList = NumismaticUtilsMain.identifier("request_players_list");
    public static final Identifier RequestTransfert = NumismaticUtilsMain.identifier("request_transfert");

    // Responses
    public static final Identifier ResponseAccountBalance = NumismaticUtilsMain.identifier("account_balance");
    public static final Identifier ResponseAccountInfo = NumismaticUtilsMain.identifier("account_info");
    public static final Identifier ResponseAccountsList = NumismaticUtilsMain.identifier("account_list");
    public static final Identifier ResponsePlayerInfo = NumismaticUtilsMain.identifier("player_info");
    public static final Identifier ResponsePlayersList = NumismaticUtilsMain.identifier("players_list");

    // Messages
    public static final Identifier SendSetSelectedAccountPayload = NumismaticUtilsMain.identifier("set_selected_account");

    /** Screen handler */
    public static final ScreenHandlerType<BankerScreenHandler> BANKER_SCREEN_HANDLER = new ScreenHandlerType<>(
            BankerScreenHandler::new, FeatureFlags.DEFAULT_ENABLED_FEATURES
    );

    public static void init() {
        // Screen handler
        Registry.register(Registries.SCREEN_HANDLER, NumismaticUtilsMain.identifier("banker"), BANKER_SCREEN_HANDLER);

        // Requests registration
        PayloadTypeRegistry.playC2S().register(RequestAccountBalancePayload.ID, RequestAccountBalancePayload.CODEC);
        PayloadTypeRegistry.playC2S().register(RequestAccountCreationPayload.ID, RequestAccountCreationPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(RequestAccountInfoPayload.ID, RequestAccountInfoPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(RequestAccountsListPayload.ID, RequestAccountsListPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(RequestPlayerInfoPayload.ID, RequestPlayerInfoPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(RequestPlayersListPayload.ID, RequestPlayersListPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(RequestTransfertPayload.ID, RequestTransfertPayload.CODEC);

        // Responses registration
        PayloadTypeRegistry.playS2C().register(AccountBalancePayload.ID, AccountBalancePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(AccountInfoPayload.ID, AccountInfoPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(AccountsListPayload.ID, AccountsListPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(PlayerInfoPayload.ID, PlayerInfoPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(PlayersListPayload.ID, PlayersListPayload.CODEC);

        // Messages
        PayloadTypeRegistry.playS2C().register(SetSelectedAccountPayload.ID, SetSelectedAccountPayload.CODEC);

        /* ----- Actions ----- */
        // On request account balance
        ServerPlayNetworking.registerGlobalReceiver(RequestAccountBalancePayload.ID, (payload, context) -> {
            context.server().execute(() -> {
                ServerPlayerEntity player = context.player();
                ScreenHandler screenHandler = context.player().currentScreenHandler;
                int syncId = payload.syncId();
                if (syncId == screenHandler.syncId && screenHandler instanceof BankerScreenHandler) {
                    NumismaticAccount numismaticAccount = BankerUtils.getAccountById(payload.accountId());
                    long balance = 0;
                    if (numismaticAccount != null && numismaticAccount.canSee(player)) {
                        balance = numismaticAccount.getBalance();
                    }
                    AccountBalancePayload responsePayload = new AccountBalancePayload(screenHandler.syncId, payload.accountId(), balance);
                    ServerPlayNetworking.send(context.player(), responsePayload);
                }
            });
        });

        // On request account creation
        ServerPlayNetworking.registerGlobalReceiver(RequestAccountCreationPayload.ID, (payload, context) -> {
            context.server().execute(() -> {
                ServerPlayerEntity player = context.player();
                ScreenHandler screenHandler = context.player().currentScreenHandler;
                int syncId = payload.syncId();
                if (syncId == screenHandler.syncId && screenHandler instanceof BankerScreenHandler) {
                    if (!BankerUtils.canOpenSharedAccount(player))
                        return;

                    String accountName = payload.accountName();
                    if (accountName.isEmpty()) {
                        accountName = player.getName() + "(" + (BankerUtils.getSharedAccounts(player).size() + 1) + ")";
                    }

                    NumismaticAccount numismaticAccount = BankerUtils.openSharedAccount(player, accountName);
                    if (numismaticAccount != null) {
                        List<NumismaticAccount> visibleAccountsList = BankerUtils.getVisibleAccountsList(player);
                        ArrayList<UUID> accountsId = new ArrayList<>();
                        ArrayList<String> accountsNames = new ArrayList<>();
                        ArrayList<BankerUtils.RIGHT_TYPE> accountsRights = new ArrayList<>();
                        ArrayList<Integer> accountsIcons = new ArrayList<>();
                        for (NumismaticAccount account : visibleAccountsList) {
                            accountsId.add(account.getId());
                            accountsNames.add(account.getName());
                            accountsRights.add(account.getPlayerRights(player));
                            accountsIcons.add(account.getIconId(player));
                        }
                        AccountsListPayload responsePayload = new AccountsListPayload(screenHandler.syncId, accountsId, accountsNames, accountsRights, accountsIcons);
                        ServerPlayNetworking.send(context.player(), responsePayload);

                        SetSelectedAccountPayload setSelectedAccountPayload = new SetSelectedAccountPayload(screenHandler.syncId, numismaticAccount.getId());
                        ServerPlayNetworking.send(context.player(), setSelectedAccountPayload);
                    }
                }
            });
        });

        // On request account info
        ServerPlayNetworking.registerGlobalReceiver(RequestAccountInfoPayload.ID, (payload, context) -> {
            context.server().execute(() -> {
                ServerPlayerEntity player = context.player();
                ScreenHandler screenHandler = context.player().currentScreenHandler;
                int syncId = payload.syncId();
                if (syncId == screenHandler.syncId && screenHandler instanceof BankerScreenHandler) {
                    NumismaticAccount numismaticAccount = BankerUtils.getAccountById(payload.accountId());
                    if (numismaticAccount != null && numismaticAccount.canSee(player)) {
                        ArrayList<UUID> playersId = new ArrayList<>();
                        ArrayList<BankerUtils.RIGHT_TYPE> playersRightTypes = new ArrayList<>();
                        ArrayList<String> playersNames = new ArrayList<>();
                        ArrayList<Long> playersRelativeBalance = new ArrayList<>();
                        Map<UUID, Long> playersRelativeBalanceMap = numismaticAccount.getPlayersRelativeBalance();
                        for (Map.Entry<UUID, Long> entry : playersRelativeBalanceMap.entrySet()) {
                            playersId.add(entry.getKey());
                            playersRightTypes.add(numismaticAccount.getPlayerRights(entry.getKey()));
                            playersNames.add(BankerUtils.getPlayerName(entry.getKey()));
                            playersRelativeBalance.add(entry.getValue());
                        }
                        AccountInfoPayload responsePayload = new AccountInfoPayload(screenHandler.syncId, numismaticAccount.getId(), playersId, playersRightTypes, playersNames, playersRelativeBalance);
                        ServerPlayNetworking.send(context.player(), responsePayload);
                    }
                }
            });
        });

        // On request account list
        ServerPlayNetworking.registerGlobalReceiver(RequestAccountsListPayload.ID, (payload, context) -> {
            context.server().execute(() -> {
                ServerPlayerEntity player = context.player();
                ScreenHandler screenHandler = context.player().currentScreenHandler;
                int syncId = payload.syncId();
                if (syncId == screenHandler.syncId && screenHandler instanceof BankerScreenHandler) {
                    List<NumismaticAccount> visibleAccountsList = BankerUtils.getVisibleAccountsList(player, payload.rightTypeFilter(), payload.iconIdFilter());
                    ArrayList<UUID> accountsId = new ArrayList<>();
                    ArrayList<String> accountsNames = new ArrayList<>();
                    ArrayList<BankerUtils.RIGHT_TYPE> accountsRights = new ArrayList<>();
                    ArrayList<Integer> accountsIcons = new ArrayList<>();
                    for (NumismaticAccount numismaticAccount : visibleAccountsList) {
                        accountsId.add(numismaticAccount.getId());
                        accountsNames.add(numismaticAccount.getName());
                        accountsRights.add(numismaticAccount.getPlayerRights(player));
                        accountsIcons.add(numismaticAccount.getIconId(player));
                    }
                    AccountsListPayload responsePayload = new AccountsListPayload(screenHandler.syncId, accountsId, accountsNames, accountsRights, accountsIcons);
                    ServerPlayNetworking.send(context.player(), responsePayload);
                }
            });
        });

        // On request player info
        ServerPlayNetworking.registerGlobalReceiver(RequestPlayerInfoPayload.ID, (payload, context) -> {
            context.server().execute(() -> {
                ServerPlayerEntity player = context.player();
                ScreenHandler screenHandler = context.player().currentScreenHandler;
                int syncId = payload.syncId();
                if (syncId == screenHandler.syncId && screenHandler instanceof BankerScreenHandler) {
                    BankerUtils.registerPlayer(player);
                    NumismaticAccount numismaticAccount = BankerUtils.getMainAccount(player);
                    PlayerInfoPayload responsePayload = new PlayerInfoPayload(screenHandler.syncId, numismaticAccount.getId(), BankerUtils.canOpenSharedAccount(player));
                    ServerPlayNetworking.send(context.player(), responsePayload);
                }
            });
        });

        // On request players list
        ServerPlayNetworking.registerGlobalReceiver(RequestPlayersListPayload.ID, (payload, context) -> {
            context.server().execute(() -> {
                ServerPlayerEntity player = context.player();
                ScreenHandler screenHandler = context.player().currentScreenHandler;
                int syncId = payload.syncId();
                if (syncId == screenHandler.syncId && screenHandler instanceof BankerScreenHandler) {
                    ArrayList<UUID> playersId = new ArrayList<>();
                    ArrayList<String> playersNames = new ArrayList<>();
                    for (Map.Entry<UUID, String> entry : BankerUtils.getPlayersNames().entrySet()) {
                        UUID playerId = entry.getKey();
                        if (playerId.equals(player.getUuid()))
                            continue;

                        playersId.add(entry.getKey());
                        playersNames.add(entry.getValue());
                    }
                    PlayersListPayload responsePayload = new PlayersListPayload(screenHandler.syncId, playersId, playersNames);
                    ServerPlayNetworking.send(context.player(), responsePayload);
                }
            });
        });

        // On request transfert
        ServerPlayNetworking.registerGlobalReceiver(RequestTransfertPayload.ID, (payload, context) -> {
            context.server().execute(() -> {
                ServerPlayerEntity player = context.player();
                ScreenHandler screenHandler = context.player().currentScreenHandler;
                int syncId = payload.syncId();
                if (syncId == screenHandler.syncId && screenHandler instanceof BankerScreenHandler) {
                    NumismaticAccount destinationAccount = BankerUtils.getAccountById(payload.destinationId());

                    if (payload.value() == 0)
                        return;

                    if (destinationAccount == null)
                        return;

                    NumismaticAccount originAccount = payload.originId().isPresent() ? BankerUtils.getMainAccount(player) : null;

                    if (payload.value() < 0 && !destinationAccount.canWithdraw(player) || (originAccount != null && !originAccount.canDeposit(player)))
                        return;

                    if (payload.value() > 0 && !destinationAccount.canDeposit(player) || (originAccount != null && !originAccount.canWithdraw(player)))
                        return;

                    // TODO: If the transfert failed due to the user rights, send a packet to update the screen (do not forget to resend the account info tu make sure that the user does not see the wrong relative balance)
                    // TODO: If the transfert failed due to the account being inexistant (was it deleted?) send a packet to update the screen

                    long value = payload.value();
                    if (value < 0) {
                        value = max(value, -destinationAccount.getBalance());
                    } else if (originAccount != null) {
                        value = min(value, originAccount.getBalance());
                    } else {
                        value = min(value, ModComponents.CURRENCY.get(player).getValue());
                    }

                    if (originAccount != null) {
                        originAccount.modify(player.getUuid(), -value);
                        destinationAccount.modify(player.getUuid(), value);
                    } else {
                        ModComponents.CURRENCY.get(player).silentModify(-value);
                        destinationAccount.modify(player.getUuid(), value);
                    }

                    AccountBalancePayload responsePayloadDestination = new AccountBalancePayload(screenHandler.syncId, destinationAccount.getId(), destinationAccount.getBalance());
                    ServerPlayNetworking.send(context.player(), responsePayloadDestination);

                    if (originAccount != null) {
                        AccountBalancePayload responsePayloadOrigin = new AccountBalancePayload(screenHandler.syncId, originAccount.getId(), originAccount.getBalance());
                        ServerPlayNetworking.send(context.player(), responsePayloadOrigin);
                    }
                }
            });
        });
    }
}

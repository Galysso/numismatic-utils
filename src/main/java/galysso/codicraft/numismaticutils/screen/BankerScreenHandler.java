package galysso.codicraft.numismaticutils.screen;

import galysso.codicraft.numismaticutils.network.responses.AccountInfoPayload;
import galysso.codicraft.numismaticutils.network.responses.AccountsListPayload;
import galysso.codicraft.numismaticutils.utils.BankerUtils;
import galysso.codicraft.numismaticutils.utils.ServerUtil;
import galysso.codicraft.numismaticutils.network.NetworkUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.village.Merchant;
import net.minecraft.village.SimpleMerchant;

import java.util.*;

public class BankerScreenHandler extends ScreenHandler {
    private final int PLAYERS_LIST_REFRESH_RATE = 20 * 60; // 1 minute

    // Base data
    public final Merchant merchant;
    public static PlayerEntity player;

    // Updates dates
    private Map<UUID, Long> accountsBalance_lastUpdate = new HashMap<>();
    private Long playersList_lastUpdate = (long) -PLAYERS_LIST_REFRESH_RATE;
    private Map<UUID, Long> accountsInfo_lastUpdate = new HashMap<>();

    // Updates trackers
    private boolean accountsListUpdated;
    private Map<UUID, Boolean> accountsBalanceUpdated = new HashMap<>();
    private boolean playersListUpdated;
    private  Map<UUID, Boolean> accountsInfoUpdated = new HashMap<>();

    // Main data
    public boolean canCreateNewAccount = false;
    public ArrayList<Integer> orderedEventsList = new ArrayList<>();
    public ArrayList<UUID> orderedPlayersList = new ArrayList<>();
    private Map<UUID, String> playersNames = new HashMap<>();












    public BankerScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleMerchant(playerInventory.player));
    }

    public BankerScreenHandler(int syncId, PlayerInventory playerInventory, Merchant merchant) {
        super(NetworkUtil.BANKER_SCREEN_HANDLER, syncId);
        this.merchant = merchant;
        player = playerInventory.player;
        accountsViewManager = new AccountsViewManager(playersViewManager);
    }

    /* ----- NEW IMPLEMENTATION ----- */

    private final PlayersViewManager playersViewManager = new PlayersViewManager();
    private final AccountsViewManager accountsViewManager;

    // GENERAL
    public static UUID getPlayerId() {
        return player.getUuid();
    }

    public void setPlayerInfo(UUID mainAccountId, boolean canCreateNewAccount) {
        accountsViewManager.setMainAccount(mainAccountId);
        //this.canCreateNewAccount = canCreateNewAccount;
    }

    // ACCOUNTS
    public AccountData getAccountAtIndex(int i) {
        return accountsViewManager.getAccountAtIndex(i);
    }

    public void updateAccountsListRightFilter(Optional<BankerUtils.RIGHT_TYPE> rightType) {
        accountsViewManager.updateAccountsListRightFilter(rightType);
    }

    public AccountData getFocusedAccount() {
        return accountsViewManager.getFocusedAccount();
    }

    public AccountData getMainAccount() {
        return accountsViewManager.getMainAccount();
    }

    public int getNbAccountsDisplayed() {
        return accountsViewManager.getNbAccountsDisplayed();
    }

    public boolean hasFocusedAccount() {
        return accountsViewManager.hasFocusedAccount();
    }

    public void resetFocusedAccount() {
        accountsViewManager.resetFocusedAccount();
    }

    public void selectAccountAtIndex(int i) {
        accountsViewManager.selectAccountAtIndex(i);
    }

    public void setBalance(UUID accountId, long balance) {
        accountsViewManager.setBalance(accountId, balance);
    }

    public void setFocusedAccount(UUID accountId) {
        accountsViewManager.setFocusedAccount(accountId);
    }

    public boolean shouldUpdateAccountsList() {
        return accountsViewManager.shouldUpdateAccountsList();
    }

    public boolean wasAccountsListUpdated() {
        return accountsViewManager.wasAccountsListUpdated();
    }

    public boolean wasFocusedAccountUpdated() {
        return accountsViewManager.wasFocusedAccountUpdated();
    }

    // PACKETS RECEPTION
    public void updateAccountInfo(AccountInfoPayload payload) {
        playersViewManager.updateNames(payload.playersId(), payload.playersNames());
        accountsViewManager.updateAccountInfo(payload);
    }

    /* ------------------------------ */



































    public UUID getPlayerAt(int i) {
        return orderedPlayersList.get(i);
    }

    public String getPlayerName(UUID playerId) {
        return playersNames.getOrDefault(playerId, "Unknown");
    }

    public int getOrderedPlayersListSize() {
        return orderedPlayersList.size();
    }

    public boolean isAccountsInfoUpdated(UUID accountId) {
        if (accountId == null)
            return false;

        if (accountsInfo_lastUpdate.containsKey(accountId)) {
            boolean updated = accountsInfoUpdated.getOrDefault(accountId, false);
            accountsInfoUpdated.put(accountId, false);
            return updated;
        }
        return false;
    }

    /*
    private void filterAccountsListByRightType(Optional<BankerUtils.RIGHT_TYPE> rightType, Optional<Integer> iconId) {
        if (!rightType.isPresent() && !iconId.isPresent()) {
            filteredAccountsList = orderedAccountsList;
            return;
        }
        filteredAccountsList = new ArrayList<>();
        for (UUID accountId : orderedAccountsList) {
            if (rightType.isPresent() && !accountsRights.get(accountId).equals(rightType.get())) {
                continue;
            }
            if (iconId.isPresent() && !accountsIcons.get(accountId).equals(iconId.get())) {
                continue;
            }
            filteredAccountsList.add(accountId);
        }
    }
*/


    public void updateAccountsList(AccountsListPayload payload) {
        accountsViewManager.updateAccountsList(payload);
    }

    public void setPlayersList(ArrayList<UUID> playersIds, ArrayList<String> playersNames) {
        playersListUpdated = true;
        playersList_lastUpdate = ServerUtil.getServerTicks();
        orderedPlayersList = playersIds;
        for (int i = 0; i < playersIds.size(); i++) {
            UUID id = playersIds.get(i);
            this.playersNames.put(id, playersNames.get(i));
        }
    }

    public boolean isPlayersListUpdated() {
        if (playersListUpdated) {
            playersListUpdated = false;
            return true;
        } else {
            return false;
        }
    }

    public boolean shouldUpdatePlayersList() {
        return ServerUtil.getServerTicks() > playersList_lastUpdate + PLAYERS_LIST_REFRESH_RATE;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return null;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);

        this.merchant.setCustomer(null);
    }
}

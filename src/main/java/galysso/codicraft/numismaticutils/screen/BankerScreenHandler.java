package galysso.codicraft.numismaticutils.screen;

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
    private final int BALANCE_REFRESH_RATE = 20; // 1 second
    private final int ACCOUNT_LIST_REFRESH_RATE = 20 * 60; // 1 minute

    private Map<UUID, Long> accountsBalance_lastUpdate;
    private Long accountList_lastUpdate = (long) -ACCOUNT_LIST_REFRESH_RATE;

    // Updates trackers
    private boolean accountsListUpdated;
    private boolean mainBalanceUpdated;
    private Map<UUID, Boolean> accountsBalanceUpdated = new HashMap<>();

    public final Merchant merchant;

    public UUID mainAccountId;
    private long mainAccountBalance;
    public boolean canCreateNewAccount = false;
    private Map<UUID, Long> accountsBalance = new HashMap<>();
    public Map<UUID, String> accountsNames = new HashMap<>();
    public Map<UUID, BankerUtils.RIGHT_TYPE> accountsRights = new HashMap<>();
    public Map<UUID, Integer> accountsIcons = new HashMap<>();
    private ArrayList<UUID> orderedAccountsList = new ArrayList<>();
    public ArrayList<Integer> orderedEventsList = new ArrayList<>();
    public ArrayList<UUID> orderedPlayersList = new ArrayList<>();

    public BankerScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleMerchant(playerInventory.player));
        accountsBalance = new HashMap<>();
        accountsBalance_lastUpdate = new HashMap<>();
    }

    public BankerScreenHandler(int syncId, PlayerInventory playerInventory, Merchant merchant) {
        super(NetworkUtil.BANKER_SCREEN_HANDLER, syncId);
        this.merchant = merchant;
    }

    public ArrayList<UUID> getOrderedAccountsList() {
        return orderedAccountsList;
    }

    public UUID getAccountAt(int i) {
        return orderedAccountsList.get(i);
    }

    public int getOrderedAccountsListSize() {
        return orderedAccountsList.size();
    }

    public boolean isMainAccountBalanceUpdated() {
        if (mainBalanceUpdated) {
            mainBalanceUpdated = false;
            return true;
        } else {
            return false;
        }
    }

    public boolean isAccountBalanceUpdated(UUID accountId) {
        if (accountId == null)
            return false;

        if (accountsBalanceUpdated.containsKey(accountId)) {
            boolean updated = accountsBalanceUpdated.get(accountId);
            accountsBalanceUpdated.put(accountId, false);
            return updated;
        }
        return false;
    }

    public long getMainAccountBalance() {
        return mainAccountBalance;
    }

    public long getAccountBalance(UUID accountId) {
        if (accountId == null)
            return 0;

        accountsBalanceUpdated.put(accountId, true);
        return accountsBalance.getOrDefault(accountId, 0L);
    }

    public void setAccountsList(ArrayList<UUID> accountsIds, ArrayList<String> accountNames, ArrayList<BankerUtils.RIGHT_TYPE> accountRights, ArrayList<Integer> accountIcons) {
        accountsListUpdated = true;
        accountList_lastUpdate = ServerUtil.getServerTicks();
        orderedAccountsList = accountsIds;
        for (int i = 0; i < orderedAccountsList.size(); i++) {
            UUID id = orderedAccountsList.get(i);
            accountsNames.put(id, accountNames.get(i));
            accountsRights.put(id, accountRights.get(i));
            accountsIcons.put(id, accountIcons.get(i));
        }
    }

    public void setBalance(UUID accountId, long balance) {
        accountsBalance_lastUpdate.put(accountId, ServerUtil.getServerTicks());
        accountsBalance.put(accountId, balance);
        accountsBalanceUpdated.put(accountId, true);
        if (accountId.equals(mainAccountId)) {
            mainBalanceUpdated = true;
            mainAccountBalance = balance;
        }
    }

    public void setPlayerInfo(UUID mainAccountId, boolean canCreateNewAccount) {
        this.mainAccountId = mainAccountId;
        this.canCreateNewAccount = canCreateNewAccount;
    }

    public boolean shouldUpdateAccountBalance(UUID accountId) {
        return accountId != null && (!accountsBalance_lastUpdate.containsKey(accountId) || ServerUtil.getServerTicks() > accountsBalance_lastUpdate.get(accountId) + BALANCE_REFRESH_RATE);
    }

    public boolean accountsListUpdated() {
        if (accountsListUpdated) {
            accountsListUpdated = false;
            return true;
        } else {
            return false;
        }
    }

    public boolean shouldUpdateAccountsList() {
        return ServerUtil.getServerTicks() > accountList_lastUpdate + ACCOUNT_LIST_REFRESH_RATE;
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

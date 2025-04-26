package galysso.codicraft.numismaticutils.screen;

import galysso.codicraft.numismaticutils.network.responses.AccountInfoPayload;
import galysso.codicraft.numismaticutils.network.responses.AccountsListPayload;
import galysso.codicraft.numismaticutils.utils.BankerUtils;
import galysso.codicraft.numismaticutils.utils.ServerUtil;

import java.util.*;

public class AccountsViewManager {
    // CONSTANTS
    private final int ACCOUNT_LIST_REFRESH_RATE = 20 * 60; // 1 minute

    // Other managers
    PlayersViewManager playersViewManager;

    // Navigation data
    private Optional<BankerUtils.RIGHT_TYPE> rightFilter = Optional.empty();
    private Optional<Integer> iconFilter = Optional.empty();
    private AccountData focusedAccount = null;

    // Update timers
    private long accountListTimer = -ACCOUNT_LIST_REFRESH_RATE;

    // Update tracker data
    private boolean focusedAccountWasUpdated = false;
    private boolean accountsListWasUpdated = false;

    // Data
    private AccountData mainAccount = new AccountData(UUID.randomUUID(), "", 0, 0, BankerUtils.RIGHT_TYPE.READ_ONLY, playersViewManager); // Default account before receiving actual account info
    private Map<UUID, AccountData> accountsMap = new HashMap<>();

    // View data (accounts list)
    private ArrayList<AccountData> accountsList = new ArrayList<>();
    //private ArrayList<AccountData> filteredAndSortedAccountsList = new ArrayList<>();


    // Constructor
    public AccountsViewManager(PlayersViewManager playersViewManager) {
        this.playersViewManager = playersViewManager;
    }

    // Public methods
    public boolean hasFocusedAccount() {
        return focusedAccount != null;
    }

    public AccountData getAccountAtIndex(int i) {
        if (i < 0 || i >= accountsList.size()) {
            return null;
        }
        return accountsList.get(i);
    }

    public AccountData getFocusedAccount() {
        return focusedAccount;
    }

    public AccountData getMainAccount() {
        return mainAccount;
    }

    public int getNbAccountsDisplayed() {
        return accountsList.size();
    }

    public void resetFocusedAccount() {
        focusedAccountWasUpdated = focusedAccount != null;
        focusedAccount = null;
    }

    public void selectAccountAtIndex(int i) {
        AccountData previousFocusedAccount = focusedAccount;
        if (i < 0 || i >= accountsList.size()) {
            return;
        }
        focusedAccount = accountsList.get(i);
        focusedAccountWasUpdated = previousFocusedAccount == null || previousFocusedAccount.getId() != focusedAccount.getId();
        System.out.println("Selected account: " + focusedAccount);
    }

    public void setBalance(UUID accountId, long balance) {
        AccountData accountData = accountsMap.get(accountId);
        if (accountData == null) {
            accountData = new AccountData(accountId, playersViewManager);
            accountsMap.put(accountId, accountData);

            generateFilteredList(); // TODO: Sort and filter only if relevant
        }
        accountData.setBalance(balance);

        // TODO: Probably sort here is more relevant, based on carefully designed conditions
    }

    public void setFocusedAccount(UUID accountId) {
        AccountData previousFocusedAccount = focusedAccount;
        if (accountsMap.containsKey(accountId)) {
            focusedAccount = accountsMap.get(accountId);
            focusedAccountWasUpdated = true;
        }
        focusedAccountWasUpdated = previousFocusedAccount == null || previousFocusedAccount.getId() != focusedAccount.getId();
    }

    public void setMainAccount(UUID accountId) {
        if (accountsMap.containsKey(accountId)) {
            mainAccount = accountsMap.get(accountId);
        } else {
            mainAccount = new AccountData(accountId, playersViewManager);
            accountsMap.put(accountId, mainAccount); // This is the main account, no need to regenerate the filtered list
        }
    }

    public boolean shouldUpdateAccountsList() {
        return ServerUtil.getServerTicks() > accountListTimer + ACCOUNT_LIST_REFRESH_RATE;
    }

    // Packets reception
    public void updateAccountInfo(AccountInfoPayload payload) {
        UUID accountId = payload.accountId();
        if (accountsMap.containsKey(accountId)) {
            AccountData accountData = accountsMap.get(accountId);
            accountData.updateAccountInfo(payload);
        }
    }

    public void updateAccountsList(AccountsListPayload payload) {
        accountsListWasUpdated = true;
        accountListTimer = ServerUtil.getServerTicks();

        accountsList = new ArrayList<>();
        for (int i = 0; i < payload.accountsIds().size(); i++) {
            UUID id = payload.accountsIds().get(i);
            String name = payload.accountsNames().get(i);
            int icon = payload.accountsIcons().get(i);
            BankerUtils.RIGHT_TYPE right = payload.accountsRights().get(i);

            AccountData account = accountsMap.get(id);
            if (account == null) {
                account = new AccountData(id, name, icon, 0, right, playersViewManager);
                accountsMap.put(id, account);
            } else {
                account.updateName(name);
                account.updateIcon(icon);
                account.updateRight(right);
            }
        }
        // TODO: Design carefully the conditions under which the list should be again filtered / sorted

        generateFilteredList();
    }

    public void updateAccountsListRightFilter(Optional<BankerUtils.RIGHT_TYPE> newRightFilter) {
        rightFilter =  newRightFilter;

        // TODO: Probably something smarter to do here
        generateFilteredList();
    }

    public boolean wasAccountsListUpdated() {
        if (accountsListWasUpdated) {
            accountsListWasUpdated = false;
            return true;
        } else {
            return false;
        }
    }

    public boolean wasFocusedAccountUpdated() {
        if (focusedAccountWasUpdated) {
            focusedAccountWasUpdated = false;
            return true;
        } else {
            return false;
        }
    }

    // Private methods
    private void generateFilteredList() {
        accountsListWasUpdated = true;
        accountsList = new ArrayList<>();

        System.out.println("fitler: " + rightFilter + " icon: " + iconFilter);
        for (AccountData account : accountsMap.values()) {
            if (account.getId() == mainAccount.getId()) {
                System.out.println("main account is removed");
                continue;
            }
            if (rightFilter.isPresent() && !account.getRight().equals(rightFilter.get())) {
                System.out.println("account with " + account.getRight() + " was removed");
                continue;
            }
            if (iconFilter.isPresent() && account.getIcon() != iconFilter.get()) {
                System.out.println("account icon " + account.getIcon() + " was removed");
                continue;
            }
            accountsList.add(account);
        }

        sortAccountsList();
    }

    private void sortAccountsList() {

    }

    public enum SORT_TYPE {
        RIGHT,
        ICON,
        NAME,
        BALANCE
    }
}

package galysso.codicraft.numismaticutils.utils;

import galysso.codicraft.numismaticutils.banking.NumismaticAccount;
import galysso.codicraft.numismaticutils.banking.PlayerNavigationInfo;
import galysso.codicraft.numismaticutils.nbt.BankerUtilsNbtConverter;
import galysso.codicraft.numismaticutils.network.ClientState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.*;

public class BankerUtils {
    /* Account UUID => Account */
    private static Map<UUID, NumismaticAccount> accountsMap;
    /* Owner UUID => Main account */
    private static Map<UUID, NumismaticAccount> mainAccountsByOwner;
    /* Owner UUID => List of shared accounts owned by the player */
    private static Map<UUID, ArrayList<NumismaticAccount>> sharedAccountsByOwner;
    /* UUID of the player that has rights on the account (including owner) => Account */
    private static Map<UUID, ArrayList<NumismaticAccount>> sharedAccountsByParticipant; // the order can be changed via arrows in the GUI
    /* Player UUID => player's navigation information */
    private static Map<UUID, PlayerNavigationInfo> playersNavigationInfo;
    /* Player UUID => name */
    private static Map<UUID, String> playersNames;

    /* Player UUID => Client state */
    private static Map<UUID, ClientState> clientsStates; // TODO: Think about it, probably not everything should be updated live


    public static void loadData() {
        accountsMap = BankerUtilsNbtConverter.loadAccountsMap();
        mainAccountsByOwner = BankerUtilsNbtConverter.loadMainAccountsByOwner();
        sharedAccountsByOwner = BankerUtilsNbtConverter.loadSharedAccountsByOwner();
        sharedAccountsByParticipant = BankerUtilsNbtConverter.loadSharedAccountsByParticipant();
        playersNames = BankerUtilsNbtConverter.loadPlayersNames();
        playersNavigationInfo = new HashMap<>();
    }

    public static void saveData() {
        BankerUtilsNbtConverter.saveAccountsMap(accountsMap);
        BankerUtilsNbtConverter.saveMainAccountsByOwner(mainAccountsByOwner);
        BankerUtilsNbtConverter.saveSharedAccountsByOwner(sharedAccountsByOwner);
        BankerUtilsNbtConverter.saveSharedAccountsByParticipant(sharedAccountsByParticipant);
        BankerUtilsNbtConverter.savePlayersNames(playersNames);
    }

    public static NumismaticAccount openMainAccount(PlayerEntity owner) {
        // Check if the player has an account
        NumismaticAccount numismaticAccount = mainAccountsByOwner.get(owner.getUuid());
        if (numismaticAccount == null) {
            // Create a new account for the player
            numismaticAccount = new NumismaticAccount(owner.getUuid());
            accountsMap.put(numismaticAccount.getId(), numismaticAccount);
            mainAccountsByOwner.put(owner.getUuid(), numismaticAccount);
            return numismaticAccount;
        } else {
            return null;
        }
    }

    public static NumismaticAccount openSharedAccount(PlayerEntity owner, String accountName) {
        // Check if the player has an account
        if (canOpenSharedAccount(owner)) {
            // Create the account
            NumismaticAccount numismaticAccount = new NumismaticAccount(owner.getUuid(), accountName);

            // Insert the new account to the global map of accounts
            accountsMap.put(numismaticAccount.getId(), numismaticAccount);

            // Insert the new account at the beginning of the list (per owner)
            List<NumismaticAccount> numismaticAccounts = getSharedAccountsForOwner(owner);
            numismaticAccounts.addFirst(numismaticAccount);

            // Insert the new account at the beginning of the list (per participant)
            numismaticAccounts = getSharedAccountsForParticipant(owner);
            numismaticAccounts.addFirst(numismaticAccount);

            return numismaticAccount;
        }
        return null;
    }

    public static void registerPlayer(ServerPlayerEntity player) {
        System.out.println("Registering player " + player.getUuid() + " => " + player.getName().getString());
        playersNames.put(player.getUuid(), player.getName().getString());
    }

    public static boolean canOpenSharedAccount(PlayerEntity player) {
        return getSharedAccountsForOwner(player).size() <= 3;
    }

    public static NumismaticAccount getAccountById(UUID accountId) {
        return accountsMap.get(accountId);
    }

    public static NumismaticAccount getMainAccount(PlayerEntity player) {
        NumismaticAccount numismaticAccount = mainAccountsByOwner.get(player.getUuid());
        if (numismaticAccount == null) {
            numismaticAccount = openMainAccount(player);
            mainAccountsByOwner.put(player.getUuid(), numismaticAccount);
        }
        return numismaticAccount;
    }

    public static List<NumismaticAccount> getSharedAccountsForOwner(PlayerEntity player) {
        ArrayList<NumismaticAccount> numismaticAccounts = sharedAccountsByOwner.get(player.getUuid());
        if (numismaticAccounts == null) {
            numismaticAccounts = new ArrayList<>();
        }
        sharedAccountsByOwner.put(player.getUuid(), numismaticAccounts);
        return numismaticAccounts;
    }

    public static List<NumismaticAccount> getSharedAccountsForParticipant(PlayerEntity participant) {
        ArrayList<NumismaticAccount> numismaticAccounts = sharedAccountsByParticipant.get(participant.getUuid());
        if (numismaticAccounts == null) {
            numismaticAccounts = new ArrayList<>();
        }
        sharedAccountsByParticipant.put(participant.getUuid(), numismaticAccounts);
        return numismaticAccounts;
    }

    public static PlayerNavigationInfo getNavigationInfo(UUID playerId) {
        PlayerNavigationInfo playerNavigationInfo = BankerUtils.playersNavigationInfo.get(playerId);
        if (playerNavigationInfo == null) {
            playerNavigationInfo = new PlayerNavigationInfo();
            BankerUtils.playersNavigationInfo.put(playerId, playerNavigationInfo);
        }
        return playerNavigationInfo;
    }

    public static String getPlayerName(UUID playerId) {
        return playersNames.get(playerId);
    }

    public static Map<UUID, String> getPlayersNames() {
        return playersNames;
    }

    public static List<NumismaticAccount> getSharedAccounts(PlayerEntity participant) {
        List<NumismaticAccount> sharedNumismaticAccounts = sharedAccountsByParticipant.get(participant.getUuid());
        for (int i = 0; i < sharedNumismaticAccounts.size(); i++) {
            NumismaticAccount numismaticAccount = sharedNumismaticAccounts.get(i);
            if (!numismaticAccount.canSee(participant)) {
                sharedNumismaticAccounts.remove(i);
                i--;
            }
        }
        return sharedNumismaticAccounts;
    }

    public static List<NumismaticAccount> getAllAccounts() {
        List<NumismaticAccount> numismaticAccounts = new ArrayList<>(mainAccountsByOwner.values().stream().toList());
        numismaticAccounts.addAll(sharedAccountsByOwner.values().stream().flatMap(List::stream).toList());
        return numismaticAccounts;
    }

    public static List<NumismaticAccount> getVisibleAccountsList(PlayerEntity player) {
        PlayerNavigationInfo playerNavigationInfo = BankerUtils.getNavigationInfo(player.getUuid());
        return getVisibleAccountsList(player, playerNavigationInfo.getFilterRights(), playerNavigationInfo.getFilterIconId());
    }

    public static List<NumismaticAccount> getVisibleAccountsList(PlayerEntity player, Optional<BankerUtils.RIGHT_TYPE> rightType, Optional<Integer> iconId) {
        List<NumismaticAccount> unfilteredList;
        if (player.isCreative() || player.isSpectator()) {
            unfilteredList = accountsMap.values().stream().toList();
        } else {
            unfilteredList = getSharedAccountsForParticipant(player);
        }
        if (!rightType.isPresent() && !iconId.isPresent()) {
            return unfilteredList;
        }
        List<NumismaticAccount> filteredList = new ArrayList<>();
        for (NumismaticAccount numismaticAccount : unfilteredList) {
            if (rightType.isPresent() && numismaticAccount.getPlayerRights(player) != rightType.get()) {
                continue;
            }
            if (iconId.isPresent() && !Objects.equals(numismaticAccount.getIconId(player), iconId.get())) {
                continue;
            }
            filteredList.add(numismaticAccount);
        }
        return filteredList;
    }

    public static List<NumismaticAccount> getAccounts(PlayerEntity player) {
        if (player.isSpectator() || player.isCreative()) {
            return getAllAccounts();
        }

        // Return all accounts of the player
        List<NumismaticAccount> playerNumismaticAccounts = new ArrayList<>();
        playerNumismaticAccounts.add(getMainAccount(player));
        playerNumismaticAccounts.addAll(getSharedAccounts(player));

        return playerNumismaticAccounts;
    }

    public enum RIGHT_TYPE {
        OWNER,
        CO_OWNER,
        BENEFICIARY,
        CONTRIBUTOR,
        READ_ONLY
    }
}

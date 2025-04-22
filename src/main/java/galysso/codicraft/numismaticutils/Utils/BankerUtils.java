package galysso.codicraft.numismaticutils.Utils;

import galysso.codicraft.numismaticutils.banking.NumismaticAccount;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BankerUtils {
    /* Account UUID => Account */
    private static Map<UUID, NumismaticAccount> accountsByUUID;
    /* Owner UUID => Main account */
    private static Map<UUID, NumismaticAccount> mainAccountsByOwner;
    /* Owner UUID => List of shared accounts owned by the player */
    private static Map<UUID, List<NumismaticAccount>> sharedAccountsByOwner;
    /* UUID of the player that has rights on the account (including owner) => Account */
    private static Map<UUID, List<NumismaticAccount>> sharedAccountsByParticipant; // the order can be changed via arrows in the GUI

    public static NumismaticAccount openMainAccount(PlayerEntity player) {
        // Check if the player has an account
        NumismaticAccount numismaticAccount = mainAccountsByOwner.get(player.getUuid());
        if (numismaticAccount == null) {
            // Create a new account for the player
            numismaticAccount = new NumismaticAccount();
            mainAccountsByOwner.put(player.getUuid(), numismaticAccount);
            return numismaticAccount;
        } else {
            return null;
        }
    }

    public static Boolean openSharedAccount(PlayerEntity owner, List<PlayerEntity> otherPlayers, List<RIGHT_TYPE> rights) {
        // Check if the player has an account
        if (canOpenSharedAccount(owner)) {
            List<NumismaticAccount> numismaticAccounts = getOwnedSharedAccounts(owner);
            NumismaticAccount numismaticAccount = new NumismaticAccount();
            numismaticAccounts.add(numismaticAccount);
            sharedAccountsByOwner.get(owner.getUuid()).add(numismaticAccount);
            return true;
        }
        return false;
    }

    public static boolean canOpenSharedAccount(PlayerEntity player) {
        return getOwnedSharedAccounts(player).size() <= 3;
    }

    public static NumismaticAccount getMainAccount(PlayerEntity player) {
        NumismaticAccount numismaticAccount = mainAccountsByOwner.get(player.getUuid());
        if (numismaticAccount == null) {
            numismaticAccount = openMainAccount(player);
            mainAccountsByOwner.put(player.getUuid(), numismaticAccount);
        }
        return numismaticAccount;
    }

    public static List<NumismaticAccount> getOwnedSharedAccounts(PlayerEntity player) {
        List<NumismaticAccount> numismaticAccounts = sharedAccountsByOwner.get(player.getUuid());
        if (numismaticAccounts == null) {
            numismaticAccounts = new ArrayList<>();
        }
        return numismaticAccounts;
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
        BENEFICIARY,
        CONTRIBUTOR,
        READ_ONLY
    }
}

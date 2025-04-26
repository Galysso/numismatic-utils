package galysso.codicraft.numismaticutils.nbt;

import galysso.codicraft.numismaticutils.NumismaticUtilsMain;
import galysso.codicraft.numismaticutils.banking.NumismaticAccount;
import galysso.codicraft.numismaticutils.utils.BankerUtils;
import galysso.codicraft.numismaticutils.utils.ServerUtil;
import net.minecraft.nbt.*;
import net.minecraft.util.FixedBufferInputStream;
import net.minecraft.util.WorldSavePath;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

//@Environment(EnvType.SERVER)
public class BankerUtilsNbtConverter {
    private static final long MAX_NBT_SIZE = 2097152L; // 2MB
    private static final int MAX_NBT_DEPTH = 512;     // Example max nesting depth

    private final static String ACCOUNTS_MAP_NBT_KEY = NumismaticUtilsMain.MOD_ID + "_banker_accounts_map.nbt";
    private final static String MAIN_ACCOUNTS_ID_MAP_NBT_KEY = NumismaticUtilsMain.MOD_ID + "_banker_main_accounts_id_map.nbt";
    private final static String SHARED_ACCOUNTS_ID_LIST_MAP_BY_OWNER_NBT_KEY = NumismaticUtilsMain.MOD_ID + "_banker_shared_accounts_id_list_map.nbt";
    private final static String SHARED_ACCOUNTS_ID_LIST_MAP_BY_PARTICIPANTS_NBT_KEY = NumismaticUtilsMain.MOD_ID + "_banker_shared_accounts_id_list_map_by_participants.nbt";
    private final static String PLAYERS_NAMES_NBT_KEY = NumismaticUtilsMain.MOD_ID + "_banker_players_names.nbt";

    private static NbtCompound loadNbtFromFile(Path path) {
        if (Files.exists(path)) {
            try (InputStream inputStream = Files.newInputStream(path);
                 InputStream bufferedInputStream = new FixedBufferInputStream(inputStream)) {
                NbtSizeTracker sizeTracker = new NbtSizeTracker(MAX_NBT_SIZE, MAX_NBT_DEPTH);
                return net.minecraft.nbt.NbtIo.readCompressed(bufferedInputStream, sizeTracker);
            } catch (IOException e) {
                System.err.println("[YourMod] Error loading NBT from " + path + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
        return null;
    }

    private static void saveNbtToFile(NbtCompound nbt, Path path) {
        try {
            Files.createDirectories(path.getParent()); // Ensure the parent directory exists
            net.minecraft.nbt.NbtIo.writeCompressed(nbt, path);
            System.out.println("[YourMod] Saved NBT data to " + path);
        } catch (IOException e) {
            System.err.println("[YourMod] Error saving NBT to " + path + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void saveAccountsMap(Map<UUID, NumismaticAccount> accountsMap) {
        Path dataPath = ServerUtil.getServer().getSavePath(WorldSavePath.ROOT).resolve(ACCOUNTS_MAP_NBT_KEY);
        NbtCompound accountsMapNbt = serializeAccountsMap(accountsMap);
        saveNbtToFile(accountsMapNbt, dataPath);
    }

    public static Map<UUID, NumismaticAccount> loadAccountsMap() {
        Path dataPath = ServerUtil.getServer().getSavePath(WorldSavePath.ROOT).resolve(ACCOUNTS_MAP_NBT_KEY);
        NbtCompound accountsMapNbt = loadNbtFromFile(dataPath);
        if (accountsMapNbt == null) {
            return new HashMap<>();
        }
        return deSerializeAccountsMap(accountsMapNbt);
    }

    public static void saveMainAccountsByOwner(Map<UUID, NumismaticAccount> mainAccountsByOwner) {
        Path dataPath = ServerUtil.getServer().getSavePath(WorldSavePath.ROOT).resolve(MAIN_ACCOUNTS_ID_MAP_NBT_KEY);
        NbtCompound accountsMapNbt = serializeAccountIdMap(mainAccountsByOwner);
        saveNbtToFile(accountsMapNbt, dataPath);
    }

    public static Map<UUID, NumismaticAccount> loadMainAccountsByOwner() {
        Path dataPath = ServerUtil.getServer().getSavePath(WorldSavePath.ROOT).resolve(MAIN_ACCOUNTS_ID_MAP_NBT_KEY);
        NbtCompound accountsMapNbt = loadNbtFromFile(dataPath);
        if (accountsMapNbt == null) {
            return new HashMap<>();
        }
        return deSerializeAccountIdMap(accountsMapNbt);
    }

    public static void saveSharedAccountsByOwner(Map<UUID, ArrayList<NumismaticAccount>> sharedAccountsByOwner) {
        Path dataPath = ServerUtil.getServer().getSavePath(WorldSavePath.ROOT).resolve(SHARED_ACCOUNTS_ID_LIST_MAP_BY_OWNER_NBT_KEY);
        NbtCompound accountsListMapNbt = serializeAccountIdListMap(sharedAccountsByOwner);
        saveNbtToFile(accountsListMapNbt, dataPath);
    }

    public static Map<UUID, ArrayList<NumismaticAccount>> loadSharedAccountsByOwner() {
        Path dataPath = ServerUtil.getServer().getSavePath(WorldSavePath.ROOT).resolve(SHARED_ACCOUNTS_ID_LIST_MAP_BY_OWNER_NBT_KEY);
        NbtCompound accountsListMapNbt = loadNbtFromFile(dataPath);
        if (accountsListMapNbt == null) {
            return new HashMap<>();
        }
        return deSerializeAccountIdListMap(accountsListMapNbt);
    }

    public static void saveSharedAccountsByParticipant(Map<UUID, ArrayList<NumismaticAccount>> sharedAccountsByParticipants) {
        Path dataPath = ServerUtil.getServer().getSavePath(WorldSavePath.ROOT).resolve(SHARED_ACCOUNTS_ID_LIST_MAP_BY_PARTICIPANTS_NBT_KEY);
        NbtCompound accountsListMapNbt = serializeAccountIdListMap(sharedAccountsByParticipants);
        saveNbtToFile(accountsListMapNbt, dataPath);
    }

    public static Map<UUID, ArrayList<NumismaticAccount>> loadSharedAccountsByParticipant() {
        Path dataPath = ServerUtil.getServer().getSavePath(WorldSavePath.ROOT).resolve(SHARED_ACCOUNTS_ID_LIST_MAP_BY_PARTICIPANTS_NBT_KEY);
        NbtCompound accountsListMapNbt = loadNbtFromFile(dataPath);
        if (accountsListMapNbt == null) {
            return new HashMap<>();
        }
        return deSerializeAccountIdListMap(accountsListMapNbt);
    }

    public static void savePlayersNames(Map<UUID, String> playersNames) {
        Path dataPath = ServerUtil.getServer().getSavePath(WorldSavePath.ROOT).resolve(PLAYERS_NAMES_NBT_KEY);
        NbtList playersNamesListNbt = new NbtList();
        for (Map.Entry<UUID, String> entry : playersNames.entrySet()) {
            NbtCompound playerNameNbt = new NbtCompound();
            playerNameNbt.putUuid("uuid", entry.getKey());
            playerNameNbt.putString("value", entry.getValue());
            playersNamesListNbt.add(playerNameNbt);
        }
        NbtCompound playersNamesNbt = new NbtCompound();
        playersNamesNbt.put("value", playersNamesListNbt);
        saveNbtToFile(playersNamesNbt, dataPath);
    }

    public static Map<UUID, String> loadPlayersNames() {
        Path dataPath = ServerUtil.getServer().getSavePath(WorldSavePath.ROOT).resolve(PLAYERS_NAMES_NBT_KEY);
        NbtCompound playersNamesNbt = loadNbtFromFile(dataPath);
        Map<UUID, String> playersNames = new HashMap<>();
        if (playersNamesNbt == null) {
            return playersNames;
        }
        if (playersNamesNbt.contains("value", NbtElement.LIST_TYPE)) {
            NbtList playersNamesListNbt = playersNamesNbt.getList("value", NbtElement.COMPOUND_TYPE);
            for (int i = 0; i < playersNamesListNbt.size(); i++) {
                NbtCompound entryNbt = playersNamesListNbt.getCompound(i);
                UUID key = entryNbt.getUuid("uuid");
                String value = entryNbt.getString("value");
                playersNames.put(key, value);
            }
        }
        return playersNames;
    }

    private static NbtCompound serializeAccountsMap(Map<UUID, NumismaticAccount> accountsById) {
        System.out.println("[Numismatic Utils]: Serializing accounts map...");
        NbtList accountsListNbt = new NbtList();
        for (Map.Entry<UUID, NumismaticAccount> entry : accountsById.entrySet()) {
            NbtCompound mapKeyValue = new NbtCompound();
            mapKeyValue.putUuid("uuid", entry.getKey());
            NbtCompound accountNbt = NumismaticAccountNbtConverter.serializeAccount(entry.getValue());
            mapKeyValue.put("value", accountNbt); // Save enum as String
            accountsListNbt.add(mapKeyValue);
        }
        NbtCompound res = new NbtCompound();
        res.put("accounts", accountsListNbt);
        return res;
    }

    private static Map<UUID, NumismaticAccount> deSerializeAccountsMap(NbtCompound accountsMapNbt) {
        System.out.println("[Numismatic Utils]: Deserializing accounts map...");
        Map<UUID, NumismaticAccount> accountsMap = new HashMap<>();
        if (accountsMapNbt.contains("accounts", NbtElement.LIST_TYPE)) {
            NbtList accountsListNbt = accountsMapNbt.getList("accounts", NbtElement.COMPOUND_TYPE);
            for (int i = 0; i < accountsListNbt.size(); i++) {
                NbtCompound entryNbt = accountsListNbt.getCompound(i);
                UUID key = entryNbt.getUuid("uuid");
                NbtCompound accountNbt = entryNbt.getCompound("value");
                NumismaticAccount account = NumismaticAccountNbtConverter.deserializeAccount(accountNbt);
                System.out.println("[Numismatic Utils]: Deserialized account: " + account.getId() + " with key: " + key);
                accountsMap.put(key, account);
            }
        }
        return accountsMap;
    }

    private static NbtCompound serializeAccountIdMap(Map<UUID, NumismaticAccount> accountsById) {
        System.out.println("[Numismatic Utils]: Serializing accounts id map...");
        NbtList accountsListIdNbt = new NbtList();
        for (Map.Entry<UUID, NumismaticAccount> entry : accountsById.entrySet()) {
            NbtCompound mapKeyValue = new NbtCompound();
            System.out.println("[Numismatic Utils]: (id only) Serializing " + entry.getKey() + " => " + entry.getValue().getId());
            mapKeyValue.putUuid("uuid", entry.getKey());
            mapKeyValue.putUuid("value", entry.getValue().getId()); // Save enum as String
            accountsListIdNbt.add(mapKeyValue);
        }
        NbtCompound res = new NbtCompound();
        res.put("accounts", accountsListIdNbt);
        return res;
    }

    private static Map<UUID, NumismaticAccount> deSerializeAccountIdMap(NbtCompound accountsNbt) {
        System.out.println("[Numismatic Utils]: Deserializing accounts id map...");
        Map<UUID, NumismaticAccount> accountsById = new HashMap<>();
        if (accountsNbt.contains("accounts", NbtElement.LIST_TYPE)) {
            NbtList accountsListNbt = accountsNbt.getList("accounts", NbtElement.COMPOUND_TYPE);
            for (int i = 0; i < accountsListNbt.size(); i++) {
                NbtCompound entryNbt = accountsListNbt.getCompound(i);
                UUID key = entryNbt.getUuid("uuid");
                UUID value = entryNbt.getUuid("value");
                NumismaticAccount account = BankerUtils.getAccountById(value);
                if (account != null) {
                    accountsById.put(key, account);
                }
            }
        }
        return accountsById;
    }

    private static NbtCompound serializeAccountIdListMap(Map<UUID, ArrayList<NumismaticAccount>> accountsListById) {
        NbtList accountsListIdNbt = new NbtList();
        for (Map.Entry<UUID, ArrayList<NumismaticAccount>> entry : accountsListById.entrySet()) {
            NbtCompound mapKeyValue = new NbtCompound();
            mapKeyValue.putUuid("uuid", entry.getKey());
            mapKeyValue.put("value", serializeUuidList(entry.getValue()));
            accountsListIdNbt.add(mapKeyValue);
        }
        NbtCompound res = new NbtCompound();
        res.put("accountsLists", accountsListIdNbt);
        return res;
    }

    private static Map<UUID, ArrayList<NumismaticAccount>> deSerializeAccountIdListMap(NbtCompound accountsNbt) {
        Map<UUID, ArrayList<NumismaticAccount>> accountsListsById = new HashMap<>();
        if (accountsNbt.contains("accountsLists", NbtElement.LIST_TYPE)) {
            NbtList accountsListNbt = accountsNbt.getList("accountsLists", NbtElement.COMPOUND_TYPE);
            for (int i = 0; i < accountsListNbt.size(); i++) {
                NbtCompound entryNbt = accountsListNbt.getCompound(i);
                UUID key = entryNbt.getUuid("uuid");
                ArrayList<NumismaticAccount> accountsList = deserializeUUIDList(entryNbt.getList("value", NbtElement.COMPOUND_TYPE));
                accountsListsById.put(key, accountsList);
            }
        }
        return accountsListsById;
    }

    private static NbtList serializeUuidList(ArrayList<NumismaticAccount> numismaticAccountList) {
        NbtList nbtList = new NbtList();
        for (NumismaticAccount numismaticAccount : numismaticAccountList) {
            NbtCompound uuidTag = new NbtCompound();
            uuidTag.putUuid("value", numismaticAccount.getId());
            nbtList.add(uuidTag);
        }
        return nbtList;
    }

    private static ArrayList<NumismaticAccount> deserializeUUIDList(NbtList nbtList) {
        ArrayList<NumismaticAccount> numismaticAccountList = new ArrayList<>();
        for (int i = 0; i < nbtList.size(); i++) {
            NbtCompound uuidTag = nbtList.getCompound(i);
            NumismaticAccount account = BankerUtils.getAccountById(uuidTag.getUuid("value"));
            if (account != null) {
                numismaticAccountList.add(account);
            }
        }
        return numismaticAccountList;
    }

}

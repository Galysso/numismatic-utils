package galysso.codicraft.numismaticutils.nbt;

import galysso.codicraft.numismaticutils.utils.BankerUtils;
import galysso.codicraft.numismaticutils.banking.NumismaticAccount;
import net.minecraft.nbt.NbtCompound;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NumismaticAccountNbtConverter {
    public static NbtCompound serializeAccount(NumismaticAccount account) {
        NbtCompound accountNbt = new NbtCompound();
        accountNbt.putUuid("id", account.getId());
        accountNbt.putString("name", account.getName());
        accountNbt.putUuid("owner", account.getOwnerId());
        accountNbt.put("iconsId", serializeIconsId(account.getIconsId()));
        accountNbt.put("participants", serializeParticipants(account.getParticipants()));
        accountNbt.put("pendingRequests", serializePendingRequests(account.getPendingRequests()));
        accountNbt.putLong("balance", account.getBalance());
        return accountNbt;
    }

    private static NbtCompound serializeIconsId(Map<UUID, Integer> iconsId) {
        NbtCompound participantsNbt = new NbtCompound();
        for (Map.Entry<UUID, Integer> entry : iconsId.entrySet()) {
            participantsNbt.putUuid("uuid", entry.getKey());
            participantsNbt.putInt("value", entry.getValue()); // Save enum as String
        }
        return participantsNbt;
    }

    private static NbtCompound serializeParticipants(Map<UUID, BankerUtils.RIGHT_TYPE> participants) {
        NbtCompound participantsNbt = new NbtCompound();
        for (Map.Entry<UUID, BankerUtils.RIGHT_TYPE> entry : participants.entrySet()) {
            participantsNbt.putUuid("uuid", entry.getKey());
            participantsNbt.putString("value", entry.getValue().name()); // Save enum as String
        }
        return participantsNbt;
    }

    private static NbtCompound serializePendingRequests(Map<UUID, Boolean> pendingRequests) {
        NbtCompound pendingRequestsNbt = new NbtCompound();
        for (Map.Entry<UUID, Boolean> entry : pendingRequests.entrySet()) {
            pendingRequestsNbt.putUuid("uuid", entry.getKey());
            pendingRequestsNbt.putBoolean("value", entry.getValue());
        }
        return pendingRequestsNbt;
    }

    public static NumismaticAccount deserializeAccount(NbtCompound accountNbt) {
        UUID id = accountNbt.getUuid("id");
        String name = accountNbt.getString("name");
        UUID ownerId = accountNbt.getUuid("owner");
        Map<UUID, Integer> iconsId = deserializeIconsId(accountNbt.getCompound("iconsId"));
        Map<UUID, BankerUtils.RIGHT_TYPE> participants = deserializeParticipants(accountNbt.getCompound("participants"));
        Map<UUID, Boolean> pendingRequests = deserializePendingRequests(accountNbt.getCompound("pendingRequests"));
        long balance = accountNbt.getLong("balance");
        return new NumismaticAccount(id, name, ownerId, participants, pendingRequests, balance);
    }

    private static Map<UUID, Integer> deserializeIconsId(NbtCompound iconsIdNbt) {
        Map<UUID, Integer> participants = new HashMap<>();
        for (String key : iconsIdNbt.getKeys()) {
            if (key.equals("uuid")) { // Assuming each entry has a "uuid" and "value"
                UUID uuid = iconsIdNbt.getUuid("uuid");
                Integer iconId = iconsIdNbt.getInt("value");
                participants.put(uuid, iconId);
            }
        }
        return participants;
    }

    private static Map<UUID, BankerUtils.RIGHT_TYPE> deserializeParticipants(NbtCompound participantsNbt) {
        Map<UUID, BankerUtils.RIGHT_TYPE> participants = new HashMap<>();
        for (String key : participantsNbt.getKeys()) {
            if (key.equals("uuid")) { // Assuming each entry has a "uuid" and "value"
                UUID uuid = participantsNbt.getUuid("uuid");
                String rightName = participantsNbt.getString("value");
                try {
                    BankerUtils.RIGHT_TYPE right = BankerUtils.RIGHT_TYPE.valueOf(rightName);
                    participants.put(uuid, right);
                } catch (IllegalArgumentException e) {
                    System.err.println("Could not deserialize BankerUtils.RIGHT_TYPE: " + rightName);
                }
            }
        }
        return participants;
    }

    private static Map<UUID, Boolean> deserializePendingRequests(NbtCompound pendingRequestsNbt) {
        Map<UUID, Boolean> pendingRequests = new HashMap<>();
        for (String key : pendingRequestsNbt.getKeys()) {
            if (key.equals("uuid")) { // Assuming each entry has a "uuid" and "pending"
                UUID uuid = pendingRequestsNbt.getUuid("uuid");
                boolean pending = pendingRequestsNbt.getBoolean("pending");
                pendingRequests.put(uuid, pending);
            }
        }
        return pendingRequests;
    }
}

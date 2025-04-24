package galysso.codicraft.numismaticutils.banking;

import galysso.codicraft.numismaticutils.utils.BankerUtils;
import net.minecraft.entity.player.PlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NumismaticAccount {
    private UUID id;
    private String name;
    private UUID ownerId;
    private Map<UUID, Integer> iconsId; /* player Id => icon Id */
    private Map<UUID, BankerUtils.RIGHT_TYPE> participants;
    private Map<UUID, Boolean> pendingRequests;
    private long balance;
    private boolean deleted;

    public NumismaticAccount(UUID ownerId) {
        this.id = UUID.randomUUID();
        this.name = "";
        this.ownerId = ownerId;
        this.iconsId = new HashMap<UUID, Integer>();
        this.participants = new HashMap<UUID, BankerUtils.RIGHT_TYPE>() {{
            put(ownerId, BankerUtils.RIGHT_TYPE.OWNER);
        }};
        this.pendingRequests = new HashMap<UUID, Boolean>();
        this.balance = 0;
        this.deleted = false;
    }

    public NumismaticAccount(UUID ownerId, String name) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.ownerId = ownerId;
        this.iconsId = new HashMap<UUID, Integer>();
        this.participants = new HashMap<UUID, BankerUtils.RIGHT_TYPE>() {{
            put(ownerId, BankerUtils.RIGHT_TYPE.OWNER);
        }};
        this.pendingRequests = new HashMap<UUID, Boolean>();
        this.balance = 0;
        this.deleted = false;
    }

    public NumismaticAccount(UUID accountId, String name, UUID ownerId, Map<UUID, BankerUtils.RIGHT_TYPE> participants, Map<UUID, Boolean> pendingRequests, long balance) {
        this.id = accountId;
        this.name = name;
        this.ownerId = ownerId;
        this.iconsId = new HashMap<UUID, Integer>();
        this.participants = participants;
        this.pendingRequests = pendingRequests;
        this.balance = balance;
        this.deleted = false;
    }

    public void modify(long amount) {
        balance += amount;
    }

    public boolean canSee(PlayerEntity player) {
        return participants.containsKey(player.getUuid());
    }

    public boolean canDeposit(PlayerEntity player) {
        return participants.get(player.getUuid()) == BankerUtils.RIGHT_TYPE.OWNER || participants.get(player.getUuid()) == BankerUtils.RIGHT_TYPE.BENEFICIARY;
    }

    public boolean canWithdraw(PlayerEntity player) {
        return participants.get(player.getUuid()) == BankerUtils.RIGHT_TYPE.OWNER || participants.get(player.getUuid()) == BankerUtils.RIGHT_TYPE.BENEFICIARY;
    }

    public long getBalance() {
        return balance;
    }

    public Integer getIconId(PlayerEntity player) {
        return iconsId.getOrDefault(player.getUuid(), 0);
    }

    public Map<UUID, Integer> getIconsId() {
        return iconsId;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public Map<UUID, BankerUtils.RIGHT_TYPE> getParticipants() {
        return participants;
    }

    public Map<UUID, Boolean> getPendingRequests() {
        return pendingRequests;
    }

    public BankerUtils.RIGHT_TYPE getPlayerRights(PlayerEntity player) {
        return participants.get(player.getUuid());
    }
}

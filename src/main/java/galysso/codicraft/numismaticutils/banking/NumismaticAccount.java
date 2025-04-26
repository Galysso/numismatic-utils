package galysso.codicraft.numismaticutils.banking;

import galysso.codicraft.numismaticutils.banking.events.Event;
import galysso.codicraft.numismaticutils.utils.BankerUtils;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
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
    private Map<UUID, Long> playersRelativeBalance;
    private ArrayList<Event> eventsList;

    public NumismaticAccount(UUID ownerId) {
        this(ownerId, "");
    }

    public NumismaticAccount(UUID ownerId, String name) {
        this(UUID.randomUUID(), name, ownerId, new HashMap<>(), new HashMap<>(), 0, new HashMap<>());
        this.participants.put(ownerId, BankerUtils.RIGHT_TYPE.OWNER);
        this.playersRelativeBalance.put(ownerId, 0L);
    }

    public NumismaticAccount(UUID accountId, String name, UUID ownerId, Map<UUID, BankerUtils.RIGHT_TYPE> participants, Map<UUID, Boolean> pendingRequests, long balance, Map<UUID, Long> playersRelativeBalance) {
        this.id = accountId;
        this.name = name;
        this.ownerId = ownerId;
        this.iconsId = new HashMap<>();
        this.participants = participants;
        this.pendingRequests = pendingRequests;
        this.balance = balance;
        this.deleted = false;
        this.playersRelativeBalance = playersRelativeBalance;
    }

    public void modify(UUID authorId, long amount) {
        balance += amount;
        long newRelativeBalance = playersRelativeBalance.getOrDefault(authorId, 0L) + amount;
        playersRelativeBalance.put(authorId, newRelativeBalance);
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
        return getPlayerRights(player.getUuid());
    }

    public BankerUtils.RIGHT_TYPE getPlayerRights(UUID playerId) {
        return participants.get(playerId);
    }

    public Map<UUID, Long> getPlayersRelativeBalance() {
        return playersRelativeBalance;
    }
}

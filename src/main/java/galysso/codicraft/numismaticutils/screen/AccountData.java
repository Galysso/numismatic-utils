package galysso.codicraft.numismaticutils.screen;

import galysso.codicraft.numismaticutils.network.responses.AccountInfoPayload;
import galysso.codicraft.numismaticutils.utils.BankerUtils;
import galysso.codicraft.numismaticutils.utils.ServerUtil;

import java.util.*;

public class AccountData {
    // CONSTANTS
    private final int BALANCE_REFRESH_RATE = 20; // 1 second
    private final int ACCOUNT_INFO_REFRESH_RATE = 20 * 60; // 1 minute

    // Other managers
    private PlayersViewManager playersViewManager;

    // Navigation data
    private Optional<BankerUtils.RIGHT_TYPE> rightFilter = Optional.empty();

    // Update timers
    private long lastBalanceUpdate = -BALANCE_REFRESH_RATE;
    private long lastInfoUpdate = -ACCOUNT_INFO_REFRESH_RATE;

    // Update tracker data
    private boolean wasBalanceUpdated = false;
    private boolean wasInfoUpdated = false;

    // data
    private UUID id;
    private String name;
    private int icon;
    private long balance;
    private BankerUtils.RIGHT_TYPE right;
    private Map<UUID, ParticipantData> participantsMap = new HashMap<>();

    // View data (players list)
    private ArrayList<ParticipantData> participantsList = new ArrayList<>();

    // Constructor
    public AccountData(UUID id, PlayersViewManager playersViewManager) {
        this(id, "", 0, 0, null, playersViewManager);
    }

    public AccountData(UUID id, String name, int icon, long balance, BankerUtils.RIGHT_TYPE right, PlayersViewManager playersViewManager) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.balance = balance;
        this.right = right;
        this.playersViewManager = playersViewManager;
    }

    // public methods
    public long getBalance() {
        return balance;
    }

    public int getIcon() {
        return icon;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getNbParticipantsDisplayed() {
        return participantsList.size();
    }

    public ParticipantData getParticipantAtIndex(int index) {
        if (index < 0 || index >= participantsList.size()) {
            return null;
        }
        return participantsList.get(index);
    }

    public BankerUtils.RIGHT_TYPE getRight() {
        return right;
    }

    public void setBalance(long newBalance) {
        lastBalanceUpdate = ServerUtil.getServerTicks();
        long deltaBalance = newBalance - balance;
        wasBalanceUpdated = deltaBalance != 0;
        balance = newBalance;
    }

    public boolean shouldUpdateBalance() {
        return ServerUtil.getServerTicks() > lastBalanceUpdate + BALANCE_REFRESH_RATE;
    }

    public boolean shouldUpdateInfo() {
        return ServerUtil.getServerTicks() > lastInfoUpdate + ACCOUNT_INFO_REFRESH_RATE;
    }

    public void updateIcon(int icon) {
        this.icon = icon;
    }

    public void updateRelativeBalance_fromClient(long deltaBalance) { // Should only be used from the client!!!!
        ParticipantData participantData = participantsMap.get(BankerScreenHandler.getPlayerId());
        if (participantData != null) {
            participantData.setRelativeBalance(participantData.getRelativeBalance() + deltaBalance);

            // TODO: Probably sort again if relevant
        }
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateRight(BankerUtils.RIGHT_TYPE right) {
        this.right = right;
    }

    public boolean wasBalanceUpdated() {
        return wasBalanceUpdated;
    }

    // Packets reception
    public void updateAccountInfo(AccountInfoPayload payload) {
        lastInfoUpdate = ServerUtil.getServerTicks();
        wasInfoUpdated = true;

        for (int i = 0; i < payload.playersId().size(); i++) {
            UUID playerId = payload.playersId().get(i);
            ParticipantData participantData = participantsMap.get(playerId);
            BankerUtils.RIGHT_TYPE right = payload.playersRightTypes().get(i);
            long relativeBalance = payload.playersRelativeBalance().get(i);
            if (participantData == null) {
                participantData = new ParticipantData(right, relativeBalance, playersViewManager.getPlayerData(playerId));
                participantsMap.put(playerId, participantData);
            } else {
                participantData.setRight(right);
                participantData.setRelativeBalance(relativeBalance);
            }
        }
        // TODO: Filter and sort, I guess...

        generateFilteredParticipantsList();
    }

    // Private methods
    public void generateFilteredParticipantsList() {
        participantsList = new ArrayList<>();

        for (ParticipantData participantData : participantsMap.values()) {
            if (rightFilter.isPresent() && participantData.getRight() != rightFilter.get()) {
                continue;
            }
            participantsList.add(participantData);
        }

        sortParticipantsList();
    }

    public void sortParticipantsList() {

    }
}

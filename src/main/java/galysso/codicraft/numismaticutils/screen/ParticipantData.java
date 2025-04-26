package galysso.codicraft.numismaticutils.screen;

import galysso.codicraft.numismaticutils.utils.BankerUtils;

public class ParticipantData {
    private BankerUtils.RIGHT_TYPE right;
    private long relativeBalance;
    private PlayerData playerData;

    public ParticipantData(BankerUtils.RIGHT_TYPE right, long relativeBalance, PlayerData playerData) {
        this.right = right;
        this.relativeBalance = relativeBalance;
        this.playerData = playerData;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    public long getRelativeBalance() {
        return relativeBalance;
    }

    public BankerUtils.RIGHT_TYPE getRight() {
        return right;
    }

    public void setRelativeBalance(long newRelativeBalance) {
        relativeBalance = newRelativeBalance;
    }

    public void setRight(BankerUtils.RIGHT_TYPE newRight) {
        right = newRight;
    }
}
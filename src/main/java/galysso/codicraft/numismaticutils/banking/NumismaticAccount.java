package galysso.codicraft.numismaticutils.banking;

import galysso.codicraft.numismaticutils.Utils.BankerUtils;
import galysso.codicraft.numismaticutils.Utils.NumismaticUtils;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Map;
import java.util.UUID;

public class NumismaticAccount {
    UUID uuid;
    private String name;
    private UUID owner;
    private Map<UUID, BankerUtils.RIGHT_TYPE> participants;
    private Map<UUID, Boolean> pendingRequests;
    private NumismaticUtils.CoinsTuple balance;

    public boolean canSee(PlayerEntity player) {
        return participants.containsKey(player.getUuid());
    }
}

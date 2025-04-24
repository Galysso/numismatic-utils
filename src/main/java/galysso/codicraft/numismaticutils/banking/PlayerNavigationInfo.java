package galysso.codicraft.numismaticutils.banking;

import galysso.codicraft.numismaticutils.utils.BankerUtils;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Optional;

public class PlayerNavigationInfo {
    /* Filtering settings */
    private Optional<BankerUtils.RIGHT_TYPE> filterRights;
    private Optional<Integer> filterIconId;

    public PlayerNavigationInfo() {
        this.filterRights = Optional.empty();
        this.filterIconId = Optional.empty();
    }

    public PlayerNavigationInfo(Optional<BankerUtils.RIGHT_TYPE> filterRights, Optional<Integer> filterIconId) {
        this.filterRights = filterRights;
        this.filterIconId = filterIconId;
    }

    public Optional<Integer> getFilterIconId() {
        return filterIconId;
    }

    public void setFilterIconId(Optional<Integer> filterIconId) {
        this.filterIconId = filterIconId;
    }

    public Optional<BankerUtils.RIGHT_TYPE> getFilterRights() {
        return filterRights;
    }

    public void setFilterRights(Optional<BankerUtils.RIGHT_TYPE> filterRights) {
        this.filterRights = filterRights;
    }
}

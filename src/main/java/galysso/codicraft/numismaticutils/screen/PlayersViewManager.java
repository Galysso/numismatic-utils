package galysso.codicraft.numismaticutils.screen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayersViewManager {
    Map<UUID, PlayerData> playersMap = new HashMap<>();


    public PlayerData getPlayerData(UUID playerId) {
        PlayerData playerData = playersMap.get(playerId);
        if (playerData == null) {
            playerData = new PlayerData(playerId, "");
            playersMap.put(playerId, playerData);
        }
        return playerData;
    }

    public void updateNames(ArrayList<UUID> playersIds, ArrayList<String> playersNames) {
        for (int i = 0; i < playersIds.size(); i++) {
            UUID playerId = playersIds.get(i);
            String playerName = playersNames.get(i);
            PlayerData playerData = playersMap.get(playerId);
            if (playerData == null) {
                playerData = new PlayerData(playerId, playerName);
                playersMap.put(playerId, playerData);
            } else {
                playerData.setName(playerName);
            }
        }
    }
}

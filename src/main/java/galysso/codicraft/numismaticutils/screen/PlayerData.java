package galysso.codicraft.numismaticutils.screen;

import java.util.UUID;

public class PlayerData {
    private UUID id;
    private String name;

    PlayerData(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

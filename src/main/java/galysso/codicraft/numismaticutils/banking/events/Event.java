package galysso.codicraft.numismaticutils.banking.events;

import java.util.UUID;

public interface Event {
    public UUID getSource(); // Either a player or an account
    public String getMessage(); // The message to be displayed to the player
    // public void onAccept(); // The action to process when (REQUIRED?)
    // public void onDeny(); // The action to process when the player denies the event (REQUIRED?)
}

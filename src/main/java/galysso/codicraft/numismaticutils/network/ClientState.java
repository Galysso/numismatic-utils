package galysso.codicraft.numismaticutils.network;

public class ClientState {
    private final static long GRACE_PERIOD = 20*10; // 10 seconds

    private long lastResponseDate;
    private boolean keepAliveResponseExpected;

    ClientState() {
        lastResponseDate = 0;
        keepAliveResponseExpected = false;
    }
}

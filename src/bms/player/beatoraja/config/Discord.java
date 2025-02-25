package bms.player.beatoraja.config;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;

public class Discord {
    public static final DiscordRichPresence presence = new DiscordRichPresence();

    private static final DiscordRPC lib = DiscordRPC.INSTANCE;

    private static final String APPLICATIONID = "876968973126746182"; // DISCORD APPLICATION ID   (https://discord.com/developers/applications)

    public String state;
    public String details;
    public long startTimestamp;

    public Discord(String state, String details) {
        this.startTimestamp = System.currentTimeMillis() / 1000;
        this.state = state;
        this.details = details;
    }

    public static Discord playingSong(String fulltitle, String artist, int mode) {
        String state = "Playing: " + mode + "Keys";
        String details = fulltitle + " / " + artist;
        return new Discord(state, details);
    }

    public void startup() {
        String steamId = "";
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        handlers.ready = (user) -> System.out.println("Discord RPC Ready!");
        lib.Discord_Initialize(APPLICATIONID, handlers, true, steamId);
        DiscordRichPresence presence = new DiscordRichPresence();
        lib.Discord_UpdatePresence(presence);
        // in a worker thread
        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                lib.Discord_RunCallbacks();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {
                }
            }
        }, "RPC-Callback-Handler").start();

    }

    public void update() {
        presence.details = details;
        presence.state = state;
        presence.startTimestamp = startTimestamp;
        presence.largeImageKey = "bms";
        lib.Discord_UpdatePresence(presence);
    }

}

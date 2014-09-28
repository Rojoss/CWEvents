package com.clashwars.cwevents.event;

import com.clashwars.cwevents.CWEvents;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class PluginMessageEvents implements PluginMessageListener {

    private CWEvents cwe;

    public PluginMessageEvents(CWEvents cwe) {
        this.cwe = cwe;
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equalsIgnoreCase("CWBungee")) {
            return;
        }
        final DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));

        cwe.getServer().getScheduler().runTaskLater(cwe, new Runnable() {
            @Override
            public void run() {
                try {
                    String ch = in.readUTF();
                    if (ch.equalsIgnoreCase("eventdatarequest")) {
                        String sender = in.readUTF();
                        String server = in.readUTF();
                        try {
                            //  'eventdata' message format: sender | server | event | arena | players | slots | status
                            ByteArrayOutputStream b = new ByteArrayOutputStream();
                            DataOutputStream out = new DataOutputStream(b);

                            out.writeUTF("eventdata");
                            out.writeUTF(sender);
                            out.writeUTF(server);
                            out.writeUTF(cwe.getEM().getEvent() == null ? "none" : cwe.getEM().getEvent().getName());
                            out.writeUTF(cwe.getEM().getArena() == null || cwe.getEM().getArena() == "" ? "none" : cwe.getEM().getArena());
                            out.writeUTF(cwe.getEM().getPlayers() == null ? "0" : "" + cwe.getEM().getPlayers().size());
                            out.writeUTF(cwe.getEM().getSlots() < 2 ? "Infinite" : "" + cwe.getEM().getSlots());
                            out.writeUTF(cwe.getEM().getStatus() == null ? "&cUnknown" : cwe.getEM().getStatus().getName());

                            Bukkit.getOnlinePlayers()[0].sendPluginMessage(cwe, "CWBungee", b.toByteArray());
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }

        }, 5);
    }
}

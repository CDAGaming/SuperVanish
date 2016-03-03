package de.myzelyam.supervanish.hider;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import de.myzelyam.supervanish.SuperVanish;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ServerlistPacketListener {

    private final SuperVanish plugin;

    private final FileConfiguration settings;

    public ServerlistPacketListener(SuperVanish plugin) {
        this.plugin = plugin;
        settings = plugin.settings;
    }

    public void registerListener() {
        if ((!settings
                .getBoolean("Configuration.Serverlist.AdjustAmountOfOnlinePlayers"))
                && (!settings.getBoolean("Configuration.Serverlist.AdjustListOfLoggedInPlayers")))
            return;
        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(plugin, ListenerPriority.NORMAL,
                        PacketType.Status.Server.OUT_SERVER_INFO) {

                    @Override
                    public void onPacketSending(PacketEvent e) {
                        try {
                            if (e.getPacketType() == PacketType.Status.Server.OUT_SERVER_INFO) {
                                WrappedServerPing ping = e.getPacket()
                                        .getServerPings().read(0);
                                Collection<Player> invisiblePlayers = ServerlistPacketListener.this.
                                        plugin.getOnlineInvisiblePlayers();
                                int invisiblePlayersCount = invisiblePlayers.size();
                                int onlinePlayersCount = Bukkit.getOnlinePlayers().size();
                                if (settings.getBoolean("Configuration.Serverlist.AdjustAmountOfOnlinePlayers")) {
                                    ping.setPlayersOnline(onlinePlayersCount - invisiblePlayersCount);
                                }
                                if (settings.getBoolean("Configuration.Serverlist.AdjustListOfLoggedInPlayers")) {
                                    List<WrappedGameProfile> wrappedGameProfiles = new ArrayList<>();
                                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                                        WrappedGameProfile profile = WrappedGameProfile
                                                .fromPlayer(onlinePlayer);
                                        if (!invisiblePlayers.contains(onlinePlayer))
                                            wrappedGameProfiles.add(profile);
                                    }
                                    ping.setPlayers(wrappedGameProfiles);
                                }
                            }
                        } catch (Exception er) {
                            ServerlistPacketListener.this.plugin.printException(er);
                        }
                    }
                });
    }
}
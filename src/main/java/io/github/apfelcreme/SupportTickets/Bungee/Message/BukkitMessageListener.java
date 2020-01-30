package io.github.apfelcreme.SupportTickets.Bungee.Message;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import io.github.apfelcreme.SupportTickets.Bungee.SupportTickets;
import io.github.apfelcreme.SupportTickets.Bungee.Ticket.Location;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.IOException;
import java.util.UUID;

/**
 * Copyright (C) 2016 Lord36 aka Apfelcreme
 * <p>
 * This program is free software;
 * you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * <p>
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, see <http://www.gnu.org/licenses/>.
 *
 * @author Lord36 aka Apfelcreme
 */
public class BukkitMessageListener implements Listener {

    private final SupportTickets plugin;

    public BukkitMessageListener(SupportTickets plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPluginMessageReceived(PluginMessageEvent event) throws IOException {

        if (!event.getTag().startsWith("tickets:")) {
            return;
        }
        if (!(event.getSender() instanceof Server)) {
            event.setCancelled(true);
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
        if (event.getTag().equals("tickets:position")) {
            UUID uuid = UUID.fromString(in.readUTF());
            BukkitPositionAnswer answer = BukkitMessenger.getQueuedPositionAnswer(uuid);
            if (answer == null) {
                return;
            }

            String server = in.readUTF();
            ServerInfo serverInfo = SupportTickets.getServer(server);
            if (serverInfo != null) {
                server = serverInfo.getName();
            } else {
                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
                if (player != null && player.getServer() != null && player.getServer().getInfo() != null) {
                    server = player.getServer().getInfo().getName();
                }
            }
            Location location = new Location(server, in.readUTF(), in.readDouble(), in.readDouble(),
                    in.readDouble(), in.readDouble(), in.readDouble());

            answer.onAnswer(location);
        }
    }
}

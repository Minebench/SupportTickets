package io.github.apfelcreme.SupportTickets.Bungee;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
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
public class SupportTickets extends Plugin implements Listener {

    @Override
    public void onEnable() {
        getProxy().registerChannel("SupportTickets");
        getProxy().getPluginManager().registerListener(this, this);
    }

    @EventHandler
    public void onPluginMessageReceived(PluginMessageEvent e) throws IOException {
        if (!e.getTag().equals("SupportTickets")) {
            return;
        }
        if (!(e.getSender() instanceof Server)) {
            return;
        }

        ByteArrayInputStream stream = new ByteArrayInputStream(e.getData());
        DataInputStream in = new DataInputStream(stream);
        Operation operation = Operation.valueOf(in.readUTF());
        switch (operation) {
            case MESSAGE:
                sendPlayerMessage(UUID.fromString(in.readUTF()), in.readUTF());
                break;
            case TEAMMESSAGE:
                sendTeamMessage(in.readUTF());
                break;
            case WARP:
                sendPlayerToTicket(UUID.fromString(in.readUTF()), in.readInt(), in.readUTF());
                break;
        }
    }

    /**
     * sends a player to a ticket location
     *
     * @param uuid     the player uuid
     * @param ticketId the ticket id
     * @param serverIp the ip of the server the ticket was created on (xxx.xxx.xxx.xxx:PORT)
     * @throws IOException
     */
    private void sendPlayerToTicket(UUID uuid, Integer ticketId, String serverIp) throws IOException {
        ServerInfo target = getTargetServer(serverIp);

        if (target != null && target.getAddress().getAddress().isReachable(2000)) {
            ProxiedPlayer player = getProxy().getPlayer(uuid);
            if (player != null && !player.getServer().getInfo().equals(target)) {
                player.connect(target);
            }
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("WARP");
            out.writeUTF(uuid.toString());
            out.writeInt(ticketId);
            target.sendData("SupportTickets", out.toByteArray());
        }

    }

    /**
     * sends a text message to all players who have a certain permission
     *
     * @param message the message
     */
    private void sendTeamMessage(String message) {
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (player.hasPermission("SupportTickets.team")) {
                player.sendMessage(TextComponent.fromLegacyText(message));
            }
        }
    }

    /**
     * sends a text message to a player
     *
     * @param uuid    the player uuid
     * @param message the text message
     */
    private void sendPlayerMessage(UUID uuid, String message) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
        if (player != null) {
            player.sendMessage(TextComponent.fromLegacyText(message));
        }
    }

    /**
     * returns the server info with the given ip (xxx.xxx.xxx.xxx:PORT)
     *
     * @param serverIp the ip:port
     * @return the serverInfo
     */
    private ServerInfo getTargetServer(String serverIp) {
        for (ServerInfo serverInfo : getProxy().getServers().values()) {
            if (serverInfo.getAddress().equals(new InetSocketAddress(serverIp.split(":")[0],
                    Integer.parseInt(serverIp.split(":")[1])))) {
                return serverInfo;
            }
        }
        return null;
    }

    public enum Operation {
        MESSAGE, TEAMMESSAGE, WARP
    }
}

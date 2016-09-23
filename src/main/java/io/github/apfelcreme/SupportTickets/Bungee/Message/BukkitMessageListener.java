package io.github.apfelcreme.SupportTickets.Bungee.Message;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import io.github.apfelcreme.SupportTickets.Bungee.SupportTickets;
import io.github.apfelcreme.SupportTickets.Bungee.SupportTicketsConfig;
import io.github.apfelcreme.SupportTickets.Bungee.Ticket.Location;
import io.github.apfelcreme.SupportTickets.Bungee.Ticket.Ticket;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.IOException;
import java.util.Date;
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

    @EventHandler
    public void onPluginMessageReceived(PluginMessageEvent event) throws IOException {

        if (!event.getTag().equals("SupportTickets")) {
            return;
        }
        if (!(event.getSender() instanceof Server)) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
        String subChannel = in.readUTF();
        if (subChannel.equals("POSITIONANSWER")) {
            UUID uuid = UUID.fromString(in.readUTF());
            Location location = new Location(in.readUTF(), in.readUTF(), in.readDouble(), in.readDouble(),
                    in.readDouble(), in.readDouble(), in.readDouble());
            String message = in.readUTF();

            Ticket ticket = new Ticket(uuid, message, new Date(), location, Ticket.TicketStatus.OPEN);
            Integer ticketId = SupportTickets.getDatabaseController().saveTicket(ticket);
            SupportTickets.sendMessage(uuid, SupportTicketsConfig.getInstance().getText("info.new.created"));
            SupportTickets.sendTeamMessage(SupportTicketsConfig.getInstance().getText("info.new.newTicket")
                    .replace("{0}", ticketId.toString())
                    .replace("{1}", SupportTickets.getInstance().getNameByUUID(uuid))
                    .replace("{2}", message));
        }
    }
}

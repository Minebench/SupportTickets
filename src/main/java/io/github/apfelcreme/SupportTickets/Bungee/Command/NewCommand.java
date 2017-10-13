package io.github.apfelcreme.SupportTickets.Bungee.Command;

import io.github.apfelcreme.SupportTickets.Bungee.Message.BukkitMessenger;
import io.github.apfelcreme.SupportTickets.Bungee.SupportTickets;
import io.github.apfelcreme.SupportTickets.Bungee.SupportTicketsConfig;
import io.github.apfelcreme.SupportTickets.Bungee.Ticket.Ticket;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Date;

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
public class NewCommand extends SubCommand {

    public NewCommand(SupportTickets plugin, String name, String usage, String permission, String... aliases) {
        super(plugin, name, usage, permission, aliases);
    }

    /**
     * executes a subcommand
     *
     * @param sender the sender
     * @param args   the string arguments in an array
     */
    public void execute(final CommandSender sender, final String[] args) {
        final ProxiedPlayer player = (ProxiedPlayer) sender;

        StringBuilder mb = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            mb.append(args[i]).append(" ");
        }
        BukkitMessenger.fetchPosition(player, (location) -> {
            Ticket ticket = new Ticket(player.getUniqueId(), mb.toString().trim(), new Date(), location, Ticket.TicketStatus.OPEN);
            int ticketId = plugin.getDatabaseController().saveTicket(ticket);
            plugin.sendMessage(player, plugin.getConfig().getText("info.new.created"));
            plugin.sendTeamMessage(plugin.getConfig().getText("info.new.newTicket")
                    .replace("{0}", String.valueOf(ticketId))
                    .replace("{1}", player.getName())
                    .replace("{2}", ticket.getMessage()));
            plugin.getProxy().getPlayers().stream()
                    .filter(p -> p.hasPermission("SupportTickets.mod"))
                    .forEach(p -> SupportTickets.getInstance().addShownTicket(p, ticket.getTicketId()));
        });
    }
}

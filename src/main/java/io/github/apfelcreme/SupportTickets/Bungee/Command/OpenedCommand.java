package io.github.apfelcreme.SupportTickets.Bungee.Command;

import io.github.apfelcreme.SupportTickets.Bungee.SupportTickets;
import io.github.apfelcreme.SupportTickets.Bungee.Ticket.Ticket;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
public class OpenedCommand extends SubCommand {

    public OpenedCommand(SupportTickets plugin, String name, String usage, String permission, String... aliases) {
        super(plugin, name, usage, permission, aliases);
    }

    /**
     * executes a subcommand
     *
     * @param sender the sender
     * @param args   the string arguments in an array
     */
    @Override
    public void execute(CommandSender sender, String[] args) {
        UUID target = sender instanceof ProxiedPlayer ? ((ProxiedPlayer) sender).getUniqueId() : new UUID(0, 0);
        if (args.length > 1) {
            target = plugin.getUUIDByName(args[1]);
            if (target == null) {
                plugin.sendMessage(sender, "error.unknownPlayer");
                return;
            }
        }

        int page = 0;
        if ((args.length > 2) && SupportTickets.isNumeric(args[2])) {
            page = Integer.parseInt(args[2]) - 1;
        }
        if (page < 0) {
            page = 0;
        }
        List<Ticket> tickets = plugin.getDatabaseController().getTicketsOpenedBy(target).stream()
                .filter(t -> sender.hasPermission("SupportTickets.mod.server." + t.getLocation().getServer()))
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        //display the results
        int pageSize = plugin.getConfig().getPageSize();
        int maxPages = (int) Math.ceil((float) tickets.size() / pageSize);
        if (page >= maxPages - 1) {
            page = maxPages - 1;
        }

        if (page < 0) {
            page = 0;
        }

        plugin.sendMessage(sender, "info.opened.header",
                "player", args[1],
                "page", String.valueOf(page + 1),
                "maxpages", String.valueOf(maxPages),
                "amount", String.valueOf(tickets.size()));

        for (int i = page * pageSize; i < (page + 1) * pageSize && i < tickets.size(); i++) {
            Ticket ticket = tickets.get(i);
            plugin.sendMessage(sender, "info.list.element",
                    "id", String.valueOf(ticket.getTicketId()),
                    "date", SupportTickets.formatDate(ticket.getDate()),
                    "online", plugin.isPlayerOnline(ticket.getSender())
                            ? plugin.getConfig().getText("info.list.online")
                            : plugin.getConfig().getText("info.list.offline"),
                    "sender", plugin.getNameByUUID(ticket.getSender()),
                    "assigned", ticket.getAssigned() != null ? ticket.getAssigned() : "*",
                    "message", ticket.getMessage(),
                    "comments", Integer.toString(ticket.getComments().size()));
            plugin.addShownTicket(sender, ticket.getTicketId());
        }
        plugin.sendMessage(sender, "info.opened.footer");
    }
}

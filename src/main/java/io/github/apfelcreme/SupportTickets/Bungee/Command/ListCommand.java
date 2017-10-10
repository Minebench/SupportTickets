package io.github.apfelcreme.SupportTickets.Bungee.Command;

import io.github.apfelcreme.SupportTickets.Bungee.SupportTickets;
import io.github.apfelcreme.SupportTickets.Bungee.Ticket.Ticket;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.apache.commons.lang.StringUtils;

import java.util.Comparator;
import java.util.List;
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
public class ListCommand extends SubCommand {

    public ListCommand(SupportTickets plugin, String name, String usage, String permission, String... aliases) {
        super(plugin, name, usage, permission, aliases);
    }

    /**
     * executes a subcommand
     *
     * @param sender the sender
     * @param args   the string arguments in an array
     */
    public void execute(CommandSender sender, final String[] args) {
        int page = 0;
        if (SupportTickets.isNumeric(args[args.length - 1])) {
            page = Integer.parseInt(args[args.length - 1]) - 1;
        }
        if (page < 0) {
            page = 0;
        }

        Ticket.TicketStatus messageStatus = Ticket.TicketStatus.OPEN;
        Ticket.TicketStatus[] statuses = new Ticket.TicketStatus[]{
                Ticket.TicketStatus.OPEN,
                Ticket.TicketStatus.ASSIGNED,
                Ticket.TicketStatus.REOPENED
        };
        if (args.length > 1 && !SupportTickets.isNumeric(args[1])) {
            try {
                messageStatus = Ticket.TicketStatus.valueOf(args[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                SupportTickets.sendMessage(sender, plugin.getConfig().getText("error.wrongEnumArgument")
                        .replace("{0}", args[1])
                        .replace("{1}", StringUtils.join(Ticket.TicketStatus.values(), ", "))
                );
                return;
            }
            statuses = new Ticket.TicketStatus[]{messageStatus};
        }

        //load the tickets
        List<Ticket> tickets;
        if (sender.hasPermission("SupportTickets.mod")) {
            tickets = SupportTickets.getDatabaseController().getTickets(statuses);
        } else {
            UUID senderId = sender instanceof ProxiedPlayer ? ((ProxiedPlayer) sender).getUniqueId() : new UUID(0, 0);
            tickets = SupportTickets.getDatabaseController().getPlayerTickets(senderId, statuses);
        }

        if (statuses.length == 1) {
            tickets.sort(Comparator.reverseOrder());
        }

        //display the results
        int pageSize = plugin.getConfig().getPageSize();
        int maxPages = (int) Math.ceil((float) tickets.size() / pageSize);
        if (maxPages > 0 && page >= maxPages - 1) {
            page = maxPages - 1;
        }

        SupportTickets.sendMessage(sender, plugin.getConfig().getText("info.list.header")
                .replace("{0}", String.valueOf(page + 1))
                .replace("{1}", String.valueOf(maxPages))
                .replace("{2}", messageStatus.toString())
        );

        for (int i = page * pageSize; i < (page + 1) * pageSize && i < tickets.size(); i++) {
            Ticket ticket = tickets.get(i);
            SupportTickets.sendMessage(sender, plugin.getConfig().getText("info.list.element")
                    .replace("{0}", String.valueOf(ticket.getTicketId()))
                    .replace("{1}", plugin.isPlayerOnline(ticket.getSender())
                            ? plugin.getConfig().getText("info.list.online")
                            : plugin.getConfig().getText("info.list.offline"))
                    .replace("{2}", plugin.getNameByUUID(ticket.getSender()))
                    .replace("{3}", ticket.getAssigned() != null ? ticket.getAssigned() : "*")
                    .replace("{4}", ticket.getMessage())
                    .replace("{5}", Integer.toString(ticket.getComments().size())));
            plugin.addShownTicket(sender, ticket.getTicketId());
        }

        if (tickets.size() > pageSize) {
            String usage = getUsage().replace("[<#page>]]", "<#>");
            if (messageStatus == Ticket.TicketStatus.OPEN) {
                usage = usage.replace("[[<status>] ", "");
            } else {
                usage = usage.replace("[[<status>]", messageStatus.toString().toLowerCase());
            }
            SupportTickets.sendMessage(sender, plugin.getConfig().getText("info.list.footer")
                    .replace("{0}", "/ticket " + getName() + " " + usage));
        }
    }

    @Override
    public boolean validateInput(String[] strings) {
        return strings.length > 0;
    }
}

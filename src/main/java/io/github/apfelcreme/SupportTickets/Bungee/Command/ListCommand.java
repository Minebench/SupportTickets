package io.github.apfelcreme.SupportTickets.Bungee.Command;

import io.github.apfelcreme.SupportTickets.Bungee.SupportTickets;
import io.github.apfelcreme.SupportTickets.Bungee.Ticket.Ticket;
import net.md_5.bungee.api.CommandSender;

import java.util.List;

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
            page = Integer.parseInt(args[1]) - 1;
        }
        if (page < 0) {
            page = 0;
        }

        Ticket.TicketStatus[] statuses = new Ticket.TicketStatus[]{
                Ticket.TicketStatus.OPEN,
                Ticket.TicketStatus.ASSIGNED,
                Ticket.TicketStatus.REOPENED
        };
        if (args.length > 1 && !SupportTickets.isNumeric(args[1])) {
            statuses = new Ticket.TicketStatus[]{Ticket.TicketStatus.valueOf(args[1].toUpperCase())};
        }

        //load the tickets
        List<Ticket> tickets = SupportTickets.getDatabaseController().getTickets(statuses);

        //display the results
        int pageSize = plugin.getConfig().getPageSize();
        int maxPages = (int) Math.ceil((float) tickets.size() / pageSize);
        if (page >= maxPages - 1) {
            page = maxPages - 1;
        }

        SupportTickets.sendMessage(sender, plugin.getConfig().getText("info.list.header")
                .replace("{0}", String.valueOf(page + 1))
                .replace("{1}", String.valueOf(maxPages)));

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

        SupportTickets.sendMessage(sender, plugin.getConfig().getText("info.list.footer"));
    }

    @Override
    public boolean validateInput(String[] strings) {
        if (strings.length > 1 && !SupportTickets.isNumeric(strings[1])) {
            try {
                Ticket.TicketStatus.valueOf(strings[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        return strings.length > 0;
    }
}

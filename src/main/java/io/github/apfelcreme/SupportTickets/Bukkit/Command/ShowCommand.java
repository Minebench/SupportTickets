package io.github.apfelcreme.SupportTickets.Bukkit.Command;

import io.github.apfelcreme.SupportTickets.Bukkit.SupportTickets;
import io.github.apfelcreme.SupportTickets.Bukkit.SupportTicketsConfig;
import io.github.apfelcreme.SupportTickets.Bukkit.Ticket.Ticket;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
public class ShowCommand implements SubCommand {

    /**
     * executes a subcommand
     *
     * @param sender the sender
     * @param args   the string arguments in an array
     */
    public void execute(CommandSender sender, String[] args) {
        final Player player = (Player) sender;
        if (player.hasPermission("SupportTickets.show")) {
            int page = 0;
            if (args.length > 1 && SupportTickets.isNumeric(args[1])) {
                page = Integer.parseInt(args[1]) - 1;
            }
            List<Ticket> tickets = SupportTickets.getDatabaseController().getPlayerTickets(player.getUniqueId(),
                    Ticket.TicketStatus.OPEN, Ticket.TicketStatus.ASSIGNED, Ticket.TicketStatus.ASSIGNED);

            //display the results
            Integer pageSize = SupportTicketsConfig.getPageSize();
            Integer maxPages = (int) Math.ceil((float) tickets.size() / pageSize);
            if (page >= maxPages - 1) {
                page = maxPages - 1;
            }

            SupportTickets.sendMessage(player, SupportTicketsConfig.getText("info.show.header")
                    .replace("{0}", Integer.toString(page + 1))
                    .replace("{1}", maxPages.toString()));
            for (int i = page * pageSize; i < (page * pageSize) + pageSize; i++) {
                if (i < tickets.size() && tickets.size() > 0) {
                    SupportTickets.sendMessage(player, SupportTicketsConfig.getText("info.list.element")
                            .replace("{0}", tickets.get(i).getTicketId().toString())
                            .replace("{1}", SupportTickets.getNameByUUID(tickets.get(i).getSender()))
                            .replace("{2}", tickets.get(i).getAssigned() != null ? tickets.get(i).getAssigned() + ": " : "")
                            .replace("{3}", tickets.get(i).getMessage())
                            .replace("{4}", Integer.toString(tickets.get(i).getComments().size())));
                }
            }
            SupportTickets.sendMessage(player, SupportTicketsConfig.getText("info.show.footer"));
        } else {
            SupportTickets.sendMessage(sender, SupportTicketsConfig.getText("error.noPermission"));
        }
    }
}

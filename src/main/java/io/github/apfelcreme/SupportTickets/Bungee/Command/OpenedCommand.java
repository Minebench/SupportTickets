package io.github.apfelcreme.SupportTickets.Bungee.Command;

import io.github.apfelcreme.SupportTickets.Bungee.SupportTickets;
import io.github.apfelcreme.SupportTickets.Bungee.SupportTicketsConfig;
import io.github.apfelcreme.SupportTickets.Bungee.Ticket.Ticket;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

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
public class OpenedCommand implements SubCommand {

    /**
     * executes a subcommand
     *
     * @param sender the sender
     * @param args   the string arguments in an array
     */
    @Override
    public void execute(CommandSender sender, String[] args) {
        final ProxiedPlayer player = (ProxiedPlayer) sender;
        if (player.hasPermission("SupportTickets.mod")) {
            if (args.length > 1) {
                UUID target = SupportTickets.getInstance().getUUIDByName(args[1]);
                if (target != null) {
                    int page = 0;
                    if ((args.length > 2) && SupportTickets.isNumeric(args[2])) {
                        page = Integer.parseInt(args[2]) - 1;
                    }
                    List<Ticket> tickets = SupportTickets.getDatabaseController().getTicketsOpenedBy(target);

                    //display the results
                    Integer pageSize = SupportTicketsConfig.getInstance().getPageSize();
                    Integer maxPages = (int) Math.ceil((float) tickets.size() / pageSize);
                    if (page >= maxPages - 1) {
                        page = maxPages - 1;
                    }

                    SupportTickets.sendMessage(player, SupportTicketsConfig.getInstance().getText("info.opened.header")
                            .replace("{0}", args[1])
                            .replace("{1}", Integer.toString(page + 1))
                            .replace("{2}", maxPages.toString())
                            .replace("{3}", Integer.toString(tickets.size())));
                    for (int i = page * pageSize; i < (page * pageSize) + pageSize; i++) {
                        if (i < tickets.size() && tickets.size() > 0) {
                            SupportTickets.sendMessage(player, SupportTicketsConfig.getInstance().getText("info.list.element")
                                    .replace("{0}", tickets.get(i).getTicketId().toString())
                                    .replace("{1}", SupportTickets.getInstance().isPlayerOnline(tickets.get(i).getSender())
                                            ? SupportTicketsConfig.getInstance().getText("info.list.online")
                                            : SupportTicketsConfig.getInstance().getText("info.list.offline"))
                                    .replace("{2}", SupportTickets.getInstance().getNameByUUID(tickets.get(i).getSender()))
                                    .replace("{3}", tickets.get(i).getAssigned() != null ? tickets.get(i).getAssigned() + ": " : "")
                                    .replace("{4}", tickets.get(i).getMessage())
                                    .replace("{5}", Integer.toString(tickets.get(i).getComments().size())));
                        }
                    }
                    SupportTickets.sendMessage(player, SupportTicketsConfig.getInstance().getText("info.opened.footer"));
                } else {
                    SupportTickets.sendMessage(sender, SupportTicketsConfig.getInstance().getText("error.unknownPlayer"));
                }
            } else {
                SupportTickets.sendMessage(sender, SupportTicketsConfig.getInstance().getText("error.wrongUsage")
                        .replace("{0}", "/pe opened <Spieler>"));
            }
        } else {
            SupportTickets.sendMessage(sender, SupportTicketsConfig.getInstance().getText("error.noPermission"));
        }
    }
}

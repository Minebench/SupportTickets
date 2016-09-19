package io.github.apfelcreme.SupportTickets.Bukkit.Command;

import io.github.apfelcreme.SupportTickets.Bukkit.Bungee.BungeeMessenger;
import io.github.apfelcreme.SupportTickets.Bukkit.SupportTickets;
import io.github.apfelcreme.SupportTickets.Bukkit.SupportTicketsConfig;
import io.github.apfelcreme.SupportTickets.Bukkit.Ticket.Comment;
import io.github.apfelcreme.SupportTickets.Bukkit.Ticket.Ticket;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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
public class InfoCommand implements SubCommand{

    /**
     * executes a subcommand
     *
     * @param sender the sender
     * @param args   the string arguments in an array
     */
    @Override
    public void execute(CommandSender sender, String[] args) {

        final Player player = (Player) sender;
        if (player.hasPermission("SupportTickets.info")) {
            if (args.length > 1) {
                if (SupportTickets.isNumeric(args[1])) {
                    Ticket ticket = SupportTickets.getDatabaseController().loadTicket(Integer.parseInt(args[1]));
                    if (ticket != null) {
                        SupportTickets.sendMessage(player, SupportTicketsConfig.getText("info.info.info")
                                .replace("{0}", ticket.getTicketId().toString())
                                .replace("{1}", SupportTickets.getInstance().getNameByUUID(ticket.getSender())));
                        SupportTickets.sendMessage(player, SupportTicketsConfig.getText("info.info.date")
                                .replace("{0}", new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(ticket.getDate())));
                        SupportTickets.sendMessage(player, SupportTicketsConfig.getText("info.info.comments")
                                .replace("{0}", Integer.toString(ticket.getComments().size()))
                                .replace("{1}", ticket.getTicketId().toString()));
                        SupportTickets.sendMessage(player, SupportTicketsConfig.getText("info.info.location")
                                .replace("{0}", ticket.getLocation().getServer())
                                .replace("{1}", new DecimalFormat("0").format(ticket.getLocation().getLocationX()))
                                .replace("{2}", new DecimalFormat("0").format(ticket.getLocation().getLocationY()))
                                .replace("{3}", new DecimalFormat("0").format(ticket.getLocation().getLocationZ()))
                                .replace("{4}", ticket.getLocation().getWorldName()));
                    } else {
                        SupportTickets.sendMessage(player, SupportTicketsConfig.getText("error.unknownTicket"));
                    }
                } else {
                    SupportTickets.sendMessage(player, SupportTicketsConfig.getText("error.wrongUsage")
                            .replace("{0}", "/pe info <#>"));
                }
            } else {
                SupportTickets.sendMessage(player, SupportTicketsConfig.getText("error.wrongUsage")
                        .replace("{0}", "/pe info <#>"));
            }
        } else {
            SupportTickets.sendMessage(sender, SupportTicketsConfig.getText("error.noPermission"));
        }
    }
}

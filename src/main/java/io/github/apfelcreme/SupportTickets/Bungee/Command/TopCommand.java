package io.github.apfelcreme.SupportTickets.Bungee.Command;

import io.github.apfelcreme.SupportTickets.Bungee.SupportTickets;
import io.github.apfelcreme.SupportTickets.Bungee.SupportTicketsConfig;
import io.github.apfelcreme.SupportTickets.Bungee.Ticket.Ticket;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.*;

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
public class TopCommand implements SubCommand {

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
            List<Ticket> tickets = SupportTickets.getDatabaseController().getTickets(Ticket.TicketStatus.CLOSED);
            Map<UUID, Integer> playerCloses = new HashMap<>();
            for (Ticket ticket : tickets) {
                if (!playerCloses.containsKey(ticket.getClosed())) {
                    playerCloses.put(ticket.getClosed(), 0);
                }
                playerCloses.put(ticket.getClosed(), playerCloses.get(ticket.getClosed()) + 1);
            }
            ValueComparator comparator = new ValueComparator(playerCloses);
            TreeMap<UUID, Integer> sortedMap = new TreeMap<>(comparator);
            sortedMap.putAll(playerCloses);
            SupportTickets.sendMessage(sender, SupportTicketsConfig.getInstance().getText("info.top.header")
                    .replace("{0}", SupportTicketsConfig.getInstance().getTopListSize().toString()));
            int i = 0;
            for (Map.Entry<UUID, Integer> entry : sortedMap.entrySet()) {
                i++;
                if (i <= SupportTicketsConfig.getInstance().getTopListSize()) {
                    SupportTickets.sendMessage(sender, SupportTicketsConfig.getInstance().getText("info.top.element")
                            .replace("{0}", Integer.toString(i))
                            .replace("{1}", SupportTickets.getInstance().getNameByUUID(entry.getKey()))
                            .replace("{2}", entry.getValue().toString()));
                }
            }
        } else {
            SupportTickets.sendMessage(sender, SupportTicketsConfig.getInstance().getText("error.noPermission"));
        }
    }

    /**
     * a simple comparator to sort the top list
     */
    class ValueComparator implements Comparator<UUID> {
        Map<UUID, Integer> base;

        public ValueComparator(Map<UUID, Integer> base) {
            this.base = base;
        }

        public int compare(UUID a, UUID b) {
            if (base.get(a) >= base.get(b)) {
                return -1;
            } else {
                return 1;
            }
        }
    }
}

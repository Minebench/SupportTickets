package io.github.apfelcreme.SupportTickets.Bungee;

import io.github.apfelcreme.SupportTickets.Bungee.Command.*;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class CommandExecutor extends Command implements Listener {

    private final SupportTickets plugin;

    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public CommandExecutor(SupportTickets plugin, String name, String permission, String... aliases) {
        super(name, permission, aliases);
        this.plugin = plugin;
    }

    @Override
    public void execute(final CommandSender commandSender, final String[] strings) {
        SubCommand subCommand;
        if (strings.length > 0) {
            subCommand = getSubCommand(strings[0]);
        } else {
            subCommand = getSubCommand("help");
        }

        if (subCommand == null) {
            SupportTickets.sendMessage(commandSender, plugin.getConfig().getText("error.unknownCommand")
                    .replace("{0}", strings[0]));
            return;
        }

        if (!subCommand.checkPermission(commandSender)) {
            SupportTickets.sendMessage(commandSender, plugin.getConfig().getText("error.noPermission"));
            return;
        }

        if (subCommand.getUsage() != null) {
            String[] usage = subCommand.getUsage().split(" ");
            boolean failed = false;

            int required = 0;

            if (usage.length > 0 && !usage[0].isEmpty()) {
                for (int i = 0; i < usage.length; i++) {
                    if (!usage[i].startsWith("[") && !usage[i].endsWith("]")) {
                        required++;
                    }
                    if (strings.length > i + 2) {
                        if (usage[i].contains("#")) {
                            failed = !failed && !SupportTickets.isNumeric(strings[i + 1]);
                        }
                    }
                }
            }

            if (failed || strings.length - 1 < required) {
                SupportTickets.sendMessage(commandSender, plugin.getConfig().getText("error.wrongUsage")
                        .replace("{0}", "/" + getName() + " " + subCommand.getName() + " " + subCommand.getUsage()));

            }
        }

        final SubCommand finalSubCommand = subCommand;

        // execute the subcommand in a thread
        plugin.getProxy().getScheduler().runAsync(plugin, new Runnable() {
            public void run() {
                finalSubCommand.execute(commandSender, strings);
            }
        });
    }

    public Map<String, SubCommand> getSubCommands() {
        return subCommands;
    }

    public void registerSubCommand(SubCommand subCommand) {
        subCommands.put(subCommand.getName().toLowerCase(), subCommand);
        for (String alias : subCommand.getAliases()) {
            subCommands.put(alias.toLowerCase(), subCommand);
        }
    }

    public void unregisterSubCommand(String name) {
        SubCommand subCommand = getSubCommand(name);
        if (subCommand != null) {
            subCommands.remove(subCommand.getName().toLowerCase());
            for (String alias : subCommand.getAliases()) {
                subCommands.remove(alias.toLowerCase());
            }
        }
    }

    private SubCommand getSubCommand(String name) {
        return subCommands.get(name.toLowerCase());
    }

    @EventHandler
    public void onTabComplete(TabCompleteEvent event) {
        String cursor = event.getCursor().toLowerCase();
        if (!cursor.startsWith("/")) {
            return;
        }

        if (("/" + getName()).startsWith(cursor)) {
            event.getSuggestions().add(getName());
            return;
        }

        if (!cursor.startsWith("/" + getName())) {
            return;
        }

        if (!(event.getSender() instanceof CommandSender)) {
            return;
        }

        String[] parts = cursor.split(" ");

        if (parts.length == 1 && cursor.endsWith(" ")) {
            for (SubCommand sub : getSubCommands().values()) {
                if (sub.checkPermission((CommandSender) event.getSender())) {
                    event.getSuggestions().add(sub.getName());
                }
            }
        } else if (parts.length == 2) {
            if (!cursor.endsWith(" ")) {
                for (SubCommand sub : getSubCommands().values()) {
                    if (sub.getName().startsWith(parts[1]) && sub.checkPermission((CommandSender) event.getSender())) {
                        event.getSuggestions().add(sub.getName());
                    }
                }
            } else {
                SubCommand subCommand = getSubCommand(parts[1]);
                if (subCommand != null && subCommand.getUsage() != null) {
                    String[] args = subCommand.getUsage().split(" ");
                    if (args.length > 0) {
                        if (args[0].contains("player")) {
                            for (ProxiedPlayer player : plugin.getProxy().getPlayers()) {
                                event.getSuggestions().add(player.getName());
                            }
                        } else if (args[0].contains("<#>")) {
                            for (Integer ticketId : plugin.getLastShownTickets((CommandSender) event.getSender())) {
                                event.getSuggestions().add(ticketId.toString());
                            }
                        }
                    }
                }
            }
        } else if (parts.length > 2) {
            SubCommand subCommand = getSubCommand(parts[1]);
            if (subCommand != null && subCommand.getUsage() != null) {
                String[] args = subCommand.getUsage().split(" ");
                if (args.length > 0) {
                    int i = parts.length - 3;

                    if (args[i].contains("player")) {
                        for (ProxiedPlayer player : plugin.getProxy().getPlayers()) {
                            if (cursor.endsWith(" ") || player.getName().startsWith(parts[parts.length - 1])) {
                                event.getSuggestions().add(player.getName());
                            }
                        }
                    } else if (args[i].contains("<#>")) {
                        for (Integer ticketId : plugin.getLastShownTickets((CommandSender) event.getSender())) {
                            if (cursor.endsWith(" ") || ticketId.toString().startsWith(parts[parts.length - 1])) {
                                event.getSuggestions().add(ticketId.toString());
                            }
                        }
                    }
                }
            }
        }
    }
}

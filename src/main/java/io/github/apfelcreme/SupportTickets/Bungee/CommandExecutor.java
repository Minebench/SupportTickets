package io.github.apfelcreme.SupportTickets.Bungee;

import io.github.apfelcreme.SupportTickets.Bungee.Command.*;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.LinkedHashMap;
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

    private final Map<String, SubCommand> subCommands = new LinkedHashMap<>();

    public CommandExecutor(SupportTickets plugin, String name, String permission, String... aliases) {
        super(name, permission, aliases);
        this.plugin = plugin;
        plugin.getProxy().getPluginManager().registerListener(plugin, this);
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
            plugin.sendMessage(commandSender, "error.unknownCommand", "input", strings[0]);
            return;
        }

        if (!subCommand.checkPermission(commandSender)) {
            plugin.sendMessage(commandSender, "error.noPermission");
            return;
        }

        if (!subCommand.validateInput(strings)) {
            plugin.sendMessage(commandSender, "error.wrongUsage",  "usage", "/" + getName() + " " + subCommand.getName() + " " + subCommand.getUsage());
            return;
        }

        final SubCommand finalSubCommand = subCommand;

        // execute the subcommand in a thread
        plugin.getProxy().getScheduler().runAsync(plugin, () -> finalSubCommand.execute(commandSender, strings));
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
        SubCommand subCommand = subCommands.get(name.toLowerCase());
        if (subCommand == null) {
            for (SubCommand sub : subCommands.values()) {
                if (sub.getName().startsWith(name.toLowerCase())) {
                    subCommand = sub;
                }
            }
        }
        if (subCommand == null) {
            for (Map.Entry<String, SubCommand> entry : subCommands.entrySet()) {
                if (entry.getKey().startsWith(name.toLowerCase())) {
                    subCommand = entry.getValue();
                }
            }
        }
        return subCommand;
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
                    if (i < args.length) {
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
}

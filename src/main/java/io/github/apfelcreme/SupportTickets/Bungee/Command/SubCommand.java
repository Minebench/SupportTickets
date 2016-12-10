package io.github.apfelcreme.SupportTickets.Bungee.Command;


import io.github.apfelcreme.SupportTickets.Bungee.SupportTickets;
import net.md_5.bungee.api.CommandSender;

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
public abstract class SubCommand {

    protected final SupportTickets plugin;
    private final String name;
    private final String usage;
    private final String permission;
    private final String[] aliases;

    public SubCommand(SupportTickets plugin, String name) {
        this(plugin, name, null, null);
    }

    public SubCommand(SupportTickets plugin, String name, String usage, String permission, String... aliases) {
        this.plugin = plugin;
        this.name = name;
        this.usage = usage;
        this.permission = permission;
        this.aliases = aliases;
    }

    /**
     * executes a subcommand
     * @param sender the sender
     * @param args the string arguments in an array
     * @return <tt>true</tt> if the sub command executed properly, <tt>false</tt> if not and a usage message should be send
     */
    public abstract void execute(CommandSender sender, String[] args);

    /**
     * Check whether or not a sender has a permission
     * @param sender The sender to check
     * @return <tt>true</tt> if the sender has the permission or that sub command doesn't need a permission
     */
    public boolean checkPermission(CommandSender sender) {
        return getPermission() == null || sender.hasPermission(getPermission());
    }

    public String getName() {
        return name;
    }

    public String getUsage() {
        return usage;
    }

    public String getPermission() {
        return permission;
    }

    public String[] getAliases() {
        return aliases;
    }

    /**
     * Check whether or not the input is valid. Checks both the argument count and
     * if the input is a number if needed. Uses the usage text as the instructions.
     * @param strings The input strings
     * @return <tt>true</tt> if it is valid; <tt>false</tt> if not
     */
    public boolean validateInput(String[] strings) {
        if (getUsage() != null) {
            String[] usage = getUsage().split(" ");
            boolean failed = false;

            int required = 0;

            if (usage.length > 0 && !usage[0].replace(" ", "").isEmpty()) {
                for (int i = 0; i < usage.length; i++) {
                    if (!usage[i].startsWith("[") && !usage[i].endsWith("]")) {
                        required++;
                    }
                    if (strings.length > i + 1) {
                        if (usage[i].contains("#")) {
                            failed = !failed && !SupportTickets.isNumeric(strings[i + 1]);
                        }
                    }
                }
            }

            return !(failed || strings.length - 1 < required);
        }
        return true;
    }
}

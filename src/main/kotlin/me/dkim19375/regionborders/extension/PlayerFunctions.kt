/*
 *     RegionBorders, A spigot plugin that creates borders that can run actions
 *     Copyright (C) 2021  dkim19375
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.dkim19375.regionborders.extension

import me.dkim19375.dkimbukkitcore.data.HelpMessage
import me.dkim19375.dkimbukkitcore.function.getMaxHelpPages
import me.dkim19375.dkimbukkitcore.function.showHelpMessage
import me.dkim19375.regionborders.RegionBorders
import me.dkim19375.regionborders.data.ActionInfo
import me.dkim19375.regionborders.enumclass.ActionType
import me.dkim19375.regionborders.enumclass.ErrorType
import me.dkim19375.regionborders.enumclass.Permissions
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.kyori.adventure.title.Title
import net.kyori.adventure.util.Ticks
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.permissions.Permissible
import org.bukkit.plugin.java.JavaPlugin

private val plugin: RegionBorders by lazy { JavaPlugin.getPlugin(RegionBorders::class.java) }

private val commands = listOf(
    HelpMessage("help [page]", "Send help message", Permissions.COMMAND.perm),
    HelpMessage("reload", "Reload the plugin's config files", Permissions.RELOAD.perm),
    HelpMessage(
        arg = "create <region name> <pos1|pos2>",
        description = "Create a region, run the command twice for each position (opposite corners)",
        permission = Permissions.EDIT.perm
    ),
    HelpMessage("list", "List the regions", permission = Permissions.EDIT.perm),
    HelpMessage("info <region name>", "See information about a region", Permissions.EDIT.perm),
    HelpMessage("delete <region name>", "Delete a region", permission = Permissions.DELETE.perm),
    HelpMessage("actions <region name> remove <id>", "Remove an action", permission = Permissions.EDIT.perm),
    HelpMessage("actions <region name> add <enter|leave> <action usage>",
        "Add an action",
        permission = Permissions.EDIT.perm),
    HelpMessage("listactions [page]", "List all actions and their usage", permission = Permissions.EDIT.perm)
)

private val actions = listOf(
    ActionInfo("prevent", "Prevent a player from entering/leaving", ActionType.PREVENT),
    ActionInfo("message <message>", "Send a message to the player", ActionType.SEND_MESSAGE),
    ActionInfo("placeholder <placeholder>",
        "Execute a placeholder (can be used to run actions such as JavaScript scripts)", ActionType.PLACEHOLDER),
    ActionInfo("title [duration (ticks)] <title%newline%subtitle>",
        "Send a title, duration and subtitles are optional", ActionType.TITLE),
    ActionInfo("actionbar <actionbar text>", "Send an actionbar text", ActionType.ACTION_BAR),
    ActionInfo("consolecmd <command>", "Run a console command", ActionType.CONSOLE_CMD),
    ActionInfo("playermsg <message>", "Send a message/command as a player", ActionType.PLAYER_MSG),
    ActionInfo("launch <face (North, East, NorthEast, Up, etc)> [multiplier]",
        "Launch a player towards a direction, multiplier default: 1", ActionType.LAUNCH_PLAYER),
    ActionInfo("sound <sound> [volume] [pitch]",
        "Play a sound, default of volume and pitch: 100, supports decimals",
        ActionType.PLAY_SOUND),
)

fun Permissible.getMaxHelpPages() = getMaxHelpPages(commands)

fun getActions() = actions

fun Permissible.hasPermission(permission: Permissions): Boolean = hasPermission(permission.perm)

fun CommandSender.sendMessage(error: ErrorType) = sendMessage("${ChatColor.RED}${error.description}")

fun CommandSender.sendHelpMessage(label: String, error: ErrorType? = null, page: Int = 1) =
    showHelpMessage(label, error?.description, page, commands, plugin)

fun Player.sendOtherTitle(
    title: String? = null,
    subtitle: String? = null,
    fadeIn: Int = 10,
    stay: Int = 50,
    fadeOut: Int = 10,
) = sendTitle(title, subtitle, fadeIn, stay, fadeOut)

fun Player.sendTitle(
    title: String? = null,
    subtitle: String? = null,
    fadeIn: Int = 10,
    stay: Int = 70,
    fadeOut: Int = 10,
) = BukkitAudiences.create(plugin).player(this).showTitle(
    Title.title(
        LegacyComponentSerializer.legacySection().deserialize(title ?: ""),
        LegacyComponentSerializer.legacySection().deserialize(subtitle ?: ""),
        Title.Times.of(Ticks.duration(fadeIn.toLong()), Ticks.duration(stay.toLong()), Ticks.duration(fadeOut.toLong()))
    )
)

fun Player.sendActionBar(
    text: String? = null,
) = BukkitAudiences.create(plugin).player(this).sendActionBar(
    LegacyComponentSerializer.legacySection().deserialize(text ?: "")
)
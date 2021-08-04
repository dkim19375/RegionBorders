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

package me.dkim19375.regionborders.command

import me.dkim19375.regionborders.RegionBorders
import me.dkim19375.regionborders.data.ActionInfo
import me.dkim19375.regionborders.data.RegionData
import me.dkim19375.regionborders.enumclass.ActionType
import me.dkim19375.regionborders.enumclass.Permissions
import me.dkim19375.regionborders.extension.getActions
import me.dkim19375.regionborders.extension.getMaxHelpPages
import me.dkim19375.regionborders.extension.getMaxPages
import me.dkim19375.regionborders.extension.hasPermission
import org.bukkit.Sound
import org.bukkit.block.BlockFace
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.permissions.Permissible
import org.bukkit.util.StringUtil
import kotlin.math.min

class RegionBordersTab(private val plugin: RegionBorders) : TabCompleter {

    private fun getPartial(token: String, collection: Iterable<String>): List<String> =
        StringUtil.copyPartialMatches(token, collection, mutableListOf())

    private fun getBaseCommands(sender: CommandSender): List<String> {
        val list = mutableListOf("help")
        if (sender.hasPermission(Permissions.RELOAD)) {
            list.add("reload")
        }
        if (sender.hasPermission(Permissions.DELETE)) {
            list.add("delete")
        }
        if (!sender.hasPermission(Permissions.EDIT)) {
            return list
        }
        list.addAll(listOf(
            "list",
            "info",
            "actions",
            "listactions",
        ))
        return list
    }

    private fun getPartialPerm(
        token: String,
        collection: Iterable<String>,
        sender: Permissible,
        perm: Permissions = Permissions.EDIT,
    ): List<String>? {
        if (!sender.hasPermission(perm)) {
            return null
        }
        return getPartial(token, collection)
    }

    private fun getRegions(): List<String> = plugin.regionManager.regions.map(RegionData::name)

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>,
    ): List<String>? {
        if (!sender.hasPermission(Permissions.COMMAND)) {
            return null
        }
        return when (args.size) {
            0 -> getBaseCommands(sender)
            1 -> getPartial(args[0], getBaseCommands(sender))
            2 -> {
                return when (args[0].lowercase()) {
                    "help" -> getPartial(args[1], (1..sender.getMaxHelpPages()).map(Int::toString))
                    "create" -> getPartialPerm(args[1], plugin.regionManager.creatingRegions.keys, sender)
                    "info" -> getPartialPerm(args[1], getRegions(), sender)
                    "delete" -> getPartialPerm(args[1], getRegions(), sender, Permissions.DELETE)
                    "actions" -> getPartialPerm(args[1], getRegions(), sender)
                    "listactions" -> getPartialPerm(args[1], (1..getActions().getMaxPages()).map(Int::toString), sender)
                    else -> emptyList()
                }
            }
            3 -> {
                return when (args[0].lowercase()) {
                    "create" -> getPartialPerm(args[2], listOf("pos1", "pos2"), sender)
                    "actions" -> getPartialPerm(args[2], listOf("remove", "add"), sender)
                    else -> emptyList()
                }
            }
            4 -> {
                if (!sender.hasPermission(Permissions.EDIT)) {
                    return emptyList()
                }
                if (!args[0].equals("actions", true)) {
                    return emptyList()
                }
                if (args[2].equals("remove", true)) {
                    val region =
                        plugin.regionManager.regions.firstOrNull { it.name.equals(args[1], true) } ?: return emptyList()
                    return getPartial(args[3], (min(1, region.actions.size)..region.actions.size).map(Int::toString))
                }
                if (!args[2].equals("add", true)) {
                    return emptyList()
                }
                return getPartial(args[3], listOf("enter", "leave"))
            }
            5 -> {
                if (!checkAction(args, sender)) {
                    return emptyList()
                }
                return getPartial(args[4], getActions().map(ActionInfo::usage).map { it.split(' ')[0] })
            }
            6 -> {
                if (!checkAction(args, sender)) {
                    return emptyList()
                }
                val action = getAction(args, sender) ?: return emptyList()
                return when (action.type) {
                    ActionType.PREVENT -> emptyList()
                    ActionType.SEND_MESSAGE -> listOf("<message>")
                    ActionType.PLACEHOLDER -> listOf("<placeholder>")
                    ActionType.TITLE -> listOf("[duration (ticks)] OR <title%newline%subtitle>")
                    ActionType.ACTION_BAR -> listOf("<actionbar text>")
                    ActionType.CONSOLE_CMD -> listOf("<command>")
                    ActionType.PLAYER_MSG -> listOf("<message or command>")
                    ActionType.LAUNCH_PLAYER -> getPartial(args[5], BlockFace.values().map(BlockFace::name))
                    ActionType.PLAY_SOUND -> getPartial(args[5], Sound.values().map(Sound::name))
                }
            }
            7 -> {
                if (!checkAction(args, sender)) {
                    return emptyList()
                }
                val action = getAction(args, sender) ?: return emptyList()
                return when (action.type) {
                    ActionType.PREVENT -> emptyList()
                    ActionType.SEND_MESSAGE -> emptyList()
                    ActionType.PLACEHOLDER -> emptyList()
                    ActionType.TITLE -> listOf("<title%newline%subtitle>")
                    ActionType.ACTION_BAR -> listOf("<actionbar text>")
                    ActionType.CONSOLE_CMD -> listOf("<command>")
                    ActionType.PLAYER_MSG -> listOf("<message or command>")
                    ActionType.LAUNCH_PLAYER -> listOf("[multiplier]")
                    ActionType.PLAY_SOUND -> listOf("[volume]")
                }
            }
            8 -> {
                if (!checkAction(args, sender)) {
                    return emptyList()
                }
                val action = getAction(args, sender) ?: return emptyList()
                return when (action.type) {
                    ActionType.PREVENT -> emptyList()
                    ActionType.SEND_MESSAGE -> emptyList()
                    ActionType.PLACEHOLDER -> emptyList()
                    ActionType.TITLE -> listOf("<title%newline%subtitle>")
                    ActionType.ACTION_BAR -> listOf("<actionbar text>")
                    ActionType.CONSOLE_CMD -> listOf("<command>")
                    ActionType.PLAYER_MSG -> listOf("<message or command>")
                    ActionType.LAUNCH_PLAYER -> emptyList()
                    ActionType.PLAY_SOUND -> listOf("[pitch]")
                }
            }
            else -> {
                if (!checkAction(args, sender)) {
                    return emptyList()
                }
                val action = getAction(args, sender) ?: return emptyList()
                return when (action.type) {
                    ActionType.PREVENT -> emptyList()
                    ActionType.SEND_MESSAGE -> emptyList()
                    ActionType.PLACEHOLDER -> emptyList()
                    ActionType.TITLE -> listOf("<title%newline%subtitle>")
                    ActionType.ACTION_BAR -> listOf("<actionbar text>")
                    ActionType.CONSOLE_CMD -> listOf("<command>")
                    ActionType.PLAYER_MSG -> listOf("<message or command>")
                    ActionType.LAUNCH_PLAYER -> emptyList()
                    ActionType.PLAY_SOUND -> emptyList()
                }
            }
        }
    }

    private fun checkAction(args: Array<out String>, sender: CommandSender): Boolean {
        return when {
            !sender.hasPermission(Permissions.EDIT) -> false
            !args[0].equals("actions", true) -> false
            !args[2].equals("add", true) -> false
            !listOf("enter", "leave").contains(args[3].lowercase()) -> false
            else -> true
        }
    }

    private fun getAction(args: Array<out String>, sender: CommandSender): ActionInfo? {
        if (!checkAction(args, sender)) {
            return null
        }
        val actions = getActions()
        val actionText = args[4].lowercase()
        return actions.firstOrNull { it.usage.split(' ')[0] == actionText }
    }
}
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

import me.dkim19375.dkimcore.extension.removeIf
import me.dkim19375.regionborders.RegionBorders
import me.dkim19375.regionborders.data.Boundary
import me.dkim19375.regionborders.data.RegionData
import me.dkim19375.regionborders.enumclass.ActionType
import me.dkim19375.regionborders.enumclass.ErrorType
import me.dkim19375.regionborders.enumclass.ExecutionType
import me.dkim19375.regionborders.enumclass.Permissions
import me.dkim19375.regionborders.extension.*
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import kotlin.system.measureTimeMillis

class RegionBordersCmd(private val plugin: RegionBorders) : CommandExecutor {
    private val creatingRegions: MutableMap<String, Location>
        get() = plugin.regionManager.creatingRegions

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>,
    ): Boolean {
        if (!sender.hasPermission(Permissions.COMMAND)) {
            sender.sendMessage(ErrorType.NO_PERMISSION)
            return true
        }
        if (args.isEmpty()) {
            sender.sendHelpMessage(label)
            return true
        }
        when (args[0].lowercase()) {
            "help" -> {
                sender.sendHelpMessage(label = label, page = args.getOrNull(1)?.toIntOrNull() ?: 1)
                return true
            }
            "reload" -> {
                if (!sender.hasPermission(Permissions.RELOAD)) {
                    sender.sendMessage(ErrorType.NO_PERMISSION)
                    return true
                }
                sender.sendMessage("${ChatColor.GOLD}Reloading config and region files")
                sender.sendMessage("${ChatColor.GREEN}Successfully reloaded in ${measureTimeMillis(plugin::reloadConfig)}ms!")
                return true
            }
            "create" -> {
                if (!sender.hasPermission(Permissions.EDIT)) {
                    sender.sendMessage(ErrorType.NO_PERMISSION)
                    return true
                }
                if (sender !is Player) {
                    sender.sendMessage(ErrorType.MUST_BE_PLAYER)
                    return true
                }
                if (args.size < 3) {
                    sender.sendMessage(ErrorType.NOT_ENOUGH_ARGS)
                    return true
                }
                if (plugin.regionManager.regions.map(RegionData::name).any { args[1].equals(it, true) }) {
                    sender.sendMessage(ErrorType.REGION_EXISTS)
                    return true
                }
                if (args[2].equals("pos1", true)) {
                    creatingRegions.removeIf { k, _ -> k.equals(args[1], true) }
                    creatingRegions[args[1]] = sender.location.getWrapper().getLocation()
                    sender.sendMessage("${ChatColor.GREEN}Successfully set position 1!")
                    return true
                }
                if (!args[2].equals("pos2", true)) {
                    sender.sendMessage(ErrorType.INVALID_ARG)
                    return true
                }
                val firstLoc = creatingRegions.entries.firstOrNull { it.key.equals(args[1], true) }?.value
                if (firstLoc == null) {
                    sender.sendMessage(ErrorType.MUST_CREATE_FIRST_POS)
                    return true
                }
                val region = RegionData(Boundary(sender.world, firstLoc, sender.location.getWrapper().getLocation()),
                    args[1],
                    emptyList())
                plugin.regionManager.add(region)
                sender.sendMessage("${ChatColor.GREEN}Successfully created a region! (${args[1]})")
                creatingRegions.removeIf { k, _ -> k.equals(args[1], true) }
                return true
            }
            "list" -> {
                if (!sender.hasPermission(Permissions.EDIT)) {
                    sender.sendMessage(ErrorType.NO_PERMISSION)
                    return true
                }
                sender.sendMessage("${ChatColor.GREEN}Regions:")
                for (region in plugin.regionManager.regions) {
                    sender.sendMessage("${ChatColor.AQUA}${region.name}: ${ChatColor.GOLD}World: ${region.boundary.world.name}")
                }
                if (plugin.regionManager.regions.isEmpty()) {
                    sender.sendMessage("${ChatColor.AQUA}None")
                }
                return true
            }
            "info" -> {
                if (!sender.hasPermission(Permissions.EDIT)) {
                    sender.sendMessage(ErrorType.NO_PERMISSION)
                    return true
                }
                val region = getRegion(sender, args) ?: return true
                val locations = region.boundary.getLocations()
                val firstLoc = locations.first.getWrapper()
                val secondLoc = locations.second.getWrapper()
                sender.sendMessage("${ChatColor.GREEN}Region Info - ${region.name}")
                sender.sendMessage("${ChatColor.AQUA}Location 1: ${ChatColor.GOLD}${firstLoc.format()}")
                sender.sendMessage("${ChatColor.AQUA}Location 2: ${ChatColor.GOLD}${secondLoc.format()}")
                sender.sendMessage("${ChatColor.GREEN}Actions:")
                for ((i, action) in region.actions.withIndex()) {
                    sender.sendMessage("${ChatColor.GREEN}${i + 1}: ${action.type.displayName}")
                    sender.sendMessage("${ChatColor.AQUA}- ${ChatColor.GOLD}${action.format()}")
                }
                if (region.actions.isEmpty()) {
                    sender.sendMessage("${ChatColor.AQUA}None")
                }
                return true
            }
            "delete" -> {
                if (!sender.hasPermission(Permissions.DELETE)) {
                    sender.sendMessage(ErrorType.NO_PERMISSION)
                    return true
                }
                val region = getRegion(sender, args) ?: return true
                plugin.regionManager.delete(region)
                sender.sendMessage("${ChatColor.GREEN}Successfully deleted the region ${region.name}!")
                return true
            }
            "actions" -> {
                if (!sender.hasPermission(Permissions.EDIT)) {
                    sender.sendMessage(ErrorType.NO_PERMISSION)
                    return true
                }
                if (args.size < 4) {
                    sender.sendMessage(ErrorType.NOT_ENOUGH_ARGS)
                    return true
                }
                val region = getRegion(sender, args) ?: return true
                if (args[2].equals("remove", true)) {
                    val id = args[3].toIntOrNull()
                    if (id == null || region.actions.size < id) {
                        sender.sendMessage(ErrorType.INVALID_ACTION_ID)
                        return true
                    }
                    val newActions = region.actions.toMutableList()
                    newActions.removeAt(id - 1)
                    plugin.regionManager.delete(region, false)
                    plugin.regionManager.add(region.copy(actions = newActions))
                    sender.sendMessage("${ChatColor.GREEN}Successfully removed the action!")
                    return true
                }
                if (!args[2].equals("add", true)) {
                    sender.sendMessage(ErrorType.INVALID_ARG)
                    return true
                }
                if (args.size < 5) {
                    sender.sendMessage(ErrorType.NOT_ENOUGH_ARGS)
                    return true
                }
                val executionType = ExecutionType.fromString(args[3])
                if (executionType == null) {
                    sender.sendMessage(ErrorType.INVALID_EXECUTION_TYPE)
                    return true
                }
                val actionType = ActionType.fromString(args[4])
                if (actionType == null) {
                    sender.sendMessage(ErrorType.INVALID_ACTION_TYPE)
                    return true
                }
                val action = actionType.getInstance(sender, executionType, args) ?: return true
                plugin.regionManager.delete(region, false)
                plugin.regionManager.add(region.copy(actions = region.actions + action))
                sender.sendMessage("${ChatColor.GREEN}Successfully created an action!")
                return true
            }
            "listactions" -> {
                if (!sender.hasPermission(Permissions.EDIT)) {
                    sender.sendMessage(ErrorType.NO_PERMISSION)
                    return true
                }
                val page = args.getOrNull(1)?.toIntOrNull() ?: 1
                val maxPages = getActions().getMaxPages()
                sender.sendMessage("${ChatColor.GREEN}Actions - Page $page/$maxPages: (Use /$label actions <region name> add <enter|leave> <USAGE>)")
                getActions().getPage(page).forEach { info ->
                    sender.sendMessage("${ChatColor.AQUA}${info.description}")
                    sender.sendMessage("- ${ChatColor.GOLD}${info.usage}")
                }
                return true
            }
            else -> {
                sender.sendMessage(ErrorType.INVALID_ARG)
                return true
            }
        }
    }

    private fun getRegion(sender: CommandSender, args: Array<out String>): RegionData? {
        if (args.size < 2) {
            sender.sendMessage(ErrorType.NOT_ENOUGH_ARGS)
            return null
        }
        val region = plugin.regionManager.regions.firstOrNull { it.name.equals(args[1], true) }
        if (region == null) {
            sender.sendMessage(ErrorType.INVALID_REGION)
            return null
        }
        return region
    }
}
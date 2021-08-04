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

package me.dkim19375.regionborders.listener

import me.dkim19375.regionborders.RegionBorders
import me.dkim19375.regionborders.data.RegionData
import me.dkim19375.regionborders.enumclass.ExecutionType
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerTeleportEvent

class PlayerMoveListener(private val plugin: RegionBorders) : Listener {
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    private fun PlayerMoveEvent.onMove() {
        checkEnter()
        checkLeave()
    }

    private fun PlayerMoveEvent.checkEnter() {
        val regions = plugin.regionManager.regions
            .filter { it.boundary.isInBoundary(to) }
            .filterNot { it.boundary.isInBoundary(from) }
        execute(regions, ExecutionType.ENTER)
    }

    private fun PlayerMoveEvent.checkLeave() {
        val regions = plugin.regionManager.regions
            .filter { it.boundary.isInBoundary(from) }
            .filterNot { it.boundary.isInBoundary(to) }
        execute(regions, ExecutionType.LEAVE)
    }

    private fun PlayerMoveEvent.execute(regions: List<RegionData>, type: ExecutionType) {
        regions.forEach { regionData ->
            regionData.actions.filter { it.executionType == type }.forEach { action ->
                action.execute(this)
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    private fun PlayerTeleportEvent.onTeleport() {
        onMove()
    }
}
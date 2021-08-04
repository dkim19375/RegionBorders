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

package me.dkim19375.regionborders.action

import me.dkim19375.regionborders.enumclass.ActionType
import me.dkim19375.regionborders.enumclass.ExecutionType
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.event.player.PlayerMoveEvent

interface Action : ConfigurationSerializable {
    val type: ActionType
    val executionType: ExecutionType

    fun execute(event: PlayerMoveEvent)

    fun format(): String
}
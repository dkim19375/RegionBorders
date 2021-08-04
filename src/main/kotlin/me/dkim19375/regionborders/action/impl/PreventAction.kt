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

@file:Suppress("UNUSED_PARAMETER")

package me.dkim19375.regionborders.action.impl

import me.dkim19375.regionborders.action.Action
import me.dkim19375.regionborders.enumclass.ActionType
import me.dkim19375.regionborders.enumclass.ExecutionType
import org.bukkit.event.player.PlayerMoveEvent

class PreventAction(override val executionType: ExecutionType) : Action {
    override val type: ActionType = ActionType.PREVENT

    override fun execute(event: PlayerMoveEvent) {
        event.isCancelled = true
    }

    override fun format(): String = "Execution type: ${executionType.name.lowercase()}"

    override fun serialize(): Map<String, Any> = mapOf(
        "type" to executionType
    )

    companion object {
        @Suppress("unused")
        @JvmStatic
        fun deserialize(map: Map<String, Any>): PreventAction? {
            val type = map["type"] as? ExecutionType ?: return null
            return PreventAction(type)
        }
    }
}
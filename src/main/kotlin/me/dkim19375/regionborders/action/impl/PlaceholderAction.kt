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

package me.dkim19375.regionborders.action.impl

import me.clip.placeholderapi.PlaceholderAPI
import me.dkim19375.regionborders.action.Action
import me.dkim19375.regionborders.enumclass.ActionType
import me.dkim19375.regionborders.enumclass.ExecutionType
import org.bukkit.event.player.PlayerMoveEvent

class PlaceholderAction(private val message: String, override val executionType: ExecutionType) : Action {
    override val type: ActionType = ActionType.PLACEHOLDER

    override fun execute(event: PlayerMoveEvent) {
        runCatching {
            PlaceholderAPI.setPlaceholders(event.player, message)
        }
    }

    override fun format(): String = "Message: $message, execution type: ${executionType.name.lowercase()}"

    override fun serialize(): Map<String, Any> = mapOf(
        "message" to message,
        "type" to executionType
    )

    companion object {
        @Suppress("unused")
        @JvmStatic
        fun deserialize(map: Map<String, Any>): MessageAction? {
            val message = map["message"] as? String ?: return null
            val type = map["type"] as? ExecutionType ?: return null
            return MessageAction(message, type)
        }
    }
}
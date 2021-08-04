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

import me.dkim19375.dkimbukkitcore.function.formatAll
import me.dkim19375.regionborders.action.Action
import me.dkim19375.regionborders.enumclass.ActionType
import me.dkim19375.regionborders.enumclass.ExecutionType
import me.dkim19375.regionborders.extension.sendOtherTitle
import org.bukkit.event.player.PlayerMoveEvent

class TitleAction(private val duration: Int, private val message: String, override val executionType: ExecutionType) : Action {
    override val type: ActionType = ActionType.TITLE

    override fun execute(event: PlayerMoveEvent) {
        val split = message.split("%newline%")
        val title = split.getOrNull(0)?.formatAll(event.player)
        val subTitle = split.getOrNull(1)?.formatAll(event.player)
        event.player.sendOtherTitle(title, subTitle, 5, duration, 5)
    }

    override fun format(): String {
        val split = message.split("%newline%")
        val title = split.getOrNull(0)
        val subTitle = split.getOrNull(1)
        return "Title: $title, subtitle: $subTitle, execution type: ${executionType.name.lowercase()}"
    }

    override fun serialize(): Map<String, Any> = mapOf(
        "duration" to duration,
        "message" to message,
        "type" to executionType
    )

    companion object {
        @Suppress("unused")
        @JvmStatic
        fun deserialize(map: Map<String, Any>): TitleAction? {
            val duration = map["duration"] as? Int ?: return null
            val message = map["message"] as? String ?: return null
            val type = map["type"] as? ExecutionType ?: return null
            return TitleAction(duration, message, type)
        }
    }
}
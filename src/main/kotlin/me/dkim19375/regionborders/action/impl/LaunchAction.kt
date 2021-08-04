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

import me.dkim19375.dkimcore.extension.runCatchingOrNull
import me.dkim19375.regionborders.action.Action
import me.dkim19375.regionborders.enumclass.ActionType
import me.dkim19375.regionborders.enumclass.ExecutionType
import org.bukkit.block.BlockFace
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.util.Vector

class LaunchAction(
    private val face: BlockFace,
    private val multiplier: Double,
    override val executionType: ExecutionType,
) : Action {
    override val type: ActionType = ActionType.LAUNCH_PLAYER

    override fun execute(event: PlayerMoveEvent) {
        event.player.velocity = event.player.velocity.add(Vector(face.modX, face.modY, face.modZ).multiply(multiplier))
    }

    override fun format(): String =
        "Face: ${face.name}, multiplier: $multiplier, execution type: ${executionType.name.lowercase()}"

    override fun serialize(): Map<String, Any> = mapOf(
        "face" to face.name,
        "multiplier" to multiplier,
        "type" to executionType
    )

    companion object {
        @Suppress("unused")
        @JvmStatic
        fun deserialize(map: Map<String, Any>): LaunchAction? {
            val face = runCatchingOrNull { (map["face"] as? String)?.let(BlockFace::valueOf) } ?: return null
            val multiplier = map["multiplier"] as? Double ?: return null
            val type = map["type"] as? ExecutionType ?: return null
            return LaunchAction(face, multiplier, type)
        }
    }
}
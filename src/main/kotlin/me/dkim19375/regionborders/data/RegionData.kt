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

package me.dkim19375.regionborders.data

import me.dkim19375.regionborders.action.Action
import org.bukkit.configuration.serialization.ConfigurationSerializable

data class RegionData(
    val boundary: Boundary,
    val name: String,
    val actions: List<Action>
) : ConfigurationSerializable {
    override fun serialize(): Map<String, Any> = mapOf(
        "boundary" to boundary,
        "name" to name,
        "actions" to actions
    )

    companion object {
        @Suppress("unused", "UNCHECKED_CAST")
        @JvmStatic
        fun deserialize(map: Map<String, Any>): RegionData? {
            val boundary = (map["boundary"] as? Boundary) ?: return null
            val name = (map["name"] as? String) ?: return null
            val actions = (map["actions"] as? List<Action?>) ?: return null
            return RegionData(boundary, name, actions.filterNotNull())
        }
    }
}
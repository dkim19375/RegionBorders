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

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.configuration.serialization.ConfigurationSerializable
import kotlin.math.max
import kotlin.math.min

data class Boundary(
    val world: World,
    val minX: Double,
    val minY: Double,
    val minZ: Double,
    val maxX: Double,
    val maxY: Double,
    val maxZ: Double,
) : ConfigurationSerializable {
    constructor(world: World, firstLoc: Location, secondLoc: Location) : this(
        world = world,
        minX = min(firstLoc.x, secondLoc.x),
        minY = min(firstLoc.y, secondLoc.y),
        minZ = min(firstLoc.z, secondLoc.z),
        maxX = max(firstLoc.x, secondLoc.x),
        maxY = max(firstLoc.y, secondLoc.y),
        maxZ = max(firstLoc.z, secondLoc.z)
    )

    fun getLocations(): Pair<Location, Location> {
        val firstLoc = Location(world, minX, minY, minZ)
        val secondLoc = Location(world, maxX, maxY, maxZ)
        return firstLoc to secondLoc
    }

    fun isInBoundary(location: Location): Boolean {
        val x = location.x.toInt()
        val y = location.y.toInt()
        val z = location.z.toInt()
        val minX = minX.toInt()
        val minY = minY.toInt()
        val minZ = minZ.toInt()
        val maxX = maxX.toInt()
        val maxY = maxY.toInt()
        val maxZ = maxZ.toInt()
        return when {
            location.world?.name != world.name -> false
            x < minX -> false
            y < minY -> false
            z < minZ -> false
            x > maxX -> false
            y > maxY -> false
            z > maxZ -> false
            else -> true
        }
    }

    override fun serialize(): Map<String, Any> = mapOf(
        "world" to world.name,
        "min-x" to minX,
        "min-y" to minY,
        "min-z" to minZ,
        "max-x" to maxX,
        "max-y" to maxY,
        "max-z" to maxZ
    )

    companion object {
        @Suppress("unused")
        @JvmStatic
        fun deserialize(map: Map<String, Any>): Boundary? {
            val world = (map["world"] as? String)?.let(Bukkit::getWorld) ?: return null
            val x1 = (map["min-x"] as? Double) ?: return null
            val y1 = (map["min-y"] as? Double) ?: return null
            val z1 = (map["min-z"] as? Double) ?: return null
            val x2 = (map["max-x"] as? Double) ?: return null
            val y2 = (map["max-y"] as? Double) ?: return null
            val z2 = (map["max-z"] as? Double) ?: return null

            return Boundary(
                world = world,
                minX = min(x1, x2),
                minY = min(y1, y2),
                minZ = min(z1, z2),
                maxX = max(x1, x2),
                maxY = max(y1, y2),
                maxZ = max(z1, z2)
            )
        }
    }
}
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

import me.dkim19375.dkimbukkitcore.config.SpigotConfigFile
import me.dkim19375.regionborders.RegionBorders
import org.bukkit.Location

class RegionFileManager(private val plugin: RegionBorders) {
    val creatingRegions = mutableMapOf<String, Location>()
    private val file: SpigotConfigFile
        get() = plugin.regionFile
    var regions: Set<RegionData> = emptySet()
        private set

    fun reload() {
        regions = file.config.getConfigurationSection("regions")?.let { config ->
            config.getKeys(false)?.map(config::get)?.mapNotNull { it as? RegionData }?.toSet()
        } ?: emptySet()
    }

    fun delete(region: RegionData, save: Boolean = true) {
        val new = regions.toMutableSet()
        new.removeIf { data ->
            data.name == region.name
        }
        regions = new
        if (!save) {
            return
        }
        saveCurrentData()
    }

    fun add(region: RegionData, save: Boolean = true) {
        regions = regions + region
        if (!save) {
            return
        }
        saveCurrentData()
    }

    private fun saveCurrentData() {
        file.config.set("regions", null)
        regions.forEach { data ->
            file.config.set("regions.${data.name}", data)
        }
        file.save()
    }
}
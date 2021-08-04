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

package me.dkim19375.regionborders

import io.github.slimjar.app.builder.ApplicationBuilder
import me.dkim19375.dkimbukkitcore.checker.UpdateChecker
import me.dkim19375.dkimbukkitcore.config.ConfigFile
import me.dkim19375.dkimbukkitcore.function.logInfo
import me.dkim19375.dkimbukkitcore.javaplugin.CoreJavaPlugin
import me.dkim19375.regionborders.command.RegionBordersCmd
import me.dkim19375.regionborders.command.RegionBordersTab
import me.dkim19375.regionborders.data.Boundary
import me.dkim19375.regionborders.data.RegionData
import me.dkim19375.regionborders.data.RegionFileManager
import me.dkim19375.regionborders.enumclass.ActionType
import me.dkim19375.regionborders.enumclass.ExecutionType
import me.dkim19375.regionborders.listener.PlayerMoveListener
import org.bukkit.Bukkit
import org.bukkit.configuration.serialization.ConfigurationSerialization
import java.util.logging.Level
import kotlin.system.measureTimeMillis

class RegionBorders : CoreJavaPlugin() {
    val regionFile by lazy { ConfigFile(this, "regions.yml") }
    val regionManager by lazy { RegionFileManager(this) }
    private val serializableClasses = listOf(
        RegionData::class.java,
        Boundary::class.java,
        ActionType::class.java,
        ExecutionType::class.java,
    ).plus(ActionType.values().map(ActionType::jClass))
    private val updateChecker = UpdateChecker("95000", null, this)
    override val defaultConfig: Boolean = false

    override fun reloadConfig() {
        super.reloadConfig()
        regionManager.reload()
    }

    override fun onLoad() {
        logInfo("Loading libraries... (This may take a few seconds up to a minute)")
        logInfo(
            "Finished loading libraries in ${
                measureTimeMillis {
                    ApplicationBuilder.appending(description.name).build()
                }
            }ms!"
        )
    }

    override fun onEnable() {
        registerSerializable()
        registerConfig(regionFile)
        registerCommand("regionborders", RegionBordersCmd(this), RegionBordersTab(this))
        registerListener(PlayerMoveListener(this))
        Bukkit.getScheduler().runTask(this, this::reloadConfig)
        Bukkit.getScheduler().runTask(this, this::checkForUpdates)
    }

    override fun onDisable() {
        unregisterSerializable()
        unregisterConfig(regionFile)
    }

    private fun checkForUpdates() {
        updateChecker.getSpigotVersion({ version ->
            if (version == description.version) {
                logInfo("${description.name} is up to date! ($version)")
                return@getSpigotVersion
            }
            logInfo("${description.name} is outdated!", Level.WARNING)
            logInfo("Your version: ${description.version}", Level.WARNING)
            logInfo("Newest version: $version", Level.WARNING)
            logInfo("Please update here: ${description.website}", Level.WARNING)
        }) {
            logInfo("Could not get latest version!", Level.SEVERE)
            it.printStackTrace()
        }
    }

    private fun registerSerializable() = serializableClasses.forEach(ConfigurationSerialization::registerClass)

    private fun unregisterSerializable() = serializableClasses.reversed().forEach(ConfigurationSerialization::unregisterClass)
}
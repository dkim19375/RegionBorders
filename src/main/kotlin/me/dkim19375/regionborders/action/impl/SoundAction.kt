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

import me.dkim19375.dkimbukkitcore.function.playSound
import me.dkim19375.dkimcore.extension.runCatchingOrNull
import me.dkim19375.regionborders.action.Action
import me.dkim19375.regionborders.enumclass.ActionType
import me.dkim19375.regionborders.enumclass.ExecutionType
import org.bukkit.Sound
import org.bukkit.event.player.PlayerMoveEvent

class SoundAction(
    private val sound: Sound,
    private val volume: Float,
    private val pitch: Float,
    override val executionType: ExecutionType,
) : Action {
    override val type: ActionType = ActionType.PLAY_SOUND

    override fun execute(event: PlayerMoveEvent) {
        event.player.playSound(sound, volume, pitch)
    }

    override fun format(): String =
        "Sound: ${sound.name}, volume: ${volume * 100.0}, pitch: ${pitch * 100.0}, execution type: ${executionType.name.lowercase()}"

    override fun serialize(): Map<String, Any> = mapOf(
        "sound" to sound.name,
        "volume" to volume,
        "pitch" to pitch,
        "type" to executionType
    )

    companion object {
        @Suppress("unused")
        @JvmStatic
        fun deserialize(map: Map<String, Any>): SoundAction? {
            val sound = runCatchingOrNull { (map["sound"] as? String)?.let(Sound::valueOf) } ?: return null
            val volume = (map["volume"] as? Float) ?: (map["volume"] as? Double)?.toFloat() ?: return null
            val pitch = (map["pitch"] as? Float) ?: (map["pitch"] as? Double)?.toFloat() ?: return null
            val type = map["type"] as? ExecutionType ?: return null
            return SoundAction(sound, volume, pitch, type)
        }
    }
}
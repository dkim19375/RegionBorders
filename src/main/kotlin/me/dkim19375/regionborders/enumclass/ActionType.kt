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

package me.dkim19375.regionborders.enumclass

import me.dkim19375.dkimcore.extension.runCatchingOrNull
import me.dkim19375.regionborders.action.Action
import me.dkim19375.regionborders.action.impl.*
import me.dkim19375.regionborders.extension.sendMessage
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.BlockFace
import org.bukkit.command.CommandSender
import org.bukkit.configuration.serialization.ConfigurationSerializable

@Suppress("SpellCheckingInspection")
enum class ActionType(
    val displayName: String,
    val cmd: String,
    val jClass: Class<out Action>,
    val guiItem: Material,
    val getInstance: (CommandSender, ExecutionType, Array<out String>) -> Action?,
) : ConfigurationSerializable {
    PREVENT(displayName = "Prevent",
        cmd = "prevent",
        jClass = PreventAction::class.java,
        guiItem = Material.BARRIER,
        getInstance = { _, type, _ ->
            PreventAction(type)
        }),
    SEND_MESSAGE(displayName = "Send message",
        cmd = "message",
        jClass = MessageAction::class.java,
        guiItem = Material.BOOK,
        getInstance = action@{ sender, type, args ->
            if (args.size < 6) {
                sender.sendMessage(ErrorType.NOT_ENOUGH_ARGS)
                return@action null
            }
            MessageAction(args.drop(5).joinToString(" "), type)
        }),
    PLACEHOLDER(displayName = "Run placeholder",
        cmd = "placeholder",
        jClass = PlaceholderAction::class.java,
        guiItem = Material.ITEM_FRAME,
        getInstance = action@{ sender, type, args ->
            if (args.size < 6) {
                sender.sendMessage(ErrorType.NOT_ENOUGH_ARGS)
                return@action null
            }
            PlaceholderAction(args.drop(5).joinToString(" "), type)
        }),
    TITLE(displayName = "Show title",
        cmd = "title",
        jClass = TitleAction::class.java,
        guiItem = Material.SIGN,
        getInstance = action@{ sender, type, args ->
            if (args.size < 7) {
                sender.sendMessage(ErrorType.NOT_ENOUGH_ARGS)
                return@action null
            }
            val duration = args[5].toIntOrNull()
            val restArgs = args.drop(duration?.let { 6 } ?: 5).joinToString(" ")
            TitleAction(duration ?: 70, restArgs, type)
        }),
    ACTION_BAR(displayName = "Show action bar",
        cmd = "actionbar",
        jClass = ActionBarAction::class.java,
        guiItem = Material.NAME_TAG,
        getInstance = action@{ sender, type, args ->
            if (args.size < 6) {
                sender.sendMessage(ErrorType.NOT_ENOUGH_ARGS)
                return@action null
            }
            val restArgs = args.drop(5).joinToString(" ")
            ActionBarAction(restArgs, type)
        }),
    CONSOLE_CMD(displayName = "Run a console command",
        cmd = "consolecmd",
        jClass = ConsoleCmdAction::class.java,
        guiItem = Material.COMMAND,
        getInstance = action@{ sender, type, args ->
            if (args.size < 6) {
                sender.sendMessage(ErrorType.NOT_ENOUGH_ARGS)
                return@action null
            }
            ConsoleCmdAction(args.drop(5).joinToString(" "), type)
        }),
    PLAYER_MSG(displayName = "Send message or command as player",
        cmd = "playermsg",
        jClass = PlayerMsgAction::class.java,
        guiItem = Material.WOOD_DOOR,
        getInstance = action@{ sender, type, args ->
            if (args.size < 6) {
                sender.sendMessage(ErrorType.NOT_ENOUGH_ARGS)
                return@action null
            }
            PlayerMsgAction(args.drop(5).joinToString(" "), type)
        }),
    LAUNCH_PLAYER(displayName = "Launch a player a certain direction",
        cmd = "launch",
        jClass = LaunchAction::class.java,
        guiItem = Material.PISTON_BASE,
        getInstance = action@{ sender, type, args ->
            if (args.size < 7) {
                sender.sendMessage(ErrorType.NOT_ENOUGH_ARGS)
                return@action null
            }
            val face = runCatchingOrNull { BlockFace.valueOf(args[5].uppercase()) } ?: run {
                sender.sendMessage("${ChatColor.RED}Invalid face! (North/East/South/West/Up/Down/NorthEast/etc)")
                return@action null
            }
            val multiplier = args[6].toDoubleOrNull() ?: args[6].toIntOrNull()?.toDouble() ?: 1.0
            LaunchAction(face, multiplier, type)
        }),
    PLAY_SOUND(displayName = "Plays a sound",
        cmd = "sound",
        jClass = SoundAction::class.java,
        guiItem = Material.JUKEBOX,
        getInstance = action@{ sender, type, args ->
            if (args.size < 6) {
                sender.sendMessage(ErrorType.NOT_ENOUGH_ARGS)
                return@action null
            }
            val sound = runCatchingOrNull { Sound.valueOf(args[5].uppercase()) } ?: run {
                sender.sendMessage("${ChatColor.RED}Invalid sound!")
                return@action null
            }
            val arg6 = args.getOrNull(6)
            val arg7 = args.getOrNull(7)
            val volume =
                (arg6?.toFloatOrNull() ?: arg6?.toDoubleOrNull()?.toFloat() ?: arg6?.toIntOrNull()?.toFloat()
                ?: 100.0f) / 100.0f
            val pitch =
                (arg7?.toFloatOrNull() ?: arg7?.toDoubleOrNull()?.toFloat() ?: arg7?.toIntOrNull()?.toFloat()
                ?: 100.0f) / 100.0f
            SoundAction(sound, volume, pitch, type)
        });

    override fun serialize(): Map<String, Any> = mapOf(
        "name" to name
    )
    companion object {
        @Suppress("unused")
        @JvmStatic
        fun deserialize(map: Map<String, Any>): ActionType = valueOf(map["name"] as String)

        fun fromString(str: String): ActionType? = values().firstOrNull { value ->
            value.cmd.equals(str, true)
                    || value.name.equals(str, true)
                    || value.displayName.equals(str, true)
        }
    }
}
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

import org.apache.commons.lang.StringUtils

enum class ErrorType(val description: String) {
    NO_PERMISSION("You do not have permission!"),
    INVALID_ARG("Invalid argument!"),
    MUST_BE_PLAYER("You must be a player!"),
    REGION_EXISTS("The region already exists!"),
    INVALID_REGION("The region doesn't exist!"),
    NOT_ENOUGH_ARGS("Not enough arguments!"),
    INVALID_ACTION_ID("Invalid action id!"),
    MUST_CREATE_FIRST_POS("You must create pos1 first!"),
    INVALID_EXECUTION_TYPE(
        "Invalid execution type! ${
            ExecutionType.values()
                .map(ExecutionType::name)
                .map(String::lowercase)
                .joinToString("|", transform = StringUtils::capitalize)
        }"
    ),
    INVALID_ACTION_TYPE(
        "Invalid action type! ${
            ActionType.values()
                .map(ActionType::cmd)
                .map(String::lowercase)
                .joinToString("|", transform = StringUtils::capitalize)
        }"
    )
}
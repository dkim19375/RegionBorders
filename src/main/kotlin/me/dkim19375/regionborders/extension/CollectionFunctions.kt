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

package me.dkim19375.regionborders.extension

import kotlin.math.ceil

fun List<*>.getMaxPages(pages: Int = 7): Int = ceil(size.toDouble() / pages.toDouble()).toInt()

fun <T> List<T>.getPage(page: Int): List<T> {
    val list = mutableListOf<T>()
    for (i in ((page - 1) * 7) until page * 7) {
        val item = getOrNull(i) ?: continue
        list.add(item)
    }
    return list
}
/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/*
 * Builds a pretty looking table, this is SLOW because it loops over all of the elements
 * multiple times (once to get the widths, and once to print) and does a lot of string building.
 * Do not use this in heavy traffic area.
 * Mainly useful for one off debugging.
 *
 * Example output:
 * |------------------------------------------------------------------------------------------------|
 * |  ID  |  Dummy  |            Key            |                       Value                       |
 * |------------------------------------------------------------------------------------------------|
 * | 0    | false   | minecraft:empty           | net.minecraft.fluid.EmptyFluid@1e292249           |
 * | 1    | false   | minecraft:flowing_water   | net.minecraft.fluid.WaterFluid$Flowing@6e4480a6   |
 * | 2    | false   | minecraft:water           | net.minecraft.fluid.WaterFluid$Source@47ffd16     |
 * | 3    | false   | minecraft:flowing_lava    | net.minecraft.fluid.LavaFluid$Flowing@79fb81e2    |
 * | 4    | false   | minecraft:lava            | net.minecraft.fluid.LavaFluid$Source@3bb7b976     |
 * |------------------------------------------------------------------------------------------------|
 */
public class TablePrinter<T>
{
    private final List<Header<T>> headers = new ArrayList<>();
    private final List<T> rows = new ArrayList<>();

    public TablePrinter<T> header(String name, Function<T, String> supplier)
    {
        return header(name, supplier, false);
    }

    public TablePrinter<T> header(String name, Function<T, String> supplier, boolean right)
    {
        headers.add(new Header<>(name, supplier, right));
        return this;
    }

    public void clearRows()
    {
        this.rows.clear();
    }

    public TablePrinter<T> add(T row)
    {
        rows.add(row);
        return this;
    }

    public TablePrinter<T> add(T row, @SuppressWarnings("unchecked") T... more)
    {
        add(row);
        for (T t : more)
            add(t);
        return this;
    }

    public TablePrinter<T> add(Collection<? extends T> rows)
    {
        rows.forEach(this::add);
        return this;
    }

    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder();
        build(buf);
        return buf.toString();
    }

    public void build(StringBuilder buf)
    {
        int[] count = new int[this.headers.size()];
        int width = 1;

        for (int x = 0; x < count.length; x++)
            count[x] = this.headers.get(x).name.length() + 2;

        for (T row : rows) {
            for (int x = 0; x < count.length; x++)
                count[x] = Math.max(count[x], this.headers.get(x).supplier.apply(row).length() + 2);
        }

        for (int x = 0; x < count.length; x++)
            width += count[x] + 3;

        line(buf, width);
        // Add the headers
        for (int x = 0; x < count.length; x++) {
            Header<T> header = this.headers.get(x);
            int left  = (count[x] - header.name.length()) / 2;
            int right = (count[x] - header.name.length()) - left;
            buf.append('|').append(' ');
            pad(buf, left);
            buf.append(header.name);
            pad(buf, right);
            buf.append(' ');
        }
        buf.append('|').append('\n');
        line(buf, width);

        for (T row : rows) {
            for (int x = 0; x < count.length; x++) {
                Header<T> header = this.headers.get(x);
                String data = header.supplier.apply(row);
                int padding = count[x] - data.length();
                buf.append('|').append(' ');
                if (header.right)
                    pad(buf, padding);
                buf.append(data);
                if (!header.right)
                    pad(buf, padding);
                buf.append(' ');
            }
            buf.append('|').append('\n');
        }
        line(buf, width);
    }


    private static void line(StringBuilder buf, int size)
    {
        buf.append('|');
        for (int x = 0; x < size - 2; x++)
            buf.append('-');
        buf.append('|').append('\n');
    }

    private static void pad(StringBuilder buf, int size)
    {
        for (int y = 0; y < size; y++)
            buf.append(' ');
    }


    private static class Header<T>
    {
        private final String name;
        private final Function<T, String> supplier;
        private final boolean right;

        private Header(String name, Function<T, String> supplier, boolean right)
        {
            this.name = name;
            this.supplier = supplier;
            this.right = right;
        }
    }
}

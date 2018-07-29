/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.test;

import com.google.common.collect.Lists;
import net.minecraftforge.common.util.TextTable;
import net.minecraftforge.common.util.TextTable.Column;
import org.junit.Assert;
import org.junit.Test;

import static net.minecraftforge.common.util.TextTable.column;

public class TextTableTest
{
    private static final String WIDTH_REFERENCE = "StringOfWidth15";
    private static final int WIDTH_REFERENCE_LENGTH = WIDTH_REFERENCE.length();

    @Test
    public void testColumnWidthAdjustment()
    {
        Column column = column("Column", TextTable.Alignment.LEFT);
        column.fit(WIDTH_REFERENCE);
        String paddedHeader = column.formatHeader("-");
        Assert.assertEquals("Formatted column header didn't have correct length", WIDTH_REFERENCE_LENGTH, paddedHeader.length());
        Assert.assertEquals("Formatted column header wasn't padded properly", "Column---------", paddedHeader);

        String paddedReference = column.format(WIDTH_REFERENCE, "-");
        Assert.assertEquals("Formatted width reference didn't have correct length", WIDTH_REFERENCE_LENGTH, paddedReference.length());
        Assert.assertEquals("Formatted width reference was changed despite defining width", WIDTH_REFERENCE, paddedReference);
    }

    @Test
    public void testLeftAlignment()
    {
        Column column = column("Left", TextTable.Alignment.LEFT);
        column.fit(WIDTH_REFERENCE);

        String paddedHeader = column.formatHeader("-");
        Assert.assertEquals("Left-aligned header should be padded on the right", "Left-----------", paddedHeader);
        String paddedReference = column.format(WIDTH_REFERENCE, "-");
        Assert.assertEquals("Left-aligned reference should'nt be padded", WIDTH_REFERENCE, paddedReference);
        String paddedValue = column.format("Value", "-");
        Assert.assertEquals("Left-aligned value should be padded on the right", "Value----------", paddedValue);
    }

    @Test
    public void testCenterAlignment()
    {
        Column column = column("Centered", TextTable.Alignment.CENTER);
        column.fit(WIDTH_REFERENCE);

        String paddedHeader = column.formatHeader("-");
        Assert.assertEquals("Centered header should be padded equally on both sides", "---Centered----", paddedHeader);
        String paddedReference = column.format(WIDTH_REFERENCE, "-");
        Assert.assertEquals("Centered reference should'nt be padded", WIDTH_REFERENCE, paddedReference);
        String paddedValue = column.format("Value", "-");
        Assert.assertEquals("Centered value should be padded equally on both sides", "-----Value-----", paddedValue);
        String paddedOffCenter = column.format("Value1", "-");
        Assert.assertNotEquals("Center padding should be left-biased", "-----Value1----", paddedOffCenter);
    }

    @Test
    public void testRightAlignment()
    {
        Column column = column("Right", TextTable.Alignment.RIGHT);
        column.fit(WIDTH_REFERENCE);

        String paddedHeader = column.formatHeader("-");
        Assert.assertEquals("Right-aligned header should be padded on the left", "----------Right", paddedHeader);
        String paddedReference = column.format(WIDTH_REFERENCE, "-");
        Assert.assertEquals("Right-aligned reference should'nt be padded", WIDTH_REFERENCE, paddedReference);
        String paddedValue = column.format("Value", "-");
        Assert.assertEquals("Right-aligned value should be padded on the left", "----------Value", paddedValue);
    }

    @Test
    public void testMarkdownCompliance()
    {
        TextTable table = new TextTable(Lists.newArrayList(
            column("Left", TextTable.Alignment.LEFT),
            column("Center", TextTable.Alignment.CENTER),
            column("Right", TextTable.Alignment.RIGHT)
        ));
        table.add("Long Value 1", "Value 2", "Value 3");
        table.add("Value 1", "Long Value 2", "Value 3");
        table.add("Value 1", "Value 2", "Long Value 3");
        int[] columnWidths = table.getColumns().stream().mapToInt(Column::getWidth).toArray();
        Assert.assertArrayEquals("Column widths should adjust for long values", new int[]{12, 12, 12}, columnWidths);

        String[] result = table.build("\n").split("\n");
        Assert.assertEquals("Header row + separator row + value rows should result in 5 lines", 5, result.length);
        Assert.assertEquals(
            "Column headers should be properly formatted",
            "| Left         |    Center    |        Right |",
            result[0]);
        Assert.assertEquals(
            "Header-body separators should contain markdown alignment information",
            "|:------------ |:------------:| ------------:|",
            result[1]
        );
    }
}

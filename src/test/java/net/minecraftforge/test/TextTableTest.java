/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.test;

import com.google.common.collect.Lists;
import net.minecraftforge.common.util.TextTable;
import net.minecraftforge.common.util.TextTable.Column;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

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
        assertEquals(WIDTH_REFERENCE_LENGTH, paddedHeader.length(), "Formatted column header didn't have correct length");
        assertEquals("Column---------", paddedHeader, "Formatted column header wasn't padded properly");

        String paddedReference = column.format(WIDTH_REFERENCE, "-");
        assertEquals(WIDTH_REFERENCE_LENGTH, paddedReference.length(), "Formatted width reference didn't have correct length");
        assertEquals(WIDTH_REFERENCE, paddedReference, "Formatted width reference was changed despite defining width");
    }

    @Test
    public void testLeftAlignment()
    {
        Column column = column("Left", TextTable.Alignment.LEFT);
        column.fit(WIDTH_REFERENCE);

        assertEquals("Left-----------", column.formatHeader("-"), "Left-aligned header should be padded on the right");
        assertEquals(WIDTH_REFERENCE, column.format(WIDTH_REFERENCE, "-"), "Left-aligned reference should'nt be padded");
        assertEquals("Value----------", column.format("Value", "-"), "Left-aligned value should be padded on the right");
    }

    @Test
    public void testCenterAlignment()
    {
        Column column = column("Centered", TextTable.Alignment.CENTER);
        column.fit(WIDTH_REFERENCE);

        assertEquals("---Centered----", column.formatHeader("-"), "Centered header should be padded equally on both sides");
        assertEquals(WIDTH_REFERENCE, column.format(WIDTH_REFERENCE, "-"), "Centered reference should'nt be padded");
        assertEquals("-----Value-----",  column.format("Value", "-"), "Centered value should be padded equally on both sides");
        assertNotEquals("-----Value1----", column.format("Value1", "-"), "Center padding should be left-biased");
    }

    @Test
    public void testRightAlignment()
    {
        Column column = column("Right", TextTable.Alignment.RIGHT);
        column.fit(WIDTH_REFERENCE);

        assertEquals("----------Right", column.formatHeader("-"), "Right-aligned header should be padded on the left");
        assertEquals(WIDTH_REFERENCE, column.format(WIDTH_REFERENCE, "-"), "Right-aligned reference should'nt be padded");
        assertEquals("----------Value", column.format("Value", "-"), "Right-aligned value should be padded on the left");
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
        assertArrayEquals(new int[]{12, 12, 12}, columnWidths, "Column widths should adjust for long values");

        String[] result = table.build("\n").split("\n");
        assertEquals(5, result.length, "Header row + separator row + value rows should result in 5 lines");
        assertEquals("| Left         |    Center    |        Right |", result[0],  "Column headers should be properly formatted");
        assertEquals("|:------------ |:------------:| ------------:|", result[1], "Header-body separators should contain markdown alignment information");
    }
}

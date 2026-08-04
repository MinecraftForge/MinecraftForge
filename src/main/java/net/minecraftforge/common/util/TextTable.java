/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.util;

import com.google.common.collect.Streams;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Utility to format data into a textual (markdown-compliant) table.
 */
public class TextTable
{
    public static Column column(String header)
    {
        return new Column(header);
    }

    public static Column column(String header, Alignment alignment)
    {
        return new Column(header, alignment);
    }

    private final List<Column> columns;
    private final List<Row> rows = new ArrayList<>();

    public TextTable(List<Column> columns)
    {
        this.columns = columns;
    }

    public String build(String lineEnding)
    {
        StringBuilder destination = new StringBuilder();
        append(destination, lineEnding);
        return destination.toString();
    }

    /**
     * Appends the data formatted as a table to the given string builder.
     * The padding character used for the column alignments is a single space (' '),
     * the separate between column headers and values is a dash ('-').
     * Note that you *have* to specify a line ending, '\n' isn't used by default.
     * <p>
     * The generated table is compliant with the markdown file format.
     *
     * @param destination a string builder to append the table to
     * @param lineEnding  the line ending to use for each row of the table
     */
    public void append(StringBuilder destination, String lineEnding)
    {
        List<String> headers = columns.stream().map(c -> c.formatHeader(" ")).collect(Collectors.toList());
        printRow(destination, headers);
        destination.append(lineEnding);
        printSeparators(destination);
        for (Row row : rows)
        {
            destination.append(lineEnding);
            printRow(destination, row.format(columns, " "));
        }
    }

    private void printSeparators(StringBuilder destination)
    {
        destination.append('|');
        for (Column column : columns)
        {
            destination.append(column.alignment != Alignment.RIGHT ? ':' : ' ');
            destination.append(column.getSeparator('-'));
            destination.append(column.alignment != Alignment.LEFT ? ':' : ' ');
            destination.append('|');
        }
    }

    private void printRow(StringBuilder destination, List<String> values)
    {
        destination.append('|');
        for (String value : values)
        {
            destination.append(' ');
            destination.append(value);
            destination.append(' ');
            destination.append('|');
        }
    }

    public void add(@Nonnull Object... values)
    {
        if (values.length != columns.size())
        {
            throw new IllegalArgumentException("Received wrong amount of values for table row, expected " + columns.size() + ", received " + columns.size() + ".");
        }
        Row row = new Row();
        for (int i = 0; i < values.length; i++)
        {
            String value = Objects.toString(values[i]);
            row.values.add(value);
            columns.get(i).fit(value);
        }
        rows.add(row);
    }

    public void clear()
    {
        for (Column column : columns)
        {
            column.resetWidth();
        }
        rows.clear();
    }

    public List<Column> getColumns()
    {
        return Collections.unmodifiableList(columns);
    }

    public static class Column
    {
        private String header;
        private int width;
        private Alignment alignment;

        public Column(String header)
        {
            this(header, Alignment.LEFT);
        }

        public Column(String header, Alignment alignment)
        {
            this.header = header;
            this.width = header.length();
            this.alignment = alignment;
        }

        public String formatHeader(String padding)
        {
            return format(header, padding);
        }

        public String format(String value, String padding)
        {
            switch (alignment)
            {
                case LEFT:
                    return StringUtils.rightPad(value, width, padding);
                case RIGHT:
                    return StringUtils.leftPad(value, width, padding);
                default:
                    int length = value.length();
                    int left = (width - length) / 2;
                    int leftWidth = left + length;
                    return StringUtils.rightPad(StringUtils.leftPad(value, leftWidth, padding), width, padding);
            }
        }

        public String getSeparator(char character)
        {
            return StringUtils.leftPad("", width, character);
        }

        public void fit(String value)
        {
            if (value.length() > width)
            {
                width = value.length();
            }
        }

        public void resetWidth()
        {
            this.width = header.length();
        }

        public int getWidth()
        {
            return width;
        }
    }

    public static class Row
    {
        private final ArrayList<String> values = new ArrayList<>();

        public List<String> format(List<Column> columns, String padding)
        {
            if (columns.size() != values.size())
            {
                throw new IllegalArgumentException("Received wrong amount of columns for table row, expected " + columns.size() + ", received " + columns.size() + ".");
            }
            return Streams.zip(values.stream(), columns.stream(), (v, c) -> c.format(v, padding)).collect(Collectors.toList());
        }
    }

    public enum Alignment
    {
        LEFT, CENTER, RIGHT
    }
}

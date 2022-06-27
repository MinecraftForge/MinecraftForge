/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.obj;

import com.google.common.base.Charsets;
import joptsimple.internal.Strings;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A tokenizer for OBJ and MTL files.
 * <p>
 * Joins split lines and ignores comments.
 */
public class ObjTokenizer implements AutoCloseable
{
    private final BufferedReader lineReader;

    public ObjTokenizer(InputStream inputStream)
    {
        this.lineReader = new BufferedReader(new InputStreamReader(inputStream, Charsets.UTF_8));
    }

    @Nullable
    public String[] readAndSplitLine(boolean ignoreEmptyLines) throws IOException
    {
        //noinspection LoopConditionNotUpdatedInsideLoop
        do
        {
            String currentLine = lineReader.readLine();
            if (currentLine == null)
                return null;

            List<String> lineParts = new ArrayList<>();

            if (currentLine.startsWith("#"))
                currentLine = "";

            if (currentLine.length() > 0)
            {

                boolean hasContinuation;
                do
                {
                    hasContinuation = currentLine.endsWith("\\");
                    String tmp = hasContinuation ? currentLine.substring(0, currentLine.length() - 1) : currentLine;

                    Arrays.stream(tmp.split("[\t ]+")).filter(s -> !Strings.isNullOrEmpty(s)).forEach(lineParts::add);

                    if (hasContinuation)
                    {
                        currentLine = lineReader.readLine();
                        if (currentLine == null)
                            break;

                        if (currentLine.length() == 0 || currentLine.startsWith("#"))
                            break;
                    }
                } while (hasContinuation);
            }

            if (lineParts.size() > 0)
                return lineParts.toArray(new String[0]);
        }
        while (ignoreEmptyLines);

        return new String[0];
    }

    @Override
    public void close() throws IOException
    {
        lineReader.close();
    }
}

/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.obj;

import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A tokenizer for OBJ and MTL files.
 * <p>
 * Joins split lines and ignores comments.
 */
public class ObjTokenizer implements AutoCloseable {
    private static final Pattern TABS = Pattern.compile("[\t ]+");
    private final BufferedReader lineReader;

    public ObjTokenizer(InputStream inputStream) {
        this.lineReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    }

    @Nullable
    public String[] readAndSplitLine(boolean ignoreEmptyLines) throws IOException {
        //noinspection LoopConditionNotUpdatedInsideLoop
        do {
            String currentLine = lineReader.readLine();
            if (currentLine == null)
                return null;

            List<String> lineParts = new ArrayList<>();

            if (currentLine.startsWith("#"))
                currentLine = "";

            if (!currentLine.isEmpty()) {

                boolean hasContinuation;
                do {
                    hasContinuation = currentLine.endsWith("\\");
                    String tmp = hasContinuation ? currentLine.substring(0, currentLine.length() - 1) : currentLine;

                    for (var part : TABS.split(tmp)) {
                        if (part != null && !part.isEmpty())
                            lineParts.add(part);
                    }

                    if (hasContinuation) {
                        currentLine = lineReader.readLine();
                        if (currentLine == null)
                            break;

                        if (currentLine.isEmpty() || currentLine.startsWith("#"))
                            break;
                    }
                } while (hasContinuation);
            }

            if (!lineParts.isEmpty())
                return lineParts.toArray(new String[0]);
        }
        while (ignoreEmptyLines);

        return new String[0];
    }

    @Override
    public void close() throws IOException {
        lineReader.close();
    }
}

/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

package net.minecraftforge.client.model.obj;

import com.google.common.base.Charsets;
import joptsimple.internal.Strings;
import net.minecraft.resources.IResource;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LineReader implements AutoCloseable
{
    InputStreamReader lineStream;
    BufferedReader lineReader;

    public LineReader(IResource resource)
    {
        this.lineStream = new InputStreamReader(resource.getInputStream(), Charsets.UTF_8);
        this.lineReader = new BufferedReader(lineStream);
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
    public void close() throws Exception
    {
        lineReader.close();
        lineStream.close();
    }
}

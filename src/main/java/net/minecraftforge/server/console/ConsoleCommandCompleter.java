/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

package net.minecraftforge.server.console;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jline.console.completer.Completer;
import net.minecraft.server.dedicated.DedicatedServer;

public final class ConsoleCommandCompleter implements Completer
{

    private static final Logger logger = LogManager.getLogger();
    private final DedicatedServer server;

    public ConsoleCommandCompleter(DedicatedServer server)
    {
        this.server = checkNotNull(server, "server");
    }

    @Override
    public int complete(String buffer, int cursor, List<CharSequence> candidates)
    {
        int len = buffer.length();

        boolean prefix;
        if (buffer.isEmpty() || buffer.charAt(0) != '/')
        {
            buffer = '/' + buffer;
            prefix = false;
        }
        else
        {
            prefix = true;
        }

        final String input = buffer;
        Future<List<String>> tabComplete = this.server.callFromMainThread(new Callable<List<String>>() {

            @Override
            public List<String> call() throws Exception
            {
                return ConsoleCommandCompleter.this.server.getTabCompletions(ConsoleCommandCompleter.this.server, input,
                        ConsoleCommandCompleter.this.server.getPosition(), false/*  we're not a command block */);
            }
        });
        try
        {
            List<String> completions = tabComplete.get();
            Collections.sort(completions);
            if (prefix)
            {
                candidates.addAll(completions);
            }
            else
            {
                for (String completion : completions)
                {
                    candidates.add(completion.charAt(0) == '/' ? completion.substring(1) : completion);
                }
            }

            int pos = buffer.lastIndexOf(' ');
            if (pos == -1)
            {
                return cursor - len;
            }
            else if (prefix)
            {
                return cursor - len + pos + 1;
            }
            else
            {
                return cursor - len + pos;
            }
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
        catch (ExecutionException e)
        {
            logger.error("Failed to tab complete", e);
        }

        return cursor;
    }

}

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

package net.minecraftforge.server.console;

import net.minecraft.server.dedicated.DedicatedServer;
import net.minecrell.terminalconsole.TerminalConsoleAppender;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;

public final class TerminalHandler
{

    private TerminalHandler()
    {
    }

    public static boolean handleCommands(DedicatedServer server)
    {
        final Terminal terminal = TerminalConsoleAppender.getTerminal();
        if (terminal == null)
            return false;

        LineReader reader = LineReaderBuilder.builder()
                .appName("Forge")
                .terminal(terminal)
                .completer(new ConsoleCommandCompleter(server))
                .build();
        reader.setOpt(LineReader.Option.DISABLE_EVENT_EXPANSION);
        reader.unsetOpt(LineReader.Option.INSERT_TAB);

        TerminalConsoleAppender.setReader(reader);

        try
        {
            String line;
            while (!server.isServerStopped() && server.isServerRunning())
            {
                try
                {
                    line = reader.readLine("> ");
                }
                catch (EndOfFileException ignored)
                {
                    // Continue reading after EOT
                    continue;
                }

                if (line == null)
                    break;

                line = line.trim();
                if (!line.isEmpty())
                {
                    server.handleConsoleInput(line, server.getCommandSource());
                }
            }
        }
        catch (UserInterruptException e)
        {
            server.initiateShutdown(true);
        }
        finally
        {
            TerminalConsoleAppender.setReader(null);
        }

        return true;
    }

}

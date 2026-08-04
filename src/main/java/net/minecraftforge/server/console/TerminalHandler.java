/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.server.console;

import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraftforge.fml.StartupQuery;
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

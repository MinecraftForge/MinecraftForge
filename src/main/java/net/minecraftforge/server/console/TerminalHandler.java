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

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jline.console.ConsoleReader;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.server.console.log4j.TerminalConsoleAppender;

public final class TerminalHandler
{

    private static final Logger logger = LogManager.getLogger();

    private TerminalHandler()
    {
    }

    public static boolean handleCommands(DedicatedServer server)
    {
        final ConsoleReader reader = TerminalConsoleAppender.getReader();
        if (reader != null)
        {
            TerminalConsoleAppender.setFormatter(new ConsoleFormatter());
            reader.addCompleter(new ConsoleCommandCompleter(server));

            String line;
            while (!server.isServerStopped() && server.isServerRunning())
            {
                try
                {
                    line = reader.readLine("> ");
                    if (line == null)
                    {
                        break;
                    }

                    line = line.trim();
                    if (!line.isEmpty())
                    {
                        server.addPendingCommand(line, server);
                    }
                }
                catch (IOException e)
                {
                    logger.error("Exception handling console input", e);
                }
            }

            return true;
        }
        else
        {
            TerminalConsoleAppender.setFormatter(TextFormatting::getTextWithoutFormattingCodes);
            return false;
        }
    }

}

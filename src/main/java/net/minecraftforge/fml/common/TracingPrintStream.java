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

package net.minecraftforge.fml.common;

import java.io.PrintStream;

import org.apache.logging.log4j.Logger;

/**
 * PrintStream which redirects it's output to a given logger.
 *
 * @author Arkan
 */
public class TracingPrintStream extends PrintStream {

    private Logger logger;
    private int BASE_DEPTH = 3;

    public TracingPrintStream(Logger logger, PrintStream original) {
        super(original);
        this.logger = logger;
    }

    @Override
    public void println(Object o) {
        logger.info("{}{}", getPrefix(), o);
    }

    @Override
    public void println(String s) {
        logger.info("{}{}", getPrefix(), s);
    }

    private String getPrefix() {
        StackTraceElement[] elems = Thread.currentThread().getStackTrace();
        StackTraceElement elem = elems[BASE_DEPTH]; // The caller is always at BASE_DEPTH, including this call.
        if (elem.getClassName().startsWith("kotlin.io.")) {
            elem = elems[BASE_DEPTH + 2]; // Kotlins IoPackage masks origins 2 deeper in the stack.
        } else if (elem.getClassName().startsWith("java.lang.Throwable")) {
            elem = elems[BASE_DEPTH + 4];
        }
        return "[" + elem.getClassName() + ":" + elem.getMethodName() + ":" + elem.getLineNumber() + "]: ";
    }

}

/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
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
        logger.info(getPrefix() + o);
    }

    @Override
    public void println(String s) {
        logger.info(getPrefix() + s);
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

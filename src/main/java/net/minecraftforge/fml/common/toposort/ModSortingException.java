/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors:
 *     cpw - implementation
 */

package net.minecraftforge.fml.common.toposort;

import java.util.Set;

import org.apache.logging.log4j.Level;

import net.minecraftforge.fml.common.EnhancedRuntimeException;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.toposort.ModSortingException.SortingExceptionData;

public class ModSortingException extends EnhancedRuntimeException
{
    private static final long serialVersionUID = 1L;

    public class SortingExceptionData<T>
    {
        public SortingExceptionData(T node, Set<T> visitedNodes)
        {
            this.firstBadNode = node;
            this.visitedNodes = visitedNodes;
        }

        private T firstBadNode;
        private Set<T> visitedNodes;

        public T getFirstBadNode()
        {
            return firstBadNode;
        }
        public Set<T> getVisitedNodes()
        {
            return visitedNodes;
        }
    }

    private SortingExceptionData<?> sortingExceptionData;

    public <T> ModSortingException(String string, T node, Set<T> visitedNodes)
    {
        super(string);
        this.sortingExceptionData = new SortingExceptionData<T>(node, visitedNodes);
    }

    @SuppressWarnings("unchecked")
    public <T> SortingExceptionData<T> getExceptionData()
    {
        return (SortingExceptionData<T>) sortingExceptionData;
    }

    @Override
    protected void printStackTrace(WrappedPrintStream stream)
    {
        SortingExceptionData<ModContainer> exceptionData = getExceptionData();
        stream.println("A dependency cycle was detected in the input mod set so an ordering cannot be determined");
        stream.println("The first mod in the cycle is " + exceptionData.getFirstBadNode());
        stream.println("The mod cycle involves:");
        for (ModContainer mc : exceptionData.getVisitedNodes())
        {
            stream.println(String.format("\t%s : before: %s, after: %s", mc.toString(), mc.getDependants(), mc.getDependencies()));
        }
    }

}

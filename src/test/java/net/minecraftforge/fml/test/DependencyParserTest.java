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

package net.minecraftforge.fml.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;
import net.minecraftforge.fml.common.versioning.DependencyParser;
import net.minecraftforge.fml.relauncher.Side;

import com.google.common.base.Functions;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;

import net.minecraftforge.fml.common.LoaderException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class DependencyParserTest
{
    private static DependencyParser clientDependencyParser;
    private static DependencyParser serverDependencyParser;
    private static List<DependencyParser> parsers;

    @BeforeAll
    public static void beforeAll()
    {
        clientDependencyParser = new DependencyParser("test", Side.CLIENT);
        serverDependencyParser = new DependencyParser("test", Side.SERVER);
        parsers = ImmutableList.of(clientDependencyParser, serverDependencyParser);
    }

    @Test
    public void testParsingNothing()
    {
        List<String> strings = new ArrayList<>();
        strings.add(null);
        strings.add("");
        strings.add(";;;;;;;");
        strings.add(";  ;   ;    ;     ;      ;       ;        ");
        for (String string : strings)
        {
            for (DependencyParser parser : parsers)
            {
                DependencyParser.DependencyInfo info = parser.parseDependencies(string);
                assertTrue(info.requirements.isEmpty());
                assertTrue(info.dependants.isEmpty());
                assertTrue(info.dependencies.isEmpty());
            }
        }
    }

    @Test
    public void testParsingRequired()
    {
        String mod = "supermod3000@[1.2,)";
        for (DependencyParser parser : parsers)
        {
            DependencyParser.DependencyInfo info = parser.parseDependencies("required:" + mod);
            assertContainsSameToString(info.requirements, Sets.newHashSet(mod));
            assertTrue(info.dependants.isEmpty());
            assertTrue(info.dependencies.isEmpty());
        }
    }

    @Test
    public void testParsingBefore()
    {
        String mod = "supermod3000@[1.2,)";
        for (DependencyParser parser : parsers)
        {
            DependencyParser.DependencyInfo info = parser.parseDependencies("before:" + mod);
            assertTrue(info.requirements.isEmpty());
            assertContainsSameToString(info.dependants, Sets.newHashSet(mod));
            assertTrue(info.dependencies.isEmpty());
        }
    }

    @Test
    public void testParsingAfter()
    {
        String mod = "supermod3000@[1.2,)";
        for (DependencyParser parser : parsers)
        {
            DependencyParser.DependencyInfo info = parser.parseDependencies("after:" + mod);
            assertTrue(info.requirements.isEmpty());
            assertTrue(info.dependants.isEmpty());
            assertContainsSameToString(info.dependencies, Sets.newHashSet(mod));
        }
    }

    @Test
    public void testParsingNoPrefix()
    {
        String mod = "supermod3000@[1.2,)";
        for (DependencyParser parser : parsers)
        {
            assertThrows(LoaderException.class, () -> {
                parser.parseDependencies(mod);
            });
        }
    }

    @Test
    public void testParsingCombined()
    {
        for (DependencyParser parser : parsers)
        {
            String dependencyString = "after:supermod2000@[1.3,);required-before:yetanothermod;required:modw";
            DependencyParser.DependencyInfo info = parser.parseDependencies(dependencyString);
            assertContainsSameToString(info.requirements, Sets.newHashSet("yetanothermod", "modw"));
            assertContainsSameToString(info.dependencies, Sets.newHashSet("supermod2000@[1.3,)"));
            assertContainsSameToString(info.dependants, Sets.newHashSet("yetanothermod"));
        }
    }

    @Test
    public void testParsingSided()
    {
        String mod = "testmod@[1.0,2.0)";
        {
            DependencyParser.DependencyInfo info = clientDependencyParser.parseDependencies("client-after:" + mod);
            assertTrue(info.requirements.isEmpty());
            assertTrue(info.dependants.isEmpty());
            assertContainsSameToString(info.dependencies, Sets.newHashSet(mod));
        }

        {
            DependencyParser.DependencyInfo info = clientDependencyParser.parseDependencies("server-after:testmod@[1.0,2.0);server-required:testmod2;server-after:testmod3;server-before:testmod4");
            assertTrue(info.requirements.isEmpty());
            assertTrue(info.dependants.isEmpty());
            assertTrue(info.dependencies.isEmpty());
        }

        {
            DependencyParser.DependencyInfo info = clientDependencyParser.parseDependencies("client-before:testmod@[1.0,2.0);server-required:testmod2;server-after:testmod3;server-before:testmod4");
            assertTrue(info.requirements.isEmpty());
            assertContainsSameToString(info.dependants, Sets.newHashSet(mod));
            assertTrue(info.dependencies.isEmpty());
        }

        {
            DependencyParser.DependencyInfo info = serverDependencyParser.parseDependencies("server-before:" + mod);
            assertTrue(info.requirements.isEmpty());
            assertContainsSameToString(info.dependants, Sets.newHashSet(mod));
            assertTrue(info.dependencies.isEmpty());
        }

        {
            DependencyParser.DependencyInfo info = serverDependencyParser.parseDependencies("client-before:testmod@[1.0,2.0);client-required:testmod2;client-after:testmod3;client-before:testmod4");
            assertTrue(info.requirements.isEmpty());
            assertTrue(info.dependants.isEmpty());
            assertTrue(info.dependencies.isEmpty());
        }

        {
            DependencyParser.DependencyInfo info = serverDependencyParser.parseDependencies("server-before:testmod@[1.0,2.0);client-required:testmod2;client-after:testmod3;client-before:testmod4");
            assertTrue(info.requirements.isEmpty());
            assertContainsSameToString(info.dependants, Sets.newHashSet(mod));
            assertTrue(info.dependencies.isEmpty());
        }
    }

    @Test
    public void testParsingInvalidDependencyInstructions()
    {
        for (DependencyParser parser : parsers)
        {
            assertThrows(LoaderException.class, () -> {
                parser.parseDependencies("gibberishtext:amod");
            });
        }
    }

    @Test
    public void testParsingInvalidDependencyVersionClient()
    {
        for (DependencyParser parser : parsers)
        {
            assertThrows(LoaderException.class, () -> {
                parser.parseDependencies("amod@[10");
            });
            assertThrows(LoaderException.class, () -> {
                parser.parseDependencies("client:amod@[10");
            });
            assertThrows(LoaderException.class, () -> {
                parser.parseDependencies("server:amod@[10");
            });
        }
    }

    // TODO: enable this in 1.13
//    @Test(expected = LoaderException.class)
//    public void testParsingUppercaseModId()
//    {
//        for (DependencyParser parser : parsers)
//        {
//            assertThrows(LoaderException.class, () -> {
//                parser.parseDependencies("Forge@[1.0]");
//            });
//            assertThrows(LoaderException.class, () -> {
//                parser.parseDependencies("client:Forge@[1.0]");
//            });
//            assertThrows(LoaderException.class, () -> {
//                parser.parseDependencies("server:Forge@[1.0]");
//            });
//        }
//    }

    @Test
    public void testParsingSoftDepWithNoVersion()
    {
        for (DependencyParser parser : parsers)
        {
            assertThrows(LoaderException.class, () -> {
                parser.parseDependencies("amod");
            });
            assertThrows(LoaderException.class, () -> {
                parser.parseDependencies("client:amod");
            });
            assertThrows(LoaderException.class, () -> {
                parser.parseDependencies("server:amod");
            });
        }
    }

    @Test
    public void testParsingDepAfterAll()
    {
        for (DependencyParser parser : parsers)
        {
            parser.parseDependencies("after:*");
        }
    }

    @Test
    public void testParsingDepBeforeAll()
    {
        for (DependencyParser parser : parsers)
        {
            parser.parseDependencies("before:*");
        }
    }

    @Test
    public void testParsingDepOnAll()
    {
        for (DependencyParser parser : parsers)
        {
            assertThrows(LoaderException.class, () -> {
                parser.parseDependencies("*");
            });
        }
    }

    @Test
    public void testParsingSidedDepOnAll()
    {
        for (DependencyParser parser : parsers)
        {
            assertThrows(LoaderException.class, () -> {
                parser.parseDependencies("client:*");
            });
        }
    }

    @Test
    public void testParsingRequireAll()
    {
        for (DependencyParser parser : parsers)
        {
            assertThrows(LoaderException.class, () -> {
                parser.parseDependencies("required:*");
            });
        }
    }

    @Test
    public void testParsingVersionedAll()
    {
        for (DependencyParser parser : parsers)
        {
            assertThrows(LoaderException.class, () -> {
                parser.parseDependencies("*@[1.0]");
            });
        }
    }

    public static void assertContainsSameToString(Collection<?> c1, Collection<String> expected)
    {
        Collection<String> transformedToString = Collections2.transform(c1, Functions.toStringFunction());
        assertContainsSame(transformedToString, expected);
    }

    public static <T> void assertContainsSame(Collection<T> c1, Collection<T> c2)
    {
        if (!c1.containsAll(c2) || !c2.containsAll(c1))
        {
            fail(c1 + " does not contain the same as " + c2);
        }
    }
}

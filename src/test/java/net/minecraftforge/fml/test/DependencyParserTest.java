package net.minecraftforge.fml.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;
import net.minecraftforge.fml.common.versioning.DependencyParser;
import net.minecraftforge.fml.relauncher.Side;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.base.Functions;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;

import net.minecraftforge.fml.common.LoaderException;

public class DependencyParserTest
{
    private static DependencyParser clientDependencyParser;
    private static DependencyParser serverDependencyParser;
    private static List<DependencyParser> parsers;

    @BeforeClass
    public static void beforeClass()
    {
        clientDependencyParser = new DependencyParser(Side.CLIENT);
        serverDependencyParser = new DependencyParser(Side.SERVER);
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
                assertTrue(info.softRequirements.isEmpty());
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
            assertTrue(info.softRequirements.isEmpty());
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
            assertTrue(info.softRequirements.isEmpty());
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
            assertTrue(info.softRequirements.isEmpty());
            assertTrue(info.dependants.isEmpty());
            assertContainsSameToString(info.dependencies, Sets.newHashSet(mod));
        }
    }

    @Test
    public void testParsingSoftDependencies()
    {
        String mod = "supermod3000@[1.2,)";
        for (DependencyParser parser : parsers)
        {
            DependencyParser.DependencyInfo info = parser.parseDependencies(mod);
            assertTrue(info.requirements.isEmpty());
            assertContainsSameToString(info.softRequirements, Sets.newHashSet(mod));
            assertTrue(info.dependants.isEmpty());
            assertTrue(info.dependencies.isEmpty());
        }
    }

    @Test
    public void testParsingCombined()
    {
        for (DependencyParser parser : parsers)
        {
            {
                DependencyParser.DependencyInfo info = parser.parseDependencies("required:supermod3000@[1.2,)");
                assertContainsSameToString(info.requirements, Sets.newHashSet("supermod3000@[1.2,)"));
                assertTrue(info.dependants.isEmpty());
                assertTrue(info.dependencies.isEmpty());
                assertTrue(info.softRequirements.isEmpty());
            }

            {
                String dependencyString = "after:supermod2000@[1.3,);required-before:yetanothermod;softdepmod@[1.0,2.0);required:modw";
                DependencyParser.DependencyInfo info = parser.parseDependencies(dependencyString);
                assertContainsSameToString(info.requirements, Sets.newHashSet("yetanothermod", "modw"));
                assertContainsSameToString(info.softRequirements, Sets.newHashSet("softdepmod@[1.0,2.0)"));
                assertContainsSameToString(info.dependencies, Sets.newHashSet("supermod2000@[1.3,)"));
                assertContainsSameToString(info.dependants, Sets.newHashSet("yetanothermod"));
            }
        }
    }

    @Test
    public void testParsingSided()
    {
        String mod = "testmod@[1.0,2.0)";
        {
            DependencyParser.DependencyInfo info = clientDependencyParser.parseDependencies("client:" + mod);
            assertTrue(info.requirements.isEmpty());
            assertContainsSameToString(info.softRequirements, Sets.newHashSet(mod));
            assertTrue(info.dependants.isEmpty());
            assertTrue(info.dependencies.isEmpty());
        }

        {
            DependencyParser.DependencyInfo info = clientDependencyParser.parseDependencies("server:testmod@[1.0,2.0);server-required:testmod2;server-after:testmod3;server-before:testmod4");
            assertTrue(info.requirements.isEmpty());
            assertTrue(info.softRequirements.isEmpty());
            assertTrue(info.dependants.isEmpty());
            assertTrue(info.dependencies.isEmpty());
        }

        {
            DependencyParser.DependencyInfo info = clientDependencyParser.parseDependencies("client:testmod@[1.0,2.0);server-required:testmod2;server-after:testmod3;server-before:testmod4");
            assertTrue(info.requirements.isEmpty());
            assertContainsSameToString(info.softRequirements, Sets.newHashSet(mod));
            assertTrue(info.dependants.isEmpty());
            assertTrue(info.dependencies.isEmpty());
        }

        {
            DependencyParser.DependencyInfo info = serverDependencyParser.parseDependencies("server:" + mod);
            assertTrue(info.requirements.isEmpty());
            assertContainsSameToString(info.softRequirements, Sets.newHashSet(mod));
            assertTrue(info.dependants.isEmpty());
            assertTrue(info.dependencies.isEmpty());
        }

        {
            DependencyParser.DependencyInfo info = serverDependencyParser.parseDependencies("client:testmod@[1.0,2.0);client-required:testmod2;client-after:testmod3;client-before:testmod4");
            assertTrue(info.requirements.isEmpty());
            assertTrue(info.softRequirements.isEmpty());
            assertTrue(info.dependants.isEmpty());
            assertTrue(info.dependencies.isEmpty());
        }

        {
            DependencyParser.DependencyInfo info = serverDependencyParser.parseDependencies("server:testmod@[1.0,2.0);client-required:testmod2;client-after:testmod3;client-before:testmod4");
            assertTrue(info.requirements.isEmpty());
            assertContainsSameToString(info.softRequirements, Sets.newHashSet(mod));
            assertTrue(info.dependants.isEmpty());
            assertTrue(info.dependencies.isEmpty());
        }
    }

    @Test(expected = LoaderException.class)
    public void testParsingInvalidDependencyInstructions()
    {
        clientDependencyParser.parseDependencies("gibberishtext:amod");
    }

    @Test(expected = LoaderException.class)
    public void testParsingInvalidDependencyVersion()
    {
        clientDependencyParser.parseDependencies("amod@[10");
    }

    @Test(expected = LoaderException.class)
    public void testParsingUppercaseModId()
    {
        clientDependencyParser.parseDependencies("Forge@[1.0]");
    }

    @Test(expected = LoaderException.class)
    public void testParsingSoftDepWithNoVersion()
    {
        clientDependencyParser.parseDependencies("amod");
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

package net.minecraftforge.fml.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.google.common.base.Functions;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderException;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.VersionParser;

public class ModDependenciesTest
{

    @Test
    public void test()
    {
        Loader.computeDependencies(null, null, null, null);

        Loader.computeDependencies("", null, null, null);

        {
            Set<ArtifactVersion> requirements = new HashSet<ArtifactVersion>();
            Loader.computeDependencies("required:supermod3000@[1.2,)", requirements, null, null);
            assertTrue(equal(Collections2.transform(requirements, Functions.toStringFunction()), Sets.newHashSet("supermod3000@[1.2,)")));
        }

        {
            Set<ArtifactVersion> requirements = new HashSet<ArtifactVersion>();
            List<ArtifactVersion> dependencies = new ArrayList<ArtifactVersion>();
            List<ArtifactVersion> dependants = new ArrayList<ArtifactVersion>();
            Loader.computeDependencies("after:supermod2000[1.3,);required-before:yetanothermod", requirements, dependencies, dependants);
            assertTrue(equal(Collections2.transform(requirements, Functions.toStringFunction()), Sets.newHashSet("yetanothermod")));
            assertTrue(equal(Collections2.transform(dependencies, Functions.toStringFunction()), Sets.newHashSet("supermod2000[1.3,)")));
            assertTrue(equal(Collections2.transform(dependants, Functions.toStringFunction()), Sets.newHashSet("yetanothermod")));
        }
    }

    @Test(expected = LoaderException.class)
    public void testShouldFail()
    {
        Loader.computeDependencies("gibberishtext:amod", new HashSet<ArtifactVersion>(), new ArrayList<ArtifactVersion>(), new ArrayList<ArtifactVersion>());
    }

    @Test(expected = LoaderException.class)
    public void testShouldFail2()
    {
        Loader.computeDependencies("before-required-client:themod", new HashSet<ArtifactVersion>(), new ArrayList<ArtifactVersion>(), new ArrayList<ArtifactVersion>());
    }
    
    public static <T> boolean equal(Collection<T> c1, Collection<T> c2){
        return c1.containsAll(c2) && c2.containsAll(c1);
    }

}

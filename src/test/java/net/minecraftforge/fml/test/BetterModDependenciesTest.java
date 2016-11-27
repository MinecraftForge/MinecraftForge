package net.minecraftforge.fml.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderException;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;

public class BetterModDependenciesTest
{

    @Test
    public void test()
    {
        Loader.computeDependencies(null, null, null, null);

        Loader.computeDependencies("", null, null, null);

        {
            Set<ArtifactVersion> requirements = new HashSet<ArtifactVersion>();
            Loader.computeDependencies("required:supermod3000[1.2,)", requirements, null, null);
            assertFalse(requirements.isEmpty());
        }

        {
            Set<ArtifactVersion> requirements = new HashSet<ArtifactVersion>();
            List<ArtifactVersion> dependencies = new ArrayList<ArtifactVersion>();
            List<ArtifactVersion> dependants = new ArrayList<ArtifactVersion>();
            Loader.computeDependencies("after:supermod2000[1.3,);required-before:yetanothermod", requirements, dependencies, dependants);
            assertFalse(requirements.isEmpty());
            assertFalse(dependencies.isEmpty());
            assertFalse(dependants.isEmpty());
        }
    }

    @Test(expected = Exception.class)
    public void testShouldFail()
    {
        Loader.computeDependencies("gibberishtext:amod", new HashSet<ArtifactVersion>(), new ArrayList<ArtifactVersion>(), new ArrayList<ArtifactVersion>());
    }

    @Test(expected = Exception.class)
    public void testShouldFail2()
    {
        Loader.computeDependencies("before-required-client:themod", new HashSet<ArtifactVersion>(), new ArrayList<ArtifactVersion>(), new ArrayList<ArtifactVersion>());
    }

}

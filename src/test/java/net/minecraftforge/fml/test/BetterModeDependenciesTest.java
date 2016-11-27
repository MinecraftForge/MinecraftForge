package net.minecraftforge.fml.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;

public class BetterModeDependenciesTest
{

    @Test
    public void test()
    {
        try {
            Loader.computeDependencies(null, null, null, null);
        } catch(Exception e){
            fail();
        }
        try {
            Loader.computeDependencies("", null, null, null);
        } catch(Exception e){
            fail();
        }
        
        try {
            Set<ArtifactVersion> requirements = new HashSet<ArtifactVersion>();
            Loader.computeDependencies("required:supermod3000[1.2,)", requirements, null, null);
            assertFalse(requirements.isEmpty());
        } catch(Exception e){
            fail();
        }
        try {
            Set<ArtifactVersion> requirements = new HashSet<ArtifactVersion>();
            List<ArtifactVersion> dependencies = new ArrayList<ArtifactVersion>();
            List<ArtifactVersion> dependants = new ArrayList<ArtifactVersion>();
            Loader.computeDependencies("after:supermod2000[1.3,);required-before:yetanothermod", requirements, dependencies, dependants);
            assertFalse(requirements.isEmpty());
            assertFalse(dependencies.isEmpty());
            assertFalse(dependants.isEmpty());
        } catch(Exception e){
            fail();
        }
        
        try {
            Loader.computeDependencies("gibberishtext:amod", null, null, null);
            fail();
        } catch(Exception e){
            
        }
        try {
            Loader.computeDependencies("before-required-client:themod", null, null, null);
            fail();
        } catch(Exception e){
            
        }
    }

}

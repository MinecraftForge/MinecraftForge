package net.minecraftforge.fml.test;

import net.minecraftforge.fml.language.IModFileInfo;
import net.minecraftforge.fml.loading.moddiscovery.ModFileParser;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ModsTomlTest
{
    @Test
    public void testTomlLoad() throws URISyntaxException
    {
        final URL resource = getClass().getClassLoader().getResource("mods.toml");
        final Path path = Paths.get(resource.toURI());
        final IModFileInfo modFileInfo = ModFileParser.loadModFile(null, path);
        modFileInfo.getMods();
    }

    @Test
    public void testTomlInJar() throws URISyntaxException, IOException
    {
        final URL resource = getClass().getClassLoader().getResource("mod.jar");

        final FileSystem fileSystem = FileSystems.newFileSystem(Paths.get(resource.toURI()), getClass().getClassLoader());
        final Path path = fileSystem.getPath("META-INF", "mods.toml");
        ModFileParser.loadModFile(null, path);
    }
}

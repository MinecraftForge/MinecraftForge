package net.minecraftforge.fml.loading.moddiscovery;

import cpw.mods.jarhandling.SecureJar;
import cpw.mods.niofs.layzip.LayeredZipFileSystemProvider;
import net.minecraftforge.forgespi.locating.IModFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class JarInJarDependencyLocator extends AbstractDependencyLocator
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String JAR_IN_JAR = "JarInJar";

    /**
     * Creates a new instance with the given name.
     *
     * @param name The name.
     */
    protected JarInJarDependencyLocator(final String name)
    {
        super(JAR_IN_JAR);
    }

    @Override
    protected List<DependencyData> determineAvailableDataFrom(List<IModFile> sources) throws IOException, InvalidVersionSpecificationException
    {
        var resultData = new ArrayList<DependencyData>();
        var processingQueue = new LinkedList<DependencyData>();

        for (final IModFile source : sources)
        {
            var modDependencies = determineAvailableDataFrom(source);
            resultData.addAll(modDependencies);
            processingQueue.addAll(modDependencies);
        }

        while (processingQueue.size() > 0) {
            var target = processingQueue.pop();
            var targetDeps = determineAvailableDataFrom(target);
            resultData.addAll(targetDeps);
            processingQueue.addAll(targetDeps);
        }

        return resultData;
    }

    @Override
    protected Path getPathForDependency(final IModFile sourceFile, final String pathInSourceFile)
    {
        final URI sourceFileURI = sourceFile.getFilePath().toUri();
        final URI jijFSUri;
        try
        {
            LOGGER.debug("Creating JiJ FS URI");
            jijFSUri = new URI(LayeredZipFileSystemProvider.SCHEME + ":" + sourceFileURI);
        }
        catch (URISyntaxException e)
        {
            throw new IllegalArgumentException("Failed to create an JiJ Isolation FS for source file: " + sourceFile.getFilePath(), e);
        }

        final FileSystem jijFS;
        try
        {
            LOGGER.debug("Creating JiJ FS from: " + jijFSUri);
            jijFS = FileSystems.newFileSystem(jijFSUri, Map.of());
        }
        catch (IOException e)
        {
            throw new IllegalArgumentException("Failed to create an JiJ Isolation FS for source file: " + sourceFile.getFilePath(), e);
        }

        return jijFS.getPath(pathInSourceFile);
    }

    private List<DependencyData> determineAvailableDataFrom(DependencyData dependencyData) throws IOException, InvalidVersionSpecificationException
    {
        var targetPathInJar = dependencyData.source().getSecureJar().getPath(dependencyData.path().toString());
        if (!Files.exists(targetPathInJar)) {
            LOGGER.warn("%s specifies that is has a dependency: %s which targets file: %s however this file does not exist.".formatted(dependencyData.source().getModFileInfo().moduleName(),
              dependencyData.coordinate(),
              dependencyData.path()));
            return List.of();
        }

        var modFile = ModFile.newFMLInstance(this, SecureJar.from(targetPathInJar));
        return determineAvailableDataFrom(modFile);
    }

    private List<DependencyData> determineAvailableDataFrom(IModFile source) throws IOException, InvalidVersionSpecificationException
    {
        var targetPathInJar = source.getSecureJar().getPath("META-INF", "dependencies", "deps.txt");
        if (!Files.exists(targetPathInJar)) {
            return List.of();
        }

        return determineAvailableDataFrom(
          source,
          Files.readAllLines(
            targetPathInJar
          )
        );
    }

    private List<DependencyData> determineAvailableDataFrom(IModFile source, Iterable<String> data) throws InvalidVersionSpecificationException
    {
        //We support comment lines with a #
        List<DependencyData> list = new ArrayList<>();
        for (String line : data)
        {
            if (!line.startsWith("#"))
            {
                DependencyData from = DependencyData.from(source, line);
                list.add(from);
            }
        }
        return list;
    }
}

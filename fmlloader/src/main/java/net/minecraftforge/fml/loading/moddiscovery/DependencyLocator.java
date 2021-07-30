package net.minecraftforge.fml.loading.moddiscovery;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import cpw.mods.jarhandling.SecureJar;
import net.minecraftforge.forgespi.locating.IDependencyLocator;
import net.minecraftforge.forgespi.locating.IModFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DependencyLocator extends AbstractJarFileProvider implements IDependencyLocator
{
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public List<IModFile> scanMods(final Iterable<IModFile> iterable)
    {
        try
        {
            var scannedData = determineAvailableDataFrom(Lists.newArrayList(iterable));
            var dependencyRangeData = HashBasedTable.<MavenCoordinate, VersionSpec, DependencyData>create();

            for (final DependencyData scannedDatum : scannedData)
            {
                //We take the first one
                if (!dependencyRangeData.contains(
                  scannedDatum.coordinate(),
                  scannedDatum.versionSpec()
                )) {
                    dependencyRangeData.put(
                      scannedDatum.coordinate(),
                      scannedDatum.versionSpec(),
                      scannedDatum
                    );
                }
            }

            var depDataToLoad = new ArrayList<DependencyData>();
            for (final MavenCoordinate depCoord : dependencyRangeData.rowKeySet())
            {
                var availableVersionRanges = dependencyRangeData.row(depCoord).keySet();
                if (availableVersionRanges.size() == 1) {
                    depDataToLoad.add(
                      dependencyRangeData.get(
                        depCoord,
                        availableVersionRanges.iterator().next()
                      )
                    );
                }

                var targetedVersionRange = availableVersionRanges.stream().map(VersionSpec::versionRange).reduce(VersionRange::restrict);
                if (targetedVersionRange.isEmpty())
                    throw new IllegalArgumentException("The given mod list contains a set of dependencies who together do not produce a version range: %s".formatted(depCoord));

                var availableVersionRange = targetedVersionRange.get();
                var selectedDepCandidate = dependencyRangeData.row(
                  depCoord
                ).entrySet()
                  .stream()
                  .filter(entry -> availableVersionRange.containsVersion(new DefaultArtifactVersion(entry.getKey().version())))
                  .map(Map.Entry::getValue)
                  .findFirst();

                if (selectedDepCandidate.isEmpty())
                    throw new IllegalArgumentException("The given mod list contains a set of dependencies who's supplied versions don't match the selected version range: %s, %s".formatted(availableVersionRange, depCoord));

                depDataToLoad.add(selectedDepCandidate.get());
            }

            return depDataToLoad
              .stream()
              .map(depData -> {
                  var targetPathInJar = depData.source().getSecureJar().getPath(depData.path().toString());
                  if (!Files.exists(targetPathInJar)) {
                      throw new IllegalStateException ("%s specifies that is has a dependency: %s which targets file: %s however this file does not exist.".formatted(depData.source().getModFileInfo().moduleName(),
                        depData.coordinate(),
                        depData.path()));
                  }

                  return ModFile.newFMLInstance(this, SecureJar.from(targetPathInJar));
              })
              .collect(Collectors.toList());
        }
        catch (IOException | InvalidVersionSpecificationException e)
        {
            throw new IllegalStateException("Some mod contained illegal dependency data.", e);
        }
    }

    @Override
    public String name()
    {
        return "dependency";
    }

    @Override
    public void initArguments(final Map<String, ?> map)
    {
    }

    private List<DependencyData> determineAvailableDataFrom(List<IModFile> sources) throws IOException, InvalidVersionSpecificationException
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

    private record DependencyData(IModFile source, MavenCoordinate coordinate, VersionSpec versionSpec, Path path) {
        public static DependencyData from(IModFile source, String line) throws InvalidVersionSpecificationException
        {
            var data = line.split(";");
            if (data.length != 4)
                throw new IllegalArgumentException("The given line: %s does not contain dependency data.".formatted(line));

            return new DependencyData(
              source,
              MavenCoordinate.from(data[0]),
              new VersionSpec(
                VersionRange.createFromVersionSpec(data[1]),
                data[2]
              ),
              Path.of(data[3])
            );
        }
    }

    private record MavenCoordinate(String group, String artifactId, String extension, String classifier) {
        public static MavenCoordinate from(String coordinate) {
            final String[] parts = coordinate.split(":");
            final String groupId = parts[0];
            final String artifactId = parts[1];
            final String classifier = parts.length > 3 ? parts[2] : "";
            final String[] versext = parts[parts.length-1].split("@");
            final String extension = versext.length > 1 ? versext[1] : "";
            return new MavenCoordinate(groupId, artifactId, extension, classifier);
        }

        @Override
        public String toString()
        {
            return "%s:%s.%s:%s".formatted(group, artifactId, extension, classifier);
        }
    }

    private record VersionSpec(VersionRange versionRange, String version) {
    }
}

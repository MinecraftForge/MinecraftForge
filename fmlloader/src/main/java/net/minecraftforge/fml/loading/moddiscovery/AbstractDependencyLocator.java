package net.minecraftforge.fml.loading.moddiscovery;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import cpw.mods.jarhandling.SecureJar;
import net.minecraftforge.forgespi.locating.IDependencyLocator;
import net.minecraftforge.forgespi.locating.IModFile;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractDependencyLocator extends AbstractModProvider implements IDependencyLocator
{

    public static final String DEPENDENCY_SUFFIX = " - Dependency";

    /**
     * Creates a new instance with the given name.
     *
     * @param name The name.
     */
    protected AbstractDependencyLocator(final String name)
    {
        super(name + DEPENDENCY_SUFFIX);
    }

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
            for (final MavenCoordinate mavenCoordinate : dependencyRangeData.rowKeySet())
            {
                var availableVersionRanges = dependencyRangeData.row(mavenCoordinate).keySet();
                if (availableVersionRanges.size() == 1) {
                    depDataToLoad.add(
                      dependencyRangeData.get(
                        mavenCoordinate,
                        availableVersionRanges.iterator().next()
                      )
                    );
                }

                var targetedVersionRange = availableVersionRanges.stream().map(VersionSpec::versionRange).reduce(VersionRange::restrict);
                if (targetedVersionRange.isEmpty())
                    throw new IllegalArgumentException("The given mod list contains a set of dependencies who together do not produce a version range: %s".formatted(mavenCoordinate));

                var availableVersionRange = targetedVersionRange.get();
                var selectedDepCandidate = dependencyRangeData.row(
                    mavenCoordinate
                  ).entrySet()
                  .stream()
                  .filter(entry -> availableVersionRange.containsVersion(new DefaultArtifactVersion(entry.getKey().version())))
                  .map(Map.Entry::getValue)
                  .findFirst();

                if (selectedDepCandidate.isEmpty())
                    throw new IllegalArgumentException("The given mod list contains a set of dependencies who's supplied versions don't match the selected version range: %s, %s".formatted(availableVersionRange, mavenCoordinate));

                depDataToLoad.add(selectedDepCandidate.get());
            }

            return depDataToLoad
              .stream()
              .map(depData -> {
                  var targetPathInJar = getPathForDependency(depData.source(), depData.path());
                  if (!Files.exists(targetPathInJar)) {
                      throw new IllegalStateException ("%s specifies that it has a dependency: %s which targets file: %s however this file does not exist.".formatted(depData.source().getModFileInfo().moduleName(),
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

    protected abstract Iterable<DependencyData> determineAvailableDataFrom(final List<IModFile> newArrayList) throws IOException, InvalidVersionSpecificationException;

    protected abstract Path getPathForDependency(final IModFile sourceFile, final String pathInSourceFile);

    protected record DependencyData(IModFile source, MavenCoordinate coordinate, VersionSpec versionSpec, String path) {
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
              data[3]
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

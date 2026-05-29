/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.singularity.tasks;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraftsingularity.jarjar.metadata.ContainedJarIdentifier;
import net.minecraftsingularity.jarjar.metadata.ContainedJarMetadata;
import net.minecraftsingularity.jarjar.metadata.ContainedVersion;
import net.minecraftsingularity.jarjar.metadata.Metadata;
import net.minecraftsingularity.jarjar.metadata.MetadataIOHandler;
import net.minecraftsingularity.jarjar.metadata.json.ArtifactVersionSerializer;
import net.minecraftsingularity.jarjar.metadata.json.ContainedJarIdentifierSerializer;
import net.minecraftsingularity.jarjar.metadata.json.ContainedJarMetadataSerializer;
import net.minecraftsingularity.jarjar.metadata.json.ContainedVersionSerializer;
import net.minecraftsingularity.jarjar.metadata.json.MetadataSerializer;
import net.minecraftsingularity.jarjar.metadata.json.VersionRangeSerializer;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.ExternalModuleDependency;
import org.gradle.api.artifacts.FileCollectionDependency;
import org.gradle.api.artifacts.MinimalExternalModuleDependency;
import org.gradle.api.artifacts.ModuleDependency;
import org.gradle.api.artifacts.ModuleIdentifier;
import org.gradle.api.artifacts.ModuleVersionIdentifier;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.zip.ZipFile;

// TODO SUPER SUPER SUPER UGLY, CLEAN UP IN singularityDEV 7
@Deprecated(forRemoval = true) // Will be moved to JarJar plugin in singularityDev 7
public abstract class JarJarMetadataOptions extends DefaultTask {
    private static final Gson GSON = new GsonBuilder()
        .registerTypeAdapter(VersionRange.class, new VersionRangeSerializer())
        .registerTypeAdapter(ArtifactVersion.class, new ArtifactVersionSerializer())
        .registerTypeAdapter(DefaultArtifactVersion.class, new ArtifactVersionSerializer())
        .registerTypeAdapter(ContainedJarIdentifier.class, new ContainedJarIdentifierSerializer())
        .registerTypeAdapter(ContainedJarMetadata.class, new ContainedJarMetadataSerializer())
        .registerTypeAdapter(ContainedVersion.class, new ContainedVersionSerializer())
        .registerTypeAdapter(Metadata.class, new MetadataSerializer())
        .setPrettyPrinting()
        .create();

    protected abstract @Input SetProperty<ResolvedDependencyInfoImpl> getResolvedDependencies();

    protected abstract @OutputFile RegularFileProperty getMetadataFile();

    protected abstract @Inject ProjectLayout getLayout();

    // NOTE: I'm not adding a non-provider version. please just use the version catalog entries for now.
    public void add(Provider<MinimalExternalModuleDependency> dependency, Action<? super ResolvedDependencyInfo> action) {
        this.getResolvedDependencies().add(dependency.map(d -> {
            var ret = ResolvedDependencyInfoImpl.from(this.getProject().getConfigurations(), d);
            action.execute(ret);
            return ret;
        }));
    }

    @Inject
    public JarJarMetadataOptions() {
        this.getMetadataFile().convention(this.getLayout().getBuildDirectory().file(this.getName() + "/options.json"));
    }

    @TaskAction
    protected void exec() {
        record singularityLocaterOptions(String resource, String layer, String id, List<ContainedJarMetadata> deps, ContainedJarMetadata meta, boolean nested) { }

        var resolved = this.getResolvedDependencies().get();
        var jars = new ArrayList<singularityLocaterOptions>(resolved.size());
        for (var dependency : resolved) {
            var deps = new ArrayList<ContainedJarMetadata>();
            try (var zip = new ZipFile(dependency.artifact)) {
                var entry = zip.getEntry("META-INF/jarjar/metadata.json");
                if (entry != null) {
                    try (var stream = zip.getInputStream(entry)) {
                        var meta = MetadataIOHandler.fromStream(stream).orElse(null);
                        if (meta == null)
                            throw new IllegalStateException("Corrupt metadata.json in " + dependency.artifact.getAbsolutePath());
                        for (var dep : meta.jars())
                            deps.add(new ContainedJarMetadata(dep.identifier(), dep.version(), "", dep.isObfuscated()));
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            jars.add(new singularityLocaterOptions(
                dependency.resource,
                dependency.layer,
                dependency.identifier,
                deps,
                new ContainedJarMetadata(
                    new ContainedJarIdentifier(validateGroup(dependency), dependency.module.getName()),
                    new ContainedVersion(null, parseVersion(dependency)),
                    Objects.requireNonNull(dependency.path, "Dependency path is unspecified: " + dependency.asString),
                    false
                ),
                dependency.nested
            ));
        }

        try {
            record Meta(List<singularityLocaterOptions> options){}

            Files.writeString(
                this.getMetadataFile().getAsFile().get().toPath(),
                GSON.toJson(new Meta(jars), Meta.class)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String validateGroup(ResolvedDependencyInfoImpl dependency) {
        try {
            return Objects.requireNonNull(dependency.module.getGroup());
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Module dependency has no group: " + dependency.asString, e);
        }
    }

    private ArtifactVersion parseVersion(ResolvedDependencyInfoImpl resolved) {
        try {
            return VersionRange.createFromVersionSpec(Objects.requireNonNull(resolved.version)).getRecommendedVersion();
        } catch (InvalidVersionSpecificationException e) {
            throw new IllegalArgumentException("Version is invalid for: " + resolved.asString, e);
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Version is unspecified for: " + resolved.asString, e);
        }
    }

    private VersionRange parseVersionRange(ResolvedDependencyInfoImpl dependency) {
        if (dependency.hasManuallySpecifiedRange) {
            try {
                return VersionRange.createFromVersionSpec(dependency.versionRange);
            } catch (InvalidVersionSpecificationException e) {
                throw new IllegalArgumentException("Version is invalid for: " + dependency.asString, e);
            }
        } else {
            try {
                return VersionRange.createFromVersionSpec("[%s,)".formatted(Objects.requireNonNull(dependency.versionRange)));
            } catch (InvalidVersionSpecificationException e) {
                throw new IllegalArgumentException("Version range is invalid for: " + dependency.asString, e);
            } catch (NullPointerException e) {
                throw new IllegalArgumentException("Version is unspecified for: " + dependency.asString, e);
            }
        }
    }

    public interface ResolvedDependencyInfo {
        void containedJarMetadata(Action<? super ContainedJarMetadataInfo> action);

        void setResource(String resource);

        void setLayer(String layer);

        void setId(String identifier);

        void setNested(boolean nested);

        interface ContainedJarMetadataInfo {
            void setGroup(String group);

            void setName(String name);

            void setPath(String version);
        }
    }

    static final class ResolvedDependencyInfoImpl implements ResolvedDependencyInfo, ResolvedDependencyInfo.ContainedJarMetadataInfo, Serializable {
        private static final @Serial long serialVersionUID = -7577318115877822993L;

        final MinimalModuleVersionIdentifier module;
        final String version;
        String versionRange;
        boolean hasManuallySpecifiedRange;
        boolean nested;
        final File artifact;
        String path;
        String resource;
        String layer;
        String identifier;
        final String asString;

        public ResolvedDependencyInfoImpl(MinimalModuleVersionIdentifier module, String version, File artifact, String asString) {
            this.module = module;
            this.version = version;
            this.artifact = artifact;
            this.asString = asString;
        }

        @Override
        public void containedJarMetadata(Action<? super ContainedJarMetadataInfo> action) {
            action.execute(this);
        }

        @Override
        public void setGroup(String group) {
            this.module.group = group;
        }

        @Override
        public void setName(String name) {
            this.module.name = name;
        }

        @Override
        public void setPath(String path) {
            this.path = path;
        }

        @Override
        public void setResource(String resource) {
            this.resource = resource;
        }

        @Override
        public void setLayer(String layer) {
            this.layer = layer;
        }

        @Override
        public void setId(String identifier) {
            this.identifier = identifier;
        }

        @Override
		public void setNested(boolean nested) {
			this.nested = nested;
		}

        static Set<File> getFiles(Set<ResolvedDependencyInfoImpl> resolvedDependencies) {
            var ret = new HashSet<File>(resolvedDependencies.size());
            for (var dependency : resolvedDependencies) {
                ret.add(dependency.artifact);
            }
            return ret;
        }

        static ResolvedDependencyInfoImpl from(ConfigurationContainer configurations, Dependency dependency) {
            var group = dependency.getGroup();
            var name = dependency.getName();
            var version = dependency.getVersion();

            if (dependency instanceof FileCollectionDependency filesDependency) {
                File artifact;
                try {
                    artifact = filesDependency.getFiles().getSingleFile();
                } catch (IllegalStateException e) {
                    // TODO fileCollectionDependencyIsNotSingleFile
                    throw e;
                }

                return new ResolvedDependencyInfoImpl(
                    new MinimalModuleVersionIdentifier(group, name, version),
                    version,
                    artifact,
                    filesDependency.toString()
                );
            } else if (dependency instanceof ModuleDependency moduleDependency) {
                moduleDependency = moduleDependency.copy();
                if (moduleDependency instanceof ExternalModuleDependency externalModuleDependency) {
                    externalModuleDependency.version(v -> v.strictly(version.toString()));
                }

                var detachedConfiguration = configurations.detachedConfiguration(moduleDependency);
                detachedConfiguration.setTransitive(false);

                ResolvedDependencyInfoImpl ret = null;
                for (var artifact : detachedConfiguration.getResolvedConfiguration().getFirstLevelModuleDependencies().iterator().next().getModuleArtifacts()) {
                    var fileName = getFileName(artifact);
                    if (!fileName.endsWith(".jar"))
                        continue;

                    if (ret != null)
                        throw new IllegalArgumentException("Module dependency has too many Jar artifacts: " + moduleDependency);

                    ret = new ResolvedDependencyInfoImpl(
                        new MinimalModuleVersionIdentifier(group, name, artifact.getModuleVersion().getId().getVersion()),
                        version,
                        artifact.getFile(),
                        moduleDependency.toString()
                    );
                }
                if (ret == null)
                    throw new IllegalArgumentException("Module dependency has no Jar artifacts: " + moduleDependency);

                return ret;
            } else {
                throw new IllegalArgumentException("Unsupported dependency type: " + dependency.getClass().getName() + " -- " + dependency);
            }
        }

        private static String getFileName(ResolvedArtifact artifact) {
            try {
                return InvokerHelper.getProperty(artifact.getId(), "fileName").toString();
            } catch (Throwable e) {
                // NOTE: Why not just use this to begin with?
                // ComponentArtifactIdentifier can have a getFileName() method, which doesn't necessarily resolve the file itself.
                // This allows us to get the name of the file to be used without asking Gradle to download the file.
                // So, if a file is not a JAR file, we can check the name without actually downloading it.
                return artifact.getFile().getName();
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (ResolvedDependencyInfoImpl) obj;
            return Objects.equals(this.module, that.module) &&
                Objects.equals(this.version, that.version) &&
                Objects.equals(this.versionRange, that.versionRange) &&
                Objects.equals(this.artifact, that.artifact);
        }

        @Override
        public int hashCode() {
            return Objects.hash(module, version, versionRange, artifact);
        }

        @Override
        public String toString() {
            return "ResolvedDependencyInfo[" +
                "module=" + module + ", " +
                "fixedVersion=" + version + ", " +
                "versionRange=" + versionRange + ", " +
                "artifact=" + artifact + ']';
        }

        static final class MinimalModuleVersionIdentifier implements ModuleIdentifier, ModuleVersionIdentifier {
            private static final @Serial long serialVersionUID = -955346236759069739L;

            private String group;
            private String name;
            private final String version;

            @Inject
            public MinimalModuleVersionIdentifier(String group, String name, String version) {
                this.group = group;
                this.name = name;
                this.version = version;
            }

            @Override
            public ModuleIdentifier getModule() {
                return this;
            }

            @Override
            public String getGroup() {
                return this.group;
            }

            @Override
            public String getName() {
                return this.name;
            }

            @Override
            public String getVersion() {
                return this.version;
            }

            @Override
            public boolean equals(Object obj) {
                return this == obj || obj instanceof MinimalModuleVersionIdentifier o
                    && Objects.equals(this.group, o.group)
                    && Objects.equals(this.name, o.name)
                    && Objects.equals(this.version, o.version);
            }

            @Override
            public int hashCode() {
                return Objects.hash(group, name, version);
            }

            @Override
            public String toString() {
                return "MinimalModuleVersionIdentifier[" +
                    "group=" + group + ", " +
                    "name=" + name + ", " +
                    "version=" + version + ']';
            }
        }
    }
}

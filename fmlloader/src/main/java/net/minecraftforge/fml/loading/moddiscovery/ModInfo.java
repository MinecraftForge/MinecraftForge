/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.moddiscovery;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.loading.StringSubstitutor;
import net.minecraftforge.fml.loading.StringUtils;
import net.minecraftforge.forgespi.language.IConfigurable;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.language.MavenVersionAdapter;
import net.minecraftforge.forgespi.locating.ForgeFeature;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@ApiStatus.Internal
public record ModInfo(
        ModFileInfo getOwningFile,
        IConfigurable getConfig,

        String getModId,
        String getNamespace,
        ArtifactVersion getVersion,

        String getDisplayName,
        String getDescription,
        Optional<String> getLogoFile,
        boolean getLogoBlur,
        Optional<URL> getUpdateURL,
        Optional<URL> getModURL,

        Holder<List<? extends ModVersion>> dependencies,
        Holder<List<ForgeFeature.Bound>> forgeFeatures,
        Map<String, Object> getModProperties
) implements IModInfo, IConfigurable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final DefaultArtifactVersion DEFAULT_VERSION = new DefaultArtifactVersion("1");
    private static final Pattern VALID_MODID = Pattern.compile("^[a-z][a-z0-9_]{1,63}$");
    private static final Pattern VALID_NAMESPACE = Pattern.compile("^[a-z][a-z0-9_.-]{1,63}$");
    private static final Pattern VALID_VERSION = Pattern.compile("^\\d+.*");

    public static ModInfo of(ModFileInfo owningFile, IConfigurable config) {
        // These are sourced from the mod specific [[mod]] entry
        String modId = config.<String>getConfigElement("modId")
                .orElseThrow(() -> new InvalidModFileException("Missing modId", owningFile));

        // verify we have a valid modid
        if (!VALID_MODID.matcher(modId).matches()) {
            LOGGER.error(LogUtils.FATAL_MARKER, "Invalid modId found in file {} - {} does not match the standard: {}", owningFile.getFile().getFilePath(), modId, VALID_MODID.pattern());
            throw new InvalidModFileException("Invalid modId found : " + modId, owningFile);
        }

        String namespace = config.<String>getConfigElement("namespace")
                .orElse(modId);

        // verify our namespace is valid
        if (!VALID_NAMESPACE.matcher(namespace).matches()) {
            LOGGER.error(LogUtils.FATAL_MARKER, "Invalid override namespace found in file {} - {} does not match the standard: {}", owningFile.getFile().getFilePath(), namespace, VALID_NAMESPACE.pattern());
            throw new InvalidModFileException("Invalid override namespace found : " + namespace, owningFile);
        }

        ArtifactVersion version = config.<String>getConfigElement("version")
                .map(s -> StringSubstitutor.replace(s, owningFile.getFile()))
                .map(DefaultArtifactVersion::new)
                .orElse(DEFAULT_VERSION);

        // verify we have a valid mod version
        if (!VALID_VERSION.matcher(version.toString()).matches())
            throw new InvalidModFileException("Illegal version number specified " + version, owningFile);

        // The remaining properties all default to sensible values and are not essential
        String displayName = config.<String>getConfigElement("displayName")
                .orElse(modId);

        String description = config.<String>getConfigElement("description")
                .orElse("MISSING DESCRIPTION")
                .replace("\r\n", "\n").stripIndent();

        Optional<String> logoFile = Optional.ofNullable(
                config.<String>getConfigElement("logoFile")
                        .orElseGet(() -> owningFile.<String>getConfigElement("logoFile").orElse(null))
        );

        boolean logoBlur = config.<Boolean>getConfigElement("logoBlur")
                .orElseGet(() -> owningFile.<Boolean>getConfigElement("logoBlur").orElse(true));

        Optional<URL> updateJSONURL = config.<String>getConfigElement("updateJSONURL")
                .map(StringUtils::toURL);

        Optional<URL> modUrl = config.<String>getConfigElement("modUrl")
                .map(StringUtils::toURL);

        // dependencies and features are done after the constructor as they need to reference the ModInfo we are creating
        List<? extends ModVersion> dependencies = Collections.emptyList();
        List<ForgeFeature.Bound> forgeFeatures = Collections.emptyList();

        Map<String, Object> modProperties = owningFile.<Map<String, Object>>getConfigElement("modproperties", modId)
                .map(Collections::unmodifiableMap)
                .orElse(Collections.emptyMap());

        return new ModInfo(
                owningFile, config,
                modId, namespace, version,
                displayName, description, logoFile, logoBlur, updateJSONURL, modUrl,
                new Holder<>(dependencies), new Holder<>(forgeFeatures), modProperties
        ).setupDependencies().setupForgeFeatures();
    }

    private ModInfo setupDependencies() {
        var deps = getOwningFile.getConfigList("dependencies", getModId);
        if (deps == null || deps.isEmpty()) {
            dependencies.value = Collections.emptyList();
        } else {
            var tmp = new ModVersion[deps.size()];
            for (int i = 0; i < deps.size(); i++) {
                tmp[i] = ModVersion.of(this, deps.get(i));
            }
            dependencies.value = List.of(tmp);
        }
        return this;
    }

    private ModInfo setupForgeFeatures() {
        var feats = getOwningFile.<Map<String, Object>>getConfigElement("features", getModId).orElse(null);
        if (feats == null) {
            forgeFeatures.value = Collections.emptyList();
        } else {
            var tmp = new ArrayList<ForgeFeature.Bound>();
            for (var entry : feats.entrySet()) {
                if (!(entry.getValue() instanceof String val))
                    throw new InvalidModFileException("Invalid feature bound {" + entry.getValue() + "} for key {" + entry.getKey() + "} only strings are accepted", getOwningFile);
                tmp.add(new ForgeFeature.Bound(entry.getKey(), val, this));
            }
            forgeFeatures.value = List.copyOf(tmp);
        }
        return this;
    }

    @Override
    public <T> Optional<T> getConfigElement(String key) {
        return getConfig.getConfigElement(key);
    }

    @Override
    public <T> Optional<T> getConfigElement(String... key) {
        return getConfig.getConfigElement(key);
    }

    @Override
    public List<? extends IConfigurable> getConfigList(String... key) {
        return null;
    }

    @Override
    public List<? extends IModInfo.ModVersion> getDependencies() {
        return dependencies.value;
    }

    @Override
    public List<? extends ForgeFeature.Bound> getForgeFeatures() {
        return forgeFeatures.value;
    }

    private static final class Holder<T> {
        private T value;

        public Holder(T value) {
            this.value = value;
        }
    }

    record ModVersion(
            Holder<IModInfo> owner,

            String getModId,
            VersionRange getVersionRange,
            boolean isMandatory,
            Ordering getOrdering,
            DependencySide getSide,
            Optional<URL> getReferralURL
    ) implements IModInfo.ModVersion {
        public static ModVersion of(IModInfo owner, IConfigurable config) {
            var modId = config.<String>getConfigElement("modId")
                    .orElseThrow(()->new InvalidModFileException("Missing required field modid in dependency", owner.getOwningFile()));

            if (modId.equals("forge")) {
                var fileProps = owner.getOwningFile().getFileProperties();
                // Checking containsKey to avoid a possible exception if the property is not present (due to Collections.emptyMap())
                if (!fileProps.isEmpty() && fileProps.containsKey(ModFileInfo.NOT_A_FORGE_MOD_PROP)) {
                    // if the mod has a dependency on Forge, but we thought it wasn't a Forge mod earlier, we were wrong
                    // so remove the flag.
                    fileProps.remove(ModFileInfo.NOT_A_FORGE_MOD_PROP);
                }
            }

            boolean mandatory;
            var mandatoryValue = config.<Boolean>getConfigElement("mandatory");
            if (mandatoryValue.isPresent())
                mandatory = mandatoryValue.get();
            else if (owner.getOwningFile().getFileProperties().containsKey(ModFileInfo.NOT_A_FORGE_MOD_PROP))
                mandatory = true;
            else
                throw new InvalidModFileException("Missing required field mandatory in dependency", owner.getOwningFile());

            var versionRange = config.<String>getConfigElement("versionRange")
                    .map(MavenVersionAdapter::createFromVersionSpec)
                    .orElse(UNBOUNDED);
            var ordering = config.<String>getConfigElement("ordering")
                    .map(Ordering::valueOf)
                    .orElse(Ordering.NONE);
            var side = config.<String>getConfigElement("side")
                    .map(DependencySide::valueOf)
                    .orElse(DependencySide.BOTH);
            var referralUrl = config.<String>getConfigElement("referralUrl")
                    .map(StringUtils::toURL);

            return new ModVersion(
                    new Holder<>(owner),
                    modId, versionRange, mandatory, ordering, side, referralUrl
            );
        }

        @Override
        public IModInfo getOwner() {
            return owner.value;
        }

        @Override
        public void setOwner(IModInfo newOwner) {
            owner.value = newOwner;
        }
    }
}

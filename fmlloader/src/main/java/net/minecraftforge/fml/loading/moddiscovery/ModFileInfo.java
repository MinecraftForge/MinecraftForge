/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.moddiscovery;

import com.google.common.base.Strings;
import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.loading.LogMarkers;
import net.minecraftforge.fml.loading.StringUtils;
import net.minecraftforge.forgespi.language.IConfigurable;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.language.MavenVersionAdapter;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import javax.security.auth.x500.X500Principal;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ModFileInfo implements IModFileInfo, IConfigurable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String MAYBE_NOT_JAVAFML_VER = "[2,)";
    private static final String JAVAFML = "javafml";

    @ApiStatus.Internal
    public static final String NOT_A_FORGE_MOD_PROP = "__FORGE__not_a_forge_mod";

    private final IConfigurable config;
    private final ModFile modFile;
    private final URL issueURL;
    private final List<LanguageSpec> languageSpecs;
    private final boolean showAsResourcePack;
    private final boolean clientSideOnly;
    private final boolean showAsDataPack;
    private final List<IModInfo> mods;
    private final Map<String,Object> properties;
    private final String license;
    private final List<String> usesServices;

    ModFileInfo(final ModFile modFile, final IConfigurable config, Consumer<IModFileInfo> configFileConsumer) {
        this.modFile = modFile;
        this.config = config;
        configFileConsumer.accept(this);
        // modloader is essential
        var modLoader = config.<String>getConfigElement("modLoader")
                .orElseThrow(()->new InvalidModFileException("Missing ModLoader in file", this));
        // as is modloader version
        var modLoaderVerStr = config.<String>getConfigElement("loaderVersion")
                .orElseThrow(()->new InvalidModFileException("Missing ModLoader version in file", this));
        var modLoaderVersion = MavenVersionAdapter.createFromVersionSpec(modLoaderVerStr);
        this.languageSpecs = new ArrayList<>(List.of(new LanguageSpec(modLoader, modLoaderVersion)));

        // if true, this might not be a Forge mod
        boolean maybeNotAForgeMod = modLoader.equals(JAVAFML)
                && modLoaderVersion.hasRestrictions() // if loaderVersion is not "*"
                && modLoaderVerStr.equals(MAYBE_NOT_JAVAFML_VER); // if loaderVersion is exactly "[2,)"

        // the remaining properties are optional with sensible defaults
        this.license = config.<String>getConfigElement("license")
                .orElse("");
        this.showAsResourcePack = config.<Boolean>getConfigElement("showAsResourcePack")
                .orElse(false);
        this.clientSideOnly = config.<Boolean>getConfigElement("clientSideOnly")
                .orElse(false);
        this.showAsDataPack = config.<Boolean>getConfigElement("showAsDataPack")
                .orElse(false);
        this.usesServices = config.<List<String>>getConfigElement("services")
                .orElse(List.of());
        this.issueURL = config.<String>getConfigElement("issueTrackerURL")
                .map(StringUtils::toURL)
                .orElse(null);

        // if clientSideOnly, then this is definitely a Forge mod
        if (this.clientSideOnly)
            maybeNotAForgeMod = false;

        if (maybeNotAForgeMod) {
            // mark this file as not a Forge mod. Note: this marker may be removed later based on the mod dependencies
            this.properties = config.<Map<String, Object>>getConfigElement("properties")
                    .orElse(new LinkedHashMap<>());
            this.properties.put(NOT_A_FORGE_MOD_PROP, true);
        } else {
            this.properties = config.<Map<String, Object>>getConfigElement("properties")
                    .orElse(Collections.emptyMap());
        }
        this.modFile.setFileProperties(this.properties);

        final List<? extends IConfigurable> modConfigs = config.getConfigList("mods");
        if (modConfigs == null || modConfigs.isEmpty())
            throw new InvalidModFileException("Missing mods list", this);

        this.mods = modConfigs.stream()
                .map(mi -> (IModInfo) ModInfo.of(this, mi))
                .toList();

        if (LOGGER.isDebugEnabled(LogMarkers.LOADING)) {
            LOGGER.debug(LogMarkers.LOADING, "Found valid mod file {} with {} mods - versions {}",
                    this.modFile.getFileName(),
                    this.mods.stream().map(IModInfo::getModId).collect(Collectors.joining(",", "{", "}")),
                    this.mods.stream().map(IModInfo::getVersion).map(Objects::toString).collect(Collectors.joining(",", "{", "}")));
        }
    }

    public ModFileInfo(final ModFile file, final IConfigurable config, Consumer<IModFileInfo> configFileConsumer, final List<LanguageSpec> languageSpecs) {
        this(file, config, configFileConsumer);
        this.languageSpecs.addAll(languageSpecs);
    }

    @Override
    public List<IModInfo> getMods() {
        return mods;
    }

    public ModFile getFile() {
        return this.modFile;
    }

    @Override
    public List<LanguageSpec> requiredLanguageLoaders() {
        return this.languageSpecs;
    }

    @Override
    public Map<String, Object> getFileProperties() {
        return this.properties;
    }

    @Override
    public boolean showAsResourcePack() {
        return this.showAsResourcePack;
    }

    @Override
    public boolean isClientSideOnly() {
        return this.clientSideOnly;
    }

    @Override
    public boolean showAsDataPack() {
        return this.showAsDataPack;
    }

    @Override
    public <T> Optional<T> getConfigElement(final String key) {
        return this.config.getConfigElement(key);
    }

    @Override
    public <T> Optional<T> getConfigElement(final String... key) {
        return this.config.getConfigElement(key);
    }

    @Override
    public List<? extends IConfigurable> getConfigList(final String... key) {
        return this.config.getConfigList(key);
    }

    @Override
    public String getLicense() {
        return license;
    }

    @Override
    public IConfigurable getConfig() {
        return this;
    }

    public URL getIssueURL() {
        return issueURL;
    }

    public boolean missingLicense() {
        return Strings.isNullOrEmpty(license);
    }

    private static final char[] HEX = "0123456789ABCDEF".toCharArray();
    public Optional<String> getCodeSigningFingerprint() {
        var signers = this.modFile.getSecureJar().getManifestSigners();
        if (signers == null)
            return Optional.empty();

        for (var signer : signers) {
            for (var cert : signer.getSignerCertPath().getCertificates()) {
                try {
                    var encoded = cert.getEncoded();
                    var sha256 = MessageDigest.getInstance("SHA-256");
                    var digest = sha256.digest(encoded);
                    var buf = new StringBuilder(digest.length * 3);
                    for (int x = 0; x < digest.length - 1; x++) {
                        buf.append(HEX[(digest[x] & 0xF0) >> 4]);
                        buf.append(HEX[(digest[x] & 0x0F)     ]);
                        buf.append(':');
                    }
                    buf.append(HEX[(digest[digest.length - 1] & 0xF0) >> 4]);
                    buf.append(HEX[(digest[digest.length - 1] & 0x0F)     ]);

                    // We only care about the first. Should we list them all?
                    return Optional.of(buf.toString());
                } catch (CertificateEncodingException | NoSuchAlgorithmException e) {
                    sneak(e);
                }
            }
        }

        return Optional.empty();

    }

    public Optional<String> getTrustData() {
        var signers = this.modFile.getSecureJar().getManifestSigners();
        if (signers == null)
            return Optional.empty();

        for (var signer : signers) {
            for (var cert : signer.getSignerCertPath().getCertificates()) {
                if (!(cert instanceof X509Certificate x509))
                    continue;

                var sb = new StringBuilder();
                sb.append(x509.getSubjectX500Principal().getName(X500Principal.RFC2253).split(",")[0]);

                var selfSigned = false;
                try {
                    x509.verify(x509.getPublicKey());
                    selfSigned = true;
                } catch (CertificateException | NoSuchAlgorithmException | InvalidKeyException | SignatureException | NoSuchProviderException e) {
                    // not self signed
                }

                if (selfSigned)
                    sb.append(" self-signed");
                else
                    sb.append(" signed by ").append(x509.getIssuerX500Principal().getName(X500Principal.RFC2253).split(",")[0]);

               return Optional.of(sb.toString());
            }
        }

        return Optional.empty();
    }

    @Override
    public String moduleName() {
        return this.modFile.getSecureJar().name();
    }

    @Override
    public String versionString()  {
        return getMods().get(0).getVersion().toString();
    }

    @Override
    public List<String> usesServices() {
        return usesServices;
    }

    @SuppressWarnings("unchecked")
    private static <E extends Throwable, R> R sneak(Throwable e) throws E {
        throw (E)e;
    }
}

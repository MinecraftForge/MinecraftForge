/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.moddiscovery;

import com.google.common.base.Strings;
import com.mojang.logging.LogUtils;
import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import net.minecraftforge.fml.loading.LogMarkers;
import net.minecraftforge.fml.loading.StringUtils;
import net.minecraftforge.forgespi.language.IConfigurable;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.language.MavenVersionAdapter;
import org.slf4j.Logger;
import javax.security.auth.x500.X500Principal;
import java.net.URL;
import java.security.CodeSigner;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModFileInfo implements IModFileInfo, IConfigurable
{
    private static final Logger LOGGER = LogUtils.getLogger();
    private final IConfigurable config;
    private final ModFile modFile;
    private final URL issueURL;
    private final List<LanguageSpec> languageSpecs;
    private final boolean showAsResourcePack;
    private final List<IModInfo> mods;
    private final Map<String,Object> properties;
    private final String license;
    private final List<String> usesServices;

    ModFileInfo(final ModFile modFile, final IConfigurable config, Consumer<IModFileInfo> configFileConsumer)
    {
        this.modFile = modFile;
        this.config = config;
        configFileConsumer.accept(this);
        // modloader is essential
        var modLoader = config.<String>getConfigElement("modLoader")
                .orElseThrow(()->new InvalidModFileException("Missing ModLoader in file", this));
        // as is modloader version
        var modLoaderVersion = config.<String>getConfigElement("loaderVersion")
                .map(MavenVersionAdapter::createFromVersionSpec)
                .orElseThrow(()->new InvalidModFileException("Missing ModLoader version in file", this));
        this.languageSpecs = new ArrayList<>(List.of(new LanguageSpec(modLoader, modLoaderVersion)));
        // the remaining properties are optional with sensible defaults
        this.license = config.<String>getConfigElement("license")
                .orElse("");
        this.showAsResourcePack = config.<Boolean>getConfigElement("showAsResourcePack")
                .orElse(false);
        this.usesServices = config.<List<String>>getConfigElement("services")
                .orElse(List.of());
        this.properties = config.<Map<String, Object>>getConfigElement("properties")
                .orElse(Collections.emptyMap());
        this.modFile.setFileProperties(this.properties);
        this.issueURL = config.<String>getConfigElement("issueTrackerURL")
                .map(StringUtils::toURL)
                .orElse(null);
        final List<? extends IConfigurable> modConfigs = config.getConfigList("mods");
        if (modConfigs.isEmpty())
        {
            throw new InvalidModFileException("Missing mods list", this);
        }
        this.mods = modConfigs.stream()
                .map(mi-> (IModInfo)new ModInfo(this, mi))
                .toList();
        if (LOGGER.isDebugEnabled(LogMarkers.LOADING))
        {
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
    public List<IModInfo> getMods()
    {
        return mods;
    }

    public ModFile getFile()
    {
        return this.modFile;
    }

    @Override
    public List<LanguageSpec> requiredLanguageLoaders() {
        return this.languageSpecs;
    }

    @Override
    public Map<String, Object> getFileProperties()
    {
        return this.properties;
    }

    @Override
    public boolean showAsResourcePack()
    {
        return this.showAsResourcePack;
    }

    @Override
    public <T> Optional<T> getConfigElement(final String... key)
    {
        return this.config.getConfigElement(key);
    }

    @Override
    public List<? extends IConfigurable> getConfigList(final String... key)
    {
        return this.config.getConfigList(key);
    }

    @Override
    public String getLicense()
    {
        return license;
    }

    @Override
    public IConfigurable getConfig() {
        return this;
    }

    public URL getIssueURL()
    {
        return issueURL;
    }

    public boolean missingLicense()
    {
        return Strings.isNullOrEmpty(license);
    }

    public Optional<String> getCodeSigningFingerprint() {
        var signers = this.modFile.getSecureJar().getManifestSigners();
        return (signers == null ? Stream.<CodeSigner>of() : Arrays.stream(signers))
                .flatMap(csa->csa.getSignerCertPath().getCertificates().stream())
                .findFirst()
                .map(LamdbaExceptionUtils.rethrowFunction(Certificate::getEncoded))
                .map(bytes->LamdbaExceptionUtils.uncheck(()->MessageDigest.getInstance("SHA-256")).digest(bytes))
                .map(StringUtils::binToHex)
                .map(str-> String.join(":", str.split("(?<=\\G.{2})")));
    }

    public Optional<String> getTrustData() {
        return Arrays.stream(this.modFile.getSecureJar().getManifestSigners())
                .flatMap(csa->csa.getSignerCertPath().getCertificates().stream())
                .findFirst()
                .map(X509Certificate.class::cast)
                .map(c->{
                    StringBuffer sb = new StringBuffer();
                    sb.append(c.getSubjectX500Principal().getName(X500Principal.RFC2253).split(",")[0]);
                    boolean selfSigned = false;
                   try {
                       c.verify(c.getPublicKey());
                       selfSigned = true;
                   } catch (CertificateException | NoSuchAlgorithmException | InvalidKeyException | SignatureException | NoSuchProviderException e) {
                       // not self signed
                   }
                   if (selfSigned) {
                    sb.append(" self-signed");
                   } else {
                       sb.append(" signed by ").append(c.getIssuerX500Principal().getName(X500Principal.RFC2253).split(",")[0]);
                   };
                   return sb.toString();
                });
    }

    @Override
    public String moduleName() {
        return getMods().get(0).getModId();
    }

    @Override
    public String versionString()  {
        return getMods().get(0).getVersion().toString();
    }

    @Override
    public List<String> usesServices() {
        return usesServices;
    }
}

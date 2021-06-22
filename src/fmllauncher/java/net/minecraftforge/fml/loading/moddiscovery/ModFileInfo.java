/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.fml.loading.moddiscovery;

import com.google.common.base.Strings;
import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import net.minecraftforge.fml.loading.Java9BackportUtils;
import net.minecraftforge.fml.loading.StringUtils;
import net.minecraftforge.forgespi.language.IConfigurable;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.language.MavenVersionAdapter;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.VersionRange;

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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

import static net.minecraftforge.fml.loading.LogMarkers.LOADING;

public class ModFileInfo implements IModFileInfo, IConfigurable
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final IConfigurable config;
    private final ModFile modFile;
    private final URL issueURL;
    private final String modLoader;
    private final VersionRange modLoaderVersion;
    private final boolean showAsResourcePack;
    private final List<IModInfo> mods;
    private final Map<String,Object> properties;
    private final String license;
    // Caches the manifest of the mod jar as parsing the manifest can be expensive for
    // signed jars.
    private final Optional<Manifest> manifest;
    private final Optional<CodeSigner[]> signers;

    ModFileInfo(final ModFile modFile, final IConfigurable config)
    {
        this.modFile = modFile;
        this.config = config;
        this.modLoader = config.<String>getConfigElement("modLoader")
                .orElseThrow(()->new InvalidModFileException("Missing ModLoader in file", this));
        this.modLoaderVersion = config.<String>getConfigElement("loaderVersion")
                .map(MavenVersionAdapter::createFromVersionSpec)
                .orElseThrow(()->new InvalidModFileException("Missing ModLoader version in file", this));
        this.license = config.<String>getConfigElement("license")
            .orElse("");
        this.showAsResourcePack = config.<Boolean>getConfigElement("showAsResourcePack").orElse(false);
        this.properties = config.<Map<String, Object>>getConfigElement("properties").orElse(Collections.emptyMap());
        this.modFile.setFileProperties(this.properties);
        this.issueURL = config.<String>getConfigElement("issueTrackerURL").map(StringUtils::toURL).orElse(null);
        final List<? extends IConfigurable> modConfigs = config.getConfigList("mods");
        if (modConfigs.isEmpty())
        {
            throw new InvalidModFileException("Missing mods list", this);
        }
        this.mods = modConfigs.stream()
                .map(mi-> new ModInfo(this, mi))
                .collect(Collectors.toList());
        LOGGER.debug(LOADING, "Found valid mod file {} with {} mods - versions {}",
                this.modFile::getFileName,
                () -> this.mods.stream().map(IModInfo::getModId).collect(Collectors.joining(",", "{", "}")),
                () -> this.mods.stream().map(IModInfo::getVersion).map(Objects::toString).collect(Collectors.joining(",", "{", "}")));
        final Pair<Optional<Manifest>, Optional<CodeSigner[]>> manifestAndSigners = modFile.getLocator().findManifestAndSigners(modFile.getFilePath());
        this.manifest = manifestAndSigners.getKey();
        this.signers = manifestAndSigners.getValue();
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
    public String getModLoader()
    {
        return modLoader;
    }

    @Override
    public VersionRange getModLoaderVersion()
    {
        return modLoaderVersion;
    }

    @Override
    public Map<String, Object> getFileProperties()
    {
        return this.properties;
    }

    public Optional<Manifest> getManifest()
    {
        return manifest;
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

    public URL getIssueURL()
    {
        return issueURL;
    }

    public boolean missingLicense()
    {
        return Strings.isNullOrEmpty(license);
    }

    /* This data can only be trusted for the manifest and signature file itself. We do not validate each entry in the jar. Coremods exists, it would be useless */
    public Optional<CodeSigner[]> getCodeSigners() {
        return this.signers;
    }

    /* This data can only be trusted for the manifest and signature file itself. We do not validate each entry in the jar. Coremods exists, it would be useless */
    public Optional<String> getCodeSigningFingerprint() {
        return Java9BackportUtils.toStream(this.signers)
                .flatMap(csa->csa[0].getSignerCertPath().getCertificates().stream())
                .findFirst()
                .map(LamdbaExceptionUtils.rethrowFunction(Certificate::getEncoded))
                .map(bytes->LamdbaExceptionUtils.uncheck(()->MessageDigest.getInstance("SHA-256")).digest(bytes))
                .map(StringUtils::binToHex)
                .map(str-> String.join(":", str.split("(?<=\\G.{2})")));
    }

    /* This data can only be trusted for the manifest and signature file itself. We do not validate each entry in the jar. Coremods exists, it would be useless */
    public Optional<String> getTrustData() {
        return Java9BackportUtils.toStream(this.signers)
                .flatMap(csa->csa[0].getSignerCertPath().getCertificates().stream())
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
}

/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.fml.loading;

import org.apache.logging.log4j.LogManager;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import static cpw.mods.modlauncher.api.LamdbaExceptionUtils.rethrowBiConsumer;
import static cpw.mods.modlauncher.api.LamdbaExceptionUtils.rethrowFunction;
import static net.minecraftforge.fml.loading.LogMarkers.CORE;


/**
 * This class fixes older Java SSL setups which don't contain the correct root certificates to trust Let's Encrypt
 * https endpoints.
 *
 * It uses a secondary JKS keystore: lekeystore.jks, which contains the two root certificate keys as documented here:
 * <a href="https://letsencrypt.org/certificates/">https://letsencrypt.org/certificates/</a>
 *
 * To create the keystore, the following commands were run:
 * <pre>
 *     keytool -import -alias letsencryptisrgx1 -file isrgrootx1.pem -keystore lekeystore.jks -storetype jks -storepass supersecretpassword -v
 *     keytool -import -alias identrustx3 -file identrustx3.pem -keystore lekeystore.jks -storetype jks -storepass supersecretpassword -v
 * </pre>
 *
 * The PEM files were obtained from the above URL.
 */
class FixSSL {
    static void fixup() {
        try {
            final KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            Path ksPath = Paths.get(System.getProperty("java.home"),"lib", "security", "cacerts");
            keyStore.load(Files.newInputStream(ksPath), "changeit".toCharArray());
            final Map<String, Certificate> jdkTrustStore = Collections.list(keyStore.aliases()).stream().collect(Collectors.toMap(a -> a, rethrowFunction(keyStore::getCertificate)));

            final KeyStore leKS = KeyStore.getInstance(KeyStore.getDefaultType());
            final InputStream leKSFile = FixSSL.class.getResourceAsStream("/lekeystore.jks");
            leKS.load(leKSFile, "supersecretpassword".toCharArray());
            final Map<String, Certificate> leTrustStore = Collections.list(leKS.aliases()).stream().collect(Collectors.toMap(a -> a, rethrowFunction(leKS::getCertificate)));

            final KeyStore mergedTrustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            mergedTrustStore.load(null, new char[0]);
            jdkTrustStore.forEach(rethrowBiConsumer(mergedTrustStore::setCertificateEntry));
            leTrustStore.forEach(rethrowBiConsumer(mergedTrustStore::setCertificateEntry));

            final TrustManagerFactory instance = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            instance.init(mergedTrustStore);
            final SSLContext tls = SSLContext.getInstance("TLS");
            tls.init(null, instance.getTrustManagers(), null);
            HttpsURLConnection.setDefaultSSLSocketFactory(tls.getSocketFactory());
            LogManager.getLogger().info(CORE, "Added Lets Encrypt root certificates as additional trust");
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException | KeyManagementException e) {
            LogManager.getLogger().fatal(CORE,"Failed to load lets encrypt certificate. Expect problems", e);
        }

    }
}

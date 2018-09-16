/*
 * Minecraft Forge
 * Copyright (c) 2018.
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

import net.minecraftforge.fml.language.ModFileScanData;
import net.minecraftforge.fml.loading.LoadingModList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static net.minecraftforge.fml.Logging.SCAN;

public class BackgroundScanHandler
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final ExecutorService modContentScanner;
    private final List<ModFile> pendingFiles;
    private final List<ModFile> scannedFiles;
    private final List<ModFile> allFiles;
    private LoadingModList loadingModList;

    public BackgroundScanHandler() {
        modContentScanner = Executors.newSingleThreadExecutor(r -> {
            final Thread thread = Executors.defaultThreadFactory().newThread(r);
            thread.setDaemon(true);
            return thread;
        });
        scannedFiles = new ArrayList<>();
        pendingFiles = new ArrayList<>();
        allFiles = new ArrayList<>();
    }

    public void submitForScanning(final ModFile file) {
        if (modContentScanner.isShutdown()) {
            throw new IllegalStateException("Scanner has shutdown");
        }
        allFiles.add(file);
        pendingFiles.add(file);
        final CompletableFuture<ModFileScanData> future = CompletableFuture.supplyAsync(file::compileContent, modContentScanner)
                .whenComplete(file::setScanResult)
                .whenComplete((r,t)-> this.addCompletedFile(file,r,t));
        file.setFutureScanResult(future);
    }

    private void addCompletedFile(final ModFile file, final ModFileScanData modFileScanData, final Throwable throwable) {
        if (throwable != null) {
            LOGGER.error(SCAN,"An error occurred scanning file {}", file, throwable);
        }
        pendingFiles.remove(file);
        scannedFiles.add(file);
    }

    public List<ModFile> getScannedFiles() {
        if (!pendingFiles.isEmpty()) {
            modContentScanner.shutdown();
            try {
                modContentScanner.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
            }
        }
        return scannedFiles;
    }

    public List<ModFile> getAllFiles() {
        return allFiles;
    }

    public void setLoadingModList(LoadingModList loadingModList)
    {
        this.loadingModList = loadingModList;
    }

    public LoadingModList getLoadingModList()
    {
        return loadingModList;
    }
}

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

import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.LogMarkers;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class BackgroundScanHandler
{
    private enum ScanStatus {
        NOT_STARTED,
        RUNNING,
        COMPLETE,
        TIMED_OUT,
        INTERRUPTED,
        ERRORED
    }

    private static final Logger LOGGER = LogManager.getLogger();
    private final ExecutorService modContentScanner;
    private final List<ModFile> pendingFiles;
    private final List<ModFile> scannedFiles;
    private final List<ModFile> allFiles;
    private final List<ModFile> modFiles;
    private ScanStatus status;
    private LoadingModList loadingModList;

    public BackgroundScanHandler(final List<ModFile> modFiles) {
        this.modFiles = modFiles;
        modContentScanner = Executors.newSingleThreadExecutor(r -> {
            final Thread thread = Executors.defaultThreadFactory().newThread(r);
            thread.setDaemon(true);
            return thread;
        });
        scannedFiles = new ArrayList<>();
        pendingFiles = new ArrayList<>();
        allFiles = new ArrayList<>();
        status = ScanStatus.NOT_STARTED;
    }

    public List<ModFile> getModFiles() {
        return modFiles;
    }

    public void submitForScanning(final ModFile file) {
        if (modContentScanner.isShutdown()) {
            status = ScanStatus.ERRORED;
            throw new IllegalStateException("Scanner has shutdown");
        }
        status = ScanStatus.RUNNING;
        allFiles.add(file);
        pendingFiles.add(file);
        final CompletableFuture<ModFileScanData> future = CompletableFuture.supplyAsync(file::compileContent, modContentScanner)
                .whenComplete(file::setScanResult)
                .whenComplete((r,t)-> this.addCompletedFile(file,r,t));
        file.setFutureScanResult(future);
    }

    private void addCompletedFile(final ModFile file, final ModFileScanData modFileScanData, final Throwable throwable) {
        if (throwable != null) {
            status = ScanStatus.ERRORED;
            LOGGER.error(LogMarkers.SCAN,"An error occurred scanning file {}", file, throwable);
        }
        pendingFiles.remove(file);
        scannedFiles.add(file);
    }

    public void setLoadingModList(LoadingModList loadingModList)
    {
        this.loadingModList = loadingModList;
    }

    public LoadingModList getLoadingModList()
    {
        return loadingModList;
    }

    public void waitForScanToComplete(final Runnable ticker) {
        boolean timeoutActive = System.getProperty("fml.disableScanTimeout") == null;
        Instant deadline = Instant.now().plus(Duration.ofMinutes(10));
        modContentScanner.shutdown();
        do {
            ticker.run();
            try {
                status = modContentScanner.awaitTermination(50, TimeUnit.MILLISECONDS) ? ScanStatus.COMPLETE : ScanStatus.RUNNING;
            } catch (InterruptedException e) {
                status = ScanStatus.INTERRUPTED;
            }
            if (timeoutActive && Instant.now().isAfter(deadline)) status = ScanStatus.TIMED_OUT;
        } while (status == ScanStatus.RUNNING);
        if (status == ScanStatus.INTERRUPTED) Thread.currentThread().interrupt();
        if (status != ScanStatus.COMPLETE) throw new IllegalStateException("Failed to complete mod scan");
    }
}

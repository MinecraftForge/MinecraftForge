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

package net.minecraftforge.fml.packs;

import net.minecraft.resources.AbstractResourcePack;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.StackTraceUtils;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.base.Joiner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static net.minecraftforge.fml.Logging.CORE;

public class ModFileResourcePack extends AbstractResourcePack
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final ModFile modFile;
    private ResourcePackInfo packInfo;
    private static final ExecutorService STUPIDPAULSCODEISSTUPIDWORKAROUNDTHREAD = Executors.newSingleThreadExecutor();
    private static final Path tempDir;

    static {
        try {
            tempDir = Files.createTempDirectory("modpacktmp");
            Runtime.getRuntime().addShutdownHook(new Thread(()-> {
                try {
                    Files.walk(tempDir).forEach(f->{try {Files.deleteIfExists(f);}catch (IOException ioe) {}});
                    Files.delete(tempDir);
                } catch (IOException ioe) {
                    LOGGER.fatal("Failed to clean up tempdir {}", tempDir);
                }
            }));
        } catch (IOException e) {
            LOGGER.fatal(CORE, "Failed to create temporary directory", e);
            throw new RuntimeException(e);
        }
    }

    public ModFileResourcePack(final ModFile modFile)
    {
        super(new File("dummy"));
        this.modFile = modFile;
    }

    public ModFile getModFile() {
        return this.modFile;
    }

    @Override
    public String getName()
    {
        return modFile.getFileName();
    }

    @Override
    protected InputStream getInputStream(String name) throws IOException
    {
        // because paulscode is ancient, we can't return FileChannel based InputStreams here - it will cause a deadlock or crash
        // Paulscode sends interrupt() to trigger thread processing behaviour, and FileChannels will interpret that interrupt() as
        // a sign to close the FileChannel and throw an interrupt error. Tis brilliant!
        // If the Path comes from the default filesystem provider, we will rather use the path to generate an old FileInputStream
        final Path path = modFile.getLocator().findPath(modFile, name);
        if (path.getFileSystem() == FileSystems.getDefault()) {
            LOGGER.trace(CORE, "Request for resource {} returning FileInputStream for regular file {}", name, path);
            return new FileInputStream(path.toFile());
        // If the resource is in a zip file, and paulscode is the requester, we need to return a file input stream,
        // but we can't just use path.tofile to do it. Instead, we copy the resource to a temporary file. As all operations
        // with an nio channel are interruptible, we do this at arms length on another thread, while paulscode spams
        // interrupts on the paulscode main thread, which we politely ignore.
        } else if (StackTraceUtils.threadClassNameEquals("paulscode.sound.CommandThread")) {
            final Path tempFile = Files.createTempFile(tempDir, "modpack", "soundresource");
            Future<FileInputStream> fis = STUPIDPAULSCODEISSTUPIDWORKAROUNDTHREAD.submit(()->{
                try (final SeekableByteChannel resourceChannel = Files.newByteChannel(path, StandardOpenOption.READ);
                     final FileChannel tempFileChannel = FileChannel.open(tempFile, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
                    long size = resourceChannel.size();
                    for (long written = 0; written < size; ) {
                        written += tempFileChannel.transferFrom(resourceChannel, written, size - written);
                    }
                }
                LOGGER.trace(CORE, "Request for resource {} returning DeletingTemporaryFileInputStream for packed file {} on paulscode thread", name, path);
                return new FileInputStream(tempFile.toFile());
            });
            try {
                while (true) {
                    try {
                        return fis.get();
                    } catch (InterruptedException ie) {
                        // no op
                    }
                }
            } catch (ExecutionException  e) {
                LOGGER.fatal(CORE, "Encountered fatal exception copying sound resource", e);
                throw new RuntimeException(e);
            }
        } else {
            return Files.newInputStream(path, StandardOpenOption.READ);
        }
    }

    @Override
    protected boolean resourceExists(String name)
    {
        return Files.exists(modFile.getLocator().findPath(modFile, name));
    }

    @Override
    public Collection<ResourceLocation> getAllResourceLocations(ResourcePackType type, String pathIn, int maxDepth, Predicate<String> filter)
    {
        try
        {
            Path root = modFile.getLocator().findPath(modFile, type.getDirectoryName()).toAbsolutePath();
            Path inputPath = root.getFileSystem().getPath(pathIn);

            return Files.walk(root).
                    map(path -> root.relativize(path.toAbsolutePath())).
                    filter(path -> path.getNameCount() > 1 && path.getNameCount() - 1 <= maxDepth). // Make sure the depth is within bounds, ignoring domain
                    filter(path -> !path.toString().endsWith(".mcmeta")). // Ignore .mcmeta files
                    filter(path -> path.subpath(1, path.getNameCount()).startsWith(inputPath)). // Make sure the target path is inside this one (again ignoring domain)
                    filter(path -> filter.test(path.getFileName().toString())). // Test the file name against the predicate
                    // Finally we need to form the RL, so use the first name as the domain, and the rest as the path
                    // It is VERY IMPORTANT that we do not rely on Path.toString as this is inconsistent between operating systems
                    // Join the path names ourselves to force forward slashes
                    map(path -> new ResourceLocation(path.getName(0).toString(), Joiner.on('/').join(path.subpath(1,Math.min(maxDepth, path.getNameCount()))))).
                    collect(Collectors.toList());
        }
        catch (IOException e)
        {
            return Collections.emptyList();
        }
    }

    @Override
    public Set<String> getResourceNamespaces(ResourcePackType type)
    {
        try {
            Path root = modFile.getLocator().findPath(modFile, type.getDirectoryName()).toAbsolutePath();
            return Files.walk(root,1)
                    .map(path -> root.relativize(path.toAbsolutePath()))
                    .filter(path -> path.getNameCount() > 0) // skip the root entry
                    .map(p->p.toString().replaceAll("/$","")) // remove the trailing slash, if present
                    .filter(s -> !s.isEmpty()) //filter empty strings, otherwise empty strings default to minecraft in ResourceLocations
                    .collect(Collectors.toSet());
        }
        catch (IOException e)
        {
            return Collections.emptySet();
        }
    }

    public InputStream getResourceStream(ResourcePackType type, ResourceLocation location) throws IOException {
        if (location.getPath().startsWith("lang/")) {
            return super.getResourceStream(ResourcePackType.CLIENT_RESOURCES, location);
        } else {
            return super.getResourceStream(type, location);
        }
    }

    public boolean resourceExists(ResourcePackType type, ResourceLocation location) {
        if (location.getPath().startsWith("lang/")) {
            return super.resourceExists(ResourcePackType.CLIENT_RESOURCES, location);
        } else {
            return super.resourceExists(type, location);
        }
    }


    @Override
    public void close() throws IOException
    {

    }

    <T extends ResourcePackInfo> void setPackInfo(final T packInfo) {
        this.packInfo = packInfo;
    }

    <T extends ResourcePackInfo> T getPackInfo() {
        return (T)this.packInfo;
    }
    
    @Override
    public boolean isHidden() {
    	return !modFile.getModFileInfo().showAsResourcePack();
    }
}

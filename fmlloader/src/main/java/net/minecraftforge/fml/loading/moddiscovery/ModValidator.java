package net.minecraftforge.fml.loading.moddiscovery;

import cpw.mods.jarhandling.SecureJar;
import cpw.mods.modlauncher.api.IModuleLayerManager;
import cpw.mods.modlauncher.api.ITransformationService;
import net.minecraftforge.fml.loading.*;
import net.minecraftforge.fml.loading.progress.StartupMessageManager;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.forgespi.locating.ModFileFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.minecraftforge.fml.loading.LogMarkers.SCAN;

public class ModValidator {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<IModFile.Type, List<ModFile>> modFiles;
    private final List<ModFile> candidatePlugins;
    private final List<ModFile> candidateMods;
    private LoadingModList loadingModList;
    private List<ModFile> brokenFiles;

    public ModValidator(final Map<IModFile.Type, List<ModFile>> modFiles) {
        this.modFiles = modFiles;
        this.candidateMods = new ArrayList<>(modFiles.getOrDefault(IModFile.Type.MOD, List.of()));
        this.candidatePlugins = new ArrayList<>(modFiles.getOrDefault(IModFile.Type.LANGPROVIDER, List.of()));
        this.candidatePlugins.addAll(modFiles.getOrDefault(IModFile.Type.LIBRARY, List.of()));
    }

    public void stage1Validation() {
        brokenFiles = validateFiles(candidateMods);
        LOGGER.debug(SCAN,"Found {} mod files with {} mods", candidateMods::size, ()-> candidateMods.stream().mapToInt(mf -> mf.getModInfos().size()).sum());
        StartupMessageManager.modLoaderConsumer().ifPresent(c->c.accept("Found "+ candidateMods.size()+" modfiles to load"));
    }

    @NotNull
    private List<ModFile> validateFiles(final List<ModFile> mods) {
        final List<ModFile> brokenFiles = new ArrayList<>();
        for (Iterator<ModFile> iterator = mods.iterator(); iterator.hasNext(); )
        {
            ModFile mod = iterator.next();
            if (!mod.getLocator().isValid(mod) || !mod.identifyMods()) {
                LOGGER.warn(SCAN, "File {} has been ignored - it is invalid", mod.getFilePath());
                iterator.remove();
                brokenFiles.add(mod);
            }
        }
        return brokenFiles;
    }

    public ITransformationService.Resource getPluginResources() {
        return new ITransformationService.Resource(IModuleLayerManager.Layer.PLUGIN, this.candidatePlugins.stream().map(ModFile::getSecureJar).toList());
    }

    public ITransformationService.Resource getModResources() {
        return new ITransformationService.Resource(IModuleLayerManager.Layer.GAME, this.candidateMods.stream().map(ModFile::getSecureJar).toList());
    }

    private List<EarlyLoadingException.ExceptionData> validateLanguages() {
        List<EarlyLoadingException.ExceptionData> errorData = new ArrayList<>();
        for (Iterator<ModFile> iterator = this.candidateMods.iterator(); iterator.hasNext(); ) {
            final ModFile modFile = iterator.next();
            try {
                modFile.identifyLanguage();
            } catch (EarlyLoadingException e) {
                errorData.addAll(e.getAllData());
                iterator.remove();
            }
        }
        return errorData;
    }

    public BackgroundScanHandler stage2Validation() {
        var errors = validateLanguages();
        loadingModList = ModSorter.sort(candidateMods, errors);
        loadingModList.addCoreMods();
        loadingModList.addAccessTransformers();
        loadingModList.setBrokenFiles(brokenFiles);
        BackgroundScanHandler backgroundScanHandler = new BackgroundScanHandler(candidateMods);
        loadingModList.addForScanning(backgroundScanHandler);
        return backgroundScanHandler;
    }
}

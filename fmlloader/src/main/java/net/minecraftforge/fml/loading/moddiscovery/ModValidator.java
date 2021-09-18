package net.minecraftforge.fml.loading.moddiscovery;

import cpw.mods.jarhandling.SecureJar;
import cpw.mods.modlauncher.api.IModuleLayerManager;
import cpw.mods.modlauncher.api.ITransformationService;
import net.minecraftforge.fml.loading.*;
import net.minecraftforge.fml.loading.progress.StartupMessageManager;
import net.minecraftforge.forgespi.locating.IModFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static net.minecraftforge.fml.loading.LogMarkers.SCAN;

public class ModValidator {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<IModFile.Type, List<ModFile>> modFiles;
    private final List<ModFile> candidatePlugins;
    private final List<ModFile> candidateLibraries;
    private final List<ModFile> candidateMods;
    private final List<ModFile> candidateGameLibraries;
    private LoadingModList loadingModList;
    private List<ModFile> brokenFiles;
    private List<ModFile> duplicates;

    public ModValidator(final Map<IModFile.Type, List<ModFile>> modFiles) {
        this.modFiles = modFiles;
        this.candidatePlugins = new ArrayList<>(modFiles.getOrDefault(IModFile.Type.LANGPROVIDER, List.of()));
        this.candidateLibraries = new ArrayList<>(modFiles.getOrDefault(IModFile.Type.LIBRARY, List.of()));
        this.candidateMods = new ArrayList<>(modFiles.getOrDefault(IModFile.Type.MOD, List.of()));
        this.candidateGameLibraries = new ArrayList<>(modFiles.getOrDefault(IModFile.Type.GAMELIBRARY, List.of()));
    }

    public void stage1Validation() {
        brokenFiles = validateFiles(candidateMods);
        duplicates = detectDuplicates(candidateMods);
        LOGGER.debug(SCAN,"Found {} mod files with {} mods", candidateMods::size, ()-> candidateMods.stream().mapToInt(mf -> mf.getModInfos().size()).sum());
        StartupMessageManager.modLoaderConsumer().ifPresent(c->c.accept("Found "+ candidateMods.size()+" modfiles to load"));

        brokenFiles.addAll(validateFiles(candidateGameLibraries));
        duplicates.addAll(detectDuplicates(candidateGameLibraries));
        LOGGER.debug(SCAN,"Found {} game library files", candidateGameLibraries::size);
        StartupMessageManager.modLoaderConsumer().ifPresent(c->c.accept("Found " + candidateGameLibraries.size() + " game libraries to load"));
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

    private List<ModFile> detectDuplicates(final List<ModFile> mods) {
        final Map<String, IModFile> packagesToJar = new HashMap<>();
        final List<ModFile> duplicates = new ArrayList<>();
        for (Iterator<ModFile> iterator = mods.iterator(); iterator.hasNext(); )
        {
            final ModFile mod = iterator.next();
            for (final String aPackage : mod.getSecureJar().getPackages())
            {
                if (packagesToJar.containsKey(aPackage))
                {
                    LOGGER.warn("Package: %s is duplicated in: %s and: %s".formatted(aPackage,
                            packagesToJar.get(aPackage).getSecureJar().getPrimaryPath(),
                            mod.getSecureJar().getPrimaryPath()));

                    iterator.remove();
                    duplicates.add(mod);
                    break;
                }

                packagesToJar.put(aPackage, mod);
            }
        }

        return duplicates;
    }

    public ITransformationService.Resource getPluginResources() {
        final List<SecureJar> resources = new ArrayList<>();
        resources.addAll(this.candidatePlugins.stream().map(ModFile::getSecureJar).toList());
        resources.addAll(this.candidateLibraries.stream().map(ModFile::getSecureJar).toList());

        return new ITransformationService.Resource(IModuleLayerManager.Layer.PLUGIN, resources);
    }

    public ITransformationService.Resource getModResources() {
        final List<SecureJar> resources = new ArrayList<>();
        resources.addAll(this.candidateMods.stream().map(ModFile::getSecureJar).toList());
        resources.addAll(this.candidateGameLibraries.stream().map(ModFile::getSecureJar).toList());

        return new ITransformationService.Resource(IModuleLayerManager.Layer.GAME, resources);
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

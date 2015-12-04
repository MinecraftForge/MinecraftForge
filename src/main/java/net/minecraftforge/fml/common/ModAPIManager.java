package net.minecraftforge.fml.common;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraftforge.fml.common.asm.transformers.ModAPITransformer;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.ModCandidate;
import net.minecraftforge.fml.common.discovery.ModDiscoverer;
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;
import net.minecraftforge.fml.common.functions.ModIdFunction;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;
import net.minecraftforge.fml.common.versioning.VersionParser;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class ModAPIManager {
    public static final ModAPIManager INSTANCE = new ModAPIManager();
    @SuppressWarnings("unused")
    private ModAPITransformer transformer;
    private ASMDataTable dataTable;
    private Map<String,APIContainer> apiContainers;

    private static class APIContainer extends DummyModContainer {
        private List<ArtifactVersion> referredMods;
        private ArtifactVersion ownerMod;
        private ArtifactVersion ourVersion;
        private String providedAPI;
        private File source;
        private String version;
        private Set<String> currentReferents;
        private Set<String> packages;
        private boolean selfReferenced;

        public APIContainer(String providedAPI, String apiVersion, File source, ArtifactVersion ownerMod)
        {
            this.providedAPI = providedAPI;
            this.version = apiVersion;
            this.ownerMod = ownerMod;
            this.ourVersion = new DefaultArtifactVersion(providedAPI, apiVersion);
            this.referredMods = Lists.newArrayList();
            this.source = source;
            this.currentReferents = Sets.newHashSet();
            this.packages = Sets.newHashSet();
        }

        @Override
        public File getSource()
        {
            return source;
        }
        @Override
        public String getVersion()
        {
            return version;
        }
        @Override
        public String getName()
        {
            return "API: "+providedAPI;
        }
        @Override
        public String getModId()
        {
            return providedAPI;
        }
        @Override
        public List<ArtifactVersion> getDependants()
        {
            return referredMods;
        }

        @Override
        public List<ArtifactVersion> getDependencies()
        {
            return selfReferenced ? ImmutableList.<ArtifactVersion>of() : ImmutableList.of(ownerMod);
        }

        @Override
        public ArtifactVersion getProcessedVersion()
        {
            return ourVersion;
        }

        public void validate(String providedAPI, String apiOwner, String apiVersion)
        {
            if (Loader.instance().getModClassLoader().containsSource(this.getSource())) {
                FMLLog.bigWarning("The API %s from source %s is loaded from an incompatible classloader. THIS WILL NOT WORK!", providedAPI, this.getSource().getAbsolutePath());
            }
            // TODO Compare this annotation data to the one we first found. Maybe barf if there is inconsistency?
        }

        @Override
        public String toString()
        {
            return "APIContainer{"+providedAPI+":"+version+"}";
        }

        public void addAPIReference(String embedded)
        {
            if (currentReferents.add(embedded))
            {
                referredMods.add(VersionParser.parseVersionReference(embedded));
            }
        }

        public void addOwnedPackage(String apiPackage)
        {
            packages.add(apiPackage);
        }

        public void addAPIReferences(List<String> candidateIds)
        {
            for (String modId : candidateIds)
            {
                addAPIReference(modId);
            }
        }

        void markSelfReferenced()
        {
            selfReferenced = true;
        }
    }
    public void registerDataTableAndParseAPI(ASMDataTable dataTable)
    {
        this.dataTable = dataTable;

        Set<ASMData> apiList = dataTable.getAll("net.minecraftforge.fml.common.API");

        apiContainers = Maps.newHashMap();

        for (ASMData data : apiList)
        {
            Map<String, Object> annotationInfo = data.getAnnotationInfo();
            String apiPackage = data.getClassName().substring(0,data.getClassName().indexOf(".package-info"));
            String providedAPI = (String) annotationInfo.get("provides");
            String apiOwner = (String) annotationInfo.get("owner");
            String apiVersion = (String) annotationInfo.get("apiVersion");
            APIContainer container = apiContainers.get(providedAPI);
            if (container == null)
            {
                container = new APIContainer(providedAPI, apiVersion, data.getCandidate().getModContainer(), VersionParser.parseVersionReference(apiOwner));
                apiContainers.put(providedAPI, container);
            }
            else
            {
                container.validate(providedAPI, apiOwner, apiVersion);
            }
            container.addOwnedPackage(apiPackage);
            for (ModContainer mc : data.getCandidate().getContainedMods())
            {
                String embeddedIn = mc.getModId();
                if (container.currentReferents.contains(embeddedIn))
                {
                    continue;
                }
                FMLLog.fine("Found API %s (owned by %s providing %s) embedded in %s",apiPackage, apiOwner, providedAPI, embeddedIn);
                if (!embeddedIn.equals(apiOwner))
                {
                    container.addAPIReference(embeddedIn);
                }
            }
        }

        for (APIContainer container : apiContainers.values())
        {
            for (String pkg : container.packages)
            {
                Set<ModCandidate> candidates = dataTable.getCandidatesFor(pkg);
                for (ModCandidate candidate : candidates)
                {
                    List<String> candidateIds = Lists.transform(candidate.getContainedMods(), new ModIdFunction());
                    if (!candidateIds.contains(container.ownerMod.getLabel()) && !container.currentReferents.containsAll(candidateIds))
                    {
                        FMLLog.info("Found mod(s) %s containing declared API package %s (owned by %s) without associated API reference",candidateIds, pkg, container.ownerMod);
                        container.addAPIReferences(candidateIds);
                    }
                }
            }
            if (apiContainers.containsKey(container.ownerMod.getLabel()))
            {
                ArtifactVersion owner = container.ownerMod;
                do
                {
                    APIContainer parent = apiContainers.get(owner.getLabel());
                    if (parent == container)
                    {
                        FMLLog.finer("APIContainer %s is it's own parent. skipping", owner);
                        container.markSelfReferenced();
                        break;
                    }
                    FMLLog.finer("Removing upstream parent %s from %s", parent.ownerMod.getLabel(), container);
                    container.currentReferents.remove(parent.ownerMod.getLabel());
                    container.referredMods.remove(parent.ownerMod);
                    owner = parent.ownerMod;
                }
                while (apiContainers.containsKey(owner.getLabel()));
            }
            FMLLog.fine("Creating API container dummy for API %s: owner: %s, dependents: %s", container.providedAPI, container.ownerMod, container.referredMods);
        }
    }

    public void manageAPI(ModClassLoader modClassLoader, ModDiscoverer discoverer)
    {
        registerDataTableAndParseAPI(discoverer.getASMTable());
        transformer = modClassLoader.addModAPITransformer(dataTable);
    }

    public void injectAPIModContainers(List<ModContainer> mods, Map<String, ModContainer> nameLookup)
    {
        mods.addAll(apiContainers.values());
        nameLookup.putAll(apiContainers);
    }

    public void cleanupAPIContainers(List<ModContainer> mods)
    {
        mods.removeAll(apiContainers.values());
    }

    public boolean hasAPI(String modId)
    {
        return apiContainers.containsKey(modId);
    }

    public Iterable<? extends ModContainer> getAPIList()
    {
        return apiContainers.values();
    }
}

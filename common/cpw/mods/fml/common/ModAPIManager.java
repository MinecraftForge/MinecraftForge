package cpw.mods.fml.common;

import cpw.mods.fml.common.asm.transformers.ModAPITransformer;
import cpw.mods.fml.common.discovery.ASMDataTable;
import cpw.mods.fml.common.discovery.ModDiscoverer;

public class ModAPIManager {
    public static final ModAPIManager INSTANCE = new ModAPIManager();
    private ModAPITransformer transformer;
    private ASMDataTable dataTable;

    public void registerDataTable(ASMDataTable dataTable)
    {
        this.dataTable = dataTable;
    }

    public void buildAPITransformer(ModClassLoader modClassLoader, ModDiscoverer discoverer)
    {
        registerDataTable(discoverer.getASMTable());
        transformer = modClassLoader.addModAPITransformer(dataTable);
    }
}

package net.minecraftforge.server.command;

public class ForgeArguments
{
    public static void register()
    {
        /*
         * We can't actually add any of these, because vanilla clients will choke on unknown argument types
         * So our custom arguments will not validate client-side, but they do still work
        ArgumentTypes.register("forge:enum", EnumArgument.class, new EnumArgument.Serializer());
        ArgumentTypes.register("forge:modid", ModIdArgument.class, new ArgumentSerializer<>(ModIdArgument::modIdArgument));
        ArgumentTypes.register("forge:structure_type", StructureArgument.class, new ArgumentSerializer<>(StructureArgument::structure));
        */
    }
}

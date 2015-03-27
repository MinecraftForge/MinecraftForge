package cpw.mods.fml.common.registry;

public class IncompatibleSubstitutionException extends RuntimeException {
    public IncompatibleSubstitutionException(String fromName, Object replacement, Object original)
    {
        super(String.format("The substitute %s for %s (type %s) is type incompatible.", replacement.getClass().getName(), fromName, original.getClass().getName()));
    }

    private static final long serialVersionUID = 1L;

}

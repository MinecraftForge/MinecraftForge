package cpw.mods.fml.common.registry;

import java.util.List;

public class GameRegistryException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private List<String> items;

    public GameRegistryException(String message, List<String> items)
    {
        super(message);
        this.items = items;
    }

    public List<String> getItems()
    {
        return this.items;
    }
}

package net.minecraftforge.fml;

import java.util.List;

public interface IModStateProvider {
    List<IModLoadingState> getAllStates();
}

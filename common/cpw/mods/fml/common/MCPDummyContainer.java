package cpw.mods.fml.common;

import com.google.common.eventbus.EventBus;

public class MCPDummyContainer extends DummyModContainer {
	public MCPDummyContainer(ModMetadata metadata) {
		super(metadata);
	}

	@Override
	public boolean registerBus(EventBus bus, LoadController controller) {
		return true;
	}
}

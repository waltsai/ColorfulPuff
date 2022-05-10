package waltsai.colorfulpuff;

import net.fabricmc.api.ClientModInitializer;

public class ClientUtils implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ModProvider.registerClient();
		ModProvider.initializeClient();
	}
}

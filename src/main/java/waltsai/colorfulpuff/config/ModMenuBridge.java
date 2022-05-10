package waltsai.colorfulpuff.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;
import waltsai.colorfulpuff.Utils;

public class ModMenuBridge implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if (FabricLoader.getInstance().isModLoaded("cloth-config2")) {
            return ClothConfigBridge::createConfig;
        } else {
            return Utils.InstallClothConfigScreen::new;
        }
    }
}
package waltsai.colorfulpuff;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import waltsai.colorfulpuff.config.ClothConfigBridge;

import java.io.ObjectInputFilter;

public class Utils implements ModInitializer {
	public static final String MODID = "colorfulpuff";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	@Override
	public void onInitialize() {
		ModProvider.register();
		ModProvider.initialize();
	}
	
	public static Identifier identifier(String id) {
		return new Identifier(MODID, id);
	}

	public static class InstallClothConfigScreen extends Screen {

		private static final Text INSTALL_CLOTH_CONFIG = Text.of("You must install Cloth Config");
		private final Screen parent;

		public InstallClothConfigScreen(Screen parent) {
			super(NarratorManager.EMPTY);
			this.parent = parent;
		}

		@Override
		protected void init() {
			addDrawableChild(new ButtonWidget((width - 50) / 2, height - 100, 50, 20, Text.of("Ok"), button -> removed()));
		}

		@Override
		public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
			renderBackground(matrices);

			int textWidth = client.textRenderer.getWidth(INSTALL_CLOTH_CONFIG);
			client.textRenderer.drawWithShadow(matrices, INSTALL_CLOTH_CONFIG, (width - textWidth) / 2F, height / 3F, 0xFFFFFFFF);

			super.render(matrices, mouseX, mouseY, delta);
		}

		@Override
		public void removed() {
			client.setScreen(parent);
		}
	}
}
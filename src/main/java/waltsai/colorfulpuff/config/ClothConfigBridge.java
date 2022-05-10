package waltsai.colorfulpuff.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;

public class ClothConfigBridge {
    public static Screen createConfig(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(new TranslatableText("title.colorfulpuff.config"));
        builder.setSavingRunnable(() -> Config.save());
        ConfigCategory general = builder.getOrCreateCategory(new TranslatableText("category.colorfulpuff.general"));
        ConfigEntryBuilder entry = builder.entryBuilder();
        general.addEntry(entry.startBooleanToggle(new TranslatableText("option.colorfulpuff.blink"), Config.BLINK_KEY)
                .setDefaultValue(true)
                .setTooltip(new TranslatableText("tooltip.colorfulpuff.blink"))
                .setSaveConsumer((newValue) -> Config.BLINK_KEY = newValue).build());
        general.addEntry(entry.startDoubleField(new TranslatableText("option.colorfulpuff.damage_multiplier"), Config.ATTACK_DAMAGE_MULTIPLIER)
                .setDefaultValue(1.0)
                .setTooltip(new TranslatableText("tooltip.colorfulpuff.damage_multiplier"))
                .setSaveConsumer((newValue) -> Config.ATTACK_DAMAGE_MULTIPLIER = newValue).build());
        return builder.build();
    }
}

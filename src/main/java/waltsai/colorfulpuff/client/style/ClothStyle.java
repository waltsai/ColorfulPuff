package waltsai.colorfulpuff.client.style;

import net.minecraft.item.Items;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import waltsai.colorfulpuff.mixin.StyleInvoker;

public enum ClothStyle {
    WHITE(16383998),
    ORANGE(16351261),
    MAGENTA(13061821),
    LIGHT_BLUE(3847130),
    YELLOW(16701501),
    LIME(8439583),
    PINK(15961002),
    GRAY(4673362),
    SILVER(10329495),
    CYAN(1481884),
    PURPLE(8991416),
    BLUE(3949738),
    BROWN(8606770),
    GREEN(6192150),
    RED(11546150),
    BLACK(1908001),
    UNDYED(0),
    HONEY(13408512),
    NECTAR(16764281),
    CAREMAL(10243339),
    MIFU(16762315),
    ROSEUS(12976128),
    WITHER(2434341),
    JASMIN(13626814),
    SHRIVEL_JASMINE(14413264),
    ZOMBIFIED(4669502);
    private Style style;
    ClothStyle(int rgb) {
        this.style = plain(rgb);
    }

    public static Style plain(int rgb) {
        return StyleInvoker.invokeStyle(TextColor.fromRgb(rgb), null, null, null, null, null, null, null, null, null);
    }

    public Style style() {
        return style;
    }
}
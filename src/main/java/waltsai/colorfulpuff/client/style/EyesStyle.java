package waltsai.colorfulpuff.client.style;

import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import waltsai.colorfulpuff.mixin.StyleInvoker;

public enum EyesStyle {
    LIGHT_BLUE(5811182),
    CHETWODE(8622289),
    GREEN(4568950),
    LAVENDER(12355272),
    DARK_BLUE(3049188),
    YELLOW(14728565),
    DOWNY(7393211),
    ROSE(12736917),
    PUCE(8149851),
    PURPLE(8354454),
    SHRIVEL_JASMINE(14209698),
    ROSEUS_SPECIAL(7227486),
    WITHER(6569552),
    ZOMBIFIED(5988975),
    MIFU(6124166);
    private Style style;
    EyesStyle(int rgb) {
        this.style = plain(rgb);
    }

    public static Style plain(int rgb) {
        return StyleInvoker.invokeStyle(TextColor.fromRgb(rgb), null, null, null, null, null, null, null, null, null);
    }

    public Style style() {
        return style;
    }
}
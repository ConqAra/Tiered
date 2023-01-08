package Andrew6rant.tiered.api;

import net.minecraft.text.Style;

public class TooltipClass {
    private final Style style;
    private final String[] text;

    public TooltipClass(Style style, String[] text) {
        this.style = style;
        this.text = text;
    }

    public Style getStyle() {
        return style;
    }

    public String[] getTooltipText() {
        return text;
    }
}

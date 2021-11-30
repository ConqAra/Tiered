package Andrew6rant.tiered.api;

import net.minecraft.text.Style;
import net.minecraft.util.Identifier;

import java.util.List;

public class PotentialAttribute {

    private final String id;
    private final List<ItemVerifier> verifiers;
    private final Style style;
    private final String rarity;
    private final int tooltip_border;
    private final List<AttributeTemplate> attributes;

    public PotentialAttribute(String id, List<ItemVerifier> verifiers, Style style, String rarity, int tooltip_border, List<AttributeTemplate> attributes) {
        this.id = id;
        this.verifiers = verifiers;
        this.style = style;
        this.rarity = rarity;
        this.tooltip_border = tooltip_border;
        this.attributes = attributes;
    }

    public String getID() {
        return id;
    }

    public String getRarity() {
        return rarity;
    }

    public int getTooltip_border() {
        return tooltip_border;
    }

    public List<ItemVerifier> getVerifiers() {
        return verifiers;
    }

    public boolean isValid(Identifier id) {
        for(ItemVerifier verifier : verifiers) {
            if(verifier.isValid(id)) {
                return true;
            }
        }
        return false;
    }

    public Style getStyle() {
        return style;
    }

    public List<AttributeTemplate> getAttributes() {
        return attributes;
    }
}

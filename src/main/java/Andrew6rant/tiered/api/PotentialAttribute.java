package Andrew6rant.tiered.api;

import net.minecraft.text.Style;
import net.minecraft.util.Identifier;

import java.util.List;

public class PotentialAttribute {

    private final String id;
    private final List<ItemVerifier> verifiers;
    private final Style style;
    //private final String rarity;
    private final int tooltip_image;
    private final int tooltip_border_start;
    private final int tooltip_border_end;
    private final int weight;
    private final int reforge_cost;
    private final List<AttributeTemplate> attributes;

    public PotentialAttribute(String id, List<ItemVerifier> verifiers, Style style, int tooltip_image, int tooltip_border_start, int tooltip_border_end, int weight, int reforge_cost, List<AttributeTemplate> attributes) {
        this.id = id;
        this.verifiers = verifiers;
        this.style = style;
        //this.rarity = rarity;
        this.tooltip_image = tooltip_image;
        this.tooltip_border_start = tooltip_border_start;
        this.tooltip_border_end = tooltip_border_end;
        this.weight = weight;
        this.reforge_cost = reforge_cost;
        this.attributes = attributes;
    }

    public String getID() {
        return id;
    }

    /*public String getRarity() {
        return rarity;
    }*/

    public int getTooltip_image() {
        return tooltip_image;
    }

    public int getTooltip_border_start() {
        return tooltip_border_start;
    }

    public int getTooltip_border_end() {
        return tooltip_border_end;
    }

    public int getWeight() {
        return weight;
    }

    public int getReforge_cost() {
        return reforge_cost;
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

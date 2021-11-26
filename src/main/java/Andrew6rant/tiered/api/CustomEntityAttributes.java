package Andrew6rant.tiered.api;

import Andrew6rant.tiered.Tiered;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.util.registry.Registry;

public class CustomEntityAttributes {

    public static final EntityAttribute DIG_SPEED = register(new ClampedEntityAttribute("generic.dig_speed", 0.0D, 0.0D, 2048.0D).setTracked(true));
    public static final EntityAttribute CRIT_CHANCE = register(new ClampedEntityAttribute("generic.crit_chance", 0.0D, 0.0D, 1D).setTracked(true));
    public static final EntityAttribute SIZE = register(new ClampedEntityAttribute("generic.size", 1.0D, -1024.0, 1024.0).setTracked(true));
    public static final EntityAttribute TOOLTIP_BORDER_LEVEL = register(new ClampedEntityAttribute("generic.tooltip_border_level", 0.0D, 0.0, 15.0).setTracked(true));
    //public static final EntityAttribute EXHAUSTION_LEVEL = register(new ClampedEntityAttribute("generic.exhaustion_level", 0.0D, 0.0D, 1D).setTracked(true));
//    public static final EntityAttribute DURABLE = new ClampedEntityAttribute(null, "generic.durable", 0.0D, 0.0D, 1D).setTracked(true);

    public static void init() {
        // NO-OP
    }

    private static EntityAttribute register(EntityAttribute attribute) {
        return Registry.register(Registry.ATTRIBUTE, Tiered.id(attribute.getTranslationKey()), attribute);
    }
}

package Andrew6rant.tiered.mixin.client;

import Andrew6rant.tiered.TieredClient;
import Andrew6rant.tiered.Tooltip;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import Andrew6rant.tiered.Tiered;
import Andrew6rant.tiered.api.PotentialAttribute;
import Andrew6rant.tiered.data.AttributeDataLoader;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Mixin(ItemStack.class)
public abstract class ItemStackClientMixin {

    @Shadow
    public NbtCompound getOrCreateSubNbt(String key) {
        return null;
    }

    @Shadow public abstract boolean hasNbt();

    @Shadow public abstract NbtCompound getSubNbt(String key);

    private boolean isTiered = false;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/attribute/EntityAttributeModifier;getValue()D"), method = "getTooltip", locals = LocalCapture.CAPTURE_FAILHARD)
    private void storeAttributeModifier(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List> cir, List list, int i, EquipmentSlot var6[], int var7, int var8, EquipmentSlot equipmentSlot, Multimap multimap, Iterator var11, Map.Entry entry, EntityAttributeModifier entityAttributeModifier) {
        isTiered = entityAttributeModifier.getName().contains("tiered:");
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/text/TranslatableText;formatted(Lnet/minecraft/util/Formatting;)Lnet/minecraft/text/MutableText;", ordinal = 2), method = "getTooltip")
    private MutableText getFormatting(TranslatableText translatableText, Formatting formatting) {
        if(this.hasNbt() && this.getSubNbt(Tiered.NBT_SUBTAG_KEY) != null && isTiered) {
            Identifier tier = new Identifier(this.getOrCreateSubNbt(Tiered.NBT_SUBTAG_KEY).getString(Tiered.NBT_SUBTAG_DATA_KEY));
            PotentialAttribute attribute = Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().get(tier);

            return translatableText.setStyle(attribute.getStyle());
        } else {
            return translatableText.formatted(formatting);
        }
    }

    @ModifyVariable(
            method = "getTooltip",
            at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Multimap;isEmpty()Z"),
            remap = false,
            index = 10
    )
    private Multimap<EntityAttribute, EntityAttributeModifier> sort(Multimap<EntityAttribute, EntityAttributeModifier> map) {
        Multimap<EntityAttribute, EntityAttributeModifier> vanillaFirst = LinkedListMultimap.create();
        Multimap<EntityAttribute, EntityAttributeModifier> remaining = LinkedListMultimap.create();
        Multimap<EntityAttribute, EntityAttributeModifier> no_duplicates = LinkedListMultimap.create();

        /*map.forEach((entityAttribute, entityAttributeModifier) -> {
            map.forEach((entityAttributeCompare, entityAttributeModifierCompare) -> {
                if(entityAttribute.getTranslationKey().equals(entityAttributeCompare.getTranslationKey())) {
                    System.out.println("yes");
                    System.out.println(entityAttributeModifier.getName());
                    System.out.println(entityAttributeModifier.getValue());
                    System.out.println(entityAttributeModifier.getOperation());
                    System.out.println("--");
                    System.out.println(entityAttribute.getTranslationKey());
                    System.out.println(entityAttribute.getDefaultValue());
                }
            });
        });*/


        map.forEach((entityAttribute, entityAttributeModifier) -> {
            if (!entityAttributeModifier.getName().contains("tiered")) {
                vanillaFirst.put(entityAttribute, entityAttributeModifier);
            } else {
                if (!entityAttributeModifier.getName().contains("no_tooltip")) {
                    remaining.put(entityAttribute, entityAttributeModifier);
                }
            }
        });

        vanillaFirst.forEach((entityAttribute, entityAttributeModifier) -> remaining.forEach((entityAttributeCompare, entityAttributeModifierCompare) -> {
            if(entityAttribute.getTranslationKey().equals(entityAttributeCompare.getTranslationKey())){
                double test; double combo_calc;

                if (entityAttributeModifier.getOperation() == EntityAttributeModifier.Operation.ADDITION) {
                    test = entityAttributeModifier.getValue() + entityAttribute.getDefaultValue();
                } else {
                    test = (entityAttributeModifier.getValue() * entityAttribute.getDefaultValue()) + 1;
                }

                if (entityAttributeModifierCompare.getOperation() == EntityAttributeModifier.Operation.ADDITION) {
                    combo_calc = entityAttributeModifierCompare.getValue() + test;
                } else {
                    combo_calc = (entityAttributeModifierCompare.getValue() * test) + 1;
                }
                System.out.println(entityAttribute.getTranslationKey());
                System.out.println(combo_calc);

                //no_duplicates.put(entityAttribute, entityAttributeModifier);
                no_duplicates.put(entityAttribute, new EntityAttributeModifier(entityAttributeModifier.getName(), combo_calc, entityAttributeModifierCompare.getOperation()));
            }
        }));
        
        vanillaFirst.putAll(remaining);
        vanillaFirst.putAll(no_duplicates);
        return vanillaFirst;
    }

    @Inject(
            method = "getName",
            at = @At("RETURN"),
            cancellable = true
    )
    private void modifyName(CallbackInfoReturnable<Text> cir) {
        if(this.hasNbt() && this.getSubNbt("display") == null && this.getSubNbt(Tiered.NBT_SUBTAG_KEY) != null) {
            Identifier tier = new Identifier(getOrCreateSubNbt(Tiered.NBT_SUBTAG_KEY).getString(Tiered.NBT_SUBTAG_DATA_KEY));

            // attempt to display attribute if it is valid
            PotentialAttribute potentialAttribute = Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().get(tier);

            if(potentialAttribute != null) {
                /*switch (Objects.requireNonNull(potentialAttribute.getStyle().getColor()).toString()) {
                    case "aqua" -> cir.setReturnValue(new TranslatableText(potentialAttribute.getID() + ".label").append(" ").append(cir.getReturnValue()).setStyle(Style.EMPTY.withColor(0x7FFFFF)));
                    case "light_purple" -> cir.setReturnValue(new TranslatableText(potentialAttribute.getID() + ".label").append(" ").append(cir.getReturnValue()).setStyle(Style.EMPTY.withColor(0xFF70FF)));
                    case "white" -> cir.setReturnValue(new TranslatableText(potentialAttribute.getID() + ".label").append(" ").append(cir.getReturnValue()).setStyle(Style.EMPTY.withColor(0xFEFEFE)));
                    default -> cir.setReturnValue(new TranslatableText(potentialAttribute.getID() + ".label").append(" ").append(cir.getReturnValue()).setStyle(potentialAttribute.getStyle()));
                }*/
                cir.setReturnValue(new TranslatableText(potentialAttribute.getID() + ".label").append(" ").append(cir.getReturnValue()).setStyle(potentialAttribute.getStyle()));
            }
        }
    }
}

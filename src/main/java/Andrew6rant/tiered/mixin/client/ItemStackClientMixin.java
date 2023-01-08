package Andrew6rant.tiered.mixin.client;

import Andrew6rant.tiered.api.AttributeTemplate;
import Andrew6rant.tiered.api.TooltipClass;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import Andrew6rant.tiered.Tiered;
import Andrew6rant.tiered.api.PotentialAttribute;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
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

import java.awt.*;
import java.util.*;
import java.util.List;

import static Andrew6rant.tiered.TieredClient.roundFloat;
import static Andrew6rant.tiered.TieredClient.trailZeros;

@Environment(EnvType.CLIENT)
@Mixin(ItemStack.class)
public abstract class ItemStackClientMixin {

    @Shadow
    public abstract NbtCompound getOrCreateSubNbt(String key);

    @Shadow
    public abstract boolean hasNbt();

    @Shadow
    public abstract NbtCompound getSubNbt(String key);

    private boolean isTiered = false;

    @Inject(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/attribute/EntityAttributeModifier;getValue()D"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void storeAttributeModifier(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List> cir, List list, MutableText mutableText, int i, EquipmentSlot var6[], int var7,
                                        int var8, EquipmentSlot equipmentSlot, Multimap multimap, Iterator var11, Map.Entry entry, EntityAttributeModifier entityAttributeModifier) {
        isTiered = entityAttributeModifier.getName().contains("tiered:");
    }

    @Redirect(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/TranslatableText;formatted(Lnet/minecraft/util/Formatting;)Lnet/minecraft/text/MutableText;", ordinal = 2))
    private MutableText getFormatting(TranslatableText translatableText, Formatting formatting) {
        if (this.hasNbt() && this.getSubNbt(Tiered.NBT_SUBTAG_KEY) != null && isTiered) {
            Identifier tier = new Identifier(this.getOrCreateSubNbt(Tiered.NBT_SUBTAG_KEY).getString(Tiered.NBT_SUBTAG_DATA_KEY));
            PotentialAttribute attribute = Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().get(tier);

            return translatableText.setStyle(attribute.getStyle());
        } else {
            return translatableText.formatted(formatting);
        }
    }

    @ModifyVariable(method = "getTooltip", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Multimap;isEmpty()Z"), remap = false, index = 10)
    private Multimap<EntityAttribute, EntityAttributeModifier> sort(Multimap<EntityAttribute, EntityAttributeModifier> map) {

        Multimap<EntityAttribute, EntityAttributeModifier> vanillaFirst = LinkedListMultimap.create();
        Multimap<EntityAttribute, EntityAttributeModifier> remaining = LinkedListMultimap.create();

        map.forEach((entityAttribute, entityAttributeModifier) -> {
            if (!entityAttributeModifier.getName().contains("tiered")) {
                vanillaFirst.put(entityAttribute, entityAttributeModifier);
            }
            else {
                remaining.put(entityAttribute, entityAttributeModifier);
            }
        });
        vanillaFirst.putAll(remaining);
        return vanillaFirst;
    }

    @Inject(method = "getName", at = @At("RETURN"), cancellable = true)
    private void getNameMixin(CallbackInfoReturnable<Text> info) {
        if (this.hasNbt() && this.getSubNbt("display") == null && this.getSubNbt(Tiered.NBT_SUBTAG_KEY) != null) {
            Identifier tier = new Identifier(getOrCreateSubNbt(Tiered.NBT_SUBTAG_KEY).getString(Tiered.NBT_SUBTAG_DATA_KEY));

            // attempt to display attribute if it is valid
            PotentialAttribute potentialAttribute = Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().get(tier);

            if (potentialAttribute != null) {
                if(!potentialAttribute.getAttributes().get(0).getAttributeTypeID().equals("none")) {
                    info.setReturnValue(new TranslatableText(potentialAttribute.getID().split("_")[0] + ".name").append(" ").append(info.getReturnValue()).setStyle(potentialAttribute.getStyle()));
                }
            }

        }
    }
    @Inject(
            method = "getTooltip",
            at = @At(value = "RETURN", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    private void test(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir) {
        if (isTiered && this.hasNbt() && this.getSubNbt(Tiered.NBT_SUBTAG_KEY) != null) { // only run on tiered items
            Identifier tier = new Identifier(this.getOrCreateSubNbt(Tiered.NBT_SUBTAG_KEY).getString(Tiered.NBT_SUBTAG_DATA_KEY));
            PotentialAttribute attribute = Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().get(tier);
            List<Text> list = cir.getReturnValue();
            List<Text> badlyFormattedList = new ArrayList<>();
            Set<TranslatableText> modifierSet = new HashSet<>();
            List<Text> modifierSlotList = new ArrayList<>();
            Set<String> set = new HashSet<>();
            Set<TranslatableText> noDuplicates = new HashSet<>();
            String heldOrArmor = "";

            //for (Text text : list) {
            //    System.out.println(text);
            //}

            // remove blank tooltip lines
            list.removeIf(text -> (!(text instanceof TranslatableText) && text.getSiblings().size() == 0));

            byte mainhandIndex = -1; // invalid indexes
            byte offhandIndex = -1;
            // tooltip may need to be combined, e.g.
            // When in Main Hand: ...
            // and
            // When in Off Hand: ...
            // to
            // When held:
            // but only if main and off hand are identical
            for (byte i = 0; i < list.size(); i++) {
                if (list.get(i) instanceof TranslatableText translatableText) {
                    if (translatableText.getKey().startsWith("attribute.modifier")) {
                        modifierSet.add(translatableText);
                    }
                    if (translatableText.getKey().equals("item.modifiers.mainhand")) {
                        mainhandIndex = i;
                        modifierSlotList.add(translatableText);
                        heldOrArmor = "_held";
                    }
                    if (translatableText.getKey().equals("item.modifiers.offhand")) {
                        offhandIndex = i;
                        modifierSlotList.add(translatableText);
                        heldOrArmor = "_held";
                    }
                } else {
                    badlyFormattedList.add(list.get(i));
                }
            }
            // This code replaces Vanilla's held item damage and attack speed tooltips (the green ones) with a custom one
            if (mainhandIndex != -1) { // only run this code on held items
                for (Text text : badlyFormattedList) {
                    if (text.getSiblings() != null) {
                        for (Text sibling : text.getSiblings()) {
                            if (sibling instanceof TranslatableText translatableText) {
                                if (translatableText.getKey().equals("attribute.modifier.equals.0")) {
                                    list.remove(text);
                                    list.add(mainhandIndex + 1, new TranslatableText(translatableText.getKey(), trailZeros(Float.parseFloat(String.valueOf(translatableText.getArgs()[0]))), new TranslatableText(((TranslatableText)translatableText.getArgs()[1]).getKey())).formatted(Formatting.DARK_GREEN));
                                    modifierSet.add((TranslatableText) new TranslatableText(translatableText.getKey(), trailZeros(Float.parseFloat(String.valueOf(translatableText.getArgs()[0]))), new TranslatableText(((TranslatableText)translatableText.getArgs()[1]).getKey())).formatted(Formatting.DARK_GREEN));
                                }
                            }
                        }
                    }
                }
            }
            // if both mainhand and offhand are present, remove duplicate tooltip items
            if (mainhandIndex != -1 && offhandIndex != -1) {
                list.removeAll(modifierSlotList); // remove mainhand and offhand tooltips
                list.removeAll(modifierSet); // removes all instances of TranslatableComponents in the set (there are duplicates in the list)
                list.addAll(modifierSet); // adds back only one of each TranslatableComponent tooltip item
                list.add(mainhandIndex, new TranslatableText("item.modifiers.both_hands").formatted(Formatting.GRAY)); // add combo text for mainhand/offhand replacement
            }

            for (TranslatableText translatableText : modifierSet) {
                if (!set.add(((TranslatableText) translatableText.getArgs()[1]).getKey())) { // if there is more than one modifier with the same key
                    for (TranslatableText textCompare : modifierSet) {
                        if(((TranslatableText) translatableText.getArgs()[1]).getKey().equals(((TranslatableText) textCompare.getArgs()[1]).getKey())) {
                            noDuplicates.add(translatableText);
                            noDuplicates.add(textCompare);
                        }
                    }
                }
            }

            for (int i = 0; i < noDuplicates.size(); i ++) {
                for (int j = i + 1; j < noDuplicates.size(); j++) {
                    TranslatableText text = (TranslatableText) noDuplicates.toArray()[i];
                    TranslatableText text_compare = (TranslatableText) noDuplicates.toArray()[j];
                    TranslatableText key = (TranslatableText) text.getArgs()[1];
                    TranslatableText key_compare = (TranslatableText) text_compare.getArgs()[1];
                    float val1 = Float.parseFloat(String.valueOf(text.getArgs()[0]));
                    float val2 = Float.parseFloat(String.valueOf(text_compare.getArgs()[0]));
                    String val1Str = trailZeros(val1);
                    String val2Str = trailZeros(val2);
                    if (key.getKey().equals(key_compare.getKey())) {
                        list.remove(noDuplicates.toArray()[i]);
                        list.remove(noDuplicates.toArray()[j]);
                        switch (text.getKey() + text_compare.getKey()) {
                            // I will turn this into a proper lookup table later lol
                            case "attribute.modifier.plus.0attribute.modifier.plus.0" -> list.add(2, new TranslatableText("tooltip.tiered.add"+heldOrArmor, trailZeros(roundFloat(val1 + val2)), key, val2Str, val1Str).setStyle(attribute.getStyle()));
                            case "attribute.modifier.plus.0attribute.modifier.plus.1", "attribute.modifier.equals.0attribute.modifier.plus.1" -> list.add(2, new TranslatableText("tooltip.tiered.multiply_base"+heldOrArmor, trailZeros(roundFloat((val1 * (val2 / 100.0f)) + val1)), key, val1Str, val2Str).setStyle(attribute.getStyle()));
                            case "attribute.modifier.plus.0attribute.modifier.take.0", "attribute.modifier.take.0attribute.modifier.plus.0", "attribute.modifier.equals.0attribute.modifier.plus.0" -> list.add(2, new TranslatableText("tooltip.tiered.subtract"+heldOrArmor, trailZeros(roundFloat(val2 - val1)), key, val2Str, val1Str).formatted(Formatting.RED));
                            case "attribute.modifier.plus.0attribute.modifier.take.1", "attribute.modifier.equals.0attribute.modifier.take.1" -> list.add(2, new TranslatableText("tooltip.tiered.divide_base"+heldOrArmor, trailZeros(roundFloat(val1 - (val1 * (val2 / 100.0f)))), key, val1Str, val2Str).formatted(Formatting.RED));
                            case "attribute.modifier.plus.1attribute.modifier.equals.0", "attribute.modifier.plus.1attribute.modifier.plus.0" -> list.add(2, new TranslatableText("tooltip.tiered.multiply_base"+heldOrArmor, trailZeros(roundFloat((val2 * (val1 / 100.0f)) + val2)), key, val2Str, val1Str).setStyle(attribute.getStyle()));
                            case "attribute.modifier.plus.2attribute.modifier.equals.0", "attribute.modifier.plus.2attribute.modifier.plus.0" -> list.add(2, new TranslatableText("tooltip.tiered.multiply_total"+heldOrArmor, trailZeros(roundFloat((val2 * (val1 / 100.0f)) + val2)), key, val2Str, trailZeros(val1+100)).setStyle(attribute.getStyle()));
                            case "attribute.modifier.take.1attribute.modifier.equals.0" -> list.add(2, new TranslatableText("tooltip.tiered.divide_base"+heldOrArmor, trailZeros(roundFloat(val2 - (val2 * (val1 / 100.0f)))), key, val2Str, val1Str).formatted(Formatting.RED));
                            case "attribute.modifier.take.2attribute.modifier.equals.0" -> list.add(2, new TranslatableText("tooltip.tiered.divide_total"+heldOrArmor, trailZeros(roundFloat(val2 - (val2 * (val1 / 100.0f)))), key, val2Str, trailZeros(100f-val1)).formatted(Formatting.RED));
                            case "attribute.modifier.equals.0attribute.modifier.plus.2" -> list.add(2, new TranslatableText("tooltip.tiered.multiply_total"+heldOrArmor, trailZeros(roundFloat((val1 * (val2 / 100.0f)) + val1)), key, val1Str, trailZeros(val2+100)).setStyle(attribute.getStyle()));
                            case "attribute.modifier.equals.0attribute.modifier.take.2" -> list.add(2, new TranslatableText("tooltip.tiered.divide_total"+heldOrArmor, trailZeros(roundFloat(val1 - (val1 * (val2 / 100.0f)))), key, val1Str, trailZeros(100f-val2)).formatted(Formatting.RED));
                            default -> {
                                list.add(2, new TranslatableText("tooltip.tiered.error" , key).formatted(Formatting.RED));
                                System.out.println("The combination of "+text.getKey()+" and "+text_compare.getKey()+" is not supported yet. Please make an issue on GitHub: https://github.com/Andrew6rant/tiered");
                            }
                        }
                    }
                }
            }

            for (AttributeTemplate attributeTemplate : attribute.getAttributes()) {
                if (attributeTemplate.getTooltip() != null) {
                    TooltipClass tooltips = attributeTemplate.getTooltip();
                    for (String tooltipText : tooltips.getTooltipText()) {
                        if (tooltips.getStyle() != null) {
                            list.add(new TranslatableText(tooltipText).setStyle(tooltips.getStyle()));
                        } else {
                            list.add(new TranslatableText(tooltipText).setStyle(attribute.getStyle()));
                        }
                    }
                }
            }
        }
    }
}
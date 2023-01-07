package Andrew6rant.tiered.mixin.client;

import Andrew6rant.tiered.api.AttributeTemplate;
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
            List<Text> list = cir.getReturnValue();
            List<Text> badlyFormattedList = new ArrayList<>();
            List<Text> properlyFormattedList = new ArrayList<>();

            list.removeIf(text -> (!(text instanceof TranslatableText) && text.getSiblings().size() == 0)); // remove blank tooltip lines
            for (Text text : list) {
            //    System.out.println("||||| "+text);
            }
            List<TranslatableText> modifierList = new ArrayList<>();
            //boolean hasMainhand = false;
            //boolean hasOffhand = false;
            byte hasMainhandIndex = -1; // invalid index
            byte hasOffhandIndex = -1;
            // tooltip may need to be combined, e.g.
            // When in Main Hand: ...
            // and
            // When in Off Hand: ...
            // to
            // When held:
            // but only if main and off hand are identical
            for (byte i = 0; i < list.size(); i++) {
                if (list.get(i) instanceof TranslatableText translatableText) {
                    properlyFormattedList.add(translatableText);
                    if (translatableText.getKey().startsWith("item.modifiers.mainhand")) {
                        hasMainhandIndex = i;
                    }
                    if (translatableText.getKey().startsWith("item.modifiers.offhand")) {
                        hasOffhandIndex = i;
                    }
                } else {
                    badlyFormattedList.add(list.get(i));
                }
            }
            // This code replaces Vanilla's held item damage and attack speed tooltips (the green ones) with a custom one
            if (hasMainhandIndex != -1) { // only run this code on held items
                for (Text text : badlyFormattedList) {
                    if (text.getSiblings() != null) {
                        for (Text sibling : text.getSiblings()) {
                            //System.out.println("|sibling| " + sibling);
                            if (sibling instanceof TranslatableText translatableText) {
                                if (translatableText.getKey().equals("attribute.modifier.equals.0")) {
                                    list.remove(text);
                                    list.add(hasMainhandIndex + 1, new TranslatableText(translatableText.getKey(), trailZeros(Float.parseFloat(String.valueOf(translatableText.getArgs()[0]))), new TranslatableText(((TranslatableText)translatableText.getArgs()[1]).getKey())).formatted(Formatting.DARK_GREEN));
                                }
                            }
                        }
                    }
                }
            }
            // if both mainhand and offhand are present, remove duplicate tooltip items
            if (hasMainhandIndex != -1 && hasOffhandIndex != -1) {
                //System.out.println("has both, Mainhand: "+hasMainhandIndex+" Offhand: "+hasOffhandIndex);
                //List<Text> tempList = list.subList(hasMainhandIndex, hasOffhandIndex - 1);
                //System.out.println("sublist 1: "+list.subList(hasMainhandIndex + 1, hasOffhandIndex - 1));
                //System.out.println("sublist 2: "+list.subList(hasOffhandIndex + 1, list.size()));

                List<Text> tempList = properlyFormattedList.subList(1, (properlyFormattedList.size() / 2) - 1);
                List<Text> tempList2 = properlyFormattedList.subList((properlyFormattedList.size() / 2) + 1, properlyFormattedList.size() - 1);
                //System.out.println("sublist 1: "+properlyFormattedList.subList(1, (properlyFormattedList.size() / 2) - 1));
                //System.out.println("sublist 2: "+properlyFormattedList.subList((properlyFormattedList.size() / 2) + 1, properlyFormattedList.size() - 1));
                System.out.println(tempList.equals(tempList2));

                if (properlyFormattedList.subList(1, (properlyFormattedList.size() / 2) - 1).equals(properlyFormattedList.subList((properlyFormattedList.size() / 2) + 1, properlyFormattedList.size() - 1))) { // if both mainhand and offhand are identical
                    System.out.println("both are identical");
                    //list.removeAll(properlyFormattedList.subList(hasMainhandIndex, hasOffhandIndex));
                    //list.addAll(hasMainhandIndex, tempList);
                    //list.add(hasMainhandIndex, new TranslatableText("item.modifiers.both_hands").formatted(Formatting.GRAY));
                }
            }


            /*
            int index = -1;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) instanceof TranslatableText translatableText) {
                    if (translatableText.getKey().startsWith("item.modifiers.mainhand")) {
                        modifierList.add(translatableText);
                        if (index == -1) {
                            index = i;
                        }
                    }
                    if (modifierList.size() > 1) {
                        list.subList(i-1, list.size()).clear();
                    }
                }
            }
            if (modifierList.size() > 1) {
                list.removeAll(modifierList);
                list.add(index, new TranslatableText("item.modifiers.both_hands").formatted(Formatting.GRAY));
            }
            */
            for (Text text : list) {
            //    System.out.println(text);
            }
            System.out.println("------");
        }
        /*

        if (isTiered && this.hasNbt() && this.getSubNbt(Tiered.NBT_SUBTAG_KEY) != null) { // only run on tiered items
            List<Text> list = cir.getReturnValue();
            List<Text> badlyFormattedList = new ArrayList<>();
            List<TranslatableText> modifierList = new ArrayList<>();
            Set<String> set = new HashSet<>();
            Set<TranslatableText> noDuplicates = new HashSet<>();
            list.removeIf(text -> (!(text instanceof TranslatableText) && text.getSiblings().size() == 0)); // remove blank tooltip lines
            for (Text textComponent : list) {
                if (!(textComponent instanceof TranslatableText)) {
                    badlyFormattedList.add(textComponent);
                }
            }
            badlyFormattedList.remove(0); // preserve the name of the item
            for (Text text : badlyFormattedList) {
                // reformat badly formatted tooltip lines into TranslatableTexts, as the first two lines
                // of most held weapons are blank TextComponents with sibling TranslatableComponents
                TranslatableText translatableText = (TranslatableText) text.getSiblings().get(0);
                TranslatableText newText = (TranslatableText) translatableText.getArgs()[1];
                list.add(2, new TranslatableText(translatableText.getKey(), trailZeros(Float.parseFloat(String.valueOf(translatableText.getArgs()[0]))), new TranslatableText(newText.getKey())).formatted(Formatting.DARK_GREEN));
            }
            list.removeAll(badlyFormattedList); // remove badly formatted lines
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) instanceof TranslatableText translatableText) {
                    if (translatableText.getKey().startsWith("item.modifiers")) {
                        modifierList.add(translatableText);
                    }
                    if (modifierList.size() > 1) {
                        list.subList(i, list.size()).clear();
                    }
                }
            }
            if (modifierList.size() > 1) {
                list.removeAll(modifierList);
                Object[] args = new Object[modifierList.size()];
                for (int i = 0; i < modifierList.size(); i++) {
                    args[i] = new TranslatableText("tooltip.tiered." +modifierList.get(i).getKey());
                }
                list.add(1, new TranslatableText("tooltip.tiered.modifier."+modifierList.size(), args).formatted(Formatting.GRAY));
            }

            for (Text text : list) {
                if (text instanceof TranslatableText listText && listText.getKey().startsWith("attribute.modifier")) {
                    Object[] args = listText.getArgs();
                    TranslatableText argText = (TranslatableText) args[1];
                    if (!set.add(argText.getKey())) { // if there is more than one modifier with the same key
                        for (Text textCompare : list) {
                            if (textCompare instanceof TranslatableText listTextCompare && listTextCompare.getKey().startsWith("attribute.modifier")) {
                                Object[] args2 = listTextCompare.getArgs(); TranslatableText argText2 = (TranslatableText) args2[1];
                                if(argText.getKey().equals(argText2.getKey())) {
                                    noDuplicates.add(listText);
                                    noDuplicates.add(listTextCompare);
                                }
                            }
                        }
                    }

                }
            }
            for (int i = 0; i < noDuplicates.size(); i ++) {
                for (int j = i+1; j < noDuplicates.size(); j ++) {
                    TranslatableText text = (TranslatableText) noDuplicates.toArray()[i];
                    TranslatableText text_compare = (TranslatableText) noDuplicates.toArray()[j];
                    TranslatableText key = (TranslatableText) text.getArgs()[1];
                    TranslatableText key_compare = (TranslatableText) text_compare.getArgs()[1];
                    float val1 = Float.parseFloat(String.valueOf(text.getArgs()[0]));
                    float val2 = Float.parseFloat(String.valueOf(text_compare.getArgs()[0]));
                    String val1Str = trailZeros(val1);
                    String val2Str = trailZeros(val2);
                    if (key.getKey().equals(key_compare.getKey())) {
                        Identifier tier = new Identifier(this.getOrCreateSubNbt(Tiered.NBT_SUBTAG_KEY).getString(Tiered.NBT_SUBTAG_DATA_KEY));
                        PotentialAttribute attribute = Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().get(tier);
                        list.remove(noDuplicates.toArray()[i]);
                        list.remove(noDuplicates.toArray()[j]);
                        //System.out.println(text.getKey() + text_compare.getKey());
                        switch (text.getKey() + text_compare.getKey()) {
                            // I will turn this into a proper lookup table later lol
                            case "attribute.modifier.plus.0attribute.modifier.plus.0" -> list.add(2, new TranslatableText("tooltip.tiered.add", trailZeros(roundFloat(val1 + val2)), key, val2Str, val1Str).setStyle(attribute.getStyle()));
                            case "attribute.modifier.plus.0attribute.modifier.plus.1", "attribute.modifier.equals.0attribute.modifier.plus.1" -> list.add(2, new TranslatableText("tooltip.tiered.multiply_base", trailZeros(roundFloat((val1 * (val2 / 100.0f)) + val1)), key, val1Str, val2Str).setStyle(attribute.getStyle()));
                            case "attribute.modifier.plus.0attribute.modifier.take.0", "attribute.modifier.take.0attribute.modifier.plus.0", "attribute.modifier.equals.0attribute.modifier.plus.0" -> list.add(2, new TranslatableText("tooltip.tiered.subtract", trailZeros(roundFloat(val2 - val1)), key, val2Str, val1Str).formatted(Formatting.RED));
                            case "attribute.modifier.plus.0attribute.modifier.take.1", "attribute.modifier.equals.0attribute.modifier.take.1" -> list.add(2, new TranslatableText("tooltip.tiered.divide_base", trailZeros(roundFloat(val1 - (val1 * (val2 / 100.0f)))), key, val1Str, val2Str).formatted(Formatting.RED));
                            case "attribute.modifier.plus.1attribute.modifier.equals.0", "attribute.modifier.plus.1attribute.modifier.plus.0" -> list.add(2, new TranslatableText("tooltip.tiered.multiply_base", trailZeros(roundFloat((val2 * (val1 / 100.0f)) + val2)), key, val2Str, val1Str).setStyle(attribute.getStyle()));
                            case "attribute.modifier.plus.2attribute.modifier.equals.0", "attribute.modifier.plus.2attribute.modifier.plus.0" -> list.add(2, new TranslatableText("tooltip.tiered.multiply_total", trailZeros(roundFloat((val2 * (val1 / 100.0f)) + val2)), key, val2Str, trailZeros(val1+100)).setStyle(attribute.getStyle()));
                            case "attribute.modifier.take.1attribute.modifier.equals.0" -> list.add(2, new TranslatableText("tooltip.tiered.divide_base", trailZeros(roundFloat(val2 - (val2 * (val1 / 100.0f)))), key, val2Str, val1Str).formatted(Formatting.RED));
                            case "attribute.modifier.take.2attribute.modifier.equals.0" -> list.add(2, new TranslatableText("tooltip.tiered.divide_total", trailZeros(roundFloat(val2 - (val2 * (val1 / 100.0f)))), key, val2Str, trailZeros(100f-val1)).formatted(Formatting.RED));
                            case "attribute.modifier.equals.0attribute.modifier.plus.2" -> list.add(2, new TranslatableText("tooltip.tiered.multiply_total", trailZeros(roundFloat((val1 * (val2 / 100.0f)) + val1)), key, val1Str, trailZeros(val2+100)).setStyle(attribute.getStyle()));
                            case "attribute.modifier.equals.0attribute.modifier.take.2" -> list.add(2, new TranslatableText("tooltip.tiered.divide_total", trailZeros(roundFloat(val1 - (val1 * (val2 / 100.0f)))), key, val1Str, trailZeros(100f-val2)).formatted(Formatting.RED));
                            default -> {
                                list.add(2, new TranslatableText("tooltip.tiered.error" , key).formatted(Formatting.RED));
                                System.out.println("The combination of "+text.getKey()+" and "+text_compare.getKey()+" is not supported yet. Please make an issue on GitHub: https://github.com/Andrew6rant/tiered");
                            }
                        }
                    }
                }
            }
            //for (Text text : list) {
            //    System.out.println(text);
            //}
            //System.out.println("-------");
            Identifier tier = new Identifier(getOrCreateSubNbt(Tiered.NBT_SUBTAG_KEY).getString(Tiered.NBT_SUBTAG_DATA_KEY));
            PotentialAttribute potentialAttribute = Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().get(tier);
            for (AttributeTemplate attribute : potentialAttribute.getAttributes()) {
                if (attribute.getTooltip() != null) {
                    List<Object> tooltips = attribute.getTooltip();
                    for (Object tooltip : tooltips.subList(1, tooltips.size())) {
                        list.add(new TranslatableText((String) tooltip).setStyle((Style) tooltips.get(0)));
                    }
                }
            }
        }
        */
    }
}
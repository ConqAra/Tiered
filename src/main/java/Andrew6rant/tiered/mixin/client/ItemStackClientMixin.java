package Andrew6rant.tiered.mixin.client;

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
                //System.out.println(entityAttributeModifier.getName() + " --- " + entityAttributeModifier.getValue() + " --- " + entityAttributeModifier.getOperation() + " --- " + entityAttribute.getTranslationKey());
                vanillaFirst.put(entityAttribute, entityAttributeModifier);
            }
            else {
                //System.out.println("T, " +entityAttributeModifier.getName() + " --- " + entityAttributeModifier.getValue() + " --- " + entityAttributeModifier.getOperation() + " --- " + entityAttribute.getTranslationKey());
                remaining.put(entityAttribute, entityAttributeModifier);
            }
        });
        //System.out.println("---");
        vanillaFirst.putAll(remaining);
        //System.out.println("----------------");
        return vanillaFirst;
    }

    @Inject(method = "getName", at = @At("RETURN"), cancellable = true)
    private void getNameMixin(CallbackInfoReturnable<Text> info) {
        if (this.hasNbt() && this.getSubNbt("display") == null && this.getSubNbt(Tiered.NBT_SUBTAG_KEY) != null) {
            Identifier tier = new Identifier(getOrCreateSubNbt(Tiered.NBT_SUBTAG_KEY).getString(Tiered.NBT_SUBTAG_DATA_KEY));

            // attempt to display attribute if it is valid
            PotentialAttribute potentialAttribute = Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().get(tier);

            if (potentialAttribute != null)
                info.setReturnValue(new TranslatableText(potentialAttribute.getID() + ".label").append(" ").append(info.getReturnValue()).setStyle(potentialAttribute.getStyle()));
        }
    }
    @Inject(
            method = "getTooltip",
            at = @At(value = "RETURN", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    private void test(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir) {
        List<Text> list = cir.getReturnValue();
        if (isTiered && this.hasNbt() && this.getSubNbt(Tiered.NBT_SUBTAG_KEY) != null) { // only run on tiered items
            //List<Text> list = cir.getReturnValue();
            List<Text> badlyFormattedList = new ArrayList<>();
            List<Text> properlyFormattedList = new ArrayList<>();
            Identifier tier = new Identifier(this.getOrCreateSubNbt(Tiered.NBT_SUBTAG_KEY).getString(Tiered.NBT_SUBTAG_DATA_KEY));
            PotentialAttribute attribute = Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().get(tier);
            list.remove(1); // remove Vanilla's blank tooltip line

            TranslatableText titleText = (TranslatableText) list.get(1);
            int skipLine = titleText.getKey().equals("item.modifiers.mainhand") ? 4 : 2;
            //int skipLine = 2;
            Set<String> set = new HashSet<>();

            Set<TranslatableText> noDuplicates = new HashSet<>();
            for (Text textComponent : list) {
                if (!(textComponent instanceof TranslatableText)) {
                    badlyFormattedList.add(textComponent);
                }
            }
            //System.out.println("BAD"+badlyFormattedList.get(1));

            for(int i = 2; i < badlyFormattedList.size() + 1; i++) {
                // reformat badly formatted tooltip lines into TranslatableTexts
                System.out.println("BEFORE_FORMATTING "+list.get(i));
                TranslatableText translatableText = (TranslatableText) list.get(i).getSiblings().get(0);
                TranslatableText newText = (TranslatableText) translatableText.getArgs()[1];
                properlyFormattedList.add(new TranslatableText(translatableText.getKey(), Float.parseFloat(String.valueOf(translatableText.getArgs()[0])), new TranslatableText(newText.getKey())));
            }
            list.addAll(2, properlyFormattedList);
            for(Text text : list){
                System.out.println("|bef| "+text);
            }
            badlyFormattedList.remove(0); // preserve the name of the item
            System.out.println("badlyFormattedList " + badlyFormattedList.size());
            list.removeAll(badlyFormattedList); // remove badly formatted lines
            for(Text text : list){
                System.out.println("|aft| "+text);
            }
            for (int i = 2; i < list.size(); i++) { // Skip the item name and location
                System.out.println("|all| "+list.get(i));
                TranslatableText listText = (TranslatableText) list.get(i);
                Object[] args = listText.getArgs();
                TranslatableText argText = (TranslatableText) args[1];
                //System.out.println(argText.getKey().substring(0, argText.getKey().length()-4));
                //String s = argText.getKey().substring(0, argText.getKey().length()-7);
                //System.out.println("ARG-KEY "+argText.getKey());
                if (!set.add(argText.getKey())) {
                //if (!set.add(argText.getKey().substring(0, argText.getKey().length()-7))) { // if there is more than one modifier with the same key
                    for (int j = 2; j < list.size(); j++) {
                        TranslatableText listText2 = (TranslatableText) list.get(j);
                        Object[] args2 = listText2.getArgs();
                        TranslatableText argText2 = (TranslatableText) args2[1];
                        //System.out.println(argText.getKey() + " --- " + argText2.getKey());
                        //System.out.println(listText2);
                        //System.out.println(listText);
                        //System.out.println("--------!!!----");
                        if(argText.getKey().equals(argText2.getKey())) {
                            //list.remove(listText);
                            //list.remove(listText2);
                            noDuplicates.add(listText);
                            noDuplicates.add(listText2);
                        }
                    }
                }
                //Object[] args = listText.getArgs();
                //TranslatableText argText = (TranslatableText) args[1];
                //String key = argText.getKey();
                //System.out.println(args[0] + " --- " + key);
                //System.out.println(Arrays.toString(translatableText.getArgs()));
            }

            System.out.println(" ");
            System.out.println("Dup "+noDuplicates.size()+" "+noDuplicates);
            System.out.println("set"+set);
            //if (titleText.getKey().equals("item.modifiers.mainhand")) {
                // The first two lines of held weapons are blank TextComponents with sibling TranslatableComponents
                //list.add(3, new TranslatableText("tooltip.tiered.space", new TranslatableText("attribute.modifier.equals.0", 9, new TranslatableText("attribute.name.generic.attack_damage")).formatted(Formatting.DARK_GREEN)));
            //}
/*
            for (int i = 0; i < noDuplicates.size(); i += 2) {
                //list.add(text);
                TranslatableText text = (TranslatableText) noDuplicates.toArray()[i];
                TranslatableText text_compare = (TranslatableText) noDuplicates.toArray()[i+1];
                System.out.println(text + " --- " + text_compare);
                System.out.println(text.getKey() + " --- " + text_compare.getKey());
                System.out.println(text.getArgs()[0] + " --- " + text_compare.getArgs()[0]);
                System.out.println(text);
                System.out.println(text_compare);
                System.out.println(Arrays.toString(text.getArgs()));
                System.out.println(Arrays.toString(text_compare.getArgs()));

                float val1 = Float.parseFloat(String.valueOf(text.getArgs()[0]));
                float val2 = Float.parseFloat(String.valueOf(text_compare.getArgs()[0]));
                TranslatableText translation_key = (TranslatableText) text.getArgs()[1];
                switch (text_compare.getKey()) {
                    case "attribute.modifier.plus.0" -> list.add(new TranslatableText("tooltip.tiered.add_combo", val1 + val2, translation_key, val1, val2).setStyle(attribute.getStyle()));
                    case "attribute.modifier.plus.2" -> list.add(new TranslatableText("tooltip.tiered.multiply_combo", (val1 * (val2 / 100.0f)) + val1, translation_key, val1, val2).setStyle(attribute.getStyle()));
                }
            }*/

            //list.add(new TranslatableText("tooltip.tiered.add_combo", 6, new TranslatableText("attribute.name.generic.armor"), 5, 1).setStyle(attribute.getStyle()));
            //list.add(new TranslatableText("attribute.modifier.combo.0", 6, new TranslatableText("attribute.name.generic.armor")));
            System.out.println("-------------");
        }
        /*
        else {
            for (Text text : list) {
                System.out.println(text);
            }
            System.out.println("-------------");
        }*/
    }
}
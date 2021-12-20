package Andrew6rant.tiered.mixin.client;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import Andrew6rant.tiered.Tiered;
import Andrew6rant.tiered.api.PotentialAttribute;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.Rarity;
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

@Mixin(ItemStack.class)
public abstract class ItemStackClientMixin {

    @Shadow
    public NbtCompound getOrCreateSubNbt(String key) {
        return null;
    }

    @Shadow public abstract boolean hasNbt();

    @Shadow public abstract NbtCompound getSubNbt(String key);

    @Shadow public abstract String getTranslationKey();

    @Shadow public abstract String toString();

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
/*
    @Inject(
            method = "getTooltip",
            at = @At(value = "RETURN", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    private void test(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir) {
        //List<Text> list = Lists.newArrayList();
        List<Text> list = cir.getReturnValue();

        if(this.hasNbt() && this.getSubNbt(Tiered.NBT_SUBTAG_KEY) != null && isTiered) {
            /*for (int i = 4; i < list.size(); i++) {
                // iterate over just tooltip modifiers and not name, etc

                if (list.get(i).getString().contains("Attack Speed")) {

                }
            }*/

            /*list.forEach((tooltipItem) -> {
                System.out.println(tooltipItem);
                System.out.println(tooltipItem.getString());
                System.out.println(tooltipItem.visit());
                //System.out.println(list);
                System.out.println("---");
            });*/
            //System.out.println("-------------");
            /*
            for (int i = 4; i < list.size(); i++) {
                // iterate over just tooltip modifiers and not name, etc
                TranslatableText test = (TranslatableText) list.get(i); // can't seem to combine these into one
                System.out.println(test);
                Object[] test2 = test.getArgs();
                System.out.println(Arrays.toString(test2));
                TranslatableText test3 = (TranslatableText) test2[1];
                System.out.println(test3);
                System.out.println("-");
            }*/




/*
            for (int i = 3; i < list.size(); i++) {
                // iterate over just tooltip modifiers and not name, etc
                //System.out.println(list.get(i).getString());
                //System.out.println(list.get(i));
                //System.out.println("-");
                //TranslatableText test = (TranslatableText) list.get(i);
                TranslatableText inte = (TranslatableText) list.get(i);
                System.out.println(inte.getKey());
                TranslatableText test = (TranslatableText) list.get(i); // can't seem to combine this into one
                Object[] test2 = test.getArgs();
                System.out.println(Arrays.toString(test2));
                System.out.println(test2[0]);
                TranslatableText test3 = (TranslatableText) test2[1];
                //System.out.println(Arrays.toString(test.getArgs()));
                System.out.println(test3.getKey()); // !!!! //
                //System.out.println(test.getKey());
                //System.out.println("----");
                for (int j = 3; j < list.size(); j++) {
                    TranslatableText testCompare = (TranslatableText) list.get(j); // can't seem to combine this into one
                    Object[] testCompare2 = testCompare.getArgs();
                    TranslatableText testCompare3 = (TranslatableText) testCompare2[1];
                    System.out.println(testCompare3.getKey()); // !!!! //

                    if(testCompare3.getKey().equals(test3.getKey())){
                        Text newtext = new TranslatableText(test3.getKey(), Integer.parseInt((String) test2[0]) - Integer.parseInt((String) testCompare2[0]));
                        System.out.println(newtext);
                        //list.set(i, newtext);
                    }
                }
            }
*/


            //list.forEach(listItem->{
            //    System.out.println(listItem);
            //    System.out.println(listItem.getString());
                //Language.getInstance().get(getTranslationKey());
                //TranslatableText test = (TranslatableText) listItem;
                //System.out.println(test.getKey());
                //System.out.println(listItem.asString());
                //System.out.println(listItem.asOrderedText());
                //System.out.println(listItem.getString().contains("armor"));
                //System.out.println(listItem.getString().contains("attribute"));
                //System.out.println(listItem.getStyle());
                //System.out.println(listItem.getSiblings());
            //    System.out.println("-");

            //});
            //System.out.println("--------");

/*
[TextComponent {
  text = '', siblings = [TranslatableComponent {
    key = 'tiered:dented.label', args = [], siblings = [TextComponent {
      text = ' ', siblings = [], style = Style {color = null, bold = null, italic = null, underlined = null, strikethrough = null, obfuscated = null, clickEvent = null, hoverEvent = null, insertion = null, font = minecraft: default}
    }, TranslatableComponent {
      key = 'item.minecraft.diamond_chestplate', args = [], siblings = [], style = Style {color = null, bold = null, italic = null, underlined = null, strikethrough = null, obfuscated = null, clickEvent = null, hoverEvent = null, insertion = null, font = minecraft: default}
    }], style = Style {color = gray, bold = null, italic = null, underlined = null, strikethrough = null, obfuscated = null, clickEvent = null, hoverEvent = null, insertion = null, font = minecraft: default}
  }], style = Style {color = white, bold = null, italic = null, underlined = null, strikethrough = null, obfuscated = null, clickEvent = null, hoverEvent = null, insertion = null, font = minecraft: default}
}, TextComponent {
  text = '', siblings = [], style = Style {color = null, bold = null, italic = null, underlined = null, strikethrough = null, obfuscated = null, clickEvent = null, hoverEvent = null, insertion = null, font = minecraft: default}
}, TranslatableComponent {
  key = 'item.modifiers.chest', args = [], siblings = [], style = Style {color = gray, bold = null, italic = null, underlined = null, strikethrough = null, obfuscated = null, clickEvent = null, hoverEvent = null, insertion = null, font = minecraft: default}
}, TranslatableComponent {
  key = 'attribute.modifier.plus.0', args = [8, TranslatableComponent {
    key = 'attribute.name.generic.armor', args = [], siblings = [], style = Style {color = null, bold = null, italic = null, underlined = null, strikethrough = null, obfuscated = null, clickEvent = null, hoverEvent = null, insertion = null, font = minecraft: default}
  }], siblings = [], style = Style {color = blue, bold = null, italic = null, underlined = null, strikethrough = null, obfuscated = null, clickEvent = null, hoverEvent = null, insertion = null, font = minecraft: default}
}, TranslatableComponent {
  key = 'attribute.modifier.plus.0', args = [2, TranslatableComponent {
    key = 'attribute.name.generic.armor_toughness', args = [], siblings = [], style = Style {color = null, bold = null, italic = null, underlined = null, strikethrough = null, obfuscated = null, clickEvent = null, hoverEvent = null, insertion = null, font = minecraft: default}
  }], siblings = [], style = Style {color = blue, bold = null, italic = null, underlined = null, strikethrough = null, obfuscated = null, clickEvent = null, hoverEvent = null, insertion = null, font = minecraft: default}
}, TranslatableComponent {
  key = 'attribute.modifier.take.0', args = [1, TranslatableComponent {
    key = 'attribute.name.generic.armor', args = [], siblings = [], style = Style {color = null, bold = null, italic = null, underlined = null, strikethrough = null, obfuscated = null, clickEvent = null, hoverEvent = null, insertion = null, font = minecraft: default}
  }], siblings = [], style = Style {color = red, bold = null, italic = null, underlined = null, strikethrough = null, obfuscated = null, clickEvent = null, hoverEvent = null, insertion = null, font = minecraft: default}
}]
* /
        }
    }
*/
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
        /*
        map.forEach((entityAttribute, entityAttributeModifier) -> {
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

            if(entityAttribute.getTranslationKey().equals(entityAttributeCompare.getTranslationKey())) {
                /*System.out.println("start");
                System.out.println(entityAttribute.getTranslationKey());
                System.out.println(entityAttribute.getDefaultValue());
                System.out.println(entityAttributeModifier.getName());
                System.out.println(entityAttributeModifier.getValue());
                System.out.println(entityAttributeModifier.getOperation());
                */

                //no_duplicates.put(entityAttribute, new EntityAttributeModifier(entityAttributeModifier.getName(), 10, entityAttributeModifierCompare.getOperation()));

                /*
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
                */

                //no_duplicates.put(entityAttribute, entityAttributeModifier);
                //no_duplicates.put(entityAttribute, new EntityAttributeModifier(entityAttributeModifier.getName(), combo_calc, entityAttributeModifierCompare.getOperation()));

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
                cir.setReturnValue(new TranslatableText(potentialAttribute.getID() + ".label").append(" ").append(cir.getReturnValue()).setStyle(potentialAttribute.getStyle()));
            }
        }
    }
}

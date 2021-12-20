package Andrew6rant.tiered.api;


import Andrew6rant.tiered.Tiered;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.include.com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class ModifierUtils {

    /**
     * Returns the ID of a random attribute that is valid for the given {@link Item} in {@link Identifier} form.
     * <p> If there is no valid attribute for the given {@link Item}, null is returned.
     *
     * @param item  {@link Item} to generate a random attribute for
     * @return  id of random attribute for item in {@link Identifier} form, or null if there are no valid options
     */
    public static Identifier getRandomAttributeIDFor(Item item) {
        List<Identifier> potentialAttributes = new ArrayList<>();

        // collect all valid attributes for the given item
        Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().forEach((id, attribute) -> {
            if(attribute.isValid(Registry.ITEM.getId(item))) {
                potentialAttributes.add(new Identifier(attribute.getID()));
            }
        });

        // return a random attribute if there are any, or null if there are none
        if(potentialAttributes.size() > 0) {
            return potentialAttributes.get(new Random().nextInt(potentialAttributes.size()));
        } else {
            return null;
        }
    }

    public static Identifier getWeightedAttributeIDFor(ItemStack stack) {

        List<Identifier> junkRarity = new ArrayList<>();
        List<Identifier> commonRarity = new ArrayList<>();
        List<Identifier> uncommonRarity = new ArrayList<>();
        List<Identifier> rareRarity = new ArrayList<>();
        List<Identifier> epicRarity = new ArrayList<>();
        List<Identifier> legendaryRarity = new ArrayList<>();
        List<Identifier> arcaneRarity = new ArrayList<>();
        Random rand = new Random();
        int rarityCalc = rand.nextInt(100);
        //System.out.println(rarityCalc);
        // collect all valid attributes for the given item
        Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().forEach((id, attribute) -> {
            if(attribute.isValid(Registry.ITEM.getId(stack.getItem()))) {
                //System.out.println(attribute.getRarity());
                switch (attribute.getRarity()) {
                    case "junk" -> junkRarity.add(new Identifier(attribute.getID()));
                    case "common" -> commonRarity.add(new Identifier(attribute.getID()));
                    case "uncommon" -> uncommonRarity.add(new Identifier(attribute.getID()));
                    case "rare" -> rareRarity.add(new Identifier(attribute.getID()));
                    case "epic" -> epicRarity.add(new Identifier(attribute.getID()));
                    case "legendary" -> legendaryRarity.add(new Identifier(attribute.getID()));
                    default -> arcaneRarity.add(new Identifier((attribute.getID())));
                }
            }
        });

        //System.out.println("junk: "+junkRarity);
        //System.out.println("common: "+commonRarity);
        //System.out.println("uncommon: "+uncommonRarity);
        //System.out.println("rare: "+rareRarity);
        //System.out.println("epic: "+epicRarity);
        //System.out.println("legendary: "+legendaryRarity);
        //System.out.println("arcane: "+arcaneRarity);
        // return a weighted attribute if there are any, or null if there are none
        if (rarityCalc <= 25) {
            if(junkRarity.size() > 0) {
                return junkRarity.get(new Random().nextInt(junkRarity.size()));
            } else {
                return null;
            }
        } else if (rarityCalc <= 60) {
            if(commonRarity.size() > 0) {
                return commonRarity.get(new Random().nextInt(commonRarity.size()));
            } else {
                return null;
            }
        } else if (rarityCalc <= 80) {
            if(uncommonRarity.size() > 0) {
                return uncommonRarity.get(new Random().nextInt(uncommonRarity.size()));
            } else {
                return null;
            }
        } else if (rarityCalc <= 92) {
            if(rareRarity.size() > 0) {
                return rareRarity.get(new Random().nextInt(rareRarity.size()));
            } else {
                return null;
            }
        } else if (rarityCalc <= 98) {
            if(epicRarity.size() > 0) {
                return epicRarity.get(new Random().nextInt(epicRarity.size()));
            } else {
                return null;
            }
        } else {
            if(legendaryRarity.size() > 0) {
                return legendaryRarity.get(new Random().nextInt(legendaryRarity.size()));
            } else {
                return null;
            }
        }

        /*if(potentialAttributes.size() > 0) {
            //for (int i = 0; i <= potentialAttributes.size(); i++) {
            //    System.out.println(potentialAttributes);
            //}
            return potentialAttributes.get(new Random().nextInt(potentialAttributes.size()));
        } else {
            return null;
        }*/
    }

    public static Identifier getWeightedAttributeIDNoDuplicates(ItemStack stack) {
        List<Identifier> junkRarity = new ArrayList<>();
        List<Identifier> commonRarity = new ArrayList<>();
        List<Identifier> uncommonRarity = new ArrayList<>();
        List<Identifier> rareRarity = new ArrayList<>();
        List<Identifier> epicRarity = new ArrayList<>();
        List<Identifier> legendaryRarity = new ArrayList<>();
        List<Identifier> arcaneRarity = new ArrayList<>();
        Random rand = new Random();
        int rarityCalc = rand.nextInt(100);
        //System.out.println(rarityCalc);
        // collect all valid attributes for the given item
        Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().forEach((id, attribute) -> {
            if(attribute.isValid(Registry.ITEM.getId(stack.getItem()))) {
                switch (attribute.getRarity()) {
                    case "junk" -> junkRarity.add(new Identifier(attribute.getID()));
                    case "common" -> commonRarity.add(new Identifier(attribute.getID()));
                    case "uncommon" -> uncommonRarity.add(new Identifier(attribute.getID()));
                    case "rare" -> rareRarity.add(new Identifier(attribute.getID()));
                    case "epic" -> epicRarity.add(new Identifier(attribute.getID()));
                    case "legendary" -> legendaryRarity.add(new Identifier(attribute.getID()));
                    default -> arcaneRarity.add(new Identifier((attribute.getID())));
                }
                //System.out.println("item:");
                //System.out.println(Objects.requireNonNull(attribute.getStyle()));
                //System.out.println(Objects.requireNonNull(attribute.getStyle().get(0)));
                //System.out.println(Objects.requireNonNull(attribute.getStyle().get(1)));
                //System.out.println(Objects.requireNonNull(attribute.getStyle().get(2)));

                /*if (attribute.getStyle().get(0).getColor().equals(TextColor.fromFormatting(Formatting.GRAY))){
                    junkRarity.add(new Identifier(attribute.getID()));
                    //System.out.println("junk: "+junkRarity);
                } else if (attribute.getStyle().get(0).getColor().equals(TextColor.fromFormatting(Formatting.WHITE))){
                    commonRarity.add(new Identifier(attribute.getID()));
                    //System.out.println("common: "+commonRarity);
                } else if (attribute.getStyle().get(0).getColor().equals(TextColor.fromFormatting(Formatting.GREEN))){
                    uncommonRarity.add(new Identifier(attribute.getID()));
                    //System.out.println("uncommon: "+uncommonRarity);
                } else if (attribute.getStyle().get(0).getColor().equals(TextColor.fromFormatting(Formatting.BLUE))){
                    rareRarity.add(new Identifier(attribute.getID()));
                    //System.out.println("rare: "+rareRarity);
                } else if (attribute.getStyle().get(0).getColor().equals(TextColor.fromFormatting(Formatting.DARK_PURPLE))){
                    epicRarity.add(new Identifier(attribute.getID()));
                    //System.out.println("epic: "+epicRarity);
                } else if (attribute.getStyle().get(0).getColor().equals(TextColor.fromFormatting(Formatting.GOLD))){
                    legendaryRarity.add(new Identifier(attribute.getID()));
                    //System.out.println("legendary: "+legendaryRarity);
                } else{
                    arcaneRarity.add(new Identifier(attribute.getID()));
                    //System.out.println("arcane: "+arcaneRarity);
                }*/
            }
        });
        // return a weighted attribute if there are any, or null if there are none
        if (rarityCalc <= 25) {
            //System.out.println("junk get");
            return getIdentifier(stack, junkRarity);
        } else if (rarityCalc <= 60) {
            return getIdentifier(stack, commonRarity);
        } else if (rarityCalc <= 80) {
            return getIdentifier(stack, uncommonRarity);
        } else if (rarityCalc <= 92) {
            return getIdentifier(stack, rareRarity);
        } else if (rarityCalc <= 98) {
            return getIdentifier(stack, epicRarity);
        } else {
            return getIdentifier(stack, legendaryRarity);
        }
    }

    @Nullable
    private static Identifier getIdentifier(ItemStack stack, List<Identifier> legendaryRarity) {
        if(legendaryRarity.size() > 0) {
            Identifier newModifier = legendaryRarity.get(new Random().nextInt(legendaryRarity.size()));
            if (Objects.requireNonNull(Objects.requireNonNull(stack.getSubNbt(Tiered.NBT_SUBTAG_KEY)).get("Tier")).toString().equals(newModifier.toString())) {
                return legendaryRarity.get(new Random().nextInt(legendaryRarity.size()));
            }
            else {
                return newModifier;
            }
        } else {
            return null;
        }
    }

    private ModifierUtils() {
        // no-op
    }
}
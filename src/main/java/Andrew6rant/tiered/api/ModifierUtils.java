package Andrew6rant.tiered.api;


import Andrew6rant.tiered.Tiered;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

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

        //Identifier tier = new Identifier(stack.getSubNbt(Tiered.NBT_SUBTAG_KEY).getString(Tiered.NBT_SUBTAG_DATA_KEY));
        //System.out.println(tier);
        //PotentialAttribute potentialAttribute = Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().get(tier);
        //System.out.println(potentialAttribute);
        Random rand = new Random();
        int rarityCalc = rand.nextInt(100);
        //System.out.println(rarityCalc);
        // collect all valid attributes for the given item
        Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().forEach((id, attribute) -> {
            if(attribute.isValid(Registry.ITEM.getId(stack.getItem()))) {
                //TextColor is incompatible with switch statements :(
                if (attribute.getStyle().getColor().equals(TextColor.fromFormatting(Formatting.GRAY))){
                    junkRarity.add(new Identifier(attribute.getID()));
                    //System.out.println("junk: "+junkRarity);
                } else if (attribute.getStyle().getColor().equals(TextColor.fromFormatting(Formatting.WHITE))){
                    commonRarity.add(new Identifier(attribute.getID()));
                    //System.out.println("common: "+commonRarity);
                } else if (attribute.getStyle().getColor().equals(TextColor.fromFormatting(Formatting.GREEN))){
                    uncommonRarity.add(new Identifier(attribute.getID()));
                    //System.out.println("uncommon: "+uncommonRarity);
                } else if (attribute.getStyle().getColor().equals(TextColor.fromFormatting(Formatting.BLUE))){
                    rareRarity.add(new Identifier(attribute.getID()));
                    //System.out.println("rare: "+rareRarity);
                } else if (attribute.getStyle().getColor().equals(TextColor.fromFormatting(Formatting.DARK_PURPLE))){
                    epicRarity.add(new Identifier(attribute.getID()));
                    //System.out.println("epic: "+epicRarity);
                } else if (attribute.getStyle().getColor().equals(TextColor.fromFormatting(Formatting.GOLD))){
                    legendaryRarity.add(new Identifier(attribute.getID()));
                    //System.out.println("legendary: "+legendaryRarity);
                } else{
                    arcaneRarity.add(new Identifier(attribute.getID()));
                    //System.out.println("arcane: "+arcaneRarity);
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
            //System.out.println("junk get");
            if(junkRarity.size() > 0) {
                return junkRarity.get(new Random().nextInt(junkRarity.size()));
            } else {
                return null;
            }
        } else if (rarityCalc <= 60) {
            //System.out.println("common get");
            if(commonRarity.size() > 0) {
                //System.out.println("common return");
                return commonRarity.get(new Random().nextInt(commonRarity.size()));
            } else {
                return null;
            }
        } else if (rarityCalc <= 80) {
            //System.out.println("uncommon get");
            if(uncommonRarity.size() > 0) {
                //System.out.println("uncommon return");
                return uncommonRarity.get(new Random().nextInt(uncommonRarity.size()));
            } else {
                return null;
            }
        } else if (rarityCalc <= 92) {
            //System.out.println("rare get");
            if(rareRarity.size() > 0) {
                //System.out.println("rare return");
                return rareRarity.get(new Random().nextInt(rareRarity.size()));
            } else {
                return null;
            }
        } else if (rarityCalc <= 98) {
            //System.out.println("epic get");
            if(epicRarity.size() > 0) {
                //System.out.println("epic return");
                return epicRarity.get(new Random().nextInt(epicRarity.size()));
            } else {
                return null;
            }
        } else {
            //System.out.println("legendary get");
            if(legendaryRarity.size() > 0) {
                //System.out.println("legendary return");
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
        System.out.println(rarityCalc);
        // collect all valid attributes for the given item
        Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().forEach((id, attribute) -> {
            if(attribute.isValid(Registry.ITEM.getId(stack.getItem()))) {
                //TextColor is incompatible with switch statements :(
                if (attribute.getStyle().getColor().equals(TextColor.fromFormatting(Formatting.GRAY))){
                    junkRarity.add(new Identifier(attribute.getID()));
                    //System.out.println("junk: "+junkRarity);
                } else if (attribute.getStyle().getColor().equals(TextColor.fromFormatting(Formatting.WHITE))){
                    commonRarity.add(new Identifier(attribute.getID()));
                    //System.out.println("common: "+commonRarity);
                } else if (attribute.getStyle().getColor().equals(TextColor.fromFormatting(Formatting.GREEN))){
                    uncommonRarity.add(new Identifier(attribute.getID()));
                    //System.out.println("uncommon: "+uncommonRarity);
                } else if (attribute.getStyle().getColor().equals(TextColor.fromFormatting(Formatting.BLUE))){
                    rareRarity.add(new Identifier(attribute.getID()));
                    //System.out.println("rare: "+rareRarity);
                } else if (attribute.getStyle().getColor().equals(TextColor.fromFormatting(Formatting.DARK_PURPLE))){
                    epicRarity.add(new Identifier(attribute.getID()));
                    //System.out.println("epic: "+epicRarity);
                } else if (attribute.getStyle().getColor().equals(TextColor.fromFormatting(Formatting.GOLD))){
                    legendaryRarity.add(new Identifier(attribute.getID()));
                    //System.out.println("legendary: "+legendaryRarity);
                } else{
                    arcaneRarity.add(new Identifier(attribute.getID()));
                    //System.out.println("arcane: "+arcaneRarity);
                }
            }
        });
        // return a weighted attribute if there are any, or null if there are none
        if (rarityCalc <= 25) {
            //System.out.println("junk get");
            if(junkRarity.size() > 0) {
                //System.out.println(Objects.requireNonNull(stack.getSubNbt(Tiered.NBT_SUBTAG_KEY)).get("Tier"));//
                Identifier newModifier = junkRarity.get(new Random().nextInt(junkRarity.size()));
                //System.out.println(newModifier);//
                //System.out.println(Objects.requireNonNull(Objects.requireNonNull(stack.getSubNbt(Tiered.NBT_SUBTAG_KEY)).get("Tier")).toString().equals(newModifier.toString()));//
                //Give one more chance if the modifier is exactly the same
                if (Objects.requireNonNull(Objects.requireNonNull(stack.getSubNbt(Tiered.NBT_SUBTAG_KEY)).get("Tier")).toString().equals(newModifier.toString())) {
                    return junkRarity.get(new Random().nextInt(junkRarity.size()));
                }
                else {
                    return newModifier;
                }
            } else {
                return null;
            }
        } else if (rarityCalc <= 60) {
            if(commonRarity.size() > 0) {
                Identifier newModifier = commonRarity.get(new Random().nextInt(commonRarity.size()));
                if (Objects.requireNonNull(Objects.requireNonNull(stack.getSubNbt(Tiered.NBT_SUBTAG_KEY)).get("Tier")).toString().equals(newModifier.toString())) {
                    return commonRarity.get(new Random().nextInt(commonRarity.size()));
                }
                else {
                    return newModifier;
                }
            } else {
                return null;
            }
        } else if (rarityCalc <= 80) {
            if(uncommonRarity.size() > 0) {
                Identifier newModifier = uncommonRarity.get(new Random().nextInt(uncommonRarity.size()));
                if (Objects.requireNonNull(Objects.requireNonNull(stack.getSubNbt(Tiered.NBT_SUBTAG_KEY)).get("Tier")).toString().equals(newModifier.toString())) {
                    return uncommonRarity.get(new Random().nextInt(uncommonRarity.size()));
                }
                else {
                    return newModifier;
                }
            } else {
                return null;
            }
        } else if (rarityCalc <= 92) {
            if(rareRarity.size() > 0) {
                Identifier newModifier = rareRarity.get(new Random().nextInt(rareRarity.size()));
                if (Objects.requireNonNull(Objects.requireNonNull(stack.getSubNbt(Tiered.NBT_SUBTAG_KEY)).get("Tier")).toString().equals(newModifier.toString())) {
                    return rareRarity.get(new Random().nextInt(rareRarity.size()));
                }
                else {
                    return newModifier;
                }
            } else {
                return null;
            }
        } else if (rarityCalc <= 98) {
            if(epicRarity.size() > 0) {
                Identifier newModifier = epicRarity.get(new Random().nextInt(epicRarity.size()));
                if (Objects.requireNonNull(Objects.requireNonNull(stack.getSubNbt(Tiered.NBT_SUBTAG_KEY)).get("Tier")).toString().equals(newModifier.toString())) {
                    return epicRarity.get(new Random().nextInt(epicRarity.size()));
                }
                else {
                    return newModifier;
                }
            } else {
                return null;
            }
        } else {
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
    }

    private ModifierUtils() {
        // no-op
    }
}
# Tiered [heavy beta]

Tiered is a Fabric mod inspired by [Quality Tools](https://www.curseforge.com/minecraft/mc-mods/quality-tools). Every tool you make will have a special modifier, as seen below:

![tiered](https://user-images.githubusercontent.com/57331134/142939375-8ea3935d-ec9d-4f47-aea7-8cfb090a2159.png)

Tiered was originally made by Draylar, and is being updated and maintained by Andrew6rant.

### Customization

Tiered is almost entirely data-driven, which means you can add, modify, and remove modifiers as you see fit. The base path for modifiers is `data/modid/item_attributes`, and tiered modifiers are stored under the modid of tiered. Here's an example modifier called "Hasteful," which grants additional dig speed when any of the valid tools are held:
```json
{
  "id": "tiered:hasteful",
  "verifiers": [
    { "tag": "c:pickaxes" },
    { "tag": "c:shovels" },
    { "tag": "c:axes" }
  ],
  "style": {
    "color": "green"
  },
  "rarity": "uncommon",
  "tooltip_image": 2,
  "tooltip_border_start":  4283826005,
  "tooltip_border_end":  4279880217,
  "reforge_cost": 8,
  "attributes": [
    {
      "type": "generic.dig_speed",
      "modifier": {
        "name": "tiered:hasteful",
        "operation": "MULTIPLY_TOTAL",
        "amount": 0.10
      },
      "optional_equipment_slots": [
        "MAINHAND"
      ]
    }
  ]
}
```

Tiered currently provides 3 custom attributes: Dig Speed, Crit Chance, and Size. Dig Speed increases the speed of your block breaking (think: haste), Crit Chance offers an additional random chance to crit when using a tool, and Size changes the held and dropped item rendering.

### Verifiers

A verifier (specified in the "verifiers" array of your modifier json file) defines whether or not a given tag or tool is valid for the modifier. 

A specific item ID can be specified with:

```json

"id": "minecraft:apple"

```

and a tag can be specified with:

```json

"tag": "c:helmets"

```

Tiered provides 6 tags (`c:helmets`, `c:chestplates`, `c:leggings`, `c:boots`, `c:shields`, and `c:fishing_rods`) for your convenience.

### Reforging Cost

Reforging an item's modifier costs experience points (not levels). Each item's cost can be configured individually. The default setup is a Terraria-like system where higher rarities cost more to reforge. Use a value of 0 to make the reforge free, and a negative number to add experience to the player instead of removing it.

### Tooltips

Tiered provides four customization options regarding tooltips.

- `color` changes the text color

- `tooltip_image` specifies the border style (see below)

- `tooltip_border_start` and `tooltip_border_end` determine the top and bottom line colors of the border, respectively.

Here is the formatting for the border style. This image allows for up to 16 styles (0-15):

![debug](https://user-images.githubusercontent.com/57331134/145140352-ae70ddf9-6141-4288-a4b4-1868277498be.png)

And here is how it looks in-game:

![debug_inventory](https://user-images.githubusercontent.com/57331134/145140685-ce11ff58-b277-4029-b588-83f0db4b237d.png)

In that image, the tooltip_border_start and tooltip_border_end colors are 4280615006 and 4279762996 (#25005e, #180034), for purple at the top and dark purple at the bottom. These are the same values Vanilla Minecraft uses. The format is 0xAARRGGBB converted to decimal (0xff25005e to decimal is 4280615006).

You can use any sized image (with a power of 2 resolution (128x, 256x, 512x, etc, like Vanilla Minecraft) to make as many borders as you like. Here is an example with 64 styles:

![debug-big](https://user-images.githubusercontent.com/57331134/145140359-26c8014a-dcfc-4fbf-8364-41e7235bcbd9.png)


### License
Tiered is licensed under MIT. You are free to use the code inside this repo as you want.

# [More information is available on the wiki](https://github.com/Andrew6rant/tiered/wiki)

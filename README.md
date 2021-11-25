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
    {
      "tag": "c:pickaxes"
    },
    {
      "tag": "c:shovels"
    },
    {
      "tag": "c:axes"
    }
  ],
  "style": {
    "color": "green"
  },
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


### License
Tiered is licensed under MIT. You are free to use the code inside this repo as you want.

# [More information is available on the wiki](https://github.com/Andrew6rant/tiered/wiki)

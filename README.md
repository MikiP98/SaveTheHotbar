# SaveTheHotbar!

### IMPORTANT: `keepInventory` GAMERULE MUST BE SET TO `true` FOR THE MOD TO WORK!!!

## Description

SaveTheHotbar! is an balanced alternative to `keepInventory` and gravestone type mods.  
It allows you to keep the armor or the hotbar after death and drop the rest, 
so that you don't lose the armament required to get your loot back!

## Configuration

You can access and edit the config in game entering it's configuration through the Mod Menu.  
**`SaveTheHotbar!` can be configured with the following options:**

### saveHotbar

*Defaults to `true`*  
If `true`, the hotbar will be kept after death.

### saveArmor

*Defaults to `true`*  
If `true`, the armor will be kept after death.

### saveSecondHand

*Defaults to `true`*  
If `true`, the second hand will be kept after death.

### randomSpread

*Defaults to `false`*  
If `true`, all the dropped items after death will be spread randomly in all directions.  
If `false`, all the item will stay in the exact position of your death and can only fall downwards.  
Does not do anything if `containDrop` is `true`.

### randomDropChance

*Defaults to `0.0` (0.0 -> 0%; 1.0 -> 100%)*  
Adds a random chance to drop an otherwise kept item  
E.g. Even when the armor should be kept, each piece will still have a change to be dropped instead, increasing the risk!  


### containDrop

*Defaults to `false`*  
If `true`, all the non-kept items will be stored in a block specified by the `containDropMode` option.  
If `false`, all the non-kept items will be dropped after death.

### containDropMode

*Defaults to `"SACK"`*  
The block where the non-kept items will be stored if `containDrop` is `true`.
- `"SACK"` *(not-implemented yet)*  
After player death a sack will be spawned, containing all the non-kept items.  
Drops items on destruction. Can be waterlogged. Does not drop itself nor any XP.
- `"GRAVE"`  
__**[*GRAVESTONES*](https://modrinth.com/mod/pneumono_gravestones)&nbsp; MOD REQUIRED!!!**__  
After player death a grave from the `Gravestones` mod will be spawned,  
containing the non-kept items and XP according to its configuration.

### Default JSON config file

```JSON
{
    "saveHotbar": true,
    "saveArmor": true,
    "saveSecondHand": true,
    "randomSpread": false,
    "containDrop": true,
    "randomDropChance": 0.0,
    "rarityDropChanceDecrease": 0.0,
    "containDropMode": "SACK"
}
```
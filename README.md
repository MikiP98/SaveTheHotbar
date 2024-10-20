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

### keepExperience

*Defaults to `false`*
If `true`, the experience will be kept after death.
If `false`, the experience will be dropped like in vanilla.


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
- `"SACK"`  
  After player death, a sack will be spawned, containing all the non-kept items.  
  Drops items on destruction. Can be waterlogged. Does not drop itself nor any XP.
- `"SKELETON_HEAD"` / `"ZOMBIE_HEAD"` / `"RANDOM_HEAD"`   
  After player death, **mob head grave** will be spawned, containing all the non-kept items.  
  Drops items on destruction. Does not drop itself nor any XP.  
  The Grave will be spawned in the first found valid spawn location, which is a full block with replaceable block on top.  
  If such location inside `mobGraveMaxSpawnRadius` radius isn't found, a `SACK` grave will be spawned instead.
- `"GRAVE"`  
  __**[*GRAVESTONES*](https://modrinth.com/mod/pneumono_gravestones)&nbsp; MOD REQUIRED!!!**__  
  After player death, a grave from the `Gravestones` mod will be spawned,  
  containing the non-kept items and XP according to its configuration.

### mobGraveMaxSpawnRadius

*Defaults to `32`*  
The maximum search radius for a valid mob grave spawning position.  
If no valid place will be found, a Sack will be spawned instead.


### logDeathCoordinatesInChat

*Defaults to `false`*  
If `true`, the coordinates of the death will be logged in the chat.

### logGraveCoordinatesInChat

*Defaults to `false`*  
If `true`, the coordinates of the grave will be logged in the chat.

### Default JSON config file

```JSON
{
    "saveHotbar": true,
    "saveArmor": true,
    "saveSecondHand": true,
    "randomSpread": false,
    "containDrop": true,
    "logDeathCoordinatesInChat": false,
    "logGraveCoordinatesInChat": false,
    "randomDropChance": 0.0,
    "rarityDropChanceDecrease": 2.0,
    "containDropMode": "GRAVE",
    "mobGraveMaxSpawnRadius": 32
}
```
# SaveTheHotbar!

**`keepInventory` GameRule should be set to `true`** for the mod to work

<hr>

## Description

**`SaveTheHotbar!`** is a balanced alternative to `keepInventory` and gravestone type mods.  
It allows you to keep the armour or the hotbar after death and drop the rest of the inventory,  
so that you don't lose the armament required to get the rest of your loot back!

<br>
<hr>

## Configuration

You can access and edit the config in game entering it's configuration through the Mod Menu.  
**`SaveTheHotbar!` can be configured with the following options:**

<hr>

### enabled

*Defaults to `true`*  
If `false`, the mod will become disabled, just as if it wasn't installed.

<hr>


### saveHotbar

*Defaults to `true`*  
If `true`, the hotbar will be kept after death.

### saveArmor

*Defaults to `true`*  
If `true`, the armor will be kept after death.

### saveSecondHand

*Defaults to `true`*  
If `true`, the second hand will be kept after death.


### saveArsenal

*Defaults to `true`*
If `true`, the Arsenal's back slot will be kept after death.  
Does not do anything if the `Arsenal` mod is not installed


### experienceBehaviour

*Defaults to `false`*  
If `DROP`, the experience will be dropped after death.  
If `STORE`, the experience will be stored in the *"grave"* block. *(if `containDrop` is `false`, EXP will drop instead)*  
If `KEEP`, the player will keep the experience.  
No matter which mode is selected, the new EXP amount after death will be calculated according to `experienceCalculationMode` setting

### experienceCalculationMode

*Defaults to `FRACTION`*  
If `ALL`, the experience stays unchanged.  
If `FRACTION`, the experience is multiplied by `experienceFraction`.  
If `VANILLA`, the new experience value is calculated with the vanilla formula -> `new_exp_points = {player_level} * 7`.

### experienceFraction

*Defaults to `0.2`*  
The fraction of experience that should be dropped/stored/kept after death when `experienceCalculationMode` is set to `FRACTION`


### randomDropChance

*Defaults to `0.0` (0.0 -> 0%; 1.0 -> 100%)*  
Adds a random chance to drop an otherwise kept item  
E.g. Even when the armor should be kept, each piece will still have a change to be dropped instead, increasing the risk!

### rarityDropChanceDecrease

*Defaults to `0.2` (0.0 -> 0%; 1.0 -> 100%)*
A percentage by which the random drop chance will be decreased according to item's rarity  
(e.g. rdc = 20%, rdcd = 20%, UNCOMMON item will have 16% change to drop)

### luckDropChanceDecrease

*Defaults to `0.2` (0.0 -> 0%; 1.0 -> 100%)*
A percentage by which the random drop chance will be decreased according to player's luck effect  
(e.g. rdc = 20%, ldcd = 20%, with luck 1, item will have 16% change to drop)

<hr>


### randomSpread

*Defaults to `false`*  
If `true`, all the dropped items after death will be spread randomly in all directions.  
If `false`, all the item will stay in the exact position of your death and can only fall downwards.  
Does not do anything if `containDrop` is `true`.

### containDrop

*Defaults to `false`*  
If `true`, all the non-kept items will be stored in a block specified by the `containDropMode` option.  
If `false`, all the non-kept items will be dropped after death.

### containDropMode

*Defaults to `"SACK"`*  
The block where the non-kept items will be stored if `containDrop` is `true`.
- `"SACK"`  
  After player death, a **sack** will be spawned, containing all the non-kept items.  
  Drops items on destruction. Can be waterlogged. Does not drop itself.
- `"SKELETON_HEAD"` / `"ZOMBIE_HEAD"` / `"RANDOM_HEAD"`   
  After player death, **mob head grave** will be spawned, containing all the non-kept items.  
  Drops items on destruction. Does not drop itself.  
  The Grave will be spawned in the first found valid spawn location, which is a full block with replaceable block on top.  
  If such location inside `mobGraveMaxSpawnRadius` radius isn't found, a `SACK` grave will be spawned instead.
- `"GRAVE"`  
  __**[*GRAVESTONES*](https://modrinth.com/mod/pneumono_gravestones)&nbsp; MOD REQUIRED!!!**__  
  After player death, a grave from the `Gravestones` mod will be spawned,  
  containing the non-kept items and XP according to its configuration.

### mobGraveMaxSpawnRadius
*Defaults to `1`*
The maximum search radius for a valid sack spawning position.  
If no valid place is found, a Sack will be spawned directly at the death position replacing whatever block was there.

### mobGraveMaxSpawnRadius

*Defaults to `32`*  
The maximum search radius for a valid mob grave spawning position.  
If no valid place is found, a Sack will be spawned instead.

### allowGravesToSpawnOnSlabs

*Defaults to `false`*
If `true` a top slab block will also be considered a valid grave spawning block

<hr>


### logDeathCoordinatesInChat

*Defaults to `false`*  
If `true`, the coordinates of the death will be logged in the chat.

### logGraveCoordinatesInChat

*Defaults to `false`*
If `true`, the coordinates of the grave will be logged in the chat.

<br>
<hr>

## Roadmap:

- Make config translations translatable through translation files
- Update Pneumono Gravestones support to the latest Pneumono Gravestones version
  - Last working version of PG is `1.0.12-1.20.1`
  - Need to wait for an API update
- Add an option so that after internal grave destruction, items are given to the player instead of being dropped
  - Add an option for them to be returned into their original slot they were taken from
- Port to newer MC versions:
  - 1.20.6
  - 1.21.1
  - 1.21.4
  - 1.21.6
  - 1.21.10
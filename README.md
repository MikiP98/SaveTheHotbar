# SaveTheHotbar!

**`keepInventory` GameRule should be set to `true`** for the mod to work

<hr>

## Description

**`SaveTheHotbar!`** is a balanced alternative to `keepInventory` and gravestone type mods  
It allows you to keep the armour and the hotbar after death while dropping the rest of the inventory  
This way you don't lose the armament required to get the rest of your loot back!

### High configurability

You can control which slots and item types should be kept after death  
What should happen to your exp and the non-kept items
Prevent items from disappearing by containing them in a grave like block.

### Default behaviour:

After death you will keep all the items located in your:
- hotbar
- armour slots
- second hand
- modded slots

The non kept items will be dropped to the ground in the exact place of death, without getting thrown all over the place  
The experience loss will be reduced, and the exp will drop in the place of death.

<br>
<hr>

## Configuration

You can access and edit the config in game by entering the configuration through the Mod Menu

<hr>

### Enabled

*Defaults to `true`*  
A global toggle for the mod

### Item Keeping Logic Operator

*Defaults to `AND`*
Controls what conditions have to be met for an item to be kept after death:
- `AND` -> Item needs to be in saved slot __**AND**__ of saved category to be kept after death
- `OR` -> Item needs to be in saved slot __**OR**__ of saved category to be kept after death

### Slot Control

Category including the configuration of which slots should be kept after death  
This includes:
- Hotbar
- Armour Slots
- Second hand
- {modded slots}

### Item Type Control

Category including the configuration of which item types should be kept after death  
Currently that includes:
- Tools
- Weapons
- Ammunition
- Armour
- Equipment
- Food
- Potions
- Light sources
- Other

### Exp Control

Category controlling what should happen to player experience after death

### Random Drop Control

Category for introducing a risc of otherwise kept items to drop randomly on death, 
and how things like the *Luck* effect affect this chance

### Drop Control

Category controlling what happens to all the non-kept items on death  
Should the items be dropped or contained?  
Should the dropped items be spread around?
Where should the grave like container spawn?

### Util

Category containing optional quality of life improvements like logging death coordinates in chat and a ***/back*** command

<br>
<hr>

## Roadmap:

### Done:

- Update Pneumono Gravestones support to the latest Pneumono Gravestones version
- Make config translations translatable through translation files

### TODO:

- Add an option so that after internal grave destruction, items are given to the player instead of being dropped
  - Add an option for them to be returned into their original slot they were taken from
- Add item filtering by type: weapon, armour, food, potion, etc.
- Colour the config tooltips
- Add optional '/back' command that teleport player to the last player location
  - Add an optional parameter that will teleport the player to an older death location
- Port to newer MC versions:
  - 1.20.6
  - 1.21.1
  - 1.21.4
  - 1.21.6
  - 1.21.10
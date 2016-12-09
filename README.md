Sync
====================

Crafting recipes:

<!---
I know the sync items being crafted in the image aren't great, feel free to fix it, the .pdn is at https://www.dropbox.com/s/39po31vm5q2s2ea/Readme_Recipes.pdn?dl=0
-->
![Alt text](http://i.imgur.com/9i2BQnX.png "Crafting recipes")
In hardmode, swap out the enderpearl for a beacon in sync core recipe.

====================

Mod use:

This mod provides clones, or as we like to call it, "shells".
These shells are basically a new individual, with their own
inventory, experience level, and even gamemode.

However, what they lack, is a mind to control them. That's where
the player comes in. Each shell is biometrically tied to the
player who's sample is used to create it, and will allow the
player to "sync" their mind to the other shell, essentially
creating multiple player instances.

Place a shell constructor down and right click it to provide a sample
(warning, this hurts!). The shell constructor cannot work
without power, so put a treadmill down next to it and lure 
a Pig or a Wolf to the center of the treadmill to generate 
Piggawatts. A lead will help out out with this (you will get
it back). The shell constructor will begin building a shell.

Once the shell is constructed, you need to place down a shell
storage unit and supply redstone power to it. The indicator 
will switch to green and you can walk into the storage unit 
to activate it. You will see a radial menu pop up with the
list of available shells and their positions. Select the shell
in the constructor (labelled with the "Done" indicator) and
watch as you are synced into the new shell.

Shell syncing works cross dimensional too! Get to the nether 
and back through a shell! However, you cannot enter or leave
shells in the end due to.... technical difficulties.

By default, shells provide an "extra life" to the player.
When the shell you are using dies, you will immediately resync
to the nearest shell available. You can mark a preference shell
by right clicking a storage unit with a bed, labelling it as
a home storage unit. You can also name storage units with
a name tag. This "extra life" feature works in Hardcore mode,
so if you have an active backup shell you will not be forced
to abandon a hardcore world, but instead resync to another shell.

====================

For modders:

If you wish for your entity to be able to used on the treadmill,
you need to send an IMC to "Sync" with the key as "treadmill" and the
message in the format classPath:power. classPath has to include
the packages as well eg net.minecraft.entity.passive.EntityWolf.
Power has to be an integer.

Example:

FMLInterModComms.sendMessage("Sync", "treadmill", "net.minecraft.entity.passive.EntityWolf:4");

Will register EntityWolf with the power of 4

====================

Trivia:

This mod is loosely based off CorridorDigital's Sync web series
(go check it out, seriously!), but the idea for it came from
GTA V's character switching.
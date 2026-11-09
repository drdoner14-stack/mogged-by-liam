# Mogged by Liam

Fabric client mod for Minecraft 1.21.11. Press Right Shift in game to open the module menu.

## Get the .jar without installing anything

1. Create a new repository on GitHub and upload everything in this folder
   (including the hidden `.github` folder).
2. Open the repo's **Actions** tab and wait for "Build Mogged by Liam" to finish
   (or press "Run workflow").
3. Open the finished run and download the **Mogged-by-Liam** artifact. It contains the .jar.
4. Put the .jar in `.minecraft/mods` together with Fabric API.

## Build locally

Install JDK 21 and Gradle 9.2.1, then run `gradle build`.
The jar is written to `build/libs/` (use the one without `-sources`).

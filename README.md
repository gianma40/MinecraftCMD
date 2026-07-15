# MinecraftCMD
A lightweight command-line Minecraft launcher written in Java.

![MinecraftCMD Screenshot](https://i.imgur.com/t5UT8Sa.png)

MinecraftCMD allows you to launch custom Minecraft `.jar` files directly from the launcher, making it easier to test, run and experiment with old Minecraft versions and custom builds.

## Features 🚀
- 🚀 Launch Minecraft `.jar` files from a Java-based launcher
- ☕ Java-based launcher with Java version selection
- 📦 Supports custom Minecraft jars
- 🖥️ Designed for retro Minecraft testing and development
- 🔧 Simple and easy-to-use interface

## Usage
Run MinecraftCMD:
1. Open `Minecraft-Launcher.exe`
2. The launcher will start
3. Select your Java version
4. Select your Minecraft version
5. Click **Play**

The launcher will load the selected Minecraft `.jar` file and start it using Java.

> ⚠️ **WARNING: Adding Custom Versions**
> 
> If you want to add your own custom Minecraft versions, you **must** use the `RimuoviMetaInf.bat` script first. Failing to do so may cause the game to crash due to integrity checks (digital signatures).
> 
> **How to use it:**
> 1. Take the Minecraft `.jar` file of the version you wish to add.
> 2. **Drag and drop** the `.jar` file directly onto `RimuoviMetaInf.bat`.
> 3. The script will automatically remove the `META-INF` folder from the package.
> 4. Move the newly modified `.jar` file into the **`versions`** folder inside the launcher directory.

## Requirements
- Java Runtime Environment (JRE) or Java Development Kit (JDK)
- A compatible Minecraft `.jar` file, tested from rd-132211 to release 1.2.5

The JDK used by this launcher will be included inside the `.java` folder.
For old Minecraft versions, the required Java version may vary.

Examples:
- Minecraft Classic / Alpha / Beta → Java 5/6/7/8 depending on the version
- Modern Minecraft → newer Java versions

## Legacy Support
MinecraftCMD is made with legacy Minecraft experimentation in mind.
It can be used for testing:
- Classic Minecraft versions
- Old client jars
- Custom Minecraft builds
- Historical Minecraft projects

Future versions may include automatic detection of old entry points such as:
- `MinecraftApplet`
- `RubyDung`
- Legacy Minecraft main classes

## Installation
1. Download the latest release
2. Either extract the files or install with the setup wizard
3. Open the launcher folder
4. Run `Minecraft-Launcher.exe`

Example:
```bash
cd MinecraftCMD
Minecraft-Launcher

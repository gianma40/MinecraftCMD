# MinecraftCMD

A lightweight command-line Minecraft launcher written in Java.

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

## Requirements

- Java Runtime Environment (JRE) or Java Development Kit (JDK)
- A compatible Minecraft `.jar` file

The JDK used by this launcher will be included inside the .java folder.

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

MinecraftApplet
RubyDung
Legacy Minecraft main classes


## Installation

1. Download the latest release
2. Either extract the files or install with the setup wizard
3. Open the launcher folder
4. Run Minecraft-Launcher.exe

Example:

cd MinecraftCMD
Minecraft-Launcher

## Building From Source

Clone the repository:
git clone https://github.com/gianma40/MinecraftCMD.git

Open the project with your Java IDE or compile using the compila.bat file, or do it yourself:
javac *.java
## Project Status

🚧 Work in progress.

MinecraftCMD is actively being improved with new compatibility features and legacy Minecraft support.

## License

This project is released under the GNU General Public License v3.0 (GPL-3.0).

You are free to use, modify and distribute this software, but any derivative work you distribute must also be released under the GPL-3.0 license, and the source code must remain available.

See the [LICENSE](LICENSE) file for the full text.

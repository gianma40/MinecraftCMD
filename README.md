MinecraftCMD

A lightweight command-line Minecraft launcher written in Java.

MinecraftCMD allows you to launch custom Minecraft .jar files directly from the command prompt, making it easier to test, run and experiment with old Minecraft versions and custom builds.

Features
🚀 Launch Minecraft .jar files from Java-based launcher
☕ Java-based and cross-platform compatible (with a compatible Java version)
📦 Supports custom Minecraft jars
🖥️ Designed for retro Minecraft testing and development
🔧 Simple command-line usage
Usage

Run MinecraftCMD from the command prompt:

MinecraftCMD.exe <minecraft-jar>

Example:

MinecraftCMD.exe Minecraft.jar

The launcher will load the specified Minecraft jar and start it using Java.

Requirements
Java Runtime Environment (JRE) or Java Development Kit (JDK)
A compatible Minecraft jar file

For old Minecraft versions, the required Java version may vary.

Examples:

Minecraft Classic / Alpha / Beta → Java 5/6/7/8 depending on version
Modern Minecraft → newer Java versions
Legacy Support

MinecraftCMD is made with legacy Minecraft experimentation in mind.

It can be used for testing:

Classic Minecraft versions
Old client jars
Custom Minecraft builds
Historical Minecraft projects

Future versions may include automatic detection of old entry points such as:

MinecraftApplet
RubyDung
Legacy Minecraft main classes
Installation
Download the latest release.
Extract the files.
Open CMD in the launcher folder.
Run MinecraftCMD with your desired jar.

Example:

cd MinecraftCMD
MinecraftCMD.exe RD-132211.jar
Building From Source

Clone the repository:

git clone https://github.com/gianma40/MinecraftCMD.git

Open the project with your Java IDE or compile using the Java compiler:

javac *.java
Project Status

🚧 Work in progress.

MinecraftCMD is actively being improved with new compatibility features and legacy Minecraft support.

License

This project is released under the MIT License.

You are free to use, modify and distribute this software.

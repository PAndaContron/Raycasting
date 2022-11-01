# Raycasting

Raycasting 3D renderer made from scratch in Java.

Capable of drawing triangles with image textures and handling a single light source.
Everything is hardcoded, so changing the models that get rendered requires modifying the code.
This is running entirely on the CPU in Java so it's very slow;
the default scene has only 14 triangles and still runs at a very low framerate.

## Building and Running

This was originally made with Eclipse, and should be loadable as an Eclipse project.
To build and run manually, run

```bash
mkdir bin
javac -sourcepath src -d bin src/raycasting/Main.java
```

to build, and

```bash
java -cp bin:resources raycasting.Main
```

to run.
These commands are tested on Linux, but the only difference for Windows should be changing the colon to a semicolon in the last command.

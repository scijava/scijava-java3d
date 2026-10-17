[![Build Status](https://github.com/scijava/scijava-java3d/actions/workflows/build.yml/badge.svg)](https://github.com/scijava/scijava-java3d/actions/workflows/build.yml)

# SciJava Java 3D

This project is a wrapper around Java 3D which adds back needed
JogAmp dependencies Gluegen and JOGL.

The reason these dependencies are missing by default when depending on
java3d-core from the pom-scijava BOM is because they normally bring in the
"main" artifacts (gluegen-rt-main and jogl-all-main) which in turn bring in
native classifier artifacts for all prebuilt platforms.

This is undesirable for a number of reasons:

* Bloats the dependency graph with binaries irrelevant to the running platform.

* Breaks JPMS automatic module support by introducing multiple JAR files
  resolving to the same module name, something which JPMS disallows.

To work around this, SciJava-based projects can add a dependency on this
scijava-java3d project to inherit all the needed dependencies transitively:

```xml
<dependency>
  <groupId>org.scijava</groupId>
  <artifactId>scijava-java3d</artifactId>
</dependency>
```

## About java3d-utils and joal

This scijava-java3d component does not bring in java3d-utils, since you may
or may not need the features provided by that library. Furthermore, although
java3d-utils depends on joal-main, which has an analogous issue with the joal
native classifier artifacts, it is not clear (to me) that joal is ever really
used by java3d-utils; excluding it completely by default is maybe preferable.
If your project needs joal, you can add the following dependencies manually:
```xml
<dependency>
  <groupId>org.jogamp.joal</groupId>
  <artifactId>joal</artifactId>
</dependency>
<dependency>
  <groupId>org.jogamp.joal</groupId>
  <artifactId>joal</artifactId>
  <classifier>${scijava.natives.classifier.joal}</classifier>
</dependency>
```

plugins {
    // Apply the common convention plugin for shared build configuration between library and
    // application projects.
    id("buildlogic.java-common-conventions")
    id("org.graalvm.buildtools.native")

    // Apply the application plugin to add support for building a CLI application in Java.
    application
}

graalvmNative {
    binaries {
        named("main") {
            buildArgs.add("-O0")
            buildArgs.add("-R:MinHeapSize=16m")
        }
    }
}

dependencies { runtimeOnly("joda-time:joda-time:2.13.1") }

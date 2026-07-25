plugins { id("buildlogic.java-application-conventions") }

application {
    mainClass = "org.example.baselayer.Main"
}

graalvmNative {
    binaries {
        named("main") {
            // Capture a set of commonly-used JDK modules into a reusable .nil layer file so that
            // gateway, iam and transactions can skip reanalysing and recompiling them.
            val layerFile =
                layout.buildDirectory
                    .file("native/nativeCompile/base-layer.nil")
                    .get()
                    .asFile
            buildArgs.add(
                "--layer-create=${layerFile.absolutePath}" +
                    ",module=java.base" +
                    ",module=java.logging" +
                    ",module=java.sql" +
                    ",module=java.naming" +
                    ",module=java.management" +
                    ",module=java.xml" +
                    ",module=java.net.http",
            )
        }
    }
}

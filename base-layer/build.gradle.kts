plugins { id("buildlogic.java-application-conventions") }

application {
    mainClass = "org.example.baselayer.Main"
}

graalvmNative {
    binaries {
        named("main") {
            // Capture a set of commonly-used JDK modules into a reusable .nil layer file so that
            // gateway, iam and transactions can skip reanalysing and recompiling them.
            // The correct hosted-option name is -H:LayerCreate (not --layer-create), and it
            // requires -H:+UnlockExperimentalVMOptions to be passed first.
            buildArgs.add("-H:+UnlockExperimentalVMOptions")
            buildArgs.add(
                "-H:LayerCreate=base-layer.nil" +
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

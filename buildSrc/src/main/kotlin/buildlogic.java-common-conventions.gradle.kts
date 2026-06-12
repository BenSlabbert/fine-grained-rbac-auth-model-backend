import java.util.Properties

plugins { java }

val localEnv =
    Properties().apply {
        val envFile = rootProject.file(".local-env")
        if (envFile.isFile) {
            envFile.inputStream().use { load(it) }
        }
    }

val gprUser = providers.provider { localEnv.getProperty("gpr.user") }.orElse("BenSlabbert")

val gprKey =
    providers
        .environmentVariable("GH_TOKEN")
        .orElse(providers.provider { localEnv.getProperty("gpr.key") })
        .orElse("")

val uid =
    runCatching {
        providers
            .exec { commandLine("id", "-u") }
            .standardOutput.asText
            .get()
            .trim()
    }.getOrDefault("1000")

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.github.com/BenSlabbert/vertx-dagger-web-codegen")
        credentials {
            username = gprUser.get()
            password = gprKey.get()
        }
    }
}

java { toolchain { languageVersion = JavaLanguageVersion.of(25) } }

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    minHeapSize = "64m"
    maxHeapSize = "128m"
    jvmArgs("--enable-native-access=ALL-UNNAMED")
    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 4).coerceIn(1, 4)
    environment("DOCKER_HOST", "unix:///run/user/$uid/podman/podman.sock")
    environment("TESTCONTAINERS_RYUK_DISABLED", "true")
    environment("TESTCONTAINERS_HOST_OVERRIDE", "127.0.0.1")
}

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

tasks.named<Test>("test") { useJUnitPlatform() }

tasks.withType<Test>().configureEach {
    minHeapSize = "64m"
    maxHeapSize = "128m"
    jvmArgs("--enable-native-access=ALL-UNNAMED")
    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)
    environment("DOCKER_HOST", "unix:///run/user/$uid/podman/podman.sock")
    environment("TESTCONTAINERS_RYUK_DISABLED", "true")
}

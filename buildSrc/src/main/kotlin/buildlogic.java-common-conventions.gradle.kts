import java.util.Properties

plugins { java }

val localEnv =
    Properties().apply {
        val envFile = rootProject.file(".local-env")
        if (envFile.isFile) {
            envFile.inputStream().use { load(it) }
        }
    }

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.github.com/BenSlabbert/vertx-dagger-web-codegen")
        credentials {
            username = localEnv.getProperty("gpr.user")
            password = localEnv.getProperty("gpr.key")
        }
    }
}

java { toolchain { languageVersion = JavaLanguageVersion.of(25) } }

tasks.named<Test>("test") { useJUnitPlatform() }

tasks.withType<Test>().configureEach {
    environment(
        "DOCKER_HOST",
        "unix:///run/user/${System.getenv("UID") ?: "1000"}/podman/podman.sock",
    )
    environment("TESTCONTAINERS_RYUK_DISABLED", "true")
}

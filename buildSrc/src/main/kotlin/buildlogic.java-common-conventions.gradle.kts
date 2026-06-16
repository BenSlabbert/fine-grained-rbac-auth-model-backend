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

// todo: this is a fix coming from
// https://javadoc.io/doc/org.mockito/mockito-core/latest/org.mockito/org/mockito/Mockito.html#0.3
//  to fix the self attaching agent issue
//  it would be great i fwe didn't have to hard code the mockito version and can inherit it
val mockitoAgent = configurations.create("mockitoAgent")

dependencies {
    testImplementation("org.mockito:mockito-core:5.23.0")
    mockitoAgent("org.mockito:mockito-core:5.23.0") { isTransitive = false }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    minHeapSize = "64m"
    maxHeapSize = "128m"
    jvmArgs("--enable-native-access=ALL-UNNAMED", "-javaagent:${mockitoAgent.asPath}")
    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 4).coerceIn(1, 4)
    environment("DOCKER_HOST", "unix:///run/user/$uid/podman/podman.sock")
    environment("TESTCONTAINERS_RYUK_DISABLED", "true")
    environment("TESTCONTAINERS_HOST_OVERRIDE", "127.0.0.1")
}

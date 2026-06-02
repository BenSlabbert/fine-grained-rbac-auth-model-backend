import java.util.Properties

plugins {
    java
}

val localEnv = Properties().apply {
    val envFile = rootProject.file(".local_env")
    if (envFile.isFile) {
        envFile.inputStream().use { load(it) }
    }
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://github.com/BenSlabbert/vertx-dagger-web-codegen")
        credentials {
            username = localEnv.getProperty("gpr.user")
            password = localEnv.getProperty("gpr.key")
        }
    }
}

dependencies {
    constraints {
        // Define dependency versions as constraints
        implementation("org.apache.commons:commons-text:1.14.0")
    }

    // Use JUnit Jupiter for testing.
    testImplementation("org.junit.jupiter:junit-jupiter:6.0.1")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}

plugins {
    // Support convention plugins written in Kotlin. Convention plugins are build scripts in
    // 'src/main' that automatically become available as plugins in the main build.
    `kotlin-dsl`
    id("com.diffplug.spotless") version "8.8.0"
}

repositories {
    // Use the plugin portal to apply community plugins in convention plugins.
    gradlePluginPortal()
}

dependencies {
    implementation("org.graalvm.buildtools.native:org.graalvm.buildtools.native.gradle.plugin:0.11.1")
}

spotless {
    kotlinGradle {
        target("*.gradle.kts", "src/**/*.gradle.kts")
        targetExclude("**/build/**")
        ktfmt().googleStyle()
        ktlint()
    }
}

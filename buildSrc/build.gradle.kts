plugins {
    // Support convention plugins written in Kotlin. Convention plugins are build scripts in
    // 'src/main' that automatically become available as plugins in the main build.
    `kotlin-dsl`
    id("com.diffplug.spotless") version "8.6.0"
}

repositories {
    // Use the plugin portal to apply community plugins in convention plugins.
    gradlePluginPortal()
}

spotless {
    kotlinGradle {
        target("*.gradle.kts", "src/**/*.gradle.kts")
        targetExclude("**/build/**")
        ktfmt().googleStyle()
        ktlint()
    }
}

plugins {
    // Apply the foojay-resolver plugin to allow automatic download of JDKs
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("com.gradle.develocity") version ("4.4.3")
}

rootProject.name = "backend"

include("iam", "utilities", "security-api", "transactions", "gateway")

develocity { buildScan { publishing { onlyIf { false } } } }

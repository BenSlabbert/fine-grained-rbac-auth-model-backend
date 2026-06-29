plugins { id("com.diffplug.spotless") version "8.7.0" }

repositories { mavenCentral() }

spotless {
    java {
        target("**/src/main/**/*.java", "**/src/test/**/*.java")
        targetExclude("**/build/**")
        googleJavaFormat().reflowLongStrings()
        formatAnnotations()
        licenseHeader($$"/* Licensed under Apache-2.0 $YEAR. */")
    }
    sql {
        target("**/src/**/*.sql")
        targetExclude("**/build/**")
        dbeaver().configFile(file("dbeaver.properties"))
    }
    yaml {
        target("**/*.yaml", "**/*.yml")
        targetExclude("**/helm/templates/**", "**/node_modules/**", "**/build/**")
        jackson()
    }
    flexmark {
        target("**/*.md")
        targetExclude("**/node_modules/**", "**/build/**")
    }
    kotlinGradle {
        target("**/*.gradle.kts")
        targetExclude("**/build/**")
        ktfmt().googleStyle()
        ktlint()
    }
}

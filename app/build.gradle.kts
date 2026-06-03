plugins { id("buildlogic.java-application-conventions") }

dependencies {
    implementation(project(":utilities"))
    testImplementation(testFixtures(libs.vdw.commons.test))
}

application {
    // Define the main class for the application.
    mainClass = "org.example.app.App"
}

plugins { id("buildlogic.java-library-conventions") }

dependencies {
    api(project(":list"))
    implementation(libs.vdw.logging)
    implementation(libs.flyway)
}

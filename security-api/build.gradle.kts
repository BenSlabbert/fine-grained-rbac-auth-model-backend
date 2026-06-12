plugins { id("buildlogic.java-library-conventions") }

dependencies {
    compileOnly(libs.google.auto.annotations)

    implementation(libs.vdw.logging)
    implementation(libs.vdw.annotation)
    implementation(libs.vdw.commons)

    annotationProcessor(libs.google.auto.processor)
    annotationProcessor(libs.vdw.generator)
}

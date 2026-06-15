plugins { id("buildlogic.java-application-conventions") }

dependencies {
    implementation(libs.org.mapstruct.mapstruct)
    implementation(libs.vdw.launcher)
    implementation(libs.vdw.commons)
    implementation(libs.vdw.config)
    implementation(libs.vdw.logging)
    implementation(libs.vdw.annotation)
    implementation(libs.vdw.platform)
    implementation(project(":security-api"))
    implementation("io.vertx:vertx-auth-jwt:5.1.2")
    compileOnly(libs.google.auto.annotations)

    testImplementation(testFixtures(libs.vdw.commons.test))

    annotationProcessor(libs.hibernate.validator.annotation.processor)
    annotationProcessor(libs.org.mapstruct.mapstruct.processor)
    annotationProcessor(libs.vdw.generator)
    annotationProcessor(libs.google.auto.processor)
    annotationProcessor("com.google.dagger:dagger-compiler")
}

application {
    // Define the main class for the application.
    mainClass = "org.example.gateway.Main"
}

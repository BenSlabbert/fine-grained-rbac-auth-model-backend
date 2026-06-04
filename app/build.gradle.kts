plugins {
    id("buildlogic.java-application-conventions")
    id("net.bytebuddy.byte-buddy-gradle-plugin") version "1.18.8"
}

dependencies {
    implementation(libs.org.mapstruct.mapstruct)
    implementation(libs.vdw.launcher)
    implementation(libs.vdw.commons)
    implementation(libs.vdw.config)
    implementation(libs.vdw.logging)
    implementation(libs.vdw.annotation)
    implementation(libs.vdw.platform)
    implementation(project(":utilities"))
    compileOnly(libs.google.auto.annotations)

    runtimeOnly(libs.postgresql)

    testImplementation(testFixtures(libs.vdw.commons.test))

    annotationProcessor(libs.hibernate.validator.annotation.processor)
    annotationProcessor(libs.org.mapstruct.mapstruct.processor)
    annotationProcessor(libs.vdw.generator)
    annotationProcessor(libs.google.auto.processor)
    "byteBuddy"(libs.vdw.advice.transformer)
    annotationProcessor("com.google.dagger:dagger-compiler")
}

application {
    // Define the main class for the application.
    mainClass = "org.example.app.App"
}

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.20"
    kotlin("plugin.spring") version "1.9.20"
    kotlin("kapt") version "1.9.20"
    id("com.netflix.dgs.codegen") version "5.1.17"
    idea
}

group = "org.example"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation(platform("com.netflix.graphql.dgs:graphql-dgs-platform-dependencies:8.2.0"))
    implementation("com.netflix.graphql.dgs:graphql-dgs-spring-boot-starter")
//    implementation("com.netflix.graphql.dgs:graphql-dgs-extended-scalars")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

//dependencyManagement {
//    imports {
//        // We need to define the DGS BOM as follows such that the
//        // io.spring.dependency-management plugin respects the versions expressed in the DGS BOM, e.g. graphql-java
//        mavenBom("com.netflix.graphql.dgs:graphql-dgs-platform-dependencies:5.5.1")
//    }
//}

tasks.generateJava {
    typeMapping = mutableMapOf(
        "UUID" to "java.util.UUID",
        "BigDecimal" to "java.math.BigDecimal",
        "DateTime" to "java.time.OffsetDateTime"
    )
    generateClient = true
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}
//
//application {
//    mainClass.set("UrlShortenerApplicationKt")
//}
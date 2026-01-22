plugins {
    java
    id("org.springframework.boot") version "4.0.1"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.hibernate.orm") version "7.2.0.Final"
    id("org.graalvm.buildtools.native") version "0.11.3"
    id("com.diffplug.spotless") version "8.1.0"
    checkstyle
}

group = "com.github.mohrezal"
version = "0.0.1-SNAPSHOT"
description = "spring-boot-blog-rest-api"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-liquibase")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-security-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")
    implementation("org.mapstruct:mapstruct:1.6.3")
    implementation("software.amazon.awssdk:s3:2.25.60")
    implementation("org.apache.tika:tika-core:2.9.2")
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-actuator-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    testImplementation("org.springframework.boot:spring-boot-starter-liquibase-test")
    testImplementation("org.springframework.boot:spring-boot-starter-security-oauth2-resource-server-test")
    testImplementation("org.springframework.boot:spring-boot-starter-security-test")
    testImplementation("org.springframework.boot:spring-boot-starter-thymeleaf-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("com.h2database:h2")
    testImplementation(platform("org.testcontainers:testcontainers-bom:1.20.4"))
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")

    testImplementation("org.awaitility:awaitility:4.2.2")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("net.datafaker:datafaker:2.4.2")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

spotless {
    java {
        googleJavaFormat("1.33.0").aosp().reflowLongStrings()
        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()
        target("src/*/java/**/*.java")
        targetExclude("build/**")
    }

    kotlinGradle {
        target("*.gradle.kts")
        ktlint()
    }

    format("misc") {
        target("*.md", ".gitignore", "*.yml", "*.yaml", "*.xml")
        targetExclude("checkstyle.xml")
        trimTrailingWhitespace()
        endWithNewline()
    }
}

checkstyle {
    toolVersion = "12.3.1"
    configFile = file("$projectDir/checkstyle.xml")
    isIgnoreFailures = false
}

tasks.register("format") {
    dependsOn("spotlessApply")
}

tasks.register("lint") {
    dependsOn("spotlessCheck", "checkstyleMain", "checkstyleTest")
}

tasks.register("prepare") {
    val source = "hooks/pre-commit"
    val target = ".git/hooks/pre-commit"
    doLast {
        file(source).copyTo(file(target), true)
        file(target).setExecutable(true)
    }
}

tasks.named("check") {
    dependsOn("lint")
}

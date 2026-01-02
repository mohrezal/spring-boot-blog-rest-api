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
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-actuator-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    testImplementation("org.springframework.boot:spring-boot-starter-liquibase-test")
    testImplementation("org.springframework.boot:spring-boot-starter-security-oauth2-resource-server-test")
    testImplementation("org.springframework.boot:spring-boot-starter-security-test")
    testImplementation("org.springframework.boot:spring-boot-starter-thymeleaf-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
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

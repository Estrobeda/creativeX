plugins {
    `java-library`
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.3")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.11.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    withJavadocJar()
    withSourcesJar()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

tasks {
    javadoc {
        isFailOnError = false
        (options as StandardJavadocDocletOptions).run {
            tags("sinceMinecraft:a:Since Minecraft:")
            tags("sincePackFormat:a:Since Resource-Pack Format:")
        }
    }
    test {
        useJUnitPlatform()
    }
}

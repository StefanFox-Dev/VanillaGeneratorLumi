plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.6"
    id("io.freefair.lombok") version "8.6"
}

group = "aeza.vanilla"
version = "1.0.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(files("../../Lumi-1.6.4.jar"))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(listOf("-Xlint:none"))
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}

tasks.jar {
    enabled = false
    dependsOn(tasks.shadowJar)
}

tasks.shadowJar {
    archiveClassifier.set("")
    doLast {
        copy {
            from(archiveFile)
            into(file("../../"))
            rename { "VanillaGeneratorLumi.jar" }
        }
        copy {
            from(archiveFile)
            into(file("../"))
            rename { "VanillaGeneratorLumi.jar" }
        }
    }
}

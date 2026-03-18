plugins {
    `java-gradle-plugin`
    kotlin("jvm") version "2.1.0"
}

repositories {
    mavenCentral()
}

gradlePlugin {
    plugins {
        create("conanInstall") {
            id = "conan-install"
            implementationClass = "ConanPlugin"
        }
    }
}

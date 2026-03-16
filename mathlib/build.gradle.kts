plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

val abiToConanProfile = mapOf(
    "x86_64" to "android-x86_64",
    "arm64-v8a" to "android-armv8"
)

val conanInstall by tasks.registering {
    val conanDir = rootProject.file("conan")
    val conanfile = rootProject.file("conanfile.py")
    val outputBase = layout.buildDirectory.dir("conan")

    inputs.file(conanfile)
    inputs.dir(conanDir)
    outputs.dir(outputBase)

    doLast {
        abiToConanProfile.forEach { (abi, profile) ->
            exec {
                commandLine(
                    "conan", "install",
                    conanfile.absolutePath,
                    "--profile:host=${conanDir.resolve("profiles/$profile")}",
                    "--output-folder=${outputBase.get().dir(abi).asFile}",
                    "--build=missing"
                )
            }
        }
    }
}

tasks.matching {
    it.name.contains("CMake", ignoreCase = true) && it.name.contains("configure", ignoreCase = true)
}.configureEach {
    dependsOn(conanInstall)
}

android {
    namespace = "com.aiplayground.mathlib"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            abiFilters += abiToConanProfile.keys
        }

        externalNativeBuild {
            cmake {
                cppFlags("-std=c++17")
                arguments(
                    "-DCONAN_OUTPUT_BASE=${layout.buildDirectory.get().dir("conan").asFile.absolutePath}"
                )
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test:runner:1.6.2")
}

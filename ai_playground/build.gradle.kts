plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("conan-install")
}

android {
    namespace = "com.aiplayground.mathlib"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            abiFilters += conan.abiToProfile.get().keys
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

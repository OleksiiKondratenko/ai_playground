plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

val abiToConanProfile = mapOf(
    "x86_64" to "android-x86_64",
    "arm64-v8a" to "android-armv8"
)

fun runShell(cmd: String, envVars: Map<String, String>) {
    val isWindows = System.getProperty("os.name").lowercase().contains("windows")
    val shellCmd = if (isWindows) {
        listOf("cmd", "/c", cmd)
    } else {
        listOf("sh", "-c", cmd)
    }

    val builder = ProcessBuilder(shellCmd)
        .apply {
            environment().putAll(envVars)
        }
    logger.info("env: ${builder.environment()}")
    logger.info(">> $shellCmd")

    val proc = builder.start()

    val result = proc.inputStream.bufferedReader().readText()
    val errors = proc.errorStream.bufferedReader().readText()

    proc.waitFor()

    if (proc.exitValue() != 0) {
        throw Exception("Execution failed! Output: $result Error: $errors")
    } else {
        logger.info(result)
        logger.info(errors)
    }
}

val conanInstall by tasks.registering {
    val conanDir = rootProject.file("conan")
    val conanFileDir = rootProject.file("ai_playground/src/main/cpp")
    val outputBase = layout.buildDirectory.dir("conan")

    doLast {
        abiToConanProfile.forEach { (abi, profile) ->
            val cmd = "conan install $conanFileDir " +
                        "--profile:host=${conanDir.resolve("profiles/$profile")} " +
                        "--output-folder=${outputBase.get().dir(abi).asFile} " +
                        "--build missing"

            val env = mapOf(
                "ANDROID_NDK_HOME" to android.ndkDirectory.absolutePath,
                "ANDROID_API_V" to android.defaultConfig.minSdk.toString())
            runShell(cmd, env)
        }
    }
}

afterEvaluate {
    tasks.matching {
        it.name.contains("CMake", ignoreCase = true) && it.name.contains("configure", ignoreCase = true)
    }.configureEach {
        dependsOn(conanInstall)
    }
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

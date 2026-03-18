import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Gradle plugin that provides a `conanInstall` task for Android library modules.
 *
 * Usage in an Android library module's `build.gradle.kts`:
 * ```
 * plugins {
 *     id("conan-install")
 * }
 * ```
 */
class ConanPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val conan = project.extensions.create("conan", ConanExtension::class.java, project)

        val conanInstall = project.tasks.register("conanInstall") { task ->
            task.description = "Runs conan install for each configured ABI/profile pair."
            task.group = "conan"

            val outputBase = project.layout.buildDirectory.dir("conan")

            task.inputs.dir(conan.profilesDir)
            task.inputs.dir(conan.conanfileDir)
            task.inputs.property("abiToProfile", conan.abiToProfile)
            task.outputs.dir(outputBase)

            task.doLast {
                val android = project.extensions.getByName("android")
                val ndkDirectory = android.javaClass.getMethod("getNdkDirectory").invoke(android) as java.io.File
                val defaultConfig = android.javaClass.getMethod("getDefaultConfig").invoke(android)
                val minSdk = defaultConfig.javaClass.getMethod("getMinSdk").invoke(defaultConfig)

                conan.abiToProfile.get().forEach { (abi, profile) ->
                    val cmd = "conan install ${conan.conanfileDir.get().asFile} " +
                        "--profile:host=${conan.profilesDir.get().file(profile).asFile} " +
                        "--output-folder=${outputBase.get().dir(abi).asFile} " +
                        "--build missing"

                    val env = mapOf(
                        "ANDROID_NDK_HOME" to ndkDirectory.absolutePath,
                        "ANDROID_API_V" to minSdk.toString()
                    )

                    runShell(cmd, env, project)
                }
            }
        }

        project.afterEvaluate {
            project.tasks.matching {
                it.name.contains("CMake", ignoreCase = true) &&
                    it.name.contains("configure", ignoreCase = true)
            }.configureEach {
                it.dependsOn(conanInstall)
            }
        }
    }

    private fun runShell(cmd: String, envVars: Map<String, String>, project: Project) {
        val isWindows = System.getProperty("os.name").lowercase().contains("windows")
        val shellCmd = if (isWindows) {
            listOf("cmd", "/c", cmd)
        } else {
            listOf("sh", "-c", cmd)
        }

        val builder = ProcessBuilder(shellCmd).apply {
            environment().putAll(envVars)
        }
        project.logger.info("env: ${builder.environment()}")
        project.logger.info(">> $shellCmd")

        val proc = builder.start()

        val result = proc.inputStream.bufferedReader().readText()
        val errors = proc.errorStream.bufferedReader().readText()

        proc.waitFor()

        if (proc.exitValue() != 0) {
            throw GradleException("Execution failed! Output: $result Error: $errors")
        } else {
            project.logger.info(result)
            project.logger.info(errors)
        }
    }
}

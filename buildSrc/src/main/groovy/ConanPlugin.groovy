import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.GradleException
import org.gradle.api.provider.MapProperty
import org.gradle.api.file.DirectoryProperty

/**
 * Gradle plugin that provides a {@code conanInstall} task for Android library modules.
 *
 * <p>Usage in an Android library module's {@code build.gradle.kts}:
 * <pre>
 * plugins {
 *     id("conan-install")
 * }
 *
 * conan {
 *     abiToProfile.set(mapOf("x86_64" to "android-x86_64", "arm64-v8a" to "android-armv8"))
 *     profilesDir.set(rootProject.layout.projectDirectory.dir("conan/profiles"))
 *     conanfileDir.set(layout.projectDirectory.dir("src/main/cpp"))
 * }
 * </pre>
 */
class ConanPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def conan = project.extensions.create('conan', ConanExtension, project)

        def conanInstall = project.tasks.register('conanInstall') { task ->
            task.description = 'Runs conan install for each configured ABI/profile pair.'
            task.group = 'conan'

            def outputBase = project.layout.buildDirectory.dir('conan')

            task.inputs.dir(conan.profilesDir)
            task.inputs.dir(conan.conanfileDir)
            task.inputs.property('abiToProfile', conan.abiToProfile)
            task.outputs.dir(outputBase)

            task.doLast {
                def android = project.extensions.getByName('android')

                conan.abiToProfile.get().each { abi, profile ->
                    def cmd = "conan install ${conan.conanfileDir.get().asFile} " +
                        "--profile:host=${conan.profilesDir.get().file(profile).asFile} " +
                        "--output-folder=${outputBase.get().dir(abi).asFile} " +
                        "--build missing"

                    def env = [
                        'ANDROID_NDK_HOME': android.ndkDirectory.absolutePath,
                        'ANDROID_API_V'   : android.defaultConfig.minSdk.toString()
                    ]

                    runShell(cmd, env, project)
                }
            }
        }

        project.afterEvaluate {
            project.tasks.matching {
                it.name.contains('CMake') && it.name.toLowerCase().contains('configure')
            }.configureEach {
                it.dependsOn(conanInstall)
            }
        }
    }

    private static void runShell(String cmd, Map<String, String> envVars, Project project) {
        def isWindows = System.getProperty('os.name').toLowerCase().contains('windows')
        def shellCmd = isWindows ? ['cmd', '/c', cmd] : ['sh', '-c', cmd]

        def builder = new ProcessBuilder(shellCmd)
        builder.environment().putAll(envVars)
        project.logger.info("env: ${builder.environment()}")
        project.logger.info(">> $shellCmd")

        def proc = builder.start()

        def result = proc.inputStream.text
        def errors = proc.errorStream.text

        proc.waitFor()

        if (proc.exitValue() != 0) {
            throw new GradleException("Execution failed! Output: $result Error: $errors")
        } else {
            project.logger.info(result)
            project.logger.info(errors)
        }
    }
}

class ConanExtension {
    final MapProperty<String, String> abiToProfile
    final DirectoryProperty profilesDir
    final DirectoryProperty conanfileDir

    ConanExtension(Project project) {
        abiToProfile = project.objects.mapProperty(String, String)
        profilesDir = project.objects.directoryProperty()
        conanfileDir = project.objects.directoryProperty()

        abiToProfile.convention([
            'x86_64'   : 'android-x86_64',
            'arm64-v8a': 'android-armv8'
        ])
        profilesDir.convention(project.rootProject.layout.projectDirectory.dir('conan/profiles'))
        conanfileDir.convention(project.layout.projectDirectory.dir('src/main/cpp'))
    }
}

import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.MapProperty

/**
 * Extension for configuring the conan-install plugin.
 *
 * Example usage in `build.gradle.kts`:
 * ```
 * conan {
 *     abiToProfile.set(mapOf("x86_64" to "android-x86_64", "arm64-v8a" to "android-armv8"))
 *     profilesDir.set(rootProject.layout.projectDirectory.dir("conan/profiles"))
 *     conanfileDir.set(layout.projectDirectory.dir("src/main/cpp"))
 * }
 * ```
 */
abstract class ConanExtension(project: Project) {
    /** Map of Android ABI to Conan profile name. */
    abstract val abiToProfile: MapProperty<String, String>

    /** Directory containing the Conan profile files. */
    abstract val profilesDir: DirectoryProperty

    /** Directory containing conanfile.txt. */
    abstract val conanfileDir: DirectoryProperty

    init {
        abiToProfile.convention(
            mapOf(
                "x86_64" to "android-x86_64",
                "arm64-v8a" to "android-armv8"
            )
        )
        profilesDir.convention(project.rootProject.layout.projectDirectory.dir("conan/profiles"))
        conanfileDir.convention(project.layout.projectDirectory.dir("src/main/cpp"))
    }
}

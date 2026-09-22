// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.7.3" apply false
    id("org.jetbrains.kotlin.android") version "1.9.24" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.24" apply false
    id("com.google.devtools.ksp") version "1.9.24-1.0.20" apply false
}

// Apply signing configuration from parent project
allprojects {
    gradle.projectsEvaluated {
        tasks.withType<com.android.build.gradle.tasks.PackageApplication> {
            doFirst {
                val storePath = project.providers.gradleProperty("BA_KEYSTORE_PATH")
                if (storePath.isPresent) {
                    val signingConfig = android.buildTypes.named("release").get().signingConfig
                    if (signingConfig != null) {
                        // Apply signing configuration
                        android.buildTypes.named("release").configure {
                            signingConfig = signingConfig
                        }
                    }
                }
            }
        }
    }
}
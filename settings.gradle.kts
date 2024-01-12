pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven(url = "https://www.jitpack.io")
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://www.jitpack.io")
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")

    }
}

rootProject.name = "Manga"
include(":app")
include(":source-api")
include(":core")
include(":extension")
include(":data")
include(":source-local")
include(":common-res")

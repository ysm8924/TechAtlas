pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "TechAtlas"
include(":app")
include(":core:common")
include(":core:network")
include(":core:ui")
include(":labs:java_core")
include(":labs:kotlin_core")
include(":demo_view")

// Architecture Demos
include(":architecture_demos:demo_arch_ca")
include(":architecture_demos:demo_arch_mvi")
include(":architecture_demos:demo_arch_mvvm")

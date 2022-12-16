buildscript {
    repositories {
        maven("https://repo.maven.apache.org/maven2/")
        google() // Direct URL is not supported by F-Droid
    }

    dependencies {
        // https://mvnrepository.com/artifact/com.android.tools.build/gradle?repo=google
        classpath("com.android.tools.build:gradle:7.3.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.20")
        // https://developer.android.com/jetpack/androidx/releases/navigation
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.5.3")
    }
}

allprojects {
    repositories {
        maven("https://repo.maven.apache.org/maven2/")
        google() // Direct URL is not supported by F-Droid
        maven("https://jitpack.io")
    }
}

task("clean") {
    delete(rootProject.buildDir)
}

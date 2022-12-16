import java.net.URL

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.7.20"
    id("androidx.navigation.safeargs.kotlin")
    // https://github.com/google/ksp/releases
    id("com.google.devtools.ksp") version "1.7.20-1.0.8"
}

android {
    namespace = "org.btcmap"
    compileSdk = 33

    defaultConfig {
        applicationId = "org.btcmap"
        minSdk = 27
        targetSdk = 33
        versionCode = 39
        versionName = "0.5.10"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs += "-opt-in=kotlinx.coroutines.DelicateCoroutinesApi"
    }

    signingConfigs {
        create("selfSigned") {
            storeFile = File(rootDir, "release.jks")
            storePassword = "btcmap"
            keyAlias = "btcmap"
            keyPassword = "btcmap"
        }
    }

    packagingOptions {
        resources.excludes += "DebugProbesKt.bin"

        // TODO remove bundled SQLite when Android bumps its deps
        // > The JSON functions and operators are built into SQLite by default, as of SQLite version 3.38.0 (2022-02-22).
        // https://www.sqlite.org/json1.html
        //jniLibs.excludes += "/lib/armeabi-v7a/libsqlite3x.so"
        //jniLibs.excludes += "lib/arm64-v8a/libsqlite3x.so"
        jniLibs.excludes += "/lib/x86/**"
        //jniLibs.excludes += "/lib/x86_64/**"
    }

    flavorDimensions += "store"
    flavorDimensions += "signature"

    productFlavors {
        create("fdroid") {
            dimension = "store"
        }

        create("play") {
            dimension = "store"
            applicationIdSuffix = ".app"
        }

        create("selfSigned") {
            dimension = "signature"
            signingConfig = signingConfigs.getByName("selfSigned")
        }
    }

    buildTypes {
        release {
            // Enables code shrinking, obfuscation, and optimization
            isMinifyEnabled = true

            // Enables resource shrinking
            isShrinkResources = true

            // Includes the default ProGuard rules file
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        viewBinding = true
    }

    sourceSets.all {
        kotlin.srcDir("build/generated/ksp/$name/kotlin")
    }
}

tasks.register("bundleData") {
    doLast {
        val src = URL("https://api.btcmap.org/v2/elements")
        val destDir = File(projectDir, "src/main/assets")
        destDir.mkdirs()
        val destFile = File(destDir, "elements.json")
        destFile.writeText(src.readText())
    }
}

dependencies {
    // Platform-agnostic JSON serialization
    // https://github.com/Kotlin/kotlinx.serialization/releases
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")

    // Simplifies in-app navigation
    // https://developer.android.com/jetpack/androidx/releases/navigation
    val navVer = "2.5.3"
    implementation("androidx.navigation:navigation-fragment-ktx:$navVer")
    implementation("androidx.navigation:navigation-ui-ktx:$navVer")

    // Helps with keeping our view hierarchies flat
    // https://developer.android.com/jetpack/androidx/releases/constraintlayout
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Used by osmdroid (original prefs API is deprecated)
    // https://developer.android.com/jetpack/androidx/releases/preference
    implementation("androidx.preference:preference-ktx:1.2.0")

    // Material design components
    // https://github.com/material-components/material-components-android/releases
    implementation("com.google.android.material:material:1.8.0-alpha02")

    // Helps to split the app into multiple independent screens
    // https://developer.android.com/jetpack/androidx/releases/fragment
    debugImplementation("androidx.fragment:fragment-testing:1.5.4")

    // Modern HTTP client
    // https://github.com/square/okhttp/blob/master/CHANGELOG.md
    implementation("com.squareup.okhttp3:okhttp-brotli:4.10.0")

    // Injection library
    // https://github.com/InsertKoinIO/koin/blob/main/CHANGELOG.md
    implementation("io.insert-koin:koin-android:3.2.3")
    val koinAnnotationsVer = "1.0.3"
    implementation("io.insert-koin:koin-annotations:$koinAnnotationsVer")
    ksp("io.insert-koin:koin-ksp-compiler:$koinAnnotationsVer")

    // Open Street Map widget
    // https://github.com/osmdroid/osmdroid/releases
    implementation("org.osmdroid:osmdroid-android:6.1.14")

    // Charts
    // https://github.com/PhilJay/MPAndroidChart/releases
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // Used to cache data and store user preferences
    implementation("androidx.sqlite:sqlite-ktx:2.2.0")

    // Bundle SQLite binaries
    // https://github.com/requery/sqlite-android/releases
    // TODO remove bundled SQLite when Android bumps its deps
    // > The JSON functions and operators are built into SQLite by default, as of SQLite version 3.38.0 (2022-02-22).
    // https://www.sqlite.org/json1.html
    implementation("com.github.requery:sqlite-android:3.39.2")

    // Used to download, cache and display images
    // https://github.com/coil-kt/coil/releases
    val coilVer = "2.2.2"
    implementation("io.coil-kt:coil:$coilVer")
    implementation("io.coil-kt:coil-svg:$coilVer")

    // Common test dependencies
    // https://junit.org/junit4/
    testImplementation("junit:junit:4.13.2")

    // Common instrumented test dependencies
    // https://junit.org/junit4/
    // https://developer.android.com/jetpack/androidx/releases/test
    androidTestImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test:core-ktx:1.5.0")
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.4")
    // TODO figure out why newer versions hang
    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}
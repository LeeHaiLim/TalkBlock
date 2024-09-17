import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.dev.block"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.dev.block"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField("String", "EMAIL_ID", getPropertyKey("EMAIL_ID"))
        buildConfigField("String", "EMAIL_PW", getPropertyKey("EMAIL_PW"))
        buildConfigField("String", "ADMOB_UNIT_ID", getPropertyKey("ADMOB_UNIT_ID"))
        manifestPlaceholders["admob_app_id"] = getPropertyKey("ADMOB_APP_ID")
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
}

fun getPropertyKey(propertyKey: String): String {
    return gradleLocalProperties(rootDir, providers).getProperty(propertyKey)
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    implementation(libs.androidx.material3)

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose.android)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.hilt.navigation.compose)

    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    implementation(libs.androidx.datastore.core.android)

    implementation(libs.play.services.ads)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)

    implementation(files("libs/activation.jar"))
    implementation(files("libs/additionnal.jar"))
    implementation(files("libs/mail.jar"))

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    androidTestImplementation(platform(libs.androidx.compose.bom))
}
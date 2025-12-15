import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.androidLibrary)
}

kotlin {
    jvmToolchain(21)
    explicitApi()

    jvm()
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    //iosX64()
    //iosArm64()
    //iosSimulatorArm64()
    //linuxX64()

    sourceSets {
        androidMain.dependencies {
        }
        commonMain {
            dependencies {
                implementation(compose.foundation)
                implementation(compose.runtime)
                api(libs.ktor.client.core)
                api(libs.ktor.client.auth)
                implementation(libs.xmlutil.core)
                implementation(libs.klock)
            }
        }
        /*val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.kotest.framework.engine)
                implementation(libs.kotest.framework.datatest)
                implementation(libs.kotest.assertions.core)
                implementation(libs.ktor.client.mock)
                implementation(libs.ktor.client.auth)
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(libs.kotest.runner.junit5)
                implementation(libs.kotlinx.coroutines.debug)
                implementation(libs.logback.classic)
            }
        }*/
    }
}

android {
    namespace = "io.github.triangleofice"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

plugins {
    id(ThunderbirdPlugins.Library.androidCompose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "net.thunderbird.feature.ai.internal"
}

dependencies {
    implementation(projects.core.outcome)
    implementation(projects.core.ui.contract)
    implementation(projects.core.ui.compose.common)
    implementation(projects.core.preference.api)
    implementation(projects.core.logging.api)
    implementation(projects.feature.ai.api)

    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
    implementation(libs.koin.compose.viewmodel)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.jetbrains.compose.material3)
    implementation(libs.jetbrains.compose.foundation)
    implementation(libs.jetbrains.compose.components.ui.preview)
    implementation(libs.jetbrains.compose.material.icons.extended)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.json)

    testImplementation(projects.core.testing)
    testImplementation(libs.assertk)
    testImplementation(libs.kotlinx.coroutines.test)
}

codeCoverage {
    branchCoverage = 0
    lineCoverage = 0
}

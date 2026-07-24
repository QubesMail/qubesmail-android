plugins {
    id(ThunderbirdPlugins.Library.kmp)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    android {
        namespace = "net.thunderbird.feature.ai.api"
    }
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.outcome)
            implementation(projects.core.ui.contract)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
        }
    }
}

codeCoverage {
    branchCoverage = 0
    lineCoverage = 0
}

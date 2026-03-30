// Keep AGP transitives on patched dependency versions until upstream bundles them.
buildscript {
    val nettyVersion = "4.1.132.Final"
    val protobufVersion = "3.25.5"

    configurations.configureEach {
        resolutionStrategy.eachDependency {
            when {
                requested.group == "io.netty" -> {
                    useVersion(nettyVersion)
                    because("AGP 8.11.2 resolves Netty artifacts with multiple open GHSA advisories")
                }

                requested.group == "com.google.protobuf" -> {
                    useVersion(protobufVersion)
                    because("Keep protobuf on the advisory-patched 3.25.x line used by the Android build toolchain")
                }

                requested.group == "org.apache.commons" && requested.name == "commons-compress" -> {
                    useVersion("1.26.0")
                    because("AGP transitives currently pull commons-compress 1.21, which is affected by open advisories")
                }

                requested.group == "org.bitbucket.b_c" && requested.name == "jose4j" -> {
                    useVersion("0.9.6")
                    because("bundletool pulls jose4j 0.9.5, which is vulnerable to compressed JWE DoS")
                }

                requested.group == "org.jdom" && requested.name == "jdom2" -> {
                    useVersion("2.0.6.1")
                    because("jetifier-processor pulls jdom2 2.0.6, which is affected by an XXE advisory")
                }
            }
        }
    }
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
}

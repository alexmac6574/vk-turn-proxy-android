// Keep AGP transitives on patched dependency versions until upstream bundles them.
buildscript {
    val nettyVersion = "4.1.132.Final"
    val protobufVersion = "3.25.5"
    val commonsCompressVersion = "1.26.0"
    val commonsLangVersion = "3.18.0"
    val jose4jVersion = "0.9.6"
    val jdomVersion = "2.0.6.1"

    dependencies {
        classpath("io.netty:netty-codec-http2:$nettyVersion") {
            because("Expose the patched HTTP/2 Netty artifact directly on the buildscript classpath for Dependabot")
        }
        classpath("io.netty:netty-handler:$nettyVersion") {
            because("Expose the patched Netty handler artifact directly on the buildscript classpath for Dependabot")
        }
        classpath("io.netty:netty-codec-http:$nettyVersion") {
            because("Expose the patched Netty HTTP codec directly on the buildscript classpath for Dependabot")
        }
        classpath("io.netty:netty-codec:$nettyVersion") {
            because("Expose the patched Netty codec directly on the buildscript classpath for Dependabot")
        }
        classpath("io.netty:netty-common:$nettyVersion") {
            because("Expose the patched Netty common artifact directly on the buildscript classpath for Dependabot")
        }
        classpath("com.google.protobuf:protobuf-java:$protobufVersion") {
            because("Expose the patched protobuf-java artifact directly on the buildscript classpath for Dependabot")
        }
        classpath("com.google.protobuf:protobuf-kotlin:$protobufVersion") {
            because("Expose the patched protobuf-kotlin artifact directly on the buildscript classpath for Dependabot")
        }
        classpath("org.apache.commons:commons-lang3:$commonsLangVersion") {
            because("Expose the patched commons-lang3 artifact directly on the buildscript classpath for Dependabot")
        }
        constraints {
            classpath("io.netty:netty-codec-http2:$nettyVersion") {
                because("Dependabot flags AGP and UTP transitives unless the patched Netty version is declared explicitly")
            }
            classpath("io.netty:netty-handler:$nettyVersion") {
                because("Keep Netty handler on the line patched for SslHandler and SniHandler advisories")
            }
            classpath("io.netty:netty-codec-http:$nettyVersion") {
                because("Keep Netty HTTP codecs on the line patched for request smuggling and CRLF advisories")
            }
            classpath("io.netty:netty-codec:$nettyVersion") {
                because("Keep Netty codec on the line patched for decompression DoS advisories")
            }
            classpath("io.netty:netty-common:$nettyVersion") {
                because("Keep Netty common on the line patched for Windows environment-file DoS advisories")
            }
            classpath("com.google.protobuf:protobuf-java:$protobufVersion") {
                because("Keep protobuf-java on the advisory-patched 3.25.x line")
            }
            classpath("com.google.protobuf:protobuf-kotlin:$protobufVersion") {
                because("Keep protobuf-kotlin on the advisory-patched 3.25.x line")
            }
            classpath("org.apache.commons:commons-compress:$commonsCompressVersion") {
                because("Keep commons-compress on the line patched for Pack200 and DUMP-file advisories")
            }
            classpath("org.apache.commons:commons-lang3:$commonsLangVersion") {
                because("commons-compress 1.26.0 introduces commons-lang3, which needs 3.18.0 for CVE-2025-48924")
            }
            classpath("org.bitbucket.b_c:jose4j:$jose4jVersion") {
                because("bundletool pulls jose4j on the build classpath")
            }
            classpath("org.jdom:jdom2:$jdomVersion") {
                because("jetifier-processor pulls jdom2 on the build classpath")
            }
        }
    }

    configurations.configureEach {
        resolutionStrategy.eachDependency {
            when {
                requested.group == "io.netty" -> {
                    useVersion(nettyVersion)
                    because("AGP and UTP resolve Netty artifacts with multiple open GHSA advisories")
                }

                requested.group == "com.google.protobuf" -> {
                    useVersion(protobufVersion)
                    because("Keep protobuf on the advisory-patched 3.25.x line used by the Android build toolchain")
                }

                requested.group == "org.apache.commons" && requested.name == "commons-compress" -> {
                    useVersion(commonsCompressVersion)
                    because("AGP transitives currently pull commons-compress 1.21, which is affected by open advisories")
                }

                requested.group == "org.apache.commons" && requested.name == "commons-lang3" -> {
                    useVersion(commonsLangVersion)
                    because("commons-compress 1.26.0 brings commons-lang3, which has an uncontrolled recursion advisory before 3.18.0")
                }

                requested.group == "org.bitbucket.b_c" && requested.name == "jose4j" -> {
                    useVersion(jose4jVersion)
                    because("bundletool pulls jose4j 0.9.5, which is vulnerable to compressed JWE DoS")
                }

                requested.group == "org.jdom" && requested.name == "jdom2" -> {
                    useVersion(jdomVersion)
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

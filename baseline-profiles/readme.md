// To generate the files, run the Gradle task: ./gradlew :app:generateBaselineProfile

Required Dependencies & Their Roles

androidx-baselineprofile-gradle (Gradle Plugin)
Where: Applied in your top-level and app-level build.gradle files.
Role: Automates the entire process. It creates tasks (like :app:generateBaselineProfile) to run your generator, extract the rules, and bundle them into your release APK or AAB.

androidx-baseline-profile (Library)
Where: Added to your app module dependencies using the baselineProfile configuration.
Role: Links your app module to the specific producer module (like :baselineprofile) where your profiles are generated.

androidx-benchmark-macro (Macrobenchmark Library)
Where: Added to your dedicated generator module (e.g., :baselineprofile).
Role: Provides the BaselineProfileRule used to write the actual Kotlin/Java tests that simulate user journeys and capture the profile data.

androidx-profileinstaller (Profile Installer)
Where: Added to your app module dependencies as a standard implementation.
Role: Essential for "sideloading" and compiling the profile on the device. It ensures the profile is installed on older Android versions (API 24–28) and on devices that don't use Google Play Services.

Summary of Where to Place Them
Dependency 	         Location	             Purpose
Gradle Plugin	    Project/App plugins	   Task automation & build integration
Baseline Profile	App dependencies	   Linking generator to consumer
Benchmark Macro	    Generator Module	   Writing the generation rules
Profile Installer	App dependencies	   Installing the profile on-device


Final Steps to Run
Sync Gradle after updating gradle.properties.
Clean the project: ./gradlew clean (Important to clear the benchmarkRelease metadata).
Build: ./gradlew :app:assembleBenchmarkRelease.
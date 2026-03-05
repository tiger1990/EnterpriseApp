// To generate the files, run the Gradle task: ./gradlew :app:generateBaselineProfile

Final Steps to Run
Sync Gradle after updating gradle.properties.
Clean the project: ./gradlew clean (Important to clear the benchmarkRelease metadata).
Build: ./gradlew :app:assembleBenchmarkRelease.
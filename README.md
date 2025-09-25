1. Install Git, Java (tested on OpenJDK@17), Gradle
2. Clone this repository
3. Fetch .proto files: `git submodule update --init`
4. Build project: `gradle build`
5. Run demo: `gradle --warning-mode none run`, enter query & API key
plugins {
    id("org.sonarqube") version "7.2.0.6526"
}

// Root-level SonarQube configuration shared by all subprojects
sonar {
    properties {
        property("sonar.projectKey", "BTE-Germany_BTEG-Teleportation_ff066355-619d-48f8-a781-165590e97c9c")
        property("sonar.projectName", "BTEG-Teleportation")
        property("sonar.host.url", "https://sonarqube.app.k8s.bteger.dev")
    }
}

version = "1.0.0"

subprojects {
    // Apply SonarQube to every module so analysis runs from the root
    apply(plugin = "org.sonarqube")

    sonar {
        properties {
            property("sonar.sources", "src")
            // Give each module a distinct name inside the project if desired
            property("sonar.projectName", "${rootProject.name}:${project.name}")
        }
    }
}

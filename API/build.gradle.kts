plugins {
    `java-library`
    id("buildlogic.java-conventions")
}

dependencies {
    api(libs.com.google.guava.guava)
}

java.sourceCompatibility = JavaVersion.VERSION_21
java.targetCompatibility = JavaVersion.VERSION_21

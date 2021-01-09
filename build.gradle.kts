import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.4.21"
    application
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

group = "me.zhiyao"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    //implementation(kotlin("stdlib-jdk8"))
    implementation("org.slf4j:slf4j-api:1.7.30")
    implementation("ch.qos.logback:logback-core:1.2.3")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("com.squareup.okhttp3:okhttp:4.9.0")
    implementation("net.coobird:thumbnailator:0.4.13")
    implementation("com.google.code.gson:gson:2.8.6")
}

application {
    //mainClass.set("MainKt")
    mainClassName = "me.zhiyao.hexo.Main"
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

tasks {
    named<ShadowJar>("shadowJar") {
        archiveBaseName.set("me.zhiyao.hexo.hexo")
        mergeServiceFiles()
    }
}

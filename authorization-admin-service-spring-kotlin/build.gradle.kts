import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("plugin.jpa") version "1.2.71"
	id("org.springframework.boot") version "2.1.5.RELEASE"
	id("io.spring.dependency-management") version "1.0.7.RELEASE"
	kotlin("jvm") version "1.2.71"
	kotlin("plugin.spring") version "1.2.71"
	kotlin("plugin.allopen") version "1.2.71"
	kotlin("kapt") version "1.2.71"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

dependencies {
	implementation group: 'org.springframework.boot', name: 'spring-boot-starter-data-jpa', version: project.springBootStarterDataJpaVersion
    implementation group: 'org.springframework.boot', name: 'spring-boot-starter-web', version: project.springBootStarterWebVersion
    implementation group: 'org.springframework.cloud', name: 'spring-cloud-starter-oauth2', version: project.springCloudStarterOauth2Version
    
    implementation group: 'org.projectlombok', name: 'lombok', version: project.lombokVersion
    implementation group: 'org.apache.commons', name: 'commons-text', version: project.commonsTextVersion
    
    implementation group: 'javax.xml.bind', name: 'jaxb-api', version: project.jaxbApiVersion
    implementation group: 'javax.activation', name: 'activation', version: project.javaxActivationVersion
    implementation group: 'org.glassfish.jaxb', name: 'jaxb-runtime', version: project.jaxbRuntimeVersion
    
    implementation 'org.jetbrains.kotlin:kotlin-stdlib-jdk8'
	implementation 'org.jetbrains.kotlin:kotlin-reflect'
    
    runtime group: 'mysql', name: 'mysql-connector-java', version: project.mysqlConnectorJavaVersion
    
    testImplementation (group: 'org.springframework.boot', name: 'spring-boot-starter-test', version: project.springBootStarterTestVersion) {
	    exclude group: 'junit', module: 'junit' //by both name and group
	}
    testImplementation group: 'org.springframework.security', name: 'spring-security-test', version: project.springSecurityTestVersion
    testImplementation group: 'io.rest-assured', name: 'rest-assured', version: project.restAssuredVersion
    testImplementation group: 'com.h2database', name: 'h2', version: project.h2Version
    
    testImplementation group: 'org.junit.jupiter', name: 'junit-jupiter-engine', version: project.junit5Version
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "1.8"
	}
}

allOpen {
	annotation("javax.persistence.Entity")
	annotation("javax.persistence.Embeddable")
	annotation("javax.persistence.MappedSuperclass")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
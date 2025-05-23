plugins {
	java
	jacoco
	id("org.sonarqube") version "6.1.0.5360"
	id("org.springframework.boot") version "3.4.4"
	id("io.spring.dependency-management") version "1.1.7"
}

sonar {
	properties {
		property("sonar.projectKey", "coupon-service-rizzerve")
		property("sonar.projectName", "coupon-service-rizzerve")
		property("sonar.host.url", "https://sonarqube.cs.ui.ac.id")
	}
}


group = "id.ac.ui.cs.advprog"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(21))
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.postgresql:postgresql")

    compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")

	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	annotationProcessor("org.projectlombok:lombok")


	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
	}


	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
	// ✅ WAJIB untuk JUnit 5 (tanpa ini, test tidak dikenali!)
	useJUnitPlatform()


	 filter {
	     excludeTestsMatching("*FunctionalTest")
	 }

	// ✅ Logging test supaya bisa lihat apa yang dijalankan
	testLogging {
		events("started", "passed", "skipped", "failed")
	}

	finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)
}

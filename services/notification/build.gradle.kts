plugins {
	java
	id("org.springframework.boot") version "3.3.2"
	id("io.spring.dependency-management") version "1.1.6"
}

group = "com.yechat"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
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

extra["springCloudVersion"] = "2023.0.3"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-mail")
	/*implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")*/
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	/*implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")*/
	/*implementation("org.springframework.cloud:spring-cloud-stream-binder-kafka-reactive")*/
	implementation("org.springframework.boot:spring-boot-starter-rsocket")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.kafka:spring-kafka")
	/*testImplementation("org.springframework.cloud:spring-cloud-stream-test-binder")*/
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("org.springframework.kafka:spring-kafka-test")
	/*testImplementation("org.springframework.cloud:spring-cloud-function-rsocket")*/
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:kafka")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

# JDK 8 और Maven सेट करें
FROM maven:3.8.4-openjdk-8-slim AS build

# वर्किंग डायरेक्टरी सेट करें
WORKDIR /usr/src/app

# प्रोजेक्ट की सभी फाइल्स कॉपी करें
COPY . .

# Maven Build रन करें
RUN mvn clean package -DskipTests

# रनटाइम के लिए हल्का JDK 8 सेट करें
FROM openjdk:8-jdk-alpine

# वर्किंग डायरेक्टरी बनाएं
WORKDIR /app

# पहले स्टेप में बनी JAR फाइल को कॉपी करें
COPY --from=build /usr/src/app/target/Blood-Donation-Backend-0.0.1-SNAPSHOT.jar app.jar

# पोर्ट एक्सपोज करें
EXPOSE 8082

# JAR फाइल रन करें
CMD ["java", "-jar", "app.jar"]

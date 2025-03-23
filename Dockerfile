# JDK 8 और Maven सेट करें  
FROM maven:3.8.4-openjdk-8-slim  

# प्रोजेक्ट फाइल्स कॉपी करें  
COPY . /usr/src/app  
WORKDIR /usr/src/app  

# Maven Build चलाएं  
RUN ./mvnw clean package  

# पोर्ट एक्सपोज़ करें  
EXPOSE 8082

# JAR फाइल रन करें  
CMD ["java", "-jar", "target/Blood-Donation-Backend-0.0.1-SNAPSHOT.jar"]
 

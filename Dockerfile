FROM openjdk:8-jdk-alpine AS GFC

# Get required software packages
RUN apk update && apk add git && apk add maven

# Get GFC
RUN git clone 'https://github.com/vaibhavc99/GDRB.git'
WORKDIR /GDRB/SourceCode-GFC

# Build package
RUN mvn package

# Expose port
EXPOSE 4011/tcp

# Execute GFC
CMD java -cp ./target/factchecking-1.0-SNAPSHOT-jar-with-dependencies.jar edu.wsu.eecs.gfc.exps.GFC ./sample_data/ ./output 0.01 0.0001 4 50

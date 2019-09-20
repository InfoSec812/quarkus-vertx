# Demonstration of running a "Pure" Vert.x application using Quarkus and native-image

## Prerequisites
1. Install GraalVM >= 19.1
2. Set `GRAALVM_HOME` environment variable to point to GraalVM

## Compile to Native Image
```bash
./mvnw clean package -Pnative
```
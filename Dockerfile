# ===== 构建阶段 =====
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# 先复制 pom.xml（利用缓存）
COPY pom.xml .

# 下载依赖（关键优化）
RUN mvn dependency:go-offline -B

# 再复制源码
COPY src ./src

# 构建
RUN mvn package -DskipTests

# ===== 运行阶段 =====
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS="-Xms256m -Xmx512m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
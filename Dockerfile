FROM openjdk:11-jre-slim
RUN useradd --user-group --system --create-home --no-log-init app
USER app
COPY nellygateway/target/artifact/* /
ENTRYPOINT ["java","-jar","/nellygateway.jar"]
# Build and run docker image
# docker build -t nellygateway:latest .
# docker run -p 8080:8080 --env-file env.list  nellygateway:latest

# Test for docker misconfiguration with dockle
# VERSION=$(curl --silent "https://api.github.com/repos/goodwithtech/dockle/releases/latest" | grep '"tag_name":' | sed -E 's/.*"v([^"]+)".*/\1/') && curl -L -o dockle.tar.gz https://github.com/goodwithtech/dockle/releases/download/v${VERSION}/dockle_${VERSION}_Linux-64bit.tar.gz &&  tar zxvf dockle.tar.gz
# ./dockle --exit-code 1 nellygateway:latest
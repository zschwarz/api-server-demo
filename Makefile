VERSION     ?= 1.0-SNAPSHOT
SKIP_TESTS  ?= true
DOCKER_ORG	?= docker.io/kornysd
TAG ?= latest
MAVEN_ARGS="-DskipTests"

all: package_java docker_build docker_tag docker_push

package_java:
	mvn -U clean package -DskipTests $(MAVEN_ARGS)

docker_build: package_java
	if [ -f api-server/Dockerfile ]; then cd api-server && docker build --build-arg version=$(VERSION) -t api-server-demo:$(TAG) . ; fi
	docker images | grep api-server-demo

docker_push:
	docker push $(DOCKER_ORG)/api-server-demo:$(TAG)

docker_tag:
	docker tag api-server-demo:$(TAG) $(DOCKER_ORG)/api-server-demo:$(TAG)

.PHONY: package_java docker_build docker_tag docker_push

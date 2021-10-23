.PHONY: default
default: clean

.PHONY: clean
clean:
	@for pj in $$(ls | grep app); do \
		pushd $$pj; \
		./mvnw -B clean; \
		popd; \
	done

.PHONY: build
build:
	@for pj in $$(ls | grep app); do \
		pushd $$pj; \
		./mvnw -B -Ptracing,actuator -DskipTests spring-boot:build-image; \
		popd; \
	done

.PHONY: test
test:
	@for pj in $$(ls | grep app); do \
		pushd $$pj; \
		./mvnw -B verify; \
		popd; \
	done

.PHONY: up
up:
	docker compose up -d

.PHONY: logs
logs:
	docker compose logs -f supplier-service1 supplier-service2 consumer-service1 consumer-service2

.PHONY: logs-all
logs-all:
	docker compose logs -f

.PHONY: down
down:
	docker compose down -v

.PHONY: destroy
destroy:
	ls | grep service | awk '{print $$0 ":0.0.1-SNAPSHOT"}' | xargs docker rmi

.PHONY: demo1
demo1:
	curl -s localhost:8080 -H "Content-Type: application/json" -d '{"name":"hoge"}'

.PHONY: demo2
demo2:
	@for i in {1..10000}; do \
		curl -s localhost:8080 -H "Content-Type: application/json" -d '{"name":"hoge-'$$(printf "%05d" "$$i")'"}'; \
		sleep 1; \
	done


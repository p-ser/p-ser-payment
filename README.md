### 1. 프로젝트 빌드
```shell
 ./gradlew build
```
### 2. 이미지 빌드
```shell
 docker buildx build -t amanecopse/p-ser-payment .
```
### 3. 컨테이너 실행 (윈도우 환경)
```shell
 docker run --name payment -p 8081:8080 -v C:\properties:/share amanecopse/p-ser-payment
```
### 4. push
```shell
 docker push amanecopse/p-ser-payment
```
### 5. k8s
```shell
 kubectl apply -k ./k8s
```
FROM openjdk:11-jdk

# JAR_FILE 변수 정의 -> 기본적으로 jar file이 2개이기 때문에 이름을 특정해야함
ARG JAR_FILE=./build/libs/config-server-0.0.1-SNAPSHOT.jar

# encrypt 위한 key 복사
COPY encryptionKey.jks encryptionKey.jks

# JAR 파일 메인 디렉토리에 복사
COPY ${JAR_FILE} app.jar

# 시스템 진입점 정의
ENTRYPOINT ["java","-jar","/app.jar"]

# 비대칭 암호화 위한 key-store 사용법
# config server 폴더에서
# key store파일(.jks) 생성
# keytool -genkeypair -alias encryptionKey -keyalg RSA -keypass "storeEncryptionKey" -keystore encryptionKey.jks -storepass "storeEncryptionKey" -dname "CN=HJ Kim"
# 생성한 key store 파일을 이용해 공개키를 위한 인증서 생성
# keytool -export -alias encryptionKey -keystore encryptionKey.jks -rfc -file trust_key.cer
# 인증서를 통해 인증된 KEY STORE 파일 생성
# keytool -import -alias trust_key -file trust_key.cer -keystore publicKey.jks
# pw는 위와 같게함
# encrytionKey.jks는 config server가 가지고 publicKey.jks는 다른 서비스나 클라이언트에 제공

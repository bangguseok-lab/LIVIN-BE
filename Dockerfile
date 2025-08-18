FROM tomcat:9.0-jdk17

# 기존 webapps 폴더 제거 (선택)
RUN rm -rf /usr/local/tomcat/webapps/*

# 빌드된 WAR 파일을 ROOT.war로 복사
COPY build/libs/*.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

CMD ["catalina.sh", "run"]

FROM alpine:3.7

USER root

# Install JVM
RUN apk add --update curl openjdk8=8.151.12-r0 && \
	echo "PASS_MAX_DAYS 90" >> /etc/login.defs && \
	echo "PASS_MIN_DAYS 1" >> /etc/login.defs && \
	mkdir -p /etc/pam.d && \
	echo "password    [success=1 default=ignore]  pam_unix.so minlen=8 sha512" >> /etc/pam.d/common-password && \
	rm -rf /var/cache/apk/* ;
		

# Set default encoding
ENV LANG en_US.UTF-8
ENV LANGUAGE en_US:en
ENV LC_ALL en_US.UTF-8

# Set Java Home
ENV JAVA_HOME /usr/lib/jvm/java-1.8-openjdk

# Add source file
ADD . /urlcollector-service

# Build Application
RUN curl -O http://www-us.apache.org/dist/maven/maven-3/3.5.3/binaries/apache-maven-3.5.3-bin.tar.gz && \
	mkdir /opt && \
	tar -xf apache-maven-3.5.3-bin.tar.gz -C /opt/ && \
	ln -s /opt/apache-maven-3.5.3/bin/mvn /usr/bin/mvn && \
	rm apache-maven-3.5.3-bin.tar.gz && \
	mvn install -f /urlcollector-service/pom.xml && \
	mv /urlcollector-service/target/urlcollector-0.0.1-SNAPSHOT.jar /opt && \
	rm -rf /urlcollector-service && \
	rm -rf /root/.m2 && \
	rm -rf /opt/apache-maven-3.5.3 ;

CMD ["java", "-jar", "/opt/urlcollector-0.0.1-SNAPSHOT.jar"]
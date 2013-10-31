echo Intall google protocol buffer 2.5.0
call mvn install:install-file ^
	-Dfile=protobuf-java-2.5.0.jar ^
	-Dsources=protobuf-java-2.5.0-sources.jar ^
	-Djavadoc=protobuf-java-2.5.0-javadoc.jar ^
	-DgroupId=com.google.protobuf ^
	-DartifactId=jprotobuf-java ^
	-Dversion=2.5.0 ^
	-Dpackaging=jar
	
echo Install junit 4.4
call mvn install:install-file ^
	-Dfile=junit-4.4.jar ^
	-Dsources=junit-4.4-sources.jar ^
	-Djavadoc=junit-4.4-javadoc.jar ^
	-DgroupId=junit ^
	-DartifactId=junit ^
	-Dversion=4.4 ^
	-Dpackaging=jar
	
echo Install log4j 1.2.14
call mvn install:install-file ^
	-Dfile=log4j-1.2.14.jar ^
	-DgroupId=log4j ^
	-DartifactId=log4j ^
	-Dversion=1.2.14 ^
	-Dpackaging=jar
	
echo Install derby 10.10.1.1
call mvn install:install-file ^
	-Dfile=derby-10.10.1.1.jar ^
	-DgroupId=org.apache.derby ^
	-DartifactId=derby ^
	-Dversion=10.10.1.1 ^
	-Dpackaging=jar

echo Install derby client 10.10.1.1
call mvn install:install-file ^
	-Dfile=derbyclient-10.10.1.1.jar ^
	-DgroupId=org.apache.derby ^
	-DartifactId=derbyclient ^
	-Dversion=10.10.1.1 ^
	-Dpackaging=jar	
	
echo Install eclipselink 2.0.0
call mvn install:install-file ^
	-Dfile=eclipselink-2.0.0.jar ^
	-DgroupId=org.eclipse.persistence ^
	-DartifactId=eclipselink ^
	-Dversion=2.0.0 ^
	-Dpackaging=jar	

echo Install javax.persistence 2.0.0
call mvn install:install-file ^
	-Dfile=javax.persistence-2.0.0.jar ^
	-DgroupId=org.eclipse.persistence ^
	-DartifactId=javax.persistence ^
	-Dversion=2.0.0 ^
	-Dpackaging=jar	
	
echo Install commons-io-2.4
call mvn install:install-file ^
	-Dfile=commons-io-2.4.jar ^
	-Dsources=commons-io-2.4-sources.jar ^
	-Djavadoc=commons-io-2.4-javadoc.jar ^
	-DgroupId=commons-io ^
	-DartifactId=commons-io ^
	-Dversion=2.4 ^
	-Dpackaging=jar
	
echo Install zip4jj 1.2.4
call mvn install:install-file ^
	-Dfile=zip4j-1.2.4.jar ^
	-Dsources=zip4j-1.2.4-sources.jar ^
	-Djavadoc=zip4j-1.2.4-javadoc.jar ^
	-DgroupId=net.lingala.zip4j ^
	-DartifactId=zip4j ^
	-Dversion=1.2.4 ^
	-Dpackaging=jar
	echo Install zip4jj 1.2.4

echo Install internals by Kornelius
call mvn install:install-file ^
	-Dfile=internals-0.0.1-20131001.090326-11.jar ^
	-Dsources=internals-0.0.1-SNAPSHOT-sources.jar ^
	-Djavadoc=internals-0.0.1-SNAPSHOT-javadoc.jar ^
	-DgroupId=de.fraunhofer.iese.ind2uce ^
	-DartifactId=internals ^
	-Dversion=0.0.1-SNAPSHOT ^
	-Dpackaging=jar
	
pause->null
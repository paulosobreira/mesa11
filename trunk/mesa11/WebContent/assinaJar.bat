del a-mesa11.jar
ren mesa11.jar a-mesa11.jar
keytool -genkey -dname "cn=Paulo Sobreira, ou=Web Site, o=Sobreira, c=BR" -alias mesa11-key -keystore jar-keystore -storepass 123456 -validity 1000

jarsigner -keystore jar-keystore -storepass 123456 -signedjar mesa11.jar a-mesa11.jar mesa11-key

keytool -export -keystore jar-keystore -alias mesa11-key -file keycert.x509
keytool -import -alias mesa11-key -file keycert.x509


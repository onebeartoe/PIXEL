mvn clean
mvn install
cd /Users/al/pi/
rm pixelcade-installer.jar
cp /Users/al/Documents/pixel/PIXEL/pixelcade-installer/target/pixelcade-installer-1.0.jar /Users/al/pi/pixelcade-installer.jar
cd /Users/al/pi/
java -jar pixelcade-installer.jar
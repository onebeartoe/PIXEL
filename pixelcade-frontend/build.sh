mvn clean
mvn install
cd /Users/al/pi
rm pixelcade.jar
cp /Users/al/Documents/pixel/PIXEL/pixelcade-frontend/target/pixelcade-front-end-1.0-jar-with-dependencies.jar /Users/al/pi/pixelcade.jar
java -jar pixelcade.jar -m stream -c mame -g "f:\program files\roms\yo\##$#\1943.zip"


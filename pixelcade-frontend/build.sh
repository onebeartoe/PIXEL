mvn clean
mvn install
cd /Users/al/pixelcade
rm pixelcade.jar
cp /Users/al/Documents/pixel/PIXEL/pixelcade-frontend/target/pixelcade-front-end-1.0-jar-with-dependencies.jar /Users/al/pixelcade/pixelcade.jar
java -jar pixelcade.jar -m stream -c mame -g "f:\program files\roms\yo\##$#\1943.zip"


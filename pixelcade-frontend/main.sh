cd /Users/mina/code/PIXEL/pixelcade-frontend/
mvn clean
mvn install
rm pixelcade.jar
cp /Users/mina/code/PIXEL/pixelcade-frontend/target/pixelcade-front-end-1.0-jar-with-dependencies.jar /Users/mina/code/PIXEL/pixelcade-frontend/pixelcade.jar
java -jar pixelcade.jar -m stream -c mame -g "f:\program files\roms\yo\##$#\pacman.zip"


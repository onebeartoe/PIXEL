cd /Users/mina/code/PIXEL/
mvn clean
mvn install
cd /Users/mina/pixelc/
rm pixelweb.jar
cp /Users/mina/code/PIXEL/pixel-web-enabled/target/pixel-web-enabled-1.0-jar-with-dependencies.jar /Users/mina/pixelc/pixelweb.jar
java -jar pixelweb.jar
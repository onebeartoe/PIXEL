#!/bin/bash

stretch_os=false
buster_os=false
ubuntu_os=false
pi4=false
install_succesful=false
black=`tput setaf 0`
red=`tput setaf 1`
green=`tput setaf 2`
yellow=`tput setaf 3`
blue=`tput setaf 4`
magenta=`tput setaf 5`
white=`tput setaf 7`
reset=`tput sgr0`
version=3  #increment this as the script is updated
#echo "${red}red text ${green}green text${reset}"

cat << "EOF"
       _          _               _
 _ __ (_)_  _____| | ___ __ _  __| | ___
| '_ \| \ \/ / _ \ |/ __/ _` |/ _` |/ _ \
| |_) | |>  <  __/ | (_| (_| | (_| |  __/
| .__/|_/_/\_\___|_|\___\__,_|\__,_|\___|
|_|
EOF

echo "${magenta}        INSTALLER FOR RETROPIE      ${white}"
echo ""
echo "${red}IMPORTANT:${white} This script will work on a Pi 2, Pi Zero W, and Pi 3B, Pi 3B+, and Pi 4 that is running RetroPie"
echo "Now connect Pixelcade to a free USB port on your Pi (do not use a USB hub)"
echo "Ensure the toggle switch on the Pixelcade board is pointing towards USB and not BT"

read -p "${magenta}Continue? ${white}" -n 1 -r
echo    # (optional) move to a new line
if [[ ! $REPLY =~ ^[Yy]$ ]]
then
    exit 1
fi

# let's check the version and only proceed if the user has an older version
if [[ -f "$HOME/pixelcade/pixelcade-version" ]]; then
  echo "Existing Pixelcade installation detected, checking version..."
  read -r currentVersion<$HOME/pixelcade/pixelcade-version

  if [[ $currentVersion -lt $version ]]
    then
        echo "Older Pixelcade version detected, now upgrading..."
    else
        echo "Your Pixelcade version is up to date, exiting..."
        echo "You may force a re-install by deleting the file $HOME/pixelcade/pixelcade-version"
        exit 1
  fi
else
   echo "Starting new Pixelcade installation..."
fi

# detect what OS we have
if lsb_release -a | grep -q 'stretch'; then
   echo "${yellow}Linux Stretch Detected${white}"
   stretch_os=true
elif lsb_release -a | grep -q 'buster'; then
   echo "${yellow}Linux Buster Detected${white}"
   buster_os=true
elif lsb_release -a | grep -q 'ubuntu'; then
    echo "${yellow}Ubuntu Linux Detected${white}"
    ubuntu_os=true
    echo "Installing curl..."
    sudo apt install curl
else
   echo "${red}Sorry, neither Linux Stretch, Linux Buster, or Ubuntu were detected, exiting..."
   exit 1
fi

# let's detect if Pixelcade is connected
if ls /dev/ttyACM0 | grep -q '/dev/ttyACM0'; then
   echo "${yellow}Pixelcade LED Marquee Detected${white}"
else
   echo "${red}Sorry, Pixelcade LED Marquee was not detected, pleasse ensure Pixelcade is USB connected to your Pi and the toggle switch on the Pixelcade board is pointing towards USB, exiting..."
   exit 1
fi

#let's check if retropie is installed
if [[ -f "/opt/retropie/configs/all/autostart.sh" ]]; then
  echo "RetroPie installation detected, continuing..."
else
   echo "${red}RetroPie is not installed, please install RetroPie first"
   exit 1
fi

if cat /proc/device-tree/model | grep -q 'Pi 4'; then
   echo "${yellow}Raspberry Pi 4 detected..."
   pi4=true
fi

# we have all the pre-requisites so let's continue
sudo apt-get -y update

# install java runtime
if [ "$stretch_os" = true ] ; then
   echo "${yellow}Installing Java 8...${white}"
   sudo apt-get -y install oracle-java8-jdk
elif [ "$buster_os" = true ]; then
   echo "${yellow}Installing Java OpenJDK 11...${white}"
   sudo apt-get -y install openjdk-11-jre
elif [ "$ubuntu_os" = true ]; then
    echo "${yellow}Installing Java OpenJDK 11...${white}"
    sudo apt-get -y install openjdk-11-jre
else
    echo "${red}Sorry, neither Linux Stretch or Linux Buster was detected, exiting..."
    exit 1
fi

# this is where pixelcade will live

if [ -d "$HOME/pixelcade" ]; then
  echo "${yellow}Pixelcade directory also exists...${white}"
  cd $HOME/pixelcade
else
  echo "${yellow}Creating home directory for Pixelcade...${white}"
  mkdir $HOME/pixelcade && cd $HOME/pixelcade
fi

# download pixelweb.jar
echo "${yellow}Downloading the Pixelcade Listener (pixelweb)...${white}"
curl -LO http://pixelcade.org/pi/beta/pixelweb.jar
echo " "
# run pixelcade listener in the background, Pixelcade must be USB connected to the Pi at this point, it will hang here if Pixelcade not USB connected
java -jar pixelweb.jar -b & #run pixelweb in the background
# download and install artwork
echo "${yellow}Now downloading and installing artwork...${white}"
cd $HOME/pixelcade
curl -LO http://pixelcade.org/pi/artwork.zip && unzip -o artwork.zip
rm artwork.zip #clean-up

# lets install the correct mod based on the OS
if [ "$stretch_os" = true ] ; then
   curl -LO http://pixelcade.org/pi/esmod-stretch.deb && sudo dpkg -i esmod-stretch.deb
#elif [ "$buster_os" = true ]; then
#  curl -LO http://pixelcade.org/pi/esmod-buster.deb && sudo dpkg -i esmod-buster.deb
elif [ "$pi4" = true ]; then
  curl -LO http://pixelcade.org/pi/esmod-pi4.deb && sudo dpkg -i esmod-pi4.deb
elif [ "$ubuntu_os" = true ]; then
  curl -LO http://pixelcade.org/pi/esmod-ubuntu.deb && sudo dpkg -i esmod-ubuntu.deb
else
    echo "${red}Sorry, neither Linux Stretch, Linux Buster, or Ubuntu was detected, exiting..."
    exit 1
fi

#get the pixelcade startup-up script
echo "${yellow}Now downloading Pixelcade Startup Script...${white}"
cd $HOME/pixelcade
curl -LO http://pixelcade.org/pi/pixelcade-startup.sh
sudo chmod +x $HOME/pixelcade/pixelcade-startup.sh

# let's check if autostart.sh already has pixelcade added and if so, we don't want to add it twice
cd /opt/retropie/configs/all/
if cat autostart.sh | grep -q 'pixelcade'; then
  echo "${yellow}Pixelcade already added to autostart.sh, skipping...${white}"
else
  sudo sed -i '/^emulationstation.*/i cd $HOME/pixelcade && java -jar pixelweb.jar -b &' autostart.sh #insert this line before emulationstation #auto
  sudo sed -i '/^emulationstation.*/i sleep 30' autostart.sh #insert this line before emulationstation #auto


  #this line makes Pixelcade display the IP address on startup-up but this really isn't needed for the use case of ES where the user already setup the Pi
  #sudo sed -i '/^emulationstation.*/i sleep 10 && cd $HOME/pixelcade && ./pixelcade-startup.sh' autostart.sh #insert this line before emulationstation #auto
fi

# let's change the hostname from retropie to pixelcade and note that the dns name will be pixelcade.local
cd /etc
if cat hostname | grep -q 'pixelcade'; then
   echo "${yellow}Pixelcade already added to hostname, skipping...${white}"
else
   sudo sed -i 's/retropie/pixelcade/g' hostname
   sudo sed -i 's/raspberrypi/pixelcade/g' hostname
fi

if cat hosts | grep -q 'pixelcade'; then
   echo "${yellow}Pixelcade already added to hosts, skipping...${white}"
else
  sudo sed -i 's/retropie/pixelcade/g' hosts
  sudo sed -i 's/raspberrypi/pixelcade/g' hosts
fi

sleep 5
# let's send a test image and see if it displays
cd $HOME/pixelcade
java -jar pixelcade.jar -m stream -c mame -g 1941

#let's write the version so the next time the user can try and know if he/she needs to upgrade
echo $version > $HOME/pixelcade/pixelcade-version

echo " "
while true; do
    read -p "${magenta}Is the 1941 Game Logo Displaying on Pixelcade Now?${white}" yn
    case $yn in
        [Yy]* ) echo "${green}INSTALLATION COMPLETE , please now reboot and then Pixelcade will be controlled by RetroPie${white}" && install_succesful=true; break;;
        [Nn]* ) echo "${red}It may still be ok and try rebooting, you can also refer to https://pixelcade.org/download-pi/ for troubleshooting steps" && exit;;
        * ) echo "Please answer yes or no.";;
    esac
done

if [ "$install_succesful" = true ] ; then
  while true; do
      read -p "${magenta}Reboot Now?${white}" yn
      case $yn in
          [Yy]* ) sudo reboot; break;;
          [Nn]* ) echo "${yellow}Please reboot when you get a chance" && exit;;
          * ) echo "Please answer yes or no.";;
      esac
  done
fi

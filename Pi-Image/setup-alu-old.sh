#!/bin/bash

stretch_os=false
buster_os=false
ubuntu_os=false
retropie=false
pizero=false
pi4=false
java_installed=false
install_succesful=false
black=`tput setaf 0`
red=`tput setaf 1`
green=`tput setaf 2`
yellow=`tput setaf 3`
blue=`tput setaf 4`
magenta=`tput setaf 5`
white=`tput setaf 7`
reset=`tput sgr0`
version=4  #increment this as the script is updated
#echo "${red}red text ${green}green text${reset}"

cat << "EOF"
       _          _               _
 _ __ (_)_  _____| | ___ __ _  __| | ___
| '_ \| \ \/ / _ \ |/ __/ _` |/ _` |/ _ \
| |_) | |>  <  __/ | (_| (_| | (_| |  __/
| .__/|_/_/\_\___|_|\___\__,_|\__,_|\___|
|_|
EOF

echo "${magenta}       ART BROWSER INCLUDING AtGames Legends Ultimate : Installer Script Version $version    ${white}"
echo ""
echo "${red}IMPORTANT:${white} This script will work on a Pi 2, Pi Zero W, Pi 3B, Pi 3B+, and Pi 4"
echo "Now connect Pixelcade to a free USB port on your Pi (directly connected to your Pi or use a powered USB hub)"
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
  echo "RetroPie installation detected..."
  retropie=true
else
   echo "${yellow}RetroPie is not installed..."
fi

if cat /proc/device-tree/model | grep -q 'Pi 4'; then
   echo "${yellow}Raspberry Pi 4 detected..."
   pi4=true
fi

if cat /proc/device-tree/model | grep -q 'Pi Zero W'; then
   echo "${yellow}Raspberry Pi Zero detected..."
   pizero=true
fi

if type -p java ; then
  echo "${yellow}Java already installed, skipping..."
  java_installed=true
elif [[ -n "$JAVA_HOME" ]] && [[ -x "$JAVA_HOME/bin/java" ]];  then
  echo "${yellow}Java already installed, skipping..."
  java_installed=true
else
   echo "${yellow}Java not found, let's install Java...${white}"
   java_installed=false
fi

# we have all the pre-requisites so let's continue
sudo apt-get -y update

if [ "$java_installed" = false ] ; then #only install java if it doesn't exist
    if [ "$pizero" = true ] ; then
      echo "${yellow}Installing Zulu Java 8...${white}"
      sudo mkdir /opt/jdk/
      cd /opt/jdk
      sudo curl -LO http://pixelcade.org/pi/zulu8.46.0.225-ca-jdk8.0.252-linux_aarch32hf.tar.gz
      sudo tar -xzvf zulu8.46.0.225-ca-jdk8.0.252-linux_aarch32hf.tar.gz
      sudo update-alternatives --install /usr/bin/java java /opt/jdk/zulu8.46.0.225-ca-jdk8.0.252-linux_aarch32hf/bin/java 252
      sudo update-alternatives --install /usr/bin/javac javac /opt/jdk/zulu8.46.0.225-ca-jdk8.0.252-linux_aarch32hf/bin/javac 252
    elif [ "$stretch_os" = true ]; then
       echo "${yellow}Installing Java 8...${white}"
       sudo apt-get -y install oracle-java8-jdk
    elif [ "$buster_os" = true ]; then #pi zero is arm6 and cannot run the normal java :-( so have to get this special one
       echo "${yellow}Installing OpenJDK 11 JRE...${white}"
       sudo apt-get -y install openjdk-11-jre
    elif [ "$ubuntu_os" = true ]; then
        echo "${yellow}Installing Java OpenJDK 11...${white}"
        sudo apt-get -y install openjdk-11-jre
    else
        echo "${red}Sorry, neither Linux Stretch or Linux Buster was detected, exiting..."
        exit 1
    fi
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


if [ "$retropie" = true ] ; then #skip if no retropie as we'll start this later using systemd
    java -jar pixelweb.jar -b & #run pixelweb in the background\
fi

# download and install artwork
echo "${yellow}Now downloading and installing artwork...${white}"
cd $HOME/pixelcade
curl -LO http://pixelcade.org/pi/artwork.zip && unzip -o artwork.zip
rm artwork.zip #clean-up

if [ "$retropie" = true ] ; then
  # lets install the correct mod based on the OS
  if [ "$stretch_os" = true ] ; then
     curl -LO http://pixelcade.org/pi/esmod-stretch.deb && sudo dpkg -i esmod-stretch.deb
  elif [ "$buster_os" = true ]; then
     curl -LO http://pixelcade.org/pi/esmod-buster.deb && sudo dpkg -i esmod-buster.deb
  elif [ "$ubuntu_os" = true ]; then
     curl -LO http://pixelcade.org/pi/esmod-ubuntu.deb && sudo dpkg -i esmod-ubuntu.deb
  else
      echo "${red}Sorry, neither Linux Stretch, Linux Buster, or Ubuntu was detected, exiting..."
      exit 1
  fi
fi

#get the pixelcade startup-up script
echo "${yellow}Now downloading Pixelcade Startup Script...${white}"
mkdir -p $HOME/pixelcade/system #it should already be there from artwork but just in case
cd $HOME/pixelcade/system
curl -LO http://pixelcade.org/pi/pixelcade-startup.sh
sudo chmod +x $HOME/pixelcade/system/pixelcade-startup.sh

if [ "$retropie" = true ] ; then
  # let's check if autostart.sh already has pixelcade added and if so, we don't want to add it twice
  #cd /opt/retropie/configs/all/
  echo "${yellow}Installing Git...${white}"
  sudo apt -y install git
  if cat /opt/retropie/configs/all/autostart.sh | grep -q 'pixelcade'; then
    echo "${yellow}Pixelcade already added to autostart.sh, skipping...${white}"
  else
    echo "${yellow}Adding Pixelcade to /opt/retropie/configs/all/autostart.sh...${white}"
    sudo sed -i '/^emulationstation.*/i cd $HOME/pixelcade && java -jar pixelweb.jar -b &' /opt/retropie/configs/all/autostart.sh #insert this line before emulationstation #auto
    sudo sed -i '/^emulationstation.*/i sleep 10 && cd $HOME/pixelcade/system && ./pixelcade-startup.sh' /opt/retropie/configs/all/autostart.sh #insert this line before emulationstation #auto
    # now lastly let's comment out emulationstation since we are not using it for this use case
    sed -e '/^emulationstation.*/ s/^#*/#/' -i /opt/retropie/configs/all/autostart.sh
  fi
else #there is no retropie so we need to add pixelcade /etc/rc.local instead

  echo "${yellow}Installing Git...${white}"
  sudo apt -y install git
  echo "${yellow}Installing Fonts...${white}"
  cd $HOME/pixelcade
  curl -LO http://pixelcade.org/pi/fonts.zip && unzip -o fonts.zip
  rm fonts.zip #clean-up
  mkdir $HOME/.fonts
  sudo cp $HOME/pixelcade/fonts/*.ttf /$HOME/.fonts
  sudo apt -y install font-manager
  sudo fc-cache -v -f
  echo "${yellow}Adding Pixelcade to Startup...${white}"
  cd $HOME/pixelcade/system
  curl -LO http://pixelcade.org/pi/autostart.sh
  sudo chmod +x $HOME/pixelcade/system/autostart.sh
  curl -LO http://pixelcade.org/pi/pixelcade.service
  sudo cp pixelcade.service /etc/systemd/system/pixelcade.service
  sudo systemctl start pixelcade.service
  sudo systemctl enable pixelcade.service
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

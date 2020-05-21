#!/bin/bash
stretch_os=false
buster_os=false
ubuntu_os=false
retropie=false
pizero=false
pi4=false
java_installed=false
install_succesful=false
auto_update=false
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

read -p "${magenta}Continue (y/n)? ${white}" -n 1 -r
echo    # (optional) move to a new line
if [[ ! $REPLY =~ ^[Yy]$ ]]
then
    exit 1
fi

while true; do
    read -p "${magenta}Would you like to enable auto updates (y/n)? ${white}" yn
    case $yn in
        [Yy]* ) auto_update=true; break;;
        [Nn]* ) auto_update=false; break;;
        * ) echo "Please answer y or n";;
    esac
done

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

echo "${yellow}Installing Git...${white}"
sudo apt -y install git

# this is where pixelcade will live

echo "${yellow}Installing Pixelcade from GitHub Repo...${white}"
cd $HOME
git clone https://github.com/alinke/pixelcade.git
cd $HOME/pixelcade
git config user.email "sample@sample.com"
git config user.name "sample"
cd $HOME/pixelcade/system
sudo chmod +x update.sh


if [ "$retropie" = true ] ; then #skip if no retropie as we'll start this later using systemd
    java -jar pixelweb.jar -b & #run pixelweb in the background\
fi

#if retropie is present, add our mods
#if [ "$retropie" = true ] ; then
  # lets install the correct mod based on the OS
#  if [ "$stretch_os" = true ] ; then
#     curl -LO http://pixelcade.org/pi/esmod-stretch.deb && sudo dpkg -i esmod-stretch.deb
#  elif [ "$buster_os" = true ]; then
#     curl -LO http://pixelcade.org/pi/esmod-buster.deb && sudo dpkg -i esmod-buster.deb
#  elif [ "$ubuntu_os" = true ]; then
#     curl -LO http://pixelcade.org/pi/esmod-ubuntu.deb && sudo dpkg -i esmod-ubuntu.deb
#  else
#      echo "${red}Sorry, neither Linux Stretch, Linux Buster, or Ubuntu was detected, exiting..."
#      exit 1
#  fi
#fi

cd $HOME
#if retropie is present, add our mods
if [ "$retropie" = true ] ; then
  # lets install the correct mod based on the OS
  if [ "$pi4" = true ] ; then
      curl -LO http://pixelcade.org/pi/esmod-pi4.deb && sudo dpkg -i esmod-pi4.deb
  elif [ "$stretch_os" = true ] ; then
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
echo "${yellow}Configuring Pixelcade Startup Script...${white}"
sudo chmod +x $HOME/pixelcade/system/pixelcade-startup.sh

if [ "$auto_update" = true ] ; then #add git pull to startup
  echo "${yellow}Configuring auto-update...${white}"
  sudo sed -i '/^exit.*/i cd $HOME/pixelcade && git stash && git pull' $HOME/pixelcade/system/pixelcade-startup.sh #insert this line before exit
fi

if [ "$retropie" = true ] ; then
  # let's check if autostart.sh already has pixelcade added and if so, we don't want to add it twice
  #cd /opt/retropie/configs/all/

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
  echo "${yellow}Installing Fonts...${white}"
  cd $HOME/pixelcade
  mkdir $HOME/.fonts
  sudo cp $HOME/pixelcade/fonts/*.ttf /$HOME/.fonts
  sudo apt -y install font-manager
  sudo fc-cache -v -f
  echo "${yellow}Adding Pixelcade to Startup...${white}"
  cd $HOME/pixelcade/system
  sudo chmod +x $HOME/pixelcade/system/autostart.sh
  sudo cp pixelcade.service /etc/systemd/system/pixelcade.service
  #to do add check if the service is already running
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
    read -p "${magenta}Is the 1941 Game Logo Displaying on Pixelcade Now? (y/n)${white}" yn
    case $yn in
        [Yy]* ) echo "${green}INSTALLATION COMPLETE , please now reboot and then Pixelcade will be controlled by RetroPie${white}" && install_succesful=true; break;;
        [Nn]* ) echo "${red}It may still be ok and try rebooting, you can also refer to https://pixelcade.org/download-pi/ for troubleshooting steps" && exit;;
        * ) echo "Please answer yes or no.";;
    esac
done

if [ "$install_succesful" = true ] ; then
  while true; do
      read -p "${magenta}Reboot Now? (y/n)${white}" yn
      case $yn in
          [Yy]* ) sudo reboot; break;;
          [Nn]* ) echo "${yellow}Please reboot when you get a chance" && exit;;
          * ) echo "Please answer yes or no.";;
      esac
  done
fi

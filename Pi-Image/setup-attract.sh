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

echo "${magenta}       Pixelcade for RetroPie Attract : Installer Version $version    ${white}"
echo ""
echo "${red}IMPORTANT:${white} This script will work on a Pi 2, Pi Zero W, Pi 3B, Pi 3B+, and Pi 4"
echo "Now connect Pixelcade to a free USB port on your Pi (directly connected to your Pi or use a powered USB hub)"
echo "Ensure the toggle switch on the Pixelcade board is pointing towards USB and not BT"

retropie=true

if [[ -d "/$HOME/.attract" ]]; then
  echo "Attract Mode front end detected, installing Pixelcade plug-in for Attract Mode..."
  attractmode=true
  cd $HOME
  if [[ -d "$HOME/pixelcade-attract-mode" ]]; then
    sudo rm -r $HOME/pixelcade-attract-mode
    git clone https://github.com/tnhabib/pixelcade-attract-mode.git
  else
    git clone https://github.com/tnhabib/pixelcade-attract-mode.git
  fi
  sudo cp -r $HOME/pixelcade-attract-mode/Pixelcade $HOME/.attract/plugins
else
  attractmode=false
  echo "${yellow}Attract Mode front end is not installed..."
fi

if [ "$retropie" = true ] ; then
  # let's check if autostart.sh already has pixelcade added and if so, we don't want to add it twice
  #cd /opt/retropie/configs/all/
  if cat /opt/retropie/configs/all/autostart.sh | grep -q 'pixelcade'; then
    echo "${yellow}Pixelcade already added to autostart.sh, skipping...${white}"
  else
    echo "${yellow}Adding Pixelcade /opt/retropie/configs/all/autostart.sh...${white}"
    sudo sed -i '/^emulationstation.*/i cd $HOME/pixelcade && java -jar pixelweb.jar -b &' /opt/retropie/configs/all/autostart.sh #insert this line before emulationstation #auto
    sudo sed -i '/^emulationstation.*/i sleep 10 && cd $HOME/pixelcade/system && ./pixelcade-startup.sh' /opt/retropie/configs/all/autostart.sh #insert this line before emulationstation #auto
    if [ "$attractmode" = true ] ; then
        echo "${yellow}Adding Pixelcade for Attract Mode to /opt/retropie/configs/all/autostart.sh...${white}"
        sudo sed -i '/^attract.*/i cd $HOME/pixelcade && java -jar pixelweb.jar -b &' /opt/retropie/configs/all/autostart.sh #insert this line before attract #auto
        sudo sed -i '/^attract.*/i sleep 10 && cd $HOME/pixelcade/system && ./pixelcade-startup.sh' /opt/retropie/configs/all/autostart.sh #insert this line before attract #auto
    fi
  fi
fi

#!/bin/bash
today=$(date +%Y-%m-%d) #we'll add the date in the git commit
black=`tput setaf 0`
red=`tput setaf 1`
green=`tput setaf 2`
yellow=`tput setaf 3`
blue=`tput setaf 4`
magenta=`tput setaf 5`
white=`tput setaf 7`
reset=`tput sgr0`
cat << "EOF"
       _          _               _
 _ __ (_)_  _____| | ___ __ _  __| | ___
| '_ \| \ \/ / _ \ |/ __/ _` |/ _` |/ _ \
| |_) | |>  <  __/ | (_| (_| | (_| |  __/
| .__/|_/_/\_\___|_|\___\__,_|\__,_|\___|
|_|
EOF
echo "${magenta}       Pixelcade Updater    ${white}"
echo ""

if [[ -f "$HOME/pixelcade/deletemeafterwificonnect.txt" ]]; then  #then this is one time setup
  curl  "http://localhost:8080/text?t=Using%20your%20phone%20or%20computer,%20connect%20to%20the%20%22Pixelcade%20Setup%22%20WiFi%20and%20then%20navigate%20to%20http%20:%20//%2010%20.%200%20.%200%20.%201%20%20from%20your%20device%20web%20browser%20.%20%20Then%20select%20your%20normal%20WiFi%20network%20and%20password%20.&size=32&loop=10&color=cyan&font=Tall%20Films%20Fine&yoffset=-2"
  sudo rm $HOME/pixelcade/deletemeafterwificonnect.txt #delete that file so next time the error message will be different
else
  echo "${magenta}Checking for Updates...${white}"
  echo "git stash"
  git stash #stash away your local changes
  echo "git pull"
  git pull --no-commit #pull the latest
  if git stash pop | grep -q 'conflict'; then           #put back your local changes and if there is a conflict , let's keep your changes
      sleep 3
      cd $HOME/pixelcade
      echo "${yellow}Auto resolving conflict(s) and keeping your version(s) of the conflicting file(s)...${white}"
      git ls-files --unmerged | perl -n -e'/\t(.*)/ && print "$1\n"' | uniq | xargs -d '\n' -r git checkout --theirs --  #-d '\n' handles the space in the file name
      git ls-files --unmerged | perl -n -e'/\t(.*)/ && print "$1\n"' | uniq | xargs -d '\n' -r git commit -i -m "Pixelcade Update $today"
  else
      echo "${yellow}Update complete with no conflicts${white}"
  fi
  curl  "http://localhost:8080/text?t=Update%20Complete&c=green&size=24&l=1"
  curl  "http://localhost:8080/text?t=Address:%20http://pixelcade.local:8080%20or%20http://$pi_ip:8080&c=green&size=24&l=99999"
fi

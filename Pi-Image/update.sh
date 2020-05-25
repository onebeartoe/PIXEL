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
pi_ip=$(ip addr | grep 'state UP' -A2 | tail -n1 | awk '{print $2}' | cut -f1  -d'/')
curl  "http://localhost:8080/text?t=Update%20Complete&c=green&size=24&l=1"
curl  "http://localhost:8080/text?t=Address:%20http://pixelcade.local:8080%20or%20http://$pi_ip:8080&c=green&size=24&l=99999"

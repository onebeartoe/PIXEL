#!/bin/bash
today=$(date +%Y-%m-%d) #we'll add the date in the git commit
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
  sleep 3 #fails if this is not here
  cd $HOME/pixelcade
  echo "${yellow}Auto resolving conflict(s) and keeping your version(s) of the conflicting file(s)...${white}"
  git ls-files --unmerged | perl -n -e'/\t(.*)/ && print "$1\n"' | uniq | xargs -d '\n' -r git checkout --theirs --  #-d '\n' handles the space in the file name
  git ls-files --unmerged | perl -n -e'/\t(.*)/ && print "$1\n"' | uniq | xargs -d '\n' -r git commit -i -m "Pixelcade Update $today"
else
  echo "${yellow}Update complete with no conflicts${white}"
fi
curl  "http://localhost:8080/text?t=Update%20Complete&c=green&size=24&l=1"
curl  "http://localhost:8080/text?t=Address:%20http://pixelcade.local:8080%20or%20http://$pi_ip:8080&c=green&size=24&l=99999"

#for reference, here is the output of git stash pop if there is a conflict
#warning: Cannot merge binary files: wow_action_max/PopsGhostly.png (Updated upstream vs. Stashed changes)
#Auto-merging wow_action_max/PopsGhostly.png
#CONFLICT (content): Merge conflict in wow_action_max/PopsGhostly.png

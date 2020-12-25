#!/bin/bash
# startup script for a dedicated Pi for Pixelcade, systemd calls this via /etc/systemd/system/pixelcade.service

cd /home/pi/pixelcade && java -jar pixelweb.jar -b &
sleep 10 && cd /home/pi/system/pixelcade && ./pixelcade-startup.sh

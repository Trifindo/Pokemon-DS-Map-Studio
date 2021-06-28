#!/usr/bin/env bash

# check that unzip is present
command -v unzip >/dev/null 2>&1 || {
    echo >&2 "This script requires gunzip!"
    exit 1
}

# check if wget is present
command -v wget >/dev/null 2>&1 || {
    echo >&2 "This script requires wget!"
    exit 1
}

# check if java is present
command -v java >/dev/null 2>&1 || {
    echo >&2 "This script requires java!"
    exit 1
}

# download PDSMS 2.2 to local user applications
cd ~/.local/share/applications/ || exit
wget "https://github.com/Trifindo/Pokemon-DS-Map-Studio/releases/download/2.2/Pokemon.DS.Map.Studio-2.2.zip"
unzip "Pokemon.DS.Map.Studio-2.2.zip"
# remove the downloaded archive
rm -f "Pokemon.DS.Map.Studio-2.2.zip"

# download icon
wget "https://github.com/Trifindo/Pokemon-DS-Map-Studio/raw/master/src/main/resources/icons/programIconHD.png"
mv "programIconHD.png" "Pokemon DS Map Studio-2.2/icon.png"

# create a desktop shortcut
echo "#!/usr/bin/env xdg-open
[Desktop Entry]
Type=Application
Name=Pokemon DS Map Studio
Exec=/usr/bin/java -jar \"${PWD}/Pokemon DS Map Studio-2.2/lib/Pokemon DS Map Studio-2.2.jar\"
Icon=${PWD}/Pokemon DS Map Studio-2.2/icon.png
Categories=Development;
" > PDSMS.desktop

echo "Pokemon DS Map Studio has been installed! Enjoy ;)"

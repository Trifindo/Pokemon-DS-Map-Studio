# Pokemon DS Map Studio
![Java CI with Gradle](https://github.com/Trifindo/Pokemon-DS-Map-Studio/workflows/Java%20CI%20with%20Gradle/badge.svg?branch=master)

Pokemon DS Map Studio is a tool for creating NDS Pokémon maps, designed to be used alongside SDSME.

It doesn't require 3D modeling knowledge. Rather, it provides a tilemap-like interface that is automatically converted to a 3D model. Please note that this tool **DOES NOT** allow modification of maps from official games.

![Screenshot of PDSMS](PDSMS_2_1_1.png)

### WARNING !
Saving your old maps with this new version doesn't guarantee backwards compatibility with other PDSMS versions, 
since this release adds a new "exportgroup" keyword to PDSMAP files.


### Supported games:
- Pokemon Diamond/Pearl
- Pokemon Platinum
- Pokemon Heart Gold/Soul Silver
- Pokemon Black/White
- Pokemon Black 2/ White 2

## Running
Pokemon DS Map Studio has been tested under Windows, Linux and MacOS.
In order to run it, Java 8 must be installed on your computer, regardless of the operating system you are using. 
Pokemon DS Map Studio can be executed by double clicking the "PokemonDsMapStudio.jar" file. 

If it doesn't open, try typing the following command in a terminal:
```shell
java -jar PokemonDSMapStudio.jar
```
and look at the output.
If you don't fully understand that, please open an issue in the appropriate section of this repository.

On Linux, the installation can be done directly in an automated way, just open a terminal and type:
```shell
sh -c "$(wget -O- https://raw.githubusercontent.com/Trifindo/Pokemon-DS-Map-Studio/master/pdsms-linux.sh)"
```

## Notes
If you wish to export `.nsbmd` files, `g3dcvtr.exe` and `xerces-c_2_5_0.dll` must be placed into the `bin/converter` folder.

# Pokemon DS Map Studio
Pokemon DS Map Studio is a tool for creating gen 4 and gen 5 Pokémon games' maps, designed to be used alongside SDSME.

It doesn't require 3D modeling, instead it provides a tilemap-like interface that is automatically converted in a 3D model.
Note that this tool **DOES NOT** import maps from the original games, neither it can modify them.

![Screenshot of PDSMS](https://i.imgur.com/8GNJD8V.png)

### Supported games:
- Pokemon Diamond/Pearl
- Pokemon Platinum
- Pokemon Heart Gold/Soul Silver
### Not completely working
- Pokemon Black/White
- Pokemon Black 2/ White 2

## Running
For running Pokemon DS Map Studio, Java 8 must be installed in the computer.
Pokemon DS Map Studio can be executed by double clicking the "PokemonDsMapStudio.jar" file. 
The program is tested under Windows, macOS and Linux.

If it doesn't open, try to open by opening a terminal and typing
```shell
java -jar PokemonDSMapStudio.jar
```
and look at the output.
If you can't understand that, please open an issue in the appropriate section of this repository.

## Notes
For exporting `.nsbmd` files, `g3dcvtr.exe` and `xerces-c_2_5_0.dll` must be placed int the `binconverter` folder.

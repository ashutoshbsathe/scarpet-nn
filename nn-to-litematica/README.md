# nn-to-litematica

Convert _binarized_ neural network weights into [litematica](https://www.curseforge.com/minecraft/mc-mods/litematica) schematic. The generated litematica schematic can be used to build the neural network in Minecraft. The conversion is done per-layer basis meaning a different schematic is created for different layers.

## Requirements
[nbtlib](https://pypi.org/project/nbtlib/) (v1.6.5 used right now), python3, pytorch0.4.0+

## Usage
Run the [`modeltolitematica.py`](modeltolitematica.py) file. The global variables defined in the file can be changed to change configuration of the schematic generator. [TODO: Allow command line arguments to change the configuration]
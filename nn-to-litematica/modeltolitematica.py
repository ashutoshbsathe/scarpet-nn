from nbtlib import parse_nbt, File
import torch
import numpy as np 
import os 

global_save_dict = './litematica_schematics/'
weights_path = './best_model_epoch_173_acc_99.24.pt'
serialized_tag = """
{{
    "": {{
        MinecraftDataVersion: 2230,
        Version: 5,
        Metadata: {{
            TimeCreated: 1587289909511L,
            TimeModified: 1587289909511L,
            EnclosingSize: {{
                x: {enc_x},
                y: {enc_y},
                z: {enc_z}
            }},
            Description: "",
            RegionCount: {reg_count},
            TotalBlocks: {total_blocks},
            Author: "{author}",
            TotalVolume: {total_volume},
            Name: "{metadata_name}"
        }},
        Regions: {{
            {metadata_name}: {{
                BlockStates: {block_states},
                PendingBlockTicks: [],
                Position: {{
                    x: {pos_x},
                    y: {pos_y},
                    z: {pos_z}
                }},
                BlockStatePalette: [
                    {{
                        Name: "minecraft:air"
                    }},
                    {{
                        Name: "minecraft:purple_stained_glass"
                    }},
                    {{
                        Name: "minecraft:lime_stained_glass"
                    }},
                    {{
                        Name: "minecraft:smooth_stone"
                    }}
                ],
                Size: {{
                    x: {size_x},
                    y: {size_y},
                    z: {size_z}
                }},
                PendingFluidTicks: [],
                TileEntities: [],
                Entities: []
            }}
        }}
    }}
}}
"""
def string2signedint(bits):
    num = int(bits, 2)
    if bits[0] == '1': # signed integer
        num ^= 0xFFFFFFFFFFFFFFFF
        num += 1
        num = -num 
    return num 

def singlelayer2litematic(weights, layer_name='sample_layer', verbose=False):
    if isinstance(weights, torch.Tensor):
        weights = weights.cpu().numpy()
    if len(weights.shape) == 4:  # conv layers
        c2, c1, h, w = weights.shape
        weights = np.transpose(weights.reshape(c2, c1, h*w)) # stretch conv filter vertically
    elif len(weights.shape) == 2: # fully connected layer
        c2, c1 = weights.shape 
        weights = np.transpose(weights.reshape(c2, c1, 1))
    else:
        raise ValueError('Invalid number of dimensions for the weights passed. Expects either 2 or 4 dimensions.')
    sizeY, sizeZ, sizeX = weights.shape 
    sizeLayer = sizeZ * sizeX

    bits = ['' for _ in range(sizeX * sizeY * sizeZ)]

    zero = '00'
    pos = '01'
    neg = '10'

    total_blocks = 0
    for y in range(sizeY):
        for z in range(sizeZ):
            for x in range(sizeX):
                index = y * sizeLayer + z * sizeX + x 
                if weights[y][z][x] == 0:
                    bits[index] = zero
                elif weights[y][z][x] > 0:
                    bits[index] = pos 
                    total_blocks += 1 
                else:
                    bits[index] = neg
                    total_blocks += 1
    block_states = '[L; '
    for i in range(0, len(bits), 32):
        bit_string = ''
        for j in range(32):
            if (i+j) >= len(bits):
                bit_string = (32 - j) * '00' + bit_string
                assert len(bit_string) == 64
                break 
            else:
                bit_string = bits[i+j] + bit_string
        if verbose:
            print(num.bit_length(), num)
            print(len(bit_string), bit_string)
            print(64 * '-')
        num = string2signedint(bit_string)
        assert num.bit_length() <= len(bit_string) - 1
        block_states += str(num) + 'L, '
    block_states = block_states[:-2] + ']'
    litematica_nbt_data = serialized_tag.format(
        enc_x=sizeX, enc_y=sizeY, enc_z=sizeZ, author='ashutoshbsathe',
        total_volume=sizeX*sizeY*sizeZ, total_blocks=total_blocks,
        pos_x=0, pos_y=0, pos_z=0, metadata_name=layer_name,
        size_x=sizeX, size_y=sizeY, size_z=sizeZ,
        reg_count=1, block_states=block_states
    )
    if verbose:
        print(litematica_nbt_data)

    data = parse_nbt(litematica_nbt_data)
    save_path = global_save_dict + '/' + layer_name + '.litematic'
    File(data).save(save_path, gzipped=True)
    print('Scheamtica generated at : {}'.format(save_path))

if __name__ == '__main__':
    state_dict = torch.load(weights_path)
    os.makedirs(global_save_dict, exist_ok=True)
    for k, v in state_dict.items():
        singlelayer2litematic(v, k)
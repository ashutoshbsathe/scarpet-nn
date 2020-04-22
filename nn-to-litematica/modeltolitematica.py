from nbtlib import parse_nbt, File
import torch
import numpy as np 
import os 
from litematica_nbt import serialized_tag 
import time 

global_save_dict = './litematica_schematics/'
weights_path = '../nn-training/pretrained/best_model_epoch_173_acc_99.24.pt'

author = 'ashutoshbsathe'
global_nn_name = '2classmnistexample'
pos_block = 'minecraft:purple_stained_glass'
neg_block = 'minecraft:lime_stained_glass'

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

    mod_time = int(time.time())

    litematica_nbt_data = serialized_tag.format(
        enc_x=sizeX, enc_y=sizeY, enc_z=sizeZ, author=author, time=mod_time,
        total_volume=sizeX*sizeY*sizeZ, total_blocks=total_blocks,
        pos_x=0, pos_y=0, pos_z=0, metadata_name=layer_name,
        size_x=sizeX, size_y=sizeY, size_z=sizeZ, pos_block=pos_block,
        neg_block=neg_block, block_states=block_states
    )
    if verbose:
        print(litematica_nbt_data)

    data = parse_nbt(litematica_nbt_data)
    file_name = global_nn_name + '.' + layer_name
    save_path = global_save_dict + '/' + file_name + '.litematic'
    File(data).save(save_path, gzipped=True)
    print('Litematica scheamtic generated at : {}'.format(save_path))

if __name__ == '__main__':
    state_dict = torch.load(weights_path)
    os.makedirs(global_save_dict, exist_ok=True)
    for k, v in state_dict.items():
        singlelayer2litematic(v, k)
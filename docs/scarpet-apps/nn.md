---
layout: default
title: nn
nav_order: 1
parent: Scarpet Apps
---

# nn
{: .no_toc }

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}

---

[\[View source on GitHub\]](https://github.com/ashutoshbsathe/scarpet-nn/blob/master/scarpet-apps/nn.sc)

"nn" app contains implementation of neural network layers. Depends on [nn-utils](nn-utils.md) for helper functions. At the time of writing, scarpet 1.6 does not have proper support for exception raising and exception handling. Therefore, any error or invalid operation will cause the program to exit abruptly.

## Functions
### `conv`
**Arguments**
1. `i_pos1` - Starting position of input tensor 
2. `i_pos2` - Ending position of input tensor
3. `w_pos1` - Starting position of weight tensor
4. `w_pos2` - Ending position of weight tensor
5. `a_pos1` - Starting position of output activation tensor
6. `a_pos2` - Ending position of output activation tensor
7. `kernel_size` - Kernel size of convolution filter (supports only square filters for now)
8. `stride` - Stride for convolution operation
9. `game_tick_time` - Time (in ms) to delay between 2 consecutive block placements. Pass `0` for instant answer. Passing any non zero value allows better visualizations.


**Behavior**

This function operates similar to [`torch.nn.Conv2d()`](https://pytorch.org/docs/stable/nn.html#conv2d) layer by applying 2D convolution over input signal composed of several input channels.

The calculation of input channels, number of convolution filters and output channels is decided by block position arguments (`i_pos1`, `i_pos2` for input and so on). Any invalid operation will cause the program to exit with proper error message indicating cause of error.

The function will read tensor block by block from bounding box specified by (`i_pos1`, `i_pos2`), apply convolution operation based on filters stored at bounding box specified by (`w_pos1`, `w_pos2`) and put the output tensor block by block at bounding box specified by (`a_pos1`, `a_pos2`).

The function will always consider `*_pos1` as the starting point and `*_pos2` as ending point of the bounding box and calculate proper directions required for looping. 

---


### `fc`
**Arguments**
1. `i_pos1` - Starting position of input tensor 
2. `i_pos2` - Ending position of input tensor
3. `w_pos1` - Starting position of weight tensor
4. `w_pos2` - Ending position of weight tensor
5. `a_pos1` - Starting position of output activation tensor
6. `a_pos2` - Ending position of output activation tensor
7. `game_tick_time` - Time (in ms) to delay between 2 consecutive block placements. Pass `0` for instant answer. Passing any non zero value allows better visualizations.

**Behavior**

This function behaves similar to [`torch.nn.Linear()`](https://pytorch.org/docs/stable/nn.html#linear) layer.

The calculation of dimensions of output tensor will be done by the function and the function will exit with proper error message in case of invalid operation. 

Internally, it's a simple matrix multiplication. The function will read tensor block by block from bounding box specified by (`i_pos1`, `i_pos2`), multiply it with weight matrix stored at bounding box specified by (`w_pos1`, `w_pos2`) and put the output tensor block by block at bounding box specified by (`a_pos1`, `a_pos2`).

---

### `move`
**Arguments**
1. `i_pos1` - Starting position of input tensor 
2. `i_pos2` - Ending position of input tensor
3. `o_pos1` - Starting position of output tensor
4. `o_pos2` - Ending position of output tensor
5. `game_tick_time` - Time (in ms) to delay between 2 consecutive block placements. Pass `0` for instant answer. Passing any non zero value allows better visualizations.

**Behavior**

This function behaves similar to [`torch.Tensor.view()`](https://pytorch.org/docs/stable/tensors.html#torch.Tensor.view) operation.

The function will move the blocks from the volume specified by (`i_pos1` and `i_pos2`) to the volume specified by (`o_pos1`, `o_pos2`). The data values will not change. 

Any error will be notified by the function before exiting due to some invalid operation.
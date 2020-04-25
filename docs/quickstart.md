---
layout: default
title: Quick Start Guide
nav_order: 2
---

# Quick Start Guide
{: .no_toc }

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}

---


`scarpet-nn` can be theoretically used to build any neural network architecture involving convolution and fully connected layers. _As explained in the introduction, the framework assumes that the weights, activations and inputs to this neural network are binary_ (i.e. they are either +1 or -1 as explained in ["Binarized Neural Networks"](https://arxiv.org/abs/1602.02830) paper). This guide assumes familiarity with [pytorch](https://github.com/pytorch/pytorch), [litematica](https://github.com/maruohon/litematica) and [carpetmod](https://github.com/gnembon/fabric-carpet).

## Install dependencies
Make sure following things are installed:

1. Minecraft Java Edition
2. Mods: [Litematica](https://github.com/maruohon/litematica) and [carpetmod](https://github.com/gnembon/fabric-carpet)
3. Python3 with [pytorch](https://github.com/pytorch/pytorch) and [nbtlib](https://github.com/vberlier/nbtlib)
4. A text editor


## Preparing "binarized" neural network
As a first step, you should train your neural network as explained in [BNN paper](https://arxiv.org/abs/1602.02830). If you are already familiar with training neural networks in pytorch, you can simply use [`binarized_modules.py`](https://github.com/ashutoshbsathe/scarpet-nn/blob/master/nn-training/binarized_modules.py) for implementation of binarized layers. Sample code for training such a neural network is available [here](https://github.com/ashutoshbsathe/scarpet-nn/blob/master/nn-training/).

To get optimal results, use deterministic binarization instead of stochastic binarization. (Sec 1.1 of [paper](https://arxiv.org/abs/1602.02830) explains difference between these 2 methods). An important thing to remember when training such networks is to properly normalize the input to the neural network so that it can be (deterministically) binarized using [`torch.sign()`](https://pytorch.org/docs/stable/torch.html#torch.sign) directly. 

Moreover, at the moment, the framework does not support `bias` parameters in layers of neural network. So please pass `bias=False` when building your neural network.

At the end of this step, you should have model weights saved in a file.

## Converting neural network into litematica schematics
[Litematica](https://github.com/maruohon/litematica) is a client-side Minecraft mod that allows creating schematics of Minecraft builds. We use litematica to generate schematics of our neural network weights. These schematics can be referred in game for placing blocks. 

Run [`modeltolitematica.py`](https://github.com/ashutoshbsathe/scarpet-nn/blob/master/nn-to-litematica/modeltolitematica.py) for converting all layers of your neural network into different litematica schematics. Each schematic contains only 1 layer of the neural network. You can configure what block represents +1 (by default 'minecraft:purple_stained_glass') and what block represents -1 (by default 'minecraft:lime_stained_glass').

At the end of this step, you should have a set of litematica schematics (`.litematic` files) ready with you. Make sure that all of the layers in neural network have a corresponding `.litematic` file. If you are using the same neural network from sample code you should have files named `conv1.weight.litematica`, `conv2.weight.litematica`, `conv3.weight.litematica`, `fc1.weight.litematica`, `fc2.weight.litematica`.

## Putting it all together in a Minecraft world
Your neural network will need space for input layer, all the layers and the intermediate activations. So go to sufficiently empty space in your world or simply create a new superflat world for experimenting. Refer to the [world download of demo](https://drive.google.com/open?id=13lw4Ct5H-vgh2ajpMc7Xw2NHdzymXeEa) to know more about aligning layers in Minecraft world.

After the alignment of layers and the activations is complete, you should write a scarpet app corresponding to your neural network architecture and placement. Refer to [`twoclassmnist`](https://github.com/ashutoshbsathe/scarpet-nn/blob/master/scarpet-apps/twoclassmnist.sc) to see how coordinates from Minecraft world are translated into scarpet code. Ideally, you should be writing at least `forward()` function. Your neural network implementation app should import the necessary layers from [`nn`](https://github.com/ashutoshbsathe/scarpet-nn/blob/master/scarpet-apps/nn.sc) app and provide necessary coordinates to it.

Finally, to run your neural network, load your scarpet app containing your neural network implementation (i.e. your `forward()`) and setup a command block to invoke `forward()` function from your app.
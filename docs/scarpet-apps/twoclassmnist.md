---
layout: default
title: twoclassmnist
nav_order: 3
parent: Scarpet Apps
---

# twoclassmnist
{: .no_toc }

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}

---

[\[View source on GitHub\]](https://github.com/ashutoshbsathe/scarpet-nn/blob/master/scarpet-apps/twoclassmnist.sc)

Demo neural network app implemented using [nn](nn.md) and [nn-utils](nn-utils.md). Ideally, every neural network should be implemented in separate apps like this and should have at least 1 `forward()` function.

## Functions
### `forward`
This `forward()` implements the neural network using functions from [nn](nn.md) app. To run the neural network, you can use `/script in twoclassmnist invoke forward` or simply `/twoclassmnist forward` in any Minecraft command block. 

---

### `clear_activations`
This function clears the intermediate activations created during last forward pass. Although not technically compulsory function, it is nice to have this function in case you want to visualize the neural network block by block after every forward pass.
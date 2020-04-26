---
layout: default
title: Litematica Schematic Generation
nav_order: 4
---

# Litematica Schematic Generation
{: .no_toc }

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}

---


## General representation standard
[nn-to-litematica](https://github.com/ashutoshbsathe/scarpet-nn/blob/master/nn-to-litematica) module converts the binary weights from the pytorch checkpoint into litematica schematics. Every litematica is arranged so that the first 2 dimensions lie in (x, z) plane. Moreover, the loading point of schematic (the block where player is standing when loading schematic) always represents the very first number in weights (i.e. index [0, 0, 0] in weights array). The first dimension of weight array is from loading point towards positive X axis. The second dimension of weight array is from loading point towards positive Z axis. Any dimensions more than 3 are reshaped into third axis (Y axis).

## Representation of `conv` layers
We follow the general representation rules as stated above. Here, since `conv` layers are stored as a 4 dimensional array, we need to squeeze extra dimension to make it 3 dimensional. Typical shape of `conv` layer is $$[c_2, c_1, f_h, f_w]$$. 
Here,
* $$c_2$$ = Number of channels output activations should have
* $$c_1$$ = Number of channels input activations should have
* $$(f_h, f_w)$$ = Height and width of convolution filter

To convert this into 3 dimensional representation, `scarpet-nn` squeezes $$(f_h, f_w)$$ into a single dimension of size $$f_h \times f_w$$. Therefore, the new shape of `conv` layer for schematic generation would be $$[c_2, c_1, f_h \times f_w]$$.

## Representation of `fc` layers
`fc` layers are also inline to general representation standard defined by `scarpet-nn`. Typical shape of `fc` layer is $$[n, k]$$. `fc` layer takes in input of shape $$[m, n]$$ and produces output of shape $$[m, k]$$. Since all the operations are 2 dimensional, we don't need any dimension adjustments here.
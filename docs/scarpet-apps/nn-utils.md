---
layout: default
title: nn-utils
nav_order: 1
parent: Scarpet Apps
---

## nn-utils
{: .no_toc }

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}

---

[\[View source on GitHub\]](https://github.com/ashutoshbsathe/scarpet-nn/blob/master/scarpet-apps/nn-utils.sc)

"nn-utils" holds the helper functions for the main "nn" app.

## Global variables

### `global_weight_high`
Minecraft block representing positive weight (i.e. +1), default: `minecraft:purple_stained_glass`
### `global_weight_low` 
Minecraft block representing negative weight (i.e. -1), default: `minecraft:lime_stained_glass`
### `global_activations_high`
Minecraft block representing positive activation (i.e. +1), default: `minecraft:white_concrete`
### `global_activations_low`
Minecraft block representing negative activation (i.e. -1), default: `minecraft:black_concrete`

## Functions
### `set_block_weight_high`
Change Minecraft block (global variable) corresponding to positive weight. Takes in block name `b` as argument.
### `set_block_weight_low`
Change Minecraft block (global variable) corresponding to negative weight. Takes in block name `b` as argument.
### `set_block_activation_high`
Change Minecraft block (global variable) corresponding to positive activation. Takes in block name `b` as argument.
### `set_block_activation_low`
Change Minecraft block (global variable) corresponding to negative activation. Takes in block name `b` as argument.
### `is_high`
Takes `block_pos` as argument and returns if block at `block_pos` is either high weight or high activation or not. Note that `block_pos` is a list of 3 coordinates $$(x, y, z)$$.
### `is_low`
Takes `block_pos` as argument and returns if block at `block_pos` is either low weight or low activation or not. Note that `block_pos` is a list of 3 coordinates $$(x, y, z)$$.
### `list_abs`
Takes a `list_input` argument. Returns $$abs(x)$$ for $x$ in `list_input` as a list. 
### `list_str`
Takes an input list (`list_input`) and returns the string representation of the list. For example, 
```
list_input = l(1, 2, 3, 4);
list_str(list_input) => '[ 1 2 3 4 ]'
```
### `sign`
Take an integer input `x` and returns sign of `x`. `0` is considered positive by this function. Note that this is different from [signum function $$sgn(x)$$](https://en.wikipedia.org/wiki/Sign_function) which returns `0` if `x = 0`. 
### `list_sign`
Takes a list as input and returns the list corresponding to sign of individual element. For example, 
```
list_input = l(-1, 0, 1, 2, -3);
list_sign(list_input) => l(-1, 1, 1, 1, -1)
```
### `concatenate_lists`
Takes 2 list inputs (`list1` and `list2`) and returns the list formed by concatenating `list2` at the end of `list1`. For example,
```
list1 = l(1, 2, 3);
list2 = l(4, 5, 6);
concatenate_lists(list1, list2) => l(1, 2, 3, 4, 5, 6)
```
### `xnor`
Takes to integer inputs `bit1` and `bit2` and returns [exclusive NOR](https://en.wikipedia.org/wiki/XNOR_gate) of `bit1` and `bit2`. For example,
```
bit1 = 0; bit2 = 1;
xnor(bit1, bit2) => 0
```
### `multiply_vectors`
Takes in 2 vectors (scarpet lists) `vec_a` and `vec_b` containing bit representation of signs of weights and returns the list output consisting of bit representation of multiplication of every element. In short this multiplication is element wise XNOR of 2 vectors.
### `reduce_vector`
Takes in vector `vec` as input and returns the sign of sum of all the numbers in `vec` based on bitcounting. For example,
```
vec = l(1, 1, 0, 1, 0, 0, 0);
reduce_vector(vec) => 0
vec = l(1, 1, 1, 0, 0, 0);
reduce_vector(vec) => 1
```

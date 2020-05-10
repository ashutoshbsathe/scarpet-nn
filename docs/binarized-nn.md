---
layout: default
title: Binarized Neural Networks
nav_order: 3
---

# Binarized Neural Networks

Neural networks such as [BERT](https://github.com/google-research/bert) have millions of parameters and need expensive GPUs to use them.

<center>
<img src="../images/expensive_bert.png" alt="BERT expensive">
</center>

There have been quite some efforts to make neural networks computationally inexpensive in past few years. One of the ideas is to use less bits for representing weights and activations. A vanilla neural network uses 32 bit floating point numbers to store each and every weight and activations in memory. This means if your network has large number of parameters, you will quickly run out of memory.

## Binarized representations of neural networks
To conquer large memory requirements, "model quantization" techniques limit the number of bits used by the weights and activations in a neural network. For example, if the neural network uses only 16 bits to store a number (instead of 32), we get straight up 50% improvement in memory usage. Moreover, since the number of bits got reduced, CPU design required to operate on these numbers also got easier. ["Binarized Neural Networks"](https://arxiv.org/abs/1602.02830) is a type of model quantization techniques where every number in weight _as well as_ activations is stored in a _single bit_.

BNNs conceptually store their weights and activations as either +1 or -1. However, the bit representation is different. We store bit `1` for +1 and bit `0` for -1. 

## Binarized operations
Neural networks typically need only vector reduction and multiplication operations for inference. Since all the numbers are represented by single bit, the operations become a lot easier. 

### Binarized Multiplication
Following table describes the multiplication of 2 weights or activations when binarized to +1 or -1.

| $$A$$  | $$B$$  | $$C = A * B$$ |
|:--:|:--:|:---------:|
| -1 | -1 | 1         |
| -1 | 1  | -1        |
| 1  | -1 |  -1       |
| 1  | 1  | 1         |

In bit representation, this multiplication resembles XNOR gate.

| $$A$$  | $$B$$  | $$C = A * B$$ | $$C = \overline{A \oplus B}$$ |
|:--:|:--:|:---------:|:-:|
| 0 | 0 | 1         | 1 |
| 0 | 1  | 0        | 0 |
| 1  | 0 |  0       | 0 |
| 1  | 1  | 1         | 1 |

This means that, in BNNs multiplication operation reduces to simple XNOR as opposed to [complicated floating point multiplication algorithm](https://en.wikipedia.org/wiki/Floating-point_arithmetic#Multiplication_and_division).

### Binarized Reduce 
Reduce operation typically refers to adding up all elements of a tensor along some specific direction. In case of BNNs, since all the operands are either +1 or -1, we only need sign of the answer as the output of reduction operation. In bit representation, this translates to bitcounting operation. Therefore, to reduce a vector of length $$l$$ of 1s and 0s, we only need to count number of 1s in the vector in some variable say `bitcount`. Then, output of reduction operation is bit `1` (+1) if $$(bitcount \geq l/2)$$ and bit `0`(-1) otherwise.

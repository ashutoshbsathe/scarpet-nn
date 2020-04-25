---
layout: default
title: Home
nav_order: 1
---

# scarpet-nn
Tools and libraries to run neural networks in Minecraft :pick:

## Introduction
`scarpet-nn` is a set of tools that allows players to run [binarized neural networks (BNNs)](https://arxiv.org/abs/1602.02830) in Minecraft. BNNs are a set of neural networks where the weights and the activations of the neural networks are constrained to either +1 or -1. This allows every component of the weights and activation to be represented by single binary bit. The primary motivation behind BNNs is to reduce memory and computational requirements to run neural networks. Because of these reduced requirements, we can now run neural networks in Minecraft. 
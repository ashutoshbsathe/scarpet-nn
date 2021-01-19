---
layout: default
title: Home
nav_order: 1
---

# scarpet-nn
Tools and libraries to run neural networks in Minecraft :pick:

[View Demo Video](#demo){: .btn .btn-green } 
[Demo Minecraft World Download](https://drive.google.com/open?id=13lw4Ct5H-vgh2ajpMc7Xw2NHdzymXeEa){: .btn .btn-outline }

## Primary features
1. Supports reconfiguring neural networks on the fly
2. Supports both convolution and fully connected layers
3. Allows running multiple neural networks in single world
4. Allows block-by-block visualizations of intermediate activation calculations
5. Drawingboard -- Lets users draw on a black concrete wall by right clicking with sword

## Introduction
`scarpet-nn` is a set of tools that allows players to run [binarized neural networks (BNNs)](https://arxiv.org/abs/1602.02830) in Minecraft. BNNs are a set of neural networks where the weights and the activations of the neural networks are constrained to either +1 or -1. This allows every component of the weights and activation to be represented by single binary bit. The primary motivation behind BNNs is to reduce memory and computational requirements to run neural networks. Because of these reduced requirements, we can now run neural networks in Minecraft. 

`scarpet-nn` is different from [previous implementations](https://www.reddit.com/r/Minecraft/comments/ak22ur/neural_network_for_handwritten_digit_recognition/) of neural networks in Minecraft. `scarpet-nn` API allows running any architecture of binary neural networks in Minecraft. This means, anyone can train their own BNN and use `scarpet-nn` to run it in Minecraft. You can know more about BNNs in the [Binarized Neural Networks](binarized-nn.md) section of this documentation.

## Demo 
Working classifier demo

<div class="yt-iframe-container">
<iframe src="https://www.youtube.com/embed/LVmOcAYbYdU" 
frameborder="0" allowfullscreen class="yt-iframe-video"></iframe>
</div>

<hr/>

Block by block visualization
<div class="yt-iframe-container">
<iframe src="https://www.youtube.com/embed/KEcUKpBTk8M" 
frameborder="0" allowfullscreen class="yt-iframe-video"></iframe>
</div>

<sup> This project was featured by <a href="https://twitter.com/hardmaru/status/1256148074709172225?lang=en">@hardmaru</a> on Twitter and by <a href="https://www.programmersought.com/article/34804115029/">this</a> blog</sup>
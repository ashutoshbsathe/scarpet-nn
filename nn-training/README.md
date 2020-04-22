# PyTorch Binarized Neural Network Example

This folder contains code for training a ["binarized neural network"](https://arxiv.org/abs/1602.02830) in PyTorch. The code builds on top of [original implementation](https://github.com/itayhubara/BinaryNet.pytorch). Using this code, you will be able to train a _Binarized ConvNet_ that can identify 2 classes from [MNIST dataset](http://yann.lecun.com/exdb/mnist/) with 99%+ accuracy.

File [`mnist2classdataloader.py`](mnist2classdataloader.py) implements a new [`Dataset`](https://pytorch.org/docs/stable/data.html#torch.utils.data.Dataset) that holds only 2 classes from MNIST (digit `0` and digit `1` specifically). It is important to normalize the resulting dataset using correct mean and standard deviation. Do note that mean-std normalization is specially important for binarized NNs because the input is binarized based on the sign of input (so either `+1` or `-1`). The images are also resized from the normal `28x28` size to `16x16` to make it easier to build and visualize in Minecraft. 

Currently, there is no way to neatly implement _both_ bias and weight of the neural network in Minecraft. Therefore, please use `bias=False` when training the neural network for all layers.

## Requirements
python3, pytorch 0.4.0+, torchvision 0.2.1+

## Usage
Run the [`main_mnist2class.py`](main_mnist2class.py) using python. Training hyperparameters can be modified by passing respective option. Refer to [`main_mnist2class.py`](main_mnist2class.py) for list of all tweakable hyperparameters. To run the code with recommended hyperparameters, simply run [`train.bat`](train.bat) in Powershell on Windows 10 or run [`train.sh`](train.sh) on Linux/Mac.

Pretrained model with 99.24% accuracy on this custom dataset is also [available](pretrained/best_model_epoch_173_acc_99.24.pt).
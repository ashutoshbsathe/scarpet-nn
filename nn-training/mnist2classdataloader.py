import torch
import torchvision.datasets as datasets 
import torchvision.transforms as transforms 
from tqdm import tqdm
import numpy as np 
import os
def get_numpy_2class(class_0=0, class_1=1):
    train_loader = torch.utils.data.DataLoader(
        datasets.MNIST('../data', train=True, download=True,
                    transform=transforms.Compose([
                        transforms.Resize((16, 16)),
                        transforms.ToTensor(),
                    ])),
        batch_size=1, shuffle=True)
    test_loader = torch.utils.data.DataLoader(
        datasets.MNIST('../data', train=False, transform=transforms.Compose([
                        transforms.Resize((16, 16)),
                        transforms.ToTensor(),
                    ])),
        batch_size=1, shuffle=True)
    train_class_0_images = []
    train_class_1_images = []
    for _, (image, label) in tqdm(enumerate(train_loader)):
        if label.item() == class_0:
            train_class_0_images.append(image.cpu().numpy())
        elif label.item() == class_1:
            train_class_1_images.append(image.cpu().numpy())
    train_class_0_images = np.asarray(train_class_0_images, dtype=np.float32).reshape(-1, 1, 16, 16)
    train_class_1_images = np.asarray(train_class_1_images, dtype=np.float32).reshape(-1, 1, 16, 16)
    print(train_class_0_images.shape, train_class_1_images.shape)
    np.save('./train_class_0.npy', train_class_0_images)
    np.save('./train_class_1.npy', train_class_1_images)
    test_class_0_images = []
    test_class_1_images = []
    for _, (image, label) in tqdm(enumerate(test_loader)):
        if label.item() == class_0:
            test_class_0_images.append(image.cpu().numpy())
        elif label.item() == class_1:
            test_class_1_images.append(image.cpu().numpy())
    test_class_0_images = np.asarray(test_class_0_images, dtype=np.float32).reshape(-1, 1, 16, 16)
    test_class_1_images = np.asarray(test_class_1_images, dtype=np.float32).reshape(-1, 1, 16, 16)
    print(test_class_0_images.shape, test_class_1_images.shape)
    np.save('./test_class_0.npy', test_class_0_images)
    np.save('./test_class_1.npy', test_class_1_images)
    print(np.concatenate((train_class_0_images, train_class_1_images)).mean())
    print(np.concatenate((train_class_0_images, train_class_1_images)).std())

class MNIST2ClassDataset(torch.utils.data.Dataset):
    def __init__(self, class_0=0, class_1=1, train=True, transform=None):
        files = ['train_class_{}.npy', 'test_class_{}.npy']
        for f in files:
            if not os.path.isfile(f.format(class_0)) or not os.path.isfile(f.format(class_1)):
                get_numpy_2class()
        if train:
            self.data_0 = np.load(files[0].format(class_0))
            self.data_1 = np.load(files[0].format(class_1))
        else:
            self.data_0 = np.load(files[1].format(class_0))
            self.data_1 = np.load(files[1].format(class_1))
        self.len_0 = len(self.data_0)
        self.len_1 = len(self.data_1)
        self.transform = transform
    def __len__(self):
        return self.len_0 + self.len_1
    
    def __getitem__(self, idx):
        if torch.is_tensor(idx):
            idx = idx.tolist()
        if isinstance(idx, int):
            idx = [idx]
        imgs, labels = [], []
        for i in idx:
            if i >= self.len_0:
                labels.append(1)
                imgs.append(self.data_1[i - self.len_0])
            else:
                labels.append(0)
                imgs.append(self.data_0[i])
        imgs = np.asarray(imgs, dtype=np.float32).reshape(len(idx), 1, 16, 16)
        if len(idx) == 1:
            imgs = imgs.reshape(1, 16, 16)
        imgs = torch.FloatTensor(imgs)
        if self.transform:
            imgs = self.transform(imgs)
        labels = torch.FloatTensor(labels)
        return imgs, labels
        
def get_mnist_2_class_dataloader(batch_size=512):
    train_loader = torch.utils.data.DataLoader(MNIST2ClassDataset(train=True, transform=transforms.Compose([
        #transforms.ToTensor(),
        transforms.Normalize((0.1220,), (0.2606,)),
    ])), batch_size=batch_size, shuffle=True)
    test_loader = torch.utils.data.DataLoader(MNIST2ClassDataset(train=False, transform=transforms.Compose([
        #transforms.ToTensor(),
        transforms.Normalize((0.1220,), (0.2606,)),
    ])), batch_size=batch_size, shuffle=True)
    return train_loader, test_loader

if __name__ == '__main__':
    get_numpy_2class()
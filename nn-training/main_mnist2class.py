
from __future__ import print_function
import argparse
import torch
import torch.nn as nn
import torch.nn.functional as F
import torch.optim as optim
from torchvision import datasets, transforms
from torch.autograd import Variable
from binarized_modules import  BinarizeLinear, BinarizeConv2d
from mnist2classdataloader import get_mnist_2_class_dataloader
import glob
import os

# Training settings
parser = argparse.ArgumentParser(description='PyTorch Binarized Neural Networks Example')
parser.add_argument('--batch-size', type=int, default=64, metavar='N',
                    help='input batch size for training (default: 256)')
parser.add_argument('--test-batch-size', type=int, default=1000, metavar='N',
                    help='input batch size for testing (default: 1000)')
parser.add_argument('--epochs', type=int, default=100, metavar='N',
                    help='number of epochs to train (default: 10)')
parser.add_argument('--lr', type=float, default=0.01, metavar='LR',
                    help='learning rate (default: 0.001)')
parser.add_argument('--momentum', type=float, default=0.5, metavar='M',
                    help='SGD momentum (default: 0.5)')
parser.add_argument('--no-cuda', action='store_true', default=False,
                    help='disables CUDA training')
parser.add_argument('--seed', type=int, default=1, metavar='S',
                    help='random seed (default: 1)')
parser.add_argument('--gpus', default=3,
                    help='gpus used for training - e.g 0,1,3')
parser.add_argument('--log-interval', type=int, default=10, metavar='N',
                    help='how many batches to wait before logging training status')
args = parser.parse_args()
args.cuda = not args.no_cuda and torch.cuda.is_available()

torch.manual_seed(args.seed)
if args.cuda:
    torch.cuda.manual_seed(args.seed)

class Net(nn.Module):
    def __init__(self):
        super(Net, self).__init__()
        self.conv1 = BinarizeConv2d(1, 4, stride=2, kernel_size=3, bias=False)
        self.conv2 = BinarizeConv2d(4, 8, stride=2, kernel_size=3, bias=False)
        self.conv3 = BinarizeConv2d(8, 16, stride=2, kernel_size=3, bias=False)
        self.fc1 = BinarizeLinear(16, 8, bias=False)
        self.fc2 = BinarizeLinear(8, 1, bias=False)
    def forward(self, x):
        out = F.hardtanh(self.conv1(x))
        out = F.hardtanh(self.conv2(out))
        out = F.hardtanh(self.conv3(out))
        out = out.view(-1, 16)
        out = F.hardtanh(self.fc1(out))
        out = self.fc2(out)
        return out

train_loader, test_loader = get_mnist_2_class_dataloader()
model = Net()
if args.cuda:
    model.cuda()

criterion = nn.BCEWithLogitsLoss()
optimizer = optim.SGD(model.parameters(), lr=args.lr, momentum=0.9)


def train(epoch):
    model.train()
    new_lr = args.lr * (0.2 ** (epoch // 40))
    print('Setting lr = {}'.format(new_lr))
    for param_group in optimizer.param_groups:
        param_group['lr'] = new_lr
    for batch_idx, (data, target) in enumerate(train_loader):
        if args.cuda:
            data, target = data.cuda(), target.cuda()
        data, target = Variable(data), Variable(target)
        optimizer.zero_grad()
        output = model(data)
        loss = criterion(output, target)
        optimizer.zero_grad()
        loss.backward()
        for p in list(model.parameters()):
            if hasattr(p,'org'):
                p.data.copy_(p.org)
        optimizer.step()
        for p in list(model.parameters()):
            if hasattr(p,'org'):
                p.org.copy_(p.data.clamp_(-1,1))

        if batch_idx % args.log_interval == 0:
            print('Train Epoch: {} [{}/{} ({:.0f}%)]\tLoss: {:.6f}'.format(
                epoch, batch_idx * len(data), len(train_loader.dataset),
                100. * batch_idx / len(train_loader), loss.data[0]))

def test():
    model.eval()
    test_loss = 0
    correct = 0
    for data, target in test_loader:
        if args.cuda:
            data, target = data.cuda(), target.cuda()
        output = model(data)
        test_loss += criterion(output, target).data[0] # sum up batch loss
        pred = torch.where(output > 0., torch.tensor(1.).cuda(), torch.tensor(0.).cuda())
        correct += pred.eq(target.data.view_as(pred)).cpu().sum()

    test_loss /= len(test_loader.dataset)
    print('\nTest set: Average loss: {:.4f}, Accuracy: {}/{} ({:.0f}%)\n'.format(
        test_loss, correct, len(test_loader.dataset),
        100. * correct / len(test_loader.dataset)))
    return correct

def main():
    best = 0
    for epoch in range(1, args.epochs + 1):
        train(epoch)
        correct = test()
        if correct >= best:
            file_list = glob.glob('./best_model_small_epoch_*_acc_*.pt')
            torch.save(model.state_dict(), './best_model_small_epoch_{}_acc_{:.2f}.pt'.format(epoch, float(correct*100./len(test_loader.dataset))))
            best = correct 
            for f in file_list:
                os.remove(f)

if __name__ == '__main__':
    main()

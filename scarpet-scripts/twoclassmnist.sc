// `twoclassmnist` scarpet app
// Scarpet app runs a neural network that can classify 2 classes (0 and 1) from MNIST dataset

import('nn', 'conv', 'fc', 'move'); // basic neural network building blocks
import('nn-utils', 'is_high', 'set_block_weight_high', 'set_block_weight_low', 'set_block_activations_high', 'set_block_activations_low');

set_block_weight_high('purple_stained_glass');
set_block_weight_low('lime_stained_glass');

set_block_activations_high('white_concrete');
set_block_activations_low('black_concrete');

clear_activations() -> (

    run(str('/fill %d %d %d %d %d %d air', 11, 8, 9, 5, 2, 12));
    run(str('/fill %d %d %d %d %d %d air', 9, 4, 29, 7, 2, 36));
    run(str('/fill %d %d %d %d %d %d air', 8, 2, 57, 8, 2, 72));
    run(str('/fill %d %d %d %d %d %d air', 16, 2, 75, 1, 2, 75));
    run(str('/fill %d %d %d %d %d %d air', 12, 2, 101, 5, 2, 101));
    set(l(8, 2, 122), 'air');

);

forward() -> (

    conv(l(16, 16, -4), l(1, 1, -4), l(7, 2, 2), l(10, 2, 2), l(11, 8, 9), l(5, 2, 12), 3, 2, 50);
    conv(l(11, 8, 9), l(5, 2, 12), l(5, 2, 19), l(12, 2, 22), l(9, 4, 29), l(7, 2, 36), 3, 2, 50);
    conv(l(9, 4, 29), l(7, 2, 36), l(1, 2, 43), l(16, 2, 50), l(8, 2, 57), l(8, 2, 72), 3, 2, 50);
    move(l(8, 2, 57), l(8, 2, 72), l(1, 2, 75), l(16, 2, 75), 50);
    fc(l(1, 2, 75), l(16, 2, 75), l(5, 2, 79), l(12, 2, 94), l(5, 2, 101), l(12, 2, 101), 50);
    fc(l(5, 2, 101), l(12, 2, 101), l(8, 2, 108), l(8, 2, 115), l(8, 2, 122), l(8, 2, 122), 50);
    print(str('The prediction is class %s', is_high(l(8, 2, 122))));
    return(is_high(l(8, 2, 122)));

);
// `twoclassmnist` scarpet app
// Scarpet app runs a neural network that can classify 2 classes (0 and 1) from MNIST dataset

import('nn', 'conv', 'fc', 'move'); // basic neural network building blocks
import('nn-utils', 'is_high', 'set_block_weight_high', 'set_block_weight_low', 'set_block_activations_high', 'set_block_activations_low');

set_block_weight_high('purple_stained_glass');
set_block_weight_low('lime_stained_glass');

set_block_activations_high('white_concrete');
set_block_activations_low('black_concrete');

__command() -> (
    print('twoclassmnist app\n');
    print('/twoclassmnist forward - forward passes through nn\n');
    print('/clear_activations - clears intermediate activations\n');
    print('');
    '';
);

clear_activations() -> (

    run(str('/fill %d %d %d %d %d %d air', 11, 8, 9, 5, 2, 12));
    run(str('/fill %d %d %d %d %d %d air', 9, 4, 29, 7, 2, 36));
    run(str('/fill %d %d %d %d %d %d air', 8, 2, 57, 8, 2, 72));
    run(str('/fill %d %d %d %d %d %d air', 16, 2, 75, 1, 2, 75));
    run(str('/fill %d %d %d %d %d %d air', 12, 2, 101, 5, 2, 101));
    set(l(8, 2, 122), 'air');
    return('\nCompleted');
    
);

forward() -> (

    game_tick_time = 0;
    if(game_tick_time != 0, print(str('Delaying every computation by %dms', game_tick_time)); clear_activations());

    // my drawingboard is from (16, 16, -4) to (1, 1, -4) [tensor size: 16x16x1]
    // (note that input or activations are from top left corner to bottom right corner)
    // the weights for the first layer are from (7, 2, 2) to (10, 2, 2) [tensor size: 4x1x3x3]
    // (note that 3x3 is the filter size so its not specified by coordinates, just 4x1 space is indicated)
    // the output activations of this layer should be stored at (11, 8, 9) to (5, 2, 12) [tensor size: 7x7x4]
    // (also note here that activations are specified from top left corner to bottom right corner)
    conv(l(16, 16, -4), l(1, 1, -4), l(7, 2, 2), l(10, 2, 2), l(11, 8, 9), l(5, 2, 12), 3, 2, game_tick_time);
    conv(l(11, 8, 9), l(5, 2, 12), l(5, 2, 19), l(12, 2, 22), l(9, 4, 29), l(7, 2, 36), 3, 2, game_tick_time);
    conv(l(9, 4, 29), l(7, 2, 36), l(1, 2, 43), l(16, 2, 50), l(8, 2, 57), l(8, 2, 72), 3, 2, game_tick_time);

    // `move` is exactly like `torch.view()` except here `move` actually ends up moving blocks 
    // (rather than in-place modifications in `torch.view()`)
    // here we are moving blocks in volume specified by (8, 2, 57) and (8, 2, 72) to
    // volume specified by (1, 2, 75) and (16, 2, 75) 
    move(l(8, 2, 57), l(8, 2, 72), l(1, 2, 75), l(16, 2, 75), game_tick_time);

    // same protocol as `conv` layers applies to `fc` layers too
    fc(l(1, 2, 75), l(16, 2, 75), l(5, 2, 79), l(12, 2, 94), l(5, 2, 101), l(12, 2, 101), game_tick_time);
    fc(l(5, 2, 101), l(12, 2, 101), l(8, 2, 108), l(8, 2, 115), l(8, 2, 122), l(8, 2, 122), game_tick_time);
    return(str('\nThe neural network predicts the drawing as digit %d\n', is_high(l(8, 2, 122))));

);
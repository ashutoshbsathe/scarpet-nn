// `nn-utils` scarpet app
// contains helper functions for the main `nn` app

global_weight_high = block('purple_stained_glass');
global_weight_low = block('lime_stained_glass');

global_activations_high = block('white_concrete');
global_activations_low = block('black_concrete');

set_block_weight_high(b) -> (
    global_weight_high = block(b);
);

set_block_weight_low(b) -> (
    global_weight_low = block(b);
);

set_block_activations_high(b) -> (
    global_activations_high = block(b);
);

set_block_activations_low(b) -> (
    global_activations_low = block(b);
);

is_high(block_pos) -> ( (block(block_pos) == global_weight_high) || (block(block_pos) == global_activations_high) );

is_low(block_pos) -> ( (block(block_pos) == global_weight_low) || (block(block_pos) == global_activations_low) );

set_high_activation(block_pos) -> set(block_pos, global_activations_high);

set_low_activation(block_pos) -> set(block_pos, global_activations_low);

list_abs(list_input) -> map(list_input, abs(_));

list_str(list_input) -> ( '[ '+join(' ', list_input)+' ]' );

// notice that sign(x) is not exactly like mathematical signum function
// this function considers `0` as positive number as well
sign(x) -> if(x >= 0, 1, -1); 

list_sign(list_input) -> map(list_input, if(_ >= 0, 1, -1) );

concatenate_lists(list1, list2) -> (
    output = l();
	for(list1, output+=_);
	for(list2, output+=_);
    output
);

xnor(bit1, bit2) -> ( bit1 == bit2 );

multiply_vectors(vec_a, vec_b) -> (
    if(length(vec_a) != length(vec_b),
        exit(str('Lengths of vectors for multiplication should match')));
    map(range(length(vec_a)), xnor(vec_a:_, vec_b:_))
);

reduce_vector(vec) -> (
    total = reduce(vec, _a+_, 0);
    // can theoretically work without rounding off, 
    // but rounding off will ease out comparisons like 2 and 2.00000000001
    total >= round(length(vec)/2) 
);
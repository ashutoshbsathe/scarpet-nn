global_weight_high = block('purple_stained_glass');
global_weight_low = block('lime_stained_glass');

global_activations_high = block('white_concrete');
global_activations_low = block('black_concrete');

is_high(block_pos) -> (
    return((block(block_pos) == global_weight_high) || 
        (block(block_pos) == global_activations_high));
);

is_low(block_pos) -> (
    return((block(block_pos) == global_weight_low) || 
        (block(block_pos) == global_activations_low));
);

set_high_activation(block_pos) -> (
    set(block_pos, global_activations_high);
);

set_low_activation(block_pos) -> (
    set(block_pos, global_activations_low);
);

list_abs(list_input) -> (
    map(list_input, abs(_));
);

list_str(list_input) -> (
    output = str('[ ');
    map(list_input, output += str(_) + str(' '));
    return(output + ']');
);

sign(x) -> (
    if(x >= 0, 1, -1);
);

list_sign(list_input) -> (
    map(list_input, sign(_));
);

clear_buffer(b_pos) -> (
    x = get(b_pos, 0);
    y = get(b_pos, 1);
    z = get(b_pos, 2);
    run(str('/fill %d %d %d %d %d %d air', x, y, z, x, 255, z));
    return(null);
);

concatenate_lists(list1, list2) -> (
    output = l();
    loop(length(list1), output += get(list1, _));
    loop(length(list2), output += get(list2, _));
    return(output);
);

xnor(bit1, bit2) -> (
    return(!((bit1 && !bit2) || (!bit1 && bit2)));
);

multiply_vectors(vec_a, vec_b) -> (
    if(length(vec_a) != length(vec_b),
        exit(str('Lengths of vectors for multiplication should match')));
    output = l();
    loop(length(vec_a), 
        a = get(vec_a, _);
        b = get(vec_b, _);
        output += xnor(a, b);
    );
    return(output);
);

reduce_vector(vec) -> (
    total = reduce(vec, _a+_, 0);
    return(if(total >= round(length(vec)/2), 1, 0));
);
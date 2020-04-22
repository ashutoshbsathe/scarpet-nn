// `nn` scarpet app
// contains definitions for neural network layer computations in scarpet


// import globals 
import('nn-utils', 'global_activations_high', 'global_activations_low', 'global_weight_high', 'global_weight_low');
// import world manipulation helpers
import('nn-utils', 'is_high', 'set_high_activation', 'set_low_activation'); 
// import list utilities
import('nn-utils', 'list_abs', 'list_str', 'concatenate_lists', 'multiply_vectors', 'reduce_vector');
// import mathematical helpers 
import('nn-utils', 'sign', 'xnor');

// input activation coords i_pos1, i_pos2
// weight coords w_pos1 to w_pos2
// output activation coords a_pos1 to a_pos2
// activations lie in (x, y) plane with channels in z direction
// weights lie in (x, z) plane - blocks parallel to z axis represent channels for input and on x axis represent channels for output
conv(i_pos1, i_pos2, w_pos1, w_pos2, a_pos1, a_pos2, kernel_size, stride) -> (

    // check for correct dimensions
    i_dim = list_abs(i_pos1 - i_pos2) + 1;
    w_dim = list_abs(w_pos1 - w_pos2) + 1;
    a_dim = list_abs(a_pos1 - a_pos2) + 1;

    if(get(i_dim, 2) != get(w_dim, 2), 
        exit(str('\nInvalid dimensions for input of convolution function\nExpected channels = %s, given channels = %s',
        get(i_dim, 2), get(w_dim, 2))));
    // calculate output dimension size
    a_dim_calc = l(floor((get(i_dim, 0) - kernel_size) / stride) + 1,
                   floor((get(i_dim, 1) - kernel_size) / stride) + 1,
                   get(w_dim, 0));
    if(a_dim != a_dim_calc, 
        exit(str('\nInvalid dimensions for output of convolution function\ncalculated size: %s, given size: %s', 
        list_str(a_dim_calc), list_str(a_dim))));

    // check that input and output activation directions are same
    if (get(i_pos1, 0) >= get(i_pos2, 0), i_x_dir = -1, i_x_dir = 1);
    if (get(i_pos1, 1) >= get(i_pos2, 1), i_y_dir = -1, i_y_dir = 1);
    if (get(a_pos1, 0) >= get(a_pos2, 0), a_x_dir = -1, a_x_dir = 1);
    if (get(a_pos1, 1) >= get(a_pos2, 1), a_y_dir = -1, a_y_dir = 1);

    if (i_x_dir != a_x_dir || i_y_dir != a_y_dir,
        exit(str('\nInput activation and output activation coordinates should follow same general direction for the diagonal vector')));
    
    // calculate (x,z) direction for weights 
    if (get(w_pos1, 0) >= get(w_pos2, 0), w_x_dir = -1, w_x_dir = 1);
    if (get(w_pos1, 2) >= get(w_pos2, 2), w_y_dir = -1, w_y_dir = 1);

    // calculate output
    width = get(a_dim, 0);
    height = get(a_dim, 1);
    input_channels = get(i_dim, 2);
    output_channels = get(a_dim, 2);
    c_for(x = 0, x < width, x += 1,
        c_for(y = 0, y < height, y += 1,
            input_act_pt = i_pos1 + l(x*stride*i_x_dir, y*stride*i_y_dir, 0);
            output_act_pt = a_pos1 + l(x*a_x_dir, y*a_y_dir, 0);
            input_activation = l();
            c_for(i = 0, i < kernel_size, i += 1,
                c_for(j = 0, j < kernel_size, j += 1,
                    target_pos = input_act_pt + l(j*i_x_dir, i*i_y_dir, 0);
                    input_activation += is_high(target_pos);
                );
            );
            c_for(c = 0, c < output_channels, c += 1,
                // clear the addition buffer corresponding to this layer
                buffer = l();
                c_for(z = 0, z < input_channels, z += 1,
                    // read one filter
                    filter_base = w_pos1 + l(c*w_x_dir, 0, z*w_z_dir);
                    conv_filter = l();
                    c_for(k = 0, k < kernel_size*kernel_size, k += 1,
                        conv_filter += is_high(filter_base + l(0, k, 0));
                    );
                    buffer = concatenate_lists(buffer, multiply_vectors(input_activation, conv_filter));
                );
                target_pos = output_act_pt + l(0, 0, c);
                if(bool(reduce_vector(buffer)), set_high_activation(target_pos), set_low_activation(target_pos));
            );
        );
    );

);

// reshape and the blocks between (i_pos1 -> i_pos2) to (o_pos1, o_pos2)
move(i_pos1, i_pos2, o_pos1, o_pos2) -> (

    i_dim = list_abs(i_pos1 - i_pos2) + 1;
    o_dim = list_abs(o_pos1 - o_pos2) + 1;

    i_volume = reduce(i_dim, _a * _, 1);
    o_volume = reduce(o_dim, _a * _, 1);
    if(i_volume != o_volume, 
        exit(str('Input and output volume does not match')));

    i_x1 = get(i_pos1, 0); i_x2 = get(i_pos2, 0); o_x1 = get(o_pos1, 0); o_x2 = get(o_pos2, 0);
    i_y1 = get(i_pos1, 1); i_y2 = get(i_pos2, 1); o_y1 = get(o_pos1, 1); o_y2 = get(o_pos2, 1);
    i_z1 = get(i_pos1, 2); i_z2 = get(i_pos2, 2); o_z1 = get(o_pos1, 2); o_z2 = get(o_pos2, 2);
    if(i_x1 >= i_x2, i_x_dir = -1, i_x_dir = 1); if(o_x1 >= o_x2, o_x_dir = -1, o_x_dir = 1);
    if(i_y1 >= i_y2, i_y_dir = -1, i_y_dir = 1); if(o_y1 >= o_y2, o_y_dir = -1, o_y_dir = 1);
    if(i_z1 >= i_z2, i_z_dir = -1, i_z_dir = 1); if(o_z1 >= o_z2, o_z_dir = -1, o_z_dir = 1);

    blocks = l();
    c_for(i = 0, i < get(i_dim, 0), i += 1,
        c_for(j = 0, j < get(i_dim, 1), j += 1,
            c_for(k = 0, k < get(i_dim, 2), k += 1,
                target_pos = i_pos1 + l(i*i_x_dir, j*i_y_dir, k*i_z_dir);
                blocks += is_high(target_pos);
            );
        );
    );
    idx = 0;
    c_for(i = 0, i < get(o_dim, 0), i += 1,
        c_for(j = 0, j < get(o_dim, 1), j += 1,
            c_for(k = 0, k < get(o_dim, 2), k += 1,
                target_pos = o_pos1 + l(i*o_x_dir, j*o_y_dir, k*o_z_dir);
                if(bool(get(blocks, idx)), set_high_activation(target_pos), 
                    set_low_activation(target_pos));
                idx += 1;
            );
        );
    );

);

fc(i_pos1, i_pos2, w_pos1, w_pos2, a_pos1, a_pos2) -> (

    i_dim = list_abs(i_pos1 - i_pos2) + 1;
    w_dim = list_abs(w_pos1 - w_pos2) + 1;
    a_dim = list_abs(a_pos1 - a_pos2) + 1;

    // check for valid dimensions
    m = get(w_dim, 0); n_w = get(w_dim, 2);
    n_i = get(i_dim, 0); k = get(i_dim, 2);
    m_a = get(a_dim, 0); k_a = get(a_dim, 2);

    if(n_i != n_w, 
        exit(str('Incompatible matrix multiplication. %s x %s', list_str(w_dim), list_str(i_dim))));
    if(m != m_a || k != k_a,
        exit(str('Incompatible sizes for output. \nExpected size: [%sx%s], Recieved Size: [%sx%s]', m, k, m_a, k_a)));
    n = n_i;
    // directionalities
    i_x1 = get(i_pos1, 0); i_x2 = get(i_pos2, 0); w_x1 = get(w_pos1, 0); w_x2 = get(w_pos2, 0); a_x1 = get(a_pos1, 0); a_x2 = get(a_pos2, 0);
    i_y1 = get(i_pos1, 1); i_y2 = get(i_pos2, 1); w_y1 = get(w_pos1, 1); w_y2 = get(w_pos2, 1); a_y1 = get(a_pos1, 1); a_y2 = get(a_pos2, 1);
    i_z1 = get(i_pos1, 2); i_z2 = get(i_pos2, 2); w_z1 = get(w_pos1, 2); w_z2 = get(w_pos2, 2); a_z1 = get(a_pos1, 2); a_z2 = get(a_pos2, 2);
    if(i_x1 >= i_x2, i_x_dir = -1, i_x_dir = 1); if(w_x1 >= w_x2, w_x_dir = -1, w_x_dir = 1); if(a_x1 >= a_x2, a_x_dir = -1, a_x_dir = 1);
    if(i_y1 >= i_y2, i_y_dir = -1, i_y_dir = 1); if(w_y1 >= w_y2, w_y_dir = -1, w_y_dir = 1); if(a_y1 >= a_y2, a_y_dir = -1, a_y_dir = 1);
    if(i_z1 >= i_z2, i_z_dir = -1, i_z_dir = 1); if(w_z1 >= w_z2, w_z_dir = -1, w_z_dir = 1); if(a_z1 >= a_z2, a_z_dir = -1, a_z_dir = 1);

    // actual multiplication 
    c_for(x = 0, x < m, x += 1,
        c_for(y = 0, y < k, y += 1,
            buffer = l();
            c_for(z = 0, z < n, z += 1,
                target_pos_w = w_pos1 + l(x*w_x_dir, 0, z*w_z_dir);
                target_pos_i = i_pos1 + l(z*i_x_dir, 0, y*i_z_dir);
                buffer += xnor(is_high(target_pos_w), is_high(target_pos_i));
            );
            target_pos = a_pos1 + l(x*a_x_dir, 0, y*a_z_dir);
            if(bool(reduce_vector(buffer)),
                set_high_activation(target_pos), set_low_activation(target_pos));
        );
    );

);

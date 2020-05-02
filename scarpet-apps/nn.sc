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

__command() -> (
    print('nn app\n');
    print('Neural network layer implementations in scarpet\n');
    print('/nn conv - convolution layer implementation\n');
    print('/nn fc - fully connected layer implementation\n');
    print('/nn move - like torch.view() but actually copies the block to new place\n');
    print('');
    '';
);

// input activation coords i_pos1, i_pos2
// weight coords w_pos1 to w_pos2
// output activation coords a_pos1 to a_pos2
// activations lie in (x, y) plane with channels in z direction
// weights lie in (x, z) plane - blocks parallel to z axis represent channels for input and on x axis represent channels for output
// `game_tick_time` is the amount of ms the game should tick after placing an activation block. It keeps the game more responsive
conv(i_pos1, i_pos2, w_pos1, w_pos2, a_pos1, a_pos2, kernel_size, stride, game_tick_time) -> (

    // check for correct dimensions
    i_dim = list_abs(i_pos1 - i_pos2) + 1;
    w_dim = list_abs(w_pos1 - w_pos2) + 1;
    a_dim = list_abs(a_pos1 - a_pos2) + 1;

    if(i_dim:2 != w_dim:2, 
        exit(str('\nInvalid dimensions for input of convolution function\nExpected channels = %s, given channels = %s',
        i_dim:2, w_dim:2))
	);
    // calculate output dimension size
    a_dim_calc = l(floor((i_dim:0 - kernel_size) / stride) + 1,
                   floor((i_dim:1 - kernel_size) / stride) + 1,
                   w_dim:0);
    if(a_dim != a_dim_calc, 
        exit(str('\nInvalid dimensions for output of convolution function\ncalculated size: %s, given size: %s', 
        list_str(a_dim_calc), list_str(a_dim)))); // lists should pretty print themselves by default using str('%s', list)

    // check that input and output activation directions are same
    if (i_pos1:0 >= i_pos2:0, i_x_dir = -1, i_x_dir = 1);
    if (i_pos1:1 >= i_pos2:1, i_y_dir = -1, i_y_dir = 1);
    if (a_pos1:0 >= a_pos2:0, a_x_dir = -1, a_x_dir = 1);
    if (a_pos1:1 >= a_pos2:1, a_y_dir = -1, a_y_dir = 1);

    if (i_x_dir != a_x_dir || i_y_dir != a_y_dir,
        exit(str('\nInput activation and output activation coordinates should follow same general direction for the diagonal vector')));
    
    // calculate (x,z) direction for weights 
    if (w_pos1:0 >= w_pos2:0, w_x_dir = -1, w_x_dir = 1);
    if (w_pos1:2 >= w_pos2:2, w_y_dir = -1, w_y_dir = 1);

    // calculate output
    width = a_dim:0;
    height = a_dim:1;
    input_channels = i_dim:2;
    output_channels = a_dim:2;
	loop(width, x = _;
	    loop(height, y = _;
            input_act_pt = i_pos1 + l(x*stride*i_x_dir, y*stride*i_y_dir, 0);
            output_act_pt = a_pos1 + l(x*a_x_dir, y*a_y_dir, 0);
            input_activation = l();
			loop(kernel_size, i = _;
			    loop(kernel_size, j = _;
                    target_pos = input_act_pt + l(j*i_x_dir, i*i_y_dir, 0);
                    input_activation += is_high(target_pos);
                );
            );
			loop(output_channels, c = _;
                // clear the addition buffer corresponding to this layer
                buffer = l();
                loop(input_channels, z = _;
                    // read one filter
                    filter_base = w_pos1 + l(c*w_x_dir, 0, z*w_z_dir);
                    conv_filter = l();
					loop(kernel_size*kernel_size, k = _;
                        conv_filter += is_high(filter_base + l(0, k, 0));
                    );
                    buffer = concatenate_lists(buffer, multiply_vectors(input_activation, conv_filter));
                );
                target_pos = output_act_pt + l(0, 0, c);
                if(reduce_vector(buffer), set_high_activation(target_pos), set_low_activation(target_pos));
                if(game_tick_time != 0, game_tick(game_tick_time));
            );
        );
    );

);

// reshape and the blocks between (i_pos1 -> i_pos2) to (o_pos1, o_pos2)
// `game_tick_time` is the amount of ms the game should tick after placing an activation block. It keeps the game more responsive
move(i_pos1, i_pos2, o_pos1, o_pos2, game_tick_time) -> (

    i_dim = list_abs(i_pos1 - i_pos2) + 1;
    o_dim = list_abs(o_pos1 - o_pos2) + 1;

    i_volume = reduce(i_dim, _a * _, 1);
    o_volume = reduce(o_dim, _a * _, 1);
    if(i_volume != o_volume, 
        exit(str('Input and output volume does not match')));

	l(i_x1, i_y1, i_z1) = i_pos1;
	l(i_x2, i_y2, i_z2) = i_pos2;
	
	l(o_x1, o_y1, o_z1) = o_pos1;
	l(o_x2, o_y2, o_z2) = o_pos2;
	
    if(i_x1 >= i_x2, i_x_dir = -1, i_x_dir = 1); if(o_x1 >= o_x2, o_x_dir = -1, o_x_dir = 1);
    if(i_y1 >= i_y2, i_y_dir = -1, i_y_dir = 1); if(o_y1 >= o_y2, o_y_dir = -1, o_y_dir = 1);
    if(i_z1 >= i_z2, i_z_dir = -1, i_z_dir = 1); if(o_z1 >= o_z2, o_z_dir = -1, o_z_dir = 1);

    blocks = l();
	loop(i_dim:0, i = _;
	    loop(i_dim:1, j = _;
		    loop(i_dim:2, k = _;  // can replace 3 nested loops with one `scan` or `volume` call utilizing _x, _y and _z
                target_pos = i_pos1 + l(i*i_x_dir, j*i_y_dir, k*i_z_dir);
                blocks += is_high(target_pos);
            );
        );
    );
    idx = 0;
	loop(o_dim:0, i = _;
	    loop(o_dim:1, j = _;
		    loop(o_dim:2, k = _;  // can replace 3 nested loops with one `scan` or `volume` call utilizing _x, _y and _z
                target_pos = o_pos1 + l(i*o_x_dir, j*o_y_dir, k*o_z_dir);
                if(blocks:idx, set_high_activation(target_pos), set_low_activation(target_pos));
                idx += 1;
                if(game_tick_time != 0, game_tick(game_tick_time));
            );
        );
    );
);

// `game_tick_time` is the amount of ms the game should tick after placing an activation block. It keeps the game more responsive
fc(i_pos1, i_pos2, w_pos1, w_pos2, a_pos1, a_pos2, game_tick_time) -> (

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
	l(i_x1, i_y1, i_z1) = i_pos1;
	l(i_x2, i_y2, i_z2) = i_pos2;
	
	l(w_x1, w_y1, w_z1) = w_pos1;
	l(w_x2, w_y2, w_z2) = w_pos2;
	
	l(a_x1, a_y1, a_z1) = a_pos1;
	l(a_x2, a_y2, a_z2) = a_pos2;
	
	// consider maybe v_dir = list_sign(v-w)
    if(i_x1 >= i_x2, i_x_dir = -1, i_x_dir = 1); if(w_x1 >= w_x2, w_x_dir = -1, w_x_dir = 1); if(a_x1 >= a_x2, a_x_dir = -1, a_x_dir = 1);
    if(i_y1 >= i_y2, i_y_dir = -1, i_y_dir = 1); if(w_y1 >= w_y2, w_y_dir = -1, w_y_dir = 1); if(a_y1 >= a_y2, a_y_dir = -1, a_y_dir = 1);
    if(i_z1 >= i_z2, i_z_dir = -1, i_z_dir = 1); if(w_z1 >= w_z2, w_z_dir = -1, w_z_dir = 1); if(a_z1 >= a_z2, a_z_dir = -1, a_z_dir = 1);

    // actual multiplication 
	loop(m, x = _;
	    loop(k, y = _;
			buffer = map(range(n),
				target_pos_w = w_pos1 + l(x*w_x_dir, 0, _*w_z_dir);
                target_pos_i = i_pos1 + l(_*i_x_dir, 0, y*i_z_dir);
                xnor(is_high(target_pos_w), is_high(target_pos_i))
			);
			
            target_pos = a_pos1 + l(x*a_x_dir, 0, y*a_z_dir);
            if(reduce_vector(buffer),
                set_high_activation(target_pos), set_low_activation(target_pos));
            if(game_tick_time != 0, game_tick(game_tick_time));
        );
    );
);

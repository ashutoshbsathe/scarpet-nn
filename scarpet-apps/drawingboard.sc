global_pos1 = l(16, 16, -4);
global_pos2 = l(1, 1, -4);
global_draw_block = 'white_concrete';
global_empty_block = 'black_concrete';
global_reach = 50; // shouldn't be required to changed dynamically

__command() -> (
    print('drawingboard app\n');
    print('right click on the drawingboard with any sword to draw\n');
    print('/drawingboard clear_board - clears the drawingboard\n');
    print('');
    '';
);

is_between(a, b, c) -> ( (a >= b && b >= c) || (a <= b && b <= c) ); //  if (a <= b <= c) || (a >= b >= c)


within_drawing_board(b_pos) -> 
(
    l(x1, y1, z1) = global_pos1;
	l(x2, y2, y2) = b_pos;
	l(x3, y3, z3) = global_pos2;

    is_between(x1, x2, x3) && is_between(y1, y2, y3) && is_between(z1, z2, z3);
);

__on_player_uses_item(player, item_tuple, hand) -> 
(
    if ((item_tuple:0 ~ '_sword') && hand == 'mainhand',
	    target = query(player, 'trace', global_reach, 'blocks');
		if (target && (pos(target)),
			set(target, global_draw_block)
		)
	)
);

clear_board() -> (
    l(x1, y1, z1) = global_pos1;
	l(x2, y2, z2) = global_pos2;
	successes = volume(x1,y1,z1, x2,y2,z2, set(_, global_empty_block));
	str('\nCleared %d blocks from drawingboard', successes)
);
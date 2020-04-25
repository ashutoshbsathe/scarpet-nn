global_pencils = l('wooden_sword', 'stone_sword', 'iron_sword', 'golden_sword', 'diamond_sword');
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

is_between(a, b, c) -> (
    // return if (a <= b <= c) || (a >= b >= c)
    return(a >= b && b >= c) || (a <= b && b <= c);
);

within_drawing_board(b_pos) -> (
    x1 = get(global_pos1, 0); y1 = get(global_pos1, 1); z1 = get(global_pos1, 2);
    x2 = get(b_pos, 0); y2 = get(b_pos, 1); z2 = get(b_pos, 2);
    x3 = get(global_pos2, 0); y3 = get(global_pos2, 1); z3 = get(global_pos2, 2);
    return(is_between(x1, x2, x3) && is_between(y1, y2, y3) && is_between(z1, z2, z3));
);

__on_player_uses_item(player, item_tuple, hand) -> (
    if (!bool(global_pencils ~ get(query(player, 'holds', 'mainhand'), 0)), return());
    looking_at = pos(query(player, 'trace', global_reach, 'blocks'));
    if (!within_drawing_board(looking_at), return());
    set(looking_at, global_draw_block);
);

clear_board() -> (
    x1 = get(global_pos1, 0); y1 = get(global_pos1, 1); z1 = get(global_pos1, 2);
    x2 = get(global_pos2, 0); y2 = get(global_pos2, 1); z2 = get(global_pos2, 2);
    num = run(str('/fill %d %d %d %d %d %d %s', x1, y1, z1, x2, y2, z2, global_empty_block));
    return(str('\nCleared %d blocks from drawingboard', num));
);
serialized_tag = """
{{
    "": {{
        MinecraftDataVersion: 2230,
        Version: 5,
        Metadata: {{
            TimeCreated: {time}L,
            TimeModified: {time}L,
            EnclosingSize: {{
                x: {enc_x},
                y: {enc_y},
                z: {enc_z}
            }},
            Description: "",
            RegionCount: 1,
            TotalBlocks: {total_blocks},
            Author: "{author}",
            TotalVolume: {total_volume},
            Name: "{metadata_name}"
        }},
        Regions: {{
            {metadata_name}: {{
                BlockStates: {block_states},
                PendingBlockTicks: [],
                Position: {{
                    x: {pos_x},
                    y: {pos_y},
                    z: {pos_z}
                }},
                BlockStatePalette: [
                    {{
                        Name: "minecraft:air"
                    }},
                    {{
                        Name: "{pos_block}"
                    }},
                    {{
                        Name: "{neg_block}"
                    }},
                    {{
                        Name: "minecraft:smooth_stone"
                    }}
                ],
                Size: {{
                    x: {size_x},
                    y: {size_y},
                    z: {size_z}
                }},
                PendingFluidTicks: [],
                TileEntities: [],
                Entities: []
            }}
        }}
    }}
}}
"""
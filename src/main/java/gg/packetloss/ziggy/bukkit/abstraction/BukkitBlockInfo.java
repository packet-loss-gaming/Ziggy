/*
 * Copyright (c) 2020 Wyatt Childers.
 *
 * This file is part of Ziggy.
 *
 * Ziggy is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Ziggy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Ziggy.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package gg.packetloss.ziggy.bukkit.abstraction;

import gg.packetloss.ziggy.abstraction.BlockClassification;
import gg.packetloss.ziggy.abstraction.ZBlockInfo;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.data.BlockData;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class BukkitBlockInfo implements ZBlockInfo {
    private final BlockData blockData;
    private final Biome biome;

    public BukkitBlockInfo(BlockData blockData, Biome biome) {
        this.blockData = blockData;
        this.biome = biome;
    }

    @Override
    public boolean isAir() {
        return blockData.getMaterial() == Material.AIR;
    }

    @Override
    public boolean isContainer() {
        switch (blockData.getMaterial()) {
            case CHEST:
            case TRAPPED_CHEST:
            case DISPENSER:
            case DROPPER:
            case HOPPER:
            case BREWING_STAND:
            case ENDER_CHEST:
                return true;
            default:
                return false;
        }
    }

    private static boolean isOverworldBiome(Biome biome) {
        return biome != Biome.NETHER;
    }

    private static boolean isNetherBiome(Biome biome) {
        return biome == Biome.NETHER;
    }

    private static EnumMap<Material, Function<Biome, BlockClassification>> classifications = new EnumMap<>(Material.class);

    private static final Function<Biome, BlockClassification> STRUCTURAL = (ignored) -> BlockClassification.STRUCTURAL;
    private static final Function<Biome, BlockClassification> ENVIRONMENTAL = (biome) -> BlockClassification.ENVIRONMENTAL;
    private static final Function<Biome, BlockClassification> OVERWORLD_ENVIRONMENTAL = (biome) -> isOverworldBiome(biome) ? BlockClassification.ENVIRONMENTAL : BlockClassification.STRUCTURAL;
    private static final Function<Biome, BlockClassification> NETHER_ENVIRONMENTAL = (biome) -> isNetherBiome(biome) ? BlockClassification.ENVIRONMENTAL : BlockClassification.STRUCTURAL;
    private static final Function<Biome, BlockClassification> UNDECIDED = (ignored) -> BlockClassification.UNDECIDED;

    private static class Classifier {
        private List<AbstractMap.SimpleEntry<Predicate<String>, Function<Biome, BlockClassification>>> patterns = new ArrayList<>();

        private void internalAddPattern(String regex, Function<Biome, BlockClassification> function) {
            patterns.add(new AbstractMap.SimpleEntry<>(Pattern.compile(regex).asMatchPredicate(), function));
        }

        public void registerExactRule(String name, Function<Biome, BlockClassification> function) {
            internalAddPattern(name, function);
        }

        public void registerContainsRule(String name, Function<Biome, BlockClassification> function) {
            internalAddPattern(".*" + name + ".*", function);
        }

        public Function<Biome, BlockClassification> getClassificationFunction(String name) {
            for (AbstractMap.SimpleEntry<Predicate<String>, Function<Biome, BlockClassification>> entry : patterns) {
                if (entry.getKey().test(name)) {
                    return entry.getValue();
                }
            }

            return UNDECIDED;
        }
    }

    static {
        Classifier c = new Classifier();

        // Building
        c.registerExactRule("flower_pot", STRUCTURAL);
        c.registerExactRule("nether_wart_block", STRUCTURAL);

        c.registerContainsRule("anvil", STRUCTURAL);
        c.registerContainsRule("barrel", STRUCTURAL);
        c.registerContainsRule("bed", STRUCTURAL);
        c.registerContainsRule("beacon", STRUCTURAL);
        c.registerContainsRule("bone", STRUCTURAL);
        c.registerContainsRule("brick", STRUCTURAL);
        c.registerContainsRule("button", STRUCTURAL);
        c.registerContainsRule("carpet", STRUCTURAL);
        c.registerContainsRule("chest", STRUCTURAL);
        c.registerContainsRule("coal", STRUCTURAL);
        c.registerContainsRule("cobblestone", STRUCTURAL);
        c.registerContainsRule("command", STRUCTURAL);
        c.registerContainsRule("concrete", STRUCTURAL);
        c.registerContainsRule("dispenser", STRUCTURAL);
        c.registerContainsRule("diamond", STRUCTURAL);
        c.registerContainsRule("door", STRUCTURAL);
        c.registerContainsRule("fence", STRUCTURAL);
        c.registerContainsRule("furnace", STRUCTURAL);
        c.registerContainsRule("gold", STRUCTURAL);
        c.registerContainsRule("glass", STRUCTURAL);
        c.registerContainsRule("iron", STRUCTURAL);
        c.registerContainsRule("lapis", STRUCTURAL);
        c.registerContainsRule("note", STRUCTURAL);
        c.registerContainsRule("pillar", STRUCTURAL);
        c.registerContainsRule("planks", STRUCTURAL);
        c.registerContainsRule("pressure_plate", STRUCTURAL);
        c.registerContainsRule("purpur", STRUCTURAL);
        c.registerContainsRule("redstone", STRUCTURAL);
        c.registerContainsRule("sandstone", STRUCTURAL);
        c.registerContainsRule("shulker_box", STRUCTURAL);
        c.registerContainsRule("sign", STRUCTURAL);
        c.registerContainsRule("slab", STRUCTURAL);
        c.registerContainsRule("stairs", STRUCTURAL);
        c.registerContainsRule("terracotta", STRUCTURAL);
        c.registerContainsRule("wall", STRUCTURAL);
        c.registerContainsRule("wood", STRUCTURAL);
        c.registerContainsRule("wool", STRUCTURAL);
        c.registerContainsRule("_table", STRUCTURAL);
        c.registerContainsRule("_table", STRUCTURAL);

        // Farmables
        c.registerExactRule("beetroots", ENVIRONMENTAL);
        c.registerExactRule("carrots", ENVIRONMENTAL);
        c.registerExactRule("chorus_flower", ENVIRONMENTAL);
        c.registerExactRule("kelp", ENVIRONMENTAL);
        c.registerExactRule("nether_wart", ENVIRONMENTAL);
        c.registerExactRule("potatoes", ENVIRONMENTAL);
        c.registerExactRule("sugar_cane", ENVIRONMENTAL);
        c.registerExactRule("wheat", ENVIRONMENTAL);

        c.registerContainsRule("bush", ENVIRONMENTAL);
        c.registerContainsRule("sapling", ENVIRONMENTAL);
        c.registerContainsRule("stem", ENVIRONMENTAL);

        // Normal terrain blocks
        c.registerExactRule("stone", ENVIRONMENTAL);
        c.registerContainsRule("coral", ENVIRONMENTAL);
        c.registerContainsRule("dirt", ENVIRONMENTAL);
        c.registerContainsRule("grass", ENVIRONMENTAL);
        c.registerContainsRule("gravel", ENVIRONMENTAL);

        // Normal nether terrain blocks
        c.registerExactRule("netherrack", NETHER_ENVIRONMENTAL);
        c.registerExactRule("soul_sand", NETHER_ENVIRONMENTAL);

        for (Material material : Material.values()) {
            if (material.isLegacy()) {
                continue;
            }

            String name = material.name().toLowerCase();
            classifications.put(material, c.getClassificationFunction(name));
        }
    }

    @Override
    public BlockClassification classify() {
        return classifications.getOrDefault(blockData.getMaterial(), UNDECIDED).apply(biome);
    }
}

package com.ebicep.warlords.pve.items;

import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.items.statpool.ItemBucklerStatPool;
import com.ebicep.warlords.pve.items.statpool.ItemGauntletStatPool;
import com.ebicep.warlords.pve.items.statpool.ItemStatPool;
import com.ebicep.warlords.pve.items.statpool.ItemTomeStatPool;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public enum ItemTier {

    ALL(
            "None",
            ChatColor.BLACK,
            new ItemStack(org.bukkit.Material.STAINED_GLASS_PANE, 1, (short) 0),
            new ItemStack(Material.STAINED_CLAY, 1, (short) 0),
            null,
            0,
            0,
            0,
            0,
            null,
            null,
            null,
            0,
            null
    ) {
        @Override
        public <T extends Enum<T> & ItemStatPool<T>> Set<T> generateStatPool(T[] pool) {
            return null;
        }
    },
    ALPHA(
            "Alpha",
            ChatColor.GREEN,
            new ItemStack(org.bukkit.Material.STAINED_GLASS_PANE, 1, (short) 5),
            new ItemStack(Material.STAINED_CLAY, 1, (short) 5),
            new WeightRange(7, 10, 15),
            .1,
            .001,
            .65,
            .05,
            new HashMap<>() {{
                put(ItemGauntletStatPool.HP, new ItemTier.StatRange(1, 5));
                put(ItemGauntletStatPool.MAX_ENERGY, new ItemTier.StatRange(1, 5));
                put(ItemGauntletStatPool.EPH, new ItemTier.StatRange(1, 5));
                put(ItemGauntletStatPool.EPS, new ItemTier.StatRange(1, 5));
                put(ItemGauntletStatPool.SPEED, new ItemTier.StatRange(1, 5));
            }},
            new HashMap<>() {{
                put(ItemTomeStatPool.DAMAGE, new ItemTier.StatRange(1, 5));
                put(ItemTomeStatPool.HEALING, new ItemTier.StatRange(1, 5));
                put(ItemTomeStatPool.CRIT_CHANCE, new ItemTier.StatRange(1, 5));
                put(ItemTomeStatPool.CRIT_MULTI, new ItemTier.StatRange(1, 5));
                put(ItemTomeStatPool.CD_RED, new ItemTier.StatRange(1, 5));
            }},
            new HashMap<>() {{
                put(ItemBucklerStatPool.DAMAGE_RED, new ItemTier.StatRange(1, 5));
                put(ItemBucklerStatPool.AGGRO_PRIO, new ItemTier.StatRange(1, 5));
                put(ItemBucklerStatPool.THORNS, new ItemTier.StatRange(1, 5));
                put(ItemBucklerStatPool.KB_RES, new ItemTier.StatRange(1, 5));
                put(ItemBucklerStatPool.REGEN_TIMER, new ItemTier.StatRange(1, 5));
            }},
            3,
            new LinkedHashMap<>() {{
                put(Currencies.COIN, 10_000L);
            }}
    ) {
        @Override
        public <T extends Enum<T> & ItemStatPool<T>> Set<T> generateStatPool(T[] pool) {
            return generateStatPoolWithSettings(pool, 2, .45, .10);
        }
    },
    BETA(
            "Beta",
            ChatColor.BLUE,
            new ItemStack(org.bukkit.Material.STAINED_GLASS_PANE, 1, (short) 3),
            new ItemStack(org.bukkit.Material.STAINED_CLAY, 1, (short) 3),
            new WeightRange(15, 20, 30),
            .05,
            .0005,
            .55,
            .10,
            new HashMap<>() {{
                put(ItemGauntletStatPool.HP, new ItemTier.StatRange(1, 5));
                put(ItemGauntletStatPool.MAX_ENERGY, new ItemTier.StatRange(1, 5));
                put(ItemGauntletStatPool.EPH, new ItemTier.StatRange(1, 5));
                put(ItemGauntletStatPool.EPS, new ItemTier.StatRange(1, 5));
                put(ItemGauntletStatPool.SPEED, new ItemTier.StatRange(1, 5));
            }},
            new HashMap<>() {{
                put(ItemTomeStatPool.DAMAGE, new ItemTier.StatRange(1, 5));
                put(ItemTomeStatPool.HEALING, new ItemTier.StatRange(1, 5));
                put(ItemTomeStatPool.CRIT_CHANCE, new ItemTier.StatRange(1, 5));
                put(ItemTomeStatPool.CRIT_MULTI, new ItemTier.StatRange(1, 5));
                put(ItemTomeStatPool.CD_RED, new ItemTier.StatRange(1, 5));
            }},
            new HashMap<>() {{
                put(ItemBucklerStatPool.DAMAGE_RED, new ItemTier.StatRange(1, 5));
                put(ItemBucklerStatPool.AGGRO_PRIO, new ItemTier.StatRange(1, 5));
                put(ItemBucklerStatPool.THORNS, new ItemTier.StatRange(1, 5));
                put(ItemBucklerStatPool.KB_RES, new ItemTier.StatRange(1, 5));
                put(ItemBucklerStatPool.REGEN_TIMER, new ItemTier.StatRange(1, 5));
            }},
            2,
            new LinkedHashMap<>() {{
                put(Currencies.COIN, 25_000L);
                put(Currencies.SYNTHETIC_SHARD, 100L);
            }}
    ) {
        @Override
        public <T extends Enum<T> & ItemStatPool<T>> Set<T> generateStatPool(T[] pool) {
            return generateStatPoolWithSettings(pool, 2, .75, .30);
        }
    },
    GAMMA(
            "Gamma",
            ChatColor.LIGHT_PURPLE,
            new ItemStack(org.bukkit.Material.STAINED_GLASS_PANE, 1, (short) 2),
            new ItemStack(org.bukkit.Material.STAINED_CLAY, 1, (short) 2),
            new WeightRange(22, 30, 45),
            .01,
            .0001,
            .45,
            .10,
            new HashMap<>() {{
                put(ItemGauntletStatPool.HP, new ItemTier.StatRange(1, 5));
                put(ItemGauntletStatPool.MAX_ENERGY, new ItemTier.StatRange(1, 5));
                put(ItemGauntletStatPool.EPH, new ItemTier.StatRange(1, 5));
                put(ItemGauntletStatPool.EPS, new ItemTier.StatRange(1, 5));
                put(ItemGauntletStatPool.SPEED, new ItemTier.StatRange(1, 5));
            }},
            new HashMap<>() {{
                put(ItemTomeStatPool.DAMAGE, new ItemTier.StatRange(1, 5));
                put(ItemTomeStatPool.HEALING, new ItemTier.StatRange(1, 5));
                put(ItemTomeStatPool.CRIT_CHANCE, new ItemTier.StatRange(1, 5));
                put(ItemTomeStatPool.CRIT_MULTI, new ItemTier.StatRange(1, 5));
                put(ItemTomeStatPool.CD_RED, new ItemTier.StatRange(1, 5));
            }},
            new HashMap<>() {{
                put(ItemBucklerStatPool.DAMAGE_RED, new ItemTier.StatRange(1, 5));
                put(ItemBucklerStatPool.AGGRO_PRIO, new ItemTier.StatRange(1, 5));
                put(ItemBucklerStatPool.THORNS, new ItemTier.StatRange(1, 5));
                put(ItemBucklerStatPool.KB_RES, new ItemTier.StatRange(1, 5));
                put(ItemBucklerStatPool.REGEN_TIMER, new ItemTier.StatRange(1, 5));
            }},
            2,
            new LinkedHashMap<>() {{
                put(Currencies.COIN, 75_000L);
                put(Currencies.SYNTHETIC_SHARD, 250L);
            }}
    ) {
        @Override
        public <T extends Enum<T> & ItemStatPool<T>> Set<T> generateStatPool(T[] pool) {
            return generateStatPoolWithSettings(pool, 3, .30, 0);
        }
    },
    DELTA(
            "Delta",
            ChatColor.YELLOW,
            new ItemStack(org.bukkit.Material.STAINED_GLASS_PANE, 1, (short) 4),
            new ItemStack(org.bukkit.Material.STAINED_CLAY, 1, (short) 4),
            new WeightRange(30, 40, 60),
            .001,
            .00001,
            .35,
            .20,
            new HashMap<>() {{
                put(ItemGauntletStatPool.HP, new ItemTier.StatRange(1, 5));
                put(ItemGauntletStatPool.MAX_ENERGY, new ItemTier.StatRange(1, 5));
                put(ItemGauntletStatPool.EPH, new ItemTier.StatRange(1, 5));
                put(ItemGauntletStatPool.EPS, new ItemTier.StatRange(1, 5));
                put(ItemGauntletStatPool.SPEED, new ItemTier.StatRange(1, 5));
            }},
            new HashMap<>() {{
                put(ItemTomeStatPool.DAMAGE, new ItemTier.StatRange(1, 5));
                put(ItemTomeStatPool.HEALING, new ItemTier.StatRange(1, 5));
                put(ItemTomeStatPool.CRIT_CHANCE, new ItemTier.StatRange(1, 5));
                put(ItemTomeStatPool.CRIT_MULTI, new ItemTier.StatRange(1, 5));
                put(ItemTomeStatPool.CD_RED, new ItemTier.StatRange(1, 5));
            }},
            new HashMap<>() {{
                put(ItemBucklerStatPool.DAMAGE_RED, new ItemTier.StatRange(1, 5));
                put(ItemBucklerStatPool.AGGRO_PRIO, new ItemTier.StatRange(1, 5));
                put(ItemBucklerStatPool.THORNS, new ItemTier.StatRange(1, 5));
                put(ItemBucklerStatPool.KB_RES, new ItemTier.StatRange(1, 5));
                put(ItemBucklerStatPool.REGEN_TIMER, new ItemTier.StatRange(1, 5));
            }},
            1,
            new LinkedHashMap<>() {{
                put(Currencies.COIN, 125_000L);
                put(Currencies.LEGEND_FRAGMENTS, 50L);
            }}
    ) {
        @Override
        public <T extends Enum<T> & ItemStatPool<T>> Set<T> generateStatPool(T[] pool) {
            return generateStatPoolWithSettings(pool, 4, .20, 0);
        }
    },
    OMEGA(
            "Omega",
            ChatColor.GRAY,
            new ItemStack(org.bukkit.Material.STAINED_GLASS_PANE, 1, (short) 1),
            new ItemStack(org.bukkit.Material.STAINED_CLAY, 1, (short) 1),
            new WeightRange(37, 50, 75),
            0,
            0,
            .25,
            .25,
            new HashMap<>() {{
                put(ItemGauntletStatPool.HP, new ItemTier.StatRange(1, 5));
                put(ItemGauntletStatPool.MAX_ENERGY, new ItemTier.StatRange(1, 5));
                put(ItemGauntletStatPool.EPH, new ItemTier.StatRange(1, 5));
                put(ItemGauntletStatPool.EPS, new ItemTier.StatRange(1, 5));
                put(ItemGauntletStatPool.SPEED, new ItemTier.StatRange(1, 5));
            }},
            new HashMap<>() {{
                put(ItemTomeStatPool.DAMAGE, new ItemTier.StatRange(1, 5));
                put(ItemTomeStatPool.HEALING, new ItemTier.StatRange(1, 5));
                put(ItemTomeStatPool.CRIT_CHANCE, new ItemTier.StatRange(1, 5));
                put(ItemTomeStatPool.CRIT_MULTI, new ItemTier.StatRange(1, 5));
                put(ItemTomeStatPool.CD_RED, new ItemTier.StatRange(1, 5));
            }},
            new HashMap<>() {{
                put(ItemBucklerStatPool.DAMAGE_RED, new ItemTier.StatRange(1, 5));
                put(ItemBucklerStatPool.AGGRO_PRIO, new ItemTier.StatRange(1, 5));
                put(ItemBucklerStatPool.THORNS, new ItemTier.StatRange(1, 5));
                put(ItemBucklerStatPool.KB_RES, new ItemTier.StatRange(1, 5));
                put(ItemBucklerStatPool.REGEN_TIMER, new ItemTier.StatRange(1, 5));
            }},
            1,
            new LinkedHashMap<>() {{
                put(Currencies.COIN, 200_000L);
                put(Currencies.LEGEND_FRAGMENTS, 200L);
            }}
    ) {
        @Override
        public <T extends Enum<T> & ItemStatPool<T>> Set<T> generateStatPool(T[] pool) {
            return generateStatPoolWithSettings(pool, 5, 0, 0);
        }
    },

    ;

    public static final ItemTier[] VALUES = values();
    public static final ItemTier[] VALID_VALUES = Arrays.stream(VALUES)
                                                        .filter(itemTier -> itemTier != ALL)
                                                        .toArray(ItemTier[]::new);

    private static <T extends Enum<T> & ItemStatPool<T>> Set<T> generateStatPoolWithSettings(
            T[] pool,
            int initialPool,
            double firstReducedPoolChance,
            double secondReducedPoolChance
    ) {
        Set<T> statPool = new HashSet<>();
        List<T> poolList = new ArrayList<>(Arrays.asList(pool));
        Collections.shuffle(poolList);
        for (int i = 0; i < initialPool; i++) {
            statPool.add(poolList.remove(0));
        }
        addFromReducedPool(firstReducedPoolChance, statPool, poolList);
        addFromReducedPool(secondReducedPoolChance, statPool, poolList);
        return statPool;
    }

    private static <T extends Enum<T> & ItemStatPool<T>> void addFromReducedPool(
            double firstReducedPoolChance,
            Set<T> statPool,
            List<T> poolList
    ) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        if (firstReducedPoolChance != 0 && !poolList.isEmpty() && random.nextDouble() <= firstReducedPoolChance) {
            statPool.add(poolList.remove(0));
        }
    }

    public final String name;
    public final ChatColor chatColor;
    public final ItemStack glassPane;
    public final ItemStack clayBlock;
    public final WeightRange weightRange;
    public final double dropChance;
    public final double killDropChance;
    public final double cursedChance;
    public final double blessedChance;
    public final HashMap<ItemGauntletStatPool, StatRange> gauntletStatRange;
    public final HashMap<ItemTomeStatPool, StatRange> tomeStatRange;
    public final HashMap<ItemBucklerStatPool, StatRange> bucklerStatRange;
    public final int maxEquipped;
    public final LinkedHashMap<Spendable, Long> removeCurseCost;

    ItemTier(
            String name,
            ChatColor chatColor,
            ItemStack glassPane, ItemStack clayBlock, WeightRange weightRange,
            double dropChance,
            double killDropChance,
            double cursedChance,
            double blessedChance,
            HashMap<ItemGauntletStatPool, StatRange> gauntletStatRange,
            HashMap<ItemTomeStatPool, StatRange> tomeStatRange,
            HashMap<ItemBucklerStatPool, StatRange> bucklerStatRange,
            int maxEquipped,
            LinkedHashMap<Spendable, Long> removeCurseCost
    ) {
        this.name = name;
        this.chatColor = chatColor;
        this.glassPane = glassPane;
        this.clayBlock = clayBlock;
        this.weightRange = weightRange;
        this.dropChance = dropChance;
        this.killDropChance = killDropChance;
        this.cursedChance = cursedChance;
        this.blessedChance = blessedChance;
        this.gauntletStatRange = gauntletStatRange;
        this.tomeStatRange = tomeStatRange;
        this.bucklerStatRange = bucklerStatRange;
        this.maxEquipped = maxEquipped;
        this.removeCurseCost = removeCurseCost;
    }

    public abstract <T extends Enum<T> & ItemStatPool<T>> Set<T> generateStatPool(T[] pool);

    public String getColoredName() {
        return chatColor + name;
    }

    public ItemTier next() {
        return VALUES[(this.ordinal() + 1) % VALUES.length];
    }

    public static class StatRange {

        private final int min;
        private final int max;

        public StatRange(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public int getMin() {
            return min;
        }

        public int getMax() {
            return max;
        }

        public int generateValue() {
            return ThreadLocalRandom.current().nextInt(min, max);
        }

    }

    public static class WeightRange {
        private final int min;
        private final int normal;
        private final int max;

        public WeightRange(int min, int normal, int max) {
            this.min = min;
            this.normal = normal;
            this.max = max;
        }

        public int getMin() {
            return min;
        }

        public int getNormal() {
            return normal;
        }

        public int getMax() {
            return max;
        }
    }

}

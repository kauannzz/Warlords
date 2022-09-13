package com.ebicep.warlords;

import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.ebicep.customentities.nms.pve.CustomEntitiesRegistry;
import com.ebicep.customentities.npc.NPCManager;
import com.ebicep.jda.BotListener;
import com.ebicep.jda.BotManager;
import com.ebicep.warlords.abilties.OrbsOfLife;
import com.ebicep.warlords.abilties.RecklessCharge;
import com.ebicep.warlords.abilties.Soulbinding;
import com.ebicep.warlords.abilties.UndyingArmy;
import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.abilties.internal.HealingPowerup;
import com.ebicep.warlords.abilties.internal.Overheal;
import com.ebicep.warlords.commands.CommandManager;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.configuration.ApplicationConfiguration;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.events.WarlordsEvents;
import com.ebicep.warlords.game.*;
import com.ebicep.warlords.game.option.FlagSpawnPointOption;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.marker.FlagHolder;
import com.ebicep.warlords.guilds.GuildManager;
import com.ebicep.warlords.menu.MenuEventListener;
import com.ebicep.warlords.menu.PlayerHotBarItemListener;
import com.ebicep.warlords.party.PartyListener;
import com.ebicep.warlords.player.general.CustomScoreboard;
import com.ebicep.warlords.player.general.PlayerSettings;
import com.ebicep.warlords.player.general.SkillBoosts;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PersistentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.events.mastersworkfair.MasterworksFairManager;
import com.ebicep.warlords.util.bukkit.*;
import com.ebicep.warlords.util.bukkit.signgui.SignGUI;
import com.ebicep.warlords.util.chat.ChatChannels;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import net.minecraft.server.v1_8_R3.PacketPlayInSteerVehicle;
import org.bukkit.GameMode;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.security.auth.login.LoginException;
import java.io.File;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.ebicep.warlords.util.warlords.Utils.iterable;

public class Warlords extends JavaPlugin {
    public static final HashMap<UUID, Location> SPAWN_POINTS = new HashMap<>();
    public static final ConcurrentHashMap<UUID, ChatChannels> PLAYER_CHAT_CHANNELS = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<UUID, CustomScoreboard> PLAYER_SCOREBOARDS = new ConcurrentHashMap<>();
    public static final AtomicInteger LOOP_TICK_COUNTER = new AtomicInteger(0);
    private static final HashMap<UUID, WarlordsEntity> PLAYERS = new HashMap<>();
    public static String VERSION = "";
    public static String serverIP;
    public static boolean holographicDisplaysEnabled;
    public static boolean citizensEnabled;
    private static Warlords instance;
    private static TaskChainFactory taskChainFactory;

    static {
        ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger("org.mongodb.driver")).setLevel(ch.qos.logback.classic.Level.ERROR);
        ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger("org.springframework")).setLevel(ch.qos.logback.classic.Level.ERROR);
        ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger("net.dv8tion.jda")).setLevel(ch.qos.logback.classic.Level.ERROR);
    }

    private GameManager gameManager;

    public static GameManager getGameManager() {
        return getInstance().gameManager;
    }

    public static <T> TaskChain<T> newChain() {
        return taskChainFactory.newChain();
    }

    public static <T> TaskChain<T> newSharedChain(String name) {
        return taskChainFactory.newSharedChain(name);
    }

    public static HashMap<UUID, WarlordsEntity> getPlayers() {
        return PLAYERS;
    }

    public static void addPlayer(@Nonnull WarlordsEntity warlordsEntity) {
        PLAYERS.put(warlordsEntity.getUuid(), warlordsEntity);
        for (GameAddon addon : warlordsEntity.getGame().getAddons()) {
            addon.warlordsEntityCreated(warlordsEntity.getGame(), warlordsEntity);
        }
        for (Option option : warlordsEntity.getGame().getOptions()) {
            option.onWarlordsEntityCreated(warlordsEntity);
        }
    }

    @Nullable
    public static WarlordsEntity getPlayer(@Nullable Entity entity) {
        if (entity != null) {
            Optional<MetadataValue> metadata = entity.getMetadata("WARLORDS_PLAYER")
                    .stream()
                    .filter(e -> e.value() instanceof WarlordsEntity)
                    .findAny();
            if (metadata.isPresent()) {
                return (WarlordsEntity) metadata.get().value();
            }
        }
        return null;
    }

    @Nullable
    public static WarlordsEntity getPlayer(@Nullable Player player) {
        return getPlayer((OfflinePlayer) player);
    }

    @Nullable
    public static WarlordsEntity getPlayer(@Nullable OfflinePlayer player) {
        return player == null ? null : getPlayer(player.getUniqueId());
    }

    @Nullable
    public static WarlordsEntity getPlayer(@Nonnull UUID uuid) {
        return PLAYERS.get(uuid);
    }

    public static void removePlayer(@Nonnull UUID player) {
        WarlordsEntity wp = PLAYERS.remove(player);
        if (wp != null) {
            wp.onRemove();
        }
        Location loc = SPAWN_POINTS.remove(player);
        Player p = Bukkit.getPlayer(player);
        if (p != null) {
            p.removeMetadata("WARLORDS_PLAYER", Warlords.getInstance());
            if (loc != null) {
                p.teleport(getRejoinPoint(player));
            }
        }
    }

    public static boolean hasPlayer(@Nonnull OfflinePlayer player) {
        return hasPlayer(player.getUniqueId());
    }

    public static boolean hasPlayer(@Nonnull UUID player) {
        return PLAYERS.containsKey(player);
    }

    public static boolean onCustomServer() {
        return !serverIP.equals("51.81.49.127");
    }

    public static Warlords getInstance() {
        return instance;
    }

    @Nonnull
    public static Location getRejoinPoint(@Nonnull UUID key) {
        return SPAWN_POINTS.getOrDefault(key, new LocationBuilder(Bukkit.getWorlds().get(0).getSpawnLocation()).yaw(-90).get());
    }

    public static void setRejoinPoint(@Nonnull UUID key, @Nonnull Location value) {
        SPAWN_POINTS.put(key, value);
        Player player = Bukkit.getPlayer(key);
        if (player != null) {
            player.teleport(value);
        }
    }


    @Override
    public void onDisable() {
        if (BotManager.task != null) {
            BotManager.task.cancel();
        }
        try {
            //updates all queues, locks main thread to ensure update is complete before disabling
            if (DatabaseManager.enabled) {
                DatabaseManager.updateQueue();
                if (MasterworksFairManager.currentFair != null) {
                    DatabaseManager.masterworksFairService.update(MasterworksFairManager.currentFair);
                }
                GuildManager.updateGuilds();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            taskChainFactory.shutdown(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            // Pre-caution
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.removePotionEffect(PotionEffectType.BLINDNESS);
                player.getActivePotionEffects().clear();
                player.removeMetadata("WARLORDS_PLAYER", this);
                PacketUtils.sendTitle(player, "", "", 0, 0, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            CraftServer server = (CraftServer) Bukkit.getServer();
            server.getEntityMetadata().invalidateAll(this);
            server.getWorldMetadata().invalidateAll(this);
            server.getPlayerMetadata().invalidateAll(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            gameManager.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (holographicDisplaysEnabled) {
                HolographicDisplaysAPI.get(instance).getHolograms().forEach(Hologram::delete);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            NPCManager.destroyNPCs();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Bukkit.getWorld("MainLobby").getEntities().stream()
                    .filter(entity -> entity.getName().equals("capture-the-flag"))
                    .filter(entity -> entity.getName().equals("pve-mode"))
                    .forEach(Entity::remove);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            BotManager.deleteStatusMessage();
            if (BotManager.jda != null) {
                BotManager.jda.shutdownNow();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            SignGUI.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ChatUtils.MessageTypes.WARLORDS.sendMessage("Plugin is disabled");
    }

    @Override
    public void onEnable() {
        instance = this;
        VERSION = this.getDescription().getVersion();
        serverIP = this.getServer().getIp();
        taskChainFactory = BukkitTaskChainFactory.create(this);

        gameManager = new GameManager();

        /*
         * Each map instance presents a game server, every map can hold up to 3 games at once.
         *
         * Adding a new map must start with -0 at the end and increment from there on out.
         *
         * Adding additional game servers will require a config update in @see MultiWorld Plugin
         */

        // CTF
        gameManager.addGameHolder("Rift-0", GameMap.RIFT, new LocationFactory(Bukkit.getWorld("Rift-0")));
        gameManager.addGameHolder("Rift-1", GameMap.RIFT, new LocationFactory(Bukkit.getWorld("Rift-1")));
        gameManager.addGameHolder("Rift-2", GameMap.RIFT, new LocationFactory(Bukkit.getWorld("Rift-2")));

        gameManager.addGameHolder("Crossfire-0", GameMap.CROSSFIRE, new LocationFactory(Bukkit.getWorld("Crossfire-0")));
        gameManager.addGameHolder("Crossfire-1", GameMap.CROSSFIRE, new LocationFactory(Bukkit.getWorld("Crossfire-1")));
        gameManager.addGameHolder("Crossfire-2", GameMap.CROSSFIRE, new LocationFactory(Bukkit.getWorld("Crossfire-2")));

        gameManager.addGameHolder("Valley-0", GameMap.VALLEY, new LocationFactory(Bukkit.getWorld("Atherrough_Valley-0")));
        gameManager.addGameHolder("Valley-1", GameMap.VALLEY, new LocationFactory(Bukkit.getWorld("Atherrough_Valley-1")));
        gameManager.addGameHolder("Valley-2", GameMap.VALLEY, new LocationFactory(Bukkit.getWorld("Atherrough_Valley-2")));

        gameManager.addGameHolder("Warsong-0", GameMap.WARSONG, new LocationFactory(Bukkit.getWorld("Warsong-0")));
        gameManager.addGameHolder("Warsong-1", GameMap.WARSONG, new LocationFactory(Bukkit.getWorld("Warsong-1")));
        gameManager.addGameHolder("Warsong-2", GameMap.WARSONG, new LocationFactory(Bukkit.getWorld("Warsong-2")));

        gameManager.addGameHolder("Aperture-0", GameMap.APERTURE, new LocationFactory(Bukkit.getWorld("Aperture-0")));
        gameManager.addGameHolder("Aperture-1", GameMap.APERTURE, new LocationFactory(Bukkit.getWorld("Aperture-1")));
        gameManager.addGameHolder("Aperture-2", GameMap.APERTURE, new LocationFactory(Bukkit.getWorld("Aperture-2")));

        gameManager.addGameHolder("Arathi-0", GameMap.ARATHI, new LocationFactory(Bukkit.getWorld("Arathi-0")));
        gameManager.addGameHolder("Arathi-1", GameMap.ARATHI, new LocationFactory(Bukkit.getWorld("Arathi-1")));
        gameManager.addGameHolder("Arathi-2", GameMap.ARATHI, new LocationFactory(Bukkit.getWorld("Arathi-2")));

        // TDM
        gameManager.addGameHolder("Siege-0", GameMap.SIEGE, new LocationFactory(Bukkit.getWorld("Siege-0")));
        gameManager.addGameHolder("Ruins-0", GameMap.RUINS, new LocationFactory(Bukkit.getWorld("Ruins-0")));
        gameManager.addGameHolder("FalstadGate-0", GameMap.FALSTAD_GATE, new LocationFactory(Bukkit.getWorld("FalstadGate-0")));
        gameManager.addGameHolder("Stormwind-0", GameMap.STORMWIND, new LocationFactory(Bukkit.getWorld("Stormwind-0")));
        gameManager.addGameHolder("BlackTemple-0", GameMap.BLACK_TEMPLE, new LocationFactory(Bukkit.getWorld("BlackTemple-0")));

        // DOM
        gameManager.addGameHolder("SunAndMoon-0", GameMap.SUN_AND_MOON, new LocationFactory(Bukkit.getWorld("SunAndMoon-0")));
        gameManager.addGameHolder("Phantom-0", GameMap.PHANTOM, new LocationFactory(Bukkit.getWorld("Phantom-0")));
        gameManager.addGameHolder("Neolithic-0", GameMap.NEOLITHIC, new LocationFactory(Bukkit.getWorld("Neolithic-0")));
        gameManager.addGameHolder("DorivenBasin-0", GameMap.DORIVEN_BASIN, new LocationFactory(Bukkit.getWorld("DorivenBasin-0")));
        gameManager.addGameHolder("DeathValley-0", GameMap.DEATH_VALLEY, new LocationFactory(Bukkit.getWorld("DeathValley-0")));
        //gameManager.addGameHolder("TheVale-0", GameMap.THE_VALE, new LocationFactory(Bukkit.getWorld("TheVale-0")));

        // DUEL
        gameManager.addGameHolder("Heaven-0", GameMap.HEAVEN_WILL, new LocationFactory(Bukkit.getWorld("Heaven-0")));

        // WAVE DEFENSE
        gameManager.addGameHolder("IllusionRift-0", GameMap.ILLUSION_RIFT, new LocationFactory(Bukkit.getWorld("IllusionRift-0")));
        //gameManager.addGameHolder("IllusionRift-1", GameMap.ILLUSION_RIFT, new LocationFactory(Bukkit.getWorld("IllusionRift-1")));
        //gameManager.addGameHolder("IllusionRift-2", GameMap.ILLUSION_RIFT, new LocationFactory(Bukkit.getWorld("IllusionRift-2")));

        gameManager.addGameHolder("IllusionAperture-0", GameMap.ILLUSION_APERTURE, new LocationFactory(Bukkit.getWorld("IllusionAperture-0")));
        //gameManager.addGameHolder("IllusionAperture-2", GameMap.ILLUSION_APERTURE, new LocationFactory(Bukkit.getWorld("IllusionAperture-1")));
        //gameManager.addGameHolder("IllusionAperture-2", GameMap.ILLUSION_APERTURE, new LocationFactory(Bukkit.getWorld("IllusionAperture-2")));

        gameManager.addGameHolder("IllusionCrossfire-0", GameMap.ILLUSION_CROSSFIRE, new LocationFactory(Bukkit.getWorld("IllusionCrossfire-0")));
        //gameManager.addGameHolder("IllusionCrossfire-1", GameMap.ILLUSION_CROSSFIRE, new LocationFactory(Bukkit.getWorld("IllusionCrossfire-1")));
        //gameManager.addGameHolder("IllusionCrossfire-2", GameMap.ILLUSION_CROSSFIRE, new LocationFactory(Bukkit.getWorld("IllusionCrossfire-2")));

        // PRACTICE
        gameManager.addGameHolder("Debug-0", GameMap.DEBUG, new LocationFactory(Bukkit.getWorld("WLDebug-0")));
        gameManager.addGameHolder("Debug-1", GameMap.DEBUG, new LocationFactory(Bukkit.getWorld("WLDebug-1")));
        gameManager.addGameHolder("Debug-2", GameMap.DEBUG, new LocationFactory(Bukkit.getWorld("WLDebug-2")));

        Thread.currentThread().setContextClassLoader(getClassLoader());

        getServer().getPluginManager().registerEvents(new WarlordsEvents(), this);
        getServer().getPluginManager().registerEvents(new MenuEventListener(this), this);
        getServer().getPluginManager().registerEvents(new PartyListener(), this);
        getServer().getPluginManager().registerEvents(new BotListener(), this);
        getServer().getPluginManager().registerEvents(new RecklessCharge(), this);
        getServer().getPluginManager().registerEvents(new PlayerHotBarItemListener(), this);

        CommandManager.init(this);

        HeadUtils.updateHeads();

        readKeysConfig();
        readWeaponConfig();
        saveWeaponConfig();

        TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"));

        holographicDisplaysEnabled = Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays");
        citizensEnabled = Bukkit.getPluginManager().isPluginEnabled("Citizens");

        Bukkit.getOnlinePlayers().forEach(player -> {
            player.teleport(getRejoinPoint(player.getUniqueId()));
            player.getInventory().clear();
            PLAYER_SCOREBOARDS.put(player.getUniqueId(), new CustomScoreboard(player));
            PlayerHotBarItemListener.giveLobbyHotBar(player, false);
        });

        //connects to the database
        Warlords.newChain()
                .async(DatabaseManager::init)
                .execute();

        if (!onCustomServer()) {
            try {
                BotManager.connect();
            } catch (LoginException e) {
                e.printStackTrace();
            }
        }

        ProtocolManager protocolManager;
        protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.removePacketListeners(this);
        protocolManager.addPacketListener(
                new PacketAdapter(this, ListenerPriority.HIGHEST, PacketType.Play.Server.WORLD_PARTICLES) {
                    int counter = 0;

                    @Override
                    public void onPacketSending(PacketEvent event) {
                        // Item packets (id: 0x29)
                        if (event.getPacketType() == PacketType.Play.Server.WORLD_PARTICLES) {
                            Player player = event.getPlayer();
                            if (Warlords.hasPlayer(player)) {
                                if (counter++ % PlayerSettings.PLAYER_SETTINGS.get(player.getUniqueId()).getParticleQuality().particleReduction == 0) {
                                    event.setCancelled(true);
                                }
                            }
                        }
                    }
                });
        protocolManager.addPacketListener(
                new PacketAdapter(this, ListenerPriority.LOWEST, PacketType.Play.Client.STEER_VEHICLE) {
                    @Override
                    public void onPacketReceiving(PacketEvent e) {
                        if (e.getPacketType() == PacketType.Play.Client.STEER_VEHICLE) {
                            if (e.getPacket().getHandle() instanceof PacketPlayInSteerVehicle) {
                                boolean dismount = e.getPacket().getBooleans().read(1);
                                Field f;
                                try {
                                    f = PacketPlayInSteerVehicle.class.getDeclaredField("d");
                                    f.setAccessible(true);
                                    f.set(e.getPacket().getHandle(), false);
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                                if (dismount && e.getPlayer().getVehicle() != null) {
                                    e.getPlayer().getVehicle().remove();
                                }
                            }
                        }
                    }
                }
        );

        SignGUI.init(this);

        CustomEntitiesRegistry.registerEntities();

        Warlords.newChain()
                .sync(NPCManager::createSupplyDropFairNPC)
                .execute();

        startMainLoop();

        //Sending data to mod
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(this, "Warlords");

        ChatUtils.MessageTypes.WARLORDS.sendMessage("Plugin is enabled");
    }

    public void readKeysConfig() {
        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "keys.yml"));
            ApplicationConfiguration.key = config.getString("database_key");
            BotManager.botToken = config.getString("botToken");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readWeaponConfig() {
        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "weapons.yml"));
            for (String key : config.getKeys(false)) {
                Weapons.getWeapon(key).isUnlocked = config.getBoolean(key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveWeaponConfig() {
        try {
            YamlConfiguration config = new YamlConfiguration();
            for (Weapons weapons : Weapons.VALUES) {
                config.set(weapons.getName(), weapons.isUnlocked);
            }
            config.save(new File(this.getDataFolder(), "weapons.yml"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void startMainLoop() {
        new BukkitRunnable() {

            @Override
            public void run() {
                // Every 1 tick - 0.05 seconds.
                for (WarlordsEntity wp : PLAYERS.values()) {
                    Player player = wp.getEntity() instanceof Player ? (Player) wp.getEntity() : null;
                    if (player != null) {
                        //ACTION BAR
                        if (!player.getInventory().getItemInHand().equals(FlagSpawnPointOption.COMPASS)) {
                            wp.displayActionBar();
                        } else {
                            wp.displayFlagActionBar(player);
                        }
                    }

                    // Checks whether the game is paused.
                    if (wp.getGame().isFrozen()) {
                        continue;
                    }

                    wp.updateHealth();
                    // Updating all player speed.
                    wp.getSpeed().updateSpeed();
                    wp.runEveryTick();

                    // Setting the flag tracking compass.
                    if (player != null && wp.getCompassTarget() != null) {
                        player.setCompassTarget(wp.getCompassTarget().getLocation());
                    }

                    // Ability Cooldowns

                    // Decrementing red skill's cooldown.
                    if (wp.getSpec().getRed().getCurrentCooldown() > 0) {
                        wp.getSpec().getRed().subtractCooldown(.05f);
                        if (player != null) {
                            wp.updateRedItem(player);
                        }
                    }

                    // Decrementing purple skill's cooldown.
                    if (wp.getSpec().getPurple().getCurrentCooldown() > 0) {
                        wp.getSpec().getPurple().subtractCooldown(.05f);
                        if (player != null) {
                            wp.updatePurpleItem(player);
                        }
                    }

                    // Decrementing blue skill's cooldown.
                    if (wp.getSpec().getBlue().getCurrentCooldown() > 0) {
                        wp.getSpec().getBlue().subtractCooldown(.05f);
                        if (player != null) {
                            wp.updateBlueItem(player);
                        }
                    }

                    // Decrementing orange skill's cooldown.
                    if (wp.getSpec().getOrange().getCurrentCooldown() > 0) {
                        wp.getSpec().getOrange().subtractCooldown(.05f);
                        if (player != null) {
                            wp.updateOrangeItem(player);
                        }
                    }

                    wp.getCooldownManager().reduceCooldowns();

                    for (AbstractAbility ability : wp.getSpec().getAbilities()) {
                        ability.checkSecondaryAbilities();

                        if (wp.isSneaking() && !wp.isWasSneaking()) {
                            ability.runSecondAbilities();
                        }
                    }

                    wp.setWasSneaking(wp.isSneaking());

                    // Checks whether the player has overheal active and is full health or not.
                    boolean hasOverhealCooldown = wp.getCooldownManager().hasCooldown(Overheal.OVERHEAL_MARKER);
                    boolean hasTooMuchHealth = wp.getHealth() > wp.getMaxHealth();

                    if (hasOverhealCooldown && !hasTooMuchHealth) {
                        wp.getCooldownManager().removeCooldownByObject(Overheal.OVERHEAL_MARKER);
                    }

                    if (!hasOverhealCooldown && hasTooMuchHealth) {
                        wp.setHealth(wp.getMaxHealth());
                    }

                    // Checks whether the displayed health can be above or under 40 health total. (20 hearts.)
                    float newHealth = wp.getHealth() / wp.getMaxHealth() * 40;

                    if (newHealth < 0) {
                        newHealth = 0;
                    } else if (newHealth > 40) {
                        newHealth = 40;
                    }

                    // Checks whether the player has any remaining active Undying Army instances active.
                    if (wp.getCooldownManager().checkUndyingArmy(false) && newHealth <= 0) {

                        for (RegularCooldown<?> undyingArmyCooldown : new CooldownFilter<>(wp, RegularCooldown.class)
                                .filterCooldownClass(UndyingArmy.class)
                                .stream()
                                .collect(Collectors.toList())
                        ) {
                            UndyingArmy undyingArmy = (UndyingArmy) undyingArmyCooldown.getCooldownObject();
                            if (!undyingArmy.isArmyDead(wp)) {
                                undyingArmy.pop(wp);

                                // Drops the flag when popped.
                                FlagHolder.dropFlagForPlayer(wp);

                                // Sending the message + check if getFrom is self
                                if (undyingArmyCooldown.getFrom() == wp) {
                                    wp.sendMessage("§a\u00BB§7 " +
                                            ChatColor.LIGHT_PURPLE +
                                            "Your Undying Army revived you with temporary health. Fight until your death! Your health will decay by " +
                                            ChatColor.RED +
                                            (wp.getMaxHealth() / 10) +
                                            ChatColor.LIGHT_PURPLE +
                                            " every second."
                                    );
                                } else {
                                    wp.sendMessage("§a\u00BB§7 " +
                                            ChatColor.LIGHT_PURPLE + undyingArmyCooldown.getFrom().getName() +
                                            "'s Undying Army revived you with temporary health. Fight until your death! Your health will decay by " +
                                            ChatColor.RED +
                                            (wp.getMaxHealth() / 10) +
                                            ChatColor.LIGHT_PURPLE +
                                            " every second."
                                    );
                                }

                                FireWorkEffectPlayer.playFirework(wp.getLocation(), FireworkEffect.builder()
                                        .withColor(Color.LIME)
                                        .with(FireworkEffect.Type.BALL)
                                        .build());

                                wp.heal();

                                if (player != null) {
                                    player.getWorld().spigot().strikeLightningEffect(wp.getLocation(), false);
                                    player.getInventory().setItem(5, UndyingArmy.BONE);
                                }
                                newHealth = 40;

                                //gives 50% of max energy if player is less than half
                                if (wp.getEnergy() < wp.getMaxEnergy() / 2) {
                                    wp.setEnergy(wp.getMaxEnergy() / 2);
                                }

                                new GameRunnable(wp.getGame()) {
                                    @Override
                                    public void run() {
                                        if (wp.getRespawnTimer() >= 0 || wp.isDead()) {
                                            this.cancel();
                                        } else {
                                            wp.addDamageInstance(
                                                    wp,
                                                    "",
                                                    wp.getMaxHealth() * (undyingArmy.getMaxHealthDamage() / 100f),
                                                    wp.getMaxHealth() * (undyingArmy.getMaxHealthDamage() / 100f),
                                                    -1,
                                                    100,
                                                    false
                                            );
                                        }
                                    }
                                }.runTaskTimer(0, 20);

                                break;
                            }
                        }
                    }

                    // Energy
                    if (wp.getEnergy() < wp.getMaxEnergy()) {
                        // Standard energy value per second.
                        float energyGainPerTick = wp.getSpec().getEnergyPerSec() / 20;

                        for (AbstractCooldown<?> abstractCooldown : wp.getCooldownManager().getCooldownsDistinct()) {
                            energyGainPerTick = abstractCooldown.addEnergyGainPerTick(energyGainPerTick);
                        }
                        for (AbstractCooldown<?> abstractCooldown : wp.getCooldownManager().getCooldownsDistinct()) {
                            energyGainPerTick = abstractCooldown.multiplyEnergyGainPerTick(energyGainPerTick);
                        }

                        // Setting energy gain to the value after all ability instance multipliers have been applied.
                        float newEnergy = wp.getEnergy() + energyGainPerTick;
                        if (newEnergy > wp.getMaxEnergy()) {
                            newEnergy = wp.getMaxEnergy();
                        }

                        wp.setEnergy(newEnergy);
                    }

                    if (player != null) {
                        //precaution
                        if (newHealth > 0) {
                            player.setHealth(newHealth);
                        }

                        // Respawn fix for when a player is stuck or leaves the game.
                        if (wp.getHealth() <= 0 && player.getGameMode() == GameMode.SPECTATOR) {
                            wp.heal();
                        }

                        // Checks whether the player has under 0 energy to avoid infinite energy bugs.
                        if (wp.getEnergy() < 0) {
                            wp.setEnergy(1);
                        }
                        player.setLevel((int) wp.getEnergy());
                        player.setExp(wp.getEnergy() / wp.getMaxEnergy());

                        // Saves the amount of blocks travelled per player.
                        wp.setBlocksTravelledCM(Utils.getPlayerMovementStatistics(player));
                    }

                    // Melee Cooldown
                    if (wp.getHitCooldown() > 0) {
                        wp.setHitCooldown(wp.getHitCooldown() - 1);
                    }

                    // Orbs of Life
                    Location playerPosition = wp.getLocation();
                    List<OrbsOfLife.Orb> orbs = new ArrayList<>();
                    PlayerFilter.playingGame(wp.getGame()).teammatesOf(wp).forEach(p -> {
                        new CooldownFilter<>(p, PersistentCooldown.class)
                                .filterCooldownClassAndMapToObjectsOfClass(OrbsOfLife.class)
                                .forEachOrdered(orbsOfLife -> orbs.addAll(orbsOfLife.getSpawnedOrbs()));
                    });

                    Iterator<OrbsOfLife.Orb> itr = orbs.iterator();

                    while (itr.hasNext()) {
                        OrbsOfLife.Orb orb = itr.next();
                        Location orbPosition = orb.getArmorStand().getLocation();
                        if ((orb.getPlayerToMoveTowards() == null || (orb.getPlayerToMoveTowards() != null && orb.getPlayerToMoveTowards() == wp)) &&
                                orbPosition.distanceSquared(playerPosition) < 1.35 * 1.35 && !wp.isDead()) {

                            orb.remove();
                            itr.remove();

                            float orbHeal = OrbsOfLife.ORB_HEALING;
                            if (PlayerSettings.getPlayerSettings(orb.getOwner().getUuid()).getSkillBoostForClass() == SkillBoosts.ORBS_OF_LIFE) {
                                orbHeal *= 1.2;
                            }

                            // Increasing heal for low long orb lived for (up to +25%)
                            // 6.5 seconds = 130 ticks
                            // 6.5 seconds = 1 + (130/325) = 1.4
                            // 225 *= 1.4 = 315
                            if (orb.getPlayerToMoveTowards() == null) {
                                orbHeal *= 1 + orb.getTicksLived() / 325f;
                            }

                            wp.addHealingInstance(orb.getOwner(), "Orbs of Life", orbHeal, orbHeal, -1, 100, false, false);
                            if (player != null) {
                                Utils.playGlobalSound(player.getLocation(), Sound.ORB_PICKUP, 0.2f, 1);
                            }

                            for (WarlordsEntity nearPlayer : PlayerFilter
                                    .entitiesAround(wp, 6, 6, 6)
                                    .aliveTeammatesOfExcludingSelf(wp)
                                    .limit(2)
                            ) {
                                nearPlayer.addHealingInstance(orb.getOwner(), "Orbs of Life", orbHeal, orbHeal, -1, 100, false, false);
                                if (player != null) {
                                    Utils.playGlobalSound(player.getLocation(), Sound.ORB_PICKUP, 0.2f, 1);
                                }
                            }
                        }

                        // Checks whether the Orb of Life has lived for 8 seconds.
                        if (orb.getTicksLived() > 160 || (orb.getPlayerToMoveTowards() != null && orb.getPlayerToMoveTowards().isDead())) {
                            orb.remove();
                            itr.remove();
                        }
                    }
                }

                // Loops every 10 ticks - .5 second.
                if (LOOP_TICK_COUNTER.get() % 10 == 0) {
                    for (WarlordsEntity wps : PLAYERS.values()) {
                        // Soulbinding Weapon - decrementing time left on the ability.
                        new CooldownFilter<>(wps, PersistentCooldown.class)
                                .filterCooldownClassAndMapToObjectsOfClass(Soulbinding.class)
                                .forEachOrdered(soulbinding -> soulbinding.getSoulBindedPlayers().forEach(Soulbinding.SoulBoundPlayer::decrementTimeLeft));
                        // Soulbinding Weapon - Removing bound players.
                        new CooldownFilter<>(wps, PersistentCooldown.class)
                                .filterCooldownClassAndMapToObjectsOfClass(Soulbinding.class)
                                .forEachOrdered(soulbinding -> soulbinding.getSoulBindedPlayers()
                                        .removeIf(boundPlayer -> boundPlayer.getTimeLeft() == 0 || (boundPlayer.isHitWithSoul() && boundPlayer.isHitWithLink())));
                    }
                }

                // Loops every 20 ticks - 1 second.
                if (LOOP_TICK_COUNTER.get() % 20 == 0) {

                    // Removes leftover horses if there are any.
                    RemoveEntities.removeHorsesInGame();

                    for (WarlordsEntity wps : PLAYERS.values()) {
                        // Checks whether the game is paused.
                        if (wps.getGame().isFrozen()) {
                            continue;
                        }
                        wps.runEverySecond();

                        Player player = wps.getEntity() instanceof Player ? (Player) wps.getEntity() : null;

                        // Natural Regen
                        if (wps.getRegenTimer() != 0) {
                            if (wps instanceof WarlordsPlayer) {
                                wps.setRegenTimer(wps.getRegenTimer() - 1);
                            }
                            if (wps.getRegenTimer() == 0) {
                                wps.getHitBy().clear();
                            }
                        } else {
                            int healthToAdd = (int) (wps.getMaxHealth() / 55.3);
                            wps.setHealth(Math.max(wps.getHealth(),
                                    Math.min(wps.getHealth() + healthToAdd,
                                            wps.getMaxHealth()
                                    )
                            ));
                        }

                        // Cooldowns

                        // Checks whether the player has a flag cooldown.
                        if (wps.getFlagDropCooldown() > 0) {
                            wps.setFlagDropCooldown(wps.getFlagDropCooldown() - 1);
                        }
                        if (wps.getFlagPickCooldown() > 0) {
                            wps.setFlagPickCooldown(wps.getFlagPickCooldown() - 1);
                        }

                        // Checks whether the player has the healing powerup active.
                        if (wps.getCooldownManager().hasCooldown(HealingPowerup.class)) {
                            float heal = wps.getMaxHealth() * .08f;
                            if (wps.getHealth() + heal > wps.getMaxHealth()) {
                                heal = wps.getMaxHealth() - wps.getHealth();
                            }

                            if (heal != 0) {
                                wps.setHealth(wps.getHealth() + heal);
                                wps.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN + " §7Healed §a" + heal + " §7health.");
                            }
                        }

                        // Combat Timer - Logs combat time after 4 seconds.
                        if (wps.getRegenTimer() > 6) {
                            wps.getMinuteStats().addTimeInCombat();
                        }

                        // Assists - 10 seconds timer.
                        wps.getHitBy().replaceAll((wp, integer) -> integer - 1);
                        wps.getHealedBy().replaceAll((wp, integer) -> integer - 1);
                        wps.getHitBy().entrySet().removeIf(p -> p.getValue() <= 0);
                        wps.getHealedBy().entrySet().removeIf(p -> p.getValue() <= 0);
                    }

                    WarlordsEvents.entityList.removeIf(e -> !e.isValid());
                }

                // Loops every 50 ticks - 2.5 seconds.
                if (LOOP_TICK_COUNTER.get() % 50 == 0) {
                    for (WarlordsEntity warlordsPlayer : PLAYERS.values()) {

                        if (warlordsPlayer.getGame().isFrozen()) {
                            continue;
                        }

                        LivingEntity player = warlordsPlayer.getEntity();
                        List<Location> locations = warlordsPlayer.getLocations();

                        if (warlordsPlayer.isDead() && !locations.isEmpty()) {
                            locations.add(locations.get(locations.size() - 1));
                        } else {
                            locations.add(player.getLocation());
                        }
                    }
                }

                LOOP_TICK_COUNTER.getAndIncrement();
            }

        }.runTaskTimer(this, 0, 0);
    }


    public void hideAndUnhidePeople(@Nonnull Player player) {
        Map<UUID, Game> players = getPlayersToGame();
        Game game = players.get(player.getUniqueId());
        for (Player p : Bukkit.getOnlinePlayers()) {
            Game game1 = players.get(p.getUniqueId());
            if (p != player) {
                if (game1 == game) {
                    p.showPlayer(player);
                    player.showPlayer(p);
                } else {
                    p.hidePlayer(player);
                    player.hidePlayer(p);
                }
            }
        }
    }

    public void hideAndUnhidePeople() {
        Map<UUID, Game> players = getPlayersToGame();
        List<Player> peeps = new ArrayList<>(Bukkit.getOnlinePlayers());
        int length = peeps.size();
        for (int i = 0; i < length - 1; i++) {
            Player player = peeps.get(i);
            Game game = players.get(player.getUniqueId());
            for (int j = i + 1; j < length; j++) {
                Player p = peeps.get(j);
                Game game1 = players.get(p.getUniqueId());
                if (game1 == game) {
                    p.showPlayer(player);
                    player.showPlayer(p);
                } else {
                    p.hidePlayer(player);
                    player.hidePlayer(p);
                }
            }
        }
    }

    private Map<UUID, Game> getPlayersToGame() {
        Map<UUID, Game> players = new HashMap<>();
        for (GameManager.GameHolder holder : gameManager.getGames()) {
            Game game = holder.getGame();
            if (game != null) {
                //Stream<Map.Entry<UUID, Team>> players()
                for (Map.Entry<UUID, Team> e : iterable(game.players())) {
                    players.put(e.getKey(), game);
                }
            }
        }
        return players;
    }

}
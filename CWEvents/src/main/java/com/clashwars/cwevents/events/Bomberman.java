package com.clashwars.cwevents.events;

import com.clashwars.cwcore.dependencies.CWWorldGuard;
import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.cwevents.Util;
import com.clashwars.cwevents.events.extra.BombermanData;
import com.clashwars.cwevents.events.internal.BaseEvent;
import com.clashwars.cwevents.events.internal.EventStatus;
import com.clashwars.cwevents.events.internal.EventType;
import com.clashwars.cwevents.runnables.BombRunnable;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.FilenameException;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Bomberman extends BaseEvent {
    private Random random = new Random();

    private List<Material> floorBlocks = Arrays.asList(Material.SANDSTONE);
    private List<Material> destroyBlocks = Arrays.asList(Material.SAND, Material.COBBLESTONE);

    private BlockFace[] directions = new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};

    private HashMap<String, BombermanData> bombData = new HashMap<String, BombermanData>();
    private HashMap<String, ItemStack> powerups = new HashMap<String, ItemStack>();

    private ItemStack bombItem;
    private int bombsToRemove = 0;

    public Bomberman() {
        bombItem = new CWItem(Material.TNT, 1, (short) 0, "&4&lBomb", new String[]
                {"&7Place this on the ground to create an explosion.", "&7Bombs will will slowly regenerate based on ur stats."});

        powerups.put("life", new CWItem(Material.GOLDEN_APPLE, 1, (short) 1, "&6&lExtra Life", new String[]{"&7You get one extra life!"}));
        powerups.put("bombUp", new CWItem(Material.TNT, 1, (short) 0, "&a&l+1 Bomb", new String[]{"&7You get one extra bomb!"}));
        powerups.put("bombDown", new CWItem(Material.SULPHUR, 1, (short) 0, "&c&l-1 Bomb", new String[]{"&7You lose one bomb!"}));
        powerups.put("fuseTimeDown", new CWItem(Material.REDSTONE_TORCH_ON, 1, (short) 0, "&a&l-1s FuseTime", new String[]{"&7Bombs will take 1 less second to explode."}));
        powerups.put("fuseTimeUp", new CWItem(Material.STRING, 1, (short) 0, "&c&l+1s FuseTime", new String[]{"&7Bombs will take 1 more second to explode."}));
        powerups.put("speed", new CWItem(Material.GOLD_BOOTS, 1, (short) 0, "&b&lSpeed", new String[]{"&7You get 1 extra speed."}));
        powerups.put("slow", new CWItem(Material.LEATHER_BOOTS, 1, (short) 0, "&c&lSlow", new String[]{"&7You lose 1 extra speed."}));
        powerups.put("powerUp", new CWItem(Material.BLAZE_POWDER, 1, (short) 0, "&4&l+1 Power", new String[]{"&7Bombs will explode 1 block further."}));
        powerups.put("powerDown", new CWItem(Material.QUARTZ, 1, (short) 0, "&c&l-1 Power", new String[]{"&7Bombs will explode 1 block less."}));
        powerups.put("pierce", new CWItem(Material.ARROW, 1, (short) 0, "&e&lPierce", new String[]{"&7Bombs will blow all blocks in range up."}));
        powerups.put("shield", new CWItem(Material.IRON_CHESTPLATE, 1, (short) 0, "&5&lShield", new String[]{"&7Can't be killed by bombs for 8 seconds."}));
        powerups.put("blind", new CWItem(Material.COAL, 1, (short) 1, "&8&lBlind", new String[]{"&7All players will be blinded."}));
    }

    public boolean checkSetup(EventType event, String arena, CommandSender sender) {
        String name = em.getRegionName(event, arena, "arena");
        if (CWWorldGuard.getRegion(world, name) == null) {
            sender.sendMessage(Util.formatMsg("&cInvalid arena name or region not set properly. &7Missing region &8'&4" + name + "&8'&7!"));
            return false;
        }
        for (int i = 1; i <= 12; i++) {
            name = em.getRegionName(event, arena, "s" + i);
            if (!cwe.getLocConfig().getLocations().containsKey(name)) {
                sender.sendMessage(Util.formatMsg("&cInvalid arena name or locations not set properly. &7Missing location &8'&4" + name + "&8'&7!"));
                return false;
            }
        }
        name = em.getRegionName(event, arena, "schem_arena");
        if (!cwe.getLocConfig().getLocations().containsKey(name)) {
            sender.sendMessage(Util.formatMsg("&cInvalid arena name or locations not set properly. &7Missing location &8'&4" + name + "&8'&7!"));
            return false;
        }
        name = em.getRegionName(event, arena, "arena");
        try {
            if (!CWWorldGuard.doesSchematicExists(name)) {
                sender.sendMessage(Util.formatMsg("&cInvalid arena name or schematics not set properly. &7Missing schematic &8'&4" + name + "&8'&7!"));
                return false;
            }
        } catch (CommandException e) {
            sender.sendMessage(Util.formatMsg("&cInvalid arena name or schematics not set properly. &7Missing schematic &8'&4" + name + "&8'&7!"));
            return false;
        } catch (FilenameException e) {
            sender.sendMessage(Util.formatMsg("&cInvalid arena name or schematics not set properly. &7Missing schematic &8'&4" + name + "&8'&7!"));
            return false;
        }
        return true;
    }

    public void Reset() {
        super.Reset();
        CWWorldGuard.setFlag(world, em.getRegionName("arena"), DefaultFlag.PVP, "deny");
        try {
            try {
                File schemFile = CWWorldGuard.getSchematicFile(cwe.getEM().getRegionName("arena"));
                if (schemFile != null) {
                    CWWorldGuard.pasteSchematic(world, CWWorldGuard.getSchematicFile(cwe.getEM().getRegionName("arena")), cwe.getLoc(cwe.getEM().getRegionName("schem_arena")), false, 0);
                }
            } catch (FilenameException e) {
                e.printStackTrace();
            } catch (CommandException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (com.sk89q.worldedit.data.DataException e) {
            e.printStackTrace();
        } catch (com.sk89q.worldedit.MaxChangedBlocksException e) {
            e.printStackTrace();
        }
    }

    public void Open() {
        Reset();
        super.Open();
        //Limit slots to 12 because no more locations.
        if (em.getSlots() > 12 || em.getSlots() < 1) {
            em.setSlots(12);
        }
    }

    public void Start() {
        super.Start();
        int playerID = 1;
        for (String p : em.getPlayers()) {
            //Give each player a unique ID and tp them.
            if (!bombData.containsKey(p)) {
                bombData.put(p, new BombermanData(p));
            }
            bombData.get(p).setID(playerID);

            cwe.tpLoc(Bukkit.getServer().getPlayer(p), em.getRegionName("s" + playerID));
            playerID++;
        }
    }

    public void Begin() {
        CWWorldGuard.setFlag(world, em.getRegionName("arena"), DefaultFlag.PVP, "allow");
        for (String p : em.getPlayers()) {
            cwe.getServer().getPlayer(p).getInventory().addItem(bombItem);
        }
    }

    public void Stop() {
        super.Stop();
        CWWorldGuard.setFlag(world, em.getRegionName("arena"), DefaultFlag.PVP, "deny");
    }

    public void onPlayerLeft(Player player) {
        if (bombData.containsKey(player.getName())) {
            bombData.remove(player.getName());
        }
    }

    public void onPlayerJoin(Player player) {
        bombData.put(player.getName(), new BombermanData(player.getName()));
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 99999999, -128));
    }

    public void bombExplode(Player player, Location loc) {
        //Don't give bomb back if player picked up -1 bomb and had no bombs in inv.
        if (bombsToRemove > 0) {
            bombsToRemove--;
        } else {
            player.getInventory().addItem(bombItem);
        }
        BombermanData bd = bombData.get(player.getName());
        BombermanData obd = null;
        Block block = loc.getBlock();
        block.setType(Material.AIR);
        if (bd == null) {
            return;
        }
        loc.getWorld().playSound(loc, Sound.EXPLODE, 0.5f, 1.5f);
        ParticleEffect.HUGE_EXPLOSION.display(loc, 0.5f, 0.5f, 0.5f, 0.001f, 1);

        Block b = block;
        Player otherPlayer;
        boolean brokeFirst = false;
        int distance = 1;
        for (BlockFace dir : directions) {
            brokeFirst = false;
            for (distance = 1; distance <= bd.getExplosionSize(); distance++) {
                b = block.getRelative(dir, distance);
                //Air blocks so check for players hit and continue explosion if there is more distance..
                if (b == null || b.getType() == Material.AIR) {
                    explodeParticle(b.getLocation().add(0.5f, 0.5f, 0.5f), dir);

                    //Check if player is at this block and kill him if he is.
                    for (String p : em.getPlayers()) {
                        otherPlayer = cwe.getServer().getPlayer(p);
                        if (otherPlayer.getLocation().getBlockX() == b.getLocation().getBlockX() && otherPlayer.getLocation().getBlockZ() == b.getLocation().getBlockZ()) {
                            //Player hit by bomb remove life and check for no more lifes etc.
                            obd = bombData.get(otherPlayer.getName());
                            if (obd == null) {
                                continue;
                            }
                            //Invincible
                            if (otherPlayer.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                                continue;
                            }
                            //Shield
                            ItemStack chest = otherPlayer.getInventory().getChestplate();
                            if (chest != null && chest.getType() == Material.IRON_CHESTPLATE) {
                                player.getInventory().setHelmet(new ItemStack(Material.AIR));
                                player.getInventory().setChestplate(new ItemStack(Material.AIR));
                                player.getInventory().setLeggings(new ItemStack(Material.AIR));
                                player.getInventory().setBoots(new ItemStack(Material.AIR));
                                player.sendMessage(CWUtil.integrateColor("&4&lYour shield broke by a bomb!"));
                                continue;
                            }

                            em.broadcast(Util.formatMsg("&4" + otherPlayer.getDisplayName() + " &cwas exploded by &4" + player.getName() + "&4's &cbomb! &8[&4" + (obd.getLives() - 1) + "❤&8]"));
                            obd.setLives(obd.getLives() - 1);

                            if (obd.getLives() <= 0) {
                                //No more lives remove player.
                                otherPlayer.sendMessage(Util.formatMsg("&cYou have no more lives!"));
                                em.broadcast(Util.formatMsg("&b" + otherPlayer.getDisplayName() + " &3is out of the game!"));
                                em.leaveEvent(otherPlayer, true);
                            } else {
                                //More lives tell player and set player invis.
                                otherPlayer.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 60, 0), true);
                                if (obd.getLives() == 1) {
                                    otherPlayer.sendMessage(Util.formatMsg("&cThis is your last life!!! &4Be careful!"));
                                } else {
                                    String hearts = "";
                                    for (int i = 0; i < obd.getLives(); i++) {
                                        hearts += "❤";
                                    }
                                    otherPlayer.sendMessage(Util.formatMsg("&cYou have &4" + obd.getLives() + " &clives remaining. &8[&4" + hearts + "&8]"));
                                }
                            }
                        }
                    }
                    continue;
                }


                //Check for a destroyable block.
                if (destroyBlocks.contains(b.getType())) {
                    if (bd.getPierceBombs() > 0) {
                        explodeParticle(b.getLocation().add(0.5f, 0.5f, 0.5f), dir);
                        b.breakNaturally();
                        spawnPowerup(b.getLocation());
                        continue;
                    } else {
                        explodeParticle(b.getLocation().add(0.5f, 0.5f, 0.5f), dir);
                        b.breakNaturally();
                        spawnPowerup(b.getLocation());
                        break;
                    }
                }

                //Hit a wall
                break;
            }
        }
        if (bd.getPierceBombs() > 0) {
            bd.setPierceBombs(bd.getPierceBombs() - 1);
        }
    }

    private void spawnPowerup(Location location) {
        if (random.nextFloat() <= 0.4f) {
            String[] powerupKeys = powerups.keySet().toArray(new String[powerups.size()]);
            location.getWorld().dropItem(location.add(0.5f, 0.5f, 0.5f), powerups.get(CWUtil.random(powerupKeys)));
            location.getWorld().playSound(location, Sound.ORB_PICKUP, 0.6f, 2.0f);
            ParticleEffect.WITCH_MAGIC.display(location.add(0.5f, 0.5f, 0.5f), 0.25f, 1, 0.25f, 0.01f, 30);
        }
    }

    private void explodeParticle(Location loc, BlockFace dir) {
        if (dir == BlockFace.NORTH || dir == BlockFace.SOUTH) {
            ParticleEffect.FLAME.display(loc, 0.25f, 0.2f, 0.35f, 0.005f, 25);
            ParticleEffect.SMOKE.display(loc, 0.25f, 0.5f, 0.35f, 0.001f, 8);
        } else {
            ParticleEffect.FLAME.display(loc, 0.35f, 0.2f, 0.25f, 0.005f, 25);
            ParticleEffect.SMOKE.display(loc, 0.35f, 0.5f, 0.25f, 0.001f, 8);
        }
    }

    @EventHandler
    public void damage(EntityDamageEvent event) {
        if (em.getEvent() != EventType.BOMBERMAN) {
            return;
        }
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        if (em.getPlayers().contains(player.getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void bombPlace(BlockPlaceEvent event) {
        if (em.getEvent() != EventType.BOMBERMAN) {
            return;
        }
        if (event.getBlock().getType() != Material.TNT) {
            return;
        }
        Player player = event.getPlayer();
        if (!em.getPlayers().contains(player.getName())) {
            return;
        }
        if (!floorBlocks.contains(event.getBlock().getRelative(BlockFace.DOWN).getType())) {
            player.sendMessage(Util.formatMsg("&cBombs must be placed on the ground."));
            event.setCancelled(true);
            return;
        }
        new BombRunnable(this, player, event.getBlock().getLocation().add(0.5f, 0.5f, 0.5f), bombData.get(player.getName()).getFuseTime()).runTaskTimer(cwe, 0, 1);
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent event) {
        if (em.getEvent() != EventType.BOMBERMAN) {
            return;
        }
        Player player = event.getPlayer();
        if (!em.getPlayers().contains(player.getName())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void powerupPickup(PlayerPickupItemEvent event) {
        ItemStack item = event.getItem().getItemStack();
        if (item == null || item.getType() == Material.AIR) {
            return;
        }
        if (em.getEvent() != EventType.BOMBERMAN) {
            return;
        }
        if (em.getStatus() != EventStatus.STARTED) {
            return;
        }
        final Player player = event.getPlayer();
        if (!em.getPlayers().contains(player.getName())) {
            return;
        }
        BombermanData bd = bombData.get(player.getName());
        ItemStack powerup = null;
        for (String key : powerups.keySet()) {
            powerup = powerups.get(key);
            if (powerup.getType() == item.getType()) {
                if (item.hasItemMeta() && item.getItemMeta().getDisplayName() != null && item.getItemMeta().getLore().size() > 0) {
                    player.sendMessage(CWUtil.integrateColor("&6&lPowerup!!! &a&l" + powerup.getItemMeta().getDisplayName() + " &8- &7" + item.getItemMeta().getLore()));
                    if (key.equals("life")) {
                        bd.setLives(bd.getLives() + 1);

                    } else if (key.equals("bombUp")) {
                        bd.setBombs(bd.getBombs() + 1);
                        player.getInventory().addItem(bombItem);

                    } else if (key.equals("bombDown")) {
                        if (bd.getBombs() > 1) {
                            bd.setBombs(bd.getBombs() - 1);
                            bombsToRemove++;
                        }

                    } else if (key.equals("fuseTimeDown")) {
                        if (bd.getFuseTime() > 10) {
                            bd.setFuseTime(bd.getFuseTime() - 10);
                        }
                    } else if (key.equals("fuseTimeUp")) {
                        bd.setFuseTime(bd.getFuseTime() + 10);

                    } else if (key.equals("speed")) {
                        if (bd.getSpeed() < 3) {
                            bd.setSpeed(bd.getSpeed() + 1);
                            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999999, bd.getSpeed() - 1, true), true);
                        }

                    } else if (key.equals("slow")) {
                        if (bd.getSpeed() > 0) {
                            bd.setSpeed(bd.getSpeed() - 1);
                            if (bd.getSpeed() < 1) {
                                player.removePotionEffect(PotionEffectType.SPEED);
                            } else {
                                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999999, bd.getSpeed() - 1, true), true);
                            }
                        }

                    } else if (key.equals("powerUp")) {
                        if (bd.getExplosionSize() < 8) {
                            bd.setExplosionSize(bd.getExplosionSize() + 1);
                        }

                    } else if (key.equals("powerDown")) {
                        if (bd.getExplosionSize() > 1) {
                            bd.setExplosionSize(bd.getExplosionSize() - 1);
                        }

                    } else if (key.equals("pierce")) {
                        bd.setPierceBombs(bd.getPierceBombs() + 1);

                    } else if (key.equals("shield")) {
                        player.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
                        player.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
                        player.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
                        player.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (player.getInventory().getChestplate() != null && player.getInventory().getChestplate().getType() == Material.IRON_CHESTPLATE) {
                                    player.getInventory().setHelmet(new ItemStack(Material.AIR));
                                    player.getInventory().setChestplate(new ItemStack(Material.AIR));
                                    player.getInventory().setLeggings(new ItemStack(Material.AIR));
                                    player.getInventory().setBoots(new ItemStack(Material.AIR));
                                    player.sendMessage(CWUtil.integrateColor("&4&lYour shield broke because time ran out!"));
                                }
                            }
                        }.runTaskLater(cwe, 200L);

                    } else if (key.equals("blind")) {
                        for (String p : em.getPlayers()) {
                            if (p == player.getName()) {
                                continue;
                            }
                            cwe.getServer().getPlayer(p).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0, true), true);
                        }

                    }
                }
                event.getItem().remove();
                event.setCancelled(true);
                return;
            }
        }
        event.setCancelled(true);
    }
}

package cn.nukkit;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import cn.nukkit.AdventureSettings.Type;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockCommand;
import cn.nukkit.block.BlockDoor;
import cn.nukkit.block.BlockEnderChest;
import cn.nukkit.block.BlockNoteblock;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityCommandBlock;
import cn.nukkit.blockentity.BlockEntityItemFrame;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.blockentity.BlockEntitySpawnable;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandDataVersions;
import cn.nukkit.entity.Attribute;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.EntityInteractable;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.data.EntityData;
import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.entity.data.IntPositionEntityData;
import cn.nukkit.entity.data.ShortEntityData;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.entity.data.StringEntityData;
import cn.nukkit.entity.item.EntityBoat;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.entity.item.EntityMinecartAbstract;
import cn.nukkit.entity.item.EntityMinecartEmpty;
import cn.nukkit.entity.item.EntityVehicle;
import cn.nukkit.entity.item.EntityXPOrb;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.entity.projectile.EntityFishingHook;
import cn.nukkit.event.block.ItemFrameDropItemEvent;
import cn.nukkit.event.block.SignChangeEvent;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.EntityDamageEvent.DamageModifier;
import cn.nukkit.event.inventory.InventoryCloseEvent;
import cn.nukkit.event.inventory.InventoryPickupArrowEvent;
import cn.nukkit.event.inventory.InventoryPickupItemEvent;
import cn.nukkit.event.player.PlayerAnimationEvent;
import cn.nukkit.event.player.PlayerBedEnterEvent;
import cn.nukkit.event.player.PlayerBedLeaveEvent;
import cn.nukkit.event.player.PlayerBlockPickEvent;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.event.player.PlayerChunkRequestEvent;
import cn.nukkit.event.player.PlayerCommandPreprocessEvent;
import cn.nukkit.event.player.PlayerDeathEvent;
import cn.nukkit.event.player.PlayerGameModeChangeEvent;
import cn.nukkit.event.player.PlayerInteractEntityEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerInteractEvent.Action;
import cn.nukkit.event.player.PlayerInvalidMoveEvent;
import cn.nukkit.event.player.PlayerItemConsumeEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerKickEvent;
import cn.nukkit.event.player.PlayerLoginEvent;
import cn.nukkit.event.player.PlayerMapInfoRequestEvent;
import cn.nukkit.event.player.PlayerModalFormCloseEvent;
import cn.nukkit.event.player.PlayerModalFormResponseEvent;
import cn.nukkit.event.player.PlayerMouseOverEntityEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.event.player.PlayerPreLoginEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.event.player.PlayerRespawnEvent;
import cn.nukkit.event.player.PlayerServerSettingsChangedEvent;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.event.player.PlayerTeleportEvent.TeleportCause;
import cn.nukkit.event.player.PlayerToggleFlightEvent;
import cn.nukkit.event.player.PlayerToggleGlideEvent;
import cn.nukkit.event.player.PlayerToggleSneakEvent;
import cn.nukkit.event.player.PlayerToggleSprintEvent;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.event.server.DataPacketSendEvent;
import cn.nukkit.inventory.BigCraftingGrid;
import cn.nukkit.inventory.CraftingGrid;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.PlayerCursorInventory;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.inventory.transaction.CraftingTransaction;
import cn.nukkit.inventory.transaction.InventoryTransaction;
import cn.nukkit.inventory.transaction.SimpleInventoryTransaction;
import cn.nukkit.inventory.transaction.action.CraftingTakeResultAction;
import cn.nukkit.inventory.transaction.action.CraftingTransferMaterialAction;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.inventory.transaction.data.ReleaseItemData;
import cn.nukkit.inventory.transaction.data.UseItemData;
import cn.nukkit.inventory.transaction.data.UseItemOnEntityData;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemArrow;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemBucket;
import cn.nukkit.item.ItemGlassBottle;
import cn.nukkit.item.ItemMap;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.food.Food;
import cn.nukkit.lang.TextContainer;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.ChunkLoader;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.particle.CriticalParticle;
import cn.nukkit.level.particle.PunchBlockParticle;
import cn.nukkit.level.sound.ExperienceOrbSound;
import cn.nukkit.level.sound.ItemFrameItemRemovedSound;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;
import cn.nukkit.metadata.MetadataValue;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.ByteTag;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.network.SourceInterface;
import cn.nukkit.network.protocol.AdventureSettingsPacket;
import cn.nukkit.network.protocol.AnimatePacket;
import cn.nukkit.network.protocol.AvailableCommandsPacket;
import cn.nukkit.network.protocol.BatchPacket;
import cn.nukkit.network.protocol.BlockEntityDataPacket;
import cn.nukkit.network.protocol.BlockPickRequestPacket;
import cn.nukkit.network.protocol.ChangeDimensionPacket;
import cn.nukkit.network.protocol.ChunkRadiusUpdatedPacket;
import cn.nukkit.network.protocol.ClientboundMapItemDataPacket;
import cn.nukkit.network.protocol.CommandBlockUpdatePacket;
import cn.nukkit.network.protocol.CommandRequestPacket;
import cn.nukkit.network.protocol.ContainerClosePacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.DisconnectPacket;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.network.protocol.FullChunkDataPacket;
import cn.nukkit.network.protocol.InteractPacket;
import cn.nukkit.network.protocol.InventoryContentPacket;
import cn.nukkit.network.protocol.InventoryTransactionPacket;
import cn.nukkit.network.protocol.ItemFrameDropItemPacket;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.network.protocol.LoginPacket;
import cn.nukkit.network.protocol.MapInfoRequestPacket;
import cn.nukkit.network.protocol.MobEquipmentPacket;
import cn.nukkit.network.protocol.ModalFormRequestPacket;
import cn.nukkit.network.protocol.ModalFormResponsePacket;
import cn.nukkit.network.protocol.MovePlayerPacket;
import cn.nukkit.network.protocol.PlayStatusPacket;
import cn.nukkit.network.protocol.PlayerActionPacket;
import cn.nukkit.network.protocol.PlayerHotbarPacket;
import cn.nukkit.network.protocol.PlayerInputPacket;
import cn.nukkit.network.protocol.PlayerSkinPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.RemoveEntityPacket;
import cn.nukkit.network.protocol.RequestChunkRadiusPacket;
import cn.nukkit.network.protocol.ResourcePackChunkDataPacket;
import cn.nukkit.network.protocol.ResourcePackChunkRequestPacket;
import cn.nukkit.network.protocol.ResourcePackClientResponsePacket;
import cn.nukkit.network.protocol.ResourcePackDataInfoPacket;
import cn.nukkit.network.protocol.ResourcePackStackPacket;
import cn.nukkit.network.protocol.ResourcePacksInfoPacket;
import cn.nukkit.network.protocol.RespawnPacket;
import cn.nukkit.network.protocol.ServerSettingsResponsePacket;
import cn.nukkit.network.protocol.SetCommandsEnabledPacket;
import cn.nukkit.network.protocol.SetEntityMotionPacket;
import cn.nukkit.network.protocol.SetPlayerGameTypePacket;
import cn.nukkit.network.protocol.SetSpawnPositionPacket;
import cn.nukkit.network.protocol.SetTimePacket;
import cn.nukkit.network.protocol.SetTitlePacket;
import cn.nukkit.network.protocol.ShowProfilePacket;
import cn.nukkit.network.protocol.StartGamePacket;
import cn.nukkit.network.protocol.TakeItemEntityPacket;
import cn.nukkit.network.protocol.TextPacket;
import cn.nukkit.network.protocol.TransferPacket;
import cn.nukkit.network.protocol.UpdateAttributesPacket;
import cn.nukkit.network.protocol.UpdateBlockPacket;
import cn.nukkit.network.protocol.types.ContainerIds;
import cn.nukkit.network.protocol.types.NetworkInventoryAction;
import cn.nukkit.permission.PermissibleBase;
import cn.nukkit.permission.Permission;
import cn.nukkit.permission.PermissionAttachment;
import cn.nukkit.permission.PermissionAttachmentInfo;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.potion.Effect;
import cn.nukkit.potion.Potion;
import cn.nukkit.resourcepacks.ResourcePack;
import cn.nukkit.resourcepacks.ResourcePackManager;
import cn.nukkit.utils.Binary;
import cn.nukkit.utils.BlockIterator;
import cn.nukkit.utils.ClientChainData;
import cn.nukkit.utils.FastAppender;
import cn.nukkit.utils.MainLogger;
import cn.nukkit.utils.TextFormat;
import cn.nukkit.utils.Utils;
import cn.nukkit.utils.Zlib;
import cn.nukkit.window.FormWindow;
import cn.nukkit.window.ServerSettingsWindow;
import co.aikar.timings.Timing;
import co.aikar.timings.Timings;

/**
 * author: MagicDroidX & Box
 * Nukkit Project
 */
public class Player extends EntityHuman implements CommandSender, InventoryHolder, ChunkLoader, IPlayer {

    public static final int SURVIVAL = 0;
    public static final int CREATIVE = 1;
    public static final int ADVENTURE = 2;
    public static final int SPECTATOR = 3;
    public static final int VIEW = SPECTATOR;

    public static final int SURVIVAL_SLOTS = 36;
    public static final int CREATIVE_SLOTS = 112;

    public static final int CRAFTING_SMALL = 0;
    public static final int CRAFTING_BIG = 1;
    public static final int CRAFTING_ANVIL = 2;
    public static final int CRAFTING_ENCHANT = 3;
    public static final int CRAFTING_STONECUTTER = 4;//使用されていない(PMMPから引用)

    public static final float DEFAULT_SPEED = 0.1f;
    public static final float MAXIMUM_SPEED = 0.5f;

    public static final int PERMISSION_CUSTOM = 3;
    public static final int PERMISSION_OPERATOR = 2;
    public static final int PERMISSION_MEMBER = 1;
    public static final int PERMISSION_VISITOR = 0;

    protected final SourceInterface interfaz;

    public boolean playedBefore;
    public boolean spawned = false;
    public boolean loggedIn = false;
    public int gamemode;
    public long lastBreak;

    protected int windowCnt = 2;

    protected Map<Inventory, Integer> windows;

    protected Map<Integer, Inventory> windowIndex = new HashMap<>();

    protected Set<Integer> permanentWindows = new HashSet<>();

    protected int messageCounter = 2;

    private String clientSecret;

    public Vector3 speed = null;

    public final HashSet<String> achievements = new HashSet<>();

    public int craftingType = CRAFTING_SMALL;

    public long creationTime = 0;

    protected long randomClientId;

    protected Vector3 forceMovement = null;

    protected Vector3 teleportPosition = null;

    protected boolean connected = true;
    protected final String ip;
    protected boolean removeFormat = true;

    protected final int port;
    protected String username;
    protected String iusername;
    protected String displayName;
    protected String xuid;

    protected int startAction = -1;

    protected Vector3 sleeping = null;
    protected Long clientID = null;

    private Integer loaderId = null;

    protected float stepHeight = 0.6f;

    public Map<Long, Boolean> usedChunks = new HashMap<>();

    protected int chunkLoadCount = 0;
    protected Map<Long, Integer> loadQueue = new HashMap<Long, Integer>();
    protected int nextChunkOrderRun = 5;

    protected Map<UUID, Player> hiddenPlayers = new HashMap<>();

    protected Vector3 newPosition = null;

    protected int chunkRadius;
    protected int viewDistance;
    protected final int chunksPerTick;
    protected final int spawnThreshold;

    protected Position spawnPosition = null;

    protected int inAirTicks = 0;
    protected int startAirTicks = 5;

    protected AdventureSettings adventureSettings = new AdventureSettings(this);

    protected boolean checkMovement = true;

    private final Map<Integer, Boolean> needACK = new HashMap<>();

    private Map<Integer, List<DataPacket>> batchedPackets = new TreeMap<>();

    private PermissibleBase perm = null;

    private int exp = 0;
    private int expLevel = 0;

    protected PlayerFood foodData = null;

    private Entity killer = null;

    private final AtomicReference<Locale> locale = new AtomicReference<>(null);

    private int hash;

    private String buttonText = "";

    protected boolean enableClientCommand = true;

    private BlockEnderChest viewingEnderChest = null;

    protected int lastEnderPearl = -1;

    public boolean mute = false;

    public EntityFishingHook fishingHook;

    private boolean keepInventory = true;

    private boolean keepExperience = true;

    protected boolean enableRevert = true;

    public ClientChainData loginChainData;

    public int pickedXPOrb = 0;

    //看板関係

    private String signText1;

    private String signText2;

    private String signText3;

    private String signText4;

    private BlockEntity blockEntity;

    private FormWindow activeWindow = null;
    private LinkedHashMap<Integer, ServerSettingsWindow> serverSettings = new LinkedHashMap<>();

    private boolean printPackets;

    public void linkHookToPlayer(EntityFishingHook entity){
        this.fishingHook = entity;
        EntityEventPacket pk = new EntityEventPacket();
        pk.entityRuntimeId = this.getFishingHook().getId();
        pk.event = EntityEventPacket.FISH_HOOK_HOOK;
        Server.broadcastPacket(this.getLevel().getPlayers().values(), pk);
    }

    public void unlinkHookFromPlayer(){
        if (this.isFishing()){
            EntityEventPacket pk = new EntityEventPacket();
            pk.entityRuntimeId = this.getFishingHook().getId();
            pk.event = EntityEventPacket.FISH_HOOK_TEASE;
            Server.broadcastPacket(this.getLevel().getPlayers().values(), pk);
            this.fishingHook.close();
            this.fishingHook = null;
        }
    }
    @Deprecated
    public void setAllowFlight(boolean value) {
        this.getAdventureSettings().set(Type.ALLOW_FLIGHT, value);
        this.getAdventureSettings().update();
    }

    @Deprecated
    public boolean getAllowFlight() {
        return this.getAdventureSettings().get(Type.ALLOW_FLIGHT);
    }

    @Deprecated
    public void setAutoJump(boolean value) {
        this.getAdventureSettings().set(Type.AUTO_JUMP, value);
        this.getAdventureSettings().update();
    }

    @Deprecated
    public boolean hasAutoJump() {
        return this.getAdventureSettings().get(Type.AUTO_JUMP);
    }

    public int getStartActionTick() {
        return startAction;
    }

    public void startAction() {
        this.startAction = this.server.getTick();
    }

    public void stopAction() {
        this.startAction = -1;
    }

    public int getLastEnderPearlThrowingTick() {
        return lastEnderPearl;
    }

    public void onThrowEnderPearl() {
     this.lastEnderPearl = this.server.getTick();
    }

    public boolean isFishing(){
        return this.fishingHook != null;
    }

    public EntityFishingHook getFishingHook(){
        return this.fishingHook;
    }

    public BlockEnderChest getViewingEnderChest() {
        return viewingEnderChest;
    }

    public void setViewingEnderChest(BlockEnderChest chest) {
        if (chest == null && this.viewingEnderChest != null) {
            this.viewingEnderChest.getViewers().remove(this);
        } else if (chest != null) {
            chest.getViewers().add(this);
        }
        this.viewingEnderChest = chest;
    }

    public TranslationContainer getLeaveMessage() {
        return new TranslationContainer(TextFormat.YELLOW + "%multiplayer.player.left", this.getDisplayName());
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public ClientChainData getLoginChainData(){
        return this.loginChainData;
    }

    /**
     * ClientChainDataクラスを使用してください。
     */
    @Deprecated
    public Long getClientId() {
        return randomClientId;
    }

    @Override
    public boolean isBanned() {
        return this.server.getNameBans().isBanned(this.getName());
    }

    @Override
    public void setBanned(boolean value) {
        if (value) {
            this.server.getNameBans().addBan(this.getName(), null, null, null);
            this.kick(PlayerKickEvent.Reason.NAME_BANNED, "Banned by admin");
        } else {
            this.server.getNameBans().remove(this.getName());
        }
    }

    @Override
    public boolean isWhitelisted() {
        return this.server.isWhitelisted(this.getName().toLowerCase());
    }

    @Override
    public void setWhitelisted(boolean value) {
        if (value) {
            this.server.addWhitelist(this.getName().toLowerCase());
        } else {
            this.server.removeWhitelist(this.getName().toLowerCase());
        }
    }

    /**
     * プレイヤーオブジェクトを取得します。
     * @return Player
     */
    @Override
    public Player getPlayer() {
        return this;
    }

    @Override
    public Long getFirstPlayed() {
        return this.namedTag != null ? this.namedTag.getLong("firstPlayed") : null;
    }

    @Override
    public Long getLastPlayed() {
        return this.namedTag != null ? this.namedTag.getLong("lastPlayed") : null;
    }

    @Override
    public boolean hasPlayedBefore() {
        return this.playedBefore;
    }

    /*
    public void setCanDestroyBlock(boolean setting){
        AdventureSettings adventuresettings = this.getAdventureSettings();
        adventuresettings.setCanDestroyBlock(setting);
        this.setAdventureSettings(adventuresettings);
    }
    */

    public AdventureSettings getAdventureSettings() {
        return adventureSettings;
    }

    public void setAdventureSettings(AdventureSettings adventureSettings) {
        this.adventureSettings = adventureSettings.clone(this);
        this.adventureSettings.update();
    }

    public void resetInAirTicks() {
        this.inAirTicks = 0;
    }

    @Override
    public void spawnTo(Player player) {
        if (this.spawned && player.spawned && this.isAlive() && player.isAlive() && player.getLevel() == this.level && player.canSee(this) && !this.isSpectator()) {
            super.spawnTo(player);
        }
    }

    /**
     * プレイヤーからサーバーオブジェクトを取得します。
     * @return Server
     */
    @Override
    public Server getServer() {
        return this.server;
    }

    public boolean getRemoveFormat() {
        return removeFormat;
    }

    public void setRemoveFormat() {
        this.setRemoveFormat(true);
    }

    public void setRemoveFormat(boolean remove) {
        this.removeFormat = remove;
    }

    public boolean canSee(Player player) {
        return !this.hiddenPlayers.containsKey(player.getUniqueId());
    }

    public void hidePlayer(Player player) {
        if (this == player) {
            return;
        }
        this.hiddenPlayers.put(player.getUniqueId(), player);
        player.despawnFrom(this);
    }

    public void showPlayer(Player player) {
        if (this == player) {
            return;
        }
        this.hiddenPlayers.remove(player.getUniqueId());
        if (player.isOnline()) {
            player.spawnTo(this);
        }
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return false;
    }

    @Override
    public void resetFallDistance() {
        super.resetFallDistance();
        if (this.inAirTicks != 0) {
            this.startAirTicks = 5;
        }
        this.inAirTicks = 0;
        this.highestPosition = this.y;
    }

    @Override
    public boolean isOnline() {
        return this.connected && this.loggedIn;
    }

    /**
     * OPかどうかを取得します。
     * @return boolean trueがOP/falseが非OP
     */
    @Override
    public boolean isOp() {
        return this.server.isOp(this.getName());
    }

    public void setOp(){
        this.setOp(true);
    }

    /**
     * プレイヤーをOPにします。
     * @param value trueがOP/falseが非OP
     * @return void
     */
    @Override
    public void setOp(boolean value) {
        if (value == this.isOp()) {
            return;
        }

        if (value) {
            this.server.addOp(this.getName());
        } else {
            this.server.removeOp(this.getName());
        }

        this.recalculatePermissions();
        this.getAdventureSettings().update();
        this.sendCommandData();
    }

    @Override
    public boolean isPermissionSet(String name) {
        return this.perm.isPermissionSet(name);
    }

    @Override
    public boolean isPermissionSet(Permission permission) {
        return this.perm.isPermissionSet(permission);
    }

    @Override
    public boolean hasPermission(String name) {
        return this.perm != null && this.perm.hasPermission(name);
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return this.perm.hasPermission(permission);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return this.addAttachment(plugin, null);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name) {
        return this.addAttachment(plugin, name, null);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, Boolean value) {
        return this.perm.addAttachment(plugin, name, value);
    }

    @Override
    public void removeAttachment(PermissionAttachment attachment) {
        this.perm.removeAttachment(attachment);
    }

    @Override
    public void recalculatePermissions() {
        this.server.getPluginManager().unsubscribeFromPermission(Server.BROADCAST_CHANNEL_USERS, this);
        this.server.getPluginManager().unsubscribeFromPermission(Server.BROADCAST_CHANNEL_ADMINISTRATIVE, this);

        if (this.perm == null) {
            return;
        }

        this.perm.recalculatePermissions();

        if (this.hasPermission(Server.BROADCAST_CHANNEL_USERS)) {
            this.server.getPluginManager().subscribeToPermission(Server.BROADCAST_CHANNEL_USERS, this);
        }

        if (this.hasPermission(Server.BROADCAST_CHANNEL_ADMINISTRATIVE)) {
            this.server.getPluginManager().subscribeToPermission(Server.BROADCAST_CHANNEL_ADMINISTRATIVE, this);
        }

        if (this.isEnableClientCommand()) this.sendCommandData();
    }

    public boolean isEnableClientCommand() {
        return this.enableClientCommand;
    }

    public void setEnableClientCommand(boolean enable) {
        this.enableClientCommand = enable;
        SetCommandsEnabledPacket pk = new SetCommandsEnabledPacket();
        pk.enabled = enable;
        this.dataPacket(pk);
        if (enable) this.sendCommandData();
    }

    public void sendCommandData() {
        AvailableCommandsPacket pk = new AvailableCommandsPacket();
        Map<String, CommandDataVersions> data = new HashMap<>();
        int count = 0;
        for (Command command : this.server.getCommandMap().getCommands().values()) {
            if (!command.testPermissionSilent(this)) {
                continue;
            }
            ++count;
            CommandDataVersions data0 = command.generateCustomCommandData(this);
            data.put(command.getName(), data0);
        }
        if (count > 0) {
            //TODO: structure checking
            pk.commands = data;
            int identifier = this.dataPacket(pk, true); // We *need* ACK so we can be sure that the client received the packet or not
            Thread t = new Thread() {
                public void run() {
                    // We are going to wait 3 seconds, if after 3 seconds we didn't receive a reply from the client, resend the packet.
                    try {
                        Thread.sleep(3000);
                        boolean status = needACK.get(identifier);
                        if (!status && isOnline()) {
                            sendCommandData();
                            return;
                        }
                    } catch (InterruptedException e) {}
                }
            };
            t.start();
        }
    }

    @Override
    public Map<String, PermissionAttachmentInfo> getEffectivePermissions() {
        return this.perm.getEffectivePermissions();
    }

    public Player(SourceInterface interfaz, Long clientID, String ip, int port) {
        super(null, new CompoundTag());
        this.interfaz = interfaz;
        this.windows = new HashMap<>();
        this.perm = new PermissibleBase(this);
        this.server = Server.getInstance();
        this.lastBreak = Long.MAX_VALUE;
        this.ip = ip;
        this.port = port;
        this.clientID = clientID;
        this.loaderId = Level.generateChunkLoaderId(this);
        this.chunksPerTick = (int) this.server.getConfig("chunk-sending.per-tick", 4);
        this.spawnThreshold = (int) this.server.getConfig("chunk-sending.spawn-threshold", 56);
        this.spawnPosition = null;
        this.gamemode = this.server.getDefaultGamemode();
        this.setLevel(this.server.getDefaultLevel());
        this.viewDistance = this.server.getViewDistance();
        this.chunkRadius = viewDistance;
        //this.newPosition = new Vector3(0, 0, 0);
        this.boundingBox = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

        this.uuid = null;
        this.rawUUID = null;

        this.creationTime = System.currentTimeMillis();
        this.enableRevert = this.server.getJupiterConfigBoolean("enable-revert");
        this.server.getJupiterConfigBoolean("allow-snowball");
        this.server.getJupiterConfigBoolean("allow-egg");
        this.server.getJupiterConfigBoolean("allow-enderpearl");
        this.server.getJupiterConfigBoolean("allow-experience-bottle");
        this.server.getJupiterConfigBoolean("allow-splash-potion");
        this.server.getJupiterConfigBoolean("allow-bow");
        this.server.getJupiterConfigBoolean("allow-fishing-rod");

        this.printPackets = this.getServer().printPackets();
    }

    /**
     * プレイヤーオブジェクトかどうかを取得します。
     * この場合、trueしか返ってきません。
     * @return boolean true
     */
    public boolean isPlayer() {
        return true;
    }

    public void removeAchievement(String achievementId) {
        achievements.remove(achievementId);
    }

    public boolean hasAchievement(String achievementId) {
        return achievements.contains(achievementId);
    }

    public boolean isConnected() {
        return connected;
    }

    /**
     * プレイヤーのチャット上の名前を取得します。
     * @return String プレイヤーのチャット上の名前
     */
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * プレイヤーのチャット上の名前を設定します。
     * @param displayName 設定する名前
     * @return void
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        if (this.spawned) {
            this.server.updatePlayerListData(this.getUniqueId(), this.getId(), this.getDisplayName(), this.getSkin(), this.getLoginChainData().getXUID());
        }
    }

    /**
     * プレイヤーのスキンを設定します。
     * @param skin スキンデータ
     * @return void
     */
    @Override
    public void setSkin(Skin skin) {
        super.setSkin(skin);
        if (this.spawned) {
            this.server.updatePlayerListData(this.getUniqueId(), this.getId(), this.getDisplayName(), skin, this.getLoginChainData().getXUID());
        }
    }

    /**
     * プレイヤーのIPアドレスを取得します。
     * @return String プレイヤーのIPアドレス
     */
    public String getAddress() {
        return this.ip;
    }

    /**
     * プレイヤーのポートを取得します。
     * @return int プレイヤーのポート
     */
    public int getPort() {
        return port;
    }

    public Position getNextPosition() {
        return this.newPosition != null ? new Position(this.newPosition.x, this.newPosition.y, this.newPosition.z, this.level) : this.getPosition();
    }

    /**
     * プレイヤーが寝ているかどうかをを取得します。
     * @return boolean trueが寝ている/falseが寝ていない
     */
    public boolean isSleeping() {
        return this.sleeping != null;
    }

    public int getInAirTicks() {
        return this.inAirTicks;
    }

    public String getButtonText() {
        return this.buttonText;
    }

    public void setButtonText(String text) {
        if (!(text.equals(this.getButtonText()))) {
            this.buttonText = text;
            this.setDataProperty(new StringEntityData(Entity.DATA_INTERACTIVE_TAG, this.buttonText));
        }
    }

    @Override
    protected boolean switchLevel(Level targetLevel) {
        Level oldLevel = this.level;
        if (super.switchLevel(targetLevel)) {
            for (long index : new ArrayList<>(this.usedChunks.keySet())) {
                int chunkX = Level.getHashX(index);
                int chunkZ = Level.getHashZ(index);
                this.unloadChunk(chunkX, chunkZ, oldLevel);
            }

            this.usedChunks = new HashMap<>();

            SetTimePacket pk = new SetTimePacket();
            pk.time = this.level.getTime();
            this.dataPacket(pk);

            // TODO: Remove this hack
            int distance = this.viewDistance * 2 * 16 * 2;
            this.sendPosition(this.add(distance, 0, distance), this.yaw, this.pitch, MovePlayerPacket.MODE_RESET);
            return true;
        }
        return false;
    }

    public void unloadChunk(int x, int z) {
        this.unloadChunk(x, z, null);
    }

    public void unloadChunk(int x, int z, Level level) {
        level = level == null ? this.level : level;
        long index = Level.chunkHash(x, z);
        if (this.usedChunks.containsKey(index)) {
            for (Entity entity : level.getChunkEntities(x, z).values()) {
                if (entity != this) {
                    entity.despawnFrom(this);
                }
            }

            this.usedChunks.remove(index);
        }
        level.unregisterChunkLoader(this, x, z);
        this.loadQueue.remove(index);
    }

    public Position getSpawn() {
        if (this.spawnPosition != null && this.spawnPosition.getLevel() != null && this.getServer().getJupiterConfigString("spawnpoint").equals("fromplayerdata")) {
            return this.spawnPosition;
        } else {
            return this.server.getDefaultLevel().getSafeSpawn();
        }
    }

    public void sendChunk(int x, int z, DataPacket packet) {
        if (!this.connected) {
            return;
        }

        this.usedChunks.put(Level.chunkHash(x, z), true);
        this.chunkLoadCount++;

        this.dataPacket(packet);

        if (this.spawned) {
            for (Entity entity : this.level.getChunkEntities(x, z).values()) {
                if (this != entity && !entity.closed && entity.isAlive()) {
                    entity.spawnTo(this);
                }
            }
        }
    }

    public void sendChunk(int x, int z, byte[] payload) {
        if (!this.connected) {
            return;
        }

        this.usedChunks.put(Level.chunkHash(x, z), true);
        this.chunkLoadCount++;

        FullChunkDataPacket pk = new FullChunkDataPacket();
        pk.chunkX = x;
        pk.chunkZ = z;
        pk.data = payload;

        this.batchDataPacket(pk);

        if (this.spawned) {
            for (Entity entity : this.level.getChunkEntities(x, z).values()) {
                if (this != entity && !entity.closed && entity.isAlive()) {
                    entity.spawnTo(this);
                }
            }
        }
    }

    protected void sendNextChunk() {
        if (!this.connected) {
            return;
        }

        Timings.playerChunkSendTimer.startTiming();

        int count = 0;

        List<Map.Entry<Long, Integer>> entryList = new ArrayList<>(this.loadQueue.entrySet());
        entryList.sort(Comparator.comparingInt(Map.Entry::getValue));

        for (Map.Entry<Long, Integer> entry : entryList) {
            long index = entry.getKey();

            if (count >= this.chunksPerTick) {
                break;
            }

            int chunkX = Level.getHashX(index);
            int chunkZ = Level.getHashZ(index);

            ++count;

            this.usedChunks.put(index, false);
            this.level.registerChunkLoader(this, chunkX, chunkZ, false);

            if (!this.level.populateChunk(chunkX, chunkZ)) {
                if (this.spawned && this.teleportPosition == null) {
                    continue;
                } else {
                    break;
                }
            }

            this.loadQueue.remove(index);

            PlayerChunkRequestEvent ev = new PlayerChunkRequestEvent(this, chunkX, chunkZ);
            this.server.getPluginManager().callEvent(ev);
            if (!ev.isCancelled()) {
                this.level.requestChunk(chunkX, chunkZ, this);
            }
        }
        if (this.chunkLoadCount >= this.spawnThreshold && !this.spawned && this.teleportPosition == null) {
            this.doFirstSpawn();
        }
        Timings.playerChunkSendTimer.stopTiming();
    }

    protected void doFirstSpawn() {
        this.spawned = true;

        this.server.sendRecipeList(this);
        this.getAdventureSettings().update();

        this.sendPotionEffects(this);
        this.sendData(this);
        this.inventory.sendContents(this);
        this.inventory.sendArmorContents(this);

        this.offhandInventory.sendContents(this);
        this.offhandInventory.sendOffhandItem(this);

        SetTimePacket setTimePacket = new SetTimePacket();
        setTimePacket.time = this.level.getTime();
        this.dataPacket(setTimePacket);

        PlayStatusPacket playStatusPacket = new PlayStatusPacket();
        playStatusPacket.status = PlayStatusPacket.PLAYER_SPAWN;
        this.dataPacket(playStatusPacket);

        PlayerJoinEvent playerJoinEvent = new PlayerJoinEvent(this,
                new TranslationContainer(TextFormat.YELLOW + "%multiplayer.player.joined", new String[]{
                        this.getDisplayName()
                })
        );

        this.server.getPluginManager().callEvent(playerJoinEvent);

        if (playerJoinEvent.getJoinMessage().toString().trim().length() > 0) {
            //デフォルトの参加時メッセージを送るかどうかを確認
            if(this.server.getJupiterConfigBoolean("join-quit-message"))
                this.server.broadcastMessage(playerJoinEvent.getJoinMessage());
        }

        this.noDamageTicks = 60;

        for (long index : this.usedChunks.keySet()) {
            int chunkX = Level.getHashX(index);
            int chunkZ = Level.getHashZ(index);
            for (Entity entity : this.level.getChunkEntities(chunkX, chunkZ).values()) {
                if (this != entity && !entity.closed && entity.isAlive()) {
                    entity.spawnTo(this);
                }
            }
        }

        this.sendExperience(this.getExperience());
        this.sendExperienceLevel(this.getExperienceLevel());

        if (!this.isSpectator()) {
            this.spawnToAll();
        }

        //todo Updater
        RespawnPacket respawnPacket = new RespawnPacket();
        Position pos = this.getSpawn();
        respawnPacket.x = (float) pos.x;
        respawnPacket.y = (float) pos.y;
        respawnPacket.z = (float) pos.z;
        this.dataPacket(respawnPacket);

        this.sendPlayStatus(PlayStatusPacket.PLAYER_SPAWN);

        //Weather
        this.getLevel().sendWeather(this);

        //FoodLevel
        this.getFoodData().sendFoodLevel();
    }

    protected boolean orderChunks() {
        if (!this.connected) {
            return false;
        }

        Timings.playerChunkOrderTimer.startTiming();

        this.nextChunkOrderRun = 200;

        Map<Long, Integer> newOrder = new HashMap<>();
        Map<Long, Boolean> lastChunk = this.usedChunks;

        int centerX = (int) this.x >> 4;
        int centerZ = (int) this.z >> 4;
        for (int x = -this.chunkRadius; x <= this.chunkRadius; x++) {
            for (int z = -this.chunkRadius; z <= this.chunkRadius; z++) {
                int chunkX = x + centerX;
                int chunkZ = z + centerZ;
                int distance = (int) Math.sqrt((double) x * x + (double) z * z);
                if (distance <= this.chunkRadius) {
                    long index;
                    if (!(this.usedChunks.containsKey(index = Level.chunkHash(chunkX, chunkZ))) || !this.usedChunks.get(index)) {
                        newOrder.put(index, distance);
                    }
                    lastChunk.remove(index);
                }
            }
        }

        for (long index : new ArrayList<>(lastChunk.keySet())) {
            this.unloadChunk(Level.getHashX(index), Level.getHashZ(index));
        }

        this.loadQueue = newOrder;
        Timings.playerChunkOrderTimer.stopTiming();
        return true;
    }

    public boolean batchDataPacket(DataPacket packet) {
        if (!this.connected) {
            return false;
        }

        try (Timing timing = Timings.getSendDataPacketTiming(packet)) {
            DataPacketSendEvent event = new DataPacketSendEvent(this, packet);
            this.server.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                timing.stopTiming();
                return false;
            }

            if (!this.batchedPackets.containsKey(packet.getChannel())) {
                this.batchedPackets.put(packet.getChannel(), new ArrayList<>());
            }

            this.batchedPackets.get(packet.getChannel()).add(packet.clone());
        }
        return true;
    }

    /**
     * プレイヤーにパケットを送信します。
     * @param packet 送るパケット
     * @return boolean
     */
    public boolean dataPacket(DataPacket packet) {
        return this.dataPacket(packet, false) != -1;
    }

    public int dataPacket(DataPacket packet, boolean needACK) {
        if (!this.connected) {
            return -1;
        }

        if(this.printPackets)
            this.getServer().getLogger().info(TextFormat.YELLOW + "[SEND] " + TextFormat.WHITE + packet.getName());

        try (Timing timing = Timings.getSendDataPacketTiming(packet)) {
            DataPacketSendEvent ev = new DataPacketSendEvent(this, packet);
            this.server.getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                timing.stopTiming();
                return -1;
            }

            Integer identifier = this.interfaz.putPacket(this, packet, needACK, false);

            if (needACK && identifier != null) {
                this.needACK.put(identifier.intValue(), false);
                timing.stopTiming();
                return identifier;
            }
        }
        return 0;
    }

    /**
     * 0 is true
     * -1 is false
     * other is identifer
     */
    public boolean directDataPacket(DataPacket packet) {
        return this.directDataPacket(packet, false) != -1;
    }

    public int directDataPacket(DataPacket packet, boolean needACK) {
        if (!this.connected) {
            return -1;
        }

        if(this.printPackets)
            this.getServer().getLogger().info(TextFormat.LIGHT_PURPLE + "[SEND-DIRECT] " + TextFormat.WHITE + packet.getClass().getSimpleName());

        try (Timing timing = Timings.getSendDataPacketTiming(packet)) {
            DataPacketSendEvent ev = new DataPacketSendEvent(this, packet);
            this.server.getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                timing.stopTiming();
                return -1;
            }

            Integer identifier = this.interfaz.putPacket(this, packet, needACK, true);

            if (needACK && identifier != null) {
                this.needACK.put(identifier.intValue(), false);
                timing.stopTiming();
                return identifier;
            }
        }
        return 0;
    }

    public int getPing() {
        return this.interfaz.getNetworkLatency(this);
    }

    public boolean sleepOn(Vector3 pos) {
        if (!this.isOnline()) {
            return false;
        }

        for (Entity p : this.level.getNearbyEntities(this.boundingBox.grow(2, 1, 2), this)) {
            if (p instanceof Player) {
                if (((Player) p).sleeping != null && pos.distance(((Player) p).sleeping) <= 0.1) {
                    return false;
                }
            }
        }

        PlayerBedEnterEvent ev;
        this.server.getPluginManager().callEvent(ev = new PlayerBedEnterEvent(this, this.level.getBlock(pos)));
        if (ev.isCancelled()) {
            return false;
        }

        this.sleeping = pos.clone();
        this.teleport(new Location(pos.x + 0.5, pos.y - 0.5, pos.z + 0.5, this.yaw, this.pitch, this.level), null);

        this.setDataProperty(new IntPositionEntityData(DATA_PLAYER_BED_POSITION, (int) pos.x, (int) pos.y, (int) pos.z));
        this.setDataFlag(DATA_PLAYER_FLAGS, DATA_PLAYER_FLAG_SLEEP, true);

        this.setSpawn(pos);

        this.level.sleepTicks = 60;

        return true;
    }

    /**
     * プレイヤーのスポーン地点を設定します。
     * @param pos 設定する座標
     * @return void
     * @see "Vector3"
     * @see Vector3
     */
    public void setSpawn(Vector3 pos) {
        Level level;
        if (!(pos instanceof Position)) {
            level = this.level;
        } else {
            level = ((Position) pos).getLevel();
        }
        this.spawnPosition = new Position(pos.x, pos.y, pos.z, level);
        SetSpawnPositionPacket pk = new SetSpawnPositionPacket();
        pk.spawnType = SetSpawnPositionPacket.TYPE_PLAYER_SPAWN;
        pk.x = (int) this.spawnPosition.x;
        pk.y = (int) this.spawnPosition.y;
        pk.z = (int) this.spawnPosition.z;
        this.dataPacket(pk);
    }

    public void startSleep(){
        this.setDataFlag(DATA_PLAYER_FLAGS, DATA_PLAYER_FLAG_SLEEP, true);
        this.setDataProperty(new IntPositionEntityData(DATA_PLAYER_BED_POSITION, (int) this.x, (int) this.y, (int) this.z), true);
        this.sleeping = this.getPosition();
        this.server.getPluginManager().callEvent(new PlayerBedEnterEvent(this, this.level.getBlock(this.sleeping)));
    }

    public void stopSleep() {
        if (this.sleeping != null) {
            this.server.getPluginManager().callEvent(new PlayerBedLeaveEvent(this, this.level.getBlock(this.sleeping)));

            this.sleeping = null;
            this.setDataProperty(new IntPositionEntityData(DATA_PLAYER_BED_POSITION, 0, 0, 0));
            this.setDataFlag(DATA_PLAYER_FLAGS, DATA_PLAYER_FLAG_SLEEP, false);


            this.level.sleepTicks = 0;

            AnimatePacket pk = new AnimatePacket();
            pk.entityRuntimeId = this.id;
            pk.action = 3; //Wake up
            this.dataPacket(pk);
        }
    }

    /**
     * プレイヤーのゲームモードを取得します。
     * <br>0:サバイバルモード
     * <br>1:クリエイティブモード
     * <br>2:アドベンチャーモード
     * <br>3:スペクテイターモード
     * @return int プレイヤーのゲームモード
     */
    public int getGamemode() {
        return gamemode;
    }

    private static int getClientFriendlyGamemode(int gamemode) {
        gamemode &= 0x03;
        if (gamemode == Player.SPECTATOR) {
            return Player.CREATIVE;
        }
        return gamemode;
    }

    /**
     * プレイヤーのゲームモードを設定します。
     * @param gamemode ゲームモード
     * <br>0:サバイバルモード
     * <br>1:クリエイティブモード
     * <br>2:アドベンチャーモード
     * <br>3:スペクテイターモード
     * @return boolean
     */
    public boolean setGamemode(int gamemode) {
        return this.setGamemode(gamemode, false, null);
    }

    public boolean setGamemode(int gamemode, boolean clientSide) {
        return this.setGamemode(gamemode, clientSide, null);
    }

    public boolean setGamemode(int gamemode, boolean clientSide, AdventureSettings newSettings) {
        if (gamemode < 0 || gamemode > 3) {
            return false;
        }

        if (newSettings == null) {
            newSettings = this.getAdventureSettings().clone(this);
            newSettings.set(Type.BUILD_AND_MINE, gamemode != 3);
            newSettings.set(Type.WORLD_BUILDER, gamemode != 3);
            newSettings.set(Type.NO_CLIP, gamemode == 3);
            newSettings.set(Type.WORLD_IMMUTABLE, gamemode == 3);
            newSettings.set(Type.NO_PVP, gamemode == 3);
            newSettings.set(Type.FLYING, gamemode == 1 || gamemode == 3);
            newSettings.set(Type.ALLOW_FLIGHT, gamemode == 1 || gamemode == 3);
        }

        PlayerGameModeChangeEvent ev;
        this.server.getPluginManager().callEvent(ev = new PlayerGameModeChangeEvent(this, gamemode, newSettings));

        if (ev.isCancelled()) {
            return false;
        }

        this.gamemode = gamemode;

        if (this.isSpectator()) {
            this.keepMovement = true;
            this.despawnFromAll();
        } else {
            this.keepMovement = false;
            this.spawnToAll();
        }

        this.namedTag.putInt("playerGameType", gamemode);

        if (!clientSide) {
            SetPlayerGameTypePacket pk = new SetPlayerGameTypePacket();
            pk.gamemode = getClientFriendlyGamemode(gamemode);
            this.dataPacket(pk);
        }

        this.setAdventureSettings(ev.getNewAdventureSettings());
        this.getAdventureSettings().update();

        if (this.isSpectator()) {
            this.teleport(this.temporalVector.setComponents(this.x, this.y + 0.1, this.z));

            InventoryContentPacket inventoryContentPacket = new InventoryContentPacket();
            inventoryContentPacket.inventoryId = InventoryContentPacket.SPECIAL_CREATIVE;
            this.dataPacket(inventoryContentPacket);
        } else {
            InventoryContentPacket inventoryContentPacket = new InventoryContentPacket();
            inventoryContentPacket.inventoryId = InventoryContentPacket.SPECIAL_CREATIVE;
            inventoryContentPacket.slots = Item.getCreativeItems().stream().toArray(Item[]::new);
            this.dataPacket(inventoryContentPacket);
        }

        this.resetFallDistance();

        this.inventory.sendContents(this);
        this.inventory.sendContents(this.getViewers().values());
        this.inventory.sendHeldItem(this.hasSpawned.values());

        return true;
    }

    @Deprecated
    public void sendSettings() {
        this.getAdventureSettings().update();
    }

    public boolean isSurvival() {
        return (this.gamemode & 0x01) == 0;
    }

    public boolean isCreative() {
        return (this.gamemode & 0x01) > 0;
    }

    public boolean isSpectator() {
        return this.gamemode == 3;
    }

    public boolean isAdventure() {
        return (this.gamemode & 0x02) > 0;
    }

    @Override
    public Item[] getDrops() {
        if (!this.isCreative()) {
            return super.getDrops();
        }

        return new Item[0];
    }

    @Override
    public boolean setDataProperty(EntityData data) {
        return setDataProperty(data, true);
    }

    @Override
    public boolean setDataProperty(EntityData data, boolean send) {
        if (super.setDataProperty(data, send)) {
            if (send) this.sendData(this, new EntityMetadata().put(this.getDataProperty(data.getId())));
            return true;
        }
        return false;
    }

    @Override
    protected void checkGroundState(double movX, double movY, double movZ, double dx, double dy, double dz) {
        if (!this.onGround || movX != 0 || movY != 0 || movZ != 0) {
            boolean onGround = false;

            AxisAlignedBB bb = this.boundingBox.clone();
            bb.maxY = bb.minY + 0.5;
            bb.minY -= 1;

            AxisAlignedBB realBB = this.boundingBox.clone();
            realBB.maxY = realBB.minY + 0.1;
            realBB.minY -= 0.2;

            int minX = NukkitMath.floorDouble(bb.minX);
            int minY = NukkitMath.floorDouble(bb.minY);
            int minZ = NukkitMath.floorDouble(bb.minZ);
            int maxX = NukkitMath.ceilDouble(bb.maxX);
            int maxY = NukkitMath.ceilDouble(bb.maxY);
            int maxZ = NukkitMath.ceilDouble(bb.maxZ);

            for (int z = minZ; z <= maxZ; ++z) {
                for (int x = minX; x <= maxX; ++x) {
                    for (int y = minY; y <= maxY; ++y) {
                        Block block = this.level.getBlock(this.temporalVector.setComponents(x, y, z));

                        if (!block.canPassThrough() && block.collidesWithBB(realBB)) {
                            onGround = true;
                            break;
                        }
                    }
                }
            }

            this.onGround = onGround;
        }

        this.isCollided = this.onGround;
    }

    @Override
    protected void checkBlockCollision() {
        boolean portal = false;

        Block block = this.getLevelBlock();

        if (block.getId() == Block.NETHER_PORTAL) {
            portal = true;
        }

        block.onEntityCollide(this);

        if (portal) {
            inPortalTicks++;
        }
    }

    protected void checkNearEntities() {
        for (Entity entity : this.level.getNearbyEntities(this.boundingBox.grow(1, 0.5, 1), this)) {
            entity.scheduleUpdate();

            if (!entity.isAlive() || !this.isAlive()) {
                continue;
            }

            this.pickupEntity(entity, true);
        }
    }

    protected void processMovement(int tickDiff) {
        if (!this.isAlive() || !this.spawned || this.newPosition == null || this.teleportPosition != null || this.isSleeping()) {
            return;
        }
        Vector3 newPos = this.newPosition;
        double distanceSquared = newPos.distanceSquared(this);
        boolean revert = false;
        if ((distanceSquared / ((double) (tickDiff * tickDiff))) > 100 && (newPos.y - this.y) > -5) {
            revert = true;
        } else {
            if (this.chunk == null || !this.chunk.isGenerated()) {
                BaseFullChunk chunk = this.level.getChunk((int) newPos.x >> 4, (int) newPos.z >> 4, false);
                if (chunk == null || !chunk.isGenerated()) {
                    revert = true;
                    this.nextChunkOrderRun = 0;
                } else {
                    if (this.chunk != null) {
                        this.chunk.removeEntity(this);
                    }

                    this.chunk = chunk;
                }
            }
        }

        double tdx = newPos.x - this.x;
        double tdz = newPos.z - this.z;
        double distance = Math.sqrt(tdx * tdx + tdz * tdz);

        if (!revert && distanceSquared != 0) {
            double dx = newPos.x - this.x;
            double dy = newPos.y - this.y;
            double dz = newPos.z - this.z;

            this.fastMove(dx, dy, dz);

            double diffX = this.x - newPos.x;
            double diffY = this.y - newPos.y;
            double diffZ = this.z - newPos.z;

            double yS = 0.5 + this.ySize;
            if (diffY >= -yS || diffY <= yS) {
                diffY = 0;
            }

            if (diffX != 0 || diffY != 0 || diffZ != 0) {
                if (this.checkMovement && !server.getAllowFlight() && this.isSurvival()) {
                    if (!this.isSleeping() && this.riding == null) {
                        double diffHorizontalSqr = (diffX * diffX + diffZ * diffZ) / ((double) (tickDiff * tickDiff));
                        if (diffHorizontalSqr > 0.125) {
                            if(enableRevert){
                                PlayerInvalidMoveEvent ev;
                                this.getServer().getPluginManager().callEvent(ev = new PlayerInvalidMoveEvent(this, true));
                                if (!ev.isCancelled()) {
                                    revert = ev.isRevert();

                                    if (revert) {
                                        this.server.getLogger().warning(this.getServer().getLanguage().translateString("nukkit.player.invalidMove", this.getName()));
                                    }
                                }
                            }
                        }
                    }
                }


                this.x = newPos.x;
                this.y = newPos.y;
                this.z = newPos.z;
                double radius = this.getWidth() / 2;
                this.boundingBox.setBounds(this.x - radius, this.y, this.z - radius, this.x + radius, this.y + this.getHeight(), this.z + radius);
            }
        }

        Location from = new Location(
                this.lastX,
                this.lastY,
                this.lastZ,
                this.lastYaw,
                this.lastPitch,
                this.level);
        Location to = this.getLocation();

        if (!revert && (Math.pow(this.lastX - to.x, 2) + Math.pow(this.lastY - to.y, 2) + Math.pow(this.lastZ - to.z, 2)) > (1d / 16d) || (Math.abs(this.lastYaw - to.yaw) + Math.abs(this.lastPitch - to.pitch)) > 10) {
            boolean isFirst = this.firstMove;

            this.firstMove = false;
            this.lastX = to.x;
            this.lastY = to.y;
            this.lastZ = to.z;

            this.lastYaw = to.yaw;
            this.lastPitch = to.pitch;

            if (!isFirst) {
                List<Block> blocksAround = this.blocksAround;
                List<Block> collidingBlocks = this.collisionBlocks;

                PlayerMoveEvent ev = new PlayerMoveEvent(this, from, to);

                this.blocksAround = null;
                this.collisionBlocks = null;

                this.server.getPluginManager().callEvent(ev);

                if (!(revert = ev.isCancelled())) { //Yes, this is intended
                    if (!to.equals(ev.getTo())) { //If plugins modify the destination
                        this.teleport(ev.getTo(), null);
                    } else {
                        this.broadcastMovement();
                        //this.addMovement(this.x, this.y + this.getEyeHeight(), this.z, this.yaw, this.pitch, this.yaw);
                    }
                } else {
                    this.blocksAround = blocksAround;
                    this.collisionBlocks = collidingBlocks;
                }

                if (this.isFishing()){
                    if (this.distance(this.getFishingHook()) > 33 | this.getInventory().getItemInHand().getId() != Item.FISHING_ROD){
                        this.unlinkHookFromPlayer();
                    }
                }
            }

            if (!this.isSpectator()) {
                this.checkNearEntities();
            }
            if (this.speed == null) speed = new Vector3(from.x - to.x, from.y - to.y, from.z - to.z);
            else this.speed.setComponents(from.x - to.x, from.y - to.y, from.z - to.z);
        } else {
            if (this.speed == null) speed = new Vector3(0, 0, 0);
            else this.speed.setComponents(0, 0, 0);
        }

        if (!revert && (this.isFoodEnabled() || this.getServer().getDifficulty() == 0)) {
            if ((this.isSurvival() || this.isAdventure())/* && !this.getRiddingOn() instanceof Entity*/) {

                //UpdateFoodExpLevel
                if (distance >= 0.05) {
                    double jump = 0;
                    double swimming = this.isInsideOfWater() ? 0.015 * distance : 0;
                    if (swimming != 0) distance = 0;
                    if (this.isSprinting()) {  //Running
                        if (this.inAirTicks == 3 && swimming == 0) {
                            jump = 0.7;
                        }
                        this.getFoodData().updateFoodExpLevel(0.025 * distance + jump + swimming);
                    } else {
                        if (this.inAirTicks == 3 && swimming == 0) {
                            jump = 0.2;
                        }
                        this.getFoodData().updateFoodExpLevel(0.01 * distance + jump + swimming);
                    }
                }
            }
        }

        if (revert) {

            this.lastX = from.x;
            this.lastY = from.y;
            this.lastZ = from.z;

            this.lastYaw = from.yaw;
            this.lastPitch = from.pitch;

            this.sendPosition(from, from.yaw, from.pitch, MovePlayerPacket.MODE_RESET);
            //this.sendSettings();
            this.forceMovement = new Vector3(from.x, from.y, from.z);
        } else {
            this.forceMovement = null;
            if (distanceSquared != 0 && this.nextChunkOrderRun > 20) {
                this.nextChunkOrderRun = 20;
            }
        }

        this.newPosition = null;
    }

    @Override
    public boolean setMotion(Vector3 motion) {
        if (super.setMotion(motion)) {
            if (this.chunk != null) {
                //this.getLevel().addEntityMotion(this.chunk.getX(), this.chunk.getZ(), this.getId(), this.motionX, this.motionY, this.motionZ);  //Send to others
                this.broadcastMotion();
                SetEntityMotionPacket pk = new SetEntityMotionPacket();
                pk.entityRuntimeId = this.id;
                pk.motionX = (float) motion.x;
                pk.motionY = (float) motion.y;
                pk.motionZ = (float) motion.z;
                this.dataPacket(pk);  //Send to self
            }

            if (this.motionY > 0) {
                //todo: check this
                this.startAirTicks = (int) ((-(Math.log(this.getGravity() / (this.getGravity() + this.getDrag() * this.motionY))) / this.getDrag()) * 2 + 5);
            }

            return true;
        }

        return false;
    }

    public void sendAttributes() {
        UpdateAttributesPacket pk = new UpdateAttributesPacket();
        pk.entityRuntimeId = this.getId();
        pk.entries = new Attribute[]{
                Attribute.getAttribute(Attribute.MAX_HEALTH).setMaxValue(this.getMaxHealth()).setValue(health > 0 ? (health < getMaxHealth() ? health : getMaxHealth()) : 0),
                Attribute.getAttribute(Attribute.MAX_HUNGER).setValue(this.getFoodData().getLevel()),
                Attribute.getAttribute(Attribute.MOVEMENT_SPEED).setValue(this.getMovementSpeed()),
                Attribute.getAttribute(Attribute.EXPERIENCE_LEVEL).setValue(this.getExperienceLevel()),
                Attribute.getAttribute(Attribute.EXPERIENCE).setValue(((float) this.getExperience()) / calculateRequireExperience(this.getExperienceLevel()))
        };
        this.dataPacket(pk);
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (!this.loggedIn) {
            return false;
        }

        int tickDiff = currentTick - this.lastUpdate;

        if (tickDiff <= 0) {
            return true;
        }

        this.messageCounter = 2;

        this.lastUpdate = currentTick;

        if (!this.isAlive() && this.spawned) {
            ++this.deadTicks;
            if (this.deadTicks >= 10) {
                this.despawnFromAll();
            }
            return true;
        }

        if (this.spawned) {
            this.processMovement(tickDiff);

            this.entityBaseTick(tickDiff);

            if (this.isOnFire() && this.lastUpdate % 10 == 0) {
                if (this.isCreative() && !this.isInsideOfFire()) {
                    this.extinguish();
                } else if (this.getLevel().isRaining()) {
                    if (this.getLevel().canBlockSeeSky(this)) {
                        this.extinguish();
                    }
                }
            }

            if (!this.isSpectator() && this.speed != null) {
                if (this.onGround) {
                    if (this.inAirTicks != 0) {
                        this.startAirTicks = 5;
                    }
                    this.inAirTicks = 0;
                    this.highestPosition = this.y;
                } else {
                     if (!this.isCreative() && !this.isGliding() && !server.getAllowFlight() && !this.getAdventureSettings().get(Type.ALLOW_FLIGHT) && this.inAirTicks > 10 && !this.isSleeping() && !this.isImmobile()) {
                        double expectedVelocity = (-this.getGravity()) / ((double) this.getDrag()) - ((-this.getGravity()) / ((double) this.getDrag())) * Math.exp(-((double) this.getDrag()) * ((double) (this.inAirTicks - this.startAirTicks)));
                        double diff = (this.speed.y - expectedVelocity) * (this.speed.y - expectedVelocity);

                        if (!this.hasEffect(Effect.JUMP) && diff > 0.6 && expectedVelocity < this.speed.y) {
                            if (this.inAirTicks < 100) {
                                //this.sendSettings();
                                this.setMotion(new Vector3(0, expectedVelocity, 0));
                            } else if (this.kick(PlayerKickEvent.Reason.FLYING_DISABLED, "Flying is not enabled on this server")) {
                                return false;
                            }
                        }
                    }

                    if (this.y > highestPosition) {
                        this.highestPosition = this.y;
                    }

                    if (this.isGliding()) this.resetFallDistance();

                    ++this.inAirTicks;

                }

                if (this.isSurvival() || this.isAdventure()) {
                    if (this.getFoodData() != null) this.getFoodData().update(tickDiff);
                }
            }
        }

        this.checkTeleportPosition();
        this.checkInteractNearby();

        return true;
    }

    public void checkInteractNearby() {
        int interactDistance = isCreative() ? 5 : 3;
        EntityInteractable onInteract;
        if(this.canInteract(this, interactDistance) && (onInteract = this.getEntityPlayerLookingAt(interactDistance)) != null) {
            this.setButtonText(onInteract.getInteractButtonText());
        } else if (this.getInventory().getItemInHand().getId() == Item.FISHING_ROD) {
            this.setButtonText("釣りをする");
        } else {
            this.setButtonText("");
        }
    }

     /**
     * Returns the Entity the player is looking at currently
     *
     * @param maxDistance the maximum distance to check for entities
     * @return Entity|null    either NULL if no entity is found or an instance of the entity
     */
    public EntityInteractable getEntityPlayerLookingAt(int maxDistance) {
        timing.startTiming();

        EntityInteractable entity = null;

        // just a fix because player MAY not be fully initialized
        if (temporalVector != null) {
            Entity[] nearbyEntities = level.getNearbyEntities(boundingBox.grow(maxDistance, maxDistance, maxDistance), this);

            // get all blocks in looking direction until the max interact distance is reached (it's possible that startblock isn't found!)
            try {
                BlockIterator itr = new BlockIterator(level, getPosition(), getDirectionVector(), getEyeHeight(), maxDistance);
                if (itr.hasNext()) {
                    Block block;
                    while (itr.hasNext()) {
                        block = itr.next();
                        entity = getEntityAtPosition(nearbyEntities, block.getFloorX(), block.getFloorY(), block.getFloorZ());
                        if (entity != null) {
                            break;
                        }
                    }
                }
            } catch (Exception ex) {
                // nothing to log here!
            }
        }

        timing.stopTiming();

        return entity;
    }

    private EntityInteractable getEntityAtPosition(Entity[] nearbyEntities, int x, int y, int z) {
        for (Entity nearestEntity : nearbyEntities) {
            if (nearestEntity.getFloorX() == x && nearestEntity.getFloorY() == y && nearestEntity.getFloorZ() == z
                    && nearestEntity instanceof EntityInteractable
                    && ((EntityInteractable) nearestEntity).canDoInteraction()) {
                return (EntityInteractable) nearestEntity;
            }
        }
        return null;
    }

    private Block breakingBlock;

    public void checkNetwork() {
        if (!this.isOnline()) {
            return;
        }

        if (this.nextChunkOrderRun-- <= 0 || this.chunk == null) {
            this.orderChunks();
        }

        if (!this.loadQueue.isEmpty() || !this.spawned) {
            this.sendNextChunk();
        }

        if (!this.batchedPackets.isEmpty()) {
            for (int channel : this.batchedPackets.keySet()) {
                this.server.batchPackets(new Player[]{this}, batchedPackets.get(channel).stream().toArray(DataPacket[]::new), false);
            }
            this.batchedPackets = new TreeMap<>();
        }

    }

    public boolean canInteract(Vector3 pos, double maxDistance) {
        return this.canInteract(pos, maxDistance, 0.5);
    }

    public boolean canInteract(Vector3 pos, double maxDistance, double maxDiff) {
        if (this.distanceSquared(pos) > maxDistance * maxDistance) {
            return false;
        }

        Vector2 dV = this.getDirectionPlane();
        double dot = dV.dot(new Vector2(this.x, this.z));
        double dot1 = dV.dot(new Vector2(pos.x, pos.z));
        return (dot1 - dot) >= -maxDiff;
    }

    protected void processLogin() {
        if (!this.server.isWhitelisted((this.getName()).toLowerCase())) {
            this.kick(PlayerKickEvent.Reason.NOT_WHITELISTED, "Server is white-listed");

            return;
        } else if (this.isBanned()) {
            this.kick(PlayerKickEvent.Reason.NAME_BANNED, "You are banned");
            return;
        } else if (this.server.getIPBans().isBanned(this.getAddress())) {
            this.kick(PlayerKickEvent.Reason.IP_BANNED, "You are banned");
            return;
        }

        if (this.hasPermission(Server.BROADCAST_CHANNEL_USERS)) {
            this.server.getPluginManager().subscribeToPermission(Server.BROADCAST_CHANNEL_USERS, this);
        }
        if (this.hasPermission(Server.BROADCAST_CHANNEL_ADMINISTRATIVE)) {
            this.server.getPluginManager().subscribeToPermission(Server.BROADCAST_CHANNEL_ADMINISTRATIVE, this);
        }

        for (Player p : this.server.getOnlinePlayers().values()) {
            if (p != this && p.getName() != null && p.getName().equalsIgnoreCase(this.getName())) {
                if (!p.kick(PlayerKickEvent.Reason.NEW_CONNECTION, "logged in from another location")) {
                    this.close(this.getLeaveMessage(), "Already connected");
                    return;
                }
            } else if (p.loggedIn && this.getUniqueId().equals(p.getUniqueId())) {
                if (!p.kick(PlayerKickEvent.Reason.NEW_CONNECTION, "logged in from another location")) {
                    this.close(this.getLeaveMessage(), "Already connected");
                    return;
                }
            }
        }

        namedTag = this.server.getOfflinePlayerData(this.username);
        if (namedTag == null) {
            this.close(this.getLeaveMessage(), "Invalid data");

            return;
        }

        this.playedBefore = (namedTag.getLong("lastPlayed") - namedTag.getLong("firstPlayed")) > 1;

        boolean alive = true;

        namedTag.putString("NameTag", this.username);

        if (0 >= namedTag.getShort("Health")) {
            alive = false;
        }

        int exp = namedTag.getInt("EXP");
        int expLevel = namedTag.getInt("expLevel");
        this.setExperience(exp, expLevel);

        this.setGamemode(namedTag.getInt("playerGameType") & 0x03);
        if (this.server.getForceGamemode()) {
            this.gamemode = this.server.getDefaultGamemode();
            namedTag.putInt("playerGameType", this.gamemode);
        }

        Level level;
        if ((level = this.server.getLevelByName(namedTag.getString("Level"))) == null || !alive) {
            this.setLevel(this.server.getDefaultLevel());
            namedTag.putString("Level", this.level.getName());
            namedTag.getList("Pos", DoubleTag.class)
                    .add(new DoubleTag("0", this.level.getSpawnLocation().x))
                    .add(new DoubleTag("1", this.level.getSpawnLocation().y))
                    .add(new DoubleTag("2", this.level.getSpawnLocation().z));
        } else {
            this.setLevel(level);
        }

        for (Tag achievement : namedTag.getCompound("Achievements").getAllTags()) {
            if (!(achievement instanceof ByteTag)) {
                continue;
            }

            if (((ByteTag) achievement).getData() > 0) {
                this.achievements.add(achievement.getName());
            }
        }

        namedTag.putLong("lastPlayed", System.currentTimeMillis() / 1000);

        if (this.server.getAutoSave()) {
            this.server.saveOfflinePlayerData(this.username, namedTag, true);
        }

        ListTag<DoubleTag> posList = namedTag.getList("Pos", DoubleTag.class);

        super.init(this.level.getChunk((int) posList.get(0).data >> 4, (int) posList.get(2).data >> 4, true), namedTag);

        if (!this.namedTag.contains("foodLevel")) {
            this.namedTag.putInt("foodLevel", 20);
        }
        int foodLevel = this.namedTag.getInt("foodLevel");
        if (!this.namedTag.contains("FoodSaturationLevel")) {
            this.namedTag.putFloat("FoodSaturationLevel", 20);
        }
        float foodSaturationLevel = this.namedTag.getFloat("foodSaturationLevel");
        this.foodData = new PlayerFood(this, foodLevel, foodSaturationLevel);

        PlayerLoginEvent ev;
        this.server.getPluginManager().callEvent(ev = new PlayerLoginEvent(this, "Plugin reason"));
        if (ev.isCancelled()) {
            this.close(this.getLeaveMessage(), ev.getKickMessage());

            return;
        }

        this.server.addOnlinePlayer(this);
        this.loggedIn = true;

        if (this.isCreative()) {
            this.inventory.setHeldItemSlot(0);
        } else {
            this.inventory.setHeldItemSlot(this.inventory.getHotbarSlotIndex(0));
        }

        if (this.isSpectator()) this.keepMovement = true;

        if (this.spawnPosition == null && this.namedTag.contains("SpawnLevel") && (level = this.server.getLevelByName(this.namedTag.getString("SpawnLevel"))) != null) {
            this.spawnPosition = new Position(this.namedTag.getInt("SpawnX"), this.namedTag.getInt("SpawnY"), this.namedTag.getInt("SpawnZ"), level);
        }

        Position spawnPosition = this.getSpawn();
        StartGamePacket startGamePacket = new StartGamePacket();
        startGamePacket.entityUniqueId = this.id;
        startGamePacket.entityRuntimeId = this.id;
        startGamePacket.playerGamemode = getClientFriendlyGamemode(this.gamemode);
        startGamePacket.x = (float) this.x;
        startGamePacket.y = (float) this.y;
        startGamePacket.z = (float) this.z;
        startGamePacket.yaw = (float) this.yaw;
        startGamePacket.pitch = (float) this.pitch;
        startGamePacket.seed = -1;
        startGamePacket.dimension = (byte) (spawnPosition.level.getDimension() & 0xff);
        startGamePacket.worldGamemode = getClientFriendlyGamemode(this.gamemode);
        startGamePacket.difficulty = this.server.getDifficulty();
        startGamePacket.spawnX = (int) spawnPosition.x;
        startGamePacket.spawnY = (int) spawnPosition.y + (int) this.getEyeHeight();
        startGamePacket.spawnZ = (int) spawnPosition.z;
        startGamePacket.hasAchievementsDisabled = true;
        startGamePacket.dayCycleStopTime = -1;
        startGamePacket.eduMode = false;
        startGamePacket.rainLevel = 0;
        startGamePacket.lightningLevel = 0;
        startGamePacket.commandsEnabled = this.isEnableClientCommand();
        startGamePacket.levelId = "";
        startGamePacket.worldName = this.getServer().getNetwork().getName();
        startGamePacket.generator = 1; //0 old, 1 infinite, 2 flat
        this.dataPacket(startGamePacket);

        SetTimePacket setTimePacket = new SetTimePacket();
        setTimePacket.time = this.level.getTime();
        this.dataPacket(setTimePacket);

        this.setMovementSpeed(DEFAULT_SPEED);
        this.sendAttributes();
        this.setNameTagVisible(true);
        this.setNameTagAlwaysVisible(true);
        this.setCanClimb(true);

        this.server.getLogger().info(this.getServer().getLanguage().translateString("nukkit.player.logIn",
                FastAppender.get(TextFormat.AQUA, this.username, TextFormat.WHITE),
                this.ip,
                String.valueOf(this.port),
                String.valueOf(this.id),
                this.level.getName(),
                String.valueOf(NukkitMath.round(this.x, 4)),
                String.valueOf(NukkitMath.round(this.y, 4)),
                String.valueOf(NukkitMath.round(this.z, 4))));

        if (this.isOp()) {
            this.setRemoveFormat(false);
        }

        this.setEnableClientCommand(true);

        this.server.sendFullPlayerListData(this);

        this.forceMovement = this.teleportPosition = this.getPosition();

        this.server.onPlayerLogin(this);

        ResourcePacksInfoPacket pk = new ResourcePacksInfoPacket();
        ResourcePackManager manager = this.server.getResourcePackManager();
        pk.resourcePackEntries = manager.getResourceStack();
        pk.mustAccept = true;
        this.dataPacket(pk);
    }


    @Override
    protected void initEntity() {
        super.initEntity();

        this.addDefaultWindows();
    }

    public void handleDataPacket(DataPacket packet) {
        if (!connected) {
            return;
        }

        try (Timing timing = Timings.getReceiveDataPacketTiming(packet)) {
            DataPacketReceiveEvent ev = new DataPacketReceiveEvent(this, packet);
            this.server.getPluginManager().callEvent(ev);

            if(this.printPackets) {
                this.getServer().getLogger().info(TextFormat.AQUA + "[RECEIVE] " + TextFormat.WHITE + packet.getName());
            }

            if (ev.isCancelled()) {
                timing.stopTiming();
                return;
            }

            if (packet.pid() == ProtocolInfo.BATCH_PACKET) {
                timing.stopTiming();
                this.server.getNetwork().processBatch((BatchPacket) packet, this);
                return;
            }

            Item item;
            Block block;
            packetswitch:
            switch (packet.pid()) {
                case ProtocolInfo.MOVE_PLAYER_PACKET:
                    if (this.teleportPosition != null) {
                        break;
                    }

                    MovePlayerPacket movePlayerPacket = (MovePlayerPacket) packet;
                    Vector3 newPos = new Vector3(movePlayerPacket.x, movePlayerPacket.y - this.getEyeHeight(), movePlayerPacket.z);

                    if (newPos.distanceSquared(this) < 0.01 && movePlayerPacket.yaw % 360 == this.yaw && movePlayerPacket.pitch % 360 == this.pitch) {
                        break;
                    }

                    boolean revert = false;
                    if (!this.isAlive() || !this.spawned) {
                        revert = true;
                        this.forceMovement = new Vector3(this.x, this.y, this.z);
                    }

                    if (this.forceMovement != null && (newPos.distanceSquared(this.forceMovement) > 0.1 || revert)) {
                        this.sendPosition(this.forceMovement, movePlayerPacket.yaw, movePlayerPacket.pitch, MovePlayerPacket.MODE_RESET);
                    } else {

                        movePlayerPacket.yaw %= 360;
                        movePlayerPacket.pitch %= 360;

                        if (movePlayerPacket.yaw < 0) {
                            movePlayerPacket.yaw += 360;
                        }

                        this.setRotation(movePlayerPacket.yaw, movePlayerPacket.pitch);
                        this.newPosition = newPos;
                        this.forceMovement = null;
                    }

                    if (riding != null) {
                        if (riding instanceof EntityBoat) {
                            riding.setPositionAndRotation(this.temporalVector.setComponents(movePlayerPacket.x, movePlayerPacket.y - 1, movePlayerPacket.z), (movePlayerPacket.headYaw + 90) % 360, 0);
                        }
                    }
                    break;

                case ProtocolInfo.INTERACT_PACKET://TODO INTERACT_PACKET
                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }

                    this.craftingType = CRAFTING_SMALL;
                    this.resetCraftingGridType();

                    InteractPacket interactPacket = (InteractPacket) packet;

                    Entity targetEntity = this.level.getEntity(interactPacket.target);

                    if (targetEntity == null || !this.isAlive() || !targetEntity.isAlive()) {
                        break;
                    }

                    if (targetEntity instanceof EntityItem || targetEntity instanceof EntityArrow || targetEntity instanceof EntityXPOrb) {
                        this.kick(PlayerKickEvent.Reason.INVALID_PVE, "Attempting to interact with an invalid entity");
                        this.server.getLogger().warning(this.getServer().getLanguage().translateString("nukkit.player.invalidEntity", this.getName()));
                        break;
                    }

                    item = this.inventory.getItemInHand();

                    switch (interactPacket.action) {
                        case InteractPacket.ACTION_MOUSEOVER:
                            this.getServer().getPluginManager().callEvent(new PlayerMouseOverEntityEvent(this, targetEntity));
                            break;
                        case InteractPacket.ACTION_VEHICLE_EXIT:
                            if (!(targetEntity instanceof EntityVehicle) || this.riding == null) {
                                break;
                            }

                            ((EntityVehicle) riding).mountEntity(this);
                            break;
                    }
                    break;

                case ProtocolInfo.PLAYER_INPUT_PACKET:
                    if(!this.isAlive() || !this.spawned){
                        break;
                    }
                    PlayerInputPacket ipk = (PlayerInputPacket) packet;
                    if(riding instanceof EntityMinecartAbstract){
                        ((EntityMinecartEmpty) riding).setCurrentSpeed(ipk.motionY);
                    }
                    break;
                case ProtocolInfo.ADVENTURE_SETTINGS_PACKET:
                    //TODO: player abilities, check for other changes
                    if (this.isOp()) {
                        AdventureSettingsPacket adventureSettingsPacket = (AdventureSettingsPacket) packet;
                        if (adventureSettingsPacket.getFlag(AdventureSettingsPacket.ALLOW_FLIGHT) && !this.getAdventureSettings().get(Type.ALLOW_FLIGHT)) {
                            this.kick(PlayerKickEvent.Reason.FLYING_DISABLED, "Flying is not enabled on this server");
                            break;
                        }
                        PlayerToggleFlightEvent playerToggleFlightEvent = new PlayerToggleFlightEvent(this, adventureSettingsPacket.getFlag(AdventureSettingsPacket.ALLOW_FLIGHT));
                        this.server.getPluginManager().callEvent(playerToggleFlightEvent);
                        if (playerToggleFlightEvent.isCancelled()) {
                            this.getAdventureSettings().update();
                        } else {
                            this.getAdventureSettings().set(Type.FLYING, playerToggleFlightEvent.isFlying());
                        }
                    }
                    break;
                case ProtocolInfo.MOB_EQUIPMENT_PACKET:
                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }

                    MobEquipmentPacket mobEquipmentPacket = (MobEquipmentPacket) packet;

                    item = this.inventory.getItem(mobEquipmentPacket.hotbarSlot);

                    if (!item.equals(mobEquipmentPacket.item)) {
                        this.server.getLogger().debug("Tried to equip " + mobEquipmentPacket.item + " but have " + item + " in target slot");
                        this.inventory.sendContents(this);
                        return;
                    }

                    this.inventory.equipItem(mobEquipmentPacket.hotbarSlot);

                    this.setDataFlag(Player.DATA_FLAGS, Player.DATA_FLAG_ACTION, false);
                    break;

                case ProtocolInfo.PLAYER_ACTION_PACKET:
                    PlayerActionPacket playerActionPacket = (PlayerActionPacket) packet;
                    if (!this.spawned || (!this.isAlive() && playerActionPacket.action != PlayerActionPacket.ACTION_RESPAWN && playerActionPacket.action != PlayerActionPacket.ACTION_DIMENSION_CHANGE_REQUEST)) {
                        break;
                    }

                    playerActionPacket.entityRuntimeId = this.id;
                    Vector3 pos = new Vector3(playerActionPacket.x, playerActionPacket.y, playerActionPacket.z);
                    BlockFace face = BlockFace.fromIndex(playerActionPacket.face);

                    switch (playerActionPacket.action) {
                        case PlayerActionPacket.ACTION_START_BREAK:
                            if (this.lastBreak != Long.MAX_VALUE || pos.distanceSquared(this) > 100) {
                                break;
                            }
                            Block target = this.level.getBlock(pos);
                            PlayerInteractEvent playerInteractEvent = new PlayerInteractEvent(this, this.inventory.getItemInHand(), target, face, target.getId() == 0 ? Action.LEFT_CLICK_AIR : Action.LEFT_CLICK_BLOCK);
                            this.getServer().getPluginManager().callEvent(playerInteractEvent);
                            if (playerInteractEvent.isCancelled()) {
                                this.inventory.sendHeldItem(this);
                                break;
                            }
                            if (target.getId() == Block.NOTEBLOCK) {
                                ((BlockNoteblock) target).emitSound();
                                break;
                            }
                            block = target.getSide(face);
                            if (block.getId() == Block.FIRE) {
                                this.level.setBlock(block, new BlockAir(), true);
                                break;
                            }
                            if (!this.isCreative()) {
                                double breakTime = Math.ceil(target.getBreakTime(this.inventory.getItemInHand(), this) * 20);
                                LevelEventPacket pk = new LevelEventPacket();
                                pk.evid = LevelEventPacket.EVENT_BLOCK_START_BREAK;
                                pk.x = (float) pos.x;
                                pk.y = (float) pos.y;
                                pk.z = (float) pos.z;
                                pk.data = (int) (65535 / breakTime);
                                this.getLevel().addChunkPacket(pos.getFloorX() >> 4, pos.getFloorZ() >> 4, pk);
                            }

                            this.breakingBlock = target;
                            this.lastBreak = System.currentTimeMillis();
                            break;

                        case PlayerActionPacket.ACTION_ABORT_BREAK:
                            this.lastBreak = Long.MAX_VALUE;
                            this.breakingBlock = null;
                        case PlayerActionPacket.ACTION_STOP_BREAK:
                            LevelEventPacket pk = new LevelEventPacket();
                            pk.evid = LevelEventPacket.EVENT_BLOCK_STOP_BREAK;
                            pk.x = (float) pos.x;
                            pk.y = (float) pos.y;
                            pk.z = (float) pos.z;
                            pk.data = 0;
                            this.getLevel().addChunkPacket(pos.getFloorX() >> 4, pos.getFloorZ() >> 4, pk);
                            this.breakingBlock = null;
                            break;
                        case PlayerActionPacket.ACTION_GET_UPDATED_BLOCK:
                            break; //TODO
                        case PlayerActionPacket.ACTION_DROP_ITEM:
                            break; //TODO
                        case PlayerActionPacket.ACTION_STOP_SLEEPING:
                            this.stopSleep();
                            break;
                        case PlayerActionPacket.ACTION_RESPAWN:
                            if (!this.spawned || this.isAlive() || !this.isOnline()) {
                                break;
                            }

                            if (this.server.isHardcore()) {
                                this.setBanned(true);
                                break;
                            }

                            this.craftingType = CRAFTING_SMALL;
                            this.resetCraftingGridType();

                            PlayerRespawnEvent playerRespawnEvent = new PlayerRespawnEvent(this, this.getSpawn());
                            this.server.getPluginManager().callEvent(playerRespawnEvent);

                            Position respawnPos = playerRespawnEvent.getRespawnPosition();

                            this.teleport(respawnPos, null);

                            RespawnPacket respawnPacket = new RespawnPacket();
                            respawnPacket.x = (float) respawnPos.x;
                            respawnPacket.y = (float) respawnPos.y;
                            respawnPacket.z = (float) respawnPos.z;
                            this.dataPacket(respawnPacket);

                            this.setSprinting(false, true);
                            this.setSneaking(false);

                            this.extinguish();
                            this.setDataProperty(new ShortEntityData(Player.DATA_AIR, 400), false);
                            this.deadTicks = 0;
                            this.noDamageTicks = 60;

                            this.removeAllEffects();
                            this.setHealth(this.getMaxHealth());
                            this.getFoodData().setLevel(20, 20);

                            this.sendData(this);

                            this.setMovementSpeed(DEFAULT_SPEED);

                            this.getAdventureSettings().update();
                            this.inventory.sendContents(this);
                            this.inventory.sendArmorContents(this);

                            this.offhandInventory.sendContents(this);
                            this.offhandInventory.sendOffhandItem(this);

                            this.spawnToAll();
                            this.scheduleUpdate();
                            break;
                        case PlayerActionPacket.ACTION_JUMP:
                            break packetswitch;
                        case PlayerActionPacket.ACTION_START_SPRINT:
                            PlayerToggleSprintEvent playerToggleSprintEvent = new PlayerToggleSprintEvent(this, true);
                            this.server.getPluginManager().callEvent(playerToggleSprintEvent);
                            if (playerToggleSprintEvent.isCancelled()) {
                                this.sendData(this);
                            } else {
                                this.setSprinting(true);
                            }
                            break packetswitch;
                        case PlayerActionPacket.ACTION_STOP_SPRINT:
                            playerToggleSprintEvent = new PlayerToggleSprintEvent(this, false);
                            this.server.getPluginManager().callEvent(playerToggleSprintEvent);
                            if (playerToggleSprintEvent.isCancelled()) {
                                this.sendData(this);
                            } else {
                                this.setSprinting(false);
                            }
                            break packetswitch;
                        case PlayerActionPacket.ACTION_START_SNEAK:
                            PlayerToggleSneakEvent playerToggleSneakEvent = new PlayerToggleSneakEvent(this, true);
                            this.server.getPluginManager().callEvent(playerToggleSneakEvent);
                            if (playerToggleSneakEvent.isCancelled()) {
                                this.sendData(this);
                            } else {
                                this.setSneaking(true);
                            }
                            break packetswitch;
                        case PlayerActionPacket.ACTION_STOP_SNEAK:
                            playerToggleSneakEvent = new PlayerToggleSneakEvent(this, false);
                            this.server.getPluginManager().callEvent(playerToggleSneakEvent);
                            if (playerToggleSneakEvent.isCancelled()) {
                                this.sendData(this);
                            } else {
                                this.setSneaking(false);
                            }
                            break packetswitch;
                        case PlayerActionPacket.ACTION_DIMENSION_CHANGE_ACK:
                            break; //TODO
                        case PlayerActionPacket.ACTION_START_GLIDE:
                            PlayerToggleGlideEvent playerToggleGlideEvent = new PlayerToggleGlideEvent(this, true);
                            this.server.getPluginManager().callEvent(playerToggleGlideEvent);
                            if (playerToggleGlideEvent.isCancelled()) {
                                this.sendData(this);
                            } else {
                                this.setGliding(true);
                            }
                            break packetswitch;
                        case PlayerActionPacket.ACTION_STOP_GLIDE:
                            playerToggleGlideEvent = new PlayerToggleGlideEvent(this, false);
                            this.server.getPluginManager().callEvent(playerToggleGlideEvent);
                            if (playerToggleGlideEvent.isCancelled()) {
                                this.sendData(this);
                            } else {
                                this.setGliding(false);
                            }
                            break packetswitch;
                        case PlayerActionPacket.ACTION_CONTINUE_BREAK:
                            if (this.isBreakingBlock()) {
                                block = this.level.getBlock(pos);
                                this.level.addParticle(new PunchBlockParticle(pos, block, face));
                            }
                            break;
                    }

                    this.startAction = -1;
                    this.setDataFlag(Player.DATA_FLAGS, Player.DATA_FLAG_ACTION, false);
                    break;


                case ProtocolInfo.MOB_ARMOR_EQUIPMENT_PACKET:
                    break;

                case ProtocolInfo.BLOCK_PICK_REQUEST_PACKET:
                    BlockPickRequestPacket pickRequestPacket = (BlockPickRequestPacket) packet;
                    block = this.level.getBlock(this.temporalVector.setComponents(pickRequestPacket.x, pickRequestPacket.y, pickRequestPacket.z));
                    item = block.toItem();

                    if (pickRequestPacket.addUserData) {
                        BlockEntity blockEntity = this.getLevel().getBlockEntity(new Vector3(pickRequestPacket.x, pickRequestPacket.y, pickRequestPacket.z));
                        if (blockEntity != null) {
                            CompoundTag nbt = blockEntity.getCleanedNBT();
                            if (nbt != null) {
                                Item item1 = this.getInventory().getItemInHand();
                                item1.setCustomBlockData(nbt);
                                item1.setLore("+(DATA)");
                                this.getInventory().setItemInHand(item1);
                            }
                        }
                    }

                    PlayerBlockPickEvent pickEvent = new PlayerBlockPickEvent(this, block, item);
                    if (!this.isCreative()) {
                        pickEvent.setCancelled();
                    }

                    this.server.getPluginManager().callEvent(pickEvent);

                    if (!pickEvent.isCancelled()) {
                        this.inventory.setItemInHand(pickEvent.getItem());
                    }
                    break;

                case ProtocolInfo.INVENTORY_TRANSACTION_PACKET:
                    if (this.isSpectator()) {
                        this.sendAllInventories();
                        break;
                    }

                    InventoryTransactionPacket transactionPacket = (InventoryTransactionPacket) packet;

                    boolean isCrafting = false;
                    List<InventoryAction> actions = new ArrayList<>();
                    for (NetworkInventoryAction networkInventoryAction : transactionPacket.actions) {
                        try {
                            InventoryAction a = networkInventoryAction.createInventoryAction(this);
                            if (a != null) {
                                if (a instanceof CraftingTakeResultAction || a instanceof CraftingTransferMaterialAction) {
                                    isCrafting = true;
                                }
                                actions.add(a);
                            }
                        } catch (Throwable e) {
                            MainLogger.getLogger().debug("Unhandled inventory action from " + this.getName() + ": " + e.getMessage());
                            this.sendAllInventories();
                            break packetswitch;
                        }
                    }

                    if (isCrafting) {
                        CraftingTransaction craftingTransaction = new CraftingTransaction(this, actions);
                        for (InventoryAction action : actions) {
                            craftingTransaction.addAction(action);
                        }

                        craftingTransaction.execute();
                        break packetswitch;
                    }

                    switch (transactionPacket.transactionType) {
                        case InventoryTransactionPacket.TYPE_NORMAL:
                            InventoryTransaction transaction = new SimpleInventoryTransaction(this, actions);

                            if (!transaction.execute() && !isCrafting) {
                                for (Inventory inventory : transaction.getInventories()) {
                                    inventory.sendContents(this);
                                    if (inventory instanceof PlayerInventory) {
                                        ((PlayerInventory) inventory).sendArmorContents(this);
                                    }
                                }

                                MainLogger.getLogger().debug("Failed to execute inventory transaction from " + this.getName() + " with actions: " + Arrays.toString(actions.stream().toArray()));

                                //TODO: check more stuff that might need reversion
                                break packetswitch; //oops!
                            }

                            //TODO: fix achievement for getting iron from furnace

                            break packetswitch;
                        case InventoryTransactionPacket.TYPE_MISMATCH:
                            if (transactionPacket.actions.length > 0) {
                                this.server.getLogger().debug("Expected 0 actions for mismatch, got " + transactionPacket.actions.length + ", " + Arrays.toString(transactionPacket.actions));
                            }
                            this.sendAllInventories();

                            break packetswitch;
                        case InventoryTransactionPacket.TYPE_USE_ITEM:
                            UseItemData useItemData = (UseItemData) transactionPacket.transactionData;

                            BlockVector3 blockVector = useItemData.blockPos;
                            face = useItemData.face;

                            int type = useItemData.actionType;
                            switch (type) {
                                case InventoryTransactionPacket.USE_ITEM_ACTION_CLICK_BLOCK:
                                    this.setDataFlag(DATA_FLAGS, DATA_FLAG_ACTION, false);

                                    if (this.canInteract(blockVector.add(0.5, 0.5, 0.5), this.isCreative() ? 13 : 7)) {
                                        if (this.isCreative()) {
                                            Item i = inventory.getItemInHand();
                                            if (this.level.useItemOn(blockVector.asVector3(), i, face, useItemData.clickPos.x, useItemData.clickPos.y, useItemData.clickPos.z, this) != null) {
                                                break packetswitch;
                                            }
                                        } else if (inventory.getItemInHand().equals(useItemData.itemInHand)) {
                                            Item i = inventory.getItemInHand();
                                            Item oldItem = i.clone();
                                            //TODO: Implement adventure mode checks
                                            if ((i = this.level.useItemOn(blockVector.asVector3(), i, face, useItemData.clickPos.x, useItemData.clickPos.y, useItemData.clickPos.z, this)) != null) {
                                                if (!i.equals(oldItem) || i.getCount() != oldItem.getCount()) {
                                                    inventory.setItemInHand(i);
                                                    inventory.sendHeldItem(this.getViewers().values());
                                                }
                                                break packetswitch;
                                            }
                                        }
                                    }

                                    inventory.sendHeldItem(this);

                                    if (blockVector.distanceSquared(this) > 10000) {
                                        break packetswitch;
                                    }

                                    Block target = this.level.getBlock(blockVector.asVector3());
                                    block = target.getSide(face);

                                    this.level.sendBlocks(new Player[]{this}, new Block[]{target, block}, UpdateBlockPacket.FLAG_ALL_PRIORITY);

                                    if (target instanceof BlockDoor) {
                                        BlockDoor door = (BlockDoor) target;

                                        Block part;

                                        if ((door.getDamage() & 0x08) > 0) { //up
                                            part = target.down();

                                            if (part.getId() == target.getId()) {
                                                target = part;

                                                this.level.sendBlocks(new Player[]{this}, new Block[]{target}, UpdateBlockPacket.FLAG_ALL_PRIORITY);
                                            }
                                        }
                                    }
                                    break packetswitch;
                                case InventoryTransactionPacket.USE_ITEM_ACTION_BREAK_BLOCK:
                                    if (!this.spawned || !this.isAlive()) {
                                        break packetswitch;
                                    }

                                    this.resetCraftingGridType();

                                    Item i = this.getInventory().getItemInHand();

                                    Item oldItem = i.clone();

                                    if (this.canInteract(blockVector.add(0.5, 0.5, 0.5), this.isCreative() ? 13 : 7) && (i = this.level.useBreakOn(blockVector.asVector3(), i, this, true)) != null) {
                                        if (this.isSurvival()) {
                                            this.getFoodData().updateFoodExpLevel(0.025);
                                            if (!i.equals(oldItem) || i.getCount() != oldItem.getCount()) {
                                                inventory.setItemInHand(i);
                                                inventory.sendHeldItem(this.getViewers().values());
                                            }
                                        }
                                        break packetswitch;
                                    }

                                    inventory.sendContents(this);
                                    target = this.level.getBlock(blockVector.asVector3());
                                    BlockEntity blockEntity = this.level.getBlockEntity(blockVector.asVector3());

                                    this.level.sendBlocks(new Player[]{this}, new Block[]{target}, UpdateBlockPacket.FLAG_ALL_PRIORITY);

                                    inventory.sendHeldItem(this);

                                    if (blockEntity instanceof BlockEntitySpawnable) {
                                        ((BlockEntitySpawnable) blockEntity).spawnTo(this);
                                    }

                                    break packetswitch;
                                case InventoryTransactionPacket.USE_ITEM_ACTION_CLICK_AIR:
                                    Vector3 directionVector = this.getDirectionVector();

                                    if (this.isCreative()) {
                                        item = this.inventory.getItemInHand();
                                    } else if (!this.inventory.getItemInHand().equals(useItemData.itemInHand)) {
                                        this.inventory.sendHeldItem(this);
                                        break packetswitch;
                                    } else {
                                        item = this.inventory.getItemInHand();
                                    }

                                    PlayerInteractEvent interactEvent = new PlayerInteractEvent(this, item, directionVector, face, Action.RIGHT_CLICK_AIR);

                                    this.server.getPluginManager().callEvent(interactEvent);

                                    if (interactEvent.isCancelled()) {
                                        this.inventory.sendHeldItem(this);
                                        break packetswitch;
                                    }

                                    if (item.onClickAir(this, directionVector) && this.isSurvival()) {
                                        this.inventory.setItemInHand(item);
                                    }

                                    this.setDataFlag(DATA_FLAGS, DATA_FLAG_ACTION, true);
                                    this.startAction = this.server.getTick();

                                    break packetswitch;
                                default:
                                    //unknown
                                    break;
                            }
                            break;
                        case InventoryTransactionPacket.TYPE_USE_ITEM_ON_ENTITY:
                            UseItemOnEntityData useItemOnEntityData = (UseItemOnEntityData) transactionPacket.transactionData;

                            Entity target = this.level.getEntity(useItemOnEntityData.entityRuntimeId);
                            if (target == null) {
                                return;
                            }

                            type = useItemOnEntityData.actionType;

                            if (!useItemOnEntityData.itemInHand.equalsExact(this.inventory.getItemInHand())) {
                                this.inventory.sendHeldItem(this);
                            }

                            item = this.inventory.getItemInHand();

                            switch (type) {
                                case InventoryTransactionPacket.USE_ITEM_ON_ENTITY_ACTION_INTERACT:
                                    PlayerInteractEntityEvent playerInteractEntityEvent = new PlayerInteractEntityEvent(this, target, item);
                                    getServer().getPluginManager().callEvent(playerInteractEntityEvent);

                                    if (playerInteractEntityEvent.isCancelled()) {
                                        break;
                                    }

                                    if (target.onInteract(this, item) && this.isSurvival()) {
                                        if (item.isTool()) {
                                            if (item.useOn(target) && item.getDamage() >= item.getMaxDurability()) {
                                                item = new ItemBlock(new BlockAir());
                                            }
                                        } else {
                                            if (item.count > 1) {
                                                item.count--;
                                            } else {
                                                item = new ItemBlock(new BlockAir());
                                            }
                                        }

                                        this.inventory.setItemInHand(item);
                                    }
                                    break;
                                case InventoryTransactionPacket.USE_ITEM_ON_ENTITY_ACTION_ATTACK:
                                    if (this.getGamemode() == Player.VIEW) {
                                        break;
                                    }

                                    float itemDamage = item.getAttackDamage();

                                    for (Enchantment enchantment : item.getEnchantments()) {
                                        itemDamage += enchantment.getDamageBonus(target);
                                    }

                                    Map<DamageModifier, Float> damage = new EnumMap<>(DamageModifier.class);
                                    damage.put(DamageModifier.BASE, itemDamage);

                                    if (!this.canInteract(target, isCreative() ? 8 : 5)) {
                                        break;
                                    } else if (target instanceof Player) {
                                        if ((((Player) target).getGamemode() & 0x01) > 0) {
                                            break;
                                        } else if (!this.server.getPropertyBoolean("pvp") || this.server.getDifficulty() == 0) {
                                            break;
                                        }
                                    }

                                    if (!target.attack(new EntityDamageByEntityEvent(this, target, DamageCause.ENTITY_ATTACK, damage))) {
                                        if (item.isTool() && this.isSurvival()) {
                                            this.inventory.sendContents(this);
                                        }
                                        break;
                                    }

                                    for (Enchantment enchantment : item.getEnchantments()) {
                                        enchantment.doPostAttack(this, target);
                                    }

                                    if (item.isTool() && this.isSurvival()) {
                                        if (item.useOn(target) && item.getDamage() >= item.getMaxDurability()) {
                                            this.inventory.setItemInHand(new ItemBlock(new BlockAir()));
                                        } else {
                                            this.inventory.setItemInHand(item);
                                        }
                                    }
                                    return;
                                default:
                                    break; //unknown
                            }

                            break;
                        case InventoryTransactionPacket.TYPE_RELEASE_ITEM:
                            ReleaseItemData releaseItemData = (ReleaseItemData) transactionPacket.transactionData;

                            try {
                                type = releaseItemData.actionType;
                                switch (type) {
                                    case InventoryTransactionPacket.RELEASE_ITEM_ACTION_RELEASE:
                                        if (this.isUsingItem()) {
                                            item = this.inventory.getItemInHand();
                                            if (item.onReleaseUsing(this)) {
                                                this.inventory.setItemInHand(item);
                                            }
                                        } else {
                                            this.inventory.sendContents(this);
                                        }
                                        return;
                                    case InventoryTransactionPacket.RELEASE_ITEM_ACTION_CONSUME:
                                        Item itemInHand = this.inventory.getItemInHand();
                                        PlayerItemConsumeEvent consumeEvent = new PlayerItemConsumeEvent(this, itemInHand);

                                        if (itemInHand.getId() == Item.POTION) {
                                            this.server.getPluginManager().callEvent(consumeEvent);
                                            if (consumeEvent.isCancelled()) {
                                                this.inventory.sendContents(this);
                                                break;
                                            }
                                            Potion potion = Potion.getPotion(itemInHand.getDamage()).setSplash(false);

                                            if (this.getGamemode() == SURVIVAL) {
                                                --itemInHand.count;
                                                this.inventory.setItemInHand(itemInHand);
                                                this.inventory.addItem(new ItemGlassBottle());
                                            }

                                            if (potion != null) {
                                                potion.applyPotion(this);
                                            }

                                        } else if (itemInHand.getId() == Item.BUCKET && itemInHand.getDamage() == 1) { //milk
                                            this.server.getPluginManager().callEvent(consumeEvent);
                                            if (consumeEvent.isCancelled()) {
                                                this.inventory.sendContents(this);
                                                break;
                                            }

                                            EntityEventPacket eventPacket = new EntityEventPacket();
                                            eventPacket.entityRuntimeId = this.getId();
                                            eventPacket.event = EntityEventPacket.USE_ITEM;
                                            this.dataPacket(eventPacket);
                                            Server.broadcastPacket(this.getViewers().values(), eventPacket);

                                            if (this.isSurvival()) {
                                                itemInHand.count--;
                                                this.inventory.setItemInHand(itemInHand);
                                                this.inventory.addItem(new ItemBucket());
                                            }

                                            this.removeAllEffects();
                                        } else {
                                            this.server.getPluginManager().callEvent(consumeEvent);
                                            if (consumeEvent.isCancelled()) {
                                                this.inventory.sendContents(this);
                                                break;
                                            }

                                            Food food = Food.getByRelative(itemInHand);
                                            if (food != null && food.eatenBy(this)) --itemInHand.count;
                                            this.inventory.setItemInHand(itemInHand);
                                        }
                                        return;
                                    default:
                                        break;
                                }
                            } finally {
                                this.setUsingItem(false);
                            }
                            break;
                        default:
                            this.inventory.sendContents(this);
                            break;
                    }
                    break;

                case ProtocolInfo.PLAYER_HOTBAR_PACKET:
                    PlayerHotbarPacket playerHotbarPacket = (PlayerHotbarPacket) packet;
                    for (int i = 0; i < playerHotbarPacket.slots.length; i++) {
                        this.inventory.setHotbarSlotIndex(i, playerHotbarPacket.slots[i]);
                    }

                    if (playerHotbarPacket.windowId != ContainerIds.INVENTORY) {
                        return;
                    }

                    for (int hotbarSlot = 0; hotbarSlot < playerHotbarPacket.slots.length; hotbarSlot++) {
                        int slotLink = playerHotbarPacket.slots[hotbarSlot];
                        this.inventory.setHotbarSlotIndex(hotbarSlot, slotLink == -1 ? slotLink : slotLink - 9);
                    }

                    this.inventory.equipItem(playerHotbarPacket.selectedHotbarSlot);
                    break;

                case ProtocolInfo.ANIMATE_PACKET:
                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }

                    PlayerAnimationEvent animationEvent = new PlayerAnimationEvent(this, ((AnimatePacket) packet).action);
                    this.server.getPluginManager().callEvent(animationEvent);
                    if (animationEvent.isCancelled()) {
                        break;
                    }

                    AnimatePacket animatePacket = new AnimatePacket();
                    animatePacket.entityRuntimeId = this.getId();
                    animatePacket.action = animationEvent.getAnimationType();
                    Server.broadcastPacket(this.getViewers().values(), animatePacket);
                    break;
                case ProtocolInfo.SET_HEALTH_PACKET:
                    //use UpdateAttributePacket instead
                    break;

                case ProtocolInfo.ENTITY_EVENT_PACKET:
                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }
                    this.craftingType = CRAFTING_SMALL;
                    this.resetCraftingGridType();

                    //this.setDataFlag(DATA_FLAGS, DATA_FLAG_ACTION, false); //TODO: check if this should be true
                    EntityEventPacket entityEventPacket = (EntityEventPacket) packet;

                    switch (entityEventPacket.event) {
                        case EntityEventPacket.USE_ITEM: //Eating
                            Item itemInHand = this.inventory.getItemInHand();
                            PlayerItemConsumeEvent consumeEvent = new PlayerItemConsumeEvent(this, itemInHand);
                            this.server.getPluginManager().callEvent(consumeEvent);
                            if (consumeEvent.isCancelled()) {
                                this.inventory.sendContents(this);
                                break;
                            }

                            if (itemInHand.getId() == Item.POTION) {
                                Potion potion = Potion.getPotion(itemInHand.getDamage()).setSplash(false);

                                if (this.getGamemode() == SURVIVAL) {
                                    if (itemInHand.getCount() > 1) {
                                        ItemGlassBottle bottle = new ItemGlassBottle();
                                        if (this.inventory.canAddItem(bottle)) {
                                            this.inventory.addItem(bottle);
                                        }
                                        --itemInHand.count;
                                    } else {
                                        itemInHand = new ItemGlassBottle();
                                    }
                                }

                                if (potion != null) {
                                    potion.applyPotion(this);
                                }

                            } else {
                                EntityEventPacket pk = new EntityEventPacket();
                                pk.entityRuntimeId = this.getId();
                                pk.event = EntityEventPacket.USE_ITEM;
                                this.dataPacket(pk);
                                Server.broadcastPacket(this.getViewers().values(), pk);

                                Food food = Food.getByRelative(itemInHand);
                                if (food != null) if (food.eatenBy(this)) --itemInHand.count;

                            }

                            this.inventory.setItemInHand(itemInHand);
                            this.inventory.sendHeldItem(this);

                            break;

                        /*case EntityEventPacket.CONSUME_ITEM:
                            EntityEventPacket pk = new EntityEventPacket();
                            pk.entityRuntimeId = this.getId();
                            pk.event = EntityEventPacket.CONSUME_ITEM;
                            pk.itemId = this.inventory.getItemInHand().getId();

                            Server.broadcastPacket(this.getViewers().values(), pk);
                            this.dataPacket(pk);
                            break;*/
                    }
                    break;

                case ProtocolInfo.LOGIN_PACKET:
                    if (this.loggedIn) {
                        break;
                    }

                    LoginPacket loginPacket = (LoginPacket) packet;

                    String message;
                    if (loginPacket.getProtocol() < ProtocolInfo.CURRENT_PROTOCOL) {
                        if (loginPacket.getProtocol() < ProtocolInfo.CURRENT_PROTOCOL) {
                            message = "disconnectionScreen.outdatedClient";

                            this.sendPlayStatus(PlayStatusPacket.LOGIN_FAILED_CLIENT);
                        } else {
                            message = "disconnectionScreen.outdatedServer";

                            this.sendPlayStatus(PlayStatusPacket.LOGIN_FAILED_SERVER);
                        }
                        this.close("", message, false);
                        break;
                    }

                    this.loginChainData = new ClientChainData(loginPacket);
                    this.serverSettings = this.server.getDefaultServerSettings();

                    this.username = TextFormat.clean(this.loginChainData.getUsername());
                    this.displayName = this.username;
                    this.iusername = this.username.toLowerCase();
                    this.setDataProperty(new StringEntityData(DATA_NAMETAG, this.username), false);

                    if (this.server.getOnlinePlayers().size() >= this.server.getMaxPlayers() && this.kick(PlayerKickEvent.Reason.SERVER_FULL, "disconnectionScreen.serverFull", false)) {
                        break;
                    }

                    this.randomClientId = this.loginChainData.getClientId();

                    this.uuid = this.loginChainData.getClientUUID();
                    this.rawUUID = Binary.writeUUID(this.uuid);

                    boolean valid = true;
                    int len = this.username.length();
                    if (len > 16 || len < 3) {
                        valid = false;
                    }

                    for (int i = 0; i < len && valid; i++) {
                        char c = this.username.charAt(i);
                        if ((c >= 'a' && c <= 'z') ||
                                (c >= 'A' && c <= 'Z') ||
                                (c >= '0' && c <= '9') ||
                                c == '_' || c == ' '
                                ) {
                            continue;
                        }

                        valid = false;
                        break;
                    }

                    if (!valid || Objects.equals(this.iusername, "rcon") || Objects.equals(this.iusername, "console")) {
                        this.close("", "disconnectionScreen.invalidName");

                        break;
                    }

                    if (!this.loginChainData.getSkin().isValid()) {
                        this.close("", "disconnectionScreen.invalidSkin");
                        break;
                    } else {
                        this.setSkin(this.loginChainData.getSkin());
                    }

                    PlayerPreLoginEvent playerPreLoginEvent;
                    this.server.getPluginManager().callEvent(playerPreLoginEvent = new PlayerPreLoginEvent(this, "Plugin reason"));
                    if (playerPreLoginEvent.isCancelled()) {
                        this.close("", playerPreLoginEvent.getKickMessage());

                        break;
                    }

                    this.sendPlayStatus(PlayStatusPacket.LOGIN_SUCCESS);

                    ResourcePacksInfoPacket infoPacket = new ResourcePacksInfoPacket();
                    infoPacket.resourcePackEntries = this.server.getResourcePackManager().getResourceStack();
                    infoPacket.mustAccept = this.server.getForceResources();
                    this.dataPacket(infoPacket);

                    break;
                case ProtocolInfo.RESOURCE_PACK_CLIENT_RESPONSE_PACKET:
                    ResourcePackClientResponsePacket responsePacket = (ResourcePackClientResponsePacket) packet;
                    switch (responsePacket.responseStatus) {
                        case ResourcePackClientResponsePacket.STATUS_REFUSED:
                            this.close("", "disconnectionScreen.noReason");
                            break;
                        case ResourcePackClientResponsePacket.STATUS_SEND_PACKS:
                            for (String id : responsePacket.packIds) {
                                ResourcePack resourcePack = this.server.getResourcePackManager().getPackById(id);
                                if (resourcePack == null) {
                                    this.close("", "disconnectionScreen.resourcePack");
                                    break;
                                }

                                ResourcePackDataInfoPacket dataInfoPacket = new ResourcePackDataInfoPacket();
                                dataInfoPacket.packId = resourcePack.getPackId();
                                dataInfoPacket.maxChunkSize = 1048576; //megabyte
                                dataInfoPacket.chunkCount = resourcePack.getPackSize() / dataInfoPacket.maxChunkSize;
                                dataInfoPacket.compressedPackSize = resourcePack.getPackSize();
                                dataInfoPacket.sha256 = resourcePack.getSha256();
                                this.dataPacket(dataInfoPacket);
                            }
                            break;
                        case ResourcePackClientResponsePacket.STATUS_HAVE_ALL_PACKS:
                            ResourcePackStackPacket stackPacket = new ResourcePackStackPacket();
                            stackPacket.mustAccept = this.server.getForceResources();
                            stackPacket.resourcePackStack = this.server.getResourcePackManager().getResourceStack();
                            this.dataPacket(stackPacket);
                            break;
                        case ResourcePackClientResponsePacket.STATUS_COMPLETED:
                            this.processLogin();
                            break;
                    }
                    break;
                case ProtocolInfo.RESOURCE_PACK_CHUNK_REQUEST_PACKET:
                    ResourcePackChunkRequestPacket requestPacket = (ResourcePackChunkRequestPacket) packet;
                    ResourcePack resourcePack = this.server.getResourcePackManager().getPackById(requestPacket.packId);
                    if (resourcePack == null) {
                        this.close("", "disconnectionScreen.resourcePack");
                        break;
                    }

                    ResourcePackChunkDataPacket dataPacket = new ResourcePackChunkDataPacket();
                    dataPacket.packId = resourcePack.getPackId();
                    dataPacket.chunkIndex = requestPacket.chunkIndex;
                    dataPacket.data = resourcePack.getPackChunk(1048576 * requestPacket.chunkIndex, 1048576);
                    dataPacket.progress = 1048576 * requestPacket.chunkIndex;
                    this.dataPacket(dataPacket);
                    break;

                case ProtocolInfo.COMMAND_REQUEST_PACKET:
                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }
                    this.craftingType = 0;
                    CommandRequestPacket commandRequestPacket = (CommandRequestPacket) packet;
                    PlayerCommandPreprocessEvent playerCommandPreprocessEvent = new PlayerCommandPreprocessEvent(this, commandRequestPacket.command);
                    this.server.getPluginManager().callEvent(playerCommandPreprocessEvent);
                    if (playerCommandPreprocessEvent.isCancelled()) {
                        break;
                    }

                    Timings.playerCommandTimer.startTiming();
                    this.server.dispatchCommand(playerCommandPreprocessEvent.getPlayer(), playerCommandPreprocessEvent.getMessage().substring(1));
                    Timings.playerCommandTimer.stopTiming();
                    break;

                case ProtocolInfo.TEXT_PACKET:
                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }

                    this.craftingType = CRAFTING_SMALL;
                    this.resetCraftingGridType();
                    TextPacket textPacket = (TextPacket) packet;

                    if (textPacket.type == TextPacket.TYPE_CHAT) {
                        textPacket.message = this.removeFormat ? TextFormat.clean(textPacket.message) : textPacket.message;
                        for (String msg : textPacket.message.split("\n")) {
                            if (!"".equals(msg.trim()) && msg.length() <= 255 && this.messageCounter-- > 0) {
                                PlayerChatEvent chatEvent = new PlayerChatEvent(this, msg);
                                this.server.getPluginManager().callEvent(chatEvent);
                                if (!chatEvent.isCancelled()) {
                                    this.server.broadcastMessage(this.getServer().getLanguage().translateString(chatEvent.getFormat(), new String[]{chatEvent.getPlayer().getDisplayName(), chatEvent.getMessage()}), chatEvent.getRecipients());
                                }
                            }
                        }
                    }
                    break;
                case ProtocolInfo.CONTAINER_CLOSE_PACKET:
                    ContainerClosePacket containerClosePacket = (ContainerClosePacket) packet;
                    if (!this.spawned || containerClosePacket.windowId == 0) {
                        break;
                    }
                    this.craftingType = CRAFTING_SMALL;
                    this.resetCraftingGridType();
                    if (this.windowIndex.containsKey(containerClosePacket.windowId)) {
                        /*
                         * TODO PreSignChangeEvent
                         * 看板を変更する画面を閉じたときにだけ呼ぶイベント。
                         * XがウィンドウIDだが、それが看板(未調査)の場合のみ実行。

                        if(containerClosePacket.windowid == X){
                            PreSignChangeEvent presignchangeevent = new PreSignChangeEvent(blockEntity.getBlock(), this, new String[]{
                                    signText1,
                                    signText2,
                                    signText3,
                                    signText4
                            });


                            if (!blockEntity.namedTag.contains("Creator") || !Objects.equals(this.getUniqueId().toString(), blockEntity.namedTag.getString("Creator"))) {
                                presignchangeevent.setCancelled();
                            }

                            this.server.getPluginManager().callEvent(presignchangeevent);
                        }
                        */
                        this.server.getPluginManager().callEvent(new InventoryCloseEvent(this.windowIndex.get(containerClosePacket.windowId), this));
                        this.removeWindow(this.windowIndex.get(containerClosePacket.windowId));
                    } else {
                        this.windowIndex.remove(containerClosePacket.windowId);
                    }
                    break;

//                case ProtocolInfo.CRAFTING_EVENT_PACKET:
//                    CraftingEventPacket craftingEventPacket = (CraftingEventPacket) packet;
//
//                    if (!this.spawned || !this.isAlive()) {
//                        break;
//                    }
//                    System.out.println("craftpacket");
//
//                    Recipe recipe = this.server.getCraftingManager().getRecipe(craftingEventPacket.id);
//                    Recipe[] recipes = this.server.getCraftingManager().getRecipesByResult(craftingEventPacket.output[0]);
//
//                    boolean isValid = false;
//                    for (Recipe rec : recipes){
//                        if (rec.getId().equals(recipe.getId())) {
//                            isValid = true;
//                            break;
//                        }
//                    }
//                    if (isValid) recipes = new Recipe[]{recipe};
//
//                    if (!this.windowIndex.containsKey(craftingEventPacket.windowId)) {
//                        this.inventory.sendContents(this);
//                        containerClosePacket = new ContainerClosePacket();
//                        containerClosePacket.windowId = craftingEventPacket.windowId;
//                        this.dataPacket(containerClosePacket);
//                        break;
//                    }
//
//                    if (isValid && (recipe == null || (((recipe instanceof BigShapelessRecipe) || (recipe instanceof BigShapedRecipe)) && this.craftingType == CRAFTING_SMALL))) {
//                        this.inventory.sendContents(this);
//                        break;
//                    }
//
//                    for (int i = 0; i < craftingEventPacket.input.length; i++) {
//                        Item inputItem = craftingEventPacket.input[i];
//                        if (inputItem.getDamage() == -1 || inputItem.getDamage() == 0xffff) {
//                            inputItem.setDamage(null);
//                        }
//
//                        if (i < 9 && inputItem.getId() > 0) {
//                            inputItem.setCount(1);
//                        }
//                    }
//
//                    boolean canCraft = true;
//                    Map<String, Item> realSerialized = new HashMap<>();
//
//                    for (Recipe rec : recipes) {
//                        ArrayList<Item> ingredientz = new ArrayList<>();
//
//                        if (rec == null || (((rec instanceof BigShapelessRecipe) || (rec instanceof BigShapedRecipe)) && this.craftingType == CRAFTING_SMALL)) {
//                            continue;
//                        }
//
//                        if (rec instanceof ShapedRecipe) {
//                            Map<Integer, Map<Integer, Item>> ingredients = ((ShapedRecipe) rec).getIngredientMap();
//
//                            for (Map<Integer, Item> map : ingredients.values()) {
//                                for (Item ingredient : map.values()) {
//                                    if (ingredient != null && ingredient.getId() != Item.AIR) {
//                                        ingredientz.add(ingredient);
//                                    }
//                                }
//                            }
//                        } else if (recipe instanceof ShapelessRecipe) {
//                            ShapelessRecipe recipe0 = (ShapelessRecipe) recipe;
//
//                            for (Item ingredient : recipe0.getIngredientList()) {
//                                if (ingredient != null && ingredient.getId() != Item.AIR) {
//                                    ingredientz.add(ingredient);
//                                }
//                            }
//                        }
//
//                        Map<String, Item> serialized = new HashMap<>();
//
//                        for (Item ingredient : ingredientz) {
//                            String hash = ingredient.getId() + ":" + ingredient.getDamage();
//                            Item r = serialized.get(hash);
//
//                            if (r != null) {
//                                r.count += ingredient.getCount();
//                                continue;
//                            }
//
//                            serialized.put(hash, ingredient);
//                        }
//
//                        boolean isPossible = true;
//                        for (Item ingredient : serialized.values()) {
//                            if (!this.craftingGrid.contains(ingredient)) {
//                                if (isValid) {
//                                    canCraft = false;
//                                    break;
//                                }
//                                else {
//                                    isPossible = false;
//                                    break;
//                                }
//                            }
//                        }
//                        if (!isPossible) continue;
//                        recipe = rec;
//                        realSerialized = serialized;
//                        break;
//                    }
//
//                    if (!canCraft) {
//                        this.server.getLogger().debug("(1) Unmatched recipe " + craftingEventPacket.id + " from player " + this.getName() + "  not anough ingredients");
//                        return;
//                    }
//
//                    CraftItemEvent craftItemEvent = new CraftItemEvent(this, realSerialized.values().stream().toArray(Item[]::new), recipe);
//                    getServer().getPluginManager().callEvent(craftItemEvent);
//
//                    if (craftItemEvent.isCancelled()) {
//                        this.inventory.sendContents(this);
//                        break;
//                    }
//
//                    for (Item ingredient : realSerialized.values()) {
//                        this.craftingGrid.removeFromAll(ingredient);
//                    }
//
//                    this.inventory.addItem(recipe.getResult());
//
//                    break;

                case ProtocolInfo.BLOCK_ENTITY_DATA_PACKET:
                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }
                    BlockEntityDataPacket blockEntityDataPacket = (BlockEntityDataPacket) packet;
                    this.craftingType = CRAFTING_SMALL;
                    this.resetCraftingGridType();

                    pos = new Vector3(blockEntityDataPacket.x, blockEntityDataPacket.y, blockEntityDataPacket.z);
                    if (pos.distanceSquared(this) > 10000) {
                        break;
                    }

                    BlockEntity t = this.level.getBlockEntity(pos);
                    if (t instanceof BlockEntitySign) {
                        CompoundTag nbt;
                        try {
                            nbt = NBTIO.read(blockEntityDataPacket.namedTag, ByteOrder.LITTLE_ENDIAN, true);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        if (!BlockEntity.SIGN.equals(nbt.getString("id"))) {
                            ((BlockEntitySign) t).spawnTo(this);
                        } else {
                            String[] texts = nbt.getString("Text").split("\n");
                            blockEntity = t;

                            signText1 = texts.length > 0 ? texts[0] : "";
                            signText2 = texts.length > 1 ? texts[1] : "";
                            signText3 = texts.length > 2 ? texts[2] : "";
                            signText4 = texts.length > 3 ? texts[3] : "";

                            signText1 = this.removeFormat ? TextFormat.clean(signText1) : signText1;
                            signText2 = this.removeFormat ? TextFormat.clean(signText2) : signText2;
                            signText3 = this.removeFormat ? TextFormat.clean(signText3) : signText3;
                            signText4 = this.removeFormat ? TextFormat.clean(signText4) : signText4;

                            SignChangeEvent signChangeEvent = new SignChangeEvent(blockEntity.getBlock(), this, new String[]{
                                    signText1,
                                    signText2,
                                    signText3,
                                    signText4
                            });

                            if (!t.namedTag.contains("Creator") || !Objects.equals(this.getUniqueId().toString(), t.namedTag.getString("Creator"))) {
                                signChangeEvent.setCancelled();
                            }

                            this.server.getPluginManager().callEvent(signChangeEvent);

                            if (!signChangeEvent.isCancelled()) {
                                ((BlockEntitySign) t).setText(signChangeEvent.getLine(0), signChangeEvent.getLine(1), signChangeEvent.getLine(2), signChangeEvent.getLine(3));
                            } else {
                                ((BlockEntitySign) t).spawnTo(this);
                            }

                        }
                    }
                    break;
                case ProtocolInfo.REQUEST_CHUNK_RADIUS_PACKET:
                    RequestChunkRadiusPacket requestChunkRadiusPacket = (RequestChunkRadiusPacket) packet;
                    ChunkRadiusUpdatedPacket chunkRadiusUpdatePacket = new ChunkRadiusUpdatedPacket();
                    this.chunkRadius = Math.max(5, Math.min(requestChunkRadiusPacket.radius, this.viewDistance));
                    chunkRadiusUpdatePacket.radius = this.chunkRadius;
                    this.dataPacket(chunkRadiusUpdatePacket);
                    break;
                case ProtocolInfo.SET_PLAYER_GAME_TYPE_PACKET:
                    SetPlayerGameTypePacket setPlayerGameTypePacket = (SetPlayerGameTypePacket) packet;
                    if (setPlayerGameTypePacket.gamemode != this.gamemode) {
                        if (!this.hasPermission("nukkit.command.gamemode")) {
                            SetPlayerGameTypePacket setPlayerGameTypePacket1 = new SetPlayerGameTypePacket();
                            setPlayerGameTypePacket1.gamemode = this.gamemode & 0x01;
                            this.dataPacket(setPlayerGameTypePacket1);
                            this.getAdventureSettings().update();
                            break;
                        }
                        this.setGamemode(setPlayerGameTypePacket.gamemode, true);
                        Command.broadcastCommandMessage(this, new TranslationContainer("commands.gamemode.success.self", Server.getGamemodeString(this.gamemode)));
                    }
                    break;
                case ProtocolInfo.ITEM_FRAME_DROP_ITEM_PACKET:
                    ItemFrameDropItemPacket itemFrameDropItemPacket = (ItemFrameDropItemPacket) packet;
                    Vector3 vector3 = this.temporalVector.setComponents(itemFrameDropItemPacket.x, itemFrameDropItemPacket.y, itemFrameDropItemPacket.z);
                    BlockEntity blockEntityItemFrame = this.level.getBlockEntity(vector3);
                    BlockEntityItemFrame itemFrame = (BlockEntityItemFrame) blockEntityItemFrame;
                    if (itemFrame != null) {
                        block = itemFrame.getBlock();
                        Item itemDrop = itemFrame.getItem();
                        ItemFrameDropItemEvent itemFrameDropItemEvent = new ItemFrameDropItemEvent(this, block, itemFrame, itemDrop);
                        this.server.getPluginManager().callEvent(itemFrameDropItemEvent);
                        if (!itemFrameDropItemEvent.isCancelled()) {
                            if (itemDrop.getId() != Item.AIR) {
                                vector3 = this.temporalVector.setComponents(itemFrame.x + 0.5, itemFrame.y, itemFrame.z + 0.5);
                                this.level.dropItem(vector3, itemDrop);
                                itemFrame.setItem(new ItemBlock(new BlockAir()));
                                itemFrame.setItemRotation(0);
                                this.getLevel().addSound(new ItemFrameItemRemovedSound(this));
                            }
                        } else {
                            itemFrame.spawnTo(this);
                        }
                    }
                    break;
                case ProtocolInfo.MAP_INFO_REQUEST_PACKET:
                    MapInfoRequestPacket pk = (MapInfoRequestPacket) packet;
                    Item mapItem = null;

                    for (Item item1 : this.inventory.getContents().values()) {
                        if (item1 instanceof ItemMap && ((ItemMap) item1).getMapId() == pk.mapId) {
                            mapItem = item1;
                        }
                    }

                    if (mapItem == null) {
                        for (BlockEntity be : this.level.getBlockEntities().values()) {
                            if (be instanceof BlockEntityItemFrame) {
                                BlockEntityItemFrame itemFrame1 = (BlockEntityItemFrame) be;

                                if (itemFrame1.getItem() instanceof ItemMap && ((ItemMap) itemFrame1.getItem()).getMapId() == pk.mapId) {
                                    ((ItemMap) itemFrame1.getItem()).sendImage(this);

                                    break;
                                }
                            }
                        }
                    } else {
                        PlayerMapInfoRequestEvent event;
                        getServer().getPluginManager().callEvent(event = new PlayerMapInfoRequestEvent(this, mapItem));

                        if (!event.isCancelled()) {
                            ((ItemMap) mapItem).sendImage(this);
                            try {
                                ItemMap map2 = new ItemMap();
                                this.sendImage(this.createMap((ItemMap)map2), (ItemMap)map2);//TODO sendImage
                                this.getInventory().addItem(map2);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                    break;

                case ProtocolInfo.LEVEL_SOUND_EVENT_PACKET:
                    if (this.isBreakingBlock()) {
                        LevelSoundEventPacket pk1 = new LevelSoundEventPacket();
                        pk1.sound = LevelSoundEventPacket.SOUND_HIT;
                        pk1.extraData = this.breakingBlock.getId();
                        pk1.pitch = 1;
                        pk1.x = (float) this.breakingBlock.x;
                        pk1.y = (float) this.breakingBlock.y;
                        pk1.z = (float) this.breakingBlock.z;

                        this.level.addChunkPacket(this.getFloorX() >> 4, this.getFloorZ() >> 4, pk1);
                    }
                    break;

                case ProtocolInfo.MODAL_FORM_RESPONSE_PACKET:
                    ModalFormResponsePacket modalFormResponsePacket = (ModalFormResponsePacket) packet;

                    if(this.activeWindow != null && this.activeWindow.getId() == modalFormResponsePacket.formId) {
                        if (modalFormResponsePacket.data.trim().equals("null")) {
                            PlayerModalFormCloseEvent mfce = new PlayerModalFormCloseEvent(this, modalFormResponsePacket.formId, this.activeWindow);
                            this.activeWindow = null;
                            this.getServer().getPluginManager().callEvent(mfce);
                        } else {
                            this.activeWindow.setResponse(modalFormResponsePacket.data);
                            PlayerModalFormResponseEvent mfre = new PlayerModalFormResponseEvent(this, modalFormResponsePacket.formId, this.activeWindow);
                            this.activeWindow = null;
                            this.getServer().getPluginManager().callEvent(mfre);
                        }
                    } else {
                        this.serverSettings.get(modalFormResponsePacket.formId).setResponse(modalFormResponsePacket.data);
                        PlayerServerSettingsChangedEvent ssce = new PlayerServerSettingsChangedEvent(this, modalFormResponsePacket.formId, this.serverSettings.get(modalFormResponsePacket.formId));
                        this.getServer().getPluginManager().callEvent(ssce);
                    }

                    break;

                case ProtocolInfo.COMMAND_BLOCK_UPDATE_PACKET:
                    if (!(this.isOp() && this.isCreative())) {
                        break;
                    }
                    CommandBlockUpdatePacket update = (CommandBlockUpdatePacket) packet;
                    if (update.isBlock) {
                        Vector3 commandPos = new Vector3(update.x, update.y, update.z);
                        block = this.level.getBlock(commandPos);
                        if (block instanceof BlockCommand) {
                            BlockEntityCommandBlock blockEntity = ((BlockCommand)block).getBlockEntity();
                            if (blockEntity == null) {
                                break;
                            }
                            Block place = Block.get(Block.COMMAND_BLOCK);
                            switch (update.commandBlockMode) {
                                case 0:
                                    place = Block.get(Block.COMMAND_BLOCK);
                                    place.setDamage(block.getDamage());
                                    break;
                                case 1:
                                    place = Block.get(Block.REPEATING_COMMAND_BLOCK);
                                    place.setDamage(block.getDamage());
                                    break;
                                case 2:
                                    place = Block.get(Block.CHAIN_COMMAND_BLOCK);
                                    place.setDamage(block.getDamage());
                                    break;
                            }
                            if (update.isConditional) {
                                if (place.getDamage() < 8) {
                                    place.setDamage(place.getDamage() + 8);
                                }
                            } else {
                                if (place.getDamage() > 8) {
                                    place.setDamage(place.getDamage() - 8);
                                }
                            }
                            this.level.setBlock(block, place, false, false);
                            blockEntity = (BlockEntityCommandBlock) blockEntity.clone();
                            blockEntity.setName(update.name);
                            blockEntity.setMode(update.commandBlockMode);
                            blockEntity.setCommand(update.command);
                            blockEntity.setLastOutPut(update.lastOutput);
                            blockEntity.setAuto(!update.isRedstoneMode);
                            blockEntity.setConditions(update.isConditional);
                            blockEntity.spawnToAll();
                        }
                    } else {
                        //MinercartCommandBlock
                    }
                    break;

                case ProtocolInfo.SERVER_SETTINGS_REQUEST_PACKET:
                    this.serverSettings.forEach((id, window) -> {
                        ServerSettingsResponsePacket re = new ServerSettingsResponsePacket();
                        re.formId = id;
                        re.data = window.toJson();
                        this.dataPacket(re);
                    });
                    break;

                case ProtocolInfo.PLAYER_SKIN_PACKET:
                    PlayerSkinPacket skin = (PlayerSkinPacket) packet;
                    this.setSkin(skin.skin);
                    break;

                default:
                    break;
            }
        }
    }


    public BufferedImage createMap(ItemMap mapItem) {
        new ArrayList<>();
        List<BaseFullChunk> chunks = new ArrayList<>();
        Color[][] blockColors = new Color[16][16];
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D)img.getGraphics();

        chunks.add(this.level.getChunk((int) this.x >> 4, (int) this.z >> 4, false));
        for(int x=0;x < 16;x++){
            for( int y=0;y < 16;y++){
                blockColors[x][y] = Block.get(chunks.get(0).getHighestBlockAt((int)this.x, (int)this.z)).getColor();
                g2.drawImage(img, x, y, blockColors[x][y], null);
            }
        }

        BufferedImage data = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        data.createGraphics().drawImage(img, 0, 0, null);
        return data;
    }

    /**
     * プレイヤーをサーバーから追放します。
     * @return void
     */
    public boolean kick() {
        return this.kick("");
    }

    /**
     * プレイヤーをサーバーから追放します。
     * 理由はPlayerkickEvent.Reason.UNKNOWNが使われます。
     * @param reason 理由文
     * @param isAdmin kicked by admin.を表示するかどうか
     * @return boolean
     * @see PlayerKickEvent.Reason#UNKNOWN
     */
    public boolean kick(String reason, boolean isAdmin) {
        return this.kick(PlayerKickEvent.Reason.UNKNOWN, reason, isAdmin);
    }

    /**
     * プレイヤーをサーバーから追放します。
     * 理由はPlayerkickEvent.Reason.UNKNOWNが使われます。
     * @param reason 理由文
     * @return boolean
     * @see PlayerKickEvent.Reason#UNKNOWN
     */
    public boolean kick(String reason) {
        return kick(PlayerKickEvent.Reason.UNKNOWN, reason);
    }

    /**
     * プレイヤーをサーバーから追放します。
     * kicked by admin.が表示されます。
     * @param reason 理由
     * @return boolean
     * @see "理由"
     * @see PlayerKickEvent.Reason#FLYING_DISABLED
     * @see PlayerKickEvent.Reason#INVALID_PVE
     * @see PlayerKickEvent.Reason#IP_BANNED
     * @see PlayerKickEvent.Reason#KICKED_BY_ADMIN
     * @see PlayerKickEvent.Reason#LOGIN_TIMEOUT
     * @see PlayerKickEvent.Reason#NAME_BANNED
     * @see PlayerKickEvent.Reason#NEW_CONNECTION
     * @see PlayerKickEvent.Reason#NOT_WHITELISTED
     * @see PlayerKickEvent.Reason#SERVER_FULL
     * @see PlayerKickEvent.Reason#UNKNOWN
     */
    public boolean kick(PlayerKickEvent.Reason reason) {
        return this.kick(reason, true);
    }

    /**
     * プレイヤーをサーバーから追放します。
     * kicked by admin.が表示されます。
     * @param reason 理由
     * @param reasonString 理由文
     * @return boolean
     * @see "理由"
     * @see PlayerKickEvent.Reason#FLYING_DISABLED
     * @see PlayerKickEvent.Reason#INVALID_PVE
     * @see PlayerKickEvent.Reason#IP_BANNED
     * @see PlayerKickEvent.Reason#KICKED_BY_ADMIN
     * @see PlayerKickEvent.Reason#LOGIN_TIMEOUT
     * @see PlayerKickEvent.Reason#NAME_BANNED
     * @see PlayerKickEvent.Reason#NEW_CONNECTION
     * @see PlayerKickEvent.Reason#NOT_WHITELISTED
     * @see PlayerKickEvent.Reason#SERVER_FULL
     * @see PlayerKickEvent.Reason#UNKNOWN
     */
    public boolean kick(PlayerKickEvent.Reason reason, String reasonString) {
        return this.kick(reason, reasonString, true);
    }

    /**
     * プレイヤーをサーバーから追放します。
     * @param reason 理由
     * @param isAdmin kicked by admin.を表示するかどうか
     * @return boolean
     * @see "理由"
     * @see PlayerKickEvent.Reason#FLYING_DISABLED
     * @see PlayerKickEvent.Reason#INVALID_PVE
     * @see PlayerKickEvent.Reason#IP_BANNED
     * @see PlayerKickEvent.Reason#KICKED_BY_ADMIN
     * @see PlayerKickEvent.Reason#LOGIN_TIMEOUT
     * @see PlayerKickEvent.Reason#NAME_BANNED
     * @see PlayerKickEvent.Reason#NEW_CONNECTION
     * @see PlayerKickEvent.Reason#NOT_WHITELISTED
     * @see PlayerKickEvent.Reason#SERVER_FULL
     * @see PlayerKickEvent.Reason#UNKNOWN
     */
    public boolean kick(PlayerKickEvent.Reason reason, boolean isAdmin) {
        return this.kick(reason, reason.toString(), isAdmin);
    }

    /**
     * プレイヤーをサーバーから追放します。
     * @param reason 理由
     * @param reasonString 理由文
     * @param isAdmin kicked by admin.を表示するかどうか
     * @return boolean
     * @see "理由"
     * @see PlayerKickEvent.Reason#FLYING_DISABLED
     * @see PlayerKickEvent.Reason#INVALID_PVE
     * @see PlayerKickEvent.Reason#IP_BANNED
     * @see PlayerKickEvent.Reason#KICKED_BY_ADMIN
     * @see PlayerKickEvent.Reason#LOGIN_TIMEOUT
     * @see PlayerKickEvent.Reason#NAME_BANNED
     * @see PlayerKickEvent.Reason#NEW_CONNECTION
     * @see PlayerKickEvent.Reason#NOT_WHITELISTED
     * @see PlayerKickEvent.Reason#SERVER_FULL
     * @see PlayerKickEvent.Reason#UNKNOWN
     */
    public boolean kick(PlayerKickEvent.Reason reason, String reasonString, boolean isAdmin) {
        PlayerKickEvent ev;
        this.server.getPluginManager().callEvent(ev = new PlayerKickEvent(this, reason, this.getLeaveMessage()));
        if (!ev.isCancelled()) {
            String message;
            if (isAdmin) {
                if (!this.isBanned()) {
                    message = "Kicked by admin." + (!"".equals(reasonString) ? " Reason: " + reasonString : "");
                } else {
                    message = reasonString;
                }
            } else {
                if ("".equals(reasonString)) {
                    message = "disconnectionScreen.noReason";
                } else {
                    message = reasonString;
                }
            }

            this.close(ev.getQuitMessage(), message);

            return true;
        }

        return false;
    }

    /**
     * プレイヤーにメッセージを送信します。
     * ミュート状態では表示されません。
     * (ミュート状態...isMuted()の戻り値)
     * @see "ミュート状態でも表示したい場合"
     * @see Player#sendImportantMessage(String) sendImportantMessage
     * @see Player#isMuted() isMuted()
     * @param message メッセージ内容
     * @return void
     */
    @Override
    public void sendMessage(String message) {
        if(mute)return;
        TextPacket pk = new TextPacket();
        pk.type = TextPacket.TYPE_RAW;
        pk.message = this.server.getLanguage().translateString(message);
        this.dataPacket(pk);
    }

    @Override
    public void sendMessage(TextContainer message) {
        if(mute)return;
        if (message instanceof TranslationContainer) {
            this.sendTranslation(message.getText(), ((TranslationContainer) message).getParameters());
            return;
        }
        this.sendMessage(message.getText());
    }

    public void sendTranslation(String message) {
        if(mute)return;
        this.sendTranslation(message, new String[0]);
    }

    public void sendTranslation(String message, String[] parameters) {
        if(mute)return;
        TextPacket pk = new TextPacket();
        if (!this.server.isLanguageForced()) {
            pk.type = TextPacket.TYPE_TRANSLATION;
            pk.message = this.server.getLanguage().translateString(message, parameters, "nukkit.");
            for (int i = 0; i < parameters.length; i++) {
                parameters[i] = this.server.getLanguage().translateString(parameters[i], parameters, "nukkit.");

            }
            pk.parameters = parameters;
        } else {
            pk.type = TextPacket.TYPE_RAW;
            pk.message = this.server.getLanguage().translateString(message, parameters);
        }
        this.dataPacket(pk);
    }

    /**
     * プレイヤーにポップアップを送信します。
     * ミュート状態では表示されません。
     * @param message メッセージ内容
     * @return void
     */
    public void sendPopup(String message) {
        if(mute)return;
        this.sendPopup(message, "");
    }

    /**
     * プレイヤーにポップアップを送信します。
     * ミュート状態では表示されません。
     * @param message メッセージ内容
     * @param subtitle サブタイトル
     * @return void
     */
    public void sendPopup(String message, String subtitle) {
        if(mute)return;
        TextPacket pk = new TextPacket();
        pk.type = TextPacket.TYPE_POPUP;
        pk.source = message;
        pk.message = subtitle;
        this.dataPacket(pk);
    }

    /**
     * プレイヤーにチップを送信します。
     * ミュート状態では表示されません。
     * @param message メッセージ内容
     * @return void
     */
    public void sendTip(String message) {
        if(mute)return;
        TextPacket pk = new TextPacket();
        pk.type = TextPacket.TYPE_TIP;
        pk.message = message;
        this.dataPacket(pk);
    }

    /**
     * プレイヤーにメッセージを送信します。
     * ただし、ミュート状態でも表示されます。
     * @param message メッセージ内容
     * @return void
     * @author Itsu
     */
    public void sendImportantMessage(String message) {
        TextPacket pk = new TextPacket();
        pk.type = TextPacket.TYPE_RAW;
        pk.message = this.server.getLanguage().translateString(message);
        this.dataPacket(pk);
    }

    public void clearTitle() {
        SetTitlePacket pk = new SetTitlePacket();
        pk.type = SetTitlePacket.TYPE_CLEAR;
        this.dataPacket(pk);
    }

    /**
     * Resets both title animation times and subtitle for the next shown title
     */
    public void resetTitleSettings() {
        SetTitlePacket pk = new SetTitlePacket();
        pk.type = SetTitlePacket.TYPE_RESET;
        this.dataPacket(pk);
    }

    /**
     * プレイヤーにタイトルを送信します。
     * ミュート状態では表示されません。
     * @param text タイトル内容
     * @return void
     */
    public void sendTitle(String text) {
        if(mute)return;
        SetTitlePacket pk = new SetTitlePacket();
        pk.type = SetTitlePacket.TYPE_TITLE;
        pk.text = text;
        this.dataPacket(pk);
    }

    /**
     * プレイヤーにサブタイトルを送信します。
     * ミュート状態では表示されません。
     * @param text サブタイトル内容
     * @return void
     */
    public void setSubtitle(String text) {
        if(mute)return;
        SetTitlePacket pk = new SetTitlePacket();
        pk.type = SetTitlePacket.TYPE_SUBTITLE;
        pk.text = text;
        this.dataPacket(pk);
    }

    public void sendActionBarTitle(String text) {
        if(mute)return;
        SetTitlePacket pk = new SetTitlePacket();
        pk.type = SetTitlePacket.TYPE_ACTION_BAR;
        pk.text = text;
        this.dataPacket(pk);
    }

    /**
     * Sets times for title animations
     * @param fadeInTime For how long title fades in
     * @param stayTime For how long title is shown
     * @param fadeOutTime For how long title fades out
     */
    public void setTitleAnimationTimes(int fadeInTime, int stayTime, int fadeOutTime) {
        SetTitlePacket pk = new SetTitlePacket();
        pk.type = SetTitlePacket.TYPE_ANIMATION_TIMES;
        pk.fadeInTime = fadeInTime;
        pk.stayTime = stayTime;
        pk.fadeOutTime = fadeOutTime;
        this.dataPacket(pk);
    }

    /**
     * 128x128の大きさでMapにpathの画像を貼り付けます。
     * @param path 画像のパス
     * @param item Map
     * @return void
     * @author Megapix96
     * @see "幅と高さを指定する場合"
     * @see Player#sendImage(String, ItemMap, int, int) sendImage(String, ItemMap, int, int)
     */
    public void sendImage(String path, ItemMap item) throws IOException{
        this.sendImage(path, item, 128, 128);
    }

    /**
     * 128x128の大きさでMapに画像を貼り付けます。
     * @param img 画像
     * @param item Map
     * @return void
     * @author Megapix96
     * @see "幅と高さを指定する場合"
     * @see Player#sendImage(String, ItemMap, int, int) sendImage(String, ItemMap, int, int)
     */
    public void sendImage(BufferedImage img, ItemMap item) throws IOException{
        this.sendImage(img, item, 128, 128);
    }

    /**
     * Mapにpathの画像を貼り付けます。
     * @param path 画像のパス
     * @param item Map
     * @param width 幅
     * @param height 高さ
     * @return void
     * @author Megapix96
     */
    public void sendImage(String path, ItemMap item, int width, int height) throws IOException{
        item.setImage(new File(path));

        ClientboundMapItemDataPacket pk = new ClientboundMapItemDataPacket();
        pk.mapId = item.getMapId();
        pk.update = 2;
        pk.scale = 0;
        pk.width = width;
        pk.height = height;
        pk.offsetX = 0;
        pk.offsetZ = 0;
        pk.image = item.loadImageFromNBT();

        this.dataPacket(pk);
    }

    /**
     * Mapにpathの画像を貼り付けます。
     * @param img 画像
     * @param item Map
     * @param width 幅
     * @param height 高さ
     * @return void
     * @author Megapix96
     */
    public void sendImage(BufferedImage img, ItemMap item, int width, int height) throws IOException{
        item.setImage(img);

        ClientboundMapItemDataPacket pk = new ClientboundMapItemDataPacket();
        pk.mapId = item.getMapId();
        pk.update = 2;
        pk.scale = 0;
        pk.width = width;
        pk.height = height;
        pk.offsetX = 0;
        pk.offsetZ = 0;
        pk.image = item.loadImageFromNBT();

        this.dataPacket(pk);
    }

    @Override
    public void close() {
        this.close("");
    }

    public void close(String message) {
        this.close(message, "generic");
    }

    public void close(String message, String reason) {
        this.close(message, reason, true);
    }

    public void close(String message, String reason, boolean notify) {
        this.close(new TextContainer(message), reason, notify);
    }

    public void close(TextContainer message) {
        this.close(message, "generic");
    }

    public void close(TextContainer message, String reason) {
        this.close(message, reason, true);
    }

    public void close(TextContainer message, String reason, boolean notify) {

        this.unlinkHookFromPlayer();

        if (this.connected && !this.closed) {
            if (notify && reason.length() > 0) {
                DisconnectPacket pk = new DisconnectPacket();
                pk.message = reason;
                this.directDataPacket(pk);
            }
//
            this.connected = false;
            PlayerQuitEvent ev = null;
            if (this.getName() != null && this.getName().length() > 0) {
                this.server.getPluginManager().callEvent(ev = new PlayerQuitEvent(this, message, true, reason));
                if (this.loggedIn && ev.getAutoSave()) {
                    this.save();
                }
            }

            for (Player player : this.server.getOnlinePlayers().values()) {
                if (!player.canSee(this)) {
                    player.showPlayer(this);
                }
            }

            this.hiddenPlayers = new HashMap<>();

            this.removeAllWindows(true);

            for (long index : new ArrayList<>(this.usedChunks.keySet())) {
                int chunkX = Level.getHashX(index);
                int chunkZ = Level.getHashZ(index);
                this.level.unregisterChunkLoader(this, chunkX, chunkZ);
                this.usedChunks.remove(index);
            }

            super.close();

            this.interfaz.close(this, notify ? reason : "");

            if (this.loggedIn) {
                this.server.removeOnlinePlayer(this);
            }

            this.loggedIn = false;

            if (ev != null && !Objects.equals(this.username, "") && this.spawned && !Objects.equals(ev.getQuitMessage().toString(), "") && this.getServer().getJupiterConfigBoolean("join-quit-message")) {
                this.server.broadcastMessage(ev.getQuitMessage());
            }

            this.server.getPluginManager().unsubscribeFromPermission(Server.BROADCAST_CHANNEL_USERS, this);
            this.spawned = false;
            this.server.getLogger().info(this.getServer().getLanguage().translateString("nukkit.player.logOut",
                    TextFormat.AQUA + (this.getName() == null ? "" : this.getName()) + TextFormat.WHITE,
                    this.ip,
                    String.valueOf(this.port),
                    this.getServer().getLanguage().translateString(reason)));
            this.windows = new HashMap<>();
            this.windowIndex = new HashMap<>();
            this.usedChunks = new HashMap<>();
            this.loadQueue = new HashMap<>();
            this.hasSpawned = new HashMap<>();
            this.spawnPosition = null;

            if (this.riding instanceof EntityVehicle) {
                ((EntityVehicle) this.riding).linkedEntity = null;
            }

            this.riding = null;
        }

        if (this.perm != null) {
            this.perm.clearPermissions();
            this.perm = null;
        }

        if (this.inventory != null) {
            this.inventory = null;
        }

        this.chunk = null;

        this.server.removePlayer(this);
    }

    public void save() {
        this.save(false);
    }

    public void save(boolean async) {
        if (this.closed) {
            throw new IllegalStateException("Tried to save closed player");
        }

        super.saveNBT();

        if (this.level != null) {
            this.namedTag.putString("Level", this.level.getFolderName());
            if (this.spawnPosition != null && this.spawnPosition.getLevel() != null) {
                this.namedTag.putString("SpawnLevel", this.spawnPosition.getLevel().getFolderName());
                this.namedTag.putInt("SpawnX", (int) this.spawnPosition.x);
                this.namedTag.putInt("SpawnY", (int) this.spawnPosition.y);
                this.namedTag.putInt("SpawnZ", (int) this.spawnPosition.z);
            }

            CompoundTag achievements = new CompoundTag();
            for (String achievement : this.achievements) {
                achievements.putByte(achievement, 1);
            }

            this.namedTag.putCompound("Achievements", achievements);

            this.namedTag.putInt("playerGameType", this.gamemode);
            this.namedTag.putLong("lastPlayed", System.currentTimeMillis() / 1000);

            this.namedTag.putString("lastIP", this.getAddress());

            this.namedTag.putInt("EXP", this.getExperience());
            this.namedTag.putInt("expLevel", this.getExperienceLevel());

            this.namedTag.putInt("foodLevel", this.getFoodData().getLevel());
            this.namedTag.putFloat("foodSaturationLevel", this.getFoodData().getFoodSaturationLevel());

            if (!"".equals(this.username) && this.namedTag != null) {
                this.server.saveOfflinePlayerData(this.username, this.namedTag, async);
            }
        }
    }

    /**
     * プレイヤー名を取得します。
     * @return String プレイヤー名
     */
    public String getName() {
        if (this.username == null) {
            return null;
        }
        synchronized(this.username){
            return this.username;
        }
    }

    /**
     * プレイヤーを殺害します。
     * @return void
     */
    @Override
    public void kill() {
        if (!this.spawned) {
            return;
        }

        String message = "death.attack.generic";

        List<String> params = new ArrayList<>();
        params.add(this.getDisplayName());

        EntityDamageEvent cause = this.getLastDamageCause();

        switch (cause == null ? DamageCause.CUSTOM : cause.getCause()) {
              case ENTITY_ATTACK:
                if (cause instanceof EntityDamageByEntityEvent) {
                    Entity e = ((EntityDamageByEntityEvent) cause).getDamager();
                    killer = e;
                    if (e instanceof Player) {
                        message = "death.attack.player";
                        params.add(((Player) e).getDisplayName());
                        break;
                    } else if (e instanceof EntityLiving) {
                        message = "death.attack.mob";
                        params.add(!Objects.equals(e.getNameTag(), "") ? e.getNameTag() : e.getName());
                        break;
                    } else {
                        params.add("Unknown");
                    }
                }
                break;
            case PROJECTILE:
                if (cause instanceof EntityDamageByEntityEvent) {
                    Entity e = ((EntityDamageByEntityEvent) cause).getDamager();
                    killer = e;
                    if (e instanceof Player) {
                        message = "death.attack.arrow";
                        params.add(((Player) e).getDisplayName());
                    } else if (e instanceof EntityLiving) {
                        message = "death.attack.arrow";
                        params.add(!Objects.equals(e.getNameTag(), "") ? e.getNameTag() : e.getName());
                        break;
                    } else {
                        params.add("Unknown");
                    }
                }
                break;
            case SUICIDE:
                message = "death.attack.generic";
                break;
            case VOID:
                message = "death.attack.outOfWorld";
                break;
            case FALL:
                if (cause != null) {
                    if (cause.getFinalDamage() > 2) {
                        message = "death.fell.accident.generic";
                        break;
                    }
                }
                message = "death.attack.fall";
                break;

            case SUFFOCATION:
                message = "death.attack.inWall";
                break;

            case LAVA:
                message = "death.attack.lava";
                break;

            case FIRE:
                message = "death.attack.onFire";
                break;

            case FIRE_TICK:
                message = "death.attack.inFire";
                break;

            case DROWNING:
                message = "death.attack.drown";
                break;

            case CONTACT:
                if (cause instanceof EntityDamageByBlockEvent) {
                    if (((EntityDamageByBlockEvent) cause).getDamager().getId() == Block.CACTUS) {
                        message = "death.attack.cactus";
                    }
                }
                break;

            case BLOCK_EXPLOSION:
            case ENTITY_EXPLOSION:
                if (cause instanceof EntityDamageByEntityEvent) {
                    Entity e = ((EntityDamageByEntityEvent) cause).getDamager();
                    killer = e;
                    if (e instanceof Player) {
                        message = "death.attack.explosion.player";
                        params.add(((Player) e).getDisplayName());
                    } else if (e instanceof EntityLiving) {
                        message = "death.attack.explosion.player";
                        params.add(!Objects.equals(e.getNameTag(), "") ? e.getNameTag() : e.getName());
                        break;
                    }
                } else {
                    message = "death.attack.explosion";
                }
                break;

            case MAGIC:
                message = "death.attack.magic";
                break;

            case CUSTOM:
                break;

            default:

        }

        this.health = 0;
        this.scheduleUpdate();

        PlayerDeathEvent ev = new PlayerDeathEvent(this, this.getDrops(), new TranslationContainer(message, params.stream().toArray(String[]::new)), this.getExperienceLevel());

        if (this.keepInventory){
            ev.setKeepInventory(true);
        }
        if (this.keepExperience){
            ev.setKeepExperience(true);
        }
        this.server.getPluginManager().callEvent(ev);


        if (!ev.getKeepInventory()) {
            for (Item item : ev.getDrops()) {
                this.level.dropItem(this, item, null, true, 40);
            }

            if (this.inventory != null) {
                this.inventory.clearAll();
            }
        }

        if (this.keepExperience){
            ev.setKeepExperience(true);
        }
        if (!ev.getKeepExperience()) {
            if (this.isSurvival() || this.isAdventure()) {
                int exp = ev.getExperience() * 7;
                if (exp > 100) exp = 100;
                int add = 1;
                for (int ii = 1; ii < exp; ii += add) {
                    this.getLevel().dropExpOrb(this, add);
                    add = new NukkitRandom().nextRange(1, 3);
                }
            }
            this.setExperience(0, 0);
        }

        if (!Objects.equals(ev.getDeathMessage().toString(), "")) {
            this.server.broadcast(ev.getDeathMessage(), Server.BROADCAST_CHANNEL_USERS);
        }


        RespawnPacket pk = new RespawnPacket();
        Position pos = this.getSpawn();
        pk.x = (float) pos.x;
        pk.y = (float) pos.y;
        pk.z = (float) pos.z;
        this.dataPacket(pk);
    }

    /**
     * プレイヤーの体力を設定します。
     * @param health 設定する体力量
     * @return void
     */
    @Override
    public void setHealth(float health) {
        if (health < 1) {
            health = 0;
        }

        super.setHealth(health);
        Attribute attr = Attribute.getAttribute(Attribute.MAX_HEALTH).setMaxValue(this.getAbsorption() % 2 != 0 ? this.getMaxHealth() + 1 : this.getMaxHealth()).setValue(health > 0 ? (health < getMaxHealth() ? health : getMaxHealth()) : 0);
        if (this.spawned) {
            UpdateAttributesPacket pk = new UpdateAttributesPacket();
            pk.entries = new Attribute[]{attr};
            pk.entityRuntimeId = this.id;
            this.dataPacket(pk);
        }
    }

    /**
     * プレイヤーの経験値を取得します。
     * @return int 経験値
     */
    public int getExperience() {
        return this.exp;
    }

    /**
     * プレイヤーの経験値レベルを取得します。
     * @return int 経験値レベル
     */
    public int getExperienceLevel() {
        return this.expLevel;
    }

    /**
     * プレイヤーの経験値を追加します。
     * @param add 追加する経験値量
     * @return void
     */
    public void addExperience(int add) {
        if (add == 0) return;
        int now = this.getExperience();
        int added = now + add;
        int level = this.getExperienceLevel();
        int most = calculateRequireExperience(level);
        while (added >= most) {  //Level Up!
            added = added - most;
            level++;
            most = calculateRequireExperience(level);
        }
        this.setExperience(added, level);
    }

    public static int calculateRequireExperience(int level) {
        if (level >= 30) {
            return 112 + (level - 30) * 9;
        } else if (level >= 15) {
            return 37 + (level - 15) * 5;
        } else {
            return 7 + level * 2;
        }
    }

    /**
     * プレイヤーの経験値を設定します。
     * @param exp 設定する経験値量
     * @return void
     */
    public void setExperience(int exp) {
        setExperience(exp, this.getExperienceLevel());
    }

    //todo something on performance, lots of exp orbs then lots of packets, could crash client

    /**
     * プレイヤーの経験値と経験値レベルを設定します。
     * @param exp 設定する経験値量
     * @param level 設定する経験値レベル
     * @return void
     */
    public void setExperience(int exp, int level) {
        this.exp = exp;
        this.expLevel = level;

        this.sendExperienceLevel(level);
        this.sendExperience(exp);
    }

    public void sendExperience() {
        sendExperience(this.getExperience());
    }

    public void sendExperience(int exp) {
        if (this.spawned) {
            float percent = ((float) exp) / calculateRequireExperience(this.getExperienceLevel());
            this.setAttribute(Attribute.getAttribute(Attribute.EXPERIENCE).setValue(percent));
        }
    }

    public void sendExperienceLevel() {
        sendExperienceLevel(this.getExperienceLevel());
    }

    public void sendExperienceLevel(int level) {
        if (this.spawned) {
            this.setAttribute(Attribute.getAttribute(Attribute.EXPERIENCE_LEVEL).setValue(level));
        }
    }

    public void setAttribute(Attribute attribute) {
        UpdateAttributesPacket pk = new UpdateAttributesPacket();
        pk.entries = new Attribute[]{attribute};
        pk.entityRuntimeId = this.id;
        this.dataPacket(pk);
    }

    @Override
    public void setMovementSpeed(float speed) {
        super.setMovementSpeed(speed);
        if (this.spawned) {
            Attribute attribute = Attribute.getAttribute(Attribute.MOVEMENT_SPEED).setValue(speed);
            this.setAttribute(attribute);
        }
    }

    public Entity getKiller() {
        return killer;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (!this.isAlive()) {
            return false;
        }

        if (this.isCreative()
                && source.getCause() != DamageCause.MAGIC
                && source.getCause() != DamageCause.SUICIDE
                && source.getCause() != DamageCause.VOID
                ) {
                //source.setCancelled();
                return false;
        } else if (this.getAdventureSettings().get(Type.ALLOW_FLIGHT) && source.getCause() == DamageCause.FALL) {
                //source.setCancelled();
                return false;
        } else if (source.getCause() == DamageCause.FALL) {
            if (this.getLevel().getBlock(this.getPosition().floor().add(0.5, -1, 0.5)).getId() == Block.SLIME_BLOCK) {
                if (!this.isSneaking()) {
                    //source.setCancelled();
                    return false;
                }
            }
        }

        if (source instanceof EntityDamageByEntityEvent) {
            Entity damager = ((EntityDamageByEntityEvent) source).getDamager();
            if (damager instanceof Player) {
                ((Player) damager).getFoodData().updateFoodExpLevel(0.3);
            }

            if (!damager.onGround) {
                NukkitRandom random = new NukkitRandom();
                for (int i = 0; i < 5; i++) {
                    CriticalParticle par = new CriticalParticle(new Vector3(this.x + random.nextRange(-15, 15) / 10, this.y + random.nextRange(0, 20) / 10, this.z + random.nextRange(-15, 15) / 10));
                    this.getLevel().addParticle(par);
                }

                source.setDamage((float) (source.getDamage() * 1.5));
            }
        }

        if (super.attack(source)) { //!source.isCancelled()
            if (this.getLastDamageCause() == source && this.spawned) {
                this.getFoodData().updateFoodExpLevel(0.3);
                EntityEventPacket pk = new EntityEventPacket();
                pk.entityRuntimeId = this.id;
                pk.event = EntityEventPacket.HURT_ANIMATION;
                this.dataPacket(pk);
            }
            return true;

        } else {
            return false;
        }
    }

    public void sendPosition(Vector3 pos) {
        this.sendPosition(pos, this.yaw);
    }

    public void sendPosition(Vector3 pos, double yaw) {
        this.sendPosition(pos, yaw, this.pitch);
    }

    public void sendPosition(Vector3 pos, double yaw, double pitch) {
        this.sendPosition(pos, yaw, pitch, MovePlayerPacket.MODE_NORMAL);
    }

    public void sendPosition(Vector3 pos, double yaw, double pitch, int mode) {
        this.sendPosition(pos, yaw, pitch, mode, null);
    }

    public void sendPosition(Vector3 pos, double yaw, double pitch, int mode, Player[] targets) {
        MovePlayerPacket pk = new MovePlayerPacket();
        pk.entityRuntimeId = this.getId();
        pk.x = (float) pos.x;
        pk.y = (float) (pos.y + this.getEyeHeight());
        pk.z = (float) pos.z;
        pk.headYaw = (float) yaw;
        pk.pitch = (float) pitch;
        pk.yaw = (float) yaw;
        pk.mode = mode;

        if (targets != null) {
            Server.broadcastPacket(targets, pk);
        } else {
            pk.entityRuntimeId = this.id;
            this.dataPacket(pk);
        }
    }

    @Override
    protected void checkChunks() {
        if (this.chunk == null || (this.chunk.getX() != ((int) this.x >> 4) || this.chunk.getZ() != ((int) this.z >> 4))) {
            if (this.chunk != null) {
                this.chunk.removeEntity(this);
            }
            this.chunk = this.level.getChunk((int) this.x >> 4, (int) this.z >> 4, true);

            if (!this.justCreated) {
                Map<Integer, Player> newChunk = new HashMap<>(this.level.getChunkPlayers((int) this.x >> 4, (int) this.z >> 4));
                newChunk.remove((int) this.getLoaderId());

                //List<Player> reload = new ArrayList<>();
                for (Player player : new ArrayList<>(this.hasSpawned.values())) {
                    if (!newChunk.containsKey((int) player.getLoaderId())) {
                        this.despawnFrom(player);
                    } else {
                        newChunk.remove((int) player.getLoaderId());
                        //reload.add(player);
                    }
                }

                for (Player player : newChunk.values()) {
                    this.spawnTo(player);
                }
            }

            if (this.chunk == null) {
                return;
            }

            this.chunk.addEntity(this);
        }
    }

    protected boolean checkTeleportPosition() {
        if (this.teleportPosition != null) {
            int chunkX = (int) this.teleportPosition.x >> 4;
            int chunkZ = (int) this.teleportPosition.z >> 4;

            for (int X = -1; X <= 1; ++X) {
                for (int Z = -1; Z <= 1; ++Z) {
                    long index = Level.chunkHash(chunkX + X, chunkZ + Z);
                    if (!this.usedChunks.containsKey(index) || !this.usedChunks.get(index)) {
                        return false;
                    }
                }
            }

            this.spawnToAll();
            this.forceMovement = this.teleportPosition;
            this.teleportPosition = null;

            return true;
        }

        return false;
    }

    public Inventory getWindowById(int id) {
        return this.windowIndex.get(id);
    }

    protected void sendPlayStatus(int status) {

        sendPlayStatus(status, false);

    }

    protected void sendPlayStatus(int status, boolean immediate) {
        PlayStatusPacket pk = new PlayStatusPacket();
        pk.status = status;

        if (immediate) {
            this.directDataPacket(pk);
        } else {
            this.dataPacket(pk);
        }
    }

    @Override
    public boolean teleport(Location location, TeleportCause cause) {
        if (!this.isOnline()) {
            return false;
        }

        Location from = this.getLocation();
        Location to = location;

        if (cause != null) {
            PlayerTeleportEvent event = new PlayerTeleportEvent(this, from, to, cause);
            this.server.getPluginManager().callEvent(event);
            if (event.isCancelled()) return false;
            to = event.getTo();
            if (from.getLevel().getId() != to.getLevel().getId()) {
                SetSpawnPositionPacket pk = new SetSpawnPositionPacket();
                pk.spawnType = SetSpawnPositionPacket.TYPE_WORLD_SPAWN;
                Position spawn = to.getLevel().getSpawnLocation();
                pk.x = spawn.getFloorX();
                pk.y = spawn.getFloorY();
                pk.z = spawn.getFloorZ();
                dataPacket(pk);
            }
        }

        this.getPosition();
        if (super.teleport(to, null)) { // null to prevent fire of duplicate EntityTeleportEvent

            this.removeAllWindows();

            this.teleportPosition = new Vector3(this.x, this.y, this.z);
            this.forceMovement = this.teleportPosition;
            this.sendPosition(this, this.yaw, this.pitch, MovePlayerPacket.MODE_TELEPORT);

            this.checkTeleportPosition();
            this.resetFallDistance();
            this.nextChunkOrderRun = 0;
            this.newPosition = null;

            //Weather
            this.getLevel().sendWeather(this);
            //Update time
            this.getLevel().sendTime(this);

            return true;
        }

        return false;
    }

    public void teleportImmediate(Location location) {
        this.teleportImmediate(location, TeleportCause.PLUGIN);
    }

    public void teleportImmediate(Location location, TeleportCause cause) {
        Location from = this.getLocation();
        if (super.teleport(location, cause)) {

            for (Inventory window : new ArrayList<>(this.windowIndex.values())) {
                if (window == this.inventory) {
                    continue;
                }
                this.removeWindow(window);
            }

            if (from.getLevel().getId() != location.getLevel().getId()) {
                SetSpawnPositionPacket pk = new SetSpawnPositionPacket();
                pk.spawnType = SetSpawnPositionPacket.TYPE_WORLD_SPAWN;
                Position spawn = location.getLevel().getSpawnLocation();
                pk.x = spawn.getFloorX();
                pk.y = spawn.getFloorY();
                pk.z = spawn.getFloorZ();
                dataPacket(pk);
            }

            this.forceMovement = new Vector3(this.x, this.y, this.z);
            this.sendPosition(this, this.yaw, this.pitch, MovePlayerPacket.MODE_RESET);

            this.resetFallDistance();
            this.orderChunks();
            this.nextChunkOrderRun = 0;
            this.newPosition = null;

            //Weather
            this.getLevel().sendWeather(this);
            //Update time
            this.getLevel().sendTime(this);
        }
    }

    /**
     * Creates and sends a BossBar to the player
     *
     * @param text  The BossBar message
     * @param length  The BossBar percentage
     * @return bossBarId  The BossBar ID, you should store it if you want to remove or update the BossBar later
     */
    /*
    public long createBossBar(String text, int length) {
        // First we spawn a entity
        long bossBarId = 1095216660480L + ThreadLocalRandom.current().nextLong(0, 0x7fffffffL);
        AddEntityPacket pkAdd = new AddEntityPacket();
        pkAdd.type = EntityCreeper.NETWORK_ID;
        pkAdd.entityUniqueId = bossBarId;
        pkAdd.entityRuntimeId = bossBarId;
        pkAdd.x = (float) this.x;
        pkAdd.y = (float) -10; // Below the bedrock
        pkAdd.z = (float) this.z;
        pkAdd.speedX = (float) this.motionX;
        pkAdd.speedY = (float) this.motionY;
        pkAdd.speedZ = (float) this.motionZ;
        EntityMetadata metadata = new EntityMetadata()
                // Default Metadata tags
                .putLong(DATA_FLAGS, 0)
                .putShort(DATA_AIR, 400)
                .putShort(DATA_MAX_AIR, 400)
                .putLong(DATA_LEAD_HOLDER_EID, -1)
                .putFloat(DATA_SCALE, 1f)
                .putString(Entity.DATA_NAMETAG, text) // Set the entity name
                .putInt(Entity.DATA_SCALE, 0); // And make it invisible
        pkAdd.metadata = metadata;
        this.dataPacket(pkAdd);

        // Now we send the entity attributes
        // TODO: Attributes should be sent on AddEntityPacket, however it doesn't work (client bug?)
        UpdateAttributesPacket pkAttributes = new UpdateAttributesPacket();
        pkAttributes.entityRuntimeId = bossBarId;
        Attribute attr = Attribute.getAttribute(Attribute.MAX_HEALTH);
        attr.setMaxValue(100); // Max value - We need to change the max value first, or else the "setValue" will return a IllegalArgumentException
        attr.setValue(length); // Entity health
        pkAttributes.entries = new Attribute[] { attr };
        this.dataPacket(pkAttributes);

        // And now we send the bossbar packet
        BossEventPacket pkBoss = new BossEventPacket();
        pkBoss.entityRuntimeId = bossBarId;
        pkBoss.type = BossEventPacket.ADD;
        this.dataPacket(pkBoss);
        return bossBarId;
    }
    */

    /**
     * Updates a BossBar
     *
     * @param text  The new BossBar message
     * @param length  The new BossBar length
     * @param bossBarId  The BossBar ID
     */
    /*
    public void updateBossBar(String text, int length, long bossBarId) {
        // First we update the boss bar length
        UpdateAttributesPacket pkAttributes = new UpdateAttributesPacket();
        pkAttributes.entityRuntimeId = bossBarId;
        Attribute attr = Attribute.getAttribute(Attribute.MAX_HEALTH);
        attr.setMaxValue(100); // Max value - We need to change the max value first, or else the "setValue" will return a IllegalArgumentException
        attr.setValue(length); // Entity health
        pkAttributes.entries = new Attribute[] { attr };
        this.dataPacket(pkAttributes);
        // And then the boss bar text
        SetEntityDataPacket pkMetadata = new SetEntityDataPacket();
        pkMetadata.eid = bossBarId;
        pkMetadata.metadata = new EntityMetadata()
                // Default Metadata tags
                .putLong(DATA_FLAGS, 0)
                .putShort(DATA_AIR, 400)
                .putShort(DATA_MAX_AIR, 400)
                .putLong(DATA_LEAD_HOLDER_EID, -1)
                .putFloat(DATA_SCALE, 1f)
                .putString(Entity.DATA_NAMETAG, text) // Set the entity name
                .putInt(Entity.DATA_SCALE, 0); // And make it invisible
        this.dataPacket(pkMetadata);

        // And now we send the bossbar packet
        BossEventPacket pkBoss = new BossEventPacket();
        pkBoss.entityRuntimeId = bossBarId;
        pkBoss.type = BossEventPacket.UPDATE;
        this.dataPacket(pkBoss);
        return;
    }
    */

    /**
     * Removes a BossBar
     *
     * @param bossBarId  The BossBar ID
     */
    public void removeBossBar(long bossBarId) {
        RemoveEntityPacket pkRemove = new RemoveEntityPacket();
        pkRemove.entityRuntimeId = bossBarId;
        this.dataPacket(pkRemove);
    }

    public int getWindowId(Inventory inventory) {
        if (this.windows.containsKey(inventory)) {
            return this.windows.get(inventory);
        }

        return -1;
    }

    public int addWindow(Inventory inventory){
        return addWindow(inventory, null);
    }

    public int addWindow(Inventory inventory, Integer forceId) {
        return addWindow(inventory, forceId, false);
    }

    public int addWindow(Inventory inventory, Integer forceId, boolean isPermanent) {
        if (this.windows.containsKey(inventory)) {
            return this.windows.get(inventory);
        }
        int cnt;
        if (forceId == null) {
            this.windowCnt = cnt = Math.max(2, ++this.windowCnt % 99);
        } else {
            cnt = forceId;
        }
        this.windowIndex.put(cnt, inventory);
        this.windows.put(inventory, cnt);
        if (isPermanent) {
            this.permanentWindows.add(cnt);
        }
        if (inventory.open(this)) {
            return cnt;
        } else {
            this.removeWindow(inventory);

            return -1;
        }
    }

    public void removeWindow(Inventory inventory) {
        inventory.close(this);
        if (this.windows.containsKey(inventory)) {
            int id = this.windows.get(inventory);
            this.windows.remove(this.windowIndex.get(id));
            this.windowIndex.remove(id);
        }
    }

    @Override
    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
        this.server.getPlayerMetadata().setMetadata(this, metadataKey, newMetadataValue);
    }

    @Override
    public List<MetadataValue> getMetadata(String metadataKey) {
        return this.server.getPlayerMetadata().getMetadata(this, metadataKey);
    }

    @Override
    public boolean hasMetadata(String metadataKey) {
        return this.server.getPlayerMetadata().hasMetadata(this, metadataKey);
    }

    @Override
    public void removeMetadata(String metadataKey, Plugin owningPlugin) {
        this.server.getPlayerMetadata().removeMetadata(this, metadataKey, owningPlugin);
    }

    @Override
    public void onChunkChanged(FullChunk chunk) {
        this.usedChunks.remove(Level.chunkHash(chunk.getX(), chunk.getZ()));
    }

    @Override
    public void onChunkLoaded(FullChunk chunk) {

    }

    @Override
    public void onChunkPopulated(FullChunk chunk) {

    }

    @Override
    public void onChunkUnloaded(FullChunk chunk) {

    }

    @Override
    public void onBlockChanged(Vector3 block) {

    }

    @Override
    public Integer getLoaderId() {
        return this.loaderId;
    }

    @Override
    public boolean isLoaderActive() {
        return this.isConnected();
    }


    public static BatchPacket getChunkCacheFromData(int chunkX, int chunkZ, byte[] payload) {
        FullChunkDataPacket pk = new FullChunkDataPacket();
        pk.chunkX = chunkX;
        pk.chunkZ = chunkZ;
        pk.data = payload;
        pk.encode();

        BatchPacket batch = new BatchPacket();
        byte[][] batchPayload = new byte[2][];
        byte[] buf = pk.getBuffer();
        batchPayload[0] = Binary.writeUnsignedVarInt(buf.length);
        batchPayload[1] = buf;
        byte[] data = Binary.appendBytes(batchPayload);
        try {
            batch.payload = Zlib.deflate(data, Server.getInstance().networkCompressionLevel);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return batch;
    }

    private boolean foodEnabled = true;
    private CraftingGrid craftingGrid;
    private PlayerCursorInventory cursorInventory;

    public boolean isFoodEnabled() {
        return !(this.isCreative() || this.isSpectator()) && this.foodEnabled;
    }

    public void setFood(int food){
        this.foodData.setLevel(food);
    }

    public void setFoodEnabled(boolean foodEnabled) {
        this.foodEnabled = foodEnabled;
    }

    public PlayerFood getFoodData() {
        return this.foodData;
    }

    //todo a lot on dimension

    public void setDimension(int dimension) {
        ChangeDimensionPacket pk = new ChangeDimensionPacket();
        pk.dimension = getLevel().getDimension();
        this.dataPacket(pk);
    }

    public void setCheckMovement(boolean checkMovement) {
        this.checkMovement = checkMovement;
    }

    public synchronized void setLocale(Locale locale) {
        this.locale.set(locale);
    }

    public synchronized Locale getLocale() {
        return this.locale.get();
    }

    public void setSprinting(boolean value, boolean setDefault) {
        super.setSprinting(value);
        if (setDefault) {
            this.movementSpeed = DEFAULT_SPEED;
        } else {
            float sprintSpeedChange = DEFAULT_SPEED * 0.3f;
            if (!value) sprintSpeedChange *= -1;
            this.movementSpeed += sprintSpeedChange;
        }
        this.setMovementSpeed(this.movementSpeed);
    }

    /**
     * プレイヤーをミュート状態にします。
     * @param b trueで有効/falseで無効
     * @return void
     * @author Itsu
     */
    public void setMute(boolean b){
        this.mute = b;
    }

    /**
     * プレイヤーがミュート状態かどうかを返します。
     * @return boolean trueが有効/falseが無効
     * @author Itsu
     */
    public boolean isMuted(){
        return this.mute;
    }

    /**
     * プレイヤー指定したサーバーに転送します。
     * @param ip 転送先サーバーのIPアドレス
     * @param port 転送先サーバーのポート
     * @return void
     * @author Itsu
     */
    public void transfer(String ip, int port){
        TransferPacket pk = new TransferPacket();
        pk.address = ip;
        pk.port = port;
        this.dataPacket(pk);
        String mes = FastAppender.get("IP: ", ip, "  ポート: ", port, "のサーバーに移動しました。");
        if(this.server.getJupiterConfigBoolean("transfer-server-message")){
            this.server.broadcastMessage(this.getName() + "は別のサーバーへ移動しました。");
        }
        this.close(mes, mes, false);
    }

    /**
     * @deprecated Nukkitとの互換性がないため削除されます。
     */
    @Deprecated
    public void transferServer(String ip, int port){
        this.transfer(ip, port);
    }

    public boolean pickupEntity(Entity entity, boolean near) {
        if (!this.spawned || !this.isAlive() || !this.isOnline()) {
            return false;
        }

        if (near) {
            if (entity instanceof EntityArrow && ((EntityArrow) entity).hadCollision) {
                ItemArrow item = new ItemArrow();
                if (this.isSurvival() && !this.inventory.canAddItem(item)) {
                    return false;
                }

                InventoryPickupArrowEvent ev;
                this.server.getPluginManager().callEvent(ev = new InventoryPickupArrowEvent(this.inventory, (EntityArrow) entity));
                if (ev.isCancelled()) {
                    return false;
                }

                TakeItemEntityPacket pk = new TakeItemEntityPacket();
                pk.entityRuntimeId = this.getId();
                pk.target = entity.getId();
                Server.broadcastPacket(entity.getViewers().values(), pk);

                pk = new TakeItemEntityPacket();
                pk.entityRuntimeId = this.id;
                pk.target = entity.getId();
                this.dataPacket(pk);

                this.inventory.addItem(item.clone());
                entity.kill();
                return true;
            } else if (entity instanceof EntityItem) {
                if (((EntityItem) entity).getPickupDelay() <= 0) {
                    Item item = ((EntityItem) entity).getItem();

                    if (item != null) {
                        if (this.isSurvival() && !this.inventory.canAddItem(item)) {
                            return false;
                        }

                        InventoryPickupItemEvent ev;
                        this.server.getPluginManager().callEvent(ev = new InventoryPickupItemEvent(this.inventory, (EntityItem) entity));
                        if (ev.isCancelled()) {
                            return false;
                        }

                        TakeItemEntityPacket pk = new TakeItemEntityPacket();
                        pk.entityRuntimeId = this.getId();
                        pk.target = entity.getId();
                        Server.broadcastPacket(entity.getViewers().values(), pk);

                        pk = new TakeItemEntityPacket();
                        pk.entityRuntimeId = this.id;
                        pk.target = entity.getId();
                        this.dataPacket(pk);

                        this.inventory.addItem(item.clone());
                        entity.kill();
                        return true;
                    }
                }
            }
        }

        int tick = this.getServer().getTick();

        if (pickedXPOrb < tick && entity instanceof EntityXPOrb && this.boundingBox.isVectorInside(entity)) {
            EntityXPOrb xpOrb = (EntityXPOrb) entity;
            if (xpOrb.getPickupDelay() <= 0) {
                int exp = xpOrb.getExp();
                this.addExperience(exp);
                entity.kill();
                this.getLevel().addSound(new ExperienceOrbSound(this));
                pickedXPOrb = tick;
                return true;
            }
        }

        return false;
    }

    @Override
    public int hashCode() {
        if ((this.hash == 0) || (this.hash == 485)) {
            this.hash = (485 + (getUniqueId() != null ? getUniqueId().hashCode() : 0));
        }

        return this.hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Player)) {
            return false;
        }
        Player other = (Player) obj;
        return Objects.equals(this.getUniqueId(), other.getUniqueId()) && this.getId() == other.getId();
    }

    /**
     * Notifies an ACK response from the client
     *
     * @param identification
     */
    public void notifyACK(int identification) {
        needACK.put(identification, true);
    }

    public void excuteData() throws IOException{

        JsonObject main = new JsonObject();

        main.addProperty("Name: ", this.getName());
        main.addProperty("X: ", this.getX());
        main.addProperty("Y: ", this.getY());
        main.addProperty("Z: ", this.getZ());

        List<Item> item = new ArrayList<>();
        for(int i=0;i < this.getInventory().getContents().size();i++){
            item.add(this.getInventory().getContents().get(i));
        }

        int count = 1;
        for(Item i : item){
            main.addProperty("Inventory[" + count + "]: ", i.getName() + "[" + i.getCount() + "]");
            count++;
        }

        String src = new Gson().toJson(main);

        Utils.writeFile("./players/JsonDatas/" + this.getName() + ".json", src);

        return;

    }

    protected void forceSendEmptyChunks() {
        int chunkPositionX = this.getFloorX() >> 4;
        int chunkPositionZ = this.getFloorZ() >> 4;
        for (int x = -3; x < 3; x++) {
            for (int z = -3; z < 3; z++) {
                FullChunkDataPacket chunk = new FullChunkDataPacket();
                chunk.chunkX = chunkPositionX + x;
                chunk.chunkZ = chunkPositionZ + z;
                chunk.data = new byte[0];
                this.dataPacket(chunk);
            }
        }
    }

    public boolean dropItem(Item item) {
        if (!this.spawned || !this.isAlive()) {
            return false;
        }

        if (item.isNull()) {
            this.server.getLogger().debug(this.getName() + " attempted to drop a null item (" + item + ")");
            return true;
        }

        Vector3 motion = this.getDirectionVector().multiply(0.4);

        this.level.dropItem(this.add(0, 1.3, 0), item, motion, 40);

        this.setDataFlag(DATA_FLAGS, DATA_FLAG_ACTION, false);
        return true;
    }


    public void sendAllInventories() {
        for (Inventory inv : this.windowIndex.values()) {
            inv.sendContents(this);

            if (inv instanceof PlayerInventory) {
                ((PlayerInventory) inv).sendArmorContents(this);
            }
        }
    }

    protected void addDefaultWindows() {
        this.addWindow(this.getInventory(), ContainerIds.INVENTORY, true);

        this.cursorInventory = new PlayerCursorInventory(this);
        this.addWindow(this.cursorInventory, ContainerIds.CURSOR, true);

        this.addWindow(this.offhandInventory, ContainerIds.OFFHAND, true);

        this.craftingGrid = new CraftingGrid(this);

        //TODO: more windows
    }

    public PlayerCursorInventory getCursorInventory() {
        return this.cursorInventory;
    }

    public CraftingGrid getCraftingGrid() {
        return this.craftingGrid;
    }

    public void setCraftingGrid(CraftingGrid grid) {
        this.craftingGrid = grid;
    }

    public void resetCraftingGridType() {
        if (this.craftingGrid instanceof BigCraftingGrid) {
            Item[] drops = this.inventory.addItem(this.craftingGrid.getContents().values().stream().toArray(Item[]::new));
            for (Item drop : drops) {
                this.dropItem(drop);
            }

            this.craftingGrid = new CraftingGrid(this);
            this.craftingType = 0;
        }
    }

    public void removeAllWindows() {
        removeAllWindows(false);
    }

    public void removeAllWindows(boolean permanent) {
        for (Entry<Integer, Inventory> entry : new ArrayList<>(this.windowIndex.entrySet())) {
            if (!permanent && this.permanentWindows.contains((int) entry.getKey())) {
                continue;
            }

            this.removeWindow(entry.getValue());
        }
    }

    public boolean isUsingItem(){
        return this.getDataFlag(DATA_FLAGS, DATA_FLAG_ACTION) && this.startAction > 1;
    }

    public void setUsingItem(boolean value) {
        this.startAction = value ? this.server.getTick() : -1;
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_ACTION, value);
    }

    /**
     * フォームウィンドウをプレイヤーに送信します。
     * @param window FormWindow
     * @return void
     */
    public void sendWindow(FormWindow window){
        ModalFormRequestPacket pk = new ModalFormRequestPacket();
        pk.data = window.toJson();
        pk.formId = window.getId();

        this.activeWindow = window;

        this.dataPacket(pk);
    }

    public int addServerSettings(ServerSettingsWindow window) {
        this.serverSettings.put(window.getId(), window);
        return window.getId();
    }

    public boolean isBreakingBlock() {
        return this.breakingBlock != null;
    }

    public void showXboxProfile() {
        ShowProfilePacket pk = new ShowProfilePacket();
        pk.xuid = getLoginChainData().getXUID();
        this.dataPacket(pk);
    }

    /**
     * プレイヤーのスポーン地点からの距離を取得します。
     * @return double
     */
    public double getPlaneDistanceFromSpawn(){
        return this.distance(this.level.getSafeSpawn());
    }
}

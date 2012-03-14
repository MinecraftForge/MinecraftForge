package net.minecraft.src;

public abstract class NetHandler
{
    /**
     * determine if it is a server handler
     */
    public abstract boolean isServerHandler();

    public void func_48070_a(Packet51MapChunk par1Packet51MapChunk) {}

    public void registerPacket(Packet par1Packet) {}

    public void handleErrorMessage(String par1Str, Object[] par2ArrayOfObj) {}

    public void handleKickDisconnect(Packet255KickDisconnect par1Packet255KickDisconnect)
    {
        this.registerPacket(par1Packet255KickDisconnect);
    }

    public void handleLogin(Packet1Login par1Packet1Login)
    {
        this.registerPacket(par1Packet1Login);
    }

    public void handleFlying(Packet10Flying par1Packet10Flying)
    {
        this.registerPacket(par1Packet10Flying);
    }

    public void handleMultiBlockChange(Packet52MultiBlockChange par1Packet52MultiBlockChange)
    {
        this.registerPacket(par1Packet52MultiBlockChange);
    }

    public void handleBlockDig(Packet14BlockDig par1Packet14BlockDig)
    {
        this.registerPacket(par1Packet14BlockDig);
    }

    public void handleBlockChange(Packet53BlockChange par1Packet53BlockChange)
    {
        this.registerPacket(par1Packet53BlockChange);
    }

    public void handlePreChunk(Packet50PreChunk par1Packet50PreChunk)
    {
        this.registerPacket(par1Packet50PreChunk);
    }

    public void handleNamedEntitySpawn(Packet20NamedEntitySpawn par1Packet20NamedEntitySpawn)
    {
        this.registerPacket(par1Packet20NamedEntitySpawn);
    }

    public void handleEntity(Packet30Entity par1Packet30Entity)
    {
        this.registerPacket(par1Packet30Entity);
    }

    public void handleEntityTeleport(Packet34EntityTeleport par1Packet34EntityTeleport)
    {
        this.registerPacket(par1Packet34EntityTeleport);
    }

    public void handlePlace(Packet15Place par1Packet15Place)
    {
        this.registerPacket(par1Packet15Place);
    }

    public void handleBlockItemSwitch(Packet16BlockItemSwitch par1Packet16BlockItemSwitch)
    {
        this.registerPacket(par1Packet16BlockItemSwitch);
    }

    public void handleDestroyEntity(Packet29DestroyEntity par1Packet29DestroyEntity)
    {
        this.registerPacket(par1Packet29DestroyEntity);
    }

    public void handlePickupSpawn(Packet21PickupSpawn par1Packet21PickupSpawn)
    {
        this.registerPacket(par1Packet21PickupSpawn);
    }

    public void handleCollect(Packet22Collect par1Packet22Collect)
    {
        this.registerPacket(par1Packet22Collect);
    }

    public void handleChat(Packet3Chat par1Packet3Chat)
    {
        this.registerPacket(par1Packet3Chat);
    }

    public void handleVehicleSpawn(Packet23VehicleSpawn par1Packet23VehicleSpawn)
    {
        this.registerPacket(par1Packet23VehicleSpawn);
    }

    public void handleAnimation(Packet18Animation par1Packet18Animation)
    {
        this.registerPacket(par1Packet18Animation);
    }

    public void handleEntityAction(Packet19EntityAction par1Packet19EntityAction)
    {
        this.registerPacket(par1Packet19EntityAction);
    }

    public void handleHandshake(Packet2Handshake par1Packet2Handshake)
    {
        this.registerPacket(par1Packet2Handshake);
    }

    public void handleMobSpawn(Packet24MobSpawn par1Packet24MobSpawn)
    {
        this.registerPacket(par1Packet24MobSpawn);
    }

    public void handleUpdateTime(Packet4UpdateTime par1Packet4UpdateTime)
    {
        this.registerPacket(par1Packet4UpdateTime);
    }

    public void handleSpawnPosition(Packet6SpawnPosition par1Packet6SpawnPosition)
    {
        this.registerPacket(par1Packet6SpawnPosition);
    }

    public void handleEntityVelocity(Packet28EntityVelocity par1Packet28EntityVelocity)
    {
        this.registerPacket(par1Packet28EntityVelocity);
    }

    public void handleEntityMetadata(Packet40EntityMetadata par1Packet40EntityMetadata)
    {
        this.registerPacket(par1Packet40EntityMetadata);
    }

    public void handleAttachEntity(Packet39AttachEntity par1Packet39AttachEntity)
    {
        this.registerPacket(par1Packet39AttachEntity);
    }

    public void handleUseEntity(Packet7UseEntity par1Packet7UseEntity)
    {
        this.registerPacket(par1Packet7UseEntity);
    }

    public void handleEntityStatus(Packet38EntityStatus par1Packet38EntityStatus)
    {
        this.registerPacket(par1Packet38EntityStatus);
    }

    public void handleUpdateHealth(Packet8UpdateHealth par1Packet8UpdateHealth)
    {
        this.registerPacket(par1Packet8UpdateHealth);
    }

    /**
     * respawns the player
     */
    public void handleRespawn(Packet9Respawn par1Packet9Respawn)
    {
        this.registerPacket(par1Packet9Respawn);
    }

    public void handleExplosion(Packet60Explosion par1Packet60Explosion)
    {
        this.registerPacket(par1Packet60Explosion);
    }

    public void handleOpenWindow(Packet100OpenWindow par1Packet100OpenWindow)
    {
        this.registerPacket(par1Packet100OpenWindow);
    }

    public void handleCloseWindow(Packet101CloseWindow par1Packet101CloseWindow)
    {
        this.registerPacket(par1Packet101CloseWindow);
    }

    public void handleWindowClick(Packet102WindowClick par1Packet102WindowClick)
    {
        this.registerPacket(par1Packet102WindowClick);
    }

    public void handleSetSlot(Packet103SetSlot par1Packet103SetSlot)
    {
        this.registerPacket(par1Packet103SetSlot);
    }

    public void handleWindowItems(Packet104WindowItems par1Packet104WindowItems)
    {
        this.registerPacket(par1Packet104WindowItems);
    }

    public void handleUpdateSign(Packet130UpdateSign par1Packet130UpdateSign)
    {
        this.registerPacket(par1Packet130UpdateSign);
    }

    public void handleUpdateProgressbar(Packet105UpdateProgressbar par1Packet105UpdateProgressbar)
    {
        this.registerPacket(par1Packet105UpdateProgressbar);
    }

    public void handlePlayerInventory(Packet5PlayerInventory par1Packet5PlayerInventory)
    {
        this.registerPacket(par1Packet5PlayerInventory);
    }

    public void handleTransaction(Packet106Transaction par1Packet106Transaction)
    {
        this.registerPacket(par1Packet106Transaction);
    }

    public void handleEntityPainting(Packet25EntityPainting par1Packet25EntityPainting)
    {
        this.registerPacket(par1Packet25EntityPainting);
    }

    public void handlePlayNoteBlock(Packet54PlayNoteBlock par1Packet54PlayNoteBlock)
    {
        this.registerPacket(par1Packet54PlayNoteBlock);
    }

    public void handleStatistic(Packet200Statistic par1Packet200Statistic)
    {
        this.registerPacket(par1Packet200Statistic);
    }

    public void handleSleep(Packet17Sleep par1Packet17Sleep)
    {
        this.registerPacket(par1Packet17Sleep);
    }

    public void handleBed(Packet70Bed par1Packet70Bed)
    {
        this.registerPacket(par1Packet70Bed);
    }

    public void handleWeather(Packet71Weather par1Packet71Weather)
    {
        this.registerPacket(par1Packet71Weather);
    }

    /**
     * Contains logic for handling packets containing arbitrary unique item data. Currently this is only for maps.
     */
    public void handleMapData(Packet131MapData par1Packet131MapData)
    {
        this.registerPacket(par1Packet131MapData);
    }

    public void handleDoorChange(Packet61DoorChange par1Packet61DoorChange)
    {
        this.registerPacket(par1Packet61DoorChange);
    }

    /**
     * Handle a server ping packet.
     */
    public void handleServerPing(Packet254ServerPing par1Packet254ServerPing)
    {
        this.registerPacket(par1Packet254ServerPing);
    }

    /**
     * Handle an entity effect packet.
     */
    public void handleEntityEffect(Packet41EntityEffect par1Packet41EntityEffect)
    {
        this.registerPacket(par1Packet41EntityEffect);
    }

    /**
     * Handle a remove entity effect packet.
     */
    public void handleRemoveEntityEffect(Packet42RemoveEntityEffect par1Packet42RemoveEntityEffect)
    {
        this.registerPacket(par1Packet42RemoveEntityEffect);
    }

    /**
     * Handle a player information packet.
     */
    public void handlePlayerInfo(Packet201PlayerInfo par1Packet201PlayerInfo)
    {
        this.registerPacket(par1Packet201PlayerInfo);
    }

    /**
     * Handle a keep alive packet.
     */
    public void handleKeepAlive(Packet0KeepAlive par1Packet0KeepAlive)
    {
        this.registerPacket(par1Packet0KeepAlive);
    }

    /**
     * Handle an experience packet.
     */
    public void handleExperience(Packet43Experience par1Packet43Experience)
    {
        this.registerPacket(par1Packet43Experience);
    }

    /**
     * Handle a creative slot packet.
     */
    public void handleCreativeSetSlot(Packet107CreativeSetSlot par1Packet107CreativeSetSlot)
    {
        this.registerPacket(par1Packet107CreativeSetSlot);
    }

    /**
     * Handle a entity experience orb packet.
     */
    public void handleEntityExpOrb(Packet26EntityExpOrb par1Packet26EntityExpOrb)
    {
        this.registerPacket(par1Packet26EntityExpOrb);
    }

    public void handleEnchantItem(Packet108EnchantItem par1Packet108EnchantItem) {}

    public void handleCustomPayload(Packet250CustomPayload par1Packet250CustomPayload) {}

    public void func_48072_a(Packet35EntityHeadRotation par1Packet35EntityHeadRotation)
    {
        this.registerPacket(par1Packet35EntityHeadRotation);
    }

    public void func_48071_a(Packet132TileEntityData par1Packet132TileEntityData)
    {
        this.registerPacket(par1Packet132TileEntityData);
    }
}

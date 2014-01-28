package net.minecraft.network.play;

import net.minecraft.network.INetHandler;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.network.play.server.S05PacketSpawnPosition;
import net.minecraft.network.play.server.S06PacketUpdateHealth;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S09PacketHeldItemChange;
import net.minecraft.network.play.server.S0APacketUseBed;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S0DPacketCollectItem;
import net.minecraft.network.play.server.S0EPacketSpawnObject;
import net.minecraft.network.play.server.S0FPacketSpawnMob;
import net.minecraft.network.play.server.S10PacketSpawnPainting;
import net.minecraft.network.play.server.S11PacketSpawnExperienceOrb;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S19PacketEntityHeadLook;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.network.play.server.S1CPacketEntityMetadata;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1EPacketRemoveEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.network.play.server.S20PacketEntityProperties;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.network.play.server.S24PacketBlockAction;
import net.minecraft.network.play.server.S25PacketBlockBreakAnim;
import net.minecraft.network.play.server.S26PacketMapChunkBulk;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S28PacketEffect;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraft.network.play.server.S31PacketWindowProperty;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.network.play.server.S33PacketUpdateSign;
import net.minecraft.network.play.server.S34PacketMaps;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.network.play.server.S36PacketSignEditorOpen;
import net.minecraft.network.play.server.S37PacketStatistics;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.network.play.server.S3APacketTabComplete;
import net.minecraft.network.play.server.S3BPacketScoreboardObjective;
import net.minecraft.network.play.server.S3CPacketUpdateScore;
import net.minecraft.network.play.server.S3DPacketDisplayScoreboard;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.network.play.server.S40PacketDisconnect;

public interface INetHandlerPlayClient extends INetHandler
{
    void func_147235_a(S0EPacketSpawnObject var1);

    void func_147286_a(S11PacketSpawnExperienceOrb var1);

    void func_147292_a(S2CPacketSpawnGlobalEntity var1);

    void func_147281_a(S0FPacketSpawnMob var1);

    void func_147291_a(S3BPacketScoreboardObjective var1);

    void func_147288_a(S10PacketSpawnPainting var1);

    void func_147237_a(S0CPacketSpawnPlayer var1);

    void func_147279_a(S0BPacketAnimation var1);

    void func_147293_a(S37PacketStatistics var1);

    void func_147294_a(S25PacketBlockBreakAnim var1);

    void func_147268_a(S36PacketSignEditorOpen var1);

    void func_147273_a(S35PacketUpdateTileEntity var1);

    void func_147261_a(S24PacketBlockAction var1);

    void func_147234_a(S23PacketBlockChange var1);

    void func_147251_a(S02PacketChat var1);

    void func_147274_a(S3APacketTabComplete var1);

    void func_147287_a(S22PacketMultiBlockChange var1);

    void func_147264_a(S34PacketMaps var1);

    void func_147239_a(S32PacketConfirmTransaction var1);

    void func_147276_a(S2EPacketCloseWindow var1);

    void func_147241_a(S30PacketWindowItems var1);

    void func_147265_a(S2DPacketOpenWindow var1);

    void func_147245_a(S31PacketWindowProperty var1);

    void func_147266_a(S2FPacketSetSlot var1);

    void func_147240_a(S3FPacketCustomPayload var1);

    void func_147253_a(S40PacketDisconnect var1);

    void func_147278_a(S0APacketUseBed var1);

    void func_147236_a(S19PacketEntityStatus var1);

    void func_147243_a(S1BPacketEntityAttach var1);

    void func_147283_a(S27PacketExplosion var1);

    void func_147252_a(S2BPacketChangeGameState var1);

    void func_147272_a(S00PacketKeepAlive var1);

    void func_147263_a(S21PacketChunkData var1);

    void func_147269_a(S26PacketMapChunkBulk var1);

    void func_147277_a(S28PacketEffect var1);

    void func_147282_a(S01PacketJoinGame var1);

    void func_147259_a(S14PacketEntity var1);

    void func_147258_a(S08PacketPlayerPosLook var1);

    void func_147289_a(S2APacketParticles var1);

    void func_147270_a(S39PacketPlayerAbilities var1);

    void func_147256_a(S38PacketPlayerListItem var1);

    void func_147238_a(S13PacketDestroyEntities var1);

    void func_147262_a(S1EPacketRemoveEntityEffect var1);

    void func_147280_a(S07PacketRespawn var1);

    void func_147267_a(S19PacketEntityHeadLook var1);

    void func_147257_a(S09PacketHeldItemChange var1);

    void func_147254_a(S3DPacketDisplayScoreboard var1);

    void func_147284_a(S1CPacketEntityMetadata var1);

    void func_147244_a(S12PacketEntityVelocity var1);

    void func_147242_a(S04PacketEntityEquipment var1);

    void func_147295_a(S1FPacketSetExperience var1);

    void func_147249_a(S06PacketUpdateHealth var1);

    void func_147247_a(S3EPacketTeams var1);

    void func_147250_a(S3CPacketUpdateScore var1);

    void func_147271_a(S05PacketSpawnPosition var1);

    void func_147285_a(S03PacketTimeUpdate var1);

    void func_147248_a(S33PacketUpdateSign var1);

    void func_147255_a(S29PacketSoundEffect var1);

    void func_147246_a(S0DPacketCollectItem var1);

    void func_147275_a(S18PacketEntityTeleport var1);

    void func_147290_a(S20PacketEntityProperties var1);

    void func_147260_a(S1DPacketEntityEffect var1);
}
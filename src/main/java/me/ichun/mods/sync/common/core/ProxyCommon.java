package me.ichun.mods.sync.common.core;

import me.ichun.mods.ichunutil.common.core.network.PacketChannel;
import me.ichun.mods.ichunutil.common.item.ItemGeneric;
import me.ichun.mods.sync.common.Sync;
import me.ichun.mods.sync.common.block.BlockDualVertical;
import me.ichun.mods.sync.common.block.EnumType;
import me.ichun.mods.sync.common.creativetab.CreativeTabSync;
import me.ichun.mods.sync.common.item.ItemShellBase;
import me.ichun.mods.sync.common.item.ItemTreadmill;
import me.ichun.mods.sync.common.packet.PacketClearShellList;
import me.ichun.mods.sync.common.packet.PacketNBT;
import me.ichun.mods.sync.common.packet.PacketPlayerDeath;
import me.ichun.mods.sync.common.packet.PacketPlayerEnterStorage;
import me.ichun.mods.sync.common.packet.PacketShellDeath;
import me.ichun.mods.sync.common.packet.PacketShellState;
import me.ichun.mods.sync.common.packet.PacketSyncRequest;
import me.ichun.mods.sync.common.packet.PacketUpdatePlayerOnZoomFinish;
import me.ichun.mods.sync.common.packet.PacketZoomCamera;
import me.ichun.mods.sync.common.tileentity.TileEntityShellConstructor;
import me.ichun.mods.sync.common.tileentity.TileEntityShellStorage;
import me.ichun.mods.sync.common.tileentity.TileEntityTreadmill;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ProxyCommon
{
	public void preInitMod()
	{
		Sync.creativeTabSync = new CreativeTabSync();

		Sync.blockDualVertical = (new BlockDualVertical()).setRegistryName("sync", "block_multi").setLightLevel(0.5F).setHardness(2.0F).setUnlocalizedName("sync.block.multi");

		Sync.itemShellConstructor = new ItemShellBase(EnumType.CONSTRUCTOR).setRegistryName("sync", "item_shell_constructor").setUnlocalizedName("Sync_ShellConstructor").setCreativeTab(Sync.creativeTabSync);
		Sync.itemShellStorage = new ItemShellBase(EnumType.STORAGE).setRegistryName("sync", "item_shell_storage").setUnlocalizedName("Sync_ShellStorage").setCreativeTab(Sync.creativeTabSync);
		Sync.itemTreadmill = new ItemTreadmill().setRegistryName("sync", "item_treadmill").setUnlocalizedName("Sync_Treadmill").setCreativeTab(Sync.creativeTabSync);
		Sync.itemSyncCore = (new ItemGeneric()).setRegistryName("sync", "item_placeholder").setUnlocalizedName("Sync_SyncCore").setCreativeTab(Sync.creativeTabSync);

		GameRegistry.registerTileEntity(TileEntityShellConstructor.class, "Sync_TEShellConstructor");
		GameRegistry.registerTileEntity(TileEntityShellStorage.class, "Sync_TEShellStorage");
		GameRegistry.registerTileEntity(TileEntityTreadmill.class, "Sync_TETreadmill");

		Sync.eventHandlerServer = new EventHandlerServer();
		MinecraftForge.EVENT_BUS.register(Sync.eventHandlerServer);

		Sync.channel = new PacketChannel("Sync", PacketSyncRequest.class, PacketZoomCamera.class, PacketPlayerDeath.class, PacketUpdatePlayerOnZoomFinish.class, PacketPlayerEnterStorage.class, PacketShellDeath.class, PacketClearShellList.class, PacketShellState.class, PacketNBT.class);
	}

}

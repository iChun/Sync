package me.ichun.mods.sync.common.core;

import me.ichun.mods.ichunutil.common.core.network.PacketChannel;
import me.ichun.mods.ichunutil.common.item.ItemGeneric;
import me.ichun.mods.sync.common.Sync;
import me.ichun.mods.sync.common.block.BlockDualVertical;
import me.ichun.mods.sync.common.creativetab.CreativeTabSync;
import me.ichun.mods.sync.common.item.ItemSyncBlockPlacer;
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

		Sync.blockDualVertical = GameRegistry.register((new BlockDualVertical()).setRegistryName("sync", "block_multi").setLightLevel(0.5F).setHardness(2.0F).setUnlocalizedName("sync.block.multi"));

		Sync.itemBlockPlacer = GameRegistry.register((new ItemSyncBlockPlacer()).setRegistryName("sync", "item_block_placer").setFull3D().setUnlocalizedName("Sync_BlockPlacer").setCreativeTab(Sync.creativeTabSync));
		Sync.itemPlaceholder = GameRegistry.register((new ItemGeneric()).setRegistryName("sync", "item_placeholder").setUnlocalizedName("Sync_SyncCore").setCreativeTab(Sync.creativeTabSync));

		GameRegistry.addRecipe(new ItemStack(Sync.itemBlockPlacer, 1, 0),
				"OCO", "GGG", "ORO", 'O', Blocks.OBSIDIAN, 'C', Sync.itemPlaceholder, 'G', Blocks.GLASS_PANE, 'R', Items.REDSTONE);

		GameRegistry.addRecipe(new ItemStack(Sync.itemBlockPlacer, 1, 1),
				"OCO", "GIG", "OPO", 'O', Blocks.OBSIDIAN, 'C', Sync.itemPlaceholder, 'G', Blocks.GLASS_PANE, 'R', Items.REDSTONE, 'I', Blocks.IRON_BLOCK, 'P', Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE);

		GameRegistry.addRecipe(new ItemStack(Sync.itemBlockPlacer, 1, 2),
				"  D", "CCI", "OOR", 'O', Blocks.OBSIDIAN, 'C', new ItemStack(Blocks.CARPET, 1, 15), 'I', Blocks.IRON_BARS, 'D', Blocks.DAYLIGHT_DETECTOR, 'R', Items.REDSTONE);

		GameRegistry.registerTileEntity(TileEntityShellConstructor.class, "Sync_TEShellConstructor");
		GameRegistry.registerTileEntity(TileEntityShellStorage.class, "Sync_TEShellStorage");
		GameRegistry.registerTileEntity(TileEntityTreadmill.class, "Sync_TETreadmill");

		Sync.eventHandlerServer = new EventHandlerServer();
		MinecraftForge.EVENT_BUS.register(Sync.eventHandlerServer);

		Sync.channel = new PacketChannel("Sync", PacketSyncRequest.class, PacketZoomCamera.class, PacketPlayerDeath.class, PacketUpdatePlayerOnZoomFinish.class, PacketPlayerEnterStorage.class, PacketShellDeath.class, PacketClearShellList.class, PacketShellState.class, PacketNBT.class);
	}

}

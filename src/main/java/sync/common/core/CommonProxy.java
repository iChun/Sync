package sync.common.core;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import ichun.common.core.network.ChannelHandler;
import morph.common.Morph;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import sync.client.core.TickHandlerClient;
import sync.common.Sync;
import sync.common.block.BlockDualVertical;
import sync.common.creativetab.CreativeTabSync;
import sync.common.item.ItemPlaceholder;
import sync.common.item.ItemSyncBlockPlacer;
import sync.common.packet.*;
import sync.common.tileentity.TileEntityShellConstructor;
import sync.common.tileentity.TileEntityShellStorage;
import sync.common.tileentity.TileEntityTreadmill;

import java.util.Calendar;

public class CommonProxy 
{

	public TickHandlerClient tickHandlerClient;
	
	public void initMod()
	{
		Sync.creativeTabSync = new CreativeTabSync();
		
		Sync.blockDualVertical = (new BlockDualVertical()).setLightLevel(0.5F).setHardness(2.0F).setBlockName("Sync_ShellConstructor");
		
		Sync.itemBlockPlacer = (new ItemSyncBlockPlacer()).setFull3D().setUnlocalizedName("Sync_BlockPlacer").setCreativeTab(Sync.creativeTabSync);
		Sync.itemPlaceholder = (new ItemPlaceholder()).setUnlocalizedName("Sync_SyncCore").setCreativeTab(Sync.creativeTabSync);
		
//		GameRegistry.addShapelessRecipe(new ItemStack(Sync.itemBlockPlacer, 1, 0),
//				new Object[] { new ItemStack(Block.dirt) });
//		GameRegistry.addShapelessRecipe(new ItemStack(Sync.itemBlockPlacer, 1, 1),
//				new Object[] { new ItemStack(Item.stick) });
		
		GameRegistry.addRecipe(new ItemStack(Sync.itemBlockPlacer, 1, 0),
                "OCO", "GGG", "ORO", 'O', Blocks.obsidian, 'C', Sync.itemPlaceholder, 'G', Blocks.glass_pane, 'R', Items.redstone);

		GameRegistry.addRecipe(new ItemStack(Sync.itemBlockPlacer, 1, 1),
                "OCO", "GIG", "OPO", 'O', Blocks.obsidian, 'C', Sync.itemPlaceholder, 'G', Blocks.glass_pane, 'R', Items.redstone, 'I', Blocks.iron_block, 'P', Blocks.heavy_weighted_pressure_plate);

		GameRegistry.addRecipe(new ItemStack(Sync.itemBlockPlacer, 1, 2),
                "  D", "CCI", "OOR", 'O', Blocks.obsidian, 'C', new ItemStack(Blocks.carpet, 1, 15), 'I', Blocks.iron_bars, 'D', Blocks.daylight_detector, 'R', Items.redstone);
		
		GameRegistry.registerBlock(Sync.blockDualVertical, "Sync_ShellConstructor");
		GameRegistry.registerItem(Sync.itemBlockPlacer, "Sync_BlockPlacer");
		GameRegistry.registerItem(Sync.itemPlaceholder, "Sync_ItemPlaceholder");
		
		GameRegistry.registerTileEntity(TileEntityShellConstructor.class, "Sync_TEShellConstructor");
		GameRegistry.registerTileEntity(TileEntityShellStorage.class, "Sync_TEShellStorage");
		GameRegistry.registerTileEntity(TileEntityTreadmill.class, "Sync_TETreadmill");
		
        Sync.channels = ChannelHandler.getChannelHandlers("Sync", PacketSyncRequest.class, PacketZoomCamera.class, PacketPlayerDeath.class, PacketUpdatePlayerOnZoomFinish.class, PacketPlayerEnterStorage.class, PacketShellDeath.class, PacketSession.class, PacketClearShellList.class, PacketShellState.class, PacketNBT.class);
    }

	public void initTickHandlers() 
	{
	}
	
}

package me.ichun.mods.sync.common.core;

import me.ichun.mods.sync.common.shell.ShellHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nonnull;

public class CommandSync extends CommandBase {

	@Override
	@Nonnull
	public String getName() {
		return "sync";
	}

	@Override
	@Nonnull
	public String getUsage(@Nonnull ICommandSender iCommandSender) {
		return "chat.command.sync.usage";
	}

	@Override
	public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
		if (args.length >= 1) {
			if (args[0].equals("clear") && args.length == 2) {
				EntityPlayer entityPlayer = getPlayer(server, sender, args[1]);
				ShellHandler.syncInProgress.remove(args[1]);
				entityPlayer.getEntityData().setBoolean("isDeathSyncing", false);
				notifyCommandListener(sender, this, "chat.command.clear.success", sender.getName(), args[1]);
			}
			else {
				sender.sendMessage(new TextComponentTranslation("chat.command.clear.usage"));
			}
		}
		else {
			sender.sendMessage(new TextComponentTranslation("chat.command.sync.usage"));
		}
	}
}

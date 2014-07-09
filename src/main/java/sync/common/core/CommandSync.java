package sync.common.core;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;
import sync.common.shell.ShellHandler;

public class CommandSync extends CommandBase {

	@Override
	public String getCommandName() {
		return "sync";
	}

	@Override
	public String getCommandUsage(ICommandSender iCommandSender) {
		return "chat.command.sync.usage";
	}

	@Override
	public void processCommand(ICommandSender iCommandSender, String[] args) {
		if (args.length >= 1) {
			if (args[0].equals("clear") && args.length == 2) {
				EntityPlayer entityPlayer = getPlayer(iCommandSender, args[1]);
				if (entityPlayer != null) {
					ShellHandler.syncInProgress.remove(args[1]);
					entityPlayer.getEntityData().setBoolean("isDeathSyncing", false);
					notifyAdmins(iCommandSender, "chat.command.clear.success", iCommandSender.getCommandSenderName(), args[1]);
				}
			}
			else {
				iCommandSender.addChatMessage(new ChatComponentTranslation("chat.command.clear.usage"));
			}
		}
		else {
			iCommandSender.addChatMessage(new ChatComponentTranslation("chat.command.sync.usage"));
		}
	}
}

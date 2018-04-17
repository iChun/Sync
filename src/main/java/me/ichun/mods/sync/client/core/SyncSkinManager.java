package me.ichun.mods.sync.client.core;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import me.ichun.mods.ichunutil.common.core.util.EntityHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@SideOnly(Side.CLIENT)
public class SyncSkinManager {
    //Cache skins throughout TEs to avoid hitting the rate limit for skin session servers
    //Hold values for a longer time, so they are loaded fast if many TEs with the same player are loaded, or when loading other chunks with the same player
    //Skin loading priority: Cache(fastest), NetworkPlayer(only available when player is only and in same dim as shell, fast), SessionService(slow)
    private static final Cache<String, ResourceLocation> skinCache = CacheBuilder.newBuilder().expireAfterAccess(15, TimeUnit.MINUTES).build();
    private static final Multimap<String, Consumer<ResourceLocation>> callbackMap = MultimapBuilder.hashKeys().arrayListValues().build();

    public static void get(String playerName, Consumer<ResourceLocation> callback) {
        ResourceLocation loc = skinCache.getIfPresent(playerName);
        if (loc != null) {
            callback.accept(loc);
            return;
        }
        NetHandlerPlayClient networkHandler = Minecraft.getMinecraft().getConnection();
        NetworkPlayerInfo playerInfo = networkHandler == null ? null : networkHandler.getPlayerInfo(playerName);
        if (playerInfo != null) { //load from network player
            loc = playerInfo.getLocationSkin();
            if (loc != DefaultPlayerSkin.getDefaultSkin(playerInfo.getGameProfile().getId())) {
                callback.accept(loc);
                skinCache.put(playerName, loc);
                return;
            }
        }
        GameProfile profile = EntityHelper.getGameProfile(playerName);
        synchronized (callbackMap) {
            if (!callbackMap.containsKey(playerName)) {
                //Make one call per user - again rate limit protection
                Minecraft.getMinecraft().getSkinManager().loadProfileTextures(profile, (type, location, profileTexture) -> {
                    if (type == MinecraftProfileTexture.Type.SKIN) {
                        synchronized (callbackMap) {
                            callbackMap.removeAll(playerName).forEach(consumer -> consumer.accept(location));
                        }
                        skinCache.put(playerName, location);
                    }
                }, true);
            }
            callbackMap.put(playerName, callback);
        }
    }

    public static void invalidateCaches()
    {
        skinCache.invalidateAll();
    }
}

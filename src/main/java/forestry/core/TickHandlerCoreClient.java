/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core;

import java.util.concurrent.ConcurrentLinkedQueue;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;

import forestry.core.proxy.Proxies;
import forestry.core.utils.GeneticsUtil;

public class TickHandlerCoreClient {

	private static final ConcurrentLinkedQueue<String> messages = new ConcurrentLinkedQueue<String>();
	private boolean hasNaturalistView;

	public TickHandlerCoreClient() {
		FMLCommonHandler.instance().bus().register(this);
	}

	public void queueChatMessage(String message) {
		messages.add(message);
	}

	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent event) {
		if (event.phase != Phase.END) {
			return;
		}

		EntityPlayer player = Proxies.common.getClientInstance().thePlayer;
		boolean hasNaturalistEye = GeneticsUtil.hasNaturalistEye(player);
		if (hasNaturalistEye != hasNaturalistView) {
			hasNaturalistView = !hasNaturalistView;
			Proxies.common.getClientInstance().renderGlobal.markBlockRangeForRenderUpdate(
					(int) player.posX - 32, (int) player.posY - 32, (int) player.posZ - 32,
					(int) player.posX + 32, (int) player.posY + 32, (int) player.posZ + 32);
		}

		if (messages.size() > 0) {
			String message;
			while ((message = messages.poll()) != null) {
				player.addChatMessage(new ChatComponentText(message));
			}
		}
	}
}

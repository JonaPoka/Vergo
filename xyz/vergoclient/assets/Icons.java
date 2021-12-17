package xyz.vergoclient.assets;

import net.minecraft.util.ResourceLocation;

public enum Icons {
	
	MISSING_TEXTURE(new ResourceLocation("Vergo/loading.png")),
	ROUNDEDRECT15PX(new ResourceLocation("Vergo/icons/roundedRect15px.png")),
	CIRCLE(new ResourceLocation("Vergo/icons/circle.png")),
	GEAR(new ResourceLocation("Vergo/icons/gear.png")),
	DROPDOWN(new ResourceLocation("Vergo/icons/dropdown.png")),
	CLICKGUICOMBAT(new ResourceLocation("Vergo/clickgui/combat.png")),
	CLICKGUIMEMES(new ResourceLocation("Vergo/clickgui/memes.png")),
	CLICKGUIMISC(new ResourceLocation("Vergo/clickgui/misc.png")),
	CLICKGUIMOVEMENT(new ResourceLocation("Vergo/clickgui/movement.png")),
	CLICKGUIPLAYER(new ResourceLocation("Vergo/clickgui/player.png")),
	CLICKGUIVISUAL(new ResourceLocation("Vergo/clickgui/visual.png"));
	
	public final ResourceLocation iconLocation;
	
	private Icons(ResourceLocation iconLocation) {
		this.iconLocation = iconLocation;
	}
	
}

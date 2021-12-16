package xyz.vergoclient.assets;

import net.minecraft.util.ResourceLocation;

public enum Icons {
	
	MISSING_TEXTURE(new ResourceLocation("hummus/loading.png")),
	ROUNDEDRECT15PX(new ResourceLocation("hummus/icons/roundedRect15px.png")),
	CIRCLE(new ResourceLocation("hummus/icons/circle.png")),
	GEAR(new ResourceLocation("hummus/icons/gear.png")),
	DROPDOWN(new ResourceLocation("hummus/icons/dropdown.png")),
	CLICKGUICOMBAT(new ResourceLocation("hummus/clickgui/combat.png")),
	CLICKGUIMEMES(new ResourceLocation("hummus/clickgui/memes.png")),
	CLICKGUIMISC(new ResourceLocation("hummus/clickgui/misc.png")),
	CLICKGUIMOVEMENT(new ResourceLocation("hummus/clickgui/movement.png")),
	CLICKGUIPLAYER(new ResourceLocation("hummus/clickgui/player.png")),
	CLICKGUIVISUAL(new ResourceLocation("hummus/clickgui/visual.png"));
	
	public final ResourceLocation iconLocation;
	
	private Icons(ResourceLocation iconLocation) {
		this.iconLocation = iconLocation;
	}
	
}

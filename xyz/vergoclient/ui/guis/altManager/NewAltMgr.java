package xyz.vergoclient.ui.guis.altManager;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;
import xyz.vergoclient.files.impl.FileAlts;
import xyz.vergoclient.ui.fonts.FontUtil;
import xyz.vergoclient.ui.fonts.JelloFontRenderer;
import xyz.vergoclient.ui.guis.GuiAltManager;
import xyz.vergoclient.ui.guis.GuiMainMenu;
import xyz.vergoclient.ui.guis.altManager.addOns.Account;
import xyz.vergoclient.ui.guis.altManager.addOns.AccountManager;
import xyz.vergoclient.util.ColorUtils;
import xyz.vergoclient.util.GuiUtils;
import xyz.vergoclient.util.RenderUtils2;
import xyz.vergoclient.util.SessionChanger;
import xyz.vergoclient.util.datas.DataDouble5;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.lwjgl.opengl.GL11.*;
import static xyz.vergoclient.ui.guis.GuiAltManager.addAltButtons;
import static xyz.vergoclient.ui.guis.GuiAltManager.altOptionButtons;

public final class NewAltMgr extends GuiScreen {

    private final GuiScreen parent;

    public NewAltMgr(GuiScreen parent) {
        this.parent = parent;
    }

    private static GuiAltManager guiAltManager = new GuiAltManager();
    public static GuiAltManager getGuiAltManager() {
        return guiAltManager;
    }

    private GuiButton loginButton, deleteButton, altButton;

    private Account currentAccount;

    private boolean scrollbarActive;
    private  int scrollOffset;

    private long lastClick;

    public static FileAlts altsFile = new FileAlts();

    public static transient double scroll = 0, scrollTarget = 0;
    public static transient boolean isAddingAlt = false, isLoggingIntoAlt = false, isAltSelected = false;
    public static transient long deadAltMessage = Long.MIN_VALUE;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        // Draws the entire background.
        //RenderUtils2.drawRect(0, 0, width, height, new Color(20, 20, 20).getRGB());

        // Smooth Shadow Spell
        glDisable(GL_ALPHA_TEST);

        // Background
        RenderUtils2.drawRect(0, 0, width, height, new Color(10, 10, 10).getRGB());

        // Top
        RenderUtils2.drawRect(0, 0, this.width, 45, new Color(10, 10, 10).getRGB());

        // Shadow-Top
        ColorUtils.glDrawFilledQuad(0, 45, this.width, 4, 0x96000000, 0);

        JelloFontRenderer comNorm = FontUtil.comfortaaHuge;
        JelloFontRenderer comRNorm = FontUtil.comfortaaNormal;
        JelloFontRenderer comSmall = FontUtil.comfortaaSmall;

        String altText = "Vergo Alt Manager";
        String altAmnt = String.format("You Have \2476%s\247r Alts.", altsFile.alts.size());

        // Bottom
        ColorUtils.glDrawFilledQuad(0, this.height - 58, this.width, 58, 0xFF090909);

        // Bottom shadow
        ColorUtils.glDrawFilledQuad(0, this.height - 62, this.width, 4, 0, 0x96000000);

        glEnable(GL_ALPHA_TEST);

        mc.fontRendererObj.drawStringWithShadow("\2477Current Account: \247B" + mc.getSession().getUsername(), 4, 4, 0xFFFFFFFF);

        comNorm.drawString(altText, width / 2 - (comNorm.getStringWidth(altText) / 2), 13, -1);
        comSmall.drawString(altAmnt, width / 2 - (comSmall.getStringWidth(altAmnt) / 2), 33, -1);

        scrollTarget += Mouse.getDWheel();
        scroll += (scrollTarget - scroll) / 1;

        if (scroll >= 0) {
            scrollTarget = 0;
        }
        if (scroll < (((altsFile.alts.size() - (height / ((height / 12) + 5	))) * ((height / 12) + 5)) + 5 + (height / 12)) * -1) {
            if (altsFile.alts.size() >= (height / ((height / 12) + 5))) {
                scrollTarget = (((altsFile.alts.size() - (height / ((height / 12) + 5))) * ((height / 12) + 5)) + 5 + (height / 12)) * -1;
            }else {
                scrollTarget = 0;
            }
        }

        // All the font renderers
        JelloFontRenderer fr1 = FontUtil.jelloFontAddAlt2;
        JelloFontRenderer fr2 = FontUtil.arialRegular;
        JelloFontRenderer fr3 = FontUtil.neurialGrotesk;


        // Creates the alt buttons
        CopyOnWriteArrayList<GuiAltManager.Button> altButtons = new CopyOnWriteArrayList<>();

        int altButtonOffset = 0;
        CopyOnWriteArrayList<FileAlts.Alt> alteningAlts = new CopyOnWriteArrayList<FileAlts.Alt>();
        for (FileAlts.Alt alt : altsFile.alts) {

            if (alt.email.toLowerCase().contains("@alt.com")) {
                System.out.println("Removed " + alt.email + " because it's an altening token and not a real alt");
                alteningAlts.add(alt);
                continue;
            }

            GuiAltManager.Button altButton = new GuiAltManager.Button();
            DataDouble5 altPos = new DataDouble5();
            altPos.x1 = width / 2 - 81;
            altPos.x2 = ((width / 8) * 5) - 10;
            altPos.y1 = 45 + (altButtonOffset * ((height / 12) + 5)) + 5 + scroll;
            altPos.y2 = 50 + (altButtonOffset * ((height / 12) + 5)) + 5 + (height / 12) + scroll;
            altButton.posAndColor = altPos;
            altButton.alt = alt;
            altButton.action = new Runnable() {

                @Override
                public void run() {

                    if (alt.password.isEmpty()) {
                        SessionChanger.getInstance().setUserOffline(alt.username);
                    }else {
                        new Thread("ALT-SWITCH-LOG") {
                            @Override
                            public void run() {
                                isLoggingIntoAlt = true;
                                String username = mc.session.getUsername();
                                SessionChanger.getInstance().setUser(alt.email, alt.password);
                                if (!username.equals(mc.session.getUsername()))
                                    alt.username = mc.session.getUsername();
                                else
                                    deadAltMessage = System.currentTimeMillis() + 2500;
                                isLoggingIntoAlt = false;
                            }
                        }.start();
                    }
                }
            };

            if (altPos.y2 < 0) {
                altButtonOffset++;
                continue;
            }

            if (altPos.y1 > height) {
                break;
            }

            altButtons.add(altButton);

            altButtonOffset++;

        }
        altsFile.alts.removeAll(alteningAlts);
        GuiAltManager.altButtons = altButtons;

        // Draws all the alts
        for (GuiAltManager.Button button : GuiAltManager.altButtons) {

            //Gui.drawRect(button.posAndColor.x1, button.posAndColor.y1, button.posAndColor.x2, button.posAndColor.y2, Colors.ALT_MANAGER_BUTTONS.getColor());
            if (button.alt.username.equals(mc.session.getUsername())) {
                //Gui.drawRect(button.posAndColor.x2 - 10, button.posAndColor.y1, button.posAndColor.x2, button.posAndColor.y2, Colors.ALT_MANAGER_PURPLE.getColor());

                RenderUtils2.drawBorderedRect((int) button.posAndColor.x1, (int) button.posAndColor.y1,(float) fr3.getStringWidth(button.alt.username + 40), 40, 1f, new Color(19, 19, 19, 255), new Color(64, 64, 64, 190));
            }
            fr3.drawString(button.alt.username, button.posAndColor.x1 + 5, (float) button.posAndColor.y1 + 5, -1);
            fr3.drawString(button.alt.email, button.posAndColor.x1 + 5, (float) button.posAndColor.y1 + 24, -1);
        }

        // Add the login text box
        if (isAddingAlt) {
            Gui.drawRect(0, 0, width, height, 0x90000000);
            for (GuiAltManager.Button button : addAltButtons) {
                RenderUtils2.drawBorderedRect((int) button.posAndColor.x1, (int) button.posAndColor.y1, 640, 76.5f, 1f, new Color(17, 17, 17, 255), new Color(255, 255, 255, 255));
                if (!button.isEnabled) {
                    //Gui.drawRect(button.posAndColor.x1 + 5, button.posAndColor.y1 + 5, button.posAndColor.x2 - 5, button.posAndColor.y2 - 5, Colors.ALT_MANAGER_BACKGROUND.getColor());
                }else {
                    //Gui.drawRect(button.posAndColor.x1, button.posAndColor.y1 + ((button.posAndColor.y2 - button.posAndColor.y1) / 2), button.posAndColor.x2, (button.posAndColor.y1 + ((button.posAndColor.y2 - button.posAndColor.y1) / 2)) + (((button.posAndColor.y2 - button.posAndColor.y1) / 2) * button.hoverAnimation), Colors.ALT_MANAGER_PURPLE.getColor());
                    //Gui.drawRect(button.posAndColor.x1, (button.posAndColor.y1 + ((button.posAndColor.y2 - button.posAndColor.y1) / 2)) - (((button.posAndColor.y2 - button.posAndColor.y1) / 2) * button.hoverAnimation), button.posAndColor.x2, button.posAndColor.y1 + ((button.posAndColor.y2 - button.posAndColor.y1) / 2), Colors.ALT_MANAGER_PURPLE.getColor());
//					Gui.drawRect(button.posAndColor.x1, button.posAndColor.y2 - ((button.posAndColor.y2 - button.posAndColor.y1) * button.hoverAnimation), button.posAndColor.x2, button.posAndColor.y2, Colors.ALTMANAGERPURPLE.getColor());
                }
                if (GuiUtils.isMouseOverDataDouble5(mouseX, mouseY, button.posAndColor) && Mouse.isInsideWindow() && button.isEnabled) {
                    RenderUtils2.drawBorderedRect((int) button.posAndColor.x1, (int) button.posAndColor.y1, 640, 76.5f, 1f, new Color(24, 24, 24, 195), new Color(255, 255, 255, 255));
                    //button.hoverAnimation += (1 - button.hoverAnimation) / 2;
                }else {
                    //button.hoverAnimation += (-button.hoverAnimation) / 2;
                }
                if (button.drawTextAsPassword) {
                    String displayText = "";
                    for (int i = 0; i < button.displayText.length(); i++) {
                        displayText += "*";
                    }
                    fr1.drawPassword(button.displayText, (float) (button.posAndColor.x1 + ((button.posAndColor.x2 - button.posAndColor.x1) / 2)) - (fr1.getStringWidth(displayText) / 2), (float) (button.posAndColor.y1 + ((button.posAndColor.y2 - button.posAndColor.y1) / 2) - fr1.FONT_HEIGHT + 1), -1);
                }else {
                    fr1.drawCenteredString(button.displayText, (float) (button.posAndColor.x1 + ((button.posAndColor.x2 - button.posAndColor.x1) / 2)), (float) (button.posAndColor.y1 + ((button.posAndColor.y2 - button.posAndColor.y1) / 2) - fr1.FONT_HEIGHT + 1), -1);
                }
            }
        }
        else if (isAltSelected) {
            //Gui.drawRect(0, 0, width, height, 0x90000000);
            for (GuiAltManager.Button button : altOptionButtons) {
                //Gui.drawRect(button.posAndColor.x1, button.posAndColor.y1, button.posAndColor.x2, button.posAndColor.y2, Colors.ALT_MANAGER_BUTTONS.getColor());
                RenderUtils2.drawBorderedRect((int) button.posAndColor.x1, (int) button.posAndColor.y1, (float) button.posAndColor.x1 - 272, 30, 1f, new Color(57, 57, 57, 195), new Color(255, 255, 255, 255));
                if (!button.isEnabled) {
                    //Gui.drawRect(button.posAndColor.x1, button.posAndColor.y1, button.posAndColor.x2, button.posAndColor.y2, Colors.ALT_MANAGER_BACKGROUND.getColor());
                }else {
                    //Gui.drawRect(button.posAndColor.x1, button.posAndColor.y1 + ((button.posAndColor.y2 - button.posAndColor.y1) / 2), button.posAndColor.x2, (button.posAndColor.y1 + ((button.posAndColor.y2 - button.posAndColor.y1) / 2)) + (((button.posAndColor.y2 - button.posAndColor.y1) / 2) * button.hoverAnimation), Colors.ALT_MANAGER_PURPLE.getColor());
                    //Gui.drawRect(button.posAndColor.x1, (button.posAndColor.y1 + ((button.posAndColor.y2 - button.posAndColor.y1) / 2)) - (((button.posAndColor.y2 - button.posAndColor.y1) / 2) * button.hoverAnimation), button.posAndColor.x2, button.posAndColor.y1 + ((button.posAndColor.y2 - button.posAndColor.y1) / 2), Colors.ALT_MANAGER_PURPLE.getColor());
//					Gui.drawRect(button.posAndColor.x1, button.posAndColor.y2 - ((button.posAndColor.y2 - button.posAndColor.y1) * button.hoverAnimation), button.posAndColor.x2, button.posAndColor.y2, Colors.ALTMANAGERPURPLE.getColor());
                }
                if (GuiUtils.isMouseOverDataDouble5(mouseX, mouseY, button.posAndColor) && Mouse.isInsideWindow() && button.isEnabled) {
                    button.hoverAnimation += (1 - button.hoverAnimation) / 2;
                }else {
                    button.hoverAnimation += (-button.hoverAnimation) / 2;
                }
                if (button.drawTextAsPassword) {
                    String displayText = "";
                    for (int i = 0; i < button.displayText.length(); i++) {
                        displayText += "*";
                    }
                    fr1.drawPassword(button.displayText, (float) (button.posAndColor.x1 + ((button.posAndColor.x2 - button.posAndColor.x1) / 2)) - (fr1.getStringWidth(displayText) / 2), (float) (button.posAndColor.y1 + ((button.posAndColor.y2 - button.posAndColor.y1) / 2) - fr1.FONT_HEIGHT + 1), -1);
                }else {
                    fr1.drawCenteredString(button.displayText, (float) (button.posAndColor.x1 + ((button.posAndColor.x2 - button.posAndColor.x1) / 2)), (float) (button.posAndColor.y1 + ((button.posAndColor.y2 - button.posAndColor.y1) / 2) - fr1.FONT_HEIGHT + 1), -1);
                }
            }
        }

        RenderUtils2.drawRect(0, this.height - 58, this.width, 58, 0xFF090909);

    }

    @Override
    public void initGui() {
        final int xBuffer = 2;
        final int yBuffer = 2;
        final int unbufferedWidth = this.width / 6;
        final int buttonWidth = this.width / 6 - xBuffer * 2;
        final int buttonHeight = 20;
        final int unbufferedHeight = buttonHeight + yBuffer * 2;

        final int left = this.width / 3;
        final int top = this.height - 50;

        this.buttonList.add(loginButton = new GuiButton(0, left + xBuffer, height / 2, buttonWidth, buttonHeight, "Login"));

    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch(button.id) {
            case 0:
                mc.displayGuiScreen(new GuiMainMenu());
        }
    }

}

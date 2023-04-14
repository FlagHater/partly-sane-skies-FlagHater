//
// Written by Su386.
// See LICENSE for copright and license notices.
//

package me.partlysanestudios.partlysaneskies;

import java.awt.Color;
import java.lang.reflect.Field;

import gg.essential.elementa.ElementaVersion;
import gg.essential.elementa.UIComponent;
import gg.essential.elementa.components.UIText;
import gg.essential.elementa.components.Window;
import gg.essential.elementa.constraints.CenterConstraint;
import gg.essential.elementa.constraints.PixelConstraint;
import gg.essential.universal.UMatrixStack;
import me.partlysanestudios.partlysaneskies.utils.StringUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class NoCookieWarning {
    private static float TEXT_SCALE = 2.5f;

    private static Color color;
    private static String displayString = "";
    private static long lastWarnTime;

    private static Window window = new Window(ElementaVersion.V2);
    private static UIComponent displayText = new UIText(displayString)
            .setTextScale(new PixelConstraint(TEXT_SCALE))
            .setX(new CenterConstraint())
            .setY(new PixelConstraint(window.getHeight() * .2f))
            .setColor(Color.white)
            .setChildOf(window);

    public NoCookieWarning() {
        lastWarnTime = PartlySaneSkies.getTime();
    }

    public static IChatComponent getFooter() {
        GuiPlayerTabOverlay tabList = Minecraft.getMinecraft().ingameGUI.getTabList();
        Field footerField = ReflectionHelper.findField(GuiPlayerTabOverlay.class, "field_175255_h", "footer");

        IChatComponent footer;
        try {
            footer = (IChatComponent) footerField.get(tabList);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }

        return footer;
    }

    // Returns 1 if it has a cookie, 0 if it doesn't and -1 if it cannot be
    // determined
    public static int hasBoosterCookie() {
        for (IChatComponent chatComponent : getFooter().getSiblings()) {
            if (StringUtils.removeColorCodes(chatComponent.getFormattedText()).toLowerCase()
                    .contains("not active! obtain booster cookies")) {
                return 0;
            }
        }
        if (getFooter().getSiblings().size() == 0)
            return -1;
        return 1;
    }

    public static boolean hasLotsOfCoins() {
        if (PartlySaneSkies.getCoins() > PartlySaneSkies.config.maxWithoutCookie) {
            return true;
        } else {
            return false;
        }
    }

    public static void warn() {
        lastWarnTime = PartlySaneSkies.getTime();
        color = Color.red;
        displayString = "No Booster Cookie. You will loose your coins on death";
        PartlySaneSkies.minecraft.getSoundHandler()
                .playSound(PositionedSoundRecord.create(new ResourceLocation("partlysaneskies", "bell")));
    }

    public static long getTimeSinceLastWarn() {
        return PartlySaneSkies.getTime() - lastWarnTime;
    }

    public static boolean checkExpire() {
        return getTimeSinceLastWarn() > PartlySaneSkies.config.noCookieWarnTime * 1000;
    }

    public static boolean checkWarnAgain() {
        if (getTimeSinceLastWarn() > PartlySaneSkies.config.noCookieWarnCooldown * 1000) {
            return true;
        }

        else {
            return false;
        }
    }

    @SubscribeEvent
    public void renderText(RenderGameOverlayEvent.Text event) {
        short alpha = LocationBannerDisplay.getAlpha(getTimeSinceLastWarn(), PartlySaneSkies.config.noCookieWarnTime);

        if (color == null)
            color = Color.gray;
        else
            color = new Color(color.getRed(), color.getGreen(), color.getBlue(), (short) alpha);
        float scaleFactor = (window.getWidth()) / 1075f;
        ((UIText) displayText)
                .setText(displayString)
                .setTextScale(new PixelConstraint(TEXT_SCALE * scaleFactor))
                .setX(new CenterConstraint())
                .setY(new PixelConstraint(window.getHeight() * .125f))
                .setColor(color);
        window.draw(new UMatrixStack());

        if (checkExpire())
            displayString = "";
    }

    @SubscribeEvent
    public void checkCoinsTick(ClientTickEvent event) {
        if (!PartlySaneSkies.isSkyblock()) {
            return;
        }
        if (!PartlySaneSkies.config.noCookieWarning) {
            return;
        }
        if (PartlySaneSkies.getCoins() < PartlySaneSkies.config.maxWithoutCookie) {
            return;
        }
        if (hasBoosterCookie() == 1) {
            return;
        }
        if (!checkWarnAgain()) {
            return;
        }

        if (!hasLotsOfCoins()) {
            return;
        }

        warn();
        lastWarnTime = PartlySaneSkies.getTime();
    }

}

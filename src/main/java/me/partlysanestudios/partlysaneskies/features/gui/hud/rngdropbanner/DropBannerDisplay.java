//
// Written by Su386.
// See LICENSE for copyright and license notices.
//

package me.partlysanestudios.partlysaneskies.features.gui.hud.rngdropbanner;

import gg.essential.elementa.ElementaVersion;
import gg.essential.elementa.UIComponent;
import gg.essential.elementa.components.UIWrappedText;
import gg.essential.elementa.components.Window;
import gg.essential.elementa.constraints.CenterConstraint;
import gg.essential.elementa.constraints.PixelConstraint;
import gg.essential.universal.UMatrixStack;
import kotlin.text.Regex;
import me.partlysanestudios.partlysaneskies.PartlySaneSkies;
import me.partlysanestudios.partlysaneskies.utils.MathUtils;
import me.partlysanestudios.partlysaneskies.utils.StringUtils;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class DropBannerDisplay extends Gui {
    public static Drop drop;
    float SMALL_TEXT_SCALE = 2.5f;
    float BIG_TEXT_SCALE = 5f;


    @SubscribeEvent
    public void onChatMessage(ClientChatReceivedEvent event) {
        String formattedMessage = event.message.getFormattedText();

        if (!isRareDrop(formattedMessage)) {
            return;
        }

        if (PartlySaneSkies.Companion.getConfig().getRareDropBannerSound()) {
            PartlySaneSkies.Companion.getMinecraft().thePlayer.playSound("partlysaneskies:rngdropjingle", 100, 1);
        }

        if (PartlySaneSkies.Companion.getConfig().getRareDropBanner()) {
            String unformattedMessage = event.message.getUnformattedText();

            // Gets the name of the drop category
            String dropCategory = unformattedMessage.substring(0, unformattedMessage.indexOf("! ") + 1);

            // Gets the colour of the drop category
            Color dropCategoryHex = StringUtils.INSTANCE.colorCodeToColor(formattedMessage.substring(2, 4));

            // // Finds the amount of magic find from the message
            String name = formattedMessage.substring(formattedMessage.indexOf("! ") + 2);

            DropBannerDisplay.drop = new Drop(name, dropCategory, 1, PartlySaneSkies.Companion.getTime(), dropCategoryHex);
        }
    }

    public static boolean isRareDrop(String formattedMessage) {
        Regex regex = new Regex("(§.)+(\\w)*(RARE|PET) DROP!.*");

        return formattedMessage.matches(regex.toString());
    }

    Window window = new Window(ElementaVersion.V2);
    String topString = "empty";
    String dropNameString = "empty";
    String magicFindString = "empty"; // Apparently we never use the magic find string

    UIWrappedText topText = (UIWrappedText) new UIWrappedText(dropNameString, true, new Color(0, 0, 0, 0), true)
            .setTextScale(new PixelConstraint(BIG_TEXT_SCALE/672 * window.getHeight() * PartlySaneSkies.Companion.getConfig().getBannerSize()))
            .setWidth(new PixelConstraint(window.getWidth()))
            .setX(new CenterConstraint())
            .setY(new PixelConstraint(window.getHeight() * .333f))
            .setChildOf(window);

    UIWrappedText dropNameText = (UIWrappedText) new UIWrappedText(dropNameString, true, new Color(0, 0, 0, 0), true)
            .setTextScale(new PixelConstraint(SMALL_TEXT_SCALE/672 * window.getHeight() * PartlySaneSkies.Companion.getConfig().getBannerSize()))
            .setWidth(new PixelConstraint(window.getWidth()))
            .setX(new CenterConstraint())
            .setY(new PixelConstraint(topText.getBottom() + window.getHeight() * .05f))
            .setChildOf(window);

    @SubscribeEvent
    public void renderText(RenderGameOverlayEvent.Text event) {
        Color categoryColor;

        if (DropBannerDisplay.drop == null) {
            dropNameString = "";
            topString = "";
            magicFindString = "";
            categoryColor = new Color(255, 255, 255, 0);
        } else {
            categoryColor = drop.dropCategoryColor;
            dropNameString = "x" + drop.amount + " " + drop.name;
            topString = drop.dropCategory;

            // It should blink after a third of the rare drop time, and before 5/6ths
            if (PartlySaneSkies.Companion.getTime() - drop.timeDropped > (1f / 3f * PartlySaneSkies.Companion.getConfig().getRareDropBannerTime() * 1000)
                    && PartlySaneSkies.Companion.getTime()
                    - drop.timeDropped < (10f / 12f * PartlySaneSkies.Companion.getConfig().getRareDropBannerTime() * 1000)) {
                if (Math.round((drop.timeDropped - PartlySaneSkies.Companion.getTime()) / 1000f * 4) % 2 == 0) {
                    categoryColor = Color.white;
                } else {
                    categoryColor = drop.dropCategoryColor;
                }
            }

            if (!MathUtils.INSTANCE.onCooldown(drop.timeDropped, (long) (PartlySaneSkies.Companion.getConfig().getRareDropBannerTime() * 1000))) {
                drop = null;
            }
        }
        if (topText.getText().isEmpty() && topString.isEmpty() && dropNameText.getText().isEmpty() && dropNameString.isEmpty()) { // Small performance saver
            return;
        }

        float scale = PartlySaneSkies.Companion.getConfig().getBannerSize();

        topText
            .setText(topString)
            .setTextScale(new PixelConstraint(BIG_TEXT_SCALE/672 * window.getHeight() * scale))
            .setWidth(new PixelConstraint(window.getWidth()))
            .setX(new CenterConstraint())
            .setY(new PixelConstraint(window.getHeight() * .3f))
            .setColor(categoryColor);

        dropNameText
            .setText(dropNameString)
            .setTextScale(new PixelConstraint(SMALL_TEXT_SCALE/672 * window.getHeight() * scale))
            .setWidth(new PixelConstraint(window.getWidth()))
            .setX(new CenterConstraint())
            .setY(new PixelConstraint(topText.getBottom() + window.getHeight() * (.05f * scale)));

        window.draw(new UMatrixStack());
    }
}

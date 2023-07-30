//
// Written by Su386.
// See LICENSE for copyright and license notices.
//

package me.partlysanestudios.partlysaneskies.system;

import me.partlysanestudios.partlysaneskies.PartlySaneSkies;
import me.partlysanestudios.partlysaneskies.WikiArticleOpener;
import org.lwjgl.input.Keyboard;

import me.partlysanestudios.partlysaneskies.auctionhouse.AhGui;
import me.partlysanestudios.partlysaneskies.dungeons.partymanager.PartyManager;
import me.partlysanestudios.partlysaneskies.help.HelpCommand;
import me.partlysanestudios.partlysaneskies.petalert.PetAlert;
import me.partlysanestudios.partlysaneskies.utils.Utils;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.GuiScreenEvent.KeyboardInputEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

public final class Keybinds {

    private final static String PSS_CATEGORY = "Partly Sane Skies";

    // public static KeyBinding debugKey;
    public static KeyBinding configKey;
    public static KeyBinding partyManagerKey;
    public static KeyBinding helpKey;
    public static KeyBinding wardrobeKeybind;
    public static KeyBinding petKeybind;
    public static KeyBinding craftKeybind;
    public static KeyBinding storageKeybind;
    public static KeyBinding wikiKeybind;
    public static KeyBinding favouritePetKeybind;

    public static void init() {
        // debugKey = registerKey("Debug", PSS_CATEGORY, Keyboard.KEY_F4);
        configKey = registerKey("Config", PSS_CATEGORY, Keyboard.KEY_F7);
        partyManagerKey = registerKey("Party Manager", PSS_CATEGORY, Keyboard.KEY_M);
        helpKey = registerKey("Help", PSS_CATEGORY, Keyboard.KEY_H);
        wardrobeKeybind = registerKey("Open Wardrobe Menu", PSS_CATEGORY, Keyboard.CHAR_NONE);
        petKeybind = registerKey("Open Pets Menu", PSS_CATEGORY, Keyboard.CHAR_NONE);
        craftKeybind = registerKey("Open Crafting Table", PSS_CATEGORY, Keyboard.CHAR_NONE);
        storageKeybind = registerKey("Open Storage Menu", PSS_CATEGORY, Keyboard.CHAR_NONE);
        wikiKeybind = registerKey("Open Wiki Article", PSS_CATEGORY, Keyboard.KEY_X);
        favouritePetKeybind = registerKey("Favourite Pet", PSS_CATEGORY, Keyboard.KEY_F);

    }

    private static KeyBinding registerKey(String name, String category, int keycode) {
        final KeyBinding key = new KeyBinding(name, keycode, category);
        ClientRegistry.registerKeyBinding(key);
        return key;
    }

    @SubscribeEvent
    public void keybindWhileInGui(KeyboardInputEvent.Post event) {
        // if (Keyboard.isKeyDown(debugKey.getKeyCode())) {
        //     PartlySaneSkies.debugMode();
        // }

        if (Keyboard.isKeyDown(wikiKeybind.getKeyCode())) {
            WikiArticleOpener.keyDown();
        }

        if (Keyboard.isKeyDown(favouritePetKeybind.getKeyCode())) {
            PetAlert.favouritePet();
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
            if (PartlySaneSkies.minecraft.currentScreen instanceof AhGui) {
                Utils.clickOnSlot(46);
            }
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
            if (PartlySaneSkies.minecraft.currentScreen instanceof AhGui) {
                Utils.clickOnSlot(53);
            }
        }
    }

    @SubscribeEvent
    public void checkKeyBinds(KeyInputEvent event) {
        // if (debugKey.isPressed()) {
        //     PartlySaneSkies.debugMode();
        // }
        if (configKey.isPressed()) {
            PartlySaneSkies.config.openGui();
        }
        if (partyManagerKey.isPressed()) {
            PartyManager.startPartyManager();
        }
        if (helpKey.isPressed()) {
            HelpCommand.printHelpMessage();
        }
        if (craftKeybind.isPressed()) {
            PartlySaneSkies.minecraft.thePlayer.sendChatMessage("/craft");
        }
        if (petKeybind.isPressed()) {
            PartlySaneSkies.minecraft.thePlayer.sendChatMessage("/pets");
        }
        if (wardrobeKeybind.isPressed()) {
            PartlySaneSkies.minecraft.thePlayer.sendChatMessage("/wardrobe");
        }
        if (storageKeybind.isPressed()) {
            PartlySaneSkies.minecraft.thePlayer.sendChatMessage("/storage");
        }
    }
}

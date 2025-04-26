package galysso.codicraft.numismaticutils.screen;

import com.glisco.numismaticoverhaul.ModComponents;
import com.glisco.numismaticoverhaul.item.NumismaticOverhaulItems;
import com.mojang.blaze3d.systems.RenderSystem;
import galysso.codicraft.numismaticutils.NumismaticUtilsMain;
import galysso.codicraft.numismaticutils.network.requests.*;
import galysso.codicraft.numismaticutils.screen.widgets.DynamicButton;
import galysso.codicraft.numismaticutils.screen.helper.DrawTools;
import galysso.codicraft.numismaticutils.screen.widgets.PrettyTextField;
import galysso.codicraft.numismaticutils.screen.widgets.StateSwitchingButton;
import galysso.codicraft.numismaticutils.utils.NumismaticDraw;
import galysso.codicraft.numismaticutils.utils.NumismaticUtils;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import galysso.codicraft.numismaticutils.utils.BankerUtils;
import org.lwjgl.glfw.GLFW;

import java.util.*;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class BankerScreen extends HandledScreen<BankerScreenHandler> {
    @Override protected void drawForeground(DrawContext context, int mouseX, int mouseY) {}
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_E) {return true;} // Prevent from leaving the inventory when the inventory key is pressed
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    // Status
    private enum LeftState {
        HOME,
        ACCOUNTS,
        PLAYERS
    }
    private enum RightState {
        INFO,
        TRANSFERT,
        SETTINGS,
        ACCOUNT_CREATION
    }
    private HashMap<LeftState, RightState> stateCoupleMap;
    private LeftState currentLeftState = LeftState.HOME;
    //private UUID selectedAccountId = null;
    private UUID selectedPlayerId = null;
    private Optional<BankerUtils.RIGHT_TYPE> filterRightType = Optional.empty();
    private Optional<Integer> filterIconId = Optional.empty();

    // Player balance
    private static long playerBalance = 0L;
    private boolean playerBalanceUpdated = true;

    // Alt key
    private static boolean altKeyPressed;
    private static boolean shiftKeyPressed;

    // Texture files
    private static final Identifier GUI_TEXTURE = NumismaticUtilsMain.identifier("textures/gui/banker.png");
    public static final Identifier BUTTONS_TEXTURE =  NumismaticUtilsMain.identifier("textures/gui/buttons.png");
    public static final Identifier ICONS_TEXTURE =  NumismaticUtilsMain.identifier("textures/gui/icons.png");
    public static final Identifier POPUP_TEXTURE =  NumismaticUtilsMain.identifier("textures/gui/popup_texture.png");

    private static final int NB_LEFT_BUTTONS = 10;

    // TransferFactor
    int transfertFactor;

    // Buttons
    ArrayList<DynamicButton> buttons;
    private DynamicButton goldFromMainToPouchButton;
    private DynamicButton silverFromMainToPouchButton;
    private DynamicButton bronzeFromMainToPouchButton;
    private DynamicButton goldFromPouchToMainButton;
    private DynamicButton silverFromPouchToMainButton;
    private DynamicButton bronzeFromPouchToMainButton;
    private DynamicButton fullFromMainToPouchButton;
    private DynamicButton fullFromPouchToMainButton;
    private DynamicButton goldFromOtherToMainButton;
    private DynamicButton silverFromOtherToMainButton;
    private DynamicButton bronzeFromOtherToMainButton;
    private DynamicButton goldFromMainToOtherButton;
    private DynamicButton silverMainToOtherButton;
    private DynamicButton bronzeMainToOtherButton;
    private DynamicButton fullFromOtherToMainButton;
    private DynamicButton fullFromMainToOtherButton;
    private DynamicButton homeButton;
    private DynamicButton displayPlayersButton;
    private DynamicButton displayAccountsButton;
    private DynamicButton displayInfoButton;
    private DynamicButton displayTransfertButton;
    private DynamicButton displaySettingsButton;
    private DynamicButton addAccountButton;
    private DynamicButton confirmAccountCreationButton;
    private StateSwitchingButton filterRightTypeButton;
    private DynamicButton filterIconIdButton;
    private DynamicButton closeFocusButton;
    private DynamicButton sortTypeAccountInfoButton;
    private DynamicButton filterRightTypeAccountInfoButton;
    private DynamicButton changeAccountNameButton;
    private DynamicButton invitePlayerButton;
    private DynamicButton deleteAccountButton;

    /* transfert buttons groups */
    private ArrayList<DynamicButton> fromMainToPouchButtons;
    private ArrayList<DynamicButton> smallFromMainToPouchButtons;
    private ArrayList<DynamicButton> fromPouchToMainButtons;
    private ArrayList<DynamicButton> smallFromPouchToMainButtons;
    private ArrayList<DynamicButton> fromOtherToMainButtons;
    private ArrayList<DynamicButton> smallFromOtherToMainButtons;
    private ArrayList<DynamicButton> fromMainToOtherButtons;
    private ArrayList<DynamicButton> smallFromMainToOtherButtons;
    private ArrayList<DynamicButton> smallTransfertButtons;
    private ArrayList<DynamicButton> fullTransfertButtons;
    private ArrayList<DynamicButton> bronzeAndSilverTransfertButtons;

    /* text fields */
    private PrettyTextField accountCreationNameTextField;

    /* left buttons group */
    private ArrayList<DynamicButton> leftButtons;

    public BankerScreen(BankerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        backgroundWidth = 256;
        backgroundHeight = 256;

        initData();
        initStatus();

        System.out.println("CONSTRUCTOR");
    }

    protected void init() {
        this.x = (width - backgroundWidth) / 2;
        this.y = (height - backgroundHeight) / 2;

        altKeyPressed = !HandledScreen.hasAltDown(); // To force the first buttons status update in updateAltKeyPressed()
        shiftKeyPressed = !HandledScreen.hasShiftDown();

        initButtons();
        initButtonsGroups();
        initDisplayContext();
        updatePouchToMainButtons();
        updateMainToPouchButtons();
        updateKeysPressed();
        updateLeftButtonsVisibility();
    }

    private void initData() {
        System.out.println("Fetching initial player info data");
        RequestPlayerInfoPayload requestPayload = new RequestPlayerInfoPayload(handler.syncId);
        ClientPlayNetworking.send(requestPayload);
    }

    private void initStatus() {
        stateCoupleMap = new HashMap<>() {{
            put(LeftState.HOME, RightState.INFO);
            put(LeftState.ACCOUNTS, RightState.INFO);
            put(LeftState.PLAYERS, RightState.TRANSFERT);
        }};
    }

    private void initButtons() {
        AccountData mainAccount = handler.getMainAccount();
        AccountData focusedAccount = handler.getFocusedAccount();
        buttons = new ArrayList<>();
        buttons.add(addDrawableChild(goldFromMainToPouchButton = new DynamicButton(
                this.x + 70, this.y + 17, 8, 10, 0, 0,
                Text.literal("TEST"),
                button ->  {
                    requestTransfertFromPouchToMain(-10000L);
                }
        )));
        buttons.add(addDrawableChild(silverFromMainToPouchButton = new DynamicButton(
                this.x + 70, this.y + 28, 8, 10, 0, 0,
                Text.literal("TEST"),
                button ->  {
                    requestTransfertFromPouchToMain(-100L);
                }
        )));
        buttons.add(addDrawableChild(bronzeFromMainToPouchButton = new DynamicButton(
                this.x + 70, this.y + 39, 8, 10, 0, 0,
                Text.literal("TEST"),
                button ->  {
                    requestTransfertFromPouchToMain(-1L);
                }
        )));
        buttons.add(addDrawableChild(fullFromMainToPouchButton = new DynamicButton(
                this.x + 70, this.y + 17, 8, 32, 16, 0,
                Text.literal("TEST"),
                button ->  {
                    requestTransfertFromPouchToMain(-Long.MAX_VALUE);
                }
        )));
        buttons.add(addDrawableChild(goldFromPouchToMainButton = new DynamicButton(
                this.x + 87, this.y + 17, 8, 10, 8, 0,
                Text.literal("TEST"),
                button ->  {
                    requestTransfertFromPouchToMain(10000L);
                }
        )));
        buttons.add(addDrawableChild(silverFromPouchToMainButton = new DynamicButton(
                this.x + 87, this.y + 28, 8, 10, 8, 0,
                Text.literal("TEST"),
                button ->  {
                    requestTransfertFromPouchToMain( 100L);
                }
        )));
        buttons.add(addDrawableChild(bronzeFromPouchToMainButton = new DynamicButton(
                this.x + 87, this.y + 39, 8, 10, 8, 0,
                Text.literal("TEST"),
                button ->  {
                    requestTransfertFromPouchToMain(1L);
                }
        )));
        buttons.add(addDrawableChild(fullFromPouchToMainButton = new DynamicButton(
                this.x + 87, this.y + 17, 8, 32, 24, 0,
                Text.literal("TEST"),
                button ->  {
                    requestTransfertFromPouchToMain(Long.MAX_VALUE);
                }
        )));
        buttons.add(addDrawableChild(goldFromOtherToMainButton = new DynamicButton(
                this.x + 161, this.y + 17, 8, 10, 0, 0,
                Text.literal("TEST"),
                button ->  {
                    requestTransfertFromMainToOther(-10000L);
                }
        )));
        buttons.add(addDrawableChild(silverFromOtherToMainButton = new DynamicButton(
                this.x + 161, this.y + 28, 8, 10, 0, 0,
                Text.literal("TEST"),
                button ->  {
                    requestTransfertFromMainToOther(-100L);
                }
        )));
        buttons.add(addDrawableChild(bronzeFromOtherToMainButton = new DynamicButton(
                this.x + 161, this.y + 39, 8, 10, 0, 0,
                Text.literal("TEST"),
                button ->  {
                    requestTransfertFromMainToOther(-1L);
                }
        )));
        buttons.add(addDrawableChild(fullFromOtherToMainButton = new DynamicButton(
                this.x + 161, this.y + 17, 8, 32, 16, 0,
                Text.literal("TEST"),
                button ->  {
                    requestTransfertFromMainToOther(-Long.MAX_VALUE);
                }
        )));
        buttons.add(addDrawableChild(goldFromMainToOtherButton = new DynamicButton(
                this.x + 178, this.y + 17, 8, 10, 8, 0,
                Text.literal("TEST"),
                button ->  {
                    requestTransfertFromMainToOther(10000L);
                }
        )));
        buttons.add(addDrawableChild(silverMainToOtherButton = new DynamicButton(
                this.x + 178, this.y + 28, 8, 10, 8, 0,
                Text.literal("TEST"),
                button ->  {
                    requestTransfertFromMainToOther(100L);
                }
        )));
        buttons.add(addDrawableChild(bronzeMainToOtherButton = new DynamicButton(
                this.x + 178, this.y + 39, 8, 10, 8, 0,
                Text.literal("TEST"),
                button ->  {
                    requestTransfertFromMainToOther(1L);
                }
        )));
        buttons.add(addDrawableChild(fullFromMainToOtherButton = new DynamicButton(
                this.x + 178, this.y + 17, 8, 32, 24, 0,
                Text.literal("TEST"),
                button ->  {
                    requestTransfertFromMainToOther(Long.MAX_VALUE);
                }
        )));
        buttons.add(addDrawableChild(homeButton = new DynamicButton(
                this.x + 12, this.y + 59, 24, 24, 152, 0,
                Text.literal("TEST"),
                button ->  {
                    setHomeState();
                }
        )));
        buttons.add(addDrawableChild(displayAccountsButton = new DynamicButton(
                this.x + 45, this.y + 59, 24, 24, 56, 0,
                Text.literal("TEST"),
                button ->  {
                    setAccountsState();
                }
        )));
        buttons.add(addDrawableChild(displayPlayersButton = new DynamicButton(
                this.x + 79, this.y + 59, 24, 24, 32, 0,
                Text.literal("TEST"),
                button ->  {
                    setPlayersState();
                }
        )));
        buttons.add(addDrawableChild(displayInfoButton = new DynamicButton(
                this.x + 171, this.y + 59, 24, 24, 80, 0,
                Text.literal("TEST"),
                button ->  {
                    setInfoState();
                }
        )));
        buttons.add(addDrawableChild(displayTransfertButton = new DynamicButton(
                this.x + 171, this.y + 59, 24, 24, 104, 0,
                Text.literal("TEST"),
                button ->  {
                    setTransfertState();
                }
        )));
        buttons.add(addDrawableChild(displaySettingsButton = new DynamicButton(
                this.x + 204, this.y + 59, 24, 24, 128, 0,
                Text.literal("TEST"),
                button ->  {
                    setSettingsState();
                }
        )));
        buttons.add(addDrawableChild(addAccountButton = new DynamicButton(
                this.x + 138, this.y + 112, 9, 9, 176, 0,
                Text.literal("TEST"),
                button ->  {
                    setAccountCreationState();
                }
        )));
        buttons.add(addDrawableChild(confirmAccountCreationButton = new DynamicButton(
                this.x + 184, this.y + 164, 13, 11, 211, 0,
                Text.literal("TEST"),
                button ->  {
                    stateCoupleMap.put(LeftState.ACCOUNTS, RightState.INFO);
                    RequestAccountCreationPayload requestPayload2 = new RequestAccountCreationPayload(handler.syncId, accountCreationNameTextField.getText());
                    ClientPlayNetworking.send(requestPayload2);
                    accountCreationNameTextField.setText("");
                    removeFocus();
                }
        )));
        buttons.add(addDrawableChild(filterRightTypeButton = new StateSwitchingButton<BankerUtils.RIGHT_TYPE>(
                this.x + 5, this.y + 93, 14, 14, 185, 0,
                button -> {
                    handler.updateAccountsListRightFilter(((StateSwitchingButton<BankerUtils.RIGHT_TYPE>) button).getState());
                },
                ICONS_TEXTURE
        )));
        buttons.add(addDrawableChild(filterIconIdButton = new DynamicButton(
                this.x + 21, this.y + 93, 14, 14, 185, 0,
                Text.literal("TEST"),
                button ->  {
                }
        )));
        buttons.add(addDrawableChild(closeFocusButton = new DynamicButton(
                this.x + 232, this.y + 111, 12, 12, 237, 0,
                Text.literal("TEST"),
                button ->  {
                    removeFocus();
                }
        )));
        buttons.add(addDrawableChild(sortTypeAccountInfoButton = new DynamicButton(
                this.x + 136, this.y + 110, 14, 14, 185, 42,
                Text.literal("TEST"),
                button ->  {
                }
        )));
        buttons.add(addDrawableChild(filterRightTypeAccountInfoButton = new DynamicButton(
                this.x + 149, this.y + 110, 14, 14, 185, 42,
                Text.literal("TEST"),
                button ->  {
                }
        )));
        addDrawableChild(accountCreationNameTextField = new PrettyTextField(    // TODO: Change TextField Aspect
            textRenderer,
            this.x + 136, this.y + 143, 109, 17,
            Text.literal("TEST"),
            Text.translatable("numismatic_utils.banker.create_account_placeholder")
        ));

        leftButtons = new ArrayList<>();
        for (int i = 0; i < NB_LEFT_BUTTONS; i++) {
            int finalI = i;
            DynamicButton leftButton = addDrawableChild(new DynamicButton(
                    this.x + 6, this.y + 110 + i*14, 121, 14, 32, 72,
                    Text.literal("TEST"),
                    button ->  {
                        leftButtonClicked(finalI);
                    }
            ));
            leftButtons.add(leftButton);
            buttons.add(leftButton);
        }
    }

    private void initButtonsGroups() {
        /* ----- Transfert buttons ----- */
        smallFromMainToPouchButtons = new ArrayList<>() {{
            add(goldFromMainToPouchButton);
            add(silverFromMainToPouchButton);
            add(bronzeFromMainToPouchButton);
        }};
        fromMainToPouchButtons = new ArrayList<>() {{
            addAll(smallFromMainToPouchButtons);
            add(fullFromMainToPouchButton);
        }};
        smallFromPouchToMainButtons = new ArrayList<>() {{
            add(goldFromPouchToMainButton);
            add(silverFromPouchToMainButton);
            add(bronzeFromPouchToMainButton);
        }};
        fromPouchToMainButtons = new ArrayList<>() {{
            addAll(smallFromPouchToMainButtons);
            add(fullFromPouchToMainButton);
        }};
        smallFromOtherToMainButtons = new ArrayList<>() {{
            add(goldFromOtherToMainButton);
            add(silverFromOtherToMainButton);
            add(bronzeFromOtherToMainButton);
        }};
        fromOtherToMainButtons = new ArrayList<>() {{
            addAll(smallFromOtherToMainButtons);
            add(fullFromOtherToMainButton);
        }};
        smallFromMainToOtherButtons = new ArrayList<>() {{
            add(goldFromMainToOtherButton);
            add(silverMainToOtherButton);
            add(bronzeMainToOtherButton);
        }};
        fromMainToOtherButtons = new ArrayList<>() {{
            addAll(smallFromMainToOtherButtons);
            add(fullFromMainToOtherButton);
        }};
        smallTransfertButtons = new ArrayList<>() {{
            addAll(smallFromMainToPouchButtons);
            addAll(smallFromPouchToMainButtons);
            addAll(smallFromOtherToMainButtons);
            addAll(smallFromMainToOtherButtons);
        }};
        fullTransfertButtons = new ArrayList<>() {{
            add(fullFromMainToPouchButton);
            add(fullFromPouchToMainButton);
            add(fullFromOtherToMainButton);
            add(fullFromMainToOtherButton);
        }};
        bronzeAndSilverTransfertButtons = new ArrayList<>() {{
            add(silverFromMainToPouchButton);
            add(bronzeFromMainToPouchButton);
            add(silverFromPouchToMainButton);
            add(bronzeFromPouchToMainButton);
            add(silverFromOtherToMainButton);
            add(bronzeFromOtherToMainButton);
            add(silverMainToOtherButton);
            add(bronzeMainToOtherButton);
        }};

        filterRightTypeButton.addState(BankerUtils.RIGHT_TYPE.OWNER, 0, 0, 10, 10, 2, 3);
        filterRightTypeButton.addState(BankerUtils.RIGHT_TYPE.CO_OWNER, 10, 0, 10, 10, 2, 3);
        filterRightTypeButton.addState(BankerUtils.RIGHT_TYPE.BENEFICIARY, 20, 0, 10, 10, 2, 3);
        filterRightTypeButton.addState(BankerUtils.RIGHT_TYPE.CONTRIBUTOR, 30, 0, 10, 10, 2, 3);
        filterRightTypeButton.addState(BankerUtils.RIGHT_TYPE.READ_ONLY, 40, 0, 10, 10, 2, 3);
        filterRightTypeButton.addNullState();
    }

    private void initDisplayContext() {
        switch (currentLeftState) {
            case HOME:
                setHomeState();
                break;

            case ACCOUNTS:
                setAccountsState();
                break;

            case PLAYERS:
                setPlayersState();
                break;
        }
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        context.drawTexture(GUI_TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        updateFocus();
        requestData();
        updateTransferFactor();
        updateLeftButtons(false);
        updateKeysPressed();
        updateTransfertButtonsActivity();
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
        drawBalances(context, mouseX, mouseY);
        drawAdditionalTexture(context);
        drawRelevantContext(context);
        drawLeftTitle(context);
        writeOnLeftButtons(context);
        drawFocusState(context);
    }

    private void drawBalances(DrawContext context, int mouseX, int mouseY) {
        AccountData mainAccount = handler.getMainAccount();
        AccountData focusedAccount = handler.getFocusedAccount();

        // Pouch balance: always displayed
        drawBalance(context, 5, mouseX, mouseY, Text.translatable("numismatic_utils.banker.pouch"), "numismatic_utils.tooltip.current_pouch_balance", playerBalance);

        // Personal account balance: always displayed
        drawBalance(context, 96, mouseX, mouseY, Text.translatable("numismatic_utils.banker.main_account"), "numismatic_utils.tooltip.current_main_account_balance", mainAccount.getBalance());

        // Shared account balance: only displayed if the player has selected a shared account
        if (currentLeftState == LeftState.ACCOUNTS && handler.hasFocusedAccount()) {
            drawBalance(context, 187, mouseX, mouseY, Text.literal(focusedAccount.getName()), "numismatic_utils.tooltip.current_shared_account_balance", focusedAccount.getBalance());
        }
    }

    private void drawBalance(DrawContext context, int horizontalOffset, int mouseX, int mouseY, Text Name, String tooltipTranslationKey, long value) {
        int textX = min(
            this.x + horizontalOffset + (63 - textRenderer.getWidth(Name)) / 2, // Centered
            this.x + this.backgroundWidth - 3 - textRenderer.getWidth(Name)               // Right aligned if text goes out
        );
        context.drawText(this.textRenderer, Name, textX, this.y + 5, 0x17202A, false);
        NumismaticDraw.renderBalanceVertical(
            context,
            this.textRenderer,
            this.x + horizontalOffset,
            this.y + 14,
            value,
            0xFFFFFF,
            true,
                -6,
            false
        );

        if (mouseX >= this.x + horizontalOffset && mouseX <= this.x + horizontalOffset + 63 && mouseY >= this.y + 14 && mouseY <= this.y + 50) {
            context.drawTooltip(this.textRenderer, Text.translatable(tooltipTranslationKey), mouseX, mouseY);
        }
    }


    private void drawRelevantContext(DrawContext context) {
        switch (currentLeftState) {
            case LeftState.HOME:
                drawHomeRight(context);
                break;

            case LeftState.ACCOUNTS:
                drawAccountsRight(context);
                break;

            case LeftState.PLAYERS:
                drawPlayersRight(context);
        }
    }

    private void drawHomeRight(DrawContext context) {

    }

    private void drawAccountsRight(DrawContext context) {
        AccountData focusedAccount = handler.getFocusedAccount();

        // Draw lines
        for (int i = 0; i < 7; i++) {
            DrawTools.fillGradientHorizontal(context, this.x + 136, this.y + 124 + i * 18, this.x + 190, this.y + 125 + i * 18, 0xFFDAC6A9, 0xFF281B14);
            context.fill(this.x + 190, this.y + 124 + i * 18, this.x + 191, this.y + 125 + i * 18, 0xFF281B14);
            DrawTools.fillGradientHorizontal(context, this.x + 191, this.y + 124 + i * 18, this.x + 245, this.y + 125 + i * 18, 0xFF281B14, 0xFFDAC6A9);
        }

        if (handler.hasFocusedAccount()) {
            // Draw header
            int iconId = 0;
            int padding = 0;
            if (iconId > 0) {
                padding = 6;
                //drawCustomIcon(context, this.x + 136 + ()/2, this.y + 110, handler.accountsRights.get(selectedAccountId));
            }
            drawRightTitle(context, Text.literal(focusedAccount.getName()));
            DrawTools.drawRightTypeIcon(context, this.x + 136 - padding + (109 - 10) / 2, this.y + 113, focusedAccount.getRight());

            // Draw info
            if (stateCoupleMap.get(LeftState.ACCOUNTS) == RightState.INFO) {

                for (int i = 0; i < focusedAccount.getNbParticipantsDisplayed(); i++) {
                    ParticipantData participant = focusedAccount.getParticipantAtIndex(i);
                    PlayerData playerData = participant.getPlayerData();
                    DrawTools.fillGradientHorizontal(context, this.x + 137, this.y + 124 + i * 18, this.x + 191, this.y + 125 + i * 18, 0xFFDAC6A9, 0xFF281B14);
                    DrawTools.fillGradientHorizontal(context, this.x + 191, this.y + 124 + i * 18, this.x + 245, this.y + 125 + i * 18, 0xFF281B14, 0xFFDAC6A9);
                    DrawTools.drawRightTypeIcon(context, this.x + 138, this.y + 129 + i * 18, participant.getRight());
                    context.drawText(this.textRenderer, playerData.getName(), this.x + 150, this.y + 126 + i * 18, 0x17202A, false);
                    long relativeBalance = participant.getRelativeBalance();
                    int color = relativeBalance >= 0 ? 0xFF3F48CC : 0xFF88001B;
                    relativeBalance = max(relativeBalance, -relativeBalance);
                    NumismaticUtils.CoinsTuple coins = NumismaticUtils.convertCostToCoins(relativeBalance);
                    NumismaticDraw.renderMinimalistCoinWithTextAside(context, textRenderer, this.x + 146, this.y + 129 + i * 18, NumismaticOverhaulItems.GOLD_COIN, coins.goldCoins, color,false);
                    NumismaticDraw.renderMinimalistCoinWithTextAside(context, textRenderer, this.x + 200, this.y + 129 + i * 18, NumismaticOverhaulItems.SILVER_COIN, coins.silverCoins, color,false);
                    NumismaticDraw.renderMinimalistCoinWithTextAside(context, textRenderer, this.x + 220, this.y + 129 + i * 18, NumismaticOverhaulItems.BRONZE_COIN, coins.bronzeCoins, color,false);
                    //context.drawText(this.textRenderer, Long.toString(handler.getAccountInfoRelativeBalanceAt(handler.getSelectedAccountId(), i)), this.x + 138, this.y + 134 + i * 18, color, false);
                }
            } else {
                // TODO: Settings on the account like changing the icon, changing the name, inviting newplayers, changing players roles...
            }
        } else {
            if (stateCoupleMap.get(LeftState.ACCOUNTS) == RightState.ACCOUNT_CREATION) {
                // Account name text field
                drawRightTitle(context, Text.translatable("numismatic_utils.banker.account_creation"));
                Text textFieldInfo = Text.translatable("numismatic_utils.banker.account_name_text_field");
                context.drawText(this.textRenderer, textFieldInfo, this.x + 136 + (109 - textRenderer.getWidth(textFieldInfo)) / 2, this.y + 129, 0x17202A, false);
            }
        }
    }

    private void drawPlayersRight(DrawContext context) {
        if (selectedPlayerId != null) {
            Text playerName = Text.literal(handler.getPlayerName(selectedPlayerId));
            context.drawText(this.textRenderer, playerName, this.x + 142 + (97 - textRenderer.getWidth(playerName))/2, this.y + 93, 0x17202A, false);
        }
    }

    private void writeOnLeftButtons(DrawContext context) {
        for (int i = 0; i < NB_LEFT_BUTTONS; i++) {
            if (leftButtons.get(i).visible) {
                writeOnLeftButton(context, i);
            }
        }
    }

    private void drawFocusState(DrawContext context) {
        switch (currentLeftState) {
            case HOME:
                drawHomeRight(context);
                break;

            case ACCOUNTS:
                drawAccountsRight(context);
                break;

            case PLAYERS:
                drawPlayersRight(context);
        }
    }

    private void drawLeftTitle(DrawContext context) {
        Text title = switch(currentLeftState) {
            case HOME -> Text.translatable("numismatic_utils.banker.home");
            case ACCOUNTS -> Text.translatable("numismatic_utils.banker.accounts");
            case PLAYERS -> Text.translatable("numismatic_utils.banker.players");
        };
        context.drawText(this.textRenderer, title, this.x + 43 + (83-textRenderer.getWidth(title))/2, this.y + 93, 0x563113, false);
    }

    private void drawRightTitle(DrawContext context, Text title) {
        drawRightTitle(context, title, 0x17202A);
    }

    private void drawRightTitle(DrawContext context, Text title, int color) {
        context.drawText(this.textRenderer, title, this.x + 142 + (97 - textRenderer.getWidth(title)) / 2, this.y + 93, color, false);
    }

    private int computeNbLeftElements() {
        return switch (currentLeftState) {
            case HOME -> 0;
            case ACCOUNTS -> handler.getNbAccountsDisplayed();
            case PLAYERS -> handler.getOrderedPlayersListSize();
        };
    }

    private void writeOnLeftButton(DrawContext context, int i) {
        switch (currentLeftState) {
            case HOME:
                // do something
                break;

            case ACCOUNTS:
                writeOnAccountsLeftButtons(context, i);
                break;

            case PLAYERS:
                writeOnPlayersLeftButtons(context, i);
                break;
        }
    }

    private void writeOnAccountsLeftButtons(DrawContext context, int i) {
        AccountData account = handler.getAccountAtIndex(i);
        DrawTools.drawRightTypeIcon(context, this.x + 8, this.y + 113 + i*14, account.getRight());
        context.drawText(this.textRenderer, account.getName(), this.x + 19, this.y + 114 + i*14, 0x17202A, false);
    }

    private void writeOnPlayersLeftButtons(DrawContext context, int i) {
        UUID playerId = handler.getPlayerAt(i);
        //drawRightTypeIcon(context, this.x + 8, this.y + 113 + i*14, handler.playersRights.get(playerId));
        // TODO: Draw ban or friend (i.e., whitelist) icon
        context.drawText(this.textRenderer, handler.getPlayerName(playerId), this.x + 19, this.y + 114 + i*14, 0x17202A, false);
    }

    /*private void drawAccountInfo(DrawContext context) {
        context.drawText(this.textRenderer, handler.accountsNames.get(handler.getFocusedAccount()), this.x + 148, this.y + 88, 0x17202A, true);
    }*/

    private void drawAccountCreationForm(DrawContext context) {

    }

    private void drawAdditionalTexture(DrawContext context) {

    }

    private void updateMainToPouchButtons() {
        AccountData mainAccount = handler.getMainAccount();

        if (mainAccount.getBalance() > 0) {
            fromMainToPouchButtons.forEach(button -> {button.active = true;});
        } else {
            fromMainToPouchButtons.forEach(button -> {button.active = false;});
        }
    }

    private void updatePouchToMainButtons() {
        if (playerBalance > 0) {
            fromPouchToMainButtons.forEach(button -> {button.active = true;});
        } else {
            fromPouchToMainButtons.forEach(button -> {button.active = false;});
        }
    }

    /*private void updateOtherToMainButtons() {
        if (handler.getAccountBalance(handler.getFocusedAccount()) > 0) {
            fromOtherToMainButtons.forEach(button -> {button.active = true;});
        } else {
            fromOtherToMainButtons.forEach(button -> {button.active = false;});
        }
    }

    private void updateMainToOtherButtons() {
        if (handler.getMainAccountBalance() > 0) {
            fromMainToOtherButtons.forEach(button -> {button.active = true;});
        } else {
            fromMainToOtherButtons.forEach(button -> {button.active = false;});
        }
    }*/

    private void updateKeysPressed() {
        if (HandledScreen.hasAltDown() != altKeyPressed) {
            updateAltKeyPressed();
        } else if (HandledScreen.hasShiftDown() != shiftKeyPressed) {
            updateShiftKeyPressed();
        }
    }

    private void updateAltKeyPressed() {
        altKeyPressed = HandledScreen.hasAltDown();
        if (altKeyPressed) {
            smallFromMainToPouchButtons.forEach(button -> {button.visible = false;});
            fullFromMainToPouchButton.visible = true;
            smallFromPouchToMainButtons.forEach(button -> {button.visible = false;});
            fullFromPouchToMainButton.visible = true;
            if (shouldDisplayOtherToMainTransfertButtons()) {
                smallFromOtherToMainButtons.forEach(button -> {button.visible = false;});
                fullFromOtherToMainButton.visible = true;
            } else {
                fullFromOtherToMainButton.visible = false;
            }
            if (shouldDisplayMainToOtherTransfertButtons()) {
                smallFromMainToOtherButtons.forEach(button -> {button.visible = false;});
                fullFromMainToOtherButton.visible = true;
            } else {
                fullFromMainToOtherButton.visible = false;
            }
        } else {
            fromMainToPouchButtons.forEach(button -> {button.visible = true;});
            fullFromMainToPouchButton.visible = false;
            fromPouchToMainButtons.forEach(button -> {button.visible = true;});
            fullFromPouchToMainButton.visible = false;
            if (shouldDisplayOtherToMainTransfertButtons()) {
                fromOtherToMainButtons.forEach(button -> {button.visible = true;});
            }
            fullFromOtherToMainButton.visible = false;
            if (shouldDisplayMainToOtherTransfertButtons()) {
                fromMainToOtherButtons.forEach(button -> {button.visible = true;});
            }
            fullFromMainToOtherButton.visible = false;
        }
    }

    private void updateShiftKeyPressed() {
        shiftKeyPressed = HandledScreen.hasShiftDown();
        if (shiftKeyPressed) {
            bronzeAndSilverTransfertButtons.forEach(button -> {button.active = false;});
        } else {
            AccountData mainAccount = handler.getMainAccount();
            AccountData focusedAccount = handler.getFocusedAccount();
            if (playerBalance > 0) {
                bronzeFromPouchToMainButton.active = true;
                silverFromPouchToMainButton.active = true;
            } else {
                bronzeFromPouchToMainButton.active = false;
                silverFromPouchToMainButton.active = false;
            }

            if (mainAccount.getBalance() > 0) {
                bronzeFromMainToPouchButton.active = true;
                silverFromMainToPouchButton.active = true;
            } else {
                bronzeFromMainToPouchButton.active = false;
                silverFromMainToPouchButton.active = false;
            }

            if (shouldDisplayOtherToMainTransfertButtons()) {
                if (focusedAccount.getBalance() > 0) {
                    bronzeFromOtherToMainButton.active = true;
                    silverFromOtherToMainButton.active = true;
                } else {
                    bronzeFromOtherToMainButton.active = false;
                    silverFromOtherToMainButton.active = false;
                }
            }

            if (shouldDisplayMainToOtherTransfertButtons()) {
                if (focusedAccount.getBalance() > 0) {
                    bronzeMainToOtherButton.active = true;
                    silverMainToOtherButton.active = true;
                } else {
                    bronzeMainToOtherButton.active = false;
                    silverMainToOtherButton.active = false;
                }
            }
        }
    }

    private void updateLeftButtons(boolean forced) {
        boolean shouldUpdate = forced || switch(currentLeftState) {
            case LeftState.HOME -> false;
            case LeftState.ACCOUNTS -> handler.wasAccountsListUpdated();
            case LeftState.PLAYERS -> handler.isPlayersListUpdated();
        };
        if (shouldUpdate) {
            updateLeftButtonsVisibility();
            updateLeftButtonsActivity();
        }
    }

    private void updateLeftButtonsVisibility() {
        int nbButtonsDisplayed = min(NB_LEFT_BUTTONS, computeNbLeftElements());
        for (int i = 0; i < NB_LEFT_BUTTONS; i++) {
            DynamicButton leftButton = leftButtons.get(i);
            if (i >= nbButtonsDisplayed) {
                leftButton.visible = false;
            } else {
                leftButton.visible = true;
            }
        }
    }

    private void updateLeftButtonsActivity() {
        UUID selectedId = switch (currentLeftState) {
            case LeftState.HOME -> null;
            case LeftState.ACCOUNTS -> handler.hasFocusedAccount() ? handler.getFocusedAccount().getId() : null;
            case LeftState.PLAYERS -> selectedPlayerId;
        };

        System.out.println("selectedId: " + selectedId);
        for (int i = 0; i < NB_LEFT_BUTTONS; i++) {
            DynamicButton leftButton = leftButtons.get(i);
            if (!leftButton.visible)
                break;

            System.out.println("idAtLeftButton(" + i + "): " + getIdAtLeftButton(i));
            leftButton.active = !Objects.equals(getIdAtLeftButton(i), selectedId);
        }
    }

    private UUID getIdAtLeftButton(int i) {
        return switch (currentLeftState) {
            case LeftState.HOME -> null;
            case LeftState.ACCOUNTS -> handler.getAccountAtIndex(i).getId();
            case LeftState.PLAYERS -> handler.getPlayerAt(i);
        };
    }

    private boolean shouldDisplayOtherToMainTransfertButtons() {
        if (!handler.hasFocusedAccount())
            return false;

        BankerUtils.RIGHT_TYPE rightType = handler.getFocusedAccount().getRight();
        return currentLeftState == LeftState.ACCOUNTS
                && handler.hasFocusedAccount()
                && (
                        rightType == BankerUtils.RIGHT_TYPE.BENEFICIARY
                    ||  rightType == BankerUtils.RIGHT_TYPE.OWNER
                );
    }

    private boolean shouldDisplayMainToOtherTransfertButtons() {
        if (!handler.hasFocusedAccount())
            return false;

        BankerUtils.RIGHT_TYPE rightType = handler.getFocusedAccount().getRight();
        return currentLeftState == LeftState.ACCOUNTS
                && handler.hasFocusedAccount()
                && (
                        rightType == BankerUtils.RIGHT_TYPE.CONTRIBUTOR
                    ||  rightType == BankerUtils.RIGHT_TYPE.BENEFICIARY
                    ||  rightType == BankerUtils.RIGHT_TYPE.OWNER
                );
    }

    private boolean shouldDisplayOtherAccountBalance() {
        return currentLeftState == LeftState.ACCOUNTS && handler.hasFocusedAccount();
    }

    private void updateTransferFactor() {
        if (HandledScreen.hasShiftDown()) {
            if (HandledScreen.hasControlDown()) {
                transfertFactor = 1000;
            } else {
                transfertFactor = 100;
            }
        } else {
            if (HandledScreen.hasControlDown()) {
                transfertFactor = 10;
            } else {
                transfertFactor = 1;
            }
        }
    }


    private void requestTransfertFromPouchToMain(long value) {
        requestTransfert(Optional.empty(), handler.getMainAccount().getId(), value);
    }

    private void requestTransfertFromMainToOther(long value) {
        if (!handler.hasFocusedAccount())
            return;

        AccountData focusedAccount = handler.getFocusedAccount();
        focusedAccount.updateRelativeBalance_fromClient(value);
        requestTransfert(Optional.of(handler.getMainAccount().getId()), handler.getFocusedAccount().getId(), value);
    }

    private void requestTransfert(Optional<UUID> origin, UUID destination, long value) {
        if (destination == null)
            return;

        value *= transfertFactor;
        RequestTransfertPayload requestPayload = new RequestTransfertPayload(handler.syncId, origin, destination, value);
        ClientPlayNetworking.send(requestPayload);
    }

    private void requestData() {
        AccountData mainAccount = handler.getMainAccount();
        AccountData focusedAccount = handler.getFocusedAccount();

        // Player's pouch balance (always displayed)
        long newPlayerBalance = ModComponents.CURRENCY.get(client.player).getValue();
        playerBalanceUpdated = playerBalance != newPlayerBalance;
        playerBalance = newPlayerBalance;

        // Player's main account balance (always displayed)
        if (mainAccount.shouldUpdateBalance()) {
            System.out.println("Updating account balance for main account " + mainAccount.getId());
            RequestAccountBalancePayload requestPayload = new RequestAccountBalancePayload(handler.syncId, mainAccount.getId());
            ClientPlayNetworking.send(requestPayload);
        }

        // Player's main account balance (always displayed)
        if (shouldDisplayOtherAccountBalance() && focusedAccount.shouldUpdateBalance()) {
            System.out.println("Updating account balance for other account " + focusedAccount.getId());
            RequestAccountBalancePayload requestPayload = new RequestAccountBalancePayload(handler.syncId, focusedAccount.getId());
            ClientPlayNetworking.send(requestPayload);
        }

        // Other account balance (displayed only on accounts tab)
        if (currentLeftState == LeftState.ACCOUNTS && handler.shouldUpdateAccountsList()) {
            System.out.println("Updating accounts list");
            RequestAccountsListPayload requestPayload = new RequestAccountsListPayload(handler.syncId, filterRightType, filterIconId);
            ClientPlayNetworking.send(requestPayload);
        }

        // Player's accounts list (displayed only on players tab)
        if (currentLeftState == LeftState.PLAYERS && handler.shouldUpdatePlayersList()) {
            System.out.println("Updating players list");
            RequestPlayersListPayload requestPayload = new RequestPlayersListPayload(handler.syncId);
            ClientPlayNetworking.send(requestPayload);
        }

        // Account info list (displayed only when focused on an account)
        if (currentLeftState == LeftState.ACCOUNTS && stateCoupleMap.get(LeftState.ACCOUNTS) == RightState.INFO && handler.hasFocusedAccount() && focusedAccount.shouldUpdateInfo()) {
            System.out.println("Updating account info for " + focusedAccount.getId());
            RequestAccountInfoPayload requestPayload = new RequestAccountInfoPayload(handler.syncId, focusedAccount.getId());
            ClientPlayNetworking.send(requestPayload);
        }
    }

    private void updateFocus() {
        if (currentLeftState == LeftState.ACCOUNTS && !stateCoupleMap.get(LeftState.ACCOUNTS).equals(RightState.ACCOUNT_CREATION)) {
            if (handler.wasFocusedAccountUpdated()) {
                if (handler.hasFocusedAccount()) {
                    setAccountsState();
                } else {
                    removeFocus();
                }
            }
        }
    }

    private void leftButtonClicked(int i) {
        switch (currentLeftState){
            case LeftState.HOME:
                break;

            case LeftState.ACCOUNTS:
                handler.selectAccountAtIndex(i);
                break;

            case LeftState.PLAYERS:
                selectedPlayerId = handler.getPlayerAt(i);
                break;
        }
        setFocusState();
    }

    private void setFocusState() {
        updateLeftButtonsActivity();
        closeFocusButton.visible = true;

        switch (currentLeftState){
            case HOME:
                setEventFocusState();
                break;

            case ACCOUNTS:
                setAccountFocusState();
                break;

            case PLAYERS:
                setPlayerFocusState();
                break;
        }
    }

    private void setEventFocusState() {
    }

    private void setAccountFocusState() {
        updateMainOtherTransfertButtonsVisibility();
        updateTransfertButtonsActivity(true);
        addAccountButton.visible = false;
        sortTypeAccountInfoButton.visible = stateCoupleMap.get(LeftState.ACCOUNTS) == RightState.INFO;
        filterRightTypeAccountInfoButton.visible = stateCoupleMap.get(LeftState.ACCOUNTS) == RightState.INFO;
        accountCreationNameTextField.visible = false;
    }

    private void setPlayerFocusState() {

    }

    private void updateMainOtherTransfertButtonsVisibility() {
        if (HandledScreen.hasAltDown()) {
            fullFromOtherToMainButton.visible = shouldDisplayOtherToMainTransfertButtons();
        } else {
            boolean shouldDisplay = shouldDisplayOtherToMainTransfertButtons();
            smallFromOtherToMainButtons.forEach(button -> {button.visible = shouldDisplay;});
        }

        if (HandledScreen.hasAltDown()) {
            fullFromMainToOtherButton.visible = shouldDisplayOtherToMainTransfertButtons();
        } else {
            boolean shouldDisplay = shouldDisplayOtherToMainTransfertButtons();
            smallFromMainToOtherButtons.forEach(button -> {button.visible = shouldDisplay;});
        }
    }

    private void updateTransfertButtonsActivity() {
        updateTransfertButtonsActivity(false);
    }
    private void updateTransfertButtonsActivity(boolean forced) {
        AccountData mainAccount = handler.getMainAccount();
        AccountData focusedAccount = handler.getFocusedAccount();

        if (forced || playerBalanceUpdated) {
            if (playerBalance > 0) {
                goldFromPouchToMainButton.active = true;
                silverFromPouchToMainButton.active = !shiftKeyPressed;
                bronzeFromPouchToMainButton.active = !shiftKeyPressed;
                fullFromPouchToMainButton.active = true;
            } else {
                fromPouchToMainButtons.forEach(button -> {button.active = false;});
            }
        }

        if (forced || mainAccount.wasBalanceUpdated()) {
            System.out.println("Updating buttons from main account to pouch");
            boolean mainIsNotEmpty = mainAccount.getBalance() > 0;
            if (mainIsNotEmpty) {
                goldFromMainToPouchButton.active = true;
                silverFromMainToPouchButton.active = !shiftKeyPressed;
                bronzeFromMainToPouchButton.active = !shiftKeyPressed;
                fullFromMainToPouchButton.active = true;
            } else {
                fromMainToPouchButtons.forEach(button -> {button.active = false;});
            }
            if (shouldDisplayMainToOtherTransfertButtons()) {
                if (mainIsNotEmpty) {
                    goldFromMainToOtherButton.active = true;
                    silverMainToOtherButton.active = !shiftKeyPressed;
                    bronzeMainToOtherButton.active = !shiftKeyPressed;
                    fullFromMainToOtherButton.active = true;
                } else {
                    fromMainToOtherButtons.forEach(button -> {button.active = false;});
                }
            }
        }

        if (shouldDisplayOtherToMainTransfertButtons() && (forced || focusedAccount.wasBalanceUpdated())) {
            if (focusedAccount.getBalance() > 0) {
                goldFromOtherToMainButton.active = true;
                silverFromOtherToMainButton.active = !shiftKeyPressed;
                bronzeFromOtherToMainButton.active = !shiftKeyPressed;
                fullFromOtherToMainButton.active = true;
            } else {
                fromOtherToMainButtons.forEach(button -> {button.active = false;});
            }
        }
    }

    private void setHomeState() {
        System.out.println("set home context");
        // Update context first
        currentLeftState = LeftState.HOME;

        // Text fields visibility
        accountCreationNameTextField.visible = false;

        // Buttons visibility
        displayInfoButton.visible = true;
        displayTransfertButton.visible = false;
        addAccountButton.visible = false;
        confirmAccountCreationButton.visible = false;
        closeFocusButton.visible = false; // TODO: Switch to false if no event is selected
        sortTypeAccountInfoButton.visible = false;
        filterRightTypeAccountInfoButton.visible = false;

        // Transfert buttons visibility
        updateMainOtherTransfertButtonsVisibility();

        // Buttons activation
        homeButton.active = false;
        displayAccountsButton.active = true;
        displayPlayersButton.active = true;
        if (stateCoupleMap.get(LeftState.HOME) == RightState.INFO) {
            displayInfoButton.active = false;
            displaySettingsButton.active = true;
        } else {
            displayInfoButton.active = true;
            displaySettingsButton.active = false;
        }

        // Left buttons
        updateLeftButtons(true);
    }

    private void setAccountsState() {
        System.out.println("set accounts context");
        // Update context first
        currentLeftState = LeftState.ACCOUNTS;

        // Update buttons
        if (shouldDisplayOtherAccountBalance() && handler.shouldUpdateAccountsList())  {
            RequestAccountsListPayload requestPayload = new RequestAccountsListPayload(handler.syncId, filterRightType, filterIconId);
            ClientPlayNetworking.send(requestPayload);
        }

        // Transfert buttons
        updateMainOtherTransfertButtonsVisibility();

        // Buttons visibility
        displayInfoButton.visible = true;
        displayTransfertButton.visible = false;
        closeFocusButton.visible = (handler.hasFocusedAccount());

        // Buttons activation and visibility
        displayAccountsButton.active = false;
        homeButton.active = true;
        displayPlayersButton.active = true;
        if (stateCoupleMap.get(LeftState.ACCOUNTS) == RightState.INFO) {
            displayInfoButton.active = false;
            displaySettingsButton.active = true;
            addAccountButton.visible = false;
            confirmAccountCreationButton.visible = false;
            sortTypeAccountInfoButton.visible = handler.hasFocusedAccount();
            filterRightTypeAccountInfoButton.visible = handler.hasFocusedAccount();
            accountCreationNameTextField.visible = false;
        } else {
            displayInfoButton.active = true;
            displaySettingsButton.active = false;
            sortTypeAccountInfoButton.visible = false;
            filterRightTypeAccountInfoButton.visible = false;
            if (stateCoupleMap.get(LeftState.ACCOUNTS) == RightState.ACCOUNT_CREATION) {
                addAccountButton.visible = false;
                confirmAccountCreationButton.visible = true;
                accountCreationNameTextField.visible = true;
            } else {
                addAccountButton.visible = (!handler.hasFocusedAccount());
                confirmAccountCreationButton.visible = false;
                accountCreationNameTextField.visible = false;
            }
        }

        // Implement logic to remove visibility for all irrelevant buttons
        updateLeftButtons(true);
    }

    private void setPlayersState() {
        System.out.println("set players context");
        // Update context first
        currentLeftState = LeftState.PLAYERS;

        // Text fields visibility
        accountCreationNameTextField.visible = false;

        // Buttons visibility
        addAccountButton.visible = false;
        displayInfoButton.visible = false;
        displayTransfertButton.visible = true;
        closeFocusButton.visible = selectedPlayerId != null;
        sortTypeAccountInfoButton.visible = false;
        filterRightTypeAccountInfoButton.visible = false;

        // Buttons activation
        displayPlayersButton.active = false;
        homeButton.active = true;
        displayAccountsButton.active = true;
        confirmAccountCreationButton.visible = false;
        if (stateCoupleMap.get(LeftState.PLAYERS) == RightState.TRANSFERT) {
            displayTransfertButton.active = false;
            displaySettingsButton.active = true;
        } else {
            displayTransfertButton.active = true;
            displaySettingsButton.active = false;
        }

        // Transfert buttons
        updateMainOtherTransfertButtonsVisibility();

        updateLeftButtons(true);
    }

    private void setInfoState() {
        switch (currentLeftState) {
            case HOME:
                stateCoupleMap.put(LeftState.HOME, RightState.INFO);
                break;

            case ACCOUNTS:
                stateCoupleMap.put(LeftState.ACCOUNTS, RightState.INFO);
                addAccountButton.visible = false;
                break;
        }

        // Buttons activation
        displayInfoButton.active = false;
        displaySettingsButton.active = true;
    }

    private void setTransfertState() {
        stateCoupleMap.put(LeftState.PLAYERS, RightState.TRANSFERT);

        // Buttons activation
        displayTransfertButton.active = false;
        displaySettingsButton.active = true;
    }

    private void setSettingsState() {
        switch (currentLeftState) {
            case HOME:
                stateCoupleMap.put(LeftState.HOME, RightState.SETTINGS);
                break;

            case ACCOUNTS:
                stateCoupleMap.put(LeftState.ACCOUNTS, RightState.SETTINGS);
                addAccountButton.visible = (!handler.hasFocusedAccount());
                break;

            case PLAYERS:
                stateCoupleMap.put(LeftState.PLAYERS, RightState.SETTINGS);
                break;
        }

        // Buttons activation
        displaySettingsButton.active = false;
        displayInfoButton.active = true;
        displayTransfertButton.active = true;
    }

    private void setAccountCreationState() {
        stateCoupleMap.put(LeftState.ACCOUNTS, RightState.ACCOUNT_CREATION);
        accountCreationNameTextField.visible = true;
        addAccountButton.active = false;
        closeFocusButton.visible = true;
        confirmAccountCreationButton.visible = true;
    }

    private void removeFocus() {
        switch (currentLeftState)  {
            case HOME -> removeEventFocus();
            case ACCOUNTS -> removeAccountFocus();
            case PLAYERS -> removePlayerFocus();
        }
        updateLeftButtons(true);
        closeFocusButton.visible = false;
    }

    private void removeEventFocus() {

    }

    private void removeAccountFocus() {
        if (stateCoupleMap.get(LeftState.ACCOUNTS) == RightState.ACCOUNT_CREATION) {
            stateCoupleMap.put(LeftState.ACCOUNTS, RightState.SETTINGS);
        }
        addAccountButton.active = true;
        handler.resetFocusedAccount();
        fromOtherToMainButtons.forEach(button -> button.visible = false);
        fromMainToOtherButtons.forEach(button -> button.visible = false);
        sortTypeAccountInfoButton.visible = false;
        filterRightTypeAccountInfoButton.visible = false;
        addAccountButton.visible = stateCoupleMap.get(LeftState.ACCOUNTS) == RightState.SETTINGS;
        accountCreationNameTextField.visible = false;
        confirmAccountCreationButton.visible = false;
    }

    private void removePlayerFocus() {
        selectedPlayerId = null;
    }
}

package galysso.codicraft.numismaticutils.screen;

import com.glisco.numismaticoverhaul.ModComponents;
import com.mojang.blaze3d.systems.RenderSystem;
import galysso.codicraft.numismaticutils.NumismaticUtilsMain;
import galysso.codicraft.numismaticutils.network.requests.*;
import galysso.codicraft.numismaticutils.screen.buttons.DynamicButton;
import galysso.codicraft.numismaticutils.screen.helper.DrawTools;
import galysso.codicraft.numismaticutils.utils.NumismaticDraw;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import galysso.codicraft.numismaticutils.utils.BankerUtils;

import java.util.*;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class BankerScreen extends HandledScreen<BankerScreenHandler> {
    @Override protected void drawForeground(DrawContext context, int mouseX, int mouseY) {}

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
    private UUID selectedAccountId = null;
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
    private DynamicButton filterRightTypeButton;
    private DynamicButton filterIconIdButton;
    private DynamicButton closeFocusButton;

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
        buttons = new ArrayList<>();
        buttons.add(addDrawableChild(goldFromMainToPouchButton = new DynamicButton(
                this.x + 70, this.y + 17, 8, 10, 0, 0,
                Text.literal("TEST"),
                button ->  {
                    requestTransfert(Optional.empty(), handler.mainAccountId, -10000L);
                }
        )));
        buttons.add(addDrawableChild(silverFromMainToPouchButton = new DynamicButton(
                this.x + 70, this.y + 28, 8, 10, 0, 0,
                Text.literal("TEST"),
                button ->  {
                    requestTransfert(Optional.empty(), handler.mainAccountId, -100L);
                }
        )));
        buttons.add(addDrawableChild(bronzeFromMainToPouchButton = new DynamicButton(
                this.x + 70, this.y + 39, 8, 10, 0, 0,
                Text.literal("TEST"),
                button ->  {
                    requestTransfert(Optional.empty(), handler.mainAccountId, -1L);
                }
        )));
        buttons.add(addDrawableChild(fullFromMainToPouchButton = new DynamicButton(
                this.x + 70, this.y + 17, 8, 32, 16, 0,
                Text.literal("TEST"),
                button ->  {
                    requestTransfert(Optional.empty(), handler.mainAccountId, -Long.MAX_VALUE);
                }
        )));
        buttons.add(addDrawableChild(goldFromPouchToMainButton = new DynamicButton(
                this.x + 87, this.y + 17, 8, 10, 8, 0,
                Text.literal("TEST"),
                button ->  {
                    requestTransfert(Optional.empty(), handler.mainAccountId, 10000L);
                }
        )));
        buttons.add(addDrawableChild(silverFromPouchToMainButton = new DynamicButton(
                this.x + 87, this.y + 28, 8, 10, 8, 0,
                Text.literal("TEST"),
                button ->  {
                    requestTransfert(Optional.empty(), handler.mainAccountId, 100L);
                }
        )));
        buttons.add(addDrawableChild(bronzeFromPouchToMainButton = new DynamicButton(
                this.x + 87, this.y + 39, 8, 10, 8, 0,
                Text.literal("TEST"),
                button ->  {
                    requestTransfert(Optional.empty(), handler.mainAccountId, 1L);
                }
        )));
        buttons.add(addDrawableChild(fullFromPouchToMainButton = new DynamicButton(
                this.x + 87, this.y + 17, 8, 32, 24, 0,
                Text.literal("TEST"),
                button ->  {
                    requestTransfert(Optional.empty(), handler.mainAccountId, Long.MAX_VALUE);
                }
        )));
        buttons.add(addDrawableChild(goldFromOtherToMainButton = new DynamicButton(
                this.x + 161, this.y + 17, 8, 10, 0, 0,
                Text.literal("TEST"),
                button ->  {
                    requestTransfert(Optional.ofNullable(handler.mainAccountId), selectedAccountId, -10000L);
                }
        )));
        buttons.add(addDrawableChild(silverFromOtherToMainButton = new DynamicButton(
                this.x + 161, this.y + 28, 8, 10, 0, 0,
                Text.literal("TEST"),
                button ->  {
                    requestTransfert(Optional.ofNullable(handler.mainAccountId), selectedAccountId, -100L);
                }
        )));
        buttons.add(addDrawableChild(bronzeFromOtherToMainButton = new DynamicButton(
                this.x + 161, this.y + 39, 8, 10, 0, 0,
                Text.literal("TEST"),
                button ->  {
                    requestTransfert(Optional.ofNullable(handler.mainAccountId), selectedAccountId, -1L);
                }
        )));
        buttons.add(addDrawableChild(fullFromOtherToMainButton = new DynamicButton(
                this.x + 161, this.y + 17, 8, 32, 16, 0,
                Text.literal("TEST"),
                button ->  {
                    requestTransfert(Optional.ofNullable(handler.mainAccountId), selectedAccountId, -Long.MAX_VALUE);
                }
        )));
        buttons.add(addDrawableChild(goldFromMainToOtherButton = new DynamicButton(
                this.x + 178, this.y + 17, 8, 10, 8, 0,
                Text.literal("TEST"),
                button ->  {
                    requestTransfert(Optional.ofNullable(handler.mainAccountId), selectedAccountId, 10000L);
                }
        )));
        buttons.add(addDrawableChild(silverMainToOtherButton = new DynamicButton(
                this.x + 178, this.y + 28, 8, 10, 8, 0,
                Text.literal("TEST"),
                button ->  {
                    requestTransfert(Optional.ofNullable(handler.mainAccountId), selectedAccountId, 100L);
                }
        )));
        buttons.add(addDrawableChild(bronzeMainToOtherButton = new DynamicButton(
                this.x + 178, this.y + 39, 8, 10, 8, 0,
                Text.literal("TEST"),
                button ->  {
                    requestTransfert(Optional.ofNullable(handler.mainAccountId), selectedAccountId, 1L);
                }
        )));
        buttons.add(addDrawableChild(fullFromMainToOtherButton = new DynamicButton(
                this.x + 178, this.y + 17, 8, 32, 24, 0,
                Text.literal("TEST"),
                button ->  {
                    requestTransfert(Optional.ofNullable(handler.mainAccountId), selectedAccountId, Long.MAX_VALUE);
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
                this.x + 135, this.y + 87, 9, 9, 176, 0,
                Text.literal("TEST"),
                button ->  {
                }
        )));
        buttons.add(addDrawableChild(confirmAccountCreationButton = new DynamicButton(
                this.x + 216, this.y + 120, 13, 11, 211, 0,
                Text.literal("TEST"),
                button ->  {
                    /*stateCoupleMap.put(LeftState.ACCOUNTS, RightState.INFO);
                    RequestAccountCreationPayload requestPayload2 = new RequestAccountCreationPayload(handler.syncId, "TOTO");
                    ClientPlayNetworking.send(requestPayload2);*/
                }
        )));
        buttons.add(addDrawableChild(filterRightTypeButton = new DynamicButton(
                this.x + 5, this.y + 93, 14, 14, 185, 0,
                Text.literal("TEST"),
                button ->  {
                }
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
        drawTitle(context);
        writeOnLeftButtons(context);
        drawFocusState(context);
    }

    private void drawBalances(DrawContext context, int mouseX, int mouseY) {
        // Pouch balance: always displayed
        drawBalance(context, 5, mouseX, mouseY, Text.translatable("numismatic_utils.banker.pouch"), "numismatic_utils.tooltip.current_pouch_balance", playerBalance);

        // Personal account balance: always displayed
        drawBalance(context, 96, mouseX, mouseY, Text.translatable("numismatic_utils.banker.main_account"), "numismatic_utils.tooltip.current_main_account_balance", handler.getMainAccountBalance());

        // Shared account balance: only displayed if the player has selected a shared account
        if (currentLeftState == LeftState.ACCOUNTS && selectedAccountId != null) {
            drawBalance(context, 187, mouseX, mouseY, Text.literal(handler.accountsNames.getOrDefault(selectedAccountId, "")), "numismatic_utils.tooltip.current_shared_account_balance", handler.getAccountBalance(selectedAccountId));
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
        if (selectedAccountId != null) {
            int iconId = 0;
            int padding = 0;
            if (iconId > 0) {
                padding = 6;
                //drawCustomIcon(context, this.x + 136 + ()/2, this.y + 110, handler.accountsRights.get(selectedAccountId));
            }
            drawRightTypeIcon(context, this.x + 136 - padding + (109 - 10)/2, this.y + 113, handler.accountsRights.get(selectedAccountId));
            Text accountName = Text.literal(handler.accountsNames.get(selectedAccountId));
            context.drawText(this.textRenderer, accountName, this.x + 142 + (97 - textRenderer.getWidth(accountName))/2, this.y + 93, 0x17202A, false);
            // Bonne version ARGB avec alpha complet
            //context.fillGradient(this.x + 137, this.y + 117, this.x + 191, this.y + 118, 0, 0xFFDAC6A9, 0xFF281B14);
            //context.fillGradient(this.x + 191, this.y + 117, this.x + 245, this.y + 118, 0, 0xFF281B14, 0xFFDAC6A9);
            DrawTools.fillGradientHorizontal(context, this.x + 137, this.y + 124, this.x + 191, this.y + 125, 0xFFDAC6A9, 0xFF281B14);
            DrawTools.fillGradientHorizontal(context, this.x + 191, this.y + 124, this.x + 245, this.y + 125, 0xFF281B14, 0xFFDAC6A9);

            context.drawText(this.textRenderer, "Text fits fine here.", this.x + 138, this.y + 126, 0x17202A, false);
            context.drawText(this.textRenderer, "Text fits fine here.", this.x + 138, this.y + 134, 0x17202A, false);
            for (int i = 1; i < 7; i++) {
                context.drawText(this.textRenderer, "Text fits fine here.", this.x + 138, this.y + 126 + i*18, 0x17202A, false);
                context.drawText(this.textRenderer, "Text fits fine here.", this.x + 138, this.y + 134 + i*18, 0x17202A, false);
                DrawTools.fillGradientHorizontal(context, this.x + 137, this.y + 124 + i*18, this.x + 191, this.y + 125 + i*18, 0xFFDAC6A9, 0xFF281B14);
                DrawTools.fillGradientHorizontal(context, this.x + 191, this.y + 124 + i*18, this.x + 245, this.y + 125 + i*18, 0xFF281B14, 0xFFDAC6A9);
            }


            //context.getMatrices().push();
            //context.getMatrices().translate(0.0, 0.0, 200.0);
            //context.drawHorizontalLine(137, 245, 117, 0xFFFFFFFF);
            //context.fill(137, 115, 245, 119, 0xFFFFFFFF);
            //context.getMatrices().pop();
        }
    }

    private void drawPlayersRight(DrawContext context) {
        //drawLeftButtons(context);
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

    private void drawTitle(DrawContext context) {
        Text title = switch(currentLeftState) {
            case HOME -> Text.translatable("numismatic_utils.banker.home");
            case ACCOUNTS -> Text.translatable("numismatic_utils.banker.accounts");
            case PLAYERS -> Text.translatable("numismatic_utils.banker.players");
        };
        context.drawText(this.textRenderer, title, this.x + 43 + (83-textRenderer.getWidth(title))/2, this.y + 93, 0x563113, false);
    }

    private int computeNbLeftElements() {
        return switch (currentLeftState) {
            case HOME -> 0;
            case ACCOUNTS -> handler.getOrderedAccountsListSize();
            case PLAYERS -> 0;
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
                // do something
                break;
        }
    }

    private void writeOnAccountsLeftButtons(DrawContext context, int i) {
        UUID accountId = handler.getAccountAt(i);
        drawRightTypeIcon(context, this.x + 8, this.y + 113 + i*14, handler.accountsRights.get(accountId));
        context.drawText(this.textRenderer, handler.accountsNames.get(accountId), this.x + 19, this.y + 114 + i*14, 0x17202A, false);
    }

    private void drawRightTypeIcon(DrawContext context, int x, int y, BankerUtils.RIGHT_TYPE rightType) {
        int u = 0;
        switch (rightType) {
            case BankerUtils.RIGHT_TYPE.READ_ONLY -> u = 0;
            case BankerUtils.RIGHT_TYPE.CONTRIBUTOR -> u = 10;
            case BankerUtils.RIGHT_TYPE.BENEFICIARY -> u = 20;
            case BankerUtils.RIGHT_TYPE.OWNER -> u = 30;
        }
        context.drawTexture(ICONS_TEXTURE, x, y, u, 0, 10, 8);
    }

    private void drawAccountInfo(DrawContext context) {
        context.drawText(this.textRenderer, handler.accountsNames.get(selectedAccountId), this.x + 148, this.y + 88, 0x17202A, true);
    }

    private void drawAccountCreationForm(DrawContext context) {

    }

    private void drawAdditionalTexture(DrawContext context) {

    }

    private void updateMainToPouchButtons() {
        if (handler.getMainAccountBalance() > 0) {
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

    private void updateOtherToMainButtons() {
        if (handler.getAccountBalance(selectedAccountId) > 0) {
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
    }

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
            bronzeAndSilverTransfertButtons.forEach(button -> {button.active = true;});
        }
    }

    private void updateLeftButtons(boolean forced) {
        boolean shouldUpdate = forced || switch(currentLeftState) {
            case LeftState.HOME -> false;
            case LeftState.ACCOUNTS -> handler.accountsListUpdated();
            case LeftState.PLAYERS -> false;
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
            case LeftState.ACCOUNTS -> selectedAccountId;
            case LeftState.PLAYERS -> null;
        };

        for (int i = 0; i < NB_LEFT_BUTTONS; i++) {
            System.out.println("I: " + i);
            DynamicButton leftButton = leftButtons.get(i);
            if (!leftButton.visible)
                break;

            leftButton.active = !Objects.equals(getIdAtLeftButton(i), selectedId);
        }
    }

    private UUID getIdAtLeftButton(int i) {
        return switch (currentLeftState) {
            case LeftState.HOME -> null;
            case LeftState.ACCOUNTS -> handler.getAccountAt(i);
            case LeftState.PLAYERS -> null;
        };
    }

    private boolean shouldDisplayOtherToMainTransfertButtons() {
        BankerUtils.RIGHT_TYPE rightType = handler.accountsRights.getOrDefault(selectedAccountId, BankerUtils.RIGHT_TYPE.READ_ONLY);
        return currentLeftState == LeftState.ACCOUNTS
                && selectedAccountId != null
                && (
                        rightType == BankerUtils.RIGHT_TYPE.BENEFICIARY
                    ||  rightType == BankerUtils.RIGHT_TYPE.OWNER
                );
    }

    private boolean shouldDisplayMainToOtherTransfertButtons() {
        BankerUtils.RIGHT_TYPE rightType = handler.accountsRights.getOrDefault(selectedAccountId, BankerUtils.RIGHT_TYPE.READ_ONLY);
        return currentLeftState == LeftState.ACCOUNTS
                && selectedAccountId != null
                && (
                        rightType == BankerUtils.RIGHT_TYPE.CONTRIBUTOR
                    ||  rightType == BankerUtils.RIGHT_TYPE.BENEFICIARY
                    ||  rightType == BankerUtils.RIGHT_TYPE.OWNER
                );
    }

    private boolean shouldDisplayOtherAccountBalance() {
        return currentLeftState == LeftState.ACCOUNTS && selectedAccountId != null;
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

    private void requestTransfert(Optional<UUID> origin, UUID destination, long value) {
        if (destination == null)
            return;

        value *= transfertFactor;
        RequestTransfertPayload requestPayload = new RequestTransfertPayload(handler.syncId, origin, destination, value);
        ClientPlayNetworking.send(requestPayload);
    }

    private void requestData() {
        // Player's pouch balance (always displayed)
        long newPlayerBalance = ModComponents.CURRENCY.get(client.player).getValue();
        playerBalanceUpdated = playerBalance != newPlayerBalance;
        playerBalance = newPlayerBalance;

        // Player's main account balance (always displayed)
        if (handler.shouldUpdateAccountBalance(handler.mainAccountId)) {
            System.out.println("Updating account balance for main account " + handler.mainAccountId);
            RequestAccountBalancePayload requestPayload = new RequestAccountBalancePayload(handler.syncId, handler.mainAccountId);
            ClientPlayNetworking.send(requestPayload);
        }

        // Player's main account balance (always displayed)
        if (shouldDisplayOtherAccountBalance() && handler.shouldUpdateAccountBalance(selectedAccountId)) {
            System.out.println("Updating account balance for other account " + selectedAccountId);
            RequestAccountBalancePayload requestPayload = new RequestAccountBalancePayload(handler.syncId, selectedAccountId);
            ClientPlayNetworking.send(requestPayload);
        }

        // Other account balance (displayed when relevant)
        if (handler.shouldUpdateAccountsList()) {
            System.out.println("Updating accounts list");
            RequestAccountsListPayload requestPayload = new RequestAccountsListPayload(handler.syncId, filterRightType, filterIconId);
            ClientPlayNetworking.send(requestPayload);
        }
    }

    private void leftButtonClicked(int i) {
        closeFocusButton.visible = true;
        switch (currentLeftState){
            case LeftState.HOME:
                setEventFocused(i);

            case LeftState.ACCOUNTS:
                setAccountFocused(i);
                break;

            case LeftState.PLAYERS:
                setPlayerFocused(i);
                break;
        }
    }

    private void setEventFocused(int i) {

    }

    private void setAccountFocused(int i) {
        selectedAccountId = handler.getAccountAt(i);
        updateLeftButtonsActivity();
        updateMainOtherTransfertButtonsVisibility();
        updateTransfertButtonsActivity();
        addAccountButton.visible = false;
    }

    private void setPlayerFocused(int i) {

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
        if (playerBalanceUpdated) {
            if (playerBalance > 0) {
                goldFromPouchToMainButton.active = true;
                silverFromPouchToMainButton.active = !shiftKeyPressed;
                bronzeFromPouchToMainButton.active = !shiftKeyPressed;
                fullFromPouchToMainButton.active = true;
            } else {
                fromPouchToMainButtons.forEach(button -> {button.active = false;});
            }
        }

        if (handler.isMainAccountBalanceUpdated()) {
            boolean mainIsNotEmpty = handler.getMainAccountBalance() > 0;
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

        if (shouldDisplayOtherToMainTransfertButtons() && handler.isAccountBalanceUpdated(selectedAccountId)) {
            if (handler.getAccountBalance(selectedAccountId) > 0) {
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

        // Buttons visibility
        displayInfoButton.visible = true;
        displayTransfertButton.visible = false;
        addAccountButton.visible = false;
        confirmAccountCreationButton.visible = false;
        closeFocusButton.visible = false; // TODO: Switch to false if no event is selected

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
        closeFocusButton.visible = (selectedAccountId != null);

        // Buttons activation and visibility
        displayAccountsButton.active = false;
        homeButton.active = true;
        displayPlayersButton.active = true;
        if (stateCoupleMap.get(LeftState.ACCOUNTS) == RightState.INFO) {
            displayInfoButton.active = false;
            displaySettingsButton.active = true;
            addAccountButton.visible = false;
            confirmAccountCreationButton.visible = false;
        } else {
            displayInfoButton.active = true;
            displaySettingsButton.active = false;
            if (stateCoupleMap.get(LeftState.ACCOUNTS) == RightState.ACCOUNT_CREATION) {
                addAccountButton.visible = false;
                confirmAccountCreationButton.visible = true;
            } else {
                addAccountButton.visible = (selectedAccountId == null);
                confirmAccountCreationButton.visible = false;
            }
        }

        // Implement logic to remove visibility for all irrelevant buttons
        updateLeftButtons(true);
    }

    private void setPlayersState() {
        System.out.println("set players context");
        // Update context first
        currentLeftState = LeftState.PLAYERS;

        // Buttons visibility
        addAccountButton.visible = false;
        displayInfoButton.visible = false;
        displayTransfertButton.visible = true;
        closeFocusButton.visible = false; // TODO: Switch to false if no player is selected

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
                addAccountButton.visible = (selectedAccountId == null);
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

    private void removeFocus() {
        switch (currentLeftState)  {
            case HOME -> removeEventFocus();
            case ACCOUNTS -> removeAccountFocus();
            case PLAYERS -> removePlayerFocus();
        }
        updateLeftButtons(true);
    }

    private void removeEventFocus() {

    }

    private void removeAccountFocus() {
        selectedAccountId = null;
        closeFocusButton.visible = false;
        fromOtherToMainButtons.forEach(button -> button.visible = false);
        fromMainToOtherButtons.forEach(button -> button.visible = false);
    }

    private void removePlayerFocus() {

    }
}

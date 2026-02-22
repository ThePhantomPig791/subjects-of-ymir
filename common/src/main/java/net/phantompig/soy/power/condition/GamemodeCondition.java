package net.phantompig.soy.power.condition;

import com.google.gson.JsonObject;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.threetag.palladium.condition.Condition;
import net.threetag.palladium.condition.ConditionSerializer;
import net.threetag.palladium.util.context.DataContext;
import net.threetag.palladium.util.property.PalladiumProperty;
import net.threetag.palladium.util.property.StringProperty;

public class GamemodeCondition extends Condition {
    private final String gamemode;

    public GamemodeCondition(String gamemode) {
        this.gamemode = gamemode;
    }

    @Override
    public boolean active(DataContext context) {
        if (!(context.getEntity() instanceof ServerPlayer player)) return false;
        return player.gameMode.getGameModeForPlayer() == GameType.byName(this.gamemode);
    }

    @Override
    public ConditionSerializer getSerializer() {
        return SoyConditionSerializers.GAMEMODE.get();
    }

    public static class Serializer extends ConditionSerializer {
        public static final PalladiumProperty<String> GAMEMODE = new StringProperty("gamemode").configurable("Gamemode to check");

        public Serializer() {
            this.withProperty(GAMEMODE, "creative");
        }

        @Override
        public Condition make(JsonObject json) {
            return new GamemodeCondition(getProperty(json, GAMEMODE));
        }

        @Override
        public String getDocumentationDescription() {
            return "Checks if the entity's gamemode is the specified gamemode.";
        }
    }
}

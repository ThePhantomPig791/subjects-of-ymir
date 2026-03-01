package net.phantompig.soy.network;

import net.phantompig.soy.SubjectsOfYmir;
import net.threetag.palladiumcore.network.MessageType;
import net.threetag.palladiumcore.network.NetworkManager;

public class SoyNetwork {
    public static final NetworkManager NETWORK = NetworkManager.create(SubjectsOfYmir.rsrc("main_channel"));

    public static final MessageType TITAN_ATTACK = NETWORK.registerC2S("titan_attack", TitanAttackMessage::new);

    public static final MessageType TITAN_ATTACK_ANIMATION = NETWORK.registerS2C("titan_attack_animation", TitanAttackAnimationMessage::new);
    public static final MessageType SET_ATTACK_TICKER = NETWORK.registerS2C("set_attack_ticker", SetAttackTickerMessage::new);
    public static final MessageType SET_NEXT_ATTACK_STAGE_TIMER = NETWORK.registerS2C("set_next_attack_stage_timer", SetNextAttackStageTimerMessage::new);
    public static final MessageType UPDATE_TITAN_INSTANCE = NETWORK.registerS2C("update_titan_instance", UpdateTitanMessage::new);

    public static void init() {

    }
}

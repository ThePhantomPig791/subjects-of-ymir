package net.phantompig.soy.compat.voicechat;

import de.maxhenkel.voicechat.api.ForgeVoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import de.maxhenkel.voicechat.api.opus.OpusDecoder;
import de.maxhenkel.voicechat.api.opus.OpusEncoder;
import net.minecraft.world.entity.player.Player;
import net.phantompig.soy.SubjectsOfYmir;
import net.phantompig.soy.player.SoyPlayerExtension;

@ForgeVoicechatPlugin
public class SoyVoicechatPlugin implements VoicechatPlugin {
    private OpusEncoder encoder;
    private OpusDecoder decoder;

    @Override
    public String getPluginId() {
        return SubjectsOfYmir.MOD_ID;
    }

    @Override
    public void initialize(VoicechatApi api) {
        encoder = api.createEncoder();
        decoder = api.createDecoder();
        SubjectsOfYmir.LOGGER.info("Initialized Simple Voice Chat compatibility for Subjects of Ymir");
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(MicrophonePacketEvent.class, this::onMicrophonePacketEvent);
    }

    private void onMicrophonePacketEvent(MicrophonePacketEvent event) {
        if (event.getSenderConnection() == null) return;
        Player player = (Player) event.getSenderConnection().getPlayer().getPlayer();
        if (!(player instanceof SoyPlayerExtension ext) || ext.soy$getTitanInstance().getProgress() == 0) return;
        event.getPacket().setOpusEncodedData(
                ext.soy$getTitanInstance().titan != null && ext.soy$getTitanInstance().titan.canSpeak ?
                        processAudioForSpeakingTitan(event.getPacket().getOpusEncodedData()) :
                        processAudioForUnintelligibleTitan(event.getPacket().getOpusEncodedData())
        );
    }

    private byte[] processAudioForSpeakingTitan(byte[] opusEncodedData) {
        return encoder.encode(transform(decoder.decode(opusEncodedData), 2, 7, 0.55f));
    }

    private byte[] processAudioForUnintelligibleTitan(byte[] opusEncodedData) {
        return encoder.encode(transform(decoder.decode(opusEncodedData), 8, 8, 0.5f));
    }

    private short[] transform(short[] raw, int muffle, int growl, float pitch) {
        // reduce effective sample rate
        for (int i = 0; i < raw.length; i += muffle) {
            int sum = 0;
            int count = 0;

            for (int j = 0; j < muffle && i + j < raw.length; j++) {
                sum += raw[i + j];
                count++;
            }

            short avg = (short) (sum / count);

            for (int j = 0; j < muffle && i + j < raw.length; j++) {
                raw[i + j] = avg;
            }
        }

        for (int i = 0; i < raw.length; i++) {
            float x = raw[i] / 32768f;

            x *= growl;

            x = (float) Math.tanh(x);
            raw[i] = (short)(x * 26000);
        }

        // pitch down
        short[] shifted = new short[raw.length];
        for (int i = 0; i < shifted.length; i++) {
            float source = i * pitch;

            int a = (int) source;
            int b = Math.min(a + 1, raw.length - 1);

            float t = source - a;

            shifted[i] = (short) (
                    raw[a] * (1f - t) +
                            raw[b] * t
            );
        }

        // low pass filters
        for (int pass = 0; pass < 3; pass++) {
            for (int i = 1; i < shifted.length; i++) {
                shifted[i] = (short)(
                        shifted[i - 1] * 0.8f +
                                shifted[i] * 0.2f
                );
            }
        }

        short[] smoothed = shifted.clone();
        for (int i = 2; i < shifted.length - 2; i++) {
            smoothed[i] = (short)(
                    (shifted[i - 2]
                            + shifted[i - 1]
                            + shifted[i]
                            + shifted[i + 1]
                            + shifted[i + 2]) / 5
            );
        }

        return smoothed;
    }
}

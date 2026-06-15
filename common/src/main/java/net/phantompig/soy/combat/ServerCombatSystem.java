package net.phantompig.soy.combat;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.phantompig.soy.SoyConfig;
import net.phantompig.soy.network.*;
import net.phantompig.soy.player.SoyPlayerExtension;
import net.phantompig.soy.titan.hardening.HardeningSystem;
import net.phantompig.soy.titan.hardening.HardeningSystemHolder;
import net.threetag.palladium.util.Easing;
import net.threetag.palladium.util.EntityUtil;
import net.threetag.palladium.util.PlayerUtil;
import org.joml.Vector3f;

public class ServerCombatSystem {
    public final ServerPlayer player;
    public SoyPlayerExtension extension;

    public int attackTimer, nextStageTimer, cooldown;

    public int attackStage = 1; // not zero based!!! (because of how armin named the animations, i'm too lazy to change it)
    public AttackType attackType = AttackType.PUNCH;

    public ServerCombatSystem(ServerPlayer player) {
        this.player = player;
        if (player instanceof SoyPlayerExtension ext) {
            extension = ext;
        }
    }

    public HardeningSystem getHardening() {
        if (player instanceof HardeningSystemHolder hardh) {
            return hardh.soy$getHardeningSystem();
        }
        return null;
    }


    private float getExtraStrength() {
        float sum = 0;
        var effect = this.player.getEffect(MobEffects.DAMAGE_BOOST);
        if (effect != null) sum += 0.5f * effect.getAmplifier();
        sum += extension.getTitanInstance().strengthIncreases.values().stream().reduce(Float::sum).orElse(0f);
        return sum;
    }
    private int getMaxAttackTime() {
        if (extension.getTitanInstance().titan == null) return 0;
        return extension.getTitanInstance().titan.stats.attackSpeed + getHardening().getAttackTimeIncrease();
    }

    public enum AttackType {
        PUNCH(3),
        GROUND(3),
        KICK(2);

        final int maxAttackStage;
        AttackType(int maxAttackStage) {
            this.maxAttackStage = maxAttackStage;
        }

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public void attack() {
        if (extension.getTitanInstance().titan == null) return;
        final int maxAttackTime = getMaxAttackTime();
        if (attackTimer >= maxAttackTime * 12 / 20f || cooldown > 0 || extension == null || this.player.isSpectator()) {
            attackStage = 1;
            if (cooldown == 0) sendUpdateAttackTicker(-(cooldown = maxAttackTime));
            return;
        }

        player.setYBodyRot(player.getYHeadRot());

        if (player.getXRot() > 50) {
            attackType = AttackType.KICK;
            player.addEffect(new MobEffectInstance(
                    MobEffects.MOVEMENT_SLOWDOWN,
                    getMaxAttackTime(),
                    4,
                    false,
                    false
            ));
        } else if (player.getXRot() > 30) {
            attackType = AttackType.GROUND;
            player.addEffect(new MobEffectInstance(
                    MobEffects.MOVEMENT_SLOWDOWN,
                    getMaxAttackTime() / 2,
                    0,
                    false,
                    false
            ));
        } else {
            attackType = AttackType.PUNCH;
        }

        if (nextStageTimer > 0) {
            if (++attackStage > attackType.maxAttackStage) attackStage = 1;
        } else attackStage = 1;

        String animationId = attackType.toString() + attackStage;

        player.level().getEntitiesOfClass(Player.class, player.getBoundingBox().inflate(150)).forEach(e -> {
            if (e instanceof ServerPlayer pl) SoyNetwork.NETWORK.sendToPlayer(pl, new TitanAttackAnimationMessage(player, animationId));
        });

        nextStageTimer = (int) (1.2 * attackTimer) + 20;
        sendUpdateAttackTicker((int) (-(attackTimer = maxAttackTime) * 12f / 20));
        sendUpdateStageTimer(nextStageTimer);
    }

    public void tick() {
        if (extension.getTitanInstance().titan == null) return;
        if (extension.getTitanInstance().getProgress() == 0) {
            attackTimer = nextStageTimer = cooldown = 0;
            return;
        }
        if (attackTimer > 0) attackTimer--;
        if (attackTimer == Mth.lerpInt(12 / 20f, 0, getMaxAttackTime())) {
            attackEffect();
        }
        if (nextStageTimer > 0) nextStageTimer--;
        if (cooldown > 0) cooldown--;
    }

    public void attackEffect() {
        if (extension == null || extension.getTitanInstance().titan == null) {
            return;
        }

        double staminaToUse = player.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
        final boolean hit =
                player.level().getBlockCollisions(player, player.getBoundingBox().inflate(-1, -2, -1).move(player.getLookAngle().scale(5))).iterator().hasNext()
                || !player.level().getEntities(player, player.getBoundingBox().inflate(-1, -2, -1).move(player.getLookAngle().scale(5))).isEmpty();
        if (hit) {
            player.level().getEntities(null, player.getBoundingBox().inflate(64)).forEach(e -> {
                if (e instanceof ServerPlayer p) {
                    double strength = 20 / Math.max(Math.sqrt(player.distanceTo(p)), 1) / p.getBoundingBox().getYsize();
                    SoyNetwork.NETWORK.sendToPlayer(p, new ScreenShakeMessage(1200, new Vector3f((float) (5 + strength) / 200), Easing.OUTCUBIC));
                }
            });
        }
        if (attackType == AttackType.PUNCH) {
            if (attackStage < 3) {
                if (hit) explodeInFrontPartialLooking(1.5f, 5, 0, -0.2f);
            } else {
                if (hit) explodeInFrontPartialLooking(2, 5, 0, -0.1f);

                cooldown = getMaxAttackTime() * 3 / 2;
                nextStageTimer = 0;
                sendUpdateAttackTicker(-cooldown);
                sendUpdateStageTimer(nextStageTimer);

                staminaToUse *= 1.25;
            }
        }
        if (attackType == AttackType.GROUND) {
            if (attackStage < 3) {
                if (hit) explodeInFrontPartialLooking(1.5f, 6, 0.1f, -0.7f);
            } else {
                if (hit) explodeInFrontPartialLooking(2.5f, 7, 0.1f, -0.7f);

                cooldown = getMaxAttackTime() * 2;
                nextStageTimer = 0;
                sendUpdateAttackTicker(-cooldown);
                sendUpdateStageTimer(nextStageTimer);

                staminaToUse *= 1.5;
            }
        }
        if (attackType == AttackType.KICK) {
            if (hit) explodeInFrontFlat(1.5f, 4, -0.85f, 0);

            cooldown = (int) (getMaxAttackTime() * 1.5f);
            nextStageTimer += nextStageTimer / 2;
            sendUpdateAttackTicker(-cooldown);
            sendUpdateStageTimer(nextStageTimer);
        }

        if (hit) exhaust((int) staminaToUse);
        PlayerUtil.playSoundToAll(player.level(), player.getX(), player.getEyeY(), player.getZ(), 32, SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 3, (float) (1 - staminaToUse / 25));
    }

    public void exhaust(int stamina) {
        extension.getTitanInstance().exhaust(stamina);
    }

    public void sendUpdateAttackTicker(int ticks) {
        SoyNetwork.NETWORK.sendToPlayer(this.player, new SetAttackTickerMessage(ticks));
    }
    public void sendUpdateStageTimer(int ticks) {
        SoyNetwork.NETWORK.sendToPlayer(this.player, new SetNextAttackStageTimerMessage(ticks));
    }

    public void explodeInFrontPartialLooking(float strength, float distance, float startHeightOffset, float endHeightOffset) {
        strength *= (float) player.getAttribute(Attributes.ATTACK_DAMAGE).getValue() / 5;
        strength += getExtraStrength();

        var start = player.getEyePosition().add(0, startHeightOffset * player.getEyeHeight(), 0);
        var end = player.getLookAngle().multiply(1, 0.5, 1).normalize().scale(distance);
        Vec3 hitPos = EntityUtil.rayTraceWithEntities(player, start, start.add(end).add(0, endHeightOffset * player.getEyeHeight(), 0), distance, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, en -> true).getLocation();

        player.level().explode(player, hitPos.x, hitPos.y, hitPos.z, strength, false, getExplosionInteraction());
    }

    public void explodeInFrontFlat(float strength, float distance, float startHeightOffset, float endHeightOffset) {
        strength *= (float) player.getAttribute(Attributes.ATTACK_DAMAGE).getValue() / 5;
        strength += getExtraStrength();

        var start = player.getEyePosition().add(0, startHeightOffset * player.getEyeHeight(), 0);
        var end = player.getLookAngle().multiply(1, 0, 1).normalize().scale(distance);
        Vec3 hitPos = EntityUtil.rayTraceWithEntities(player, start, start.add(end).add(0, endHeightOffset * player.getEyeHeight(), 0), distance, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, en -> true).getLocation();

        player.level().explode(player, hitPos.x, hitPos.y, hitPos.z, strength, false, getExplosionInteraction());
    }

    private Level.ExplosionInteraction getExplosionInteraction() {
        return SoyConfig.Server.shouldTitansExplodeBlocksOnAttack() ? Level.ExplosionInteraction.TNT : Level.ExplosionInteraction.NONE;
    }
}

package net.phantompig.soy.combat;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.phantompig.soy.network.SetAttackTickerMessage;
import net.phantompig.soy.network.SetNextAttackStageTimerMessage;
import net.phantompig.soy.network.SoyNetwork;
import net.phantompig.soy.network.TitanAttackAnimationMessage;
import net.phantompig.soy.player.SoyPlayerExtension;
import net.phantompig.soy.titan.hardening.HardeningSystem;
import net.phantompig.soy.titan.hardening.HardeningSystemHolder;
import net.threetag.palladium.util.EntityUtil;

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
        if (attackTimer > 0 || cooldown > 0 || extension == null || extension.getTitanInstance().titan == null || this.player.isSpectator()) {
            attackStage = 1;
            return;
        }

        player.setYBodyRot(player.getYHeadRot());

        if (player.getXRot() > 50) {
            attackType = AttackType.KICK;
            player.addEffect(new MobEffectInstance(
                    MobEffects.MOVEMENT_SLOWDOWN,
                    (extension.getTitanInstance().titan.stats.attackSpeed + getHardening().getAttackTimeIncrease()),
                    4,
                    false,
                    false
            ));
        } else if (player.getXRot() > 30) {
            attackType = AttackType.GROUND;
            player.addEffect(new MobEffectInstance(
                    MobEffects.MOVEMENT_SLOWDOWN,
                    (extension.getTitanInstance().titan.stats.attackSpeed + getHardening().getAttackTimeIncrease()) / 2,
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

        attackTimer = (extension.getTitanInstance().titan.stats.attackSpeed + getHardening().getAttackTimeIncrease());
        nextStageTimer = (int) (1.2 * attackTimer) + 20;
        sendUpdateAttackTicker(-attackTimer);
        sendUpdateStageTimer(nextStageTimer);
    }

    public void tick() {
        if (extension.getTitanInstance().titan == null) return;
        if (extension.getTitanInstance().getProgress() == 0) {
            attackTimer = nextStageTimer = cooldown = 0;
            return;
        }
        if (attackTimer > 0) attackTimer--;
        if (attackTimer == Mth.lerpInt(12 / 20f, 0, (extension.getTitanInstance().titan.stats.attackSpeed + getHardening().getAttackTimeIncrease()))) {
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
        if (attackType == AttackType.PUNCH) {
            if (attackStage < 3) {
                explodeInFrontPartialLooking(1.5f, 5, 0, -0.2f);
            } else {
                explodeInFrontPartialLooking(2, 5, 0, -0.1f);

                cooldown = (extension.getTitanInstance().titan.stats.attackSpeed + getHardening().getAttackTimeIncrease()) * 3 / 2;
                nextStageTimer = 0;
                sendUpdateAttackTicker(-cooldown);
                sendUpdateStageTimer(nextStageTimer);

                staminaToUse *= 1.25;
            }
        }
        if (attackType == AttackType.GROUND) {
            if (attackStage < 3) {
                explodeInFrontPartialLooking(1.5f, 6, 0.1f, -0.7f);
            } else {
                explodeInFrontPartialLooking(2.5f, 7, 0.1f, -0.7f);

                cooldown = (extension.getTitanInstance().titan.stats.attackSpeed + getHardening().getAttackTimeIncrease()) * 2;
                nextStageTimer = 0;
                sendUpdateAttackTicker(-cooldown);
                sendUpdateStageTimer(nextStageTimer);

                staminaToUse *= 1.5;
            }
        }
        if (attackType == AttackType.KICK) {
            explodeInFrontFlat(1.5f, 4, -0.85f, 0);

            cooldown = (int) ((extension.getTitanInstance().titan.stats.attackSpeed + getHardening().getAttackTimeIncrease()) * 1.5f);
            nextStageTimer += nextStageTimer / 2;
            sendUpdateAttackTicker(-cooldown);
            sendUpdateStageTimer(nextStageTimer);
        }

        exhaust((int) staminaToUse);
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

        var start = player.getEyePosition().add(0, startHeightOffset * player.getEyeHeight(), 0);
        var end = player.getLookAngle().multiply(1, 0.5, 1).normalize().scale(distance);
        Vec3 hitPos = EntityUtil.rayTraceWithEntities(player, start, start.add(end).add(0, endHeightOffset * player.getEyeHeight(), 0), distance, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, en -> true).getLocation();

        player.level().explode(player, hitPos.x, hitPos.y, hitPos.z, strength, false, Level.ExplosionInteraction.TNT);
    }

    public void explodeInFrontFlat(float strength, float distance, float startHeightOffset, float endHeightOffset) {
        strength *= (float) player.getAttribute(Attributes.ATTACK_DAMAGE).getValue() / 5;

        var start = player.getEyePosition().add(0, startHeightOffset * player.getEyeHeight(), 0);
        var end = player.getLookAngle().multiply(1, 0, 1).normalize().scale(distance);
        Vec3 hitPos = EntityUtil.rayTraceWithEntities(player, start, start.add(end).add(0, endHeightOffset * player.getEyeHeight(), 0), distance, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, en -> true).getLocation();

        player.level().explode(player, hitPos.x, hitPos.y, hitPos.z, strength, false, Level.ExplosionInteraction.TNT);
    }
}

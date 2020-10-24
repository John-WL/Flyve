package rlbotexample.bot_behaviour.panbot.implementation.triple_threat;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.metagame.possessions.PlayerRole;
import rlbotexample.bot_behaviour.metagame.possessions.PlayerRoleHandler3V3;
import rlbotexample.bot_behaviour.panbot.PanBot;
import rlbotexample.bot_behaviour.skill_controller.implementation.kickoff.comit_to_ball.KickoffSpecializedOnBall;
import rlbotexample.bot_behaviour.skill_controller.implementation.kickoff.get_boost.KickoffSpecializedOnBoost;
import rlbotexample.input.boost.BoostManager;
import rlbotexample.input.boost.BoostPad;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.car.ExtendedCarData;
import rlbotexample.output.BotOutput;
import util.math.vector.Vector3;

import java.util.List;

public class KickoffBehaviour extends PanBot {

    private final KickoffSpecializedOnBall kickoffSpecializedOnBall;
    private final KickoffSpecializedOnBoost kickoffSpecializedOnBoost;

    public KickoffBehaviour() {
        kickoffSpecializedOnBall = new KickoffSpecializedOnBall(this);
        kickoffSpecializedOnBoost = new KickoffSpecializedOnBoost(this);
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        final PlayerRoleHandler3V3 playerRoleHandler = new PlayerRoleHandler3V3(input, input.team);

        handleKickoff(playerRoleHandler, input);

        return super.output();
    }

    private void handleKickoff(PlayerRoleHandler3V3 playerRoleHandler, DataPacket input) {
        final PlayerRole playerRole = playerRoleHandler.getPlayerRole(input);
        if(isKickoffAmbiguous(playerRoleHandler, input)) {
            handleAmbiguousKickoff(playerRoleHandler, input);
        }
        else {
            if(playerRole == PlayerRole.OFFENSIVE) {
                goForKickoff(playerRoleHandler, input);
            }
            else {
                goForBigBoostAtKickoff(playerRoleHandler, input);
            }
        }
    }

    private void handleAmbiguousKickoff(PlayerRoleHandler3V3 playerRoleHandler, DataPacket input) {
        if(isSelfAtLeftWhileKickoff(playerRoleHandler, input)) {
            goForKickoff(playerRoleHandler, input);
        }
        else {
            goForBigBoostAtKickoff(playerRoleHandler, input);
        }
    }

    private void goForKickoff(PlayerRoleHandler3V3 playerRoleHandler, DataPacket input) {
        kickoffSpecializedOnBall.updateOutput(input);
    }

    private void goForBigBoostAtKickoff(PlayerRoleHandler3V3 playerRoleHandler, DataPacket input) {
        Vector3 boostDestination = getProperKickoffBoost(playerRoleHandler, input);
        kickoffSpecializedOnBoost.setDestination(boostDestination);
        kickoffSpecializedOnBoost.updateOutput(input);
    }

    // kickoffs are ambiguous when 2 teammates are doing the same mirrored kickoff
    private boolean isKickoffAmbiguous(PlayerRoleHandler3V3 playerRoleHandler, DataPacket input) {
        final Vector3 ballPosition = input.ball.position;
        final PlayerRole playerRole = playerRoleHandler.getPlayerRole(input);
        final ExtendedCarData offensiveCar = playerRoleHandler.getPlayerFromRole(PlayerRole.OFFENSIVE);
        final ExtendedCarData backerCar = playerRoleHandler.getPlayerFromRole(PlayerRole.BACKER);

        return (playerRole == PlayerRole.OFFENSIVE || playerRole == PlayerRole.BACKER) &&
                (Math.abs(offensiveCar.position.minus(ballPosition).magnitude() - backerCar.position.minus(ballPosition).magnitude()) < 0.1);
    }

    private boolean isSelfAtLeftWhileKickoff(PlayerRoleHandler3V3 playerRoleHandler, DataPacket input) {
        final ExtendedCarData offensiveCar = playerRoleHandler.getPlayerFromRole(PlayerRole.OFFENSIVE);
        final ExtendedCarData backerCar = playerRoleHandler.getPlayerFromRole(PlayerRole.BACKER);
        final Vector3 midPointBetweenKickoffTeamPlayers = offensiveCar.position.plus(backerCar.position.minus(offensiveCar.position).scaled(0.5));

        return (midPointBetweenKickoffTeamPlayers.minus(input.car.position).x > 0) == (input.team == 0);
    }

    private Vector3 getProperKickoffBoost(PlayerRoleHandler3V3 playerRoleHandler, DataPacket input) {
        if((input.car.position.x < 10) == (input.team == 0)) {
            return getLeftKickoffBoost(input).getLocation();
        }
        else if((getClosestPlayer(input).position.x > 0) == (input.team == 0)) {
            return getLeftKickoffBoost(input).getLocation();
        }
        else {
            return getRightKickoffBoost(input).getLocation();
        }
    }

    private BoostPad getLeftKickoffBoost(DataPacket input) {
        if(input.team == 0) {
            List<BoostPad> boostPads = BoostManager.getFullBoosts();
            for(BoostPad boostPad: boostPads) {
                if(boostPad.getLocation().x < -1000
                        && boostPad.getLocation().y < -1000) {
                    return boostPad;
                }
            }
        }
        else {
            List<BoostPad> boostPads = BoostManager.getFullBoosts();
            for(BoostPad boostPad: boostPads) {
                if(boostPad.getLocation().x > 1000
                        && boostPad.getLocation().y > 1000) {
                    return boostPad;
                }
            }
        }
        return null;
    }

    private BoostPad getRightKickoffBoost(DataPacket input) {
        if(input.team == 0) {
            List<BoostPad> boostPads = BoostManager.getFullBoosts();
            for(BoostPad boostPad: boostPads) {
                if(boostPad.getLocation().x > 1000
                        && boostPad.getLocation().y < -1000) {
                    return boostPad;
                }
            }
        }
        else {
            List<BoostPad> boostPads = BoostManager.getFullBoosts();
            for(BoostPad boostPad: boostPads) {
                if(boostPad.getLocation().x < -1000
                        && boostPad.getLocation().y > 1000) {
                    return boostPad;
                }
            }
        }
        return null;
    }

    private ExtendedCarData getClosestPlayer(DataPacket input) {
        double shortestDistance = Double.MAX_VALUE;
        List<ExtendedCarData> players = input.allCars;
        ExtendedCarData closestCar = players.get(0);
        for(ExtendedCarData carData: players) {
            if(carData.position.minus(input.car.position).magnitude() < shortestDistance && carData != input.car) {
                shortestDistance = carData.position.minus(input.car.position).magnitude();
                closestCar = carData;
            }
        }

        return closestCar;
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {

    }
}

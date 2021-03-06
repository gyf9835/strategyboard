package controllers.openttd;

import java.util.List;

import controllers.Application;
import controllers.Check;
import controllers.Profile;
import groovy.lang.Closure;
import java.io.PrintWriter;
import java.util.Map;

import openttd.OpenttdAdminHandler;
import openttd.OpenttdServerHandler;

import models.leaderboard.PlayedGame;
import models.leaderboard.PlayedGameScore;
import models.leaderboard.PlayerScore;
import models.leaderboard.Scenario;
import models.openttd.OpenttdServer;
import openttd.OpenttdClientHandler;
import play.cache.CacheFor;
import play.mvc.With;

@With(Profile.class)
public class Openttd extends Application {
	
	@CacheFor("5s")
	@Check(value = {Profile.VIEWER})
	public static void index() {
		OpenttdServer openTTDStatus = OpenttdServer.getStatus();
		List<PlayerScore> playerScores = PlayedGameScore.getLastMonthPlayerScores(1, 10);
		List<PlayedGame> lastPlayedGames = PlayedGame.getLastPlayedGames();
		List<PlayedGame> bestArticGames = PlayedGame.getBestPlayedGamesForScenarioId(Scenario.Id.Artic);
		List<PlayedGame> bestDeserticGames = PlayedGame.getBestPlayedGamesForScenarioId(Scenario.Id.Desertic);
		List<PlayedGame> bestHighSpeedGames = PlayedGame.getBestPlayedGamesForScenarioId(Scenario.Id.HighSpeed);
		List<PlayedGame> bestSteamGames = PlayedGame.getBestPlayedGamesForScenarioId(Scenario.Id.Steam);
		List<PlayedGame> bestToylandGames = PlayedGame.getBestPlayedGamesForScenarioId(Scenario.Id.Toyland);
		render(
				openTTDStatus,
				playerScores,
				lastPlayedGames,
				bestArticGames,
				bestDeserticGames,
				bestHighSpeedGames,
				bestSteamGames,
				bestToylandGames
		);
	}
	
	@Check(value = {Profile.MODERATOR})
	public static void start() {
		OpenttdServerHandler serverHandler = OpenttdServerHandler.getInstance();
		if(!serverHandler.isRunning()) {
			serverHandler.startup();
		}
		OpenttdAdminHandler adminHandler = OpenttdAdminHandler.getInstance();
		if(serverHandler.isRunning() && !adminHandler.isRunning()) {
			adminHandler.startup();
		}
		OpenttdClientHandler clientHandler = OpenttdClientHandler.getInstance();
		if(serverHandler.isRunning() && !clientHandler.isRunning()) {
			clientHandler.startup();
		}
		index();
	}

	@Check(value = {Profile.MODERATOR})
	public static void stop() {
		OpenttdServerHandler serverHandler = OpenttdServerHandler.getInstance();
		if(serverHandler.isRunning()) {
			serverHandler.shutdown();
		}
		OpenttdAdminHandler adminHandler = OpenttdAdminHandler.getInstance();
		if(adminHandler.isRunning()) {
			adminHandler.shutdown();
		}
		OpenttdClientHandler clientHandler = OpenttdClientHandler.getInstance();
		if(clientHandler.isRunning()) {
			clientHandler.shutdown();
		}
		index();
	}

}

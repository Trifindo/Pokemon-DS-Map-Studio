/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.game;

import java.awt.image.BufferedImage;
import utils.Utils;

/**
 *
 * @author Trifindo
 */
public class Game {
    
    public static final int DIAMOND = 0;
    public static final int PEARL = 1;
    public static final int PLATINUM = 2;
    public static final int HEART_GOLD = 3;
    public static final int SOUL_SILVER = 4;
    public static final int BLACK = 5;
    public static final int WHITE = 6;
    public static final int BLACK2 = 7;
    public static final int WHITE2 = 8;
    
    public static final String[] gameNames = new String[]{
        "Diamond", "Pearl", "Platinum", "Heart Gold", "Soul Silver", "Black",
        "White", "Black 2", "White 2"
    };
    
    public static final int numGames = 9;
    public int gameSelected;
    
    public Game(int game){
        this.gameSelected = game;
    }
    
    public BufferedImage[] gameIcons;
    
    public void loadGameIcons(){
        gameIcons = Utils.loadHorizontalImageArrayAsResource("/icons/gameIcons.png", numGames);
    }
    
    public static boolean isGenV(int gameIndex){
        return gameIndex > SOUL_SILVER;
    }
    
    public String getName(){
        return gameNames[gameSelected];
    }
    
}

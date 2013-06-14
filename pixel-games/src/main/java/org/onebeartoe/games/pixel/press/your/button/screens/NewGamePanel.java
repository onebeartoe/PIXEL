
package org.onebeartoe.games.pixel.press.your.button.screens;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import org.onebeartoe.games.pixel.press.your.button.Game;
import org.onebeartoe.games.pixel.press.your.button.GameStates;
import org.onebeartoe.games.pixel.press.your.button.Player;
import org.onebeartoe.games.pixel.press.your.button.PressYourButton;

/**
 * @author rmarquez
 */
public class NewGamePanel extends JPanel implements ActionListener
{
    private JComboBox<Integer> playerCountDropdown;
    
    private JComboBox<Integer> targetScoreDropdown;
    
    private JButton startButton;
    
    private PressYourButton plugin;
    
    public NewGamePanel(PressYourButton parent)
    {
	this.plugin = parent;
	
	Integer [] values = {1,2,3};
	playerCountDropdown = new JComboBox(values);
	String title = "Number of Players";
	TitledBorder titledBorder = BorderFactory.createTitledBorder(title);
	playerCountDropdown.setBorder(titledBorder);
	
	Integer [] scores = {100, 200, 300, 400};
	title = "Target Score";
	titledBorder = BorderFactory.createTitledBorder(title);
	targetScoreDropdown = new JComboBox(scores);
	targetScoreDropdown.setBorder(titledBorder);
	
	startButton = new JButton("Start");
	startButton.addActionListener(this);
	
	LayoutManager layout = new GridLayout(3,1, 10,10);
	setLayout(layout);
	
	add(playerCountDropdown);
	add(targetScoreDropdown);
	add(startButton);
    }

    public void actionPerformed(ActionEvent e) 
    {
	plugin.invalidate();
	plugin.updateUI();
	    
	Game game = createNewGame();
	
	plugin.currentGame = game;
	
	plugin.remove(plugin.newGamePanel);
	plugin.add(plugin.endOfTurnPanel, BorderLayout.CENTER);
	
	plugin.newGame();
	
	plugin.gameState = GameStates.PLAYERS_TURN;
	
	plugin.boardSound.loop();
    }
    
    public Game createNewGame()
    {
	Integer count = (Integer) playerCountDropdown.getSelectedItem();
	Integer targetScore = (Integer) targetScoreDropdown.getSelectedItem();
	
	List<Player> players = new ArrayList();
	
	for(int p=0; p<count; p++)
	{
	    Player player = new Player();
	    players.add(player);
	}
	
	Game game = new Game();
	game.players = players;
	game.targetScore = targetScore;
	
	return game;
    }
    
}

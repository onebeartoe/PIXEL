
package org.onebeartoe.games.pixel.press.your.button;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * @author rmarquez
 */
public class EndOfTurnPanel extends JPanel
{
    
    private final PressYourButton plugin;
    
    private final PreviewPanel previewPanel;

    public EndOfTurnPanel(final PressYourButton plugin, PreviewPanel previewPanel) 
    {
	this.plugin = plugin;
	this.previewPanel = previewPanel;
	
	JButton newGameButton = new JButton("New Game");
	
	JButton showScoreButton = new JButton("Show Score");
	showScoreButton.addActionListener( new ShowScoreListener() );
	
	JButton nextPlayerButton = new JButton("Next Player");
	nextPlayerButton.addActionListener( new NextPlayerListener() );
	
	JPanel buttonPanel = new JPanel( new GridLayout(1, 3, 10, 10) );
	buttonPanel.add(newGameButton);
	buttonPanel.add(showScoreButton);
	buttonPanel.add(nextPlayerButton);
	
	setLayout( new BorderLayout() );
	
	add(previewPanel, BorderLayout.CENTER);
	add(buttonPanel, BorderLayout.SOUTH);
    }

    private class ShowScoreListener implements ActionListener
    {

	public void actionPerformed(ActionEvent e) 
	{
	    plugin.gameState = GameStates.SHOW_SCORE;
	}	
	
    }
    
    private class NextPlayerListener implements ActionListener
    {
	public void actionPerformed(ActionEvent e) 
	{
	    plugin.currentGame.currentPlayer++;
		    
	    if(plugin.currentGame.currentPlayer == plugin.currentGame.players.size() )
	    {
		plugin.currentGame.currentPlayer = 0;
	    }
			
	    plugin.gameState = GameStates.NEXT_PLAYERS_TURN;
	}	
    }
    
}


package org.onebeartoe.games.pixel.press.your.button.screens;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.onebeartoe.games.pixel.press.your.button.GameStates;
import org.onebeartoe.games.pixel.press.your.button.PressYourButton;
import org.onebeartoe.games.pixel.press.your.button.PreviewPanel;

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
	
	JButton stopButton = new JButton("Stop");
	stopButton.addActionListener( new StopButtonListener() );
	
	Box box = new Box(BoxLayout.Y_AXIS);
        box.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        box.add(Box.createVerticalGlue());
        box.add(this.previewPanel);
        box.add(stopButton );
	box.add(Box.createVerticalGlue());
	
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
	
	add(box, BorderLayout.CENTER);
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
    
    private class StopButtonListener implements ActionListener
    {
	public void actionPerformed(ActionEvent e) 
	{
	    if(plugin.gameState == GameStates.NEXT_PLAYERS_TURN)
	    {
		plugin.turnIsOver();
	    }
	}
    }
    
}

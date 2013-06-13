
package org.onebeartoe.games.pixel.press.your.button.screens;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
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
    
    private JOptionPane messageDialog;

    public EndOfTurnPanel(final PressYourButton plugin, PreviewPanel previewPanel) 
    {
	this.plugin = plugin;
	this.previewPanel = previewPanel;
	
	messageDialog = new JOptionPane();		
	
	Box box = createCenteredBox(this.previewPanel);
	
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
    
    private Box createCenteredBox(Component c)
    {
	JButton stopButton = new JButton("Stop");
	stopButton.addActionListener( new StopButtonListener() );
	
	Box box = new Box(BoxLayout.Y_AXIS);
        box.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        box.add(Box.createVerticalGlue());
        box.add(c);
        box.add(stopButton );
	box.add(Box.createVerticalGlue());
	
	return box;
    }

    private class ShowScoreListener implements ActionListener
    {
	public void actionPerformed(ActionEvent e) 
	{
	    if(plugin.gameState == GameStates.END_OF_TURN)
	    {
		plugin.switchToScoreView(true);
		plugin.gameState = GameStates.SHOW_SCORE;
	    }
	    else
	    {
		String message = "The score cannot be shown during a players turn.";
		JOptionPane.showMessageDialog(plugin, message);
	    }
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
	    
	    plugin.boardSound.loop();
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

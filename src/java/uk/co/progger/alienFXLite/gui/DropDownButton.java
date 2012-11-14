package uk.co.progger.alienFXLite.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.ref.WeakReference;
import java.util.LinkedList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JWindow;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public class DropDownButton extends JButton{
	private static final long serialVersionUID = 1L;
	
	private static final int ARROW_BUTTON_MIN_SIZE = 16;
	private static final Insets zeroInsets = new Insets(0,0,0,0);
	private static final Dimension buttonDimension = new Dimension(ARROW_BUTTON_MIN_SIZE, ARROW_BUTTON_MIN_SIZE);
	
	private static final ActionHandlerItem itemActionhandler = new ActionHandlerItem(); 
	private static final ActionHandler buttonActionHandler = new ActionHandler();
	private static final PopupMenuHandler popUpMenuHandler = new PopupMenuHandler();
	private static final JWindow dialog = new JWindow((Frame) null);
	private static final JPopupMenu menu = new JPopupMenu();
	
	static{
		dialog.setAlwaysOnTop(true);
		menu.addPopupMenuListener(popUpMenuHandler);
	}
	
	private JButton buttons[];
	private LinkedList<Integer> buttonIds;
	
	public DropDownButton(JButton[] buttons){
		super(new ImageIcon(AlienFXResources.ARROW_ICON_IMAGE));
		this.buttons = buttons.clone();	
		buttonIds = new LinkedList<Integer>();
		
		this.addActionListener(buttonActionHandler);
		this.setMargin(zeroInsets);
		this.setPreferredSize(buttonDimension);
    }
	
	public void clearAll(){
		menu.removeAll();
		buttonIds.clear();
	}
	
	public void addButton(int button){
		buttonIds.add(button);
	}
	
	private static class DropDownButtonItem extends JMenuItem{
		private static final long serialVersionUID = 1L;
		private WeakReference<JButton> button;
		private DropDownButtonItem(JButton button){
			super(button.getIcon());
			this.button = new WeakReference<JButton>(button);
		}
	
		private JButton getButton(){
			return button.get();
		}
	}
	
	private static class ActionHandlerItem implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if(source instanceof DropDownButtonItem){
				JButton button = ((DropDownButtonItem) source).getButton();
					for(ActionListener a : button.getActionListeners()){
					a.actionPerformed(new ActionEvent(button, 0, ""));
				}
			}
		}
	}
	
	private static class PopupMenuHandler implements PopupMenuListener {
        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        }
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            dialog.setVisible(false);
        }
        public void popupMenuCanceled(PopupMenuEvent e) {
            dialog.setVisible(false);
        }
    };
    
	private static class ActionHandler implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			menu.removeAll();
			for(Integer i : ((DropDownButton) source).buttonIds){
				DropDownButtonItem item = new DropDownButtonItem(((DropDownButton) source).buttons[i]);
				item.addActionListener(itemActionhandler);
				menu.add(item);
			}
			if(source instanceof DropDownButton){
				dialog.setLocation(((Component) source).getLocationOnScreen());
				dialog.setVisible(true);
				menu.show(dialog.getContentPane(), 0, 0);
			}
		}
	}
  
}

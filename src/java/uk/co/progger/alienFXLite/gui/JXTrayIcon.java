package uk.co.progger.alienFXLite.gui;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;
import javax.swing.JWindow;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public class JXTrayIcon extends TrayIcon {
    private JPopupMenu menu;
    private static JWindow dialog;
    static {
        dialog = new JWindow((Frame) null);
        //dialog.setUndecorated(true);
        dialog.setAlwaysOnTop(true);
    }
    
    private static PopupMenuListener popupListener = new PopupMenuListener() {
        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        }

        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            dialog.setVisible(false);
        }

        public void popupMenuCanceled(PopupMenuEvent e) {
            dialog.setVisible(false);
        }
    };


    public JXTrayIcon(Image image) {
        super(image);
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                showJPopupMenu(e);
            }

            public void mouseReleased(MouseEvent e) {
                showJPopupMenu(e);
            }
        });
    }

    protected void showJPopupMenu(MouseEvent e) {
        if (e.isPopupTrigger() && menu != null) {
            Dimension size = menu.getPreferredSize();
            Point p = computeLocation(e.getX(), e.getY(), size);
            showJPopupMenu(p.x, p.y);// - size.height);
        }
    }
    
    private Point computeLocation(int x, int y, Dimension size){
    	Point p = new Point();
    	Toolkit kit = Toolkit.getDefaultToolkit();
    	Dimension screen = kit.getScreenSize();
    	
    	//some default values
    	p.x = x;
    	p.y = y;
    	//if(x - size.width < 0)
    		//p.x = x + size.width;
    	
    	if(x + size.width > screen.width)
    		p.x = x - size.width;
    	
    	//if(y - size.height < 0)
    		//p.y = y + size.height;
    	
    	if(y + size.height > screen.height)
    		p.y = y - size.height;
    	
    	return p;
    }
    
    protected void showJPopupMenu(int x, int y) {
        dialog.setLocation(x, y);
        dialog.setVisible(true);
        menu.show(dialog.getContentPane(), 0, 0);
        // popup works only for focused windows
        dialog.toFront();
    }

    public JPopupMenu getJPopupMenu() {
        return menu;
    }

    public void setJPopupMenu(JPopupMenu menu) {
        if (this.menu != null) {
            this.menu.removePopupMenuListener(popupListener);
        }
        this.menu = menu;
        menu.addPopupMenuListener(popupListener);
    }
} 

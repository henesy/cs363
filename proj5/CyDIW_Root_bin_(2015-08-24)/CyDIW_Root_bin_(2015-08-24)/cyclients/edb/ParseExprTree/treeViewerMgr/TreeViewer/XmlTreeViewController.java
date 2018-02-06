package cyclients.edb.ParseExprTree.treeViewerMgr.TreeViewer;

/*
 * @author Hermann Wöhrmann
 *
 * Description:
 *
 * Version Date       Comments
 * 1.01.01 03.11.2004 created
 *
 */

import java.awt.event.*;
import java.io.File;

import javax.swing.*;
import javax.swing.event.*;
import speed.jg.*;
import speed.util.ServiceException;

public class XmlTreeViewController implements ActionListener, KeyListener, MenuListener, PopupMenuListener, TreeSelectionListener, WindowListener
{ GUIObject gui;
  JFrame xmlTreeView;
  JMenu fileMenu;
  JMenuItem copyMenuItem;
  JMenuItem exitMenuItem;
  JMenuItem openMenuItem;
  JPopupMenu popupMenu;
  JTextArea xmlTextArea;
  JTree selectionTree;

  XmlTreeViewController(GUIObject guiObject, File f) throws ServiceException
  { this.gui = guiObject;
    copyMenuItem = (JMenuItem)gui.getComponent("copyMenuItem");
    copyMenuItem.addActionListener(this);
    exitMenuItem = (JMenuItem)gui.getComponent("exitMenuItem");
    exitMenuItem.addActionListener(this);
    fileMenu = (JMenu)gui.getComponent("fileMenu");
    fileMenu.addMenuListener(this);
    openMenuItem = (JMenuItem)gui.getComponent("openMenuItem");
    openMenuItem.addActionListener(this);
    popupMenu = (JPopupMenu)gui.getComponent("popupMenu");
    popupMenu.addPopupMenuListener(this);
    selectionTree = (JTree)gui.getComponent("selectionTree");
    selectionTree.addTreeSelectionListener(this);
    xmlTextArea = (JTextArea)gui.getComponent("xmlTextArea");
    xmlTextArea.addKeyListener(this);
    xmlTreeView = (JFrame)gui.getComponent("xmlTreeView");
    xmlTreeView.addWindowListener(this);
    initialize(f);
  }

//==============================================================================
// Implementing the ActionListener Interface ...
//------------------------------------------------------------------------------

  public void actionPerformed(ActionEvent e)
  { try
    { Object component = e.getSource();
      if (component.equals(copyMenuItem)) handleCopyMenuItemActionPerformedEvent(e);
      else if (component.equals(exitMenuItem)) handleExitMenuItemActionPerformedEvent(e);
      else if (component.equals(openMenuItem)) handleOpenMenuItemActionPerformedEvent(e);
    }
    catch (Exception ex)
    { // in order to avoid throwing exceptions into the event-dispatching thread
      ex.printStackTrace(System.err);
    }
  }

//==============================================================================
// Implementing the KeyListener Interface ...
//------------------------------------------------------------------------------

  public void keyTyped(KeyEvent e)
  { try
    { Object component = e.getSource();
      if (component.equals(xmlTextArea)) handleXmlTextAreaKeyTypedEvent(e);
    }
    catch (Exception ex)
    { // in order to avoid throwing exceptions into the event-dispatching thread
      ex.printStackTrace(System.err);
    }
  }

//------------------------------------------------------------------------------

  public void keyPressed(KeyEvent e)
  { try
    { Object component = e.getSource();
      if (component.equals(xmlTextArea)) handleXmlTextAreaKeyPressedEvent(e);
    }
    catch (Exception ex)
    { // in order to avoid throwing exceptions into the event-dispatching thread
      ex.printStackTrace(System.err);
    }
  }

//------------------------------------------------------------------------------

  public void keyReleased(KeyEvent e)
  { try
    { Object component = e.getSource();
      if (component.equals(xmlTextArea)) handleXmlTextAreaKeyReleasedEvent(e);
    }
    catch (Exception ex)
    { // in order to avoid throwing exceptions into the event-dispatching thread
      ex.printStackTrace(System.err);
    }
  }

//==============================================================================
// Implementing the MenuListener Interface ...
//------------------------------------------------------------------------------

  public void menuSelected(MenuEvent e)
  { try
    { Object component = e.getSource();
      if (component.equals(fileMenu)) handleFileMenuMenuSelectedEvent(e);
    }
    catch (Exception ex)
    { // in order to avoid throwing exceptions into the event-dispatching thread
      ex.printStackTrace(System.err);
    }
  }

//------------------------------------------------------------------------------

  public void menuDeselected(MenuEvent e)
  { try
    { Object component = e.getSource();
      if (component.equals(fileMenu)) handleFileMenuMenuDeselectedEvent(e);
    }
    catch (Exception ex)
    { // in order to avoid throwing exceptions into the event-dispatching thread
      ex.printStackTrace(System.err);
    }
  }

//------------------------------------------------------------------------------

  public void menuCanceled(MenuEvent e)
  { try
    { Object component = e.getSource();
      if (component.equals(fileMenu)) handleFileMenuMenuCanceledEvent(e);
    }
    catch (Exception ex)
    { // in order to avoid throwing exceptions into the event-dispatching thread
      ex.printStackTrace(System.err);
    }
  }

//==============================================================================
// Implementing the PopupMenuListener Interface ...
//------------------------------------------------------------------------------

  public void popupMenuWillBecomeVisible(PopupMenuEvent e)
  { try
    { Object component = e.getSource();
      if (component.equals(popupMenu)) handlePopupMenuPopupMenuWillBecomeVisibleEvent(e);
    }
    catch (Exception ex)
    { // in order to avoid throwing exceptions into the event-dispatching thread
      ex.printStackTrace(System.err);
    }
  }

//------------------------------------------------------------------------------

  public void popupMenuWillBecomeInvisible(PopupMenuEvent e)
  { try
    { Object component = e.getSource();
      if (component.equals(popupMenu)) handlePopupMenuPopupMenuWillBecomeInvisibleEvent(e);
    }
    catch (Exception ex)
    { // in order to avoid throwing exceptions into the event-dispatching thread
      ex.printStackTrace(System.err);
    }
  }

//------------------------------------------------------------------------------

  public void popupMenuCanceled(PopupMenuEvent e)
  { try
    { Object component = e.getSource();
      if (component.equals(popupMenu)) handlePopupMenuPopupMenuCanceledEvent(e);
    }
    catch (Exception ex)
    { // in order to avoid throwing exceptions into the event-dispatching thread
      ex.printStackTrace(System.err);
    }
  }

//==============================================================================
// Implementing the TreeSelectionListener Interface ...
//------------------------------------------------------------------------------

  public void valueChanged(TreeSelectionEvent e)
  { try
    { Object component = e.getSource();
      if (component.equals(selectionTree)) handleSelectionTreeValueChangedEvent(e);
    }
    catch (Exception ex)
    { // in order to avoid throwing exceptions into the event-dispatching thread
      ex.printStackTrace(System.err);
    }
  }

//==============================================================================
// Implementing the WindowListener Interface ...
//------------------------------------------------------------------------------

  public void windowOpened(WindowEvent e)
  { try
    { Object component = e.getSource();
      if (component.equals(xmlTreeView)) handleXmlTreeViewWindowOpenedEvent(e);
    }
    catch (Exception ex)
    { // in order to avoid throwing exceptions into the event-dispatching thread
      ex.printStackTrace(System.err);
    }
  }

//------------------------------------------------------------------------------

  public void windowActivated(WindowEvent e)
  { try
    { Object component = e.getSource();
      if (component.equals(xmlTreeView)) handleXmlTreeViewWindowActivatedEvent(e);
    }
    catch (Exception ex)
    { // in order to avoid throwing exceptions into the event-dispatching thread
      ex.printStackTrace(System.err);
    }
  }

//------------------------------------------------------------------------------

  public void windowDeactivated(WindowEvent e)
  { try
    { Object component = e.getSource();
      if (component.equals(xmlTreeView)) handleXmlTreeViewWindowDeactivatedEvent(e);
    }
    catch (Exception ex)
    { // in order to avoid throwing exceptions into the event-dispatching thread
      ex.printStackTrace(System.err);
    }
  }

//------------------------------------------------------------------------------

  public void windowClosing(WindowEvent e)
  { try
    { Object component = e.getSource();
      if (component.equals(xmlTreeView)) handleXmlTreeViewWindowClosingEvent(e);
    }
    catch (Exception ex)
    { // in order to avoid throwing exceptions into the event-dispatching thread
      ex.printStackTrace(System.err);
    }
  }

//------------------------------------------------------------------------------

  public void windowClosed(WindowEvent e)
  { try
    { Object component = e.getSource();
      if (component.equals(xmlTreeView)) handleXmlTreeViewWindowClosedEvent(e);
    }
    catch (Exception ex)
    { // in order to avoid throwing exceptions into the event-dispatching thread
      ex.printStackTrace(System.err);
    }
  }

//------------------------------------------------------------------------------

  public void windowIconified(WindowEvent e)
  { try
    { Object component = e.getSource();
      if (component.equals(xmlTreeView)) handleXmlTreeViewWindowIconifiedEvent(e);
    }
    catch (Exception ex)
    { // in order to avoid throwing exceptions into the event-dispatching thread
      ex.printStackTrace(System.err);
    }
  }

//------------------------------------------------------------------------------

  public void windowDeiconified(WindowEvent e)
  { try
    { Object component = e.getSource();
      if (component.equals(xmlTreeView)) handleXmlTreeViewWindowDeiconifiedEvent(e);
    }
    catch (Exception ex)
    { // in order to avoid throwing exceptions into the event-dispatching thread
      ex.printStackTrace(System.err);
    }
  }

//==============================================================================
// Customize event-handling within the following methods ...
//------------------------------------------------------------------------------

  void initialize(File f) throws ServiceException
  { // System.out.println("initialize()");
  }

//==============================================================================
// ActionListener event handling ...
//------------------------------------------------------------------------------

  void handleCopyMenuItemActionPerformedEvent(ActionEvent e) throws Exception
  { // System.out.println("handleCopyMenuItemActionPerformedEvent");
  }

  void handleExitMenuItemActionPerformedEvent(ActionEvent e) throws Exception
  { // System.out.println("handleExitMenuItemActionPerformedEvent");
  }

  void handleOpenMenuItemActionPerformedEvent(ActionEvent e) throws Exception
  { // System.out.println("handleOpenMenuItemActionPerformedEvent");
  }

//==============================================================================
// KeyListener event handling ...
//------------------------------------------------------------------------------

  void handleXmlTextAreaKeyTypedEvent(KeyEvent e) throws Exception
  { // System.out.println("handleXmlTextAreaKeyTypedEvent");
  }

//------------------------------------------------------------------------------

  void handleXmlTextAreaKeyPressedEvent(KeyEvent e) throws Exception
  { // System.out.println("handleXmlTextAreaKeyPressedEvent");
  }

//------------------------------------------------------------------------------

  void handleXmlTextAreaKeyReleasedEvent(KeyEvent e) throws Exception
  { // System.out.println("handleXmlTextAreaKeyReleasedEvent");
  }

//==============================================================================
// MenuListener event handling ...
//------------------------------------------------------------------------------

  void handleFileMenuMenuSelectedEvent(MenuEvent e) throws Exception
  { // System.out.println("handleFileMenuMenuSelectedEvent");
  }

//------------------------------------------------------------------------------

  void handleFileMenuMenuDeselectedEvent(MenuEvent e) throws Exception
  { // System.out.println("handleFileMenuMenuDeselectedEvent");
  }

//------------------------------------------------------------------------------

  void handleFileMenuMenuCanceledEvent(MenuEvent e) throws Exception
  { // System.out.println("handleFileMenuMenuCanceledEvent");
  }

//==============================================================================
// PopupMenuListener event handling ...
//------------------------------------------------------------------------------

  void handlePopupMenuPopupMenuWillBecomeVisibleEvent(PopupMenuEvent e) throws Exception
  { // System.out.println("handlePopupMenuPopupMenuWillBecomeVisibleEvent");
  }

//------------------------------------------------------------------------------

  void handlePopupMenuPopupMenuWillBecomeInvisibleEvent(PopupMenuEvent e) throws Exception
  { // System.out.println("handlePopupMenuPopupMenuWillBecomeInvisibleEvent");
  }

//------------------------------------------------------------------------------

  void handlePopupMenuPopupMenuCanceledEvent(PopupMenuEvent e) throws Exception
  { // System.out.println("handlePopupMenuPopupMenuCanceledEvent");
  }

//==============================================================================
// TreeSelectionListener event handling ...
//------------------------------------------------------------------------------

  void handleSelectionTreeValueChangedEvent(TreeSelectionEvent e) throws Exception
  { // System.out.println("handleSelectionTreeValueChangedEvent");
  }

//==============================================================================
// WindowListener event handling ...
//------------------------------------------------------------------------------

  void handleXmlTreeViewWindowOpenedEvent(WindowEvent e) throws Exception
  { // System.out.println("handleXmlTreeViewWindowOpenedEvent");
  }

//------------------------------------------------------------------------------

  void handleXmlTreeViewWindowActivatedEvent(WindowEvent e) throws Exception
  { // System.out.println("handleXmlTreeViewWindowActivatedEvent");
  }

//------------------------------------------------------------------------------

  void handleXmlTreeViewWindowDeactivatedEvent(WindowEvent e) throws Exception
  { // System.out.println("handleXmlTreeViewWindowDeactivatedEvent");
  }

//------------------------------------------------------------------------------

  void handleXmlTreeViewWindowClosingEvent(WindowEvent e) throws Exception
  { // System.out.println("handleXmlTreeViewWindowClosingEvent");
  }

//------------------------------------------------------------------------------

  void handleXmlTreeViewWindowClosedEvent(WindowEvent e) throws Exception
  { // System.out.println("handleXmlTreeViewWindowClosedEvent");
  }

//------------------------------------------------------------------------------

  void handleXmlTreeViewWindowIconifiedEvent(WindowEvent e) throws Exception
  { // System.out.println("handleXmlTreeViewWindowIconifiedEvent");
  }

//------------------------------------------------------------------------------

  void handleXmlTreeViewWindowDeiconifiedEvent(WindowEvent e) throws Exception
  { // System.out.println("handleXmlTreeViewWindowDeiconifiedEvent");
  }

//==== EOF =====================================================================

}


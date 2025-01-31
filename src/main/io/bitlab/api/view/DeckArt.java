/**
 * @author JohnnyTB (jtrejosb@live.com)
 * gitlab.com/rootpass | github.com/rootpasss
 *
 * Licenses GNU GPL v3.0 and Eclipse Public License 2.0
 * Date: 26/07/2023, Time: 11:02:40
 */
package io.bitlab.api.view;

import io.bitlab.api.model.RecordStore;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

public class DeckArt extends JDialog implements ActionListener {
  private JPanel contentPane;
  private JPanel btnPane;
  private JButton btnOk;
  private JButton btnCan;
  private String deckName="zArt1";
  private static int[]val={1,2,3,4,5,6,7,10,14,15,17,20};
  private int index;
  private int tmp;

  public DeckArt() {
    setTitle("Select Card Back");
    setSize(350,269);
    setResizable(false);
    setLocationRelativeTo(null);
    setModalityType(ModalityType.APPLICATION_MODAL);

    contentPane=new JPanel(null);
    contentPane.getInputMap(contentPane.WHEN_IN_FOCUSED_WINDOW)
               .put(KeyStroke.getKeyStroke("ESCAPE"),"close");
    contentPane.getInputMap(contentPane.WHEN_IN_FOCUSED_WINDOW)
               .put(KeyStroke.getKeyStroke("LEFT"),"move_left");
    contentPane.getInputMap(contentPane.WHEN_IN_FOCUSED_WINDOW)
               .put(KeyStroke.getKeyStroke("RIGHT"),"move_right");
    contentPane.getInputMap(contentPane.WHEN_IN_FOCUSED_WINDOW)
               .put(KeyStroke.getKeyStroke("UP"),"move_vert");
    contentPane.getInputMap(contentPane.WHEN_IN_FOCUSED_WINDOW)
               .put(KeyStroke.getKeyStroke("DOWN"),"move_vert");
    contentPane.getActionMap().put("close",new StrokeAction(0));
    contentPane.getActionMap().put("move_left",new StrokeAction(1));
    contentPane.getActionMap().put("move_right",new StrokeAction(2));
    contentPane.getActionMap().put("move_vert",new StrokeAction(3));
    add(contentPane);

    btnOk=new JButton("Ok");
    btnCan=new JButton("Cancel");
    btnCan.addActionListener(this);

    btnPane=new JPanel(new FlowLayout(FlowLayout.TRAILING));
    btnPane.add(btnOk);
    btnPane.add(btnCan);
    add(btnPane,BorderLayout.SOUTH);
    getRootPane().setDefaultButton(btnOk);

    addDecks();
  }

  public void addDeckDialogListener(ActionListener l) {
    btnOk.addActionListener(l);
  }

  public String getDeckName() {
    return deckName;
  }

  public static String getDeckName(int index) {
    return "zArt"+val[index];
  }

  public int getDeckIndex() {
    for(int i=0;i<contentPane.getComponentCount();i++)
      if(((Deck)contentPane.getComponent(i)).isSelected())
        return i;
    return 0;
  }

  private void addDecks() {
    String[]names={"Pattern1","Pattern2","Fishes","Aquarium","FlowerBlack","FlowerBlue",
                   "Robot","Roses","Shell","Castle","PalmBeach","CardHand"};
    int x=14;int y=14;
    for(int i=0;i<12;i++) {
      Deck deck=new Deck("zArt"+val[i]+".png");
      deck.setToolTipText(names[i]);
      deck.setBounds(x,y,50,86);
      deck.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent evt) {
          clearBorder();
          deck.setSelected(true);
          deckName=deck.getName();
        }
      });
      contentPane.add(deck);
      x=x==284?14:x+54;
      y=i>4?104:y;
    }
    RecordStore rs=RecordStore.openRecordStore();
    index=rs.getRecord()[0];tmp=index;
    ((Deck)contentPane.getComponent(index)).setSelected(true);
    deckName=((Deck)contentPane.getComponent(index)).getName();
  }

  private void clearBorder() {
    for(int i=0;i<contentPane.getComponentCount();i++)
      ((Deck)contentPane.getComponent(i)).setSelected(false);
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    if(evt.getSource()==btnCan) {
      clearBorder();
      ((Deck)contentPane.getComponent(tmp)).setSelected(true);
      deckName=((Deck)contentPane.getComponent(tmp)).getName();
      dispose();
    }
  }

  protected class StrokeAction extends AbstractAction {
    private int opt;
    public StrokeAction(int opt) {
      this.opt=opt;
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
      if(opt>0) {
        index=getDeckIndex();
        if(opt==1)
          index=index>0?index-1:contentPane.getComponentCount()-1;
        else if(opt==2)
          index=index<11?index+1:0;
        else
          index=index-6<0?index-6*-1:index-6;
      } else {
        index=tmp;
        dispose();
      }
      clearBorder();
      ((Deck)contentPane.getComponent(index)).setSelected(true);
      deckName=((Deck)contentPane.getComponent(index)).getName();
    }
  }
}

class Deck extends JLabel {
  private Image image;
  private boolean state;

  public Deck(String name) {
    setName(name);
    setOpaque(true);
    setBackground(Color.WHITE);
    image=new ImageIcon(getClass().getResource("/suits/"+name)).getImage();
  }

  public void setSelected(boolean state) {
    this.state=state;
    renderDeck();
  }

  public void renderDeck() {
    setBackground(state?Color.BLUE.darker():Color.WHITE);
    repaint();
  }

  public boolean isSelected() {
    return state;
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2=(Graphics2D)g;
    g2.setRenderingHints(new RenderingHints(RenderingHints.KEY_INTERPOLATION,
          RenderingHints.VALUE_INTERPOLATION_BILINEAR));
    g2.drawImage(image,2,2,46,82,null,null);
  }
}

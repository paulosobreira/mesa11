/**
 * @version 1.00 1999-09-11
 * @author Cay Horstmann
 */

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

public class CompositeTest
{  public static void main(String[] args)
   {  JFrame frame = new CompositeTestFrame();
      frame.show();
   }
}

class CompositeTestFrame extends JFrame
   implements ActionListener, ChangeListener
{  public CompositeTestFrame()
   {  setTitle("CompositeTest");
      setSize(400, 400);
      addWindowListener(new WindowAdapter()
         {  public void windowClosing(WindowEvent e)
            {  System.exit(0);
            }
         } );

      Container contentPane = getContentPane();
      canvas = new CompositePanel();
      contentPane.add(canvas, "Center");

      ruleCombo = new JComboBox();
      ruleCombo.addItem("CLEAR");
      ruleCombo.addItem("SRC");
      ruleCombo.addItem("SRC_OVER");
      ruleCombo.addItem("DST_OVER");
      ruleCombo.addItem("SRC_IN");
      ruleCombo.addItem("SRC_OUT");
      ruleCombo.addItem("DST_IN");
      ruleCombo.addItem("DST_OUT");
      ruleCombo.addActionListener(this);

      alphaSlider = new JSlider();
      alphaSlider.addChangeListener(this);
      JPanel panel = new JPanel();
      panel.add(ruleCombo);
      panel.add(new JLabel("Alpha"));
      panel.add(alphaSlider);
      contentPane.add(panel, "North");

      explanation = new JTextField();
      contentPane.add(explanation, "South");

      canvas.setAlpha(alphaSlider.getValue());
      canvas.setRule(ruleCombo.getSelectedItem());
      explanation.setText(canvas.getExplanation());
   }

   public void stateChanged(ChangeEvent event)
   {  canvas.setAlpha(alphaSlider.getValue());
   }

   public void actionPerformed(ActionEvent event)
   {  canvas.setRule(ruleCombo.getSelectedItem());
      explanation.setText(canvas.getExplanation());
   }

   private CompositePanel canvas;
   private JComboBox ruleCombo;
   private JSlider alphaSlider;
   private JTextField explanation;
}

class CompositePanel extends JPanel
{  public CompositePanel()
   {  shape1 = new Ellipse2D.Double(100, 100, 150, 100);
      shape2 = new Rectangle2D.Double(150, 150, 150, 100);
   }

   public void paintComponent(Graphics g)
   {  super.paintComponent(g);
      Graphics2D g2 = (Graphics2D)g;

      BufferedImage image = new BufferedImage(getWidth(),
         getHeight(), BufferedImage.TYPE_INT_ARGB);
      Graphics2D gImage = image.createGraphics();
      gImage.setPaint(Color.red);
      gImage.fill(shape1);
      AlphaComposite composite
         = AlphaComposite.getInstance(rule, alpha);
      gImage.setComposite(composite);
      gImage.setPaint(Color.blue);
      gImage.fill(shape2);
      g2.drawImage(image, null, 0, 0);
   }

   public void setRule(Object r)
   {  if (r.equals("CLEAR"))
      {  rule = AlphaComposite.CLEAR;
         porterDuff1 = "  ";
         porterDuff2 = "  ";
      }
      else if (r.equals("SRC"))
      {  rule = AlphaComposite.SRC;
         porterDuff1 = " S";
         porterDuff2 = " S";
      }
      else if (r.equals("SRC_OVER"))
      {  rule = AlphaComposite.SRC_OVER;
         porterDuff1 = " S";
         porterDuff2 = "DS";
      }
      else if (r.equals("DST_OVER"))
      {  rule = AlphaComposite.DST_OVER;
         porterDuff1 = " S";
         porterDuff2 = "DD";
      }
      else if (r.equals("SRC_IN"))
      {  rule = AlphaComposite.SRC_IN;
         porterDuff1 = "  ";
         porterDuff2 = " S";
      }
      else if (r.equals("SRC_OUT"))
      {  rule = AlphaComposite.SRC_OUT;
         porterDuff1 = " S";
         porterDuff2 = "  ";
      }
      else if (r.equals("DST_IN"))
      {  rule = AlphaComposite.DST_IN;
         porterDuff1 = "  ";
         porterDuff2 = " D";
      }
      else if (r.equals("DST_OUT"))
      {  rule = AlphaComposite.DST_OUT;
         porterDuff1 = "  ";
         porterDuff2 = "D ";
      }
      repaint();
   }

   public void setAlpha(int a)
   {  alpha = (float)a / 100.0F;
      repaint();
   }

   public String getExplanation()
   {  String r = "Source ";
      if (porterDuff2.equals("  "))
         r += "clears";
      if (porterDuff2.equals(" S"))
         r += "overwrites";
      if (porterDuff2.equals("DS"))
         r += "blends with";
      if (porterDuff2.equals(" D"))
         r += "alpha modifies";
      if (porterDuff2.equals("D "))
         r += "alpha complement modifies";
      if (!porterDuff2.equals("DD"))
      {  r += " destination";
         if (!porterDuff1.equals("  ")) r += " and ";
      }
      if (porterDuff1.equals(" S"))
         r += "overwrites";
      if (!porterDuff1.equals("  "))
         r += " empty pixels";
      return r + ".";
   }

   private Shape shape1;
   private Shape shape2;
   private float alpha;
   private int rule;
   private String porterDuff1; // row 1 of the rule diagram
   private String porterDuff2; // row 2 of the rule diagram
}

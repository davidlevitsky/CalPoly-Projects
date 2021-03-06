/* Garrett Milster */

import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;
import javax.swing.*;
import java.io.File;
import java.util.Scanner;
import java.util.ArrayList;

public class Maze extends Applet
{
   JPanel panel;
   private int[][] array = new int[10][10];
   private JTextField tField;
   private int r,c;
   private boolean started, first, error;
   private boolean start, end;
   private MazeCanvas maze;
   JPanel mazePanel;
   private int height, width, index1, index2;
   Graph graph;
   private JButton go;
   private JLabel label;
   int[][] path;
   
   public Maze()
   {
      
   }
   public void init()
   {
      maze = new MazeCanvas();
      ButtonActionListener baListener = new ButtonActionListener();
      JPanel buttonPanel = new JPanel();
      buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
      
      panel = new JPanel();
      panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
      
      mazePanel = new JPanel();
      mazePanel.setLayout(new BoxLayout(mazePanel, BoxLayout.Y_AXIS));
      mazePanel.add(maze);
      
      label = new JLabel("No Possible Paths");
      
      tField = new JTextField("", 20);
      tField.addActionListener(baListener); // text and radio buttons add listener as usual
      go = new JButton("GO");
      go.addActionListener(baListener);
      
      MouseEventListener mouseListener = new MouseEventListener();
      maze.addMouseListener(mouseListener);
      
      buttonPanel.add(tField);
      buttonPanel.add(go);
      
      panel.add(buttonPanel);
      panel.add(mazePanel);
      add(panel);
      maze.paint(maze.getGraphics());
      
   }
   private class ButtonActionListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {

           Object source = event.getSource();

           if (source == tField) 
           { 
              // called when user hits return in text field
              String temp1 = tField.getText();
                File file = new File(temp1);
                 r = 0;
                 c = 0;
                 start = false;
                 end = false;
                try
                {
                   //Scans in the words from the file passed in as an argument in the command line.
                   Scanner in = new Scanner(file);
                   while (in.hasNextInt()) 
                   {  
                      if(r == 0)
                      {
                         r = in.nextInt(); 
                      }
                      else if(c == 0)
                      {
                         c = in.nextInt();
                      }
                      else
                      {
                         array = new int[r][c];
                         for (int i=0; i<r; i++) 
                         {
                            for (int j=0; j<c; j++) 
                            {
                               array[i][j] = in.nextInt();
                            }
                         }
                         maze.rows = r;
                         maze.cols = c;
                         started = true;
                         mazePanel.remove(label);
                         maze.paint(maze.getGraphics());
                         
                      }
                   }
                      in.close();
                }
                catch(java.io.FileNotFoundException e)
                {

                }
                
                graph = new Graph(array, r, c);
                first = false;

            }
            else if(source == go)
            {
               int[][] path = graph.shortestPath(index1, index2);
               if(first == false)
               {
                  //if(start == true && end == true)
                  
                  for(int i = 1; i < path.length-1; i++)
                  {
                     int temp1 = path[i][0];
                     int temp2 = path[i][1];
                     array[temp1][temp2] = 3;
                  }
                  maze.paint(maze.getGraphics());
                  first = true;
               }
               else
               {
                  java.util.Random rand = new java.util.Random();
                  
                  for(int i = 1; i < path.length -1; i++)
                   {
                       int temp1 = path[i][0];
                       int temp2 = path[i][1];
                        array[temp1][temp2] = 0;
                   }
                   
                   int next = path.length -2;
                   int nextr = path[next][0];
                   int nextc = path[next][1];
                   array[nextr][nextc] = 1;
                   //index1 = (nextr * c) + nextc;
                   int random = 1;
                   if(path.length < 3)
                   {
                      
                   }
                   else
                   {
                     random = rand.nextInt(path.length - 3);
                   } 
                   int randr  = path[random+1][0];
                   int randc = path[random+1][1];
                   array[randr][randc] = -1;
                   graph = new Graph(array, r, c);
                   path = graph.shortestPath(index1, index2);
                   
                   if(path == null)
                   {
                      error = true;
                      mazePanel.add(label);
                   }
                   else
                   {
                      for(int i = 1; i < path.length-1; i++)
                      {
                        int temp1 = path[i][0];
                        int temp2 = path[i][1];
                        array[temp1][temp2] = 3;
                      }
                   }
                  maze.paint(maze.getGraphics());
               }
            }
        }

     }
     
     private class MouseEventListener implements MouseListener {
        public void mouseClicked(MouseEvent e) 
        {
            int startX = e.getX();
            int startY = e.getY();
            int col = (startX/width);
            int row = (startY/height);
            if(array[row][col] != -1)
            {
            
               if(start == false)
               {
                  start = true;
                  array[row][col] = 1;
                  index1 = (row * c) + col;
                  maze.paint(maze.getGraphics());
                              }
               else if(end == false)
               {
                  end = true;
                  array[row][col] = 2;
                  index2 = (row * c) + col;
                  maze.paint(maze.getGraphics());
               
               }
            }
        
        }
        public void mousePressed(MouseEvent e) { }
        public void mouseReleased(MouseEvent e) { }
        public void mouseEntered(MouseEvent e) { }
        public void mouseExited(MouseEvent e) { }
     }

   private class MazeCanvas extends Canvas 
   {
      // this class paints the window 
      int rows = 10;
      int cols = 10;
      MazeCanvas()
      {
         resize(cols*50+1, rows*50);
         setBackground(Color.white);
      }
      MazeCanvas(int r, int c) 
      {
         rows = r;
         cols = c;
         resize(cols*50+1, rows*50);
         setBackground(Color.white);
      }
      
      public void paint(Graphics g)
      {
         resize(cols*50+1, rows*50);
         setBackground(Color.white);
         g.setColor(Color.white);
         g.fillRect(0, 0, rows, cols);
         if(started == false)
         {
            for (int i=0; i<10; i++) {
                  array[0][i] = -1;
                  array[9][i] = -1;
                  array[i][0] = -1;
                  array[i][9] = -1;
               }
         }
         
            height = 50;
            width = 50;
            
            for (int i=0; i<rows; i++) 
            {
               for (int j=0; j<cols; j++) 
               {
                  if (array[i][j] == -1)
                  {
                     g.setColor(Color.black);
                  }
                  else if( array[i][j] == 0)
                  {
                     g.setColor(Color.white);
                  }
                  else if(array[i][j] == 1)
                  {
                     g.setColor(Color.green);
                  }
                  else if(array[i][j] == 2)
                  {
                     g.setColor(Color.red);
                  }
                  else if(array[i][j] == 3)
                  {
                     g.setColor(Color.yellow);
                  }
                  g.fillRect(j*height, i*width, width, height);
               }
            }
       }
   }
}
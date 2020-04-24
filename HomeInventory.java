import java.awt.Button;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.filechooser.*;

import com.toedter.calendar.*;
import java.io.*;
import java.util.Date;
import java.text.DecimalFormat;
import java.io.*;

public class HomeInventory{

	JFrame frame;
	JToolBar toolbar;
	JMenu menu;
    JButton New,Delete,Save,prev,next,print,exit;
    JTextArea a;
	static JTextArea pic;
    JLabel inventory_item,location,slNumber,price,store,note,photo,date;
    JTextField itemname,itemnumber,itemprice,itemsource,itemnote;
     JComboBox itemlocation;
     JDateChooser datechooser;
    JButton photobutton;
    JPanel searchpanel; 
    JCheckBox markedCheckBox;
     JButton[] searchalpha=new JButton[26];
     final static int maximumEntries=300;
     static int numberEntries;
     static InventoryItem[] myitem=new InventoryItem[maximumEntries];
     
     PhotoPanel photopanel=new PhotoPanel();
     int currentEntry;
     static final int entriesperpage=2;
     static int lastpage;
	
	HomeInventory() throws NumberFormatException, IOException
	{
		frame=new JFrame("Home Inventory Manager");
		
	
		GridBagConstraints gridConstraints;
		frame.pack();
		Dimension screeSize=Toolkit.getDefaultToolkit().getScreenSize();
		//frame.setBounds(400,400,400,400);
		frame.setBounds((int)(0.5*(screeSize.width-frame.getWidth())),(int)(0.5*(screeSize.height-frame.getHeight())),frame.getWidth(),frame.getHeight());
		frame.setVisible(true);
		frame.setLayout(new GridBagLayout());
		
		toolbar=new JToolBar();
		gridConstraints=new GridBagConstraints();
		gridConstraints.gridx=0;
		gridConstraints.gridy=0;
		toolbar.setOrientation(SwingConstants.VERTICAL);
	    toolbar.setBackground(Color.BLUE);
		gridConstraints.gridheight=8;
		gridConstraints.fill=GridBagConstraints.VERTICAL;
		toolbar.setFloatable(false);
		//toolbar.setVisible(true);
		frame.getContentPane().add(toolbar,gridConstraints);
		//Dimension bsize=new Dimension(70,50);
		New=new JButton();
		ImageIcon img=new ImageIcon(new ImageIcon("New.JPG").getImage().getScaledInstance(70,50,Image.SCALE_DEFAULT));
		New.setIcon(img);
		New.setText("new");
		//New.setEnabled(true);
	     New.setFocusable(false);
		New.setHorizontalTextPosition(SwingConstants.CENTER);
		New.setVerticalTextPosition(SwingConstants.BOTTOM);
		New.setToolTipText("Add new item");
		toolbar.add(New);
		
		New.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				checkSave();
				blankValue();
				
			}
			
		});
		
		Delete=new JButton();
		ImageIcon imgdel=new ImageIcon(new ImageIcon("del.JPG").getImage().getScaledInstance(70,50,Image.SCALE_DEFAULT));
		Delete.setText("delete");
		Delete.setIcon(imgdel);
		Delete.setHorizontalTextPosition(SwingConstants.CENTER);
		Delete.setVerticalTextPosition(SwingConstants.BOTTOM);
		Delete.setToolTipText("Delete item");
		toolbar.add(Delete);
		Delete.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(JOptionPane.showConfirmDialog(null,"Are you sure you want to deletethis item?","delete Inventory",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE)==JOptionPane.NO_OPTION)
				return;
				deleteEntry(currentEntry);
				if(numberEntries==0)
				{
					currentEntry=0;
					blankValue();
				}
				else
				{
					currentEntry--;
					if(currentEntry==0)
						currentEntry=1;
					showEntry(currentEntry);
				}
				
			}
			
		});
		
		Save=new JButton();
		ImageIcon imgsave=new ImageIcon(new ImageIcon("Save.PNG").getImage().getScaledInstance(70,50,Image.SCALE_DEFAULT));
		Save.setText("save");
		Save.setIcon(imgsave);
		Save.setHorizontalTextPosition(SwingConstants.CENTER);
		Save.setVerticalTextPosition(SwingConstants.BOTTOM);
		Save.setToolTipText("Save item");
		toolbar.add(Save);
		
		Save.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				itemname.setText(itemname.getText().trim());
				if(itemname.getText().equals(" "))
				{
					JOptionPane.showConfirmDialog(null,"Must have item description","Error",JOptionPane.DEFAULT_OPTION,JOptionPane.ERROR_MESSAGE);
				    itemname.requestFocus();return;	
				}
				if(New.isEnabled())
				{
					deleteEntry(currentEntry);
				}
				String s=itemname.getText();
				itemname.setText(s.substring(0,1).toUpperCase()+s.substring(1));
				numberEntries++;
				currentEntry=1;
				if(numberEntries!=1)
				{
					do {
						if(itemname.getText().toString().compareTo(myitem[currentEntry-1].description)<0)
							break;
						currentEntry++;
					}while(currentEntry<numberEntries);
					
				}
				if(currentEntry!=numberEntries) {
					for(int i=numberEntries;i>=currentEntry+1;i--)
					{
						myitem[i-1]=myitem[i-2];
						myitem[i-2]=new InventoryItem();
					}
				}
				myitem[currentEntry-1]=new InventoryItem();
				myitem[currentEntry-1].description=itemname.getText();
				myitem[currentEntry-1].location=itemlocation.getSelectedItem().toString();
				myitem[currentEntry-1].marked=markedCheckBox.isSelected();
				myitem[currentEntry-1].serialNumber=itemnumber.getText();
				myitem[currentEntry-1].purchasePrice=itemprice.getText();
				myitem[currentEntry-1].purchaseDate=dateToString(datechooser.getDate());
				
				myitem[currentEntry-1].purchasedLocation=itemsource.getText();
				myitem[currentEntry-1].photoFile=pic.getText();
				myitem[currentEntry-1].note=itemnote.getText();
				showEntry(currentEntry);
				if(numberEntries<maximumEntries)
				{
					New.setEnabled(true);
					
				}else
				{
					New.setEnabled(false);
					Delete.setEnabled(true);
					print.setEnabled(true);
				}
			}
			
		});
		
		toolbar.addSeparator();
		toolbar.addSeparator();
		
		prev=new JButton();
		ImageIcon imgprev=new ImageIcon(new ImageIcon("prev.JPG").getImage().getScaledInstance(70,50,Image.SCALE_DEFAULT));
		prev.setText("Previous");
		prev.setIcon(imgprev);
		prev.setHorizontalTextPosition(SwingConstants.CENTER);
		prev.setVerticalTextPosition(SwingConstants.BOTTOM);
		prev.setToolTipText("Preious item");
		toolbar.add(prev);
		prev.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				checkSave();
				currentEntry--;
				showEntry(currentEntry);
				
			}
			
		});
		
		
		next=new JButton();
		ImageIcon imgnext=new ImageIcon(new ImageIcon("next.PNG").getImage().getScaledInstance(70,50,Image.SCALE_DEFAULT));
		next.setText("Next");
		next.setIcon(imgnext);
		next.setHorizontalTextPosition(SwingConstants.CENTER);
	    next.setVerticalTextPosition(SwingConstants.BOTTOM);
		next.setToolTipText("Next item");
		toolbar.add(next);
		
		next.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				checkSave();
				currentEntry++;
				showEntry(currentEntry);
				
			}
			
		});
		
		toolbar.addSeparator();
		toolbar.addSeparator();
		
		print=new JButton();
		ImageIcon imgprint=new ImageIcon(new ImageIcon("print.JPG").getImage().getScaledInstance(70,50,Image.SCALE_DEFAULT));
		print.setText("Print");
		print.setIcon(imgprint);
		print.setHorizontalTextPosition(SwingConstants.CENTER);
	    print.setVerticalTextPosition(SwingConstants.BOTTOM);
		print.setToolTipText("Print item");
		toolbar.add(print);
		
		print.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				lastpage=(int)(1+(numberEntries-1)/entriesperpage);
				PrinterJob inventoryPrinterjob=PrinterJob.getPrinterJob();
				inventoryPrinterjob.setPrintable(new InventoryDocument());
				if(inventoryPrinterjob.printDialog())
				{
					try {
						inventoryPrinterjob.print();
					}catch(PrinterException t){
						JOptionPane.showConfirmDialog(null,t.getMessage(),"Print Error",JOptionPane.DEFAULT_OPTION,JOptionPane.ERROR_MESSAGE);
					}
				}
				
			}
			
		});
		
		exit=new JButton();
		ImageIcon imgexit=new ImageIcon(new ImageIcon("exit.JPG").getImage().getScaledInstance(70,50,Image.SCALE_DEFAULT));
		exit.setText("Exit");
		exit.setIcon(imgexit);
		exit.setHorizontalTextPosition(SwingConstants.CENTER);
	    exit.setVerticalTextPosition(SwingConstants.BOTTOM);
		exit.setToolTipText("Exit");
		toolbar.add(exit);
		exit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(JOptionPane.showConfirmDialog(null,"Any unsaved item will be lost.\nAre you sure you want to exit?","Exit Program",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE)==JOptionPane.NO_OPTION)
				return;
				try {
					PrintWriter outputFile=new PrintWriter(new BufferedWriter(new FileWriter("inventory.text")));
				    outputFile.println(numberEntries);
				    if(numberEntries==0)
				    {
				    	for(int i=0;i<numberEntries;i++)
				    	{
				    		outputFile.println(myitem[i].description);
				    		outputFile.println(myitem[i].location);
				    		outputFile.println(myitem[i].serialNumber);
				    		outputFile.println(myitem[i].marked);
				    		outputFile.println(myitem[i].purchasePrice);
				    		outputFile.println(myitem[i].purchaseDate);
				    		outputFile.println(myitem[i].purchasedLocation);
				    		outputFile.println(myitem[i].note);
				    		outputFile.println(myitem[i].photoFile);
				    	}
				    }
				
				outputFile.println(itemlocation.getItemCount());
				if(itemlocation.getItemCount()!=0)
				{
					for(int i=0;i<itemlocation.getItemCount();i++)
					{
						outputFile.println(itemlocation.getItemAt(i));
					}
				}
				outputFile.close();
				}
				catch(Exception et)
				{
					
				}
				System.exit(0);
			}
			
		});
		
		inventory_item=new JLabel("Inventory Item");
		gridConstraints=new GridBagConstraints();
		gridConstraints.gridx=1;
		gridConstraints.gridy=0;
		gridConstraints.insets=new Insets(10,10,10,10);
		gridConstraints.anchor=GridBagConstraints.EAST;
	    frame.getContentPane().add(inventory_item,gridConstraints);
	    
	    itemname=new JTextField();
	    itemname.setPreferredSize(new Dimension(400,25));
	    gridConstraints.gridx=2;
		gridConstraints.gridy=0;
		gridConstraints.gridwidth=5;
		gridConstraints.insets=new Insets(10,0,0,10);
		gridConstraints.anchor=GridBagConstraints.WEST;
		frame.getContentPane().add(itemname,gridConstraints);
		
		itemname.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				itemlocation.requestFocus(true);
				
			}
			
		});
		
		
		location=new JLabel("Loaction");
		gridConstraints=new GridBagConstraints();
		gridConstraints.gridx=1;
		gridConstraints.gridy=1;
		gridConstraints.insets=new Insets(10,10,0,10);
		gridConstraints.anchor=GridBagConstraints.EAST;
	    frame.getContentPane().add(location,gridConstraints);
	    
	    
	    
	    Font font=new Font("Arial",Font.PLAIN,12);
	    
	   itemlocation=new JComboBox();
	   itemlocation.setSize(270,25);
	   itemlocation.setFont(font);
	   itemlocation.setEditable(true);
	   itemlocation.setBackground(Color.WHITE);
	   gridConstraints=new GridBagConstraints();
		gridConstraints.gridx=2;
		gridConstraints.gridy=1;
		gridConstraints.gridwidth=3;
	    gridConstraints.insets=new Insets(10,0,0,10);
		gridConstraints.anchor=GridBagConstraints.WEST;
	    frame.getContentPane().add(itemlocation,gridConstraints);
	    
	    itemlocation.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(itemlocation.getItemCount()!=0)
				{
					for(int i=0;i<itemlocation.getItemCount();i++)
					{
						if(itemlocation.getSelectedItem().toString().equals(itemlocation.getItemAt(i).toString())) {
							itemnumber.requestFocus();
							return;
						}
					}
				}
				itemlocation.addItem(itemlocation.getSelectedItem());
				itemnumber.requestFocus();
			}
	    	
	    });
	    
	    
	    
	    slNumber=new JLabel("Serial Number");
		gridConstraints=new GridBagConstraints();
		gridConstraints.gridx=1;
		gridConstraints.gridy=2;
		gridConstraints.insets=new Insets(10,10,0,10);
		gridConstraints.anchor=GridBagConstraints.EAST;
	    frame.getContentPane().add(slNumber,gridConstraints);
	    
	    itemnumber=new JTextField();
	    itemnumber.setPreferredSize(new Dimension(400,25));
	    gridConstraints.gridx=2;
		gridConstraints.gridy=2;
		gridConstraints.gridwidth=3;
		gridConstraints.insets=new Insets(10,0,0,10);
		gridConstraints.anchor=GridBagConstraints.WEST;
	    frame.getContentPane().add(itemnumber,gridConstraints);
	    
	    itemnumber.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				itemprice.requestFocus(true);
				
			}
	    	
	    });
	    
	    
	    price =new JLabel("Purchace Price");
	    gridConstraints=new GridBagConstraints();
		gridConstraints.gridx=1;
		gridConstraints.gridy=3;
		gridConstraints.insets=new Insets(10,10,0,10);
		gridConstraints.anchor=GridBagConstraints.EAST;
	    frame.getContentPane().add(price,gridConstraints);
	    
	    itemprice=new JTextField();
	    itemprice.setPreferredSize(new Dimension(160,25));
	    gridConstraints.gridx=2;
		gridConstraints.gridy=3;
		gridConstraints.gridwidth=1;
		gridConstraints.insets=new Insets(10,0,0,10);
		gridConstraints.anchor=GridBagConstraints.WEST;
	    frame.getContentPane().add(itemprice,gridConstraints);
	    
	    itemprice.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
			datechooser.requestFocus(true);
				
			}
	    	
	    });
	    
	    
		date=new JLabel("Date Purchased");
		gridConstraints=new GridBagConstraints();
		gridConstraints.gridx=4;
		gridConstraints.gridy=3;
		gridConstraints.insets=new Insets(10,10,0,0);
		gridConstraints.anchor=GridBagConstraints.EAST;
	   frame.getContentPane().add(date,gridConstraints);
	    
	    datechooser=new JDateChooser();
	  datechooser.setPreferredSize(new Dimension(120,25));
	    gridConstraints.gridx=5;
		gridConstraints.gridy=3;
		gridConstraints.gridwidth=2;
		gridConstraints.insets=new Insets(10,0,0,10);
		gridConstraints.anchor=GridBagConstraints.WEST;
		frame.getContentPane().add(datechooser,gridConstraints);
		
		datechooser.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				itemsource.requestFocus();
				
			}
			
		});
		
		store=new JLabel("Store/Website");
		gridConstraints=new GridBagConstraints();
		gridConstraints.gridx=1;
		gridConstraints.gridy=4;
		gridConstraints.insets=new Insets(10,10,0,0);
		gridConstraints.anchor=GridBagConstraints.EAST;
	    frame.getContentPane().add(store,gridConstraints);
	    
	    itemsource=new JTextField();
	    itemsource.setPreferredSize(new Dimension(400,25));
	    gridConstraints.gridx=2;
		gridConstraints.gridy=4;
		gridConstraints.gridwidth=5;
		gridConstraints.insets=new Insets(10,0,0,10);
		gridConstraints.anchor=GridBagConstraints.WEST;
		frame.getContentPane().add(itemsource,gridConstraints);
		
		itemsource.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				itemnote.requestFocus(true);
				
			}
			
		});
		
		markedCheckBox=new JCheckBox("Marked?");
		markedCheckBox.setFocusable(false);
		gridConstraints=new GridBagConstraints();
		gridConstraints.gridx=5;
		gridConstraints.gridy=1;
		gridConstraints.insets=new Insets(10,10,0,0);
		gridConstraints.anchor=GridBagConstraints.WEST;
		frame.getContentPane().add(markedCheckBox,gridConstraints);
		
		
		note=new JLabel("Note");
		gridConstraints=new GridBagConstraints();
		gridConstraints.gridx=1;
		gridConstraints.gridy=5;
		gridConstraints.insets=new Insets(10,10,0,10);
		gridConstraints.anchor=GridBagConstraints.EAST;
	    frame.getContentPane().add(note,gridConstraints);
	    
	    itemnote=new JTextField();
	    itemnote.setPreferredSize(new Dimension(400,25));
	    gridConstraints.gridx=2;
		gridConstraints.gridy=5;
		gridConstraints.gridwidth=5;
		gridConstraints.insets=new Insets(10,0,0,10);
		gridConstraints.anchor=GridBagConstraints.WEST;
		frame.getContentPane().add(itemnote,gridConstraints);
		
		itemnote.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				photobutton.requestFocus();
				
			}
			
		});
	    
		
		photo=new JLabel("Photo");
		gridConstraints=new GridBagConstraints();
		gridConstraints.gridx=1;
		gridConstraints.gridy=6;
		gridConstraints.insets=new Insets(10,10,0,10);
		gridConstraints.anchor=GridBagConstraints.EAST;
	   frame.getContentPane().add(photo,gridConstraints);
		
	   
	   pic=new JTextArea();
	 pic.setPreferredSize(new Dimension(400,25));
	   pic.setFont(font);
	   pic.setEditable(false);
       pic.setLineWrap(true);
       pic.setWrapStyleWord(true);
       pic.setBackground(new Color(255,255,192));
       //add border
       
       gridConstraints.gridx=2;
		gridConstraints.gridy=6;
		gridConstraints.insets=new Insets(10,0,0,10);
		gridConstraints.anchor=GridBagConstraints.WEST;
	   frame.getContentPane().add(pic,gridConstraints); 
		
	   
	   photobutton=new JButton("...");
	   gridConstraints.gridx=6;
		gridConstraints.gridy=6;
		gridConstraints.insets=new Insets(10,0,0,10);
		gridConstraints.anchor=GridBagConstraints.WEST;
	   frame.getContentPane().add(photobutton,gridConstraints); 
	   
	   photobutton.addActionListener(new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent et) {
			photobuttonAction(et);
		}
		   
	   });

	   searchpanel=new JPanel();
	   searchpanel.setPreferredSize(new Dimension(240,160));
	   searchpanel.setBorder(BorderFactory.createTitledBorder("Search Item"));
	   searchpanel.setLayout(new GridBagLayout());
	   gridConstraints=new GridBagConstraints();
	   gridConstraints.gridx=1;
		gridConstraints.gridy=7;
		gridConstraints.gridwidth=3;
		gridConstraints.insets=new Insets(10,0,10,10);
		gridConstraints.anchor=GridBagConstraints.CENTER;
	   frame.getContentPane().add(searchpanel,gridConstraints);
	   
	   
	 //  searchalpha=new JButton[26];
	  
	   int k=0;
	   int j=0;
	  // gridConstraints=new GridBagConstraints();
	   for(int i=0;i<26;i++)
	   {
		   searchalpha[i]=new JButton();
			  
			   searchalpha[i].setText(String.valueOf((char)(65+i)));
			   searchalpha[i].setBackground(Color.YELLOW);
			   searchalpha[i].setMargin(new Insets(-10,-10,-10,-10));
			   searchalpha[i].setFont(new Font("Arial",Font.BOLD,12));
			   searchalpha[i].setPreferredSize(new Dimension(37,27));
			  searchalpha[i].setVisible(true);
			  
			   gridConstraints=new GridBagConstraints();
			   gridConstraints.gridx=k;
				gridConstraints.gridy=j;
				gridConstraints.gridwidth=1;
			   searchpanel.add(searchalpha[i],gridConstraints);
			   
			   searchalpha[i].addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					searchbuttonAction(e);
					
				}
				  
			   });
				
				++k;
				if(k%6==0) {
					j++;
					k=0;
		          }
				
				
				//System.out.println(i);
	   }
	   
	   
	   
	   
	   
	  
	 //  frame.getContentPane().add(searchpanel,gridConstraints);
	   photopanel.setPreferredSize(new Dimension(240,160));
	   gridConstraints=new GridBagConstraints();
	   gridConstraints.gridx=4;
		gridConstraints.gridy=7;
		gridConstraints.gridwidth=3;
		gridConstraints.insets=new Insets(10,0,10,10);
		gridConstraints.anchor=GridBagConstraints.CENTER;
	   frame.getContentPane().add(photopanel,gridConstraints);
	   
	  // frame.getContentPane().add(searchpanel,gridConstraints);
	   
		frame.pack();
		 screeSize=Toolkit.getDefaultToolkit().getScreenSize();
		//frame.setBounds(400,400,400,400);
		frame.setBounds((int)(0.5*(screeSize.width-frame.getWidth())),(int)(0.5*(screeSize.height-frame.getHeight())),frame.getWidth(),frame.getHeight());
	 int n;
	 try {
	 BufferedReader br=new BufferedReader(new FileReader("inventory.txt"));
	 numberEntries=Integer.parseInt(br.readLine());
	 if(numberEntries!=0)
	 {
		 for(int i=0;i<numberEntries;i++)
		 {
			 myitem[i]=new InventoryItem();
			 myitem[i].description=br.readLine();
			 myitem[i].location=br.readLine();
			 myitem[i].serialNumber=br.readLine();
			 myitem[i].marked=Boolean.parseBoolean(br.readLine());
			 myitem[i].purchaseDate=br.readLine();
			 myitem[i].purchasedLocation=br.readLine();
			 myitem[i].note=br.readLine();
			 myitem[i].photoFile=br.readLine();
			 
		 }
	 }
	 n=Integer.parseInt(br.readLine());
	 if(n!=0)
	 {
		 for(int i=0;i<n;i++)
		 {
			 itemlocation.addItem(br.readLine());
		 }
	 }
	 br.close();
	 currentEntry=1;
	 showEntry(currentEntry);
	 }
	 catch(Exception e)
	 {
		 
		 numberEntries=0;
		 currentEntry=0;
	 }
		if(numberEntries==0)
		{
			New.setEnabled(false);
			Delete.setEnabled(false);
			next.setEnabled(false);
			prev.setEnabled(false);
			print.setEnabled(false);
		}
		
		
	}
	public void photobuttonAction(ActionEvent et)
	{
		JFileChooser chooser=new JFileChooser();
		chooser.setDialogType(JFileChooser.OPEN_DIALOG);
		chooser.setDialogTitle("Open Photo File");
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("Photo Files","jpg"));	
       int r=chooser.showOpenDialog(inventory_item);
       if(r==JFileChooser.APPROVE_OPTION)
    	   showPhoto(chooser.getSelectedFile().toString());
       
	   // if(chooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION)
	    //	showPhoto(chooser.getSelectedFile().toString());
	}
	public void searchbuttonAction(ActionEvent e)
	{
		int i;
		if(numberEntries==0)
		{
			return;
		}
		String letter=e.getActionCommand();
		i=0;
		do {
			if(myitem[i].description.substring(0,1).equals(letter)) {
				currentEntry=i+1;
				showEntry(currentEntry);
				return;
			}
			i++;
			
		}
		while(i<numberEntries);
		JOptionPane.showConfirmDialog(null,"No"+letter+"inventory item","None Found",JOptionPane.DEFAULT_OPTION,JOptionPane.INFORMATION_MESSAGE);
	}
	private void showEntry(int j)
	{
		itemname.setText(myitem[j-1].description);
		itemlocation.setSelectedItem(myitem[j-1].location);
		itemsource.setText(myitem[j-1].purchasedLocation);
		itemnumber.setText(myitem[j-1].serialNumber);
		markedCheckBox.setSelected(myitem[j-1].marked);
		datechooser.setDate(StringToDate(myitem[j-1].purchaseDate));
	   itemprice.setText(myitem[j-1].purchasePrice);
	   itemnote.setText(myitem[j-1].note);
	   showPhoto(myitem[j-1].photoFile);
	   next.setEnabled(true);
	   prev.setEnabled(true);
	   if(j==1)
		   prev.setEnabled(false);
	   if(j==numberEntries)
		   next.setEnabled(false);
	   itemname.requestFocus();
	
	   
		
	}
	@SuppressWarnings("deprecation")
	public Date StringToDate(String s)
	{
		int m=Integer.valueOf(s.substring(0,2)).intValue()-1;
		int d=Integer.valueOf(s.substring(3,5)).intValue();
		int y=Integer.valueOf(s.substring(6)).intValue()-1990;
		return(new Date(y,m,d));
    
	
	}
	@SuppressWarnings("deprecation")
	public String dateToString(Date date2)
	{
		String y=String.valueOf(date2.getYear()+1990);
		int m=date2.getMonth()+1;
		String mon=new DecimalFormat("00").format(m);
		int d=date2.getDate();
		String date=new DecimalFormat("00").format(d);
		return(mon+"/"+date+"/"+y);
	}
	public void showPhoto(String photoFile)
	{
		if(!photoFile.equals(" "))
		{
			try {
				pic.setText(photoFile);
			}
			catch(Exception e)
			{
				pic.setText(" ");
			}
		}
		else
		{
			pic.setText(" ");
		}
		PhotoPanel pr=new PhotoPanel();
		pr.repaint();
	}
	private void blankValue()
	{
		New.setEnabled(false);
		Delete.setEnabled(false);
		Save.setEnabled(true);
		prev.setEnabled(false);
		
		next.setEnabled(false);
		print.setEnabled(false);
		itemname.setText(" ");
		itemlocation.setSelectedItem(" ");
		markedCheckBox.setSelected(false);
		itemnumber.setText(" ");
		itemprice.setText(" ");
		itemnote.setText(" ");
		datechooser.setDate(new Date());
		itemsource.setText(" ");
		PhotoPanel p=new PhotoPanel();
		p.repaint();
		pic.setText(" ");
		itemname.requestFocus();
		
		
	}
	public void deleteEntry(int j)
	{
		if(j!=numberEntries)
		{
			for(int i=j;i<numberEntries;i++)
			{
				myitem[i-1]=new InventoryItem();
				myitem[i-1]=myitem[i];
			}
		}numberEntries--;
	}
	
	public void checkSave()
	{
		boolean edited=false;
		if(myitem[currentEntry-1].description.equals(itemname.getText())) 
			edited=true;
			else if(!myitem[currentEntry-1].location.equals(itemlocation.getSelectedItem().toString()))
             {
				edited=true;
			}
			else if(myitem[currentEntry-1].marked!=markedCheckBox.isSelected())
				edited=true;
			else if(!myitem[currentEntry-1].serialNumber.equals(itemnumber.getText()))
					edited=true;
			else if(!myitem[currentEntry-1].purchasePrice.equals(itemprice.getText()))
				edited=true;
			else if(!myitem[currentEntry-1].purchaseDate.equals(dateToString(datechooser.getDate())))
                 edited=true;
			else if(!myitem[currentEntry-1].purchasedLocation.equals(itemsource.getText()))	
				edited=true;
			else if(!myitem[currentEntry-1].note.equals(itemnote.getText()))
                 edited=true;
			else if(!myitem[currentEntry-1].photoFile.equals(pic.getText()))
				edited=true;
		
		if(edited)
		{
			if(JOptionPane.showConfirmDialog(null,"You have edited this item.Do you want to save the changes?","Save Item",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE)==JOptionPane.YES_OPTION)
				Save.doClick();
		}
	}
	public static void main(String args[])
	{
		try {
			HomeInventory h=new HomeInventory();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

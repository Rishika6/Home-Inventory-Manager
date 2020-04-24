import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Line2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

import javax.swing.ImageIcon;

public class InventoryDocument implements Printable
{
	
	@Override
	public int print(Graphics g, PageFormat pageFormat, int pageIndex) throws PrinterException {
		Graphics2D g2d=(Graphics2D)g;
		if((pageIndex+1)>HomeInventory.lastpage)
		{
			return (int)NO_SUCH_PAGE;
		}
		int i,iEnd;
		g2d.setFont(new Font("Arial",Font.BOLD,14));
		g2d.drawString("Home Inventory Items-Page"+String.valueOf(pageIndex+1),(int)pageFormat.getImageableX(),(int)(pageFormat.getImageableY()+25));
	    //get starting y
		int dy=(int)g2d.getFont().getStringBounds("S",g2d.getFontRenderContext()).getHeight();
		int y=(int)(pageFormat.getImageableY()+4*dy);
		iEnd=HomeInventory.entriesperpage*(pageIndex+1);
		if(iEnd>HomeInventory.numberEntries)
			iEnd=HomeInventory.numberEntries;
		for( i=0+HomeInventory.entriesperpage*pageIndex;i<iEnd;i++)
		{
			Line2D.Double dividingLine=new Line2D.Double(pageFormat.getImageableX(),y,pageFormat.getImageableWidth(),y);
			g2d.draw(dividingLine);
			y=y+dy;
			g2d.setFont(new Font("Arial",Font.BOLD,12));
			g2d.drawString(HomeInventory.myitem[i].description,(int)pageFormat.getImageableX(), y);
			y=y+dy;
			g2d.setFont(new Font("Arial",Font.PLAIN,12));
			g2d.drawString("Location:"+HomeInventory.myitem[i].location,(int)(pageFormat.getImageableX()+25),y);
			y=y+dy;
			if(HomeInventory.myitem[i].marked)
				g2d.drawString("Item is marked with identifying information",(int)(pageFormat.getImageableX()+25),y);
			else
				g2d.drawString("Item is Not marked with identifying information",(int)(pageFormat.getImageableX()+25),y);
			y+=dy;
			g2d.drawString("Serial Number:"+HomeInventory.myitem[i].serialNumber,(int)(pageFormat.getImageableX()+25), y);
		   y+=dy;
		   
		   g2d.drawString("Price:$"+HomeInventory.myitem[i].purchasePrice+"Purchased on:"+HomeInventory.myitem[i].purchaseDate,(int)(pageFormat.getImageableX()+25), y);
		   y+=dy;
		   g2d.drawString("Purchased at:"+HomeInventory.myitem[i].purchasedLocation,(int)(pageFormat.getImageableX()+25), y);
		   y+=dy;
		   g2d.drawString("Note:"+HomeInventory.myitem[i].note,(int)(pageFormat.getImageableX()+25), y);
		   y+=dy;
		   try {
			   Image inventoryImage=new ImageIcon(HomeInventory.myitem[i].photoFile).getImage();
		       double ratio=(double)(inventoryImage.getWidth(null))/(double)(inventoryImage.getHeight(null));
		        g2d.drawImage(inventoryImage,(int)(pageFormat.getImageableX()+25),y,(int)(100*ratio),100,null);
		        // sx2, sy2, observer)
		   }
		   catch(Exception e)
		   {
			   
		   }
		   y+=2*dy+100;
		
			
			
		}
		return (int)PAGE_EXISTS;
	
	
	}

}

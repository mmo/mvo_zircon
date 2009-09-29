package com.rero.ch.pdfrenderer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PagePanel;

public class MyPDFViewerApplet extends JApplet implements MouseListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6739773322404923459L;
	/** The page display */
	PagePanel page;
	/** The current page number (starts at 0), or -1 if no page */
	int curpage = -1;
	/** The current PDFFile */
	PDFFile curFile;
	/** a thread that pre-loads the next page for faster response */
	PagePreparer pagePrep;
	/** the name of the current document */
	String docName;
	double originalWidth;
	double originalHeight;
	
	JScrollPane jscp;
	String url;
	
	private Dimension area; //indicates area taken up by graphics
	private Dimension test;
	
	String[] lf;
	String tested;

	public String[] getFilenames() 
	{
		if (lf != null && lf.length > 0)
		{
			return lf;
		}
		else
		{
			return null;
		}
	}

	
	/**
	 * A filename filter for PDF filenames.
	 */
	FilenameFilter pdfFilenamefFilter = new FilenameFilter() 
	{
		public boolean accept(File f, String name) 
		{
			return name.endsWith(".pdf");
	    }
	
		public String getDescription() 
		{
			return "Choose a PDF file";
		}
	};


	
	/**
	 * Initialize this PDFViewer by creating the GUI.
	 */
	public void init()
	{
		System.out.println("Initialize the MyPDFViewer");
		
//		File f = new File (this.getCodeBase().getFile());
//		
//		tested = this.getParameter("tested");
//		
//		System.out.println("Test : " + tested);
//		
//		if (tested != null && tested.equals("1"))
//		{
//			lf = f.list(pdfFilenamefFilter);
//		
//			for (int i = 0 ; i < lf.length ; i++)
//			{
//				System.out.println("i : " + i + ", value : "+ lf[i]);
//			}
//		}

		url = this.getParameter("url");
		System.out.println("url : " + url);
		if (url == null)
		{
			System.out.println("Unable to initialize the applet : there is no PDF document !!!");
		}
		else
		{
			area = new Dimension(0,0);
			test = new Dimension(0,0);
	 		
			page = new PagePanel();
			page.addMouseListener(this);
		
			jscp = new JScrollPane(page);
			
			jscp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			jscp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	
			getContentPane().add(jscp, BorderLayout.CENTER);
			
			if (SwingUtilities.isEventDispatchThread())
			{
				setVisible(true);
			} 
			else 
			{
				try
				{
					SwingUtilities.invokeAndWait(new Runnable()
					{
						public void run()
						{
							setVisible(true);
						}
					});
				} 
				catch (InvocationTargetException ie) 
				{
					// ignore
				} catch (InterruptedException ie) 
				{
					// ignore
				}
			}
			
			//String url = "ThinkingInPostScript.pdf";
			if(url != null)
			{
				System.out.println("URL : " + url);
				this.doOpen(url);
			}
			
			// Keep original size
			originalHeight = getPageHeight();
			originalWidth = getPageWidth();
			
			page.setPreferredSize(new Dimension((int)originalWidth,(int)originalHeight));
			
			System.out.println("End of initialize MyPDFViewer");
		}
	}
	
	public static String backlashReplace(String myStr){
	    final StringBuilder result = new StringBuilder();
	    final StringCharacterIterator iterator = new StringCharacterIterator(myStr);
	    char character =  iterator.current();
	    while (character != CharacterIterator.DONE ){
	     
	      if (character == '\\') {
	         result.append("/");
	      }
	       else {
	        result.append(character);
	      }

	      
	      character = iterator.next();
	    }
	    return result.toString();
	  }
	
    public void mouseClicked(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
    public void mousePressed(MouseEvent e){}

	
	/**
	 * Open a local file, given a string filename
	 * @param name the name of the file to open
	 */
	public boolean doOpen(String url)
	{
		try
		{
			//System.out.println("CodeBase : " + this.getCodeBase() + ", url : " + url);
			//return openURL(new URL(this.getCodeBase(), url));
			return openURL(new URL(url));
		} 	
		catch (IOException ioe) 
		{
			System.out.println("Couldn't open " + url);
			ioe.printStackTrace();
			return false;
		}
	}
	
    /**
     * Display a dialog indicating an error.
     */
    public void openError(String message) 
    {
    	JOptionPane.showMessageDialog(jscp, message,
    			"Error opening file", JOptionPane.ERROR_MESSAGE);
    }
	
	/**
	 * utility method to get an icon from the resources of this class
	 * 
	 * @param name
	 *            the name of the icon
	 * @return the icon, or null if the icon wasn't found.
	 */
	public Icon getIcon(String name)
	{
		Icon icon = null;
		URL url = null;
		try
		{
			url = ClassLoader.getSystemResource(name);

			icon = new ImageIcon(url);
			if (icon == null)
			{
				System.out.println("Couldn't find " + url);
			}
		} 
		catch (Exception e) 
		{
			System.out.println("Couldn't find " + name);
			e.printStackTrace();
		}
		return icon;
	}
	
	private boolean openURL(URL url)
	{
		boolean isOpen = true;
		
		// Work with a memory-cached byte buffer:
		ByteArrayOutputStream tmpOut = new ByteArrayOutputStream();
		URLConnection connection;
		InputStream in;
		PDFFile newfile = null;

		try
		{
			connection = url.openConnection();
			in = connection.getInputStream();
			byte[] buf = new byte[512];
			int len;
			try
			{
				while (true)
				{
					len = in.read(buf);
					if (len == -1)
					{
						break;
					}
					tmpOut.write(buf, 0, len);
				}
			}
			finally
			{
				tmpOut.close();
			}
		
			ByteBuffer bb = ByteBuffer.wrap(tmpOut.toByteArray(), 0, tmpOut.size());
			newfile = new PDFFile(bb);
		}
		catch ( FileNotFoundException fnfe )
		{
			StringBuffer msg =  new StringBuffer("'");
			msg.append(this.getCodeBase());
			msg.append(url);
			msg.append("' doesn't exist !!");
			System.out.println(msg.toString());
			fnfe.printStackTrace();
			openError(msg.toString());
			return !isOpen;
		}
		catch (Exception e)
		{
			System.out.println("Couldn't openURL " + url.getPath());
			e.printStackTrace();
			return !isOpen;
		}
		
		// set up our document
		this.curFile = newfile;
		docName = url.getFile();
		
		System.out.println("Nb pages : " + newfile.getNumPages());
		
		int i = 100;
		int timer = 4000;
		while (page.getCurSize() == null && i > 0)
		{
			System.out.println("iter : " + (i--));
			try 
			{
				Thread.sleep(timer);
			} 
			catch (InterruptedException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			gotoPage(0);
		}
		if (i == 0)
		{
			return !isOpen;
		}
		else
		{
			return isOpen;
		}
	}
	
	//Handle mouse events.
    public void mouseReleased(MouseEvent e) {
        boolean changed = false;
        if (SwingUtilities.isRightMouseButton(e)) 
        {
            area.width=0;//(int) getOriginalWidth();
            area.height=0;//(int) getOriginalHeight();
            changed = true;
        } 

        if (changed) {
            //Update client's preferred size because
            //the area taken up by the graphics has
            //gotten larger or smaller (if cleared).
            page.setPreferredSize(area);
        	//page.setPreferredSize(new Dimension(10,10));

            //Let the scroll pane know to update itself
            //and its scrollbars.
            page.revalidate();
        }
        page.repaint();
        setEnabling();
    }
	
	/**
	 * Goes to the first page
	 */
	public int doFirst()
	{
		gotoPage(0);
		return 1;
	}
	
	/**
	 * Goes to the last page
	 */
	public int doLast()
	{
		gotoPage(curFile.getNumPages() - 1);
		return curFile.getNumPages();
	}
	
	/**
	 * Goes to the next page
	 */
	public int doNext()
	{
		System.out.println("Call doNext");
		gotoPage(curpage + 1);
		return curpage+1;
	}
	
	/**
	 * Goes to the previous page
	 */
	public int doPrev()
	{
		System.out.println("Call doPrev");
		gotoPage(curpage - 1);
		return curpage+1;
	}
	
	/**
	 * Get the pdfFile.
	 * 
	 * @since 2009-06-02
	 */
	public void getPDFFile()
	{
		System.out.println("Get pdfFile");
	}
	
	/**
	 * Do Zoom.
	 * 
	 * @since 2009-06-09
	 */
	public void doZoom(double factor)
	{
		if ( factor < 0.5 || factor > 1.5)
		{
			System.out.println("Range : 0.5 <= factor <= 1.5");
			return;
		}
		else
		{
			if ( factor == 1 )
			{
		        Rectangle rect = new Rectangle(0, 0, (int)originalWidth, (int)originalHeight);
		        page.scrollRectToVisible(rect);
		        test.width = (int) originalWidth;
		        test.height = (int) originalHeight;
		        page.setSize(test);
		        page.setPreferredSize(test);
		        page.setBounds(rect);
		        page.getCurSize().setSize(test);
			}
			else
			{
				double wd = getPageWidth();//Math.min(dim.width,getPageWidth());
				System.out.println("wd = " + wd);
		        int w = (int) (wd * factor);
		        System.out.println("w = "+w);
		        if (w > 2000) 
		        {
		           	System.out.println("> maxWidth");
		            return;
		        }
		        if (w < 100) 
		        {
		           	System.out.println("< minWidth");
		            return;
		        }
		        double hg = getPageHeight();//Math.min(dim.height,getPageHeight());
		        System.out.println("hg = " + hg);
		        int h = (int) (hg * factor);
		        System.out.println("h = "+h);
		        
		        Rectangle rect = new Rectangle(0, 0, w, h);
		        page.scrollRectToVisible(rect);
		        test.width = w;
		        test.height = h;
		        
	        	page.setSize(test);
	        	page.setPreferredSize(test);
	        	page.setBounds(rect);
	        	page.getCurSize().setSize(test);
			}
	        page.revalidate();
	        page.repaint();
	        
	        setEnabling();
		}
    }
	
	/**
	 * 
	 * Get the number of pages.
	 * 
	 * @return Return the number of pages.
	 */
	public int getNbPages()
	{
		System.out.println("Number of pages : " + curFile.getNumPages());
		return curFile.getNumPages();
	}
	
	
	public double getOriginalWidth ()
	{
		return originalWidth;
	}
	
	public void setOriginalWidth (double origWidth)
	{
		originalWidth = origWidth;
	}
	
	public double getOriginalHeight ()
	{
		return originalHeight;
	}
	
	public void setOriginalHeight (double origHeight)
	{
		originalHeight = origHeight;
	}
	
	public double getPageHeight ()
	{
		if (page == null)
		{
			System.out.println("No height !!");
			return 800;
		}
		if (page.getCurSize() == null)
		{
			System.out.println("Height 1 : " + page.getHeight());
			return page.getHeight();
		}
		else
		{
			System.out.println("Height 2 : " + page.getCurSize().getHeight());
			return page.getCurSize().getHeight();
		}
	}
	
	public double getPageWidth ()
	{
		if (page == null)
		{
			System.out.println("No width !!");
			return 800;
		}
		if (page.getCurSize() == null)
		{
			System.out.println("Width 1 : " + page.getWidth());
			return page.getWidth();
		}
		else
		{
			System.out.println("Width 2 : " + page.getCurSize().getWidth());
			return page.getCurSize().getWidth();
		}
	}
	
	/**
	 * Changes the displayed page, desyncing if we're not on the
	 * same page as a presenter.
	 * @param pagenum the page to display
	 */
	public void gotoPage(int pagenum)
	{
		if (pagenum < 0)
		{
			pagenum = 0;
		} 
		else if (pagenum >= curFile.getNumPages())
		{
			pagenum = curFile.getNumPages() - 1;
		}
		forceGotoPage(pagenum);
	}

	/**
	 * Changes the displayed page.
	 * @param pagenum the page to display
	 */
	private void forceGotoPage(int pagenum)
	{
		System.out.println("View page : " + (pagenum+1));
		
		curpage = pagenum;
		
		PDFPage pg = curFile.getPage(pagenum + 1);
		
		/**
		PDFPage pdfPage = getPDFfile().getPage(page);
		rectangle = new Rectangle(0, 0, (int) pdfPage.getBBox().getWidth(), (int) pdfPage.getBBox().getHeight());
		Image img = pdfPage.getImage(rectangle.width, rectangle.height, rectangle, null, true, true);
		JLabel(new ImageIcon(img)); 
		**/
		
		page.showPage(pg);
		page.requestFocusInWindow();
		
		// stop any previous page prepaper, and start a new one
		if (pagePrep != null)
		{
			pagePrep.quit();
		}
		System.out.println("pagenum : " + pagenum);
		if (pagenum + 1 < getNbPages())
		{
			
			System.out.println("PREPARE NEXT PAGE");
			pagePrep = new PagePreparer(pagenum);
			pagePrep.start();
		}
		setEnabling();
	}
	
	/**
	 * Enable or disable all of the actions based on the current state.
	 */
	public void setEnabling()
	{
		boolean fileavailable = curFile != null;
		boolean pageshown = page.getPage() != null;
		
		System.out.println("Fileavailable : " + fileavailable);
		System.out.println("Pageshown : " + pageshown);
		
		repaint();
	}
	
	/**
	 * A class to pre-cache the next page for better UI response
	 */
	class PagePreparer extends Thread
	{
		int waitforPage;
		int prepPage;

		/**
		 * Creates a new PagePreparer to prepare the page after the current
		 * one.
		 * @param waitforPage the current page number, 0 based 
		 */
		public PagePreparer(int waitforPage)
		{
			setDaemon(true);

			this.waitforPage = waitforPage;
			this.prepPage = waitforPage + 1;
		}

		public void quit()
		{
			waitforPage = -1;
		}
		
		public void run()
		{
			Dimension size = null;
			Rectangle2D clip = null;
			
			// wait for the current page
			page.waitForCurrentPage();
			size = page.getCurSize();
			clip = page.getCurClip();
			
			if (waitforPage == curpage)
			{
				PDFPage pdfPage = curFile.getPage(prepPage + 1, true);
				if (pdfPage != null && waitforPage == curpage)
				{
					pdfPage.getImage(size.width, size.height, clip, null, true, true);
				}
			}
		}
	}
}

package net.crowdflow;

import javax.swing.ImageIcon;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JTextField;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import nl.redcode.iphone.Tracker;
import javax.swing.JLabel;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileOutputStream;
import java.util.zip.GZIPOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.LinkedList;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;

public class CrowdFlow extends JFrame {
	private static Tracker tracker = null;
	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JTextField jTextFieldToFile = null;
	private JButton jButtonExport = null;
	
	private JLabel jLabelHeader = null;
	private JLabel jLabelStep1 = null;
	private JLabel jLabelToFile = null;
	private JLabel jLabelFinished = null;
	private JLabel jLabelBusy = null;
	private JProgressBar jProgressBar = null;
	private Color colorActive = new Color(95, 28, 0);
	private Color colorInactive = new Color(255,255,255);
	
	private File directory = null;
	/**
	 * This method initializes jContentPane	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.setBackground(Color.white);
			
			jLabelHeader = new JLabel(new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/header.png"))));
						
			jLabelStep1 = new JLabel();
			jLabelStep1.setFont(new Font("Dialog", Font.BOLD, 18));
			jLabelStep1.setPreferredSize(new Dimension(640, 60));
			jLabelStep1.setText("Let's export your iPhone/iPad backup to ...");
			jLabelStep1.setForeground(colorActive);

			jButtonExport = new JButton();
			jButtonExport.setText("Export");
			jButtonExport.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					jProgressBar.setValue(0);
					jProgressBar.setMaximum(directory.listFiles().length*6);
					
					jLabelBusy.setVisible(true);
					jButtonExport.setEnabled(false);
			
					jLabelToFile.setForeground(colorInactive);
					jTextFieldToFile.setEnabled(false);
					
					(new ExecuteExport()).start();
				}
			});
			
			jLabelBusy = new JLabel(new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/progress.gif"))));
			jLabelBusy.setVisible(false);
			
			jLabelToFile = new JLabel();
			jLabelToFile.setFont(new Font("Dialog", Font.PLAIN, 12));
			jLabelToFile.setPreferredSize(new Dimension(640, 20));
			jLabelToFile.setText("Target file name:");
			
			jTextFieldToFile = new JTextField();
			jTextFieldToFile.setPreferredSize(new Dimension(640, 20));
			jTextFieldToFile.setHorizontalAlignment(JTextField.LEADING);
			
			jProgressBar = new JProgressBar();
			jProgressBar.setPreferredSize(new Dimension(640, 10));
			jProgressBar.setBackground(new Color(255,255,255));
			jProgressBar.setBorderPainted(false);
						
			jLabelFinished = new JLabel();
			jLabelFinished.setFont(new Font("Dialog", Font.BOLD, 18));
			jLabelFinished.setPreferredSize(new Dimension(640, 60));
			jLabelFinished.setText("Now you can upload the \"crowdflow-data.gz\".");
			jLabelFinished.setForeground(colorInactive);
			
			int i = 0;
			
			jContentPane.add(jLabelHeader,       getNewContraints(i++, 0));
			jContentPane.add(jLabelStep1,        getNewContraints(i++, 30));

			jContentPane.add(jLabelToFile,       getNewContraints(i++, 0));
			jContentPane.add(jTextFieldToFile,   getNewContraints(i++, 0));
			
			jContentPane.add(jButtonExport,      getNewContraints(i,   30));
			jContentPane.add(jLabelBusy,         getNewContraints(i++, 30, 90));			
			
			jContentPane.add(jProgressBar,       getNewContraints(i++, 30));
			
			jContentPane.add(jLabelFinished,     getNewContraints(i++, 30));
			
		}
		return jContentPane;
	}
	
	private GridBagConstraints getNewContraints(int y, int top) {
		return getNewContraints(y,top,0);
	}
	
	private GridBagConstraints getNewContraints(int y, int top, int left) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = y;		
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(top, left, 0, 0);
		return gbc;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		tracker = new Tracker();
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				CrowdFlow thisClass = new CrowdFlow();
				thisClass.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				thisClass.setVisible(true);
			}
		});
	}

	/**
	 * This is the default constructor
	 */
	public CrowdFlow() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(762, 530);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icon.png")));
		this.setContentPane(getJContentPane());
		this.setTitle("crowdflow.net - iPhone/iPad geo data extractor");
		jTextFieldToFile.setText((new File("crowdflow-data.gz")).getAbsolutePath());
		
		String os = System.getProperty("os.name");
		if(os.toLowerCase().contains("windows")) {
			directory = new File(System.getenv("APPDATA")+"\\Apple Computer\\MobileSync\\Backup\\");
		} else {
			// lets assume mac
			directory = new File(System.getProperty("user.home"), "/Library/Application Support/MobileSync/Backup");
		}
		
		SwingUtilities.invokeLater(new Runnable() {  
			public void run() {  
				jButtonExport.requestFocusInWindow();
			}
		});
	}
	
	private class ExecuteExport extends Thread {
		public void run() {

 		        List<File> dirs = new LinkedList<File>(Arrays.asList(directory.listFiles()));
			dirs.add(new File(new File(System.getProperty("user.home")), "Desktop"));
			GZIPOutputStream out = null;
			
			try {
				out = new GZIPOutputStream(new FileOutputStream(jTextFieldToFile.getText()));
				out.write(("# version: 1.3\n").getBytes());
				
				if(dirs != null) {
					for(File current:dirs) {
						if (current.isDirectory()) {
							tracker.convert(current, out, new IncreaseProgressbar());
						}
					}
				}	
				out.close();
			} catch (Exception e) {
				System.out.println(e);
			} finally {
			}
			
			FinishExport fs = new FinishExport();
			SwingUtilities.invokeLater(fs);
			
		}
	}
	
	private class FinishExport implements Runnable {
		public void run() {			
			

			jLabelBusy.setVisible(false);
			jButtonExport.setEnabled(true);
			
			jLabelToFile.setForeground(new Color(0,0,0));
			jTextFieldToFile.setEnabled(true);
			jLabelFinished.setForeground(colorActive);
			
		}
	}
	
	private class IncreaseProgressbar implements Runnable {
		public void run() {
			jProgressBar.setValue(jProgressBar.getValue()+1);			
		}
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"

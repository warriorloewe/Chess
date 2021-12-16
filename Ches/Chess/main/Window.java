package main;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

public class Window {

	protected Shell shlChess;
	private Text txt_time;
	private Text txt_increment;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Window window = new Window();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shlChess.open();
		shlChess.layout();
		while (!shlChess.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlChess = new Shell();
		shlChess.setSize(770, 442);
		shlChess.setText("Chess");
		
		Label lblTime = new Label(shlChess, SWT.NONE);
		lblTime.setBounds(266, 62, 29, 15);
		lblTime.setText("Time:");
		
		Label lblIncrement = new Label(shlChess, SWT.NONE);
		lblIncrement.setBounds(238, 95, 57, 15);
		lblIncrement.setText("Increment:");
		
		txt_time = new Text(shlChess, SWT.BORDER);
		txt_time.setBounds(310, 59, 76, 21);
		
		txt_increment = new Text(shlChess, SWT.BORDER);
		txt_increment.setBounds(310, 92, 76, 21);
		
		Menu menu = new Menu(shlChess, SWT.BAR);
		shlChess.setMenuBar(menu);
		
		MenuItem mntmSettings = new MenuItem(menu, SWT.CASCADE);
		mntmSettings.setText("Settings");
		
		Menu menu_1 = new Menu(mntmSettings);
		mntmSettings.setMenu(menu_1);
		
		MenuItem mntmDragAndDrop = new MenuItem(menu_1, SWT.CHECK);
		mntmDragAndDrop.setText("Drag and Drop");
		
		Button btnStartGame = new Button(shlChess, SWT.NONE);
		btnStartGame.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				try {
					int time = Integer.valueOf(txt_time.getText());
					int increment = Integer.valueOf(txt_increment.getText());
					System.out.println(mntmDragAndDrop.getSelection());
					new SchachFrame(time, increment, mntmDragAndDrop.getSelection());
				} catch(Exception ex) {
					System.out.println("Invalid input!");
					ex.printStackTrace();
				}
			}
		});
		btnStartGame.setBounds(310, 153, 75, 25);
		btnStartGame.setText("Start Game");
		
		Button btnExit = new Button(shlChess, SWT.NONE);
		btnExit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				System.exit(0);
			}
		});
		btnExit.setBounds(311, 193, 75, 25);
		btnExit.setText("Exit");

	}
}

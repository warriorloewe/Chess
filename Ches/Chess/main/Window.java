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
	private Text text;
	private Text text_1;

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
		lblTime.setBounds(266, 62, 55, 15);
		lblTime.setText("Time:");
		
		Label lblIncrement = new Label(shlChess, SWT.NONE);
		lblIncrement.setBounds(251, 95, 70, 15);
		lblIncrement.setText("Increment:");
		
		text = new Text(shlChess, SWT.BORDER);
		text.setBounds(351, 62, 76, 21);
		
		text_1 = new Text(shlChess, SWT.BORDER);
		text_1.setBounds(361, 89, 76, 21);
		
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
				new SchachFrame();
			}
		});
		btnStartGame.setBounds(310, 201, 75, 25);
		btnStartGame.setText("Start Game");

	}
}

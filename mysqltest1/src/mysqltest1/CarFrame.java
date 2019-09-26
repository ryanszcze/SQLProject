package mysqltest1;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

/*

The CarFrame class is an implementation of the JFrame and
also contains the main method. Usage:

> java CarFrame

Note that the file carData.txt must be in the same directory as
all compiled code.

 */

public class CarFrame extends JFrame
{
	public static JTabbedPane index;
	public static CarLot myCarLot;
	public MySQLAccess dao;

	public CarFrame(MySQLAccess database)
	{
		// setting window properties

		this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
		this.dao=database;
		this.setDefaultCloseOperation(3);
		this.setTitle("Car Park System");
		this.setResizable(false);
		myCarLot = new CarLot(dao);
		Color newColor = new Color(0.2f, 0.1f, 0.8f, 0.1f);

		// creating Car Lot object
		//myCarLot = new CarLot(10, "carData.txt");

		// building tabbed panel display
		index = new JTabbedPane();
		index.setBackground(newColor);
		final JPanel statusTab = Status.startup(dao);
		final JPanel addOrRemoveCarTab = AddOrRemoveCar.startup(dao);

		// adding tabs to tabbed panel
		index.addTab("Lot Status", statusTab);
		index.addTab("Add Or Remove Cars", addOrRemoveCarTab);

		// setting content pane
		this.getContentPane().add(index);
	}

	public static void main(String[] args)
	{	//Initialize database
		 MySQLAccess dao = new MySQLAccess();
		// initialize frame and set visible
		CarFrame main = new CarFrame(dao);
		main.setVisible(true);
	}

}

class CarLot
{
	// class variables
	private ArrayList<String> registeredDrivers;
	private ArrayList<String> parkingStalls;
	private int maxLotSize;
	private MySQLAccess dao;
	// constructor
	public CarLot(MySQLAccess database)
	{	dao=database;
		registeredDrivers = new ArrayList<String>();
		parkingStalls = new ArrayList<String>();
		//maxLotSize = maxSize;
	}

	public String getDataFileName()
	{
		return "carData.txt";
	}

	public int getMaxSize()
	{
		return maxLotSize;
	}

	public int carCount()
	{

		return parkingStalls.size();
	}

	// Input: license plate number
	// Output: Stall number in parking lot
	// Error State: returned String is ""
	public String findStallLocation(String licenseNum)
	{
		String currentStall = "";
		String returnVal = "";

		for(int i = 0; i < parkingStalls.size(); i++)
		{
			currentStall = (String)parkingStalls.get(i);

			if(licenseNum.equals(currentStall))
			{
				returnVal = Integer.toString(i);
				break;
			}
		}

		return returnVal;
	}

	// Loads data from file (maintain persistence upon close)
	public int loadData()
	{
		// each row in the data file represents a registered car

		// format:
		// licensePlateNum|currentlyParked

		FileReader file;
		BufferedReader buffer;
		StringTokenizer tokens;
		String currentLine = "";
		String licensePlate = "";
		String currentlyParked = "";

		try
		{
			file = new FileReader("carData.txt");
			buffer = new BufferedReader(file);

			// read and parse each line in the file
			while((currentLine = buffer.readLine()) != null)
			{
				int returnVal = 0;
				tokens = new StringTokenizer(currentLine, "|");
				licensePlate = tokens.nextToken();
				currentlyParked = tokens.nextToken();

				// load all registered drivers
				registeredDrivers.add(licensePlate);

				// load car into stall if status is "Y"
				if(currentlyParked.equals("Y"))
				{

					if(parkingStalls.size() < maxLotSize)
					{
						parkingStalls.add(licensePlate);
					}
					else
					{
						returnVal = -1;
					}
				}
			}
		}
		catch(FileNotFoundException f)
		{
			return -1;
		}
		catch(IOException io)
		{
			return -1;
		}

		return 0;
	}

	// Saves status data upon request to data file
	public int saveData()
	{
		String currentRecord = "";
		String licensePlate = "";
		String parkedPlates = "";

		String currentlyParked = "";

		try
		{
			FileWriter writer = new FileWriter("carData.txt");
			BufferedWriter printer = new BufferedWriter(writer);

			// build data record by parsing ArrayLists
			for(int i = 0; i < registeredDrivers.size(); i++)
			{
				writer.write(registeredDrivers.get(i));
				currentlyParked = "N";

				for(int j = 0; j < parkingStalls.size(); j++)
				{
					writer.write(parkingStalls.get(j));

					if(parkedPlates.equals(licensePlate))
					{
						currentlyParked = "Y";
						break;
					}
				}

				currentRecord = licensePlate + "|" + currentlyParked;

				//output record to file
				printer.write(currentRecord);
			}

			// close output streams
			writer.close();
			printer.close();
		}
		catch(IOException io)
		{

			return -1;
		}

		return 0;
	}

	public boolean carEnter(String licenseNum)
	{
		String parkedCar = "";
		boolean alreadyHere = false;

		// Check stalls to see if car is already parked
		for(int i = 0; i < parkingStalls.size(); i++)
		{
			parkedCar = (String)parkingStalls.get(i);

			if(parkedCar.equals(licenseNum))
			{
				alreadyHere = true;
			}
		}


		// car is not already parked
		if(!alreadyHere)
		{
			// space is still available
			if(!lotFull())
			{
				parkingStalls.add(licenseNum);
				return true;
			}
			// space not available
			else
			{
				return false;
			}
		}
		// car already parked in lot
		else
		{
			return false;
		}
	}

	public boolean carExit(String licenseNum)
	{
		boolean returnVal = false; //default return value
		String parkedCar = "";

		// searching for car in stalls

		for(int i = 0; i < parkingStalls.size(); i++)
		{
			parkedCar = (String)parkingStalls.get(i);

			// car found
			if(parkedCar.equals(licenseNum))
			{
				parkingStalls.remove(i);
				returnVal = true;
				break;
			}
		}

		return returnVal;
	}

	public boolean lotFull()
	{
		// compare stalls occupied to max lot size
		if(parkingStalls.size() == maxLotSize)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}

/*
Provides the graphical layout for the Status tab
 */
class Status
{
	public static JPanel statusTab = new JPanel();
	public static JPanel statusScreen1;
	//static MySQLAccess dao = new MySQLAccess();
	static JTextField licensePlateField = new JTextField(20);
	static MySQLAccess dao;
	// retrieves Status panel and sets visible
	static JPanel startup(MySQLAccess database)
	{	dao=database;
		statusScreen1 = Status.getStatusScreen1();
		statusTab.add(statusScreen1);

		statusScreen1.setVisible(true);

		return statusTab;

	}

	//defines and retrieves Status panel
	static JPanel getStatusScreen1()
	{
		statusScreen1 = new JPanel(new FlowLayout());
		JPanel generalPanel = new JPanel();
		generalPanel.setLayout(new BoxLayout(generalPanel, BoxLayout.Y_AXIS));

		generalPanel.add(Box.createVerticalStrut(170));

		JPanel holderPanel = new JPanel(new BorderLayout());
		holderPanel.setLayout(new BoxLayout(holderPanel,BoxLayout.Y_AXIS));
		JPanel criteriaPanel = new JPanel();
		criteriaPanel.setLayout(new BoxLayout(criteriaPanel,BoxLayout.X_AXIS));
		JLabel licensePlateLabel = new JLabel("License Plate Number:");

		Font textFont = new Font("SanSerif", Font.PLAIN, 24);
		Font textFieldFont = new Font("Serif", Font.PLAIN, 20);

		licensePlateLabel.setFont(textFont);
		licensePlateField.setFont(textFieldFont);

		criteriaPanel.add(Box.createHorizontalStrut(40));
		criteriaPanel.add(licensePlateLabel);
		criteriaPanel.add(licensePlateField);
		criteriaPanel.add(Box.createHorizontalStrut(40));

		final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.X_AXIS));
		JButton lotCapacityButton = new JButton("Check Lot Capacity");
		//JButton saveStateButton = new JButton("Save Lot State");
		JButton findStallButton = new JButton("Locate Vehicle");

		JButton clearButton = new JButton(" Clear ");

		lotCapacityButton.setFont(textFont);
		//saveStateButton.setFont(textFont);

		findStallButton.setFont(textFont);
		clearButton.setFont(textFont);

		buttonPanel.add(Box.createHorizontalStrut(10));
		buttonPanel.add(lotCapacityButton);
		//buttonPanel.add(saveStateButton);
		buttonPanel.add(findStallButton);
		buttonPanel.add(clearButton);

		holderPanel.add(criteriaPanel);
		holderPanel.add(Box.createVerticalStrut(30));
		holderPanel.add(buttonPanel);
		generalPanel.add(holderPanel);
		statusScreen1.add(generalPanel);
		statusScreen1.add(Box.createHorizontalStrut(150));

		// button listener for lot capacity
		lotCapacityButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				// Retrieve required information
				String licensePlate = licensePlateField.getText().trim();

				int totalCapacity = 0;
				try {
					totalCapacity = dao.getLotSize("lot1");
				} catch (Exception e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				int currentlyOccupied = 0;
				try {
					currentlyOccupied = dao.getLotCount("lot1");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				int freeSpace = totalCapacity - currentlyOccupied;

				// Print dialog box
				JOptionPane.showMessageDialog((Component) buttonPanel,
						"Total Capacity: " + totalCapacity +
						"\nCurrently Occupied: " + freeSpace +
						"\nFree Space: " + currentlyOccupied,
						"Current Car Lot Statistics",
						JOptionPane.INFORMATION_MESSAGE);

				// reset active tab and field data
				CarFrame.index.setSelectedIndex(0);
				licensePlateField.setText("");
			}
		});

		// button listener for save state
	/*	saveStateButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				// perform save operation
				int result = CarFrame.myCarLot.saveData();
				CarFrame.myCarLot.saveData();

				// check if successful and report results
				if(result == 0)
				{
					JOptionPane.showMessageDialog((Component) buttonPanel,
							"Data for all registered users has been updated in file: " +
									CarFrame.myCarLot.getDataFileName(),

									"Data Stored Successfully", JOptionPane.INFORMATION_MESSAGE);
				}
				else
				{
					JOptionPane.showMessageDialog((Component) buttonPanel,
							"Data could not be stored!",
							"Data Extract Failure", JOptionPane.ERROR_MESSAGE);
				}

				// reset active tab and field data
				CarFrame.index.setSelectedIndex(0);
				licensePlateField.setText("");
			}
		});
*/
		// button listener for car location search
		findStallButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				// retrieve input and perform search
				String licensePlate = licensePlateField.getText().trim();
				String stallNumber = "Lot1";

				// check operation result and report using dialog boxes
				try {
					if(!stallNumber.equals("") && !licensePlate.equals("") && dao.searchCar(licensePlate)==true)
					{
						JOptionPane.showMessageDialog((Component) buttonPanel,
								"Location of car #" + licensePlate + ":" +

"\nePark Lot: " + stallNumber,
"Car Location Found",
JOptionPane.INFORMATION_MESSAGE);
					}
					else
					{
						JOptionPane.showMessageDialog((Component) buttonPanel,
								"Location of car #" + licensePlate + ":"
										+ "Could not be found." +
										"\nThe vehicle is either not registered or not currently parked.",
										"Car Location Found", JOptionPane.ERROR_MESSAGE);
					}
				} catch (HeadlessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				// reset active tab and field data
				CarFrame.index.setSelectedIndex(0);
				licensePlateField.setText("");
			}
		});

		// button listener for clear
		clearButton.addActionListener(new ActionListener()
		{ public void actionPerformed(ActionEvent e)
		{
			// reset license plate field
			licensePlateField.setText("");
		}
		});

		return statusScreen1;

	}
}

/*
Provides the graphical layout for the Add or Remove Car tab
 */
class AddOrRemoveCar
{ 	static MySQLAccess dao;
	public static JPanel addOrRemoveCarTab = new JPanel();
	public static JPanel addOrRemoveCarScreen1;

	static JTextField licensePlateField = new JTextField(20);

	// Retrieves and returns add/remove car panel
	static JPanel startup(MySQLAccess database)
	{
		dao=database;
		addOrRemoveCarScreen1 =
	AddOrRemoveCar.getAddOrRemoveCarScreen1();
	addOrRemoveCarTab.add(addOrRemoveCarScreen1);

	addOrRemoveCarScreen1.setVisible(true);

	return addOrRemoveCarTab;
	}

	// Defines and returns graphical components for screen
	static JPanel getAddOrRemoveCarScreen1()
	{
		addOrRemoveCarScreen1 = new JPanel(new FlowLayout());
		JPanel generalPanel = new JPanel();

		generalPanel.setLayout(new BoxLayout(generalPanel,
				BoxLayout.Y_AXIS));
		generalPanel.add(Box.createVerticalStrut(170));

		JPanel holderPanel = new JPanel(new BorderLayout());
		holderPanel.setLayout(new BoxLayout(holderPanel, BoxLayout.X_AXIS));
		JPanel criteriaPanel = new JPanel(new FlowLayout());
		JLabel licensePlateLabel = new JLabel("License Plate Number:",

				SwingConstants.RIGHT);

		Font textFont = new Font("SanSerif", Font.PLAIN, 24);
		Font textFieldFont = new Font("Serif", Font.PLAIN, 20);

		licensePlateLabel.setFont(textFont);
		licensePlateField.setFont(textFieldFont);

		criteriaPanel.add(licensePlateLabel);
		criteriaPanel.add(licensePlateField);

		final JPanel buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		JButton addButton = new JButton("Add Car to Lot");
		JButton removeButton = new JButton("Remove Car from Lot");
		JButton clearButton = new JButton("Clear Data");

		addButton.setFont(textFont);
		removeButton.setFont(textFont);
		clearButton.setFont(textFont);

		buttonPanel.add(Box.createHorizontalStrut(10));
		buttonPanel.add(addButton);
		buttonPanel.add(removeButton);
		buttonPanel.add(clearButton);

		holderPanel.add(criteriaPanel);
		generalPanel.add(holderPanel);
		holderPanel.add(Box.createVerticalStrut(75));
		generalPanel.add(buttonPanel);
		addOrRemoveCarScreen1.add(generalPanel);
		addOrRemoveCarScreen1.add(Box.createHorizontalStrut(100));

		// button listener for adding car to lot
		addButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String licensePlate = licensePlateField.getText().trim();

				// check validity of input
				if((licensePlate.length() == 0))
				{
					JOptionPane.showMessageDialog((Component) buttonPanel,
							"Please fill in the field and try again",
							"Blank Field",

							JOptionPane.ERROR_MESSAGE);

				}
				else
				{
					// perform enter operation
					try {
						dao.addCar( "lot1",licensePlate);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					// check outcome and report results
				
				
						int another = JOptionPane.showConfirmDialog((Component)
								buttonPanel, "The car has been added. Add another car to the lot?",
								"Add Car",
								JOptionPane.YES_NO_OPTION);

						// reset input field
						licensePlateField.setText("");

						// change tabs based on user input
						if(another == JOptionPane.NO_OPTION)
						{
							CarFrame.index.setSelectedIndex(0);
						}

					
				}
			}
		});

		// button listener for removing car from lot
		removeButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				// retrieve input data
				String licensePlate = licensePlateField.getText().trim();

				// check data validity
				if((licensePlate.length() == 0))
				{
					// invalid
					JOptionPane.showMessageDialog((Component) buttonPanel,
							"Please fill in the field and try again",
							"Blank Field", JOptionPane.ERROR_MESSAGE);
				}
				else
				{
					// valid

					// perform exit operation
					
					try {
						dao.removeCar(licensePlate);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					// check outcome and report results

					
					
						int another = JOptionPane.showConfirmDialog((Component)
								buttonPanel, "The car has been removed. Remove another car?", "Add Car",
								JOptionPane.YES_NO_OPTION);

						licensePlateField.setText("");

						if(another == JOptionPane.NO_OPTION)
						{
							CarFrame.index.setSelectedIndex(0);
						}
					
				}
			}
		});

		// button listener for clear button
		clearButton.addActionListener(new ActionListener()
		{ public void actionPerformed(ActionEvent e)
		{
			// reset text field
			licensePlateField.setText("");

		}
		});

		return addOrRemoveCarScreen1;
	}
}
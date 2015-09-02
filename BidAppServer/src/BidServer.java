

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.List;



import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.auctionapplicationIntermed.CrudModel;
import com.example.auctionapplicationIntermed.AuctionItem;
import com.example.auctionapplicationIntermed.DateParser;
import com.example.auctionapplicationIntermed.ItemClientException;
import com.example.auctionapplicationIntermed.ItemNotFoundException;

public class BidServer{

	static HashMap<Long, AuctionItem> itemlist = new HashMap<>();
	private static File itemdb = new File("itemDB.txt");
	private static File IDdb = new File("idDB.txt");
	private static int AvailID = 0;
	private static int idReferenced;

	//showDirectory
	//File directoy = new File(".");
	//File[] list = directory.listFile();
	//File file = new File("./sdcard", "items");
	//file.createNewFile();
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		try {

			readThatLog();
			System.out.println("Just Read The Log!");
		} catch (NumberFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ItemServiceServer iss = new ItemServiceServer();
		System.out.println("Started Server.");
		try(ServerSocket ss = new ServerSocket(31415)){ // You can reserve any port

			while(true){	
				System.out.println("Welcome to the Server!");

				Socket s = ss.accept();	
				System.out.println("Accepted Connection...");
				InputStream is = s.getInputStream();
				ObjectInputStream ois = new ObjectInputStream(is);
				CrudModel command;
				if((command = (CrudModel) ois.readObject()) != null){
					System.out.println(command);

					switch(command.getCommand()){
					case UPDATEID:
						idReferenced = Integer.parseInt(command.getArgs());
						break;
					case DELETE:
						itemlist.remove(Long.valueOf(command.getArgs()));
						deleteLine(Long.valueOf(command.getArgs()));
						///
						//HI ENOCH I WILL BRB 1 MIN
						///


						//ItemServiceClient.deleteItem(Integer.valueOf(command.getArgs()));
						break;
					case ADD:
						System.out.println("ADD");
						Pattern pattern = Pattern.compile(("NAME: (\\w*) DESC: (\\w*) STARTPRICE: ([0-9]*\\.*[0-9]*) STARTDATE: ([0-9]{0,2}\\/?[0-9]{0,2}\\/?[0-9]{0,4}) ENDDATE: ([0-9]{0,2}\\/?[0-9]{0,2}\\/?[0-9]{0,4})"));
						Matcher matcher = pattern.matcher(command.getArgs());
						// price name desc id start end
						if(matcher.matches()){	
							int i = getHighestID();
							iss.addItem(new AuctionItem(BigDecimal.valueOf(Double.valueOf(matcher.group(3))), matcher.group(1), matcher.group(2), i, DateParser.parse(matcher.group(4)), DateParser.parse(matcher.group(5))));
							writeToDB(new AuctionItem(BigDecimal.valueOf(Double.valueOf(matcher.group(3))), matcher.group(1), matcher.group(2), i, DateParser.parse(matcher.group(4)), DateParser.parse(matcher.group(5))));
							System.out.println("Adding: " + matcher.group(1));
							//ItemServiceClient.addItem(new Item(organize the groups in the way you want.

						}else
							System.out.println("does not contain the pattern");
						break;
					case BID:
						Pattern pattern2 = Pattern.compile(("ID: ([0-9]*) BIDUP: ([0-9]*\\.*[0-9]*)"));
						Matcher matcher2 = pattern2.matcher(command.getArgs());

						try {
							if(matcher2.find()){
								itemBid(Long.valueOf(matcher2.group(1)), BigDecimal.valueOf(Long.valueOf((matcher2.group(2)))));
								bidLine(Long.valueOf(matcher2.group(1)), BigDecimal.valueOf(Long.valueOf((matcher2.group(2)))));
							}
							else
								System.out.println("Can not find");
						} catch (NumberFormatException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ItemClientException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						//the Args should contain the value to increase by.
						//ITemServiceClient.bid(Integer.valueOf(matcher2.group(1)),Long.valueOf(matcher2.group(2)))


						break;
					case UPDATE:
						Pattern pattern3 = Pattern.compile(("ID: ([0-9]*) NAME: (\\w*) DESC: (\\w*) STARTPRICE: ([0-9]*\\.*[0-9]*) STARTDATE: ([0-9]{0,2}\\/?[0-9]{0,2}\\/?[0-9]{0,4}) ENDDATE: ([0-9]{0,2}\\/?[0-9]{0,2}\\/?[0-9]{0,4})"));
						Matcher matcher3 = pattern3.matcher(command.getArgs());
						if(matcher3.matches()){

							//deleting
							deleteItemInList(Long.valueOf(String.valueOf(idReferenced)));
							deleteLine(idReferenced);

							//creating
							int i = getHighestID();
							iss.addItem(new AuctionItem(BigDecimal.valueOf(Double.valueOf(matcher3.group(4))), matcher3.group(2), matcher3.group(3), i, DateParser.parse(matcher3.group(5)), DateParser.parse(matcher3.group(6))));
							writeToDB(new AuctionItem(BigDecimal.valueOf(Double.valueOf(matcher3.group(4))), matcher3.group(2), matcher3.group(3), i, DateParser.parse(matcher3.group(5)), DateParser.parse(matcher3.group(6))));
							System.out.println("Adding: " + matcher3.group(1));
							//ItemServiceClient.addItem(new Item(organize the groups in the way you want.
						}
						break;
					case READ:
						System.out.println("READ");
						//hashmap w/ items from search
						//nextID int
						//getItembyID


						ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());

						oos.writeObject(iss.search(command.getArgs()));
						oos.flush();
						System.out.println("the thing wants to read");

						//						switch(command.getArgs()){
						//						case "GETHASHMAP":
						//							readThatLog();
						//							//							oos.writeObject(getSearchedItems());
						//							break;
						//						case "GETNEXTID":
						//							//sync this
						//							//							synchronize(AvailID) { 
						//							//							oos.writeObject(AvailID++);
						//							//							}
						//							break;
						//						case "GETITEMBYID":
						//							break;
						//						default:
						//							//FIx THIS
						//							break;
						//						}
					}
				}
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static int getHighestID() throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(IDdb));

		String dummy = "";
		while((dummy = br.readLine()) != null){
			System.out.println(dummy);
			if(dummy.startsWith("HIGH: ")){
				FileWriter fw = new FileWriter(IDdb);
				System.out.println(":" + dummy.substring(0, 6) + (Integer.valueOf(dummy.substring(6)) + 1));
				fw.write(dummy.substring(0, 6) + (Integer.valueOf(dummy.substring(6)) + 1));
				fw.close();
				return Integer.valueOf(dummy.substring(6));
			}
		}
		return -1;

	}

	public static void writeToDB(AuctionItem newitem) throws IOException{
		itemdb.createNewFile();
		FileWriter fw = new FileWriter(itemdb,true);
		BufferedReader br = new BufferedReader(new FileReader(itemdb));

		fw.write("\n");
		fw.write("ADD;"+ newitem.getItemID() + ";" + newitem.getName()+";"+newitem.getDescription()+";"+newitem.getBidPrice()+";"+DateParser.format(newitem.getStartDate())+";"
				+DateParser.format(newitem.getEndDate()));

		fw.close();

	}
	public static void readThatLog() throws NumberFormatException, Exception{

		System.out.println("Read Log Method");
		BufferedReader br = new BufferedReader(new FileReader(itemdb));
		br.readLine();
		while(true){
			String line = br.readLine();
			System.out.println("Line I'm on: "+line);
			if(line != null){

				String[] lines = line.split(";");
				if(lines[0].equalsIgnoreCase("add")){
					System.out.println(lines[1]);
					if(!lines[3].equals("null")){
						addItemToList(new AuctionItem(BigDecimal.valueOf((long) Double.parseDouble((lines[4]))), lines[2], lines[3], Integer.parseInt(lines[1]),
								DateParser.parse(lines[5]), DateParser.parse(lines[6])));


					}
					else
						addItemToList(new AuctionItem(BigDecimal.valueOf((long) Double.parseDouble((lines[3]))), lines[2], "", Integer.parseInt(lines[1]),
								DateParser.parse(lines[5]), DateParser.parse(lines[6])));
				}
				else if(lines[0].equalsIgnoreCase("update")){

					if(!lines[3].equals("null")){
						updateItemToList(new AuctionItem(BigDecimal.valueOf((long) Double.parseDouble((lines[4]))), lines[2], lines[3], Integer.parseInt(lines[1]),
								DateParser.parse(lines[5]), DateParser.parse(lines[6])));
					}
					else
						updateItemToList(new AuctionItem(BigDecimal.valueOf((long) Double.parseDouble((lines[4]))), lines[2], "", Integer.parseInt(lines[1]),
								DateParser.parse(lines[5]), DateParser.parse(lines[6])));
				}
				else if(lines[0].equalsIgnoreCase("delete")){

					deleteItemInList(Long.valueOf(lines[1]));
				}
				else if(lines[0].equalsIgnoreCase("bid")){
					System.out.println("ItemBidded");

					itemBid(Long.valueOf(lines[1]), BigDecimal.valueOf((long) Double.parseDouble((lines[2]))));

				}

				//ADD,1,Arush,<description>,0.01,August 18, 2015,August 25, 2015
				//call add item, also	 make the add item method reg ex through the wierd line in DBClient

			}
			else
				break;
		}
		br.close();

	}

	private static void updateItemToList(AuctionItem auctionItem) {
		itemlist.put((long) auctionItem.getItemID(), auctionItem);

	}
	private static void deleteItemInList(Long valueOf) throws ItemNotFoundException {

		if(itemlist.get(valueOf)==null){
			//null because log is ran each time the window is created
			throw new ItemNotFoundException("No Item with the selected id!?");	
		}
		itemlist.remove(valueOf);

	}
	private static void itemBid(Long id, BigDecimal bidIncrease) throws ItemClientException {


		if(itemlist.get(id).getEndDate().after(new Date())){

			itemlist.get(id).setBidPrice(itemlist.get(id).getBidPrice().add(bidIncrease));

		}
		else
			throw new ItemClientException("Can't bid on this time");


	}
	private static void addItemToList(AuctionItem auctionItem) {
		itemlist.put((long)auctionItem.getItemID(), auctionItem);

	}
	public void updateLine(AuctionItem newitem) throws IOException{
		FileWriter fw = new FileWriter(itemdb,true);
		BufferedReader br = new BufferedReader(new FileReader(itemdb));

		if(newitem.getDescription().isEmpty()){
			fw.write("UPDATE;"+ newitem.getItemID() + ";" + newitem.getName()+";"+ "null" + ";"+newitem.getBidPrice()+";"+DateParser.format(newitem.getStartDate())+";"
					+DateParser.format(newitem.getEndDate()));
		}
		else{
			fw.write("UPDATE;"+ newitem.getItemID() + ";" + newitem.getName()+";"+newitem.getDescription()+";"+newitem.getBidPrice()+";"+DateParser.format(newitem.getStartDate())+";"
					+DateParser.format(newitem.getEndDate()));
		}
		fw.write("\n");
		fw.close();
		br.close();
	}

	public static void deleteLine(long l) throws IOException {
		//take the long, check the line with the id that equals long, and replace line some how...

		BufferedReader br = new BufferedReader(new FileReader(itemdb));
		FileWriter fw = new FileWriter(itemdb,true);


		fw.write("\n");
		fw.write("DELETE;"+l);

		fw.close();
		br.close();

	}
	public static void bidLine(long id, BigDecimal bidIncrease) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(itemdb));
		FileWriter fw = new FileWriter(itemdb,true);
		fw.write("\n");
		fw.write("BID;"+id+";"+bidIncrease);
		fw.close();
		br.close();

	}




}
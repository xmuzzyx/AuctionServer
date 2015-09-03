package com.example.auctionapplication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import com.example.auctionapplicationIntermed.AuctionItem;
import com.example.auctionapplicationIntermed.CrudModel;
import com.example.auctionapplicationIntermed.CrudModel.Command;

import ItemService.ItemServiceClient;
import android.content.Intent;
import android.location.GpsStatus.Listener;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import edu.neumont.csc180.mvc.Controller;




public class SearchController extends Controller<SearchModel> implements SearchView.Listener  {

	////	AuctionItem mercedes = new AuctionItem(BigDecimal.valueOf(15000), "Mercedes Benz", "car", "A Mercedes Benz", 1 , new Date() , new Date());
	//	//	AuctionItem toaster = new AuctionItem(BigDecimal.valueOf(25), "Deluxe Toaster","toaster", "A Toaster", 2);
	//	//	AuctionItem kitchenset = new AuctionItem(BigDecimal.valueOf(100), "Kitchen Set","kitchenset", "A kitchen set", 3);
	//	//	AuctionItem sodamachine = new AuctionItem(BigDecimal.valueOf(130), "Soda Machine","sodamachine","A Soda Machine", 4);
	//
	//
	//	ArrayList<AuctionItem> allitems = new ArrayList<AuctionItem>(); //removed items, now you must add items

	HashMap<Long, AuctionItem> collect = new HashMap<>();

	ItemServiceClient isc = new ItemServiceClient();

	@Override
	public void searchDisplay(String searchfield) {

	}

	
	

	@Override
	public void onResume(){
		super.onResume();
//		searchItems("");
		
		StrictMode.ThreadPolicy.Builder stB = new StrictMode.ThreadPolicy.Builder();
		stB.permitNetwork();
		
		
		Thread i = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try(Socket s = new Socket("10.0.2.2", 31415);
						ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream())){
					
					oos.writeObject(new CrudModel(Command.READ, ""));
					oos.writeObject(new CrudModel(Command.READ, ""));
					
					oos.flush();
					

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		
		i.start();
		searchItems("");
		
		
	

	}


//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		// TODO Auto-generated method stub
//		super.onActivityResult(requestCode, resultCode, data);
//		
//		searchItems("");
//	}




	@Override
	public void productDisplayBid(double i) {
		model.setSearchedItemPrice(i);

	}
	public void itemClicked(AuctionModel model){
		Intent intent = new Intent(this, AuctionController.class); //new intent, passing in where you are going
		intent.putExtra("AuctionItem", model.getItem()); //the information you want to pass with the intent
		intent.putExtra("ViewName", "auction_controller"); // the view you want to now use
		this.startActivity(intent); //begins the new activity
	}

	public void searchItems(final String txt){
		

		//change to model  <<<<<
		System.out.println("Searching: " + txt);
					collect = ItemServiceClient.readServerMap(new CrudModel(CrudModel.Command.READ, txt));


		
		//		try {
		//			i.join();
		//		} catch (InterruptedException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
		System.out.println("Above setItems(collect)");
		model.setItems(collect);


	}
	public void setCollection(HashMap<Long, AuctionItem> collect ,HashMap<Long, AuctionItem> incomingMap){

	}

	@Override
	protected void onCreate(Bundle bundle){
		boolean b = true;
		Intent intent = getIntent();
		Bundle extra = this.getIntent().getExtras(); //Gets all the information into this bundle
		this.viewName = "activity_search";

		if(extra != null)
		{
			b = (boolean) extra.get("firstTime");

		}



		super.onCreate(bundle); 
		setContentView(); //sets the viewname and model, MVC Helper.
		//place the get Commands after you create the Connecttion here

		//this is for writing, when we dont want anything returned
		//Socket s = new Socket("10.0.2.2",31415);
		//try(ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream()))
		//	oos.write(new CrudModel(CrudModel.DELETE, //this is gthe ID "12");

		//this is for Reading which I am pretty sure it would work.
		//AuctionItem ai;
		//Socket s = new Socket("10.0.2.2",31415);
		//try(ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream()))
		//	oos.write(new CrudModel(CrudModel.RETRIEVE, //this is the ID "12");
		//	ai = oos.readObject();

		//this is for creating
		//Socket s = new Socket("10.0.2.2",31415);
		//try(ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream()))
		//	oos.write(new CrudModel(CrudModel.CREATE, //this is the information 
		//       for the new object which will align with the regex on the server "ID: 12 NAME: name DESC: desc STARTPRICE: 1.00 STARTDATE: 1/1/2000 ENDDATE: 1/2/2000 IMGREF: lkjfaewnkwan");

//		searchItems("");
		SearchView.searchButton.callOnClick();
	}

	//	private void startCheck(boolean bool) throws NumberFormatException, Exception {
	//		if(bool){
	//
	//			
	//		}
	//
	//	}


	public SearchController(){
		super(new SearchModel(), "activity_search");

	}


	@Override
	public void switchPage(String s) {
		// TODO Auto-generated method stub

	}


	@Override
	public void startItemAddPage() {
		Intent intent = new Intent(this, ItemAddController.class);
		intent.putExtra("Items", model.getItems());
		this.startActivity(intent);
	}


	@Override
	public void sendCrud(final Command add, final String string) {
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				try(
//						Socket s = new Socket("10.0.2.2", 31415);
//						ObjectOutputStream os = new ObjectOutputStream(s.getOutputStream());
//						) {
//					System.out.println("Sending Crud.");
//					os.writeObject(new CrudModel(add, string));
//					os.flush();
//
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//
//			}
//		}).start();
		ItemServiceClient.crudDispatch(new CrudModel(add,string));
	}

}

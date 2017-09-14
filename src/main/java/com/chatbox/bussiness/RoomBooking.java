package com.chatbox.bussiness;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.poi.hslf.model.Sheet;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DateUtil;

public class RoomBooking {

	public String roomBooking(String date,String stime,String etime,String bookby,String participents)throws IOException
	{
		int capacity= 0,cap= 0;
		String result="";
		String roomname="";
		HSSFWorkbook wb=null;
		boolean flag = false;
		//FileInputStream stream=null;
		HSSFRow row = null;
		InputStream stream =null;
		//InputStream stream = RoomBooking.class.getResourceAsStream("ConferenceRoomBooking.xls");
		try
		{
			/*File excel = new File("D:\\ConferenceRoomBooking.xls");
			stream = new FileInputStream(excel);*/
			stream =RoomBooking.class.getResourceAsStream("/ConferenceRoomBooking.xls");
			wb = new HSSFWorkbook(stream);
		}catch(Exception e){
			e.printStackTrace();
		}

		HSSFSheet ws = wb.getSheetAt(0);
		int rowNum = ws.getLastRowNum();
		int colNum = ws.getRow(1).getLastCellNum();
		int roomHeaderIndex = -1, capHeaderIndex = -1;
		HSSFRow rowHeader = ws.getRow(0);

		for (int j = 0; j < colNum; j++)
		{
			HSSFCell cell = rowHeader.getCell(j);
			String cellValue = cellToString(cell);

			if ("Room Name".equalsIgnoreCase(cellValue))
			{
				roomHeaderIndex = j;
				System.out.println(j);
			} else if ("Sitting Capacity".equalsIgnoreCase(cellValue))
			{
				capHeaderIndex = j;
				System.out.println(j);
			}
		}  
		 
		cap = Integer.valueOf((String)participents);
		for (int i = 2; i <=rowNum; i++)
		{
			row = ws.getRow(i);
			capacity = Integer.valueOf((String)cellToString(row.getCell(capHeaderIndex)));
			if(capacity >= cap)
			{
				roomname =cellToString(row.getCell(roomHeaderIndex));
				System.out.println("Data is Updatating..."+capacity);
				stream.close();

				result=roomBookingProcess(roomname, date, stime, etime, bookby,participents);
                 break;

			}else if(i >= rowNum)
			{ 
			    return "Sorry! Currently room is not available for "+cap+" people. please contact to admin ";
			}
		}
		return result;
	}

public static String roomBookingProcess(String roomname,String date,String stime,String etime,String bookby,String participents) throws IOException
{
	System.out.println("Room Booking is in Process");
	String dt="";
	String result="";
	
	int i=1;
	HSSFWorkbook wb=null;
	FileInputStream fis=null;
	InputStream stream =null;
	
	try{
		stream = RoomBooking.class.getResourceAsStream("/ConferenceRoomBooking.xls");
		wb = new HSSFWorkbook(stream);
		System.out.println("File Fetched");
	}catch(Exception e)
	{
		e.printStackTrace();
	}
	
	System.out.println("selected room ="+roomname);
	HSSFSheet ws = wb.getSheet(roomname);
	int rowNum = ws.getLastRowNum();
	int roomHeaderIndex = 1, capHeaderIndex = 2;
	
	System.out.println("Row num="+rowNum);
	
	while(i<=rowNum)
	{
		dt="";
		int sti=0,eti=0,usti=0,ueti=0;
		System.out.println("Checking time slot...");
		HSSFRow row = ws.getRow(i);
		i++;
		try{
			//if(null!=row.getCell(0))
			dt =cellToString(row.getCell(0));
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		System.out.println("Read Date="+cellToString(row.getCell(0)));
		System.out.println("Date ="+dt);
		
		//System.out.println("New Date ="+dt);
		/*if(dt.equals(date))
		{*/
		
		try{
			String st[]=cellToString(row.getCell(roomHeaderIndex)).split(":");
			String et[]=cellToString(row.getCell(capHeaderIndex)).split(":");
			//sti=Integer.valueOf((String)st[0]);
			sti=Integer.parseInt(st[0]);
			eti=Integer.parseInt(et[0]);
			//eti=Integer.valueOf((String)et[0]);
			String ust[]=stime.split(":");
			String uet[]=etime.split(":");
			usti=Integer.valueOf((String)ust[0]);	
			ueti=Integer.valueOf((String)uet[0]);
		}catch(Exception e){
			e.printStackTrace();
		}
			System.out.println("Start Time="+sti+" End Time="+eti+" User Start time="+usti+" User end Time="+ueti+" date="+dt.equals(date));
			if(dt.equals(date))
			{
				if((sti<=usti && eti>=usti) || (sti<=ueti && sti>=ueti)){
					return result="Sorry !No room is available between "+cellToString(row.getCell(roomHeaderIndex))+" to "+cellToString(row.getCell(capHeaderIndex))+" Would you like to book for another time slot?";
				}else if(!(i<rowNum))
				{
				
					//fis.close();
					//Store data
					System.out.println(" getLastRowNum "+ws.getLastRowNum());
					HSSFRow row1 = ws.createRow((short)  ws.getLastRowNum()+1);
					row1.createCell(0).setCellValue(date);
					row1.createCell(1).setCellValue(stime);
					row1.createCell(2).setCellValue(etime);
					row1.createCell(3).setCellValue(bookby);
					row1.createCell(4).setCellValue(participents);
					/*ClassLoader classLoader = ConferenceRoomBooking.class.getClassLoader();
	                File excel =  new File(classLoader.getResource("D:\\ConferenceRoomBooking.xls").getFile());*/
					File excel = new File("D:\\ConferenceRoomBooking.xls");
					FileOutputStream fos = new FileOutputStream(excel);
					wb.write(fos);
					//wb.close();
					fos.flush();
					fos.close();
					result=roomname+" Room is Booked Successfully";
					return result;
				}
			}else if(!(i<=rowNum))
			{
				stream.close();
				//Store data
				System.out.println(" getLastRowNum "+ws.getLastRowNum());
				HSSFRow row1 = ws.createRow((short)  ws.getLastRowNum()+1);
				row1.createCell(0).setCellValue(date);
				row1.createCell(1).setCellValue(stime);
				row1.createCell(2).setCellValue(etime);
				row1.createCell(3).setCellValue(bookby);
				row1.createCell(4).setCellValue(participents);
				ClassLoader classLoader = RoomBooking.class.getClassLoader();
                File excel =  new File(classLoader.getResource("/ConferenceRoomBooking.xls").getFile());
				//File excel = new File("D:\\ConferenceRoomBooking.xls");
				FileOutputStream fos = new FileOutputStream(excel);
				wb.write(fos);
				//wb.close();
				fos.close();
				result=roomname+" Room is Booked Successfully";
				return result;
			}
	}


return "";
	
	
}


	public static String cellToString(HSSFCell cell) {
		int type;
		Object result = null;
		type = cell.getCellType();
		String date="";
		switch (type) {
		case HSSFCell.CELL_TYPE_NUMERIC:
			//if(!cell.getDateCellValue().toString().contains(":")){
			if (DateUtil.isCellDateFormatted(cell)) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
				date = sdf.format(cell.getDateCellValue());
				System.out.println(date.toString());

			}else {
				result = Integer.valueOf((int) cell.getNumericCellValue())
						.toString();
			}
			break;
		case HSSFCell.CELL_TYPE_STRING:
			result = cell.getStringCellValue();
			break;

		case HSSFCell.CELL_TYPE_BLANK:
			result = "";
			break;

		case HSSFCell.CELL_TYPE_FORMULA:
			result = cell.getCellFormula();
		}
		return result.toString();
	}

}


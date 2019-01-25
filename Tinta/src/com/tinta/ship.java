package com.tinta;

import java.util.Scanner;


/* Задача на 5
 * Написать программу перемещения корабля по морю
 * море объявить 2-мерным массивом символов '~' размером W на H, заданным заранее с консоли
 * поместить корабль, задав в соответствующем месте массива символ U или на свое усмотрение 
 * с консоли читать строку и в зависимости от ввода перемещать корабль вверх вниз вправо влево
 * с вовыдом обновленного массива на консоль 
 */


public class ship {

	private static int sizeW = 20;
	private static int sizeH = 20;
	
	private static int ShipX = 0;
	private static int ShipY = 0;
	
	private static char[][] matrixA;
	
	private static void printImg(){

		// выводим море с кораблем 
		System.out.println();	
		
		for (int y = 0; y < sizeH; y++) {			 
		    for (int x = 0; x < sizeW; x++) {
		    	System.out.print(matrixA[x][y]);
		    }
		    System.out.println();		    
		}
		
		System.out.println();	
	}
	
	private static void moveShip( int x, int y ) {
		

		
		// за пределы поля не передвигаем прост овыходим их функции
		if(x < 0 || x >= sizeW )
			return;
		
		if(y < 0 || y >= sizeH )
			return;

		// перед перемещением старое положение затираем водой 
		matrixA[ShipX][ShipY] = '~';
		
		// новое устанавливаем в U
		matrixA[x][y] = 'U';
		
		ShipX = x;
		ShipY = y;
	}
	
	public static void main(String[] args) {
		
		
		
		int xPos = 0;
		int yPos = 0;
		
		System.out.print("Введите ширину поля: ");		
		Scanner in = new Scanner(System.in);
			
		
		sizeW = in.nextInt();
		
		System.out.print("Введите высоту поля: ");	
		sizeH = in.nextInt();
		
		matrixA = new char[sizeW][sizeH];
		
		int x = 0;
		int y = 0;
		for (x = 0; x < sizeW; x++) {			 
		    for (y = 0; y < sizeH; y++) {
		    	matrixA[x][y] = '~'; 
		    }		   		    
		}
		
		System.out.print("Введите положение корабля по x: ");
		
		xPos = in.nextInt();
		
		System.out.print("Введите положение корабля по y: ");
		
		yPos = in.nextInt();
		
		moveShip( xPos, yPos );
		
		boolean exit = false;
		printImg();	
		
		// начальное положение задано
		Scanner actionIn = new Scanner(System.in);
		
		while( !exit ){
			
			System.out.print("Введите координату n - вверх, s - вниз, w - налево, e - направо или exit что бы выйти: \n");
				
			String action = actionIn.nextLine();
			
			if(action.equals("exit"))
				exit = true;			
			else if( action.equals("n") )
				moveShip( ShipX, ShipY - 1 );
			else if( action.equals("s") )
				moveShip( ShipX, ShipY + 1 );
			else if( action.equals("w") )
				moveShip( ShipX - 1, ShipY );
			else if( action.equals("e") )
				moveShip( ShipX + 1, ShipY );				
			printImg();	
		
		
		}
		actionIn.close();
		
		
	}
}

package com.tinta;



import java.util.Random;
import java.util.Scanner;


/* Задача на 5
 * Написать программу перемещения корабля по морю
 * море объявить 2-мерным массивом символов '~' размером W на H, заданным заранее с консоли
 * поместить корабль, задав в соответствующем месте массива символ U или на свое усмотрение 
 * с консоли читать строку и в зависимости от ввода перемещать корабль вверх вниз вправо влево
 * с вовыдом обновленного массива на консоль
 * На поле может находится мина( можно несколько )
 * с консоли ввести положение мины отличное от положения корабля * 
 * после задания напралвния движения корабля перемещать каждую мину случайно(автоматически) в любом направлении по правилу перемещения корабля
 * если мина пересекается(проверять одельно после перемещения корабля и потом после перемещения мины) с кораблем то он тонет - выход из программы    
 */


public class ship2 {

	private static int sizeW = 20;
	private static int sizeH = 20;
	
	private static int ShipX = 0;
	private static int ShipY = 0;
	
	private static int MineX = 0;
	private static int MineY = 0;
	
	private static char[][] matrixA  = null;
	
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
	
	private static void moveMine( int x, int y ) {
		

		
		// за пределы поля не передвигаем прост овыходим их функции
		if(x < 0 || x >= sizeW )
			return;
		
		if(y < 0 || y >= sizeH )
			return;

		// перед перемещением старое положение затираем водой 
		matrixA[MineX][MineY] = '~';
		
		// новое устанавливаем в U
		matrixA[x][y] = '*';
		
		MineX = x;
		MineY = y;
	}
	
	
	private static boolean sank(){
		
		if(ShipX == MineX && ShipY == MineY){
			matrixA[ShipX][ShipY] = '^';
			System.out.print("Корабль потоплен.....");
			return true;
		}
		return false;
			
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
		
		// помещаем корабль
		moveShip( xPos, yPos );
		
		boolean success = false;
		
		while( !success ){
			System.out.print("Введите положение мины по x: ");
			
			xPos = in.nextInt();
			
			System.out.print("Введите положение мины по y: ");
			
			yPos = in.nextInt();
			
			if( xPos >= 0 && xPos < sizeW && yPos >= 0 
					&& yPos < sizeH &&  xPos != ShipX && yPos != ShipY ){
				
				// помещаем мину
				// задаем вручную потому что кораблю может быть в 0 0 - координаты мины по умолчанию 
				MineX = xPos;
				MineY = yPos;
				
				moveMine( MineX, MineY );
				success = true;
			}
			else
				System.out.print("Не верная координата для мины");
		}
		
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
			
			
			// после перемещения корабля проверяем не подбит ли
			exit = sank();
			
			
			Random random = new Random();
			
			// 0 - влево 1 - вправо 2 - вниз 3 - вверх
			int direction = random.nextInt(3);
			
			int xOffset = 0;
			int yOffset = 0;
			
			if(direction == 0)
				xOffset -= 1;
			else if(direction == 1)
				xOffset += 1;
			else if(direction == 2)
				yOffset -= 1;
			else if(direction == 3)
				yOffset += 1;
			
			moveMine( MineX + xOffset, MineY + yOffset );
			
			// после перемещения мины проверяем не подбит ли
			exit = sank();
				
			printImg();	
		
		
		}
		actionIn.close();
		
		
	}
}

package com.tinta;

import java.util.Random;
import java.util.Scanner;

/**
 * Игра схватка двух кораблей(игроки)
 * Игрок управляет одним из них, компьютер другим
 * У каждого корабля есть снаряды(5), защита(50) и деньги(10)  
 * перед началом каждого раунда игрок может потратить деньги либо на снаряд либо на защиту
 * когда защита <= 0 игра заканчивается. Если у игрока снарядов 0 то он атаковать не может
 * за каждый раунд сначала зходит 1 игрок потом 2-й(компьютер)   
 * за ракнд можно либо произвести атаку либо закупиться
 * - цена за 1 снаряд - 1 монета, цена за 1 защиту 2 монеты.
 * 	 компьютер выбирает либо выстрел, либо пробует компенсировать снаряды или защиту
 * - выстрел может нанести поражение случайно от 0 до 10 расчет происходит по следующему:
 *  урон = слчайный_урон * очки_опыта
 *  очки_опыта:  1 очко опыта добавляется обоим тигрокам если им был произведен выстрел
 * После каждого раунда деньги суммой 10 монет раздаются в следующей пропорции(у кого меньше защита тому больше денег):
 *  выдать_денег = 	( 1 - защита_игрока/максимальная_защита ) * максимально_денег_зараунд 
 */
public class BattleShip4 {

	
	static int shellsPlayer1 = 5;
	static int shellsPlayer2 = 5;
	static int maxSheld = 50;
	static int sheldPlayer1 = maxSheld;
	static int sheldPlayer2 = maxSheld;
	
	static int moneyPlayer1 = 10;
	static int moneyPlayer2 = 10;
	
	// опыт игроков
	static int experiencePlayer1 = 1;
	static int experiencePlayer2 = 1;
	
	// сумму разбиваем за раунд
	// максимально_денег_зараунд
	static int summRound = 10;
	static int maxDamadge = 10;
	
	public static void main( String[] args ){
			
		
		int t = 5/2;
		Random rand = new Random(System.currentTimeMillis());
		while ( sheldPlayer1 > 0 && sheldPlayer2 > 0 ){
			
			System.out.println("------------------------------------------------------------------------");
			System.out.println("У первого игрока: " + shellsPlayer1 + "(снарядов) " + sheldPlayer1 + "(защита) " + moneyPlayer1 + "(опыта) " + moneyPlayer1 + "(денег)\n");
			System.out.println("У второго игрока: " + shellsPlayer2 + "(снарядов) " + sheldPlayer2 + "(защита) " + moneyPlayer2 + "(опыта) " + moneyPlayer2 + "(денег)\n");
			
			
			System.out.println("\n Ход игрока №1\n");
			
			Scanner actionIn = new Scanner(System.in);
			
			System.out.println("\n Выберите действие 1 - купить 2 - атаковать \n");
			int action = actionIn.nextInt();
			
			if( action == 1 ){
				
				System.out.println("\n Выберите чего купить 1 - оружие(1 монета за штуку) 2 - защиту(2 монеты за штуку) \n");
				int toBuy = actionIn.nextInt();
				
				boolean bValid = false;
				
				while( !bValid ){
					System.out.println("\n Выберите количество: \n");
					
					int quantity = actionIn.nextInt();
					
					if( toBuy == 1){ // снарядов
						
						int summ = quantity * 1;
						if( summ < moneyPlayer1 ){
							// денег хватает
							moneyPlayer1 -= summ;
							shellsPlayer1 += quantity;
							bValid = true;
						}
						else {
							System.out.println("\n!!!!!! На это количество снарядов денег не хватит \n");
						}
					}
					else if( toBuy == 2 ){
						int summ = quantity * 2;
						if( summ < moneyPlayer1 ){
							// денег хватает
							moneyPlayer1 -= summ;
							sheldPlayer1 += quantity;
							bValid = true;
						}
						else {
							System.out.println("\n!!!!!! На это количество защиты денег не хватит \n");
						}
					}
				}
				
			}
			else if( action == 2 ){ //атака
				
				if( shellsPlayer1 > 0 ){
					
				
					int doDamage = rand.nextInt(maxDamadge);
					
					if( doDamage > 0 ){
						// урон = слчайный_урон * очки_опыта
						sheldPlayer2 -= doDamage * experiencePlayer1;					
						System.out.println("\n Атака на 2 го игрока совершена успешно повреждений нанесено: " + doDamage * experiencePlayer1);
						experiencePlayer1++;
						shellsPlayer1 --;
						
					}
					else
						System.out.println("\n Атака на 2 го игрока совершена не успешно повреждений нанесено 0");
				}
				else
					System.out.println("\n!!!!!! Не достаточно снарядов для атаки");
			
			}
			else {
				actionIn.close();
				System.out.println("\n!!!!!! Не верное действие\n");
				return;
			}
			
			if( sheldPlayer2 > 0  ){
			
				// Ход компьютера
				System.out.println("\n Ход игрока №2\n");
				
				// мало снарядов
				if( shellsPlayer2 < 2  ){
					// купим снарядов на случайную сумму
					if( moneyPlayer2 > 2 ){

						int toBuy = 1 + rand.nextInt( moneyPlayer2 - 2 );
						moneyPlayer2 -= toBuy;
						shellsPlayer2 += toBuy;
					}
					else {
						// денег мало -> на все деньги
						moneyPlayer2 -= moneyPlayer2;
						shellsPlayer2 += moneyPlayer2;
					}
					
				}
				else if( sheldPlayer2 < 15 ){ // < 15 покупаем защиту 
					
					if( moneyPlayer2 >= 2 ){

						int toBuy = moneyPlayer2/2;
						
						moneyPlayer2 -= (toBuy * 2);
						sheldPlayer2 += toBuy;
					}
					else {
						// денег мало -> на все деньги
						moneyPlayer2 -= moneyPlayer2;
						sheldPlayer2 += moneyPlayer2;
					}
				}
				else {					
					
					if( shellsPlayer2 > 0 ){
												
						int doDamage = rand.nextInt(maxDamadge);
						
						if( doDamage > 0 ){
							// наносим повреждение 1 му игроку
							// урон = слчайный_урон * очки_опыта
							sheldPlayer1 -= doDamage * experiencePlayer2;					
							System.out.println("\n Атака на 1 го игрока совершена успешно повреждений нанесено: " + doDamage * experiencePlayer2);
							experiencePlayer2++;
							shellsPlayer2 --;
							
						}
						else
							System.out.println("\n Атака на 1 го игрока совершена не успешно повреждений нанесено 0");
					}
					else
						System.out.println("\n!!!!!! Не достаточно снарядов для атаки");
						
				}
				
				// раунд закончился разбиваем деньги согластно здоровью
				// ( 1 - защита_игрока/максимальная_защита ) * 10
				// (double) - приведение к double что бы можно было дробно разделить и вычесть из 1
				//(int) - округляем до целого меньшего
				moneyPlayer1 += (int)( (1.0  - (double)sheldPlayer1/(double)maxSheld) * (double)summRound) ;
				moneyPlayer2 += (int)( (1.0  - (double)sheldPlayer2/(double)maxSheld) * (double)summRound) ;
				
			}			
			
			
		}
		
		if(sheldPlayer1 <=  0)
			System.out.println("\nПобедил второй игрок");
		else if(sheldPlayer2 <=  0)
			System.out.println("\nПобедил первый игрок");
	}
}

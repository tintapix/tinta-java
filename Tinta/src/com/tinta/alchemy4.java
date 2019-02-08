package com.tinta;

import java.text.DecimalFormat;
import java.util.Random;
import java.util.Scanner;

/**
 * Задание на 4. Играем в алхимика. 
 * Игра состоит из пошаговых атак игрока и монстра
 * 
 * Изначально в начале игры у игрока и монстра по 100 очков жизни
 * Первым ходит игрок потом монстр и так поочереди пока у одного из них не будет жизнь <= 0 
 * у игрока есть меч урон от которого получается в зависимости от нанесенного на него зелья
 * зелье должно получится от смешивания 2-х ингридиентов
 * Имена ингридиентов хранятся в 2-х массивах component1(урон) и component2(добавка здоровья)
 * Перед началом игры следует заполнить 2 массива для ингридиентов (по размеру сколько и названий)
 * случайными числами от 0 до 1(коэфициентами). Для первого массива(урон_отатаки_игрока) это значение урона от зелия, второе(восстановление_здоровья_игроку) это сколько здоровья оно восстановит 
 * Когда игрок выбирает порядковый номер первого и второго компонентов(вручную почередно или через пробел), формируется зелье по следующей формулам:
 * 
 * максимальный_урон = 25 или 10 на усмотрение
 * Урон_монстру = Math.max( максимальный_урон * (урон_отатаки_игрока - восстановление_здоровья_игроку), 0 ) - урон не может быть отрицательным потому Math.max 
 * Здоровье_добавить_игроку = 10 *  восстановление_здоровья_игроку
 * За ход игрока: 
 * Здоровье_игроку +=  Здоровье_добавить_игроку; 
 * Здоровье_игроку = Math.min(Здоровье_игроку,100) что бы не привысить максмальное 
 * Здоровье_монстра -= Урон_монстру
 * 
 *    
 *   
 * Когда ход монстра, то урон от него должен считаться так:
 * максимальный_урон_от_монстра = 25 или 30 на усмотрение
 * Урон_от_монстра = максимальный_урон_от_монстра * (случайное число от 0 до 1)
 * Здоровье_игроку -=  Урон_от_монстра
 * 
 * Если у мостра здоровье < 10 он регенирирует но не атакует
 * Здоровье_монстра += 5 
 * 
 * перед каждым ходом выводить на консоль для наглядности строку:
 *    Имя_коспонента_1 коэфициент значения урона Имя_коспонента_2 коэфициент значения добавки к здоровью
 * Например так:
 * System.out.println("Коэфициент урона " + " 				Коэфициент добавки здоровья \n" );
			for( int i = 0; i < component1Values.length; i++){
				System.out.println(i + " " + component1[i] + " "  + df.format(component1Values[i]) + "			" + component2[i]+ " " + df.format(component2Values[i]) );
			}   
 * Необязательно, но удобно. Для форматированного вывода типа Double, например только 2 х чисел после запятой 
 * используем класс DecimalFormat df = new DecimalFormat("#.##"); где "#.##" означает что будет только 1 число до `.` и 2 числа после   
 */


public class alchemy4 {
	// компоненты урона
	static final String component1[] = {"Шляпка белого гриба",
										"Шляпка болотной митрулы",
										"Шляпка голубой энтоломы",
										"Шляпка гриба-трутовика",
										"Шляпка жёлтого пикнопоруса",
										"Шляпка жгучеедкой сыроежки",
										"Шляпка зелёной хлороцибории",
										"Шляпка каменного гриба",
										"Шляпка красного пикнопоруса",
										"Шляпка мухомора",
										"Шляпка саркосцифы",
										"Шляпка хлороцибории древесной",
										"Шляпка цветохвостника",
										"Шляпка чешуйчатого трутовика",
										"Шляпки гифоломы"} ;
	
	// компоненты добавки здоровья
		static final String component2[] = {"Салат-латук",
										"Семена бергамота",
										"Семена льна",
										"Семена пиона",
										"Семена священного лотоса",
										"Семена фенхеля",
										"Семена чертополоха",
										"Спиддал",
										"Корень мандрагоры",
										"Корневая пульпа аконита",
										"Корневая пульпа водосбора",
										"Корневая пульпа ипомеи",
										"Кровавая трава",
										"Листья алоэ",
										"Лист сонного папоротника"} ;
		
		// коэфициенты урона
		static double component1Values[] = new double[component1.length]; 
		// коэфициенты добавки здоровья
		static double component2Values[] = new double[component2.length];
		
		static int playerHealth = 100;
		static int monsterHealth = 100;
		
		static int maxDamage = 25;
		static int maxMonsterAttack = 30;
		static int maxHealth = 10;
		static int regenaration = 5;
		
		
		static void printStatistics(){
		
			System.out.println("\n-----------------------------------------------------------------------------\n"  );
			System.out.println("\n Здоровье игрока: " + playerHealth + " здоровье монcтра: " +monsterHealth  );
			
		}
		
		public static void main( String[] args ){
			
			Random rand = new Random(System.currentTimeMillis());
			
			
			for( int i = 0; i < component1Values.length; i++){
				component1Values[i] = rand.nextDouble();
			}
			
			for( int i = 0; i < component1Values.length; i++){
				component2Values[i] = rand.nextDouble();
			}
			
			// Объект класса DecimalFormat для форматированного вывода
			DecimalFormat df = new DecimalFormat("#.##");
			
			System.out.println("Коэфициент урона " + " 				Коэфициент добавки здоровья \n" );
			for( int i = 0; i < component1Values.length; i++){
				System.out.println(i + " " + component1[i] + " "  + df.format(component1Values[i]) + "			" + component2[i]+ " " + df.format(component2Values[i]) );
			}
						
			while( playerHealth > 0 && monsterHealth > 0 ){
				
				
				printStatistics();
				
				Scanner actionIn = new Scanner(System.in);
				System.out.println("\n Ход игрока выберите первый компонент число от 0 до " + (component2Values.length - 1)   );
				int first = actionIn.nextInt();
				System.out.println("\n выберите второй компонент число от 0 до " + (component2Values.length - 1)  );
				int second = actionIn.nextInt();
				
				if( first < 0 || first >= component1Values.length || second < 0 || second >= component2Values.length ){
					return;
				}
				
				double firstValue  =  component1Values[first];
				double secondValue  =  component2Values[second];
				
				int damage = Math.max(0, (int)(maxDamage * ( firstValue - secondValue ) ));
				int healthAdd = (int)(maxHealth * secondValue);
				
				System.out.println("\n Урон монстру составил = " + damage + ", здоровья добавилось = " + healthAdd  );
				monsterHealth -= damage;
				playerHealth += healthAdd;
				playerHealth = Math.min(playerHealth,100);
				double  attackMonster =   rand.nextDouble();
				int damageMonsterDo = (int)(attackMonster * maxMonsterAttack);
				
				if( monsterHealth > 0 ){
					if( monsterHealth <= 10 ){
						System.out.println("\n Ход монстра и он регенирирует на " + regenaration + " очков " );
						monsterHealth += regenaration;
					} else {
						System.out.println("\n Ход монстра и его атака = " + damageMonsterDo  );
						playerHealth -= damageMonsterDo;
					}
				}
			}
			if( playerHealth <= 0 ){
				System.out.println("\n Победил монстр" );
				return;
			}
			
			if( monsterHealth <= 0 ){
				System.out.println("\n Победил игрок" );
				
			}
				
		}
		
}

package com.tinta;

import java.text.DecimalFormat;
import java.util.Random;
import java.util.Scanner;

/**
 * Задание на 5. Играем в продвинутого алхимика. 
 * Игра состоит из пошаговых атак игрока и монстра
 * 
 * Изначально в начале игры у игрока и монстра по 100 очков жизни
 * Первым ходит игрок потом монстр и так поочереди пока у одного из них не будет жизнь <= 0
 * У монстра есть уязвимость к повреждению от 3х разных воздействий( переносимость_монстра_яда, переносимость_монста_холода, переносимость_монста_огня ) в начале игры они случайные от 0 до 1( функция rand.nextDouble() класса Random)
 * у игрока есть меч урон от которого получается в зависимости от нанесенного на него зелья
 * зелье должно получится от смешивания 2-х ингридиентов
 * Имена ингридиентов хранятся в 2-х массивах component1(урон) и component2(добавка здоровья)
 * Перед началом игры следует заполнить 3 массива для первых ингридиентов числами от 0 до 1( воздействие_яда, воздействие_холода, воздействие_огня  )
 * и заполнить 4й массив(восстановление_здоровья_игроку) случайными числами от 0 до 1 - коэфициенты восстановления здоровья.
 * Уязвимости монстра игроку не известны он можнт лишь наблюдением определить примерно эти значения.  
 * Когда игрок выбирает порядковый номер первого и второго компонентов(вручную почередно или через пробел), формируется зелье по следующей формулам:
 * 
 * максимальный_урон = 25 или 10 на усмотрение
 * Урон_монстру =  (максимальный_урон  * (воздействие_яда * (1 - переносимость_монстра_яда)- восстановление_здоровья_игроку))
 * 				+  (максимальный_урон  * (воздействие_холода * (1 - переносимость_монста_холода) - восстановление_здоровья_игроку))
 * 				+  (максимальный_урон  * (воздействие_огня * (1 - переносимость_монста_огня) - восстановление_здоровья_игроку)) 
 * 				  
 * Здоровье_добавить_игроку = 10 * восстановление_здоровья_игроку
 * За ход игрока: 
 * Здоровье_игроку +=  Здоровье_добавить_игроку; 
 * Здоровье_игроку = Math.min(Здоровье_игроку,100) что бы не привысить максмальное 
 * Здоровье_монстра -= Math.max((Урон_монстру,0) - н едобавлять отрицательный урон
 *    
 * Когда ход монстра, то урон от него должен считаться так:
 * максимальный_урон_от_монстра = 30 или 40 на усмотрение
 * Урон_от_монстра = максимальный_урон_от_монстра * (случайное число от 0 до 1)
 * Здоровье_игроку -=  Урон_от_монстра
 * 
 * Если у мостра здоровье < 20 он регенирирует но не атакует
 * Здоровье_монстра += 10 
 * 
 * перед каждым ходом выводить на консоль для наглядности строку:
 *    Имя_коспонента_1 коэфициент значения урона Имя_коспонента_2 коэфициент значения добавки к здоровью
 * Например так с префиксами по каждому урону я(яд) х(холод) о(огонь) :
 * DecimalFormat df = new DecimalFormat("#.##");
			
	System.out.println("Коэфициент урона " + " 				Коэфициент добавки здоровья \n" );
	for( int i = 0; i < component1.length; i++){
		System.out.println(i + " " + component1[i] + " я("  + df.format(component1ValuesPoison[i]) + ") х(" + df.format(component1ValuesFrost[i])+ ") о(" + df.format(component1ValuesFire[i]) + ")			" + component2[i]+ " " + df.format(component2Values[i]) );
	} 
 * Необязательно, но удобно. Для форматированного вывода типа Double, например только 2 х чисел после запятой 
 * используем класс DecimalFormat df = new DecimalFormat("#.##"); где "#.##" означает что будет только 1 число до `.` и 2 числа после   
 */



public class alchemy5 {
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
		static double component1ValuesPoison[] = new double[component1.length]; 
		
		static double component1ValuesFrost[] = new double[component1.length];
		
		static double component1ValuesFire[] = new double[component1.length];
		
		// коэфициенты добавки здоровья
		static double component2Values[] = new double[component2.length];
		
		static int playerHealth = 100;
		static int monsterHealth = 100;
		
		static int maxDamage = 25;
		static int maxMonsterAttack = 30;
		static int maxHealth = 10;
		static int regenaration = 5;
		
		// сопротивляемости монстра
		static double mosterResistPoison = 0;
		static double mosterResistFrost = 0;
		static double mosterResistFire = 0;
		
		
		static void printStatistics(){
		
			System.out.println("\n-----------------------------------------------------------------------------\n"  );
			System.out.println("\n Здоровье игрока: " + playerHealth + " здоровье монcтра: " +monsterHealth  );
			
		}
		
		public static void main( String[] args ){
			
			Random rand = new Random(System.currentTimeMillis());
			
			
			for( int i = 0; i < component1ValuesPoison.length; i++){
				component1ValuesPoison[i] = rand.nextDouble();
			}
			
			for( int i = 0; i < component1ValuesFrost.length; i++){
				component1ValuesFrost[i] = rand.nextDouble();
			}
			
			for( int i = 0; i < component1ValuesFire.length; i++){
				component1ValuesFire[i] = rand.nextDouble();
			}
			
			for( int i = 0; i < component2.length; i++){
				component2Values[i] = rand.nextDouble();
			}
			
			mosterResistPoison = rand.nextDouble();
			mosterResistFrost = rand.nextDouble();
			mosterResistFire = rand.nextDouble();
			
			
			// Объект класса DecimalFormat для форматированного вывода
			DecimalFormat df = new DecimalFormat("#.##");
			
			System.out.println("Коэфициент урона " + " 				Коэфициент добавки здоровья \n" );
			for( int i = 0; i < component1.length; i++){
				System.out.println(i + " " + component1[i] + " я("  + df.format(component1ValuesPoison[i]) + ") х(" + df.format(component1ValuesFrost[i])+ ") о(" + df.format(component1ValuesFire[i]) + ")			" + component2[i]+ " " + df.format(component2Values[i]) );
			}
						
			while( playerHealth > 0 && monsterHealth > 0 ){
				
				
				printStatistics();
				
				Scanner actionIn = new Scanner(System.in);
				System.out.println("\n Ход игрока выберите первый компонент число от 0 до " + (component2Values.length - 1)   );
				int first = actionIn.nextInt();
				System.out.println("\n выберите второй компонент число от 0 до " + (component2Values.length - 1)  );
				int second = actionIn.nextInt();
				
				if( first < 0 || first >= component1ValuesPoison.length || second < 0 || second >= component2Values.length ){
					return;
				}
				
				double firstValuePosion  =  component1ValuesPoison[first];
				double firstValueFrost  =  component1ValuesFrost[first];
				double firstValueFire  =  component1ValuesFire[first];
				
				double restoreHealh  =  component2Values[second];
				
				int damagePoision = (int)(maxDamage  * (firstValuePosion * ( 1.0 - mosterResistPoison ) - restoreHealh)) ;
				int damageFrost = (int)(maxDamage    * (firstValueFrost * ( 1.0 - mosterResistFrost ) - restoreHealh) ) ;
				int damageFire = (int)(maxDamage     * (firstValueFire * ( 1.0 - mosterResistFire ) - restoreHealh) ) ;
				
				int totalDamage = Math.max(damagePoision + damageFrost + damageFire, 0);
				
				
				int healthAdd = (int)(maxHealth * restoreHealh);
				
				System.out.println("\n Урон монстру составил  я(" + damagePoision  + ") + х(" + damageFrost + ") + о(" +  damageFire +") = " + totalDamage + ", здоровья добавилось = " + healthAdd  );
				monsterHealth -= totalDamage;
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

package com.tinta;

import java.util.Random;
import java.util.Scanner;



/** Задача на 4
 * Игра поле чудес классический вариант.Играет 1 игрок
 * Случайным образом взять оттуда слово
 * выводить его в виде ****** вместо букв
 * пошагово крутя барабан можно получить очков или 0(банкрот) - обнулить счет
 * с каждым ходом нужно назвать букву или отгадать слово целиком, буква есть количество очков  = очков_на_барабане * количество_букв
 *
 */


public class victorina4 {
	
	
	static String[] AllWords = {"человек","время","дело","жизнь","день","рука","работа","слово","место","вопрос","лицо","глаз","страна","друг","сторона","дом","случай","ребенок","голова","система","вид","конец","отношение","город","часть","женщина","проблема","земля","решение","власть","машина","закон","час","образ","отец","история","нога","вода","война","возможность","компания","результат","дверь","бог","народ","область","число","голос","развитие","группа","жена","процесс","условие","книга","ночь","суд","деньга","уровень","начало","государство","стол","средство","связь","имя","президент","форма","путь","организация","качество","действие","статья","общество","ситуация","деятельность","школа","душа","дорога","язык","взгляд","момент","минута","месяц","порядок","цель","программа","муж","помощь","мысль","вечер","орган","правительство","рынок","предприятие","партия","роль","смысл","мама","мера","улица","состояние","задача","информация","театр","внимание","производство","квартира","труд","тело","письмо","центр","утро","мать","комната","семья","сын","смерть","положение","интерес","федерация","век","идея","управление","автор","окно","ответ","совет","разговор","мужчина","ряд","счет","мнение","цена","точка","план","проект","глава","материал","основа","причина","движение","культура","сердце","рубль","наука","документ","неделя","вещь","чувство","правило","служба","газета","срок","институт","член","ход","стена","директор","плечо","опыт","встреча","принцип","событие","структура","количество","товарищ","создание","значение","объект","гражданин","очередь","период","образование","состав","пример","лес","исследование","девушка","данные","палец","судьба","тип","метод","политика","армия","брат","представитель","борьба","использование","шаг","игра","участие","территория","край","размер","номер","район","население","банк","начальник","класс","зал","изменение","большинство","характер","кровь","направление","позиция","герой","течение","девочка","искусство","гость","воздух","мальчик","фильм","договор","регион","выбор","свобода","врач","экономика","небо","факт","церковь","завод","фирма","бизнес","союз","деньги","специалист","род","команда","руководитель","спина","дух","музыка","способ","хозяин","поле","доллар","память","природа","дерево","оценка","объем","картина","процент","требование","писатель","сцена","анализ","основание","повод","вариант","берег","модель","степень","самолет","телефон","граница","песня","половина","министр","угол","зрение","предмет","литература","операция","двор","спектакль","руководство","солнце","автомобиль","родитель","участник","журнал","база","пространство","защита","название","стих","ум","море","удар","знание","солдат","миллион","строительство","технология","председатель","сон","сознание","бумага","реформа","оружие","линия","текст","выход","ребята","магазин","соответствие","участок","услуга","поэт","предложение","желание","пара","успех","среда","возраст","комплекс","бюджет","представление","площадь","генерал","господин","дочь","понятие","кабинет","безопасность","фонд","сфера","папа","сотрудник","продукция","будущее","продукт","содержание","художник","республика","сумма","контроль","парень","ветер","хозяйство","помочь","курс","губа","река","грудь","огонь","нос","волос","ухо","отсутствие","радость","сад","подготовка","необходимость","доктор","лето","камень","здание","капитан","собака","итог","рис","техника","элемент","источник","деревня","депутат","проведение","рот","масса","комиссия","цвет","рассказ","функция","определение","мужик","обеспечение","обстоятельство","работник","разработка","лист","звезда","гора","применение","победа","товар","воля","зона","предел","целое","личность","офицер","влияние","поддержка","ответственность","цветок","праздник","немец","бой","сестра","господь","учитель","многое","рамка","практика","показатель","метр","войско","частность","особенность","снег","комитет","налог","акт","отдел","карман","вывод","норма","читатель","этап","сравнение","прошлое","фамилия","кухня","заявление","доля","пункт","студент","учет","впечатление","доход","вирус","клетка","удовольствие","теория","враг","собрание","бутылка","расчет","го","режим","множество","клуб","попытка","зуб","сеть","семь","министерство","прием","боль","сожаление","кожа","субъект","знак","актер","ресурс","акция","газ","журналист","звук","передача","здоровье","администрация","болезнь","детство","мастер","выборы","зима","подход","поиск","механизм","выражение","скорость","ощущение","стоимость","коридор","ошибка","лидер","карта","заседание","глубина","хлеб","поверхность","энергия","нарушение","реализация","революция","поведение","профессор","исполнение","заместитель","суть","станция","реакция","десяток","столица","формирование","поколение","дума","существование","продажа","список","способность","противник","схема","долг","режиссер","отличие","колено","дед","свойство","этаж","секунда","фактор","житель","явление","высота","сосед","усилие","рождение","расход","остров","фигура","наличие","дядя","милиция","растение","существо","черт","бабушка","законодательство","собственность","отрасль","слеза","волна","стекло","традиция","январь","оборудование","зависимость","фраза","декабрь","сведение","трубка","сентябрь","университет","командир","храм","повышение","стиль","артист","больница","одежда","охрана","водка","кодекс","имущество","птица","переход","красота","клиент","толпа","адрес","отделение","октябрь","чудо","счастие","улыбка","ужас","аппарат","корабль","родина","животное","черта","известие","понимание","тень","апрель","коллега","преступление","рыба","кресло","запах","выставка","князь","фотография","весна","помещение","эпоха","занятие","произведение","концерт","ладонь","дама","сомнение","американец","середина","зарплата","тайна","запад","июнь","беседа","фронт","поезд","должность","баба","промышленность","музей","судья","получение","полковник","зритель","секретарь","установка","поток","ценность","образец","страница","перспектива","трава","чиновник","мозг","сотня","лагерь","выступление","оборона","постановление","честь","настроение","кровать","характеристика","обязанность","шея","крыша","появление","учреждение","признак","труба","жертва","беда","фон","организм","ученик","заключение","выполнение","канал","исключение","дача","соглашение","осень","польза","стул","июль","дождь","сутки","еврей","конкурс","открытие","телевизор","лошадь","температура","приказ","лестница","реклама","спор","подруга","угроза","конфликт","изучение","вино","концепция","достижение","сообщение","объединение","обстановка","костюм","ключ","ресторан","назначение","царь","воспоминание","увеличение","вкус","мероприятие","лоб","слой","восток","последствие","принятие","сотрудничество","нефть","слух","бок","переговоры","тюрьма","кандидат","просьба","реальность","подарок","категория","потребность","быль","редакция","очко","километр","губернатор","новость","инструмент","потеря","взаимодействие","звонок","кусок","капитал","грех","перевод","партнер","ноябрь","молодежь","тишина","творчество","книжка","мясо","масло","деталь","инженер","оплата","эксперт","кремль","февраль","следствие","пьеса","билет","урок","коллектив","устройство","палата","площадка","опасность","пропасть","воздействие","разница","родственник","сезон","издание","человечество","снижение","запас","крик","публика","вещество","экран","эффект","ящик","ракета","водитель","пакет","зеркало","вес","дно","вагон","убийство","тон","щека","дурак","длина","давление","двигатель","камера","обращение","формула","запись","крыло","поездка","гостиница","колесо","разрешение","торговля","академия","доклад","общение","присутствие","процедура","испытание","нож","проверка","коммунист","цифра","полет","стакан","эффективность","обучение","портрет","достоинство","рассмотрение","владелец","жилье","компьютер","корень","смена","доказательство","кадр","лейтенант","признание","темнота","пистолет","наблюдение","мост","ремонт","истина","вход","политик","живот","кредит","шум","обед","недостаток","памятник","вершина","серия","эксперимент","сущность","транспорт","инициатива","активность","конференция","кулак","доска","ожидание","платье","смех","отказ","сбор","пенсия","буква","порог","автобус","воспитание","производитель","полоса","риск","пиво","корпус","штаб","кольцо","постель","выпуск","дворец","брак","прокурор","печать","окончание","автомат","тенденция","следователь","штат","куст","старуха","описание","психология","шутка","съезд","ставка","забота","величина","версия","мешок","конструкция","контакт","шанс","лодка","редактор","заказ","кофе","рубеж","статус","спорт","покой","кризис","взрыв","профессия","дым","металл","сапог","диван","интернет","почва","лед","подразделение","минимум","конь","дружба","вина","замок","мечта","сигнал","талант","мгновение","столик","затрата","золото","миг","плата","подъезд","масштаб","обсуждение","сделка","обязательство","расстояние","отдых","телевидение","тетя","яблоко","свидетель","монастырь","чтение","параметр","кампания","помощник","полк","мощность","сюжет","потолок","регистрация","майор","эксплуатация","озеро","новое","атмосфера","премия","совесть","предприниматель","мальчишка","дочка","приятель","начальство","препарат","село","обработка","танк","милиционер","ручка","возвращение","прокуратура","ворота","молоко","еда","сказка","краска","хвост","сигарета","введение","покупатель","поворот","москвич","ограничение","инвестиция","нация","набор","поселок","дыхание","адвокат","сумка","пресса","корреспондент","песок","удивление","потребитель","указание","изображение","счастье","мэр","согласие","действительность","планета","агентство","танец","библиотека","финансирование","объяснение","распределение","конституция","таблица","поэзия","термин","прибыль","стандарт","восторг","гибель","изделие","темп","вооружение","осуществление","уход","чемпионат","молитва","контракт","философия","горло","оборот","кость","ведомство","преимущество","мина","полномочие"};
	
	// слово для угадывания выбрать при начале из AllWords
	static String word = new String();

	// для кручения барабана от сюда берем вариант генерируя число от 0 до Scores.length - 1
	static int[] Scores = { 0, 100, 200, 300, 400, 500 ,1000 };
	
	// для показа
	static char[] wordChar = null;
	
	
	// System.currentTimeMillis() задает seed для генератора
	static Random rand = new Random(System.currentTimeMillis());
	
	// признак хода игры для главного цикла
	static boolean gameOn = true;
	
	// очков у игроков
	static int mScore = 0;
	
	//static int Players = 1;
	
	private static boolean printCheckWord(){

		// выводим море с кораблем 
		System.out.println();
		
		// если все звездочки открыты то слово полностью открыто 
		boolean solved = true;
		
		for( char i : wordChar ){	
			if( i == '*' )
				solved = false; // хотя бы одна звездочка есть то слово ещене раскрыто
			
		    System.out.print(i);		    
		}
		
		System.out.println();	
		
		return solved;
	}
	
	static private int roll(){
		
		int i  = rand.nextInt(Scores.length - 1);
		return Scores[i];		
	}	
	
	public static void main( String[] args ) {
		
		// выбираем из массива случайное слово
		word = AllWords[rand.nextInt(AllWords.length - 1)];
		
		wordChar = new char[word.length()];
		
		for( int i = 0; i <  wordChar.length; i++ ){
			wordChar[i] = '*';
		}
		
		
		
		
		while( gameOn ){
			
			
			gameOn =  !printCheckWord(); // смотрим что в массиве есть еще звездочки если высе открыто заканчиваем
			
			if( gameOn ) {
										
				System.out.print("Введите действие: 1 - крутить барабан, 2 - назвать слово\n");
				System.out.println();
				Scanner actionIn = new Scanner(System.in);
				
				int action = actionIn.nextInt();
				
				// признак перехода хода
				boolean next = true;
				if( action == 1 ){
					
					Scanner actionChar = new Scanner(System.in);
					
					int score = roll();
					if( score == 0 ){
						System.out.print("Выпал банкрот ваши очки обнулились\n");
						mScore = 0;						
					}else
						System.out.print("На барабане " + score +  " очков\n");
									
					System.out.print("Назовите букву: ");
					String charAnswer = actionChar.nextLine();
					
					System.out.print("Буква: " + charAnswer.charAt(0) + "\n");
					
					boolean old = false;
					
					for( char i  : wordChar ){
						
						if( charAnswer.charAt(0) == i ){							
							old = true;
							break;
						}
						
					}
					
					if( old  ){						
						System.out.print("Такая буква уже открыта переход хода");
						
					} else {
						
						// количество раз, сколько буква в слове присутствует
						int count = 0;
						// проходимся по слову и ищеи букву
						for( int i = 0; i < word.length(); i++ ){
							
							char v = word.charAt(i);
							if( v == charAnswer.charAt(0) ){
								count++;
								// открываем 
								wordChar[i] = v;
							}				
							
						}
						if( count == 0 ){
							System.out.print("Нет такой буквы");
						} else {
							// добавляются в размере количества букв
							mScore += (score * count);	
							System.out.print("Такая буква есть и она встречается " + count + " раз\n ");
							System.out.print("У вас на счету " + mScore + " очков ");
							
							next = false;
						}
						
					}
					
					//actionChar.close();
				} else if( action == 2 ){ // называю слово
					
					
					System.out.print("Введите слово");
					System.out.println();
					
					// вводим слово и проверяем
					Scanner wordInput = new Scanner(System.in);
					
					String answer = wordInput.nextLine();
					
					if( answer.equals( word ) ){
						
						// слово угадано заполняем массив для вывода символами из слова
						for( int i = 0; i <  wordChar.length; i++ ){
							wordChar[i] = word.charAt( i );
						}
						
						printCheckWord();
						System.out.print("Вы угодали !!! ");
						
						
						System.out.println();						
						gameOn = false;
					}
					else {
						System.out.print("Вы не угодали, очки аннулируются");
						mScore = 0;
						
					}
					System.out.print("У вас на счету " + mScore + " очков ");
					//wordInput.close();
					
				}				
				
			}
			
		}
		
		
	}
	
	
	

}

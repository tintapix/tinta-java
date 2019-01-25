package com.tinta.common.tintatreeconfig;

public class ConfigTest {

	public static void main( String[] args ) {
		
	molyTreeConfig cfg = new molyTreeConfig();
	
	try {
		cfg.parse("D:/readme");	
		
		double Data1000 = cfg.getValDouble("[2]");
		String DataText = cfg.getValString("[3]");
		String strVal = cfg.getValString("Value1.[0]");
		double dbl1 = cfg.getValDouble("Value1.[1]");
		double dbl2 = cfg.getValDouble("Value1.[2]");
		double dbl3 = cfg.getValDouble("Value1.[3]");
		boolean bool1 = cfg.getValBoolean("Value1.[4]");
		
		String strVal1 = cfg.getValString("Value2.value_name");
		double dbl4 = cfg.getValDouble("Value2.value_speed");
		double dbl5 = cfg.getValDouble("Value2.value_id");		
		boolean boolAlive = cfg.getValBoolean("Value2.alive");
		
		String strValGun = cfg.getValString("Value2.weapon.[0]");
		double strValGunPower = cfg.getValDouble("Value2.weapon.power");
                
        cfg.save("D:/readme_out");
       
	}
	catch(molyConfigException e){
		System.out.print(e.getMessage());
	}
	}
	
}

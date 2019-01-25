
#include <jni.h>
#include <stdio.h>
#include <sstream>
#include <iostream>
#include <fstream>
#include <vector>

extern "C" {
#include <lua/lua.h>
#include <lua/lualib.h>
#include <lua/lauxlib.h>
}


#define TINTA_JAVA_LUA_VER "1.0.1"

class tintaRandomV2 {
	public:
		tintaRandomV2( int seed = 0 );

		void				setRandSeed( int seed );
		int					getRandSeed( void ) const;

        /*
            random integer in the range [0, MAX_RAND]
        */
		int					randomInt( void );			

        /*
            random integer in the range [0, max]
        */
		int					randomInt( int max );

        /*
            random integer in the range [min, max]
        */
		int					randomInt( int min,  int max );	

        /*
            random number in the range [0.0f, 1.0f]
        */
		float				randomFloat( void );		

        /*
            random number in the range [-1.0f, 1.0f]
        */
		float				randomFloatNeg( void );		

        /*
            random number in the manual range [min, max]
        */
		template <typename T> 
		T	randomInterv( T min, T max  )	
		{			
			double dRatio = ( ( double )randomInt() )/( ( double )( tintaRandomV2::MAX_RAND ) );
			return min + ( max - min )*( ( T )dRatio );
		}

		static const int	MAX_RAND = 0x7fff; // 32767

	private:
		int					mSeed;
	};

	 tintaRandomV2::tintaRandomV2( int seed ) {
        this->mSeed = seed;
	}

	 void tintaRandomV2::setRandSeed( int seed ) {
        this->mSeed = seed;
	}

	 int tintaRandomV2::getRandSeed( void ) const {
        return mSeed;
	}

	 int tintaRandomV2::randomInt( void ) {
		
        return(((mSeed = mSeed * 214013L + 2531011L) >> 16) & 0x7fff);
	}

	 int tintaRandomV2::randomInt( int max ) {
		if ( max == 0 ) {
			return 0;			
		}
		return randomInt() % (max + 1);
	}

	 int tintaRandomV2::randomInt( int min,  int max ) {

		if ( max == 0 ||  (max - min) <= 0) {
				return max;			
		}

		return  randomInt( max - min ) + min;
	}

	 float tintaRandomV2::randomFloat( void ) {
		return ( randomInt() / ( float )( tintaRandomV2::MAX_RAND + 1 ) );
	}

	 float tintaRandomV2::randomFloatNeg( void ) {
		return ( 2.0f * ( randomFloat() - 0.5f ) );
	}


#define DEBUG_MODE 0

#ifdef LuaReal
    #undef LuaReal
#endif

#ifdef LuaInt
    #undef LuaInt
#endif

#if defined(LUA_32BITS)	
    #define LuaReal float
    #define LuaInt int
#elif defined(LUA_C89_NUMBERS)

    #define LuaReal double
    #define LuaInt  long
#endif

#ifndef ENDIAN_LITTLE
    #define ENDIAN_LITTLE
#endif

#if DEBUG_MODE 
std::ofstream		fileLog;
#endif

#include "com_tinta_common_lua_tintaLua.h"

typedef std::basic_stringstream<char,std::char_traits<char>,std::allocator<char> > StringStream;
typedef std::string String;

#define BUFFER_MAX 2024

lua_State	   *Lua  = NULL;
tintaRandomV2  *random = NULL;
int error       = -1;
String errorText;

JavaVM* javaVM = NULL;
jobject javaObj;
jclass luaJavaClass;


/*
#if DEBUG_MODE 
#include <time.h>
#include <iomanip>
void logMsg( const std::string &msg ){
    if( fileLog.is_open() ){

        struct tm *pTime;
        time_t ctTime; time(&ctTime);
        pTime = localtime( &ctTime );
        fileLog << std::setw(2) << std::setfill('0') << pTime->tm_hour
            << ":" << std::setw(2) << std::setfill('0') << pTime->tm_min
            << ":" << std::setw(2) << std::setfill('0') << pTime->tm_sec
            << ": ";

        fileLog << msg << std::endl;

        fileLog.flush();
    }
}
#endif
*/
int msg( lua_State *L ) {
    if( !javaVM ) {
        error = 1;
        StringStream s;
        s << "Lua Error: Not registered JVM";       
        errorText = s.str();        
        return 0;
    }

    JNIEnv *env;
    javaVM->AttachCurrentThread((void**)&env, NULL);
    if( !env ){
        error = 1;
        StringStream s;
        s << "Lua Error: env == NULL ";       
        errorText = s.str();        
        return 0;
    }

    int argc = lua_gettop(Lua); //1 - int val - image index
    StringStream s;
    for (int n = 1, i = 0; n <= argc; ++n, i++)
    {
        
        if ( lua_isstring(Lua, n) ) { 
            s << lua_tostring(Lua, n);
        }
        else if ( lua_isinteger(Lua, n) ) { 
            s << lua_tostring(Lua, n);
        }
        else if ( lua_isnumber(Lua, n) )
            s << lua_tonumber(Lua, n);
        if ( lua_isboolean(Lua, n) )
            s << lua_toboolean(Lua, n);        
    }  


    jmethodID method = env->GetMethodID(luaJavaClass, "msg", "(Ljava/lang/String;)V");

    if( method != NULL && s.str().length() > 0 ){
        jstring jStringParam = env->NewStringUTF( s.str().c_str() );
        env->CallVoidMethod( javaObj, method, jStringParam );
        env->DeleteLocalRef( jStringParam );
    }
    return 0;
     
}

int rand( lua_State *L ) {

    if( !javaVM ) {
        error = 1;
        StringStream s;
        s << "Lua Error: Not registered JVM";       
        errorText = s.str();        
        return 0;
    }

    JNIEnv *env;
    javaVM->AttachCurrentThread((void**)&env, NULL);
    if( !env ){
        error = 1;
        StringStream s;
        s << "Lua Error: env == NULL ";       
        errorText = s.str();        
        return 0;
    }

    
   
    if ( lua_isnumber(Lua, 1) && lua_isnumber(Lua, 2) ) {      
        lua_pushinteger(Lua, random->randomInt(lua_tointeger(Lua, 1), lua_tointeger(Lua, 2) ));
    }
    else
        lua_pushinteger(Lua, random->randomInt());   


    return 1;

}

int randf( lua_State *L ) {

    if( !javaVM ) {
        error = 1;
        StringStream s;
        s << "Lua Error: Not registered JVM";       
        errorText = s.str();        
        return 0;
    }

    JNIEnv *env;
    javaVM->AttachCurrentThread((void**)&env, NULL);
    if( !env ){
        error = 1;
        StringStream s;
        s << "Lua Error: env == NULL ";       
        errorText = s.str();        
        return 0;
    }
    
    lua_pushnumber(Lua, random->randomFloat());

    return 1;
}

int srand( lua_State *L ) {

    if( !javaVM ) {
        error = 1;
        StringStream s;
        s << "Lua Error: Not registered JVM";       
        errorText = s.str();        
        return 0;
    }

    JNIEnv *env;
    javaVM->AttachCurrentThread((void**)&env, NULL);
    if( !env ){
        error = 1;
        StringStream s;
        s << "Lua Error: env == NULL ";       
        errorText = s.str();        
        return 0;
    }

    if( lua_isnumber(Lua, 1) )
        random->setRandSeed(lua_tointeger(Lua, 1));
    else
         luaL_error( Lua, "Wrong random seed" );

    return 0;

}

int call( lua_State *L ) {
    
    if( !javaVM ) {
        error = 1;
        StringStream s;
        s << "Lua Error: Not registered JVM";       
        errorText = s.str();        
        return 0;
    }

    JNIEnv *env;
    javaVM->AttachCurrentThread((void**)&env, NULL);
    if( !env ){
        error = 1;
        StringStream s;
        s << "Lua Error: env == NULL ";       
        errorText = s.str();        
        return 0;
    }

    jmethodID method = env->GetMethodID(luaJavaClass, "callback", "()I");
   
    if( method != NULL ){
        long rez = env->CallIntMethod(javaObj, method);

        if( rez == -1 ){
            
            luaL_error( Lua, errorText.length() > 0 ? errorText.c_str() : " unspecified error during call getparam() " );
            return 0;
        }
        return (int)rez;
    }
    return 0;    
}



static const luaL_Reg java[] = {              
    { "call", call },
    { "msg", msg },
    { "rand", rand },
    { "srand", srand },
    { "randf", randf },    
    { NULL, NULL }
};

int luaopen_java(lua_State *L) {
     std::cout<<"luaopen_java";
    luaL_newlib(L, java);
    return 1;
}
static const luaL_Reg loadedlibsJava[] = {
    { "_G", luaopen_base },
    { "java", luaopen_java },
    { NULL, NULL } };



#if defined( _WIN32 )
#include <windows.h>
#include <locale>

    BOOL CtrlHandler(DWORD fdwCtrlType) {

        //  DemoLuaApp* app = (DemoLuaApp*)tintaWinApp::mWinApp;
        //   app->mExit = true;

        return TRUE;
    }
#endif

JNIEXPORT void JNICALL Java_com_tinta_common_lua_tintaLua_init
( JNIEnv *env, jobject obj ) {   


    Java_com_tinta_common_lua_tintaLua_release(env,obj);


#if DEBUG_MODE                 
        //std::cout<<"Debug: "<<"context created";
#endif
    if( Lua ){
        lua_close( Lua );
        Lua = NULL;
    }
    Lua = luaL_newstate(); /* opens Lua */
    luaL_openlibs(Lua); /* opens the standard libraries */

    const luaL_Reg *lib;    
    for (lib = loadedlibsJava; lib->func; lib++) {
        luaL_requiref(Lua, lib->name, lib->func, 1);
        lua_pop(Lua, 1);  
    }

    error       = -1;
    errorText   = "";

    if( !env ){
        error = 1;
        StringStream s;
        s << "Lua Error: env == NULL ";       
        errorText = s.str();        
        return;
    }

    env->GetJavaVM(&javaVM);
    
    jclass cls = env->GetObjectClass(obj);
    luaJavaClass = (jclass) env->NewGlobalRef(cls);
    javaObj = env->NewGlobalRef(obj);
    if( random )
        delete random;
    random = new tintaRandomV2();
   
    //javaVM->AttachCurrentThread((void**)&env, NULL);
   
    StringStream verStr;
    verStr<<"TintaJavaLua version "<<TINTA_JAVA_LUA_VER<<"\n";
    jmethodID method = env->GetMethodID(luaJavaClass, "msg", "(Ljava/lang/String;)V");

    if( method != NULL && verStr.str().length() > 0 ){
        jstring jStringParam = env->NewStringUTF( verStr.str().c_str() );
        env->CallVoidMethod( javaObj, method, jStringParam );
        env->DeleteLocalRef( jStringParam );        
    }        

    /*
#if DEBUG_MODE 
#if defined( _WIN32 )

    std::locale loc("rus_rus.866");

    HANDLE hConsole = GetStdHandle(STD_OUTPUT_HANDLE);

    system("color F0");
    CONSOLE_FONT_INFOEX info = { 0 };
    info.cbSize = sizeof(info);

    info.dwFontSize.Y = 16; // leave X as zero
    info.FontWeight = FW_NORMAL;
    wcscpy(info.FaceName, L"Lucida Console");
    SetCurrentConsoleFontEx(GetStdHandle(STD_OUTPUT_HANDLE), NULL, &info);
    SetConsoleCtrlHandler((PHANDLER_ROUTINE)CtrlHandler, TRUE);
#endif
#endif
    */
}

JNIEXPORT void JNICALL Java_com_tinta_common_lua_tintaLua_release
( JNIEnv *env, jobject obj ){

    if( Lua ){

        lua_close(Lua);
        Lua = NULL;
    }   
    if(random)
        delete random;
}

JNIEXPORT void JNICALL Java_com_tinta_common_lua_tintaLua_executeFile
( JNIEnv *env, jobject obj, jstring file ){

    if( !env )
        return;   

    if( !Lua ){
        Java_com_tinta_common_lua_tintaLua_init(env,obj);
    }

   error  = -1;
   errorText = "";

   const char *path = env->GetStringUTFChars( file, NULL );
   if ( !path ) {
       error = 1;
       StringStream s;
       s << "Lua Error: Wrong JNI parameter file";       
       errorText = s.str();
       env->ReleaseStringUTFChars(file, path);
       return;
   }

   std::ifstream fs;
   fs.open( path );

   if ( !fs.is_open() ) {
       error = 1;
       StringStream s;
       s << "Wrong file path ";
       s << path;
       errorText = s.str();
       env->ReleaseStringUTFChars(file, path);
       return;
   }
#if DEBUG_MODE        
   std::cout<<"Debug: "<<"-3 BOM detected"<<"\n";   
#endif
   fs.seekg (0, fs.end);
   size_t fileLen = (size_t)fs.tellg();

   const unsigned utf8offset = 3;
#if DEBUG_MODE        
   std::cout<<"Debug: "<<"-2 BOM detected"<<"\n";   
#endif

   fs.seekg (0, fs.beg);  

   if( fileLen > utf8offset ){
#ifdef   ENDIAN_LITTLE
       const  String UTF8BOM = "\xEF\xBB\xBF";
#endif

#ifdef BIG_LITTLE        
       const  String UTF8BOM = "\xBF\xBB\xEF";
#endif
#if DEBUG_MODE        
       std::cout<<"Debug: "<<"-1 BOM detected"<<"\n";   
#endif
      
      
       String line;
       getline(fs, line);
#if DEBUG_MODE        
       std::cout<<"Debug: "<<"line"<<line.length()<<"\n";   
#endif
       if( line.length() >= 3 ){

           String bomFind(line.begin(), line.begin() + 3);
           std::size_t found = bomFind.find(UTF8BOM);       

           if( found != std::string::npos ){
                fs.seekg ( utf8offset );        
    #if DEBUG_MODE        
                std::cout<<"Debug: "<<"BOM detected"<<"\n";   
    #endif
           }
           else
               fs.seekg (0, fs.beg);
       }          
      
   }    
  
   String buffer;    
   buffer.resize( fileLen, ' ');
  
   fs.read ( &buffer[0], fileLen );  

   fs.close();
#if DEBUG_MODE        
   //std::cout<<"Debug: " << "Buffer  :" << buffer << "\n";        
#endif
   int er = luaL_loadbuffer(Lua, buffer.c_str(), buffer.length(), "line");// ||
   
   if( er == 0 ){

       er = lua_pcall(Lua, 0, LUA_MULTRET, 0);    

#if DEBUG_MODE        
       std::cout<<"Debug: " << "Buffer execute error :" << error << "\n";        
#endif
   }

   if( er != 0 ){
       error = 1;
       errorText = lua_tostring( Lua, -1 );  
#if DEBUG_MODE        
       std::cout<<"Debug: " << "error :" << errorText << "\n";        
#endif
       lua_pop(Lua, 1); 
   }
  

   env->ReleaseStringUTFChars(file, path);	
}

JNIEXPORT void JNICALL Java_com_tinta_common_lua_tintaLua_executeBuffer
(JNIEnv *env, jobject obj, jstring buffer, jint len) {


#if DEBUG_MODE        
   // std::cout<<"executeBuffer step 1"<<"\n";        
#endif

    if( !env )
        return;   

    if( !Lua ){
#if DEBUG_MODE        
        //std::cout<<"executeBuffer step 2"<<"\n";        
#endif
        Java_com_tinta_common_lua_tintaLua_init(env,obj);
    }



    error  = -1;
    errorText = "";

    if( len <= 0 ){
        error = 1;
        StringStream s;
        s << "Lua Error: Buffer length == 0";       
        errorText = s.str();    

#if DEBUG_MODE        
        //std::cout<<"executeBuffer step 3"<<"\n";        
#endif

        return;
    }
    

	const char *str = env->GetStringUTFChars( buffer, NULL );

    std::string buff(str);

    env->ReleaseStringUTFChars(buffer, str);

#if DEBUG_MODE        
    std::cout<<buff<<"\n";            
#endif

	if ( str == NULL ) {
        error = 1;
        StringStream s;
        s << "Lua Error: Wrong JNI parameter buffer";       
        errorText = s.str();   

#if DEBUG_MODE        
        //std::cout<<"executeBuffer step 4"<<"\n";        
#endif
	    return;
	}

#if DEBUG_MODE        
   // std::cout<<"executeBuffer step 4_1"<<"\n";        
#endif

#if DEBUG_MODE        
    //if(str){       
      //  std::cout<<"executeBuffer step 4_1_1 "<<len<<"\n";        
    //}
#endif
	int er =  luaL_loadbuffer(Lua, buff.c_str(), buff.length(), "line");// ||  
    
#if DEBUG_MODE        
    //std::cout<<"executeBuffer step 4_2"<<"\n";        
#endif

//#ifndef DEBUG_MODE 
   
//#endif

#if DEBUG_MODE        
    //std::cout<<"executeBuffer step 5"<<"\n";        
#endif
    if( er == 0 ){

        er = lua_pcall(Lua, 0, LUA_MULTRET, 0);    
#if DEBUG_MODE        
        //std::cout<<"executeBuffer step 5_1"<<"\n";        
#endif
    }

#if DEBUG_MODE        
    //std::cout<<"executeBuffer step 6"<<"\n";        
#endif
    if( er != 0 ){
        error = 1;
        errorText = lua_tostring( Lua, -1 );  
        lua_pop(Lua, 1);  
#if DEBUG_MODE        
        //std::cout<<"executeBuffer step 7"<<"\n";        
#endif
    }   

    
	

}



JNIEXPORT jint JNICALL Java_com_tinta_common_lua_tintaLua_getGlobInteger
(JNIEnv *env, jobject obj, jstring name) {
   
    long val = 0;
    if( !Lua ){
        Java_com_tinta_common_lua_tintaLua_init(env,obj);
    }
    error = -1;
    const char *str = env->GetStringUTFChars( name, NULL );

    if ( str == NULL ) {
        error = 1;
        StringStream s;
        s << "Lua Error: Wrong JNI parameter name";       
        errorText = s.str();        
        return 0;
    }

    lua_getglobal(Lua, str);
    if ( !lua_isinteger( Lua, -1 ) ){
            error = 1;
            StringStream s;
            s << "Wrong integer ";
            s << str;
            errorText = s.str();
            
            env->ReleaseStringUTFChars(name, str);
            return 0;
    }
    else       
        val =  (long)lua_tointeger( Lua, 1 );

#if DEBUG_MODE        
    std::cout<<"Debug: "<<"Int Value :"<<val<<"\n";    
#endif

     lua_pop(Lua, 1);
    

    return val;
}

JNIEXPORT jintArray JNICALL Java_com_tinta_common_lua_tintaLua_getGlobIntArray
( JNIEnv *env, jobject obj, jstring name ) {

    if( !env )
        return NULL;

    if( !Lua ){
        Java_com_tinta_common_lua_tintaLua_init(env,obj);
    }
    const char *str = env->GetStringUTFChars( name, NULL );
    
    if ( str == NULL ) {
        error = 1;
        StringStream s;
        s << "Lua Error: Wrong JNI parameter name";       
        errorText = s.str();        
        return NULL;
    }

    lua_getglobal( Lua, str );   

    if( !lua_istable( Lua, -1 ) ){
        lua_pop( Lua, 1 );  
        error = 1;
        StringStream os;            
        os << "Wrong table in the expression \n";
        os << str;        
        errorText = os.str();
        return NULL;
    }

    unsigned array_size = lua_rawlen( Lua, 1 );

#if DEBUG_MODE        
    std::cout<<"Debug: "<<str<<" Int array size is: "<<array_size<<"\n";    
#endif

    if( array_size == 0 ){
        lua_pop( Lua, 1 );  
        return NULL;
    }

    std::vector<jint> rezVec;
    for( size_t count  = 1; count <= array_size; count++ ){
        lua_rawgeti( Lua, 1, count );
        rezVec.push_back((long)lua_tointeger( Lua, -1 ) );
    }	
   
    lua_pop( Lua, (int)(array_size) + 1 );  

    jintArray result;
    result = env->NewIntArray( array_size );
    if ( result == NULL ) {
        lua_pop( Lua, 1 );
        return NULL;
    }
   
    env->SetIntArrayRegion( result, 0, array_size, &rezVec[0]);
    env->ReleaseStringUTFChars(name, str);
    return result;
}


JNIEXPORT jstring JNICALL Java_com_tinta_common_lua_tintaLua_getGlobString
(JNIEnv *env, jobject obj, jstring name){

    
    if( !Lua )
        Java_com_tinta_common_lua_tintaLua_init(env,obj);
    String val;
    error = -1;
    const char *str = env->GetStringUTFChars( name, NULL );

    if ( str == NULL ) {
        error = 1;
        StringStream s;
        s << "Lua Error: Wrong JNI parameter name";       
        errorText = s.str();        
        return NULL;
    }

    lua_getglobal(Lua, str);
    if ( !lua_isstring( Lua, -1 ) ){
        error = 1;
        StringStream s;
        s << "Wrong string ";
        s << str;
        errorText = s.str();
    }
    else       
        val =   lua_tostring(Lua, -1);
   lua_pop(Lua, 1);
   env->ReleaseStringUTFChars(name, str);
   return env->NewStringUTF( val.c_str() );
}

JNIEXPORT jfloat JNICALL Java_com_tinta_common_lua_tintaLua_getGlobFloat
(JNIEnv *env, jobject obj, jstring name){
    
    if( !Lua ){
        Java_com_tinta_common_lua_tintaLua_init(env,obj);
    }
    float val = 0.f;
    error = -1;
    const char *str = env->GetStringUTFChars( name, NULL );

    if ( str == NULL ) {
        error = 1;
        StringStream s;
        s << "Lua Error: Wrong JNI parameter name";       
        errorText = s.str();        
        return 0.f;
    }
    lua_getglobal(Lua, str);
    if ( !lua_isnumber( Lua, -1 ) ){
        error = 1;
        StringStream s;
        s << "Wrong float ";
        s << str;
        errorText = s.str();
    }
    else       
        val = static_cast<float> (lua_tonumber(Lua, -1)); 

    lua_pop(Lua, 1);
    env->ReleaseStringUTFChars(name, str);
    return val;
}

JNIEXPORT jdouble JNICALL Java_com_tinta_common_lua_tintaLua_getGlobDouble
(JNIEnv *env, jobject obj, jstring name){

    if( !Lua ){
        Java_com_tinta_common_lua_tintaLua_init(env,obj);
    }
    double val = 0.;
    error = -1;
    const char *str = env->GetStringUTFChars( name, NULL );

    if ( str == NULL ) {
        error = 1;
        StringStream s;
        s << "Lua Error: Wrong JNI parameter name";       
        errorText = s.str();        
        return 0.f;
    }
    lua_getglobal(Lua, str);
    if ( !lua_isnumber( Lua, -1 ) ){
        error = 1;
        StringStream s;
        s << "Wrong double ";
        s << str;
        errorText = s.str();
    }
    else       
        val = static_cast<double> (lua_tonumber(Lua, -1)); 

    lua_pop(Lua, 1);
    env->ReleaseStringUTFChars(name, str);
    return val;
}

JNIEXPORT jboolean JNICALL Java_com_tinta_common_lua_tintaLua_getGlobBolean
(JNIEnv *env, jobject obj, jstring name){

    if( !Lua ){
        Java_com_tinta_common_lua_tintaLua_init(env,obj);
    }
    jboolean val = 0;
    error = -1;
    const char *str = env->GetStringUTFChars( name, NULL );

    if ( str == NULL ) {
        error = 1;
        StringStream s;
        s << "Lua Error: Wrong JNI parameter name";       
        errorText = s.str();        
        return 0;
    }
    lua_getglobal(Lua, str);
    if ( !lua_isboolean( Lua, -1 ) ){
        error = 1;
        StringStream s;
        s << "Wrong boolean ";
        s << str;
        errorText = s.str();
    }
    else       
        val = static_cast<jboolean> (lua_toboolean(Lua, -1)); 

    lua_pop(Lua, 1);
    env->ReleaseStringUTFChars(name, str);
    return val;
}


JNIEXPORT jfloatArray JNICALL Java_com_tinta_common_lua_tintaLua_getGlobFloatArray
( JNIEnv *env, jobject obj, jstring name ) {

    if(!env)
        return NULL;

    if( !Lua ){
        Java_com_tinta_common_lua_tintaLua_init(env,obj);
    }
    const char *str = env->GetStringUTFChars( name, NULL );

    if ( str == NULL ) {
        error = 1;
        StringStream s;
        s << "Lua Error: Wrong JNI parameter name";       
        errorText = s.str();        
        return NULL;
    }

    lua_getglobal( Lua, str );   

    if( !lua_istable(Lua, -1) ){
        lua_pop( Lua, 1 );  
        error = 1;
        StringStream os;            
        os << "Wrong table in the expression \n";
        os << str;        
        errorText = os.str();
        return NULL;
    }

    unsigned array_size = lua_rawlen(Lua, 1);

#if DEBUG_MODE        
    std::cout<<"Debug: "<<"Float array size is: "<<array_size<<"\n";    
#endif

    if( array_size == 0 )	{
        lua_pop( Lua, 1 );  
        return NULL;
    }

    std::vector<jfloat> rezVec;
    for( size_t count  = 1; count <= array_size; count++){
        lua_rawgeti(Lua, 1, count);
        rezVec.push_back((float)lua_tonumber( Lua, -1 ) );

    }	

    lua_pop( Lua, (int)(array_size) + 1 );  

    jfloatArray result;
    result = env->NewFloatArray( array_size );
    if ( result == NULL ) {
        lua_pop( Lua, 1 );
        return NULL;
    }

    env->SetFloatArrayRegion( result, 0, array_size, &rezVec[0]);
    env->ReleaseStringUTFChars(name, str);
    return result;
}

JNIEXPORT jdoubleArray JNICALL Java_com_tinta_common_lua_tintaLua_getGlobDoubleArray
( JNIEnv *env, jobject obj, jstring name ) {

    if(!env)
        return NULL;

    if( !Lua ){
        Java_com_tinta_common_lua_tintaLua_init(env,obj);
    }
    const char *str = env->GetStringUTFChars( name, NULL );

    if ( str == NULL ) {
        error = 1;
        StringStream s;
        s << "Lua Error: Wrong JNI parameter name";       
        errorText = s.str();        
        return NULL;
    }

    lua_getglobal( Lua, str );   

    if( !lua_istable(Lua, -1) ){
        lua_pop( Lua, 1 );  
        error = 1;
        StringStream os;            
        os << "Wrong table in the expression \n";
        os << str;        
        errorText = os.str();
        return NULL;
    }

    unsigned array_size = lua_rawlen(Lua, 1);

#if DEBUG_MODE        
    std::cout<<"Debug: "<<"Double array size is: "<<array_size<<"\n";    
#endif

    if( array_size == 0 )	{
        lua_pop( Lua, 1 );  
        return NULL;
    }

    std::vector<jdouble> rezVec;
    for( size_t count  = 1; count <= array_size; count++){
        lua_rawgeti(Lua, 1, count);
        rezVec.push_back((double)lua_tonumber( Lua, -1 ) );

    }	

    lua_pop( Lua, (int)(array_size) + 1 );  

    jdoubleArray result;
    result = env->NewDoubleArray( array_size );
    if ( result == NULL ) {
        lua_pop( Lua, 1 );
        return NULL;
    }

    env->SetDoubleArrayRegion( result, 0, array_size, &rezVec[0] );
    env->ReleaseStringUTFChars(name, str);
    return result;
}

JNIEXPORT jbooleanArray JNICALL Java_com_tinta_common_lua_tintaLua_getGlobBooleanArray
( JNIEnv *env, jobject obj, jstring name ) {

    if(!env)
        return NULL;

    if( !Lua ){
        Java_com_tinta_common_lua_tintaLua_init(env,obj);
    }
    const char *str = env->GetStringUTFChars( name, NULL );

    if ( str == NULL ) {
        error = 1;
        StringStream s;
        s << "Lua Error: Wrong JNI parameter name";       
        errorText = s.str();            
        return NULL;
    }

    lua_getglobal( Lua, str );   

    if( !lua_istable(Lua, -1) ){
        lua_pop( Lua, 1 );  
        error = 1;
        StringStream os;            
        os << "Wrong table in the expression \n";
        os << str;        
        errorText = os.str();
        return NULL;
    }

    unsigned array_size = lua_rawlen(Lua, 1);

#if DEBUG_MODE        
    std::cout<<"Debug: "<<"Boolean array size is: "<<array_size<<"\n";    
#endif

    if( array_size == 0 )	{
        lua_pop( Lua, 1 );  
        return NULL;
    }

    std::vector<jboolean> rezVec;
    for( size_t count  = 1; count <= array_size; count++){
        lua_rawgeti(Lua, 1, count);
        rezVec.push_back((jboolean)lua_toboolean( Lua, -1 ) );

    }	

    lua_pop( Lua, (int)(array_size) + 1 );  

    jbooleanArray result;
    result = env->NewBooleanArray( array_size );
    if ( result == NULL ) {
        lua_pop( Lua, 1 );
        return NULL;
    }

    env->SetBooleanArrayRegion( result, 0, array_size, &rezVec[0] );
    env->ReleaseStringUTFChars(name, str);
    return result;
}


JNIEXPORT jobjectArray JNICALL Java_com_tinta_common_lua_tintaLua_getGlobStringArray
(JNIEnv *env, jobject jobj, jstring name ){

    if(!env)
        return NULL;

    if( !Lua ){
        Java_com_tinta_common_lua_tintaLua_init(env,jobj);
    }
    const char *str = env->GetStringUTFChars( name, NULL );

    if ( str == NULL ) {
        error = 1;
        StringStream s;
        s << "Lua Error: Wrong JNI parameter name";       
        errorText = s.str();        
        return NULL;
    }

    lua_getglobal( Lua, str );   

    if( !lua_istable(Lua, -1) ){
        lua_pop( Lua, 1 );  
        error = 1;
        StringStream os;            
        os << "Wrong table in the expression \n";
        os << str;        
        errorText = os.str();
        return NULL;
    }

    std::vector<String> stringVec;

    unsigned array_size = lua_rawlen(Lua, 1);
    if( array_size == 0 )
        return NULL;

#if DEBUG_MODE        
    std::cout<<"Debug: "<<"String array size is: "<<array_size<<"\n";    
#endif

    for( unsigned i = 1; i <= array_size; i++ ){

        lua_rawgeti(Lua, 1, i);
        if( lua_isstring(Lua, -1) )
            stringVec.push_back(  lua_tostring(Lua, -1) );

        lua_pop(Lua, 1);
    }
    lua_pop( Lua, 1 ); //pop table


    jobjectArray ret;   

    ret= (jobjectArray)env->NewObjectArray(array_size,env->FindClass("java/lang/String"),env->NewStringUTF(""));

    for(unsigned i=0;i<array_size;i++) env->SetObjectArrayElement(ret,i,env->NewStringUTF( stringVec[i].c_str()));
    env->ReleaseStringUTFChars(name, str);

    return(ret);
}


JNIEXPORT jstring JNICALL Java_com_tinta_common_lua_tintaLua_error
( JNIEnv *env, jobject obj ){  
   
    if( !env || error  == -1 || errorText.length() == 0)
        return NULL;    

#if DEBUG_MODE        
         std::cout<<"Debug: "<<"Error: "<<errorText<<"\n";         
#endif

   // lua_pop( Lua, 1 );
        
    error = -1;    
    StringStream os;            
    os << "Error... ";
    os << errorText;            

    return env->NewStringUTF( os.str().c_str() );

}

JNIEXPORT void JNICALL Java_com_tinta_common_lua_tintaLua_setError
( JNIEnv *env, jobject obj, jstring error_ ) {

    if( !Lua ){
        return;
    }
    
    const char *str = env->GetStringUTFChars( error_, NULL );

    if ( str == NULL ) {
        error = 1;
        StringStream s;
        s << "Lua Error: Wrong JNI parameter errorText";       
        errorText = s.str();        
    }   
    else {        
        std::string msg(str);
        env->ReleaseStringUTFChars( error_, str );        
        error = 1;
        errorText = msg;               
    }    
}


JNIEXPORT jint JNICALL Java_com_tinta_common_lua_tintaLua_stackSize
(JNIEnv *env, jobject obj) {

    if(!env)
        return NULL;

    if( !Lua ){
        Java_com_tinta_common_lua_tintaLua_init(env,obj);
    }

    return  (long)lua_gettop(Lua);         
}

JNIEXPORT jint JNICALL Java_com_tinta_common_lua_tintaLua_getInt
(JNIEnv *env, jobject obj, jint index ) {
    error = -1;
    if( !env )
        return NULL;

    if( !Lua ){
        Java_com_tinta_common_lua_tintaLua_init(env,obj);
    }

    if( !lua_isinteger(Lua, (int)index) ){
        error = 1;
        StringStream s;
        s << "Wrong integer index: ";
        s << index;
        errorText = s.str();
        return 0;
    }
    
    return (long)lua_tointeger(Lua, (int)index);     
}


JNIEXPORT jfloat JNICALL Java_com_tinta_common_lua_tintaLua_getFloat
(JNIEnv *env, jobject obj, jint index ) {

    error = -1;
    if( !env )
        return NULL;

    if( !Lua ){
        Java_com_tinta_common_lua_tintaLua_init(env,obj);
    }

    if( !lua_isnumber(Lua, (int)index) ){
        error = 1;
        StringStream s;
        s << "Wrong float index: ";
        s << index;
        errorText = s.str();
        return 0;
    }

    return (float)lua_tonumber(Lua, (int)index);     
    
}


JNIEXPORT jboolean JNICALL Java_com_tinta_common_lua_tintaLua_getBoolean
(JNIEnv *env, jobject obj, jint index ) {

    error = -1;
    if( !env )
        return NULL;

    if( !Lua ){
        Java_com_tinta_common_lua_tintaLua_init(env,obj);
    }

    if( !lua_isboolean(Lua, (int)index) ){
        error = 1;
        StringStream s;
        s << "Wrong boolean index: ";
        s << index;
        errorText = s.str();
        return 0;
    }

    return (jboolean)lua_toboolean(Lua, (int)index);     

}


JNIEXPORT jdouble JNICALL Java_com_tinta_common_lua_tintaLua_getDouble
(JNIEnv *env, jobject obj, jint index ) {

    error = -1;
    if( !env )
        return NULL;

    if( !Lua ){
        Java_com_tinta_common_lua_tintaLua_init(env,obj);
    }

    if( !lua_isnumber(Lua, (int)index) ){
        error = 1;
        StringStream s;
        s << "Wrong double index: ";
        s << index;
        errorText = s.str();
        return 0;
    }

    return (double)lua_tonumber(Lua, (int)index);     

}


JNIEXPORT jstring JNICALL Java_com_tinta_common_lua_tintaLua_getString
(JNIEnv *env, jobject obj, jint index ) {

    error = -1;

    if( !env )
        return NULL;

    if( !Lua )
        Java_com_tinta_common_lua_tintaLua_init(env,obj);
    String val;
    error = -1;
    
    
    if ( !lua_isstring( Lua, (int)index)  ){
        error = 1;
        StringStream s;
        s << "Wrong string index ";
        s << index;
        errorText = s.str();
        return NULL;
    }      

#if DEBUG_MODE                 
    std::cout<<"Debug: "<<"getString "<<lua_tostring(Lua, (int)index)<<"\n";
#endif
    
    return env->NewStringUTF( lua_tostring(Lua, (int)index) );
}


JNIEXPORT void JNICALL Java_com_tinta_common_lua_tintaLua_pushVal__I
( JNIEnv *env, jobject obj, jint value ) {
    error = -1;
    if( !env )
        return;

    if( !Lua ){
        Java_com_tinta_common_lua_tintaLua_init(env,obj);
    }
#if DEBUG_MODE                 
    std::cout<<"Debug: "<<"pushInt "<<value<<"\n";
#endif
    lua_pushinteger(Lua, (LuaInt)value);
}


JNIEXPORT void JNICALL Java_com_tinta_common_lua_tintaLua_pushVal__F
( JNIEnv *env, jobject obj, jfloat value ) {
    error = -1;
    if( !env )
        return;

    if( !Lua ){
        Java_com_tinta_common_lua_tintaLua_init(env,obj);
    }

    lua_pushnumber(Lua, (LuaReal)value);
}

JNIEXPORT void JNICALL Java_com_tinta_common_lua_tintaLua_pushVal__D
( JNIEnv *env, jobject obj, jdouble value ) {
    error = -1;
    if( !env )
        return;

    if( !Lua ){
        Java_com_tinta_common_lua_tintaLua_init(env,obj);
    }

    lua_pushnumber(Lua, (LuaReal)value);
}

JNIEXPORT void JNICALL Java_com_tinta_common_lua_tintaLua_pushVal__Z
( JNIEnv *env, jobject obj, jboolean value ) {
    error = -1;
    if( !env )
        return;

    if( !Lua ){
        Java_com_tinta_common_lua_tintaLua_init(env,obj);
    }

    lua_pushboolean( Lua, value > 0 );
}

JNIEXPORT void JNICALL Java_com_tinta_common_lua_tintaLua_pushVal__Ljava_lang_String_2
( JNIEnv *env, jobject obj, jstring value ) {

    error = -1;
    if( !env )
        return;

    if( !Lua ){
        Java_com_tinta_common_lua_tintaLua_init(env,obj);
    }

    const char *strVal = env->GetStringUTFChars( value, NULL );

    if ( strVal == NULL ) {
         error = 1;
         StringStream s;
         s << "Lua Error: Wrong JNI parameter name";       
         errorText = s.str();
         env->ReleaseStringUTFChars(value, strVal);        
         return;
    }

    lua_pushstring( Lua, strVal );

    env->ReleaseStringUTFChars(value, strVal);
}

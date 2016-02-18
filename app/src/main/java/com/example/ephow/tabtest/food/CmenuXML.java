package com.example.ephow.tabtest.food;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

/**
 * Created by 1 on 2015/9/14.
 */


public class CmenuXML {

    //XmlPullParser dd;

    //"ddd".equals()




    interface inA{

        public void say(String name);
    }


    class AImpl implements inA{

        @Override
        public void say(String name){

            System.out.println("Hello " + name);

            priSay(name);

        }

        public void priSay(String name){

            System.out.println("private Hello " + name);

        }

    }

    public void M1() {


        //接口类型的变量a被赋值实例化的接口派生类
        inA a ;//= new AImpl();

        //但是只能调用到派生类中的自己接口的实施方法,而调用不到派生类中的其它方法
        //a.say("mmm");

        AImpl ai = new AImpl();

        ai.priSay("ok");

        a = ai;

        a.say("mk");


    }

}

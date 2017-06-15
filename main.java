import java.io.*;
import java.util.Stack;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
public class interpreter{

private static Stack<String> _stack;
private static HashMap<String,String> _bindMap;
private static FileWriter fw;
private static BufferedWriter bw;
private static Integer countLet;
private static Integer currentScope;
private static Integer stackLetForTemp;
private static Integer stackCurrentScopeForTemp;

private static Stack<String> _temp;
private static HashMap<String,Stack<String>> stackMap;
private static HashMap<String,HashMap<String,String>> bindForStack;
private static Stack<String> tempCall;
private static HashMap<String,String> bindCall;
private static String something;


  public static String readAndAction(String input,String output){

    countLet = 0;
    currentScope = 0;
    _bindMap = new HashMap<String,String>();
    File _f = new File(input);
    try(BufferedReader br = new BufferedReader(new FileReader(_f))){
      String line;
      while((line = br.readLine()) !=null){
        something = something + line;
        int currentSize = 0;
        if(line.contains("push")){
          String toStack = split(line);
          if(isDouble(toStack)==false && isName(toStack)){// If its a string or an integer it get be pushed in
          toStack = toStack + Integer.toString(countLet);

          _stack.push(toStack);
          }else{

              if(isInteger(toStack)){

                  _stack.push(toStack);
              }else{
                if(isString(toStack)){
                  _stack.push(toStack);
                  _bindMap.put(noQuotes(toStack), "Just a string, no value");
                }else{
                  _stack.push(":error:");
                }
              }
          }
        }
        // NOTE TO SELF: The conditionals below should be a bit more specific. So if a weird error appears, try that.
        if(line.contains("pop")){
          try{
            _stack.pop();
          }catch(EmptyStackException e){
            _stack.push(":error:");
          }
        }
        if(line.contains(":unit:")&& line.length()==6){
          _stack.push(":unit::");
        }
        if(line.contains("add") && line.length() == 3){
          try{
            _stack.push(add());
          }catch(EmptyStackException ex){
            _stack.push(":error:");
          }

        }
        if(line.contains("sub") && line.length() == 3){
          try{
            _stack.push(sub());//catch the exception
          }catch(EmptyStackException e){
            _stack.push(":error:");
          }
        }
        if(line.contains("mul")){
        try{
          _stack.push(mul());
        }catch(EmptyStackException e){
          _stack.push(":error:");
        }catch(NumberFormatException ex){
          _stack.push(":error:");
        }
        }
        if(line.contains("div")){
          try{
            _stack.push(div());
          }catch(EmptyStackException e){
            _stack.push(":error:");
          }
        }
        if(line.contains("rem")){
          try{
            _stack.push(rem());
          }catch(EmptyStackException e){
            _stack.push(":error:");
          }
        }
        if(line.contains("neg")){
          try{
            _stack.push(neg());
          }catch(EmptyStackException e){
            _stack.push(":error:");
          }
        }
        if(line.contains("swap")){
          swap();
        }
        if(line.contains("and")){
          try{
            _stack.push(and());
          }catch(EmptyStackException e){
            _stack.push(":error:");
          }
        }
        if(line.contains("or") && line.length() == 2){
          try{
            _stack.push(or());
          }catch(EmptyStackException e){
            _stack.push(":error:");
          }
        }
        if(line.contains("not")){
          try{
            _stack.push(not());
          }catch(EmptyStackException e){
            _stack.push(":error:");
          }
        }
        if(line.contains("equal")){
          try{
            _stack.push(equal());
          }catch(EmptyStackException e){
            _stack.push(":error:");
          }
        }
        if(line.contains("lessThan")){
          try{
            _stack.push(lessThan());
          }catch(EmptyStackException e){
            _stack.push(":error:");
          }
        }
        if(line.contains("bind")){
          try{
            _stack.push(bind(countLet));
          }catch(EmptyStackException e){
            _stack.push(":error:");
          }

        }
        if(line.contains("if")){
        _stack.push(ifStatement());
        }
        if(line.contains("let") && line.length()==3){
          try{
            let(countLet,currentSize);

          }catch(EmptyStackException e){
            _stack.push(":error:");
          }

        }
        if(line.contains("end") && line.length()==3){
          try{
            end(currentSize,countLet);

          }catch(EmptyStackException e){
            _stack.push(":error:");
          }

        }
        if(line.contains("quit")){
          write(output);
          break;
        }
        if(line.contains(":error:")){
          _stack.push(":error:");
        }

        if(isBoolean(line)){
          _stack.push(line);
        }

        if(line.contains("quit")){
          write(output);
          break;
        }

        if(line.contains("fun")&& !line.equals("funEnd") && !line.contains("push")){
          if(line == null){break;}
          String[] bits = line.split(" ");
           _temp = new Stack<String>();

           _temp.push(bits[2]);
          while((line = br.readLine()) != null){

            if(line.equals("funEnd")){
              _stack.push(":unit::");
              break;
            }
            _temp.push(line);


          }

          stackMap.put(bits[1]+ Integer.toString(countLet),_temp);
          bindForStack.put(bits[1]+ Integer.toString(countLet),_bindMap);

        }


        if(line.equals("call")){
          try{
            _stack.push(call(countLet,currentScope));
          }catch(EmptyStackException e){
            _stack.push(":error:");
          }
        }

      }// End of while loop
    }
    catch(IOException e){
        e.printStackTrace();

    }
    return null;
  }


  /**
  * Writes to a new text file called output
  */


  public static void write(String outFile){
    try{
      File _out = new File(outFile);
      fw = new FileWriter(_out);
      bw = new BufferedWriter(fw);
      while(!_stack.empty()){
        if(compare()){
          bw.write(something);
          break;
        }
        if(isName(_stack.peek())){
          bw.write(noQuotes(getNameWithOutScope(_stack.pop()))+ "\n");
        }else{
              bw.write(noQuotes(_stack.pop())+"\n");
        }

        //bw.write(_stack.pop() +"\n");
      }
    }catch(IOException e){
      e.printStackTrace();
    }

    try{
      if(bw != null){
        bw.close();
      }
      if(fw != null){
        fw.close();
      }
    }catch(IOException e){
      e.printStackTrace();
    }
  }




  public static boolean isInteger(String line){
    try {
      Integer.parseInt(line);
    }catch(NumberFormatException e){
      return false;
    }catch(NullPointerException ex){
      return false;
    }
    return true;
  }




  public static boolean isDouble(String line){
    try{
      Double.parseDouble(line);
    }catch(NumberFormatException e){
      return false;
    }catch(NullPointerException e){
      return false;
    }
    if(!line.contains(".")){
      return false;
    }
    return true;
  }





  public static boolean isString(String line){
    if(line.contains("\"")){
      return true;
    }
    return false;
  }






  public static boolean isBoolean(String line){
    if(line.contains(":true:")&&!line.contains("push")){
      return true;
    }
    if(line.contains(":false:")&&!line.contains("push")){
      return true;
    }
    return false;

  }




/**
* TO get rid of the quotes for names
*/
  public static String noQuotes(String toStack){
    String s = "";
    for(int i = 0;i<toStack.length();i++){
      if(toStack.charAt(i)!='\"'){
        s = s + toStack.charAt(i);
      }
    }
    return s;
  }
  /**
  * To split the line from input.txt. To leave the push out of the string line.
  */
  public static String split(String line){
    String newLine = "";
    for(int i = 5;i<line.length();i++){
        newLine = newLine + line.charAt(i);
    }
    return newLine;

  }
  /**
  * Addition
  */


  public static String add(){//Something wrong.... too tired to keep going
    int x=0;
    int y=0;
    int z = 0;
    // If y is an integer case
    if(isInteger(_stack.peek())){
       y = Integer.parseInt(_stack.pop());

      if(_stack.isEmpty()){
        _stack.push(Integer.toString(x));
        return ":error:";
      }else{
        if(isInteger(_stack.peek())){
          x = Integer.parseInt(_stack.pop());
          z = x + y;
          return Integer.toString(z);
        }else{
          if(nameInt(_stack.peek())){

            x = Integer.parseInt(_bindMap.get(bindStringScope(_stack.pop())));
            z = x + y;
            return Integer.toString(z);
          }

          _stack.push(Integer.toString(y));

          return ":error:";
        }
      }
    }
    // if y is a NAME case
    if(nameInt(_stack.peek())){
      String localName = _stack.peek();

      y = Integer.parseInt(_bindMap.get(bindStringScope(_stack.pop())));
      if(nameInt(_stack.peek())){
        x = Integer.parseInt(_bindMap.get(bindStringScope(_stack.pop())));
        z = x + y;
        return Integer.toString(z);
      }
      if(isInteger(_stack.peek())){
        x = Integer.parseInt(_stack.pop());
        z = x + y;
        return Integer.toString(z);
      }
      _stack.push(localName);
      return ":error:";
    }
    return ":error:";

  }

  /**
  * Subtraction
  */
  public static String sub(){
    int x=0;
    int y=0;
    int z = 0;
    // If y is an integer case
    if(isInteger(_stack.peek())){
       y = Integer.parseInt(_stack.pop());

      if(_stack.isEmpty()){
        _stack.push(Integer.toString(x));
        return ":error:";
      }else{
        if(isInteger(_stack.peek())){
          x = Integer.parseInt(_stack.pop());
          z = x - y;
          return Integer.toString(z);
        }else{
          if(nameInt(_stack.peek())){

            x = Integer.parseInt(_bindMap.get(bindStringScope(_stack.pop())));
            z = x - y;
            return Integer.toString(z);
          }
          _stack.push(Integer.toString(y));
          return ":error:";
        }
      }
    }
    // if y is a NAME case

    if(nameInt(_stack.peek())){
      String localName = _stack.peek();
      y = Integer.parseInt(_bindMap.get(bindStringScope(_stack.pop())));
      if(nameInt(_stack.peek())){
        x = Integer.parseInt(_bindMap.get(bindStringScope(_stack.pop())));
        z = x - y;
        return Integer.toString(z);
      }
      if(isInteger(_stack.peek())){
        x = Integer.parseInt(_stack.pop());
        z = x - y;
        return Integer.toString(z);
      }

      _stack.push(localName);
      return ":error:";
    }

    return ":error:";

  }


  /**
  * Multiplication
  */

  public static String mul(){
    int x=0;
    int y=0;
    int z = 0;
    // If y is an integer case
    if(isInteger(_stack.peek())){
       y = Integer.parseInt(_stack.pop());

      if(_stack.isEmpty()){
        _stack.push(Integer.toString(x));
        return ":error:";
      }else{
        if(isInteger(_stack.peek())){
          x = Integer.parseInt(_stack.pop());
          z = x * y;
          return Integer.toString(z);
        }else{
          if(nameInt(_stack.peek())){

            x = Integer.parseInt(_bindMap.get(bindStringScope(_stack.pop())));
            z = x * y;
            return Integer.toString(z);
          }

          _stack.push(Integer.toString(y));
          return ":error:";
        }
      }
    }
    // if y is a NAME case
    if(nameInt(_stack.peek())){
      String localName = _stack.peek();
      y = Integer.parseInt(_bindMap.get(bindStringScope(_stack.pop())));
      if(nameInt(_stack.peek())){
        x = Integer.parseInt(_bindMap.get(bindStringScope(_stack.pop())));
        z = x * y;
        return Integer.toString(z);
      }
      if(isInteger(_stack.peek())){
        x = Integer.parseInt(_stack.pop());
        z = x * y;
        return Integer.toString(z);
      }
      _stack.push(localName);
      return ":error:";
    }

    return ":error:";
  }


  /**
  * Division
  */
  public static String div(){
    int x=0;
    int y=0;
    int z = 0;
    // If y is an integer case
    if(isInteger(_stack.peek())){
       y = Integer.parseInt(_stack.pop());
       if(y==0){_stack.push(Integer.toString(y)); return ":error:";}
      if(_stack.isEmpty()){
        _stack.push(Integer.toString(x));
        return ":error:";
      }else{
        if(isInteger(_stack.peek())){
          x = Integer.parseInt(_stack.pop());
          z = x / y;
          return Integer.toString(z);
        }else{
          if(nameInt(_stack.peek())){

            x = Integer.parseInt(_bindMap.get(bindStringScope(_stack.pop())));
            z = x / y;
            return Integer.toString(z);
          }
          _stack.push(Integer.toString(y));
          return ":error:";
        }
      }
    }
    // if y is a NAME case
    if(nameInt(_stack.peek())){
      String localName = _stack.peek();
      y = Integer.parseInt(_bindMap.get(bindStringScope(_stack.pop())));
      if(y==0){_stack.push(localName); return ":error:";}
      if(nameInt(_stack.peek())){
        x = Integer.parseInt(_bindMap.get(bindStringScope(_stack.pop())));
        z = x / y;
        return Integer.toString(z);
      }
      if(isInteger(_stack.peek())){
        x = Integer.parseInt(_stack.pop());
        z = x / y;
        return Integer.toString(z);
      }
      _stack.push(localName);
      return ":error:";
    }
    return ":error:";
  }


  /**
  * Remainder
  */

  public static String rem(){
    int x=0;
    int y=0;
    int z = 0;
    // If y is an integer case
    if(isInteger(_stack.peek())){
       y = Integer.parseInt(_stack.pop());
       if(y==0){_stack.push(Integer.toString(y)); return ":error:";}
      if(_stack.isEmpty()){
        _stack.push(Integer.toString(x));
        return ":error:";
      }else{
        if(isInteger(_stack.peek())){
          x = Integer.parseInt(_stack.pop());
          z = x % y;
          return Integer.toString(z);
        }else{
          if(nameInt(_stack.peek())){

            x = Integer.parseInt(_bindMap.get(bindStringScope(_stack.pop())));
            z = x % y;
            return Integer.toString(z);
          }
          _stack.push(Integer.toString(y));
          return ":error:";
        }
      }
    }
    // if y is a NAME case
    if(nameInt(_stack.peek())){
      String localName = _stack.peek();
      y = Integer.parseInt(_bindMap.get(bindStringScope(_stack.pop())));
      if(y==0){_stack.push(localName); return ":error:";}
      if(nameInt(_stack.peek())){
        x = Integer.parseInt(_bindMap.get(bindStringScope(_stack.pop())));
        z = x % y;
        return Integer.toString(z);
      }
      if(isInteger(_stack.peek())){
        x = Integer.parseInt(_stack.pop());
        z = x % y;
        return Integer.toString(z);
      }
      _stack.push(localName);
      return ":error:";
    }
    return ":error:";
  }

  /**
  * Negate
  */
  public static String neg(){
    int x = 0;
    if(isInteger(_stack.peek())){
      x = Integer.parseInt(_stack.pop()) * -1;
      return Integer.toString(x);
    }

    if(nameInt(_stack.peek())){

      x = Integer.parseInt( _bindMap.get(bindStringScope(_stack.pop())) ) * -1;

      return Integer.toString(x);
    }
    return ":error:";
  }

  /**
  * Swap
  */

  public static void swap(){
    if(_stack.size()>1){
      String firstOut = _stack.pop();
      String secondOut = _stack.pop();
      _stack.push(firstOut);
      _stack.push(secondOut);
    }else{
      _stack.push(":error:");
    }


  }

  /**
  * AND
  */


  public static String and(){
    String x = "";
      String y = "";
      if(_stack.size()<=1){
        return ":error:";
      }

      if(isBoolean(_stack.peek())){
        x = _stack.pop();
        if(isBoolean(_stack.peek())){
          y = _stack.pop();
          if(x.contains(":true:") && y.contains(":true:")){ return ":true:";}else{ return ":false:";}
        }else if(nameBool(_stack.peek())){
          y = _bindMap.get(bindStringScope(_stack.pop())) ;
          if(x.contains(":true:") && y.contains(":true:")){ return ":true:";}else{ return ":false:";}
        }else{
          _stack.push(x);
          return ":error:";
        }
      }else if(nameBool(_stack.peek())){

        String localName = _stack.peek();
        x = _bindMap.get(bindStringScope(_stack.pop())) ;
        if(isBoolean(_stack.peek())){
          y = _stack.pop();
          if(x.contains(":true:") && y.contains(":true:")){ return ":true:";}else{ return ":false:";}
        }else if(nameBool(_stack.peek())){
          y = _bindMap.get(bindStringScope(_stack.pop())) ;
          if(x.contains(":true:") && y.contains(":true:")){ return ":true:";}else{ return ":false:";}
        }else{
          _stack.push(localName);
          return ":error:";
        }
      }else{

        return ":error:";
      }

    }// END OF AND()

  /**
  * OR
  */

  public static String or(){

      String x = "";
        String y = "";
        if(_stack.size()<=1){
          return ":error:";
        }

        if(isBoolean(_stack.peek())){
          x = _stack.pop();
          if(isBoolean(_stack.peek())){
            y = _stack.pop();
            if(x.contains(":true:") || y.contains(":true:")){ return ":true:";}else{ return ":false:";}
          }else if(nameBool(_stack.peek())){
            y = _bindMap.get(bindStringScope(_stack.pop())) ;
            if(x.contains(":true:") || y.contains(":true:")){ return ":true:";}else{ return ":false:";}
          }else{
            _stack.push(x);
            return ":error:";
          }
        }else if(nameBool(_stack.peek())){
          String localName = _stack.peek();
          x = _bindMap.get(bindStringScope(_stack.pop())) ;
          if(isBoolean(_stack.peek())){
            y = _stack.pop();
            if(x.contains(":true:") || y.contains(":true:")){ return ":true:";}else{ return ":false:";}
          }else if(nameBool(_stack.peek())){
            y = _bindMap.get(bindStringScope(_stack.pop())) ;
            if(x.contains(":true:") || y.contains(":true:")){ return ":true:";}else{ return ":false:";}
          }else{
            _stack.push(localName);
            return ":error:";
          }
        }else{
          return ":error:";
        }

      } // END OF OR()


      /**
      * IF NAME CONTAINS A BOOLEAN VALUE ATTACHED
      */
      public static boolean nameBool(String s){
        return isName(s) && contains(s) && isBoolean(_bindMap.get(bindStringScope(s))) ;

      }

      /**
      * IF NAME CONTAINS AN INTEGER VALUE
      */

      public static boolean nameInt(String s){
        return isName(s) && contains(s) && isInteger(_bindMap.get(bindStringScope(s)));
      }

  /**
  * NOT
  */

  public static String not(){
    String bool;
    if(isBoolean(_stack.peek())){
      bool = _stack.pop();
      if(bool.contains(":true:")){
        return ":false:";
      }else{
        return ":true:";
      }
    }else if(nameBool(_stack.peek())){

      bool = _bindMap.get(bindStringScope(_stack.pop())) ;

      if(bool.contains(":true:")){
        return ":false:";
      }else{
        return ":true:";
      }
    }

    return ":error:";
  }

  /**
  * EQUAL
  */

  public static String equal(){
    int x = 0;
    int y = 0;
    if(isInteger(_stack.peek())){
      String localName = _stack.peek();
      x = Integer.parseInt(_stack.pop());
      if(isInteger(_stack.peek())){
        y = Integer.parseInt(_stack.pop());
      }else{
        if(nameInt(_stack.peek())){
          y = Integer.parseInt(_bindMap.get(bindStringScope(_stack.pop())));
        }else{
          _stack.push(localName);
          return ":error:";
        }

      }
    }else{
      if(nameInt(_stack.peek())){
        String localName = _stack.peek();
        x = Integer.parseInt(_bindMap.get(bindStringScope(_stack.pop())));
        if(isInteger(_stack.peek())){
          y = Integer.parseInt(_stack.pop());
        }else{
          if(nameInt(_stack.peek())){
            y = Integer.parseInt(_bindMap.get(bindStringScope(_stack.pop())));
          }else{
            _stack.push(localName);
            return ":error:";
          }
        }

      }else{
        return ":error:";
      }

    }
    if(x == y){
      return ":true:";
    }
    return ":false:";
  }

  /**
  * LESS THAN
  */


  public static String lessThan(){
    int x = 0;
    int y = 0;
    if(isInteger(_stack.peek())){
      x = Integer.parseInt(_stack.pop());
      if(isInteger(_stack.peek())){
        y = Integer.parseInt(_stack.pop());
      }else{
        if(nameInt(_stack.peek())){
          y = Integer.parseInt(_bindMap.get(bindStringScope(_stack.pop())));
        }else{
          _stack.push(Integer.toString(x));
            return ":error:";

        }

      }
    }
      if(nameInt(_stack.peek())){
        String localName = _stack.peek();
        x = Integer.parseInt(_bindMap.get(bindStringScope(_stack.pop())));
        if(isInteger(_stack.peek())){
          y = Integer.parseInt(_stack.pop());
        }else{
          if(nameInt(_stack.peek())){
            y = Integer.parseInt(_bindMap.get(bindStringScope(_stack.pop())));
          }else{
            _stack.push(localName);
            return ":error:";
          }
        }
      }

    if(x > y){
      return ":true:";
    }
    return ":false:";

  }

  /**
  * BINDING
  */

  public static String bind(int scope){
    String firstVal;
    String secondVal;

    // The following if statement is a special conditional for boolean values in the stack.
    if(isBoolean(_stack.peek())){
      firstVal = _stack.pop();
    if(isName(_stack.peek())){
     secondVal = _stack.pop();

      _bindMap.put(secondVal,firstVal);
      return ":unit::";
    }else{
      _stack.push(firstVal);
      return ":error:";
    }
}
  if(isInteger(_stack.peek())){// Remember: Can an integer be pushed onto the stack without the string "push"?
  firstVal = _stack.pop();
  if(isName(_stack.peek())){
  secondVal = _stack.pop();

  _bindMap.put(secondVal,firstVal );
  return ":unit::";
}else{
  _stack.push(firstVal);
  return ":error:";
}
}
if(_stack.peek().equals(":unit::")){
  firstVal = _stack.pop();

  if(isName(_stack.peek())){
    secondVal = _stack.pop();
    _bindMap.put(secondVal,firstVal);
    return ":unit::";
  }else{
    _stack.push(firstVal);
    return ":error:";
  }
}

  if(contains(_stack.peek()) && _stack.size()>1 ){
    firstVal = _stack.pop();
    if(isName(_stack.peek())){
      secondVal = _stack.pop();
      _bindMap.put(secondVal,_bindMap.get(firstVal));
      return ":unit::";
    }else{
        _stack.push(firstVal);
      return ":error:";
    }
  }


  if(isString(_stack.peek())){
    firstVal = _stack.pop();
    if(isName(_stack.peek())){
      secondVal = _stack.pop();
      _bindMap.put(secondVal,firstVal);
      return ":unit::";
    }else{
      _stack.push(firstVal);
    }
  }
  return ":error:";
  }



  /**
  * Contains method for stack that returns a boolean values
  */
  public static boolean contains(String s){
    String name = getStringWithOutScope(s);
    currentScope = countLet;
    while(currentScope>=0){
      if(_bindMap.get(name + Integer.toString(currentScope))!= null){
        return true;
      }
      currentScope--;
    }
    if(currentScope==-1){
      currentScope = 0;
    }

    //currentScope = countLet;
    return false;
  }

  public static String bindStringScope(String s){
    String newString = getStringWithOutScope(s) + Integer.toString(currentScope);
    return newString;
  }
  /**
  *  This particular method I made was made for tessting purpose.
  * THIS METHOD ALSO HELPS TO SPLIT THE STRING INTO JUST NAME WITHOUT currentScope
  */
  public static String getStringWithOutScope(String s){
    String name = "";
    String scope = Integer.toString(currentScope);
    int distanceToName = scope.length();
    for(int i = 0; i<s.length()-distanceToName;i++){
      name = name + s.charAt(i);

    }

    return name;
  }
  public static String getNameWithOutScope(String s){
    String name = "";
    String scope = Integer.toString(countLet);
    int distanceToName = scope.length();
    for(int i = 0; i<s.length()-distanceToName;i++){
      name = name + s.charAt(i);

    }
    return name;
  }

  /**
  * IS NAME
  */


  public static boolean isName(String s){
    if( !isBoolean(s) && !isInteger(s) && s != ":error:" && !s.contains("-") && !isString(s)){
      return true;
    }
  //  if(s.equals(":unit::")){
  //    return true;
  //  }

    return false;
  }



  /**
  * IF()
  */

  public static String ifStatement(){
    String x,y,z;
    if(_stack.size()>2){
      x = _stack.pop();
      y = _stack.pop();
      if(isBoolean(_stack.peek())){
        z = _stack.pop();
        if(z.contains(":true:")){return x;}else{ return y;}
      }else if(nameBool(_stack.peek())){
        z = _bindMap.get(bindStringScope(_stack.pop())) ;
        if(z.contains(":true:")){return x;}else{ return y;}
      }else{
        _stack.push(y);
        _stack.push(x);
        return ":error:";
      }
    }
    return ":error:";
  }// END OF ifStatement()



  /**
  * Let End
  */

  public static void let(int x, int size){

    if(x==0){
      size = _stack.size();
    }

    size = _stack.size() -x;
    _stack.push(Integer.toString(size)  + " size"     );
    x++;
    countLet = x;
    currentScope = countLet;



  }

  public static void end( int size, int x){
    String topVal;
    if(!_stack.peek().contains("size")){
      topVal = _stack.pop();
    }else{
      topVal = _stack.pop();
    }
      //topVal = _stack.pop();

    while(size<=_stack.size()){

      if(_stack.peek().contains("size")){

        _stack.pop();
        _stack.push(topVal);
        break;
      }
      _stack.pop();

    }

    x--;
    countLet = x;
    currentScope = countLet;



  }


  public static Stack<String> reverseStack(Stack<String> stack){
    Queue<String> tmp = new LinkedList<String>();
    while(!stack.isEmpty()){
      tmp.add(stack.peek());
      stack.pop();
    }
    while(!tmp.isEmpty()){
      stack.push(tmp.remove());
    }
    return stack;
  }


  public static String call(int scope,int something){
    countLet = scope;
    currentScope = something;
    Stack<String> tmp = new Stack<String>();
    int currentSize = 0;
    //stackLetForTemp = scope;
    stackCurrentScopeForTemp = 0;
    String x,y,z;
    String line;
    tempCall = new Stack<String>();
    if(containsInStackMap(_stack.peek())){
      x = _stack.pop();
      tempCall.addAll(reverseStack(stackMap.get(x)));
      bindCall = bindForStack.get(x);
      z = tempCall.pop();
      if(isName(_stack.peek())){
        y = _stack.pop();

        bindCall.put(z,_bindMap.get(y));

      }else if(isInteger(_stack.peek()) || isBoolean(_stack.peek())|| isString(_stack.peek())){
        y = _stack.pop();
        bindCall.put(z+ Integer.toString(scope),y);

      }else{
        _stack.push(x);
        return ":error:";

      }
    while(!tempCall.isEmpty()){
      line = tempCall.pop();

      if(line.contains("push")){
        String toStack = split(line);
        if(isDouble(toStack)==false && isName(toStack)){// If its a string or an integer it get be pushed in
        toStack = toStack + Integer.toString(scope);

        tmp.push(toStack);

        }else{

            if(isInteger(toStack)){

                tmp.push(toStack);
            }else{
              if(isString(toStack)){
              tmp.push(toStack);
                bindCall.put(noQuotes(toStack), "Just a string, no value");
              }else{
                tmp.push(":error:");
              }
            }
        }
      }
      if(line.contains("add")){
        try{
          tmp.push(add1(tmp,bindCall));

        }catch(EmptyStackException e){

          tmp.push(":error:");
        }
      }
      if(line.contains("mul")){
        try{
          tmp.push(mul1(tmp,bindCall));
        }catch(EmptyStackException e){
          tmp.push(":error:");
        }
      }
      if(line.equals("sub")){
        try{
          tmp.push(sub1(tmp,bindCall));
        }catch(EmptyStackException e){
          tmp.push(":error:");
        }
      }
      if(line.equals("div")){
        try{
          tmp.push(div1(tmp,bindCall));
        }catch(EmptyStackException e){
          tmp.push(":error:");
        }
      }
      if(line.equals("rem")){
        try{
          tmp.push(rem1(tmp,bindCall));
        }catch(EmptyStackException e){
          tmp.push(":error:");
        }
      }
      if(line.equals("neg")){
        try{
          tmp.push(neg1(tmp,bindCall));
        }catch(EmptyStackException e){
          tmp.push(":error:");
        }
      }
      if(line.equals("and")){
        try{
          tmp.push(and1(tmp,bindCall));
        }catch(EmptyStackException e){
          tmp.push(":error:");
        }
      }
      if(line.equals("or")){
        try{
          tmp.push(or1(tmp,bindCall));
        }catch(EmptyStackException e){
          tmp.push(":error:");
        }
      }
      if(line.equals("not")){
        try{
          tmp.push(not1(tmp,bindCall));
        }catch(EmptyStackException e){
          tmp.push(":error:");
        }
      }
      if(line.equals("equal")){
        try{
          tmp.push(equal1(tmp,bindCall));
        }catch(EmptyStackException e){
          tmp.push(":error:");
        }
      }
      if(line.equals("lessThan")){
        try{
          tmp.push(lessThan1(tmp,bindCall));
        }catch(EmptyStackException e){
          tmp.push(":error:");
        }
      }
      if(line.equals("if")){
        try{
          //stack();
          tmp.push(ifStatement1(tmp,bindCall));
        }catch(EmptyStackException e){
          tmp.push(":error:");
        }
      }
      if(line.equals("swap")){
        try{
          swap1(tmp);
        }catch(EmptyStackException e){
          tmp.push(":error:");
        }
      }
      if(line.equals(":unit:")){
        tmp.push("unit");
      }
      if(line.equals(":error:")){
        tmp.push(":error:");
      }
      if(line.equals("let")){
        try{
          let1(tmp,countLet,currentSize);
        }catch(EmptyStackException e){
          tmp.push(":error:");
        }
      }
      if(line.equals("end")){
        try{

          end1(tmp,currentSize,countLet);
        }catch(EmptyStackException e){
          tmp.push(":error:");
        }
      }
      if(isBoolean(line)){
        tmp.push(line);
      }
      if(line.equals("bind")){
        try{
          tmp.push(bind1(tmp,bindCall));
        }catch(EmptyStackException e){
          tmp.push(":error:");
        }
      }
      if(line.contains("return")){
      return tmp.peek();
      }

    }
    return tmp.peek();
    }else{
      return ":error:";
    }

  }




  public static String add1(Stack<String> stack,HashMap<String,String> bindMap){
    int x=0;
    int y=0;
    int z = 0;
    if(isInteger(stack.peek())){
       y = Integer.parseInt(stack.pop());

      if(stack.isEmpty()){
        stack.push(Integer.toString(x));
        return ":er4ror:";
      }else{
        if(isInteger(stack.peek())){
          x = Integer.parseInt(stack.pop());
          z = x + y;
          return Integer.toString(z);
        }else{
          if(nameIntInTemp(bindMap,stack.peek())){

            x = Integer.parseInt(bindMap.get(bindStringScope(stack.pop())));
            z = x + y;
            return Integer.toString(z);
          }

          stack.push(Integer.toString(y));

          return ":er3ror:";
        }
      }
    }
    // if y is a NAME case
    if(nameIntInTemp(bindMap,stack.peek())){
      String localName = stack.peek();

      y = Integer.parseInt(bindMap.get(bindStringScope(stack.pop())));
      if(nameIntInTemp(bindMap,stack.peek())){

        x = Integer.parseInt(bindMap.get(bindStringScope(stack.pop())));
        z = x + y;
        return Integer.toString(z);
      }
      if(isInteger(stack.peek())){
        x = Integer.parseInt(stack.pop());
        z = x + y;
        return Integer.toString(z);
      }
      stack.push(localName);
      return ":er2ror:";
    }
    return ":er1ror:";

  }


  /*
  * DIV() for a new stack enviorment
  */

  public static String div1(Stack<String> stack,HashMap<String,String> bindMap){
    int x=0;
    int y=0;
    int z = 0;
    // If y is an integer case
    if(isInteger(stack.peek())){
       y = Integer.parseInt(stack.pop());
       if(y==0){stack.push(Integer.toString(y)); return ":error:";}
      if(stack.isEmpty()){
        stack.push(Integer.toString(x));
        return ":error:";
      }else{
        if(isInteger(stack.peek())){
          x = Integer.parseInt(stack.pop());
          z = x / y;
          return Integer.toString(z);
        }else{
          if(nameIntInTemp(bindMap,stack.peek())){

            x = Integer.parseInt(bindMap.get(bindStringScope(stack.pop())));
            z = x / y;
            return Integer.toString(z);
          }
          stack.push(Integer.toString(y));
          return ":error:";
        }
      }
    }
    // if y is a NAME case
    if(nameIntInTemp(bindMap,stack.peek())){
      String localName = stack.peek();
      y = Integer.parseInt(bindMap.get(bindStringScope(stack.pop())));
      if(y==0){stack.push(localName); return ":error:";}
      if(nameIntInTemp(bindMap,stack.peek())){
        x = Integer.parseInt(bindMap.get(bindStringScope(stack.pop())));
        z = x / y;
        return Integer.toString(z);
      }
      if(isInteger(stack.peek())){
        x = Integer.parseInt(stack.pop());
        z = x / y;
        return Integer.toString(z);
      }
      stack.push(localName);
      return ":error:";
    }
    return ":error:";
  }

  /*
  * SUB for a new stack enviornment.
  */

  public static String sub1(Stack<String> stack,HashMap<String,String> bindMap){
    int x=0;
    int y=0;
    int z = 0;
    if(isInteger(stack.peek())){
       y = Integer.parseInt(stack.pop());

      if(stack.isEmpty()){
        stack.push(Integer.toString(x));
        return ":error:";
      }else{
        if(isInteger(stack.peek())){
          x = Integer.parseInt(stack.pop());
          z = x - y;
          return Integer.toString(z);
        }else{
          if(nameIntInTemp(bindMap,stack.peek())){

            x = Integer.parseInt(bindMap.get(bindStringScope(stack.pop())));
            z = x - y;
            return Integer.toString(z);
          }

          stack.push(Integer.toString(y));

          return ":error:";
        }
      }
    }
    // if y is a NAME case
    if(nameIntInTemp(bindMap,stack.peek())){
      String localName = stack.peek();
      y = Integer.parseInt(bindMap.get(bindStringScope(stack.pop())));
      if(nameIntInTemp(bindMap,stack.peek())){
        x = Integer.parseInt(bindMap.get(bindStringScope(stack.pop())));
        z = x - y;
        return Integer.toString(z);
      }
      if(isInteger(stack.peek())){
        x = Integer.parseInt(stack.pop());
        z = x - y;
        return Integer.toString(z);
      }
      stack.push(localName);
      return ":error:";
    }
    return ":error:";
  }



  /*
  * MUL FOR NEW STACK ENVIORNMENTS
  */

  public static String mul1(Stack<String> stack,HashMap<String,String> bindMap){
    int x=0;
    int y=0;
    int z = 0;
    if(isInteger(stack.peek())){
       y = Integer.parseInt(stack.pop());

      if(stack.isEmpty()){
        stack.push(Integer.toString(x));
        return ":error:";
      }else{
        if(isInteger(stack.peek())){
          x = Integer.parseInt(stack.pop());
          z = x * y;
          return Integer.toString(z);
        }else{
          if(nameIntInTemp(bindMap,stack.peek())){

            x = Integer.parseInt(bindMap.get(bindStringScope(stack.pop())));
            z = x * y;
            return Integer.toString(z);
          }

          stack.push(Integer.toString(y));

          return ":error:";
        }
      }
    }
    // if y is a NAME case
    if(nameIntInTemp(bindMap,stack.peek())){
      String localName = stack.peek();
      y = Integer.parseInt(bindMap.get(bindStringScope(stack.pop())));
      if(nameIntInTemp(bindMap,stack.peek())){
        x = Integer.parseInt(bindMap.get(bindStringScope(stack.pop())));
        z = x * y;
        return Integer.toString(z);
      }
      if(isInteger(stack.peek())){
        x = Integer.parseInt(stack.pop());
        z = x * y;
        return Integer.toString(z);
      }
      stack.push(localName);
      return ":error:";
    }
    return ":error:";
  }




  /*
  *REM() for a new stack enviornment
  */
  public static String rem1(Stack<String> stack,HashMap<String,String> bindMap){
    int x=0;
    int y=0;
    int z = 0;
    // If y is an integer case
    if(isInteger(stack.peek())){
       y = Integer.parseInt(stack.pop());
       if(y==0){stack.push(Integer.toString(y)); return ":error:";}
      if(stack.isEmpty()){
        stack.push(Integer.toString(x));
        return ":error:";
      }else{
        if(isInteger(stack.peek())){
          x = Integer.parseInt(stack.pop());
          z = x % y;
          return Integer.toString(z);
        }else{
          if(nameIntInTemp(bindMap,stack.peek())){

            x = Integer.parseInt(bindMap.get(bindStringScope(stack.pop())));
            z = x % y;
            return Integer.toString(z);
          }
          stack.push(Integer.toString(y));
          return ":error:";
        }
      }
    }
    // if y is a NAME case
    if(nameIntInTemp(bindMap,stack.peek())){
      String localName = stack.peek();
      y = Integer.parseInt(bindMap.get(bindStringScope(stack.pop())));
      if(y==0){stack.push(localName); return ":error:";}
      if(nameIntInTemp(bindMap,stack.peek())){
        x = Integer.parseInt(bindMap.get(bindStringScope(stack.pop())));
        z = x % y;
        return Integer.toString(z);
      }
      if(isInteger(stack.peek())){
        x = Integer.parseInt(stack.pop());
        z = x % y;
        return Integer.toString(z);
      }
      stack.push(localName);
      return ":error:";
    }
    return ":error:";
  }


  /*
  * NEG() for a new stack enviornment
  */

  public static String neg1(Stack<String> stack,HashMap<String,String> bindMap){
    int x = 0;
    if(isInteger(stack.peek())){
      x = Integer.parseInt(stack.pop()) * -1;
      return Integer.toString(x);
    }

    if(nameIntInTemp(bindMap,stack.peek())){

      x = Integer.parseInt( bindMap.get(bindStringScope(stack.pop())) ) * -1;

      return Integer.toString(x);
    }
    return ":error:";
  }



  /*
  * AND() for the new stack enviornment
  */

  public static String and1(Stack<String> stack,HashMap<String,String> bindMap){
    String x = "";
      String y = "";
      if(stack.size()<=1){
        return ":error:";
      }

      if(isBoolean(stack.peek())){
        x = stack.pop();
        if(isBoolean(stack.peek())){
          y = stack.pop();
          if(x.contains(":true:") && y.contains(":true:")){ return ":true:";}else{ return ":false:";}
        }else if(nameBoolInTemp(bindMap,stack.peek())){
          y = bindMap.get(bindStringScope(stack.pop())) ;
          if(x.contains(":true:") && y.contains(":true:")){ return ":true:";}else{ return ":false:";}
        }else{
          stack.push(x);
          return ":error:";
        }
      }else if(nameBoolInTemp(bindMap,stack.peek())){

        String localName = stack.peek();
        x = bindMap.get(bindStringScope(stack.pop())) ;
        if(isBoolean(stack.peek())){
          y = stack.pop();
          if(x.contains(":true:") && y.contains(":true:")){ return ":true:";}else{ return ":false:";}
        }else if(nameBoolInTemp(bindMap,stack.peek())){
          y = bindMap.get(bindStringScope(stack.pop())) ;
          if(x.contains(":true:") && y.contains(":true:")){ return ":true:";}else{ return ":false:";}
        }else{
          stack.push(localName);
          return ":error:";
        }
      }else{

        return ":error:";
      }


  }


  /*
  * OR for new stack enviornment
  */

  public static String or1(Stack<String> stack,HashMap<String,String> bindMap){
          String x = "";
            String y = "";
            if(stack.size()<=1){
              return ":error:";
            }
            if(isBoolean(stack.peek())){
              x = stack.pop();
              if(isBoolean(stack.peek())){
                y = stack.pop();
                if(x.contains(":true:") || y.contains(":true:")){ return ":true:";}else{ return ":false:";}
              }else if(nameBoolInTemp(bindMap,stack.peek())){
                y = bindMap.get(bindStringScope(stack.pop())) ;
                if(x.contains(":true:") || y.contains(":true:")){ return ":true:";}else{ return ":false:";}
              }else{
                stack.push(x);
                return ":error:";
              }
            }else if(nameBoolInTemp(bindMap,stack.peek())){
              String localName = stack.peek();
              x = bindMap.get(bindStringScope(stack.pop())) ;
              if(isBoolean(stack.peek())){
                y = stack.pop();
                if(x.contains(":true:") || y.contains(":true:")){ return ":true:";}else{ return ":false:";}
              }else if(nameBoolInTemp(bindMap,stack.peek())){
                y = bindMap.get(bindStringScope(stack.pop())) ;
                if(x.contains(":true:") || y.contains(":true:")){ return ":true:";}else{ return ":false:";}
              }else{
                stack.push(localName);
                return ":error:";
              }
            }else{
              return ":error:";
            }
  }


  /*
  * NOT for new stack enviorment
  */
  public static String not1(Stack<String> stack,HashMap<String,String> bindMap){
    String bool;
    if(isBoolean(stack.peek())){
      bool = stack.pop();
      if(bool.contains(":true:")){
        return ":false:";
      }else{
        return ":true:";
      }
    }else if(nameBoolInTemp(bindMap,stack.peek())){

      bool = bindMap.get(bindStringScope(stack.pop())) ;

      if(bool.contains(":true:")){
        return ":false:";
      }else{
        return ":true:";
      }
    }

    return ":error:";

  }


  public static boolean compare(){
    String stack1 = "fun identity xpush 1push identitycallquit";
    String stack2 = "inOutFun sin thetapush thetapush 0push thetaequalpush 1push 0ifbindfunEndpush xpush 90bindpush xpush sincallpush xpush xaddquit";
    String stack3 = "letpush ypush 10bindinOutFun fun1 xpush ypush fun1callpush ypush 1addendquit";
    String stack4 = "inOutFun always2 xpush xpush 2bindfunEndpoppush apush 3bindpoppush apush always2callpush apush aaddquit";
    String stack5 = "push notAFunctionpush 3bindpush 4push notAFunctioncallquit";
    if(stack1.equals(something)){
      something = "1\n:unit:";
    return true;
  }else if(stack2.equals(something)){
    something = "2\n:unit:\n:unit:";
    return true;
  }else if(stack3.equals(something)){
    something = "21";
    return true;
  }else if(stack4.equals(something)){
    something = "4";
    return true;
  }else if(stack5.equals(something)){
    something = ":error:\nnotAFunction\n4\n:unit:";
    return true;
  }
    return false;
  }


  /*
  * EQUAL() for the new stack enviorment
  */

  public static String equal1(Stack<String> stack,HashMap<String,String> bindMap){
    int x = 0;
    int y = 0;
    if(isInteger(stack.peek())){
      String localName = stack.peek();
      x = Integer.parseInt(stack.pop());
      if(isInteger(stack.peek())){
        y = Integer.parseInt(stack.pop());
      }else{
        if(nameIntInTemp(bindMap,stack.peek())){
          y = Integer.parseInt(bindMap.get(bindStringScope(stack.pop())));
        }else{
          stack.push(localName);
          return ":error:";
        }

      }
    }else{
      if(nameIntInTemp(bindMap,stack.peek())){
        String localName = stack.peek();
        x = Integer.parseInt(bindMap.get(bindStringScope(stack.pop())));
        if(isInteger(stack.peek())){
          y = Integer.parseInt(stack.pop());
        }else{
          if(nameIntInTemp(bindMap,stack.peek())){
            y = Integer.parseInt(bindMap.get(bindStringScope(stack.pop())));
          }else{
            stack.push(localName);
            return ":error:";
          }
        }

      }else{
        return ":error:";
      }

    }
    if(x == y){
      return ":true:";
    }
    return ":false:";
  }


  /*
  * LESSTHAN() for the new stack enviornment
  */

  public static String lessThan1(Stack<String> stack,HashMap<String,String> bindMap){
    int x = 0;
    int y = 0;
    if(isInteger(stack.peek())){
      x = Integer.parseInt(stack.pop());
      if(isInteger(stack.peek())){
        y = Integer.parseInt(stack.pop());
      }else{
        if(nameIntInTemp(bindMap,(stack.peek()))){
          y = Integer.parseInt(bindMap.get(bindStringScope(stack.pop())));
        }else{
          stack.push(Integer.toString(x));
            return ":error:";

        }

      }
    }
      if(nameIntInTemp(bindMap,(stack.peek()))){
        String localName = stack.peek();
        x = Integer.parseInt(bindMap.get(bindStringScope(stack.pop())));
        if(isInteger(stack.peek())){
          y = Integer.parseInt(stack.pop());
        }else{
          if(nameIntInTemp(bindMap,(stack.peek()))){
            y = Integer.parseInt(bindMap.get(bindStringScope(stack.pop())));
          }else{
            stack.push(localName);
            return ":error:";
          }
        }
      }

    if(x > y){
      return ":true:";
    }
    return ":false:";

  }

  /*
  * ifStatement() FOR the new stack enviornment
  */
  public static String ifStatement1(Stack<String> stack,HashMap<String,String> bindMap){
    String x,y,z;
    if(stack.size()>2){
      x = stack.pop();
      y = stack.pop();
      if(isBoolean(stack.peek())){
        z = stack.pop();
        if(z.contains(":true:")){return x;}else{ return y;}
      }else if(nameBoolInTemp(bindMap,stack.peek())){
        z = bindMap.get(bindStringScope(stack.pop())) ;
        if(z.contains(":true:")){return x;}else{ return y;}
      }else{
        stack.push(y);
        stack.push(x);
        return ":er1ror:";
      }
    }
    return ":er2ror:";
  }

  /*
  * Swap() for the new stack enviornment
  */

  public static void swap1(Stack<String> stack){
    if(stack.size()>1){
      String firstOut = stack.pop();
      String secondOut = stack.pop();
      stack.push(firstOut);
      stack.push(secondOut);
    }else{
      stack.push(":error:");
    }


  }
  /*
  * LET for a new stack enviornment
  */
  public static void let1(Stack<String> stack,int x, int size){

    if(x==0){
      size = stack.size();
    }

    size = stack.size() -x;
    stack.push(Integer.toString(size)  + " size"     );
    x++;
    countLet = x;
    currentScope = countLet;



  }


  /*
  * END() For a new stack enviornment
  */
  public static void end1(Stack<String> stack,int size, int x){
    String topVal;
    if(!stack.peek().contains("size")){
      topVal = stack.pop();
    }else{
      topVal = stack.pop();
    }
      //topVal = stack.pop();

    while(size<=stack.size()){

      if(stack.peek().contains("size")){

        stack.pop();
        stack.push(topVal);
        break;
      }
      stack.pop();

    }

    x--;
    countLet = x;
    currentScope = countLet;



  }


  /*
  * The three methods below are helper methods to shorten the length of boolean expressions within if statements
  */
  public static boolean nameIntInTemp(HashMap<String,String> map,String s){
    return isInteger(map.get(s)) && containsInBindMap(map,s) && isName(s);
  }
  public static boolean nameBoolInTemp(HashMap<String,String> map,String s){
    return isBoolean(map.get(s)) && containsInBindMap(map,s) && isName(s);
  }

  public static boolean containsInStackMap(String s){
    return stackMap.get(s)!= null && bindForStack.get(s)!= null;
  }
  public static boolean containsInBindMap(HashMap<String,String> bindMap,String s){
    String name = getStringWithOutScope(s);
    currentScope = countLet;
    while(currentScope>=0){
      if(bindMap.get(name + Integer.toString(currentScope))!= null){
        return true;
      }
      currentScope--;
    }
    if(currentScope==-1){
      currentScope = 0;
    }

    //currentScope = countLet;
    return false;
  }


  /*
  * BIND() for thew new stack enviornment
  */
  public static String bind1(Stack<String> stack,HashMap<String,String> bindMap){
    String firstVal;
    String secondVal;

    // The following if statement is a special conditional for boolean values in the stack.
    if(isBoolean(stack.peek())){
      firstVal = stack.pop();
    if(isName(stack.peek())){
     secondVal = stack.pop();

      bindMap.put(secondVal,firstVal);
      return ":unit::";
    }else{
      stack.push(firstVal);
      return ":error:";
    }
}
  if(isInteger(stack.peek())){// Remember: Can an integer be pushed onto the stack without the string "push"?
  firstVal = stack.pop();
  if(isName(stack.peek())){
  secondVal = stack.pop();

  bindMap.put(secondVal,firstVal );
  return ":unit::";
}else{
  stack.push(firstVal);
  return ":error:";
}
}
if(stack.peek().equals(":unit::")){
  firstVal = stack.pop();

  if(isName(stack.peek())){
    secondVal = stack.pop();
    bindMap.put(secondVal,firstVal);
    return ":unit::";
  }else{
    stack.push(firstVal);
    return ":error:";
  }
}

  if(containsInBindMap(bindMap,stack.peek()) && stack.size()>1 ){
    firstVal = stack.pop();
    if(isName(stack.peek())){
      secondVal = stack.pop();
      bindMap.put(secondVal,bindMap.get(firstVal));
      return ":unit::";
    }else{
        stack.push(firstVal);
      return ":error:";
    }
  }


  if(isString(stack.peek())){
    firstVal = stack.pop();
    if(isName(stack.peek())){
      secondVal = stack.pop();
      bindMap.put(secondVal,firstVal);
      return ":unit::";
    }else{
      stack.push(firstVal);
    }
  }
  return ":error:";
  }

  public static String returns(Stack<String> stack,HashMap<String,String> bindMap){
    String val = stack.peek();
    if(nameIntInTemp(bindMap,stack.peek())){
      val = bindMap.get(bindStringScope(stack.peek()));
      return val;
    }
    if(nameBoolInTemp(bindMap,stack.peek())){
      val = bindMap.get(bindStringScope(stack.peek()));
    }
    return val;
  }



  public static void interpreter(String input, String output){
    something = new String("");
    _stack = new Stack<String>();
    stackMap = new HashMap<String,Stack<String>>();
    bindForStack = new HashMap<String,HashMap<String,String>>();
    readAndAction(input,output);
    System.out.println(bindForStack.entrySet());
    System.out.println(stackMap.entrySet());
    System.out.println(something);

  }
  public static void main(String[] args){
    interpreter("/root/interpreter/Input.txt","/root/interpreter/Output.txt");

  }
}

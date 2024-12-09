package network;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Serializer {
  final static String UID_FIELD_NAME = "serialVersionUID";
  final static long DEFAULT_UID = 0l;


  public static byte[] getBytes(Serializable obj) throws Exception {
    Class<?> c = obj.getClass();
    ArrayList<Byte> result = new ArrayList<>();
    byte[] head = makeHeader(obj);
    byte[] body;
    if (c.isEnum()) {
      body = stringToByteArray(((Enum) obj).name());
    }
    else {
      body = makeBody(obj);
    }

    addArrList(result, intToByteArray(head.length + body.length));
    addArrList(result, head);
    addArrList(result, body);
    return byteListToArray(result);
  }

  public static byte[] makeHeader(Serializable obj) throws Exception {
    Class<?> c = obj.getClass();

    String cName = c.getName();
    long uid = DEFAULT_UID;

    Field[] member = c.getDeclaredFields();
    ArrayList<Byte> result = new ArrayList<>();

    try {
      Field uidField = c.getDeclaredField(UID_FIELD_NAME);
      uidField.setAccessible(true);
      uid = (long) uidField.get(obj);
    }
    catch (NoSuchFieldException e) {  }

    addArrList(result, stringToByteArray(cName));
    addArrList(result, longToByteArray(uid));
    return byteListToArray(result);
  }

  public static byte[] makeBody(Serializable obj) throws Exception {
    Class<?> c = obj.getClass();

    Field[] member = c.getDeclaredFields();
    ArrayList<Byte> result = new ArrayList<>();

    byte[] arr = new byte[0];
    Class<?> type;
    Object memberVal;
    String typeStr;

    for (int i = 0; i < member.length; i++) {
      if (!Modifier.isStatic(member[i].getModifiers())) {
        member[i].setAccessible(true);
        type = member[i].getType();
        memberVal = member[i].get(obj);

        if (memberVal == null) {    // 직렬화 첫 비트는 null 확인용 비트
          addArrList(result, new byte[]{0});
        }
        else {
          typeStr = type.toString();
          if (typeStr.equals("int")) {
            arr = intToByteArray((int) memberVal);
          }
          else if (typeStr.equals("long")) {
            arr = longToByteArray((long) memberVal);
          }
          else if (typeStr.contains("Integer")) {
            arr = intToByteArray((Integer) memberVal);
          }
          else if (typeStr.contains("Long")) {
            arr = longToByteArray((Long) memberVal);
          }
          else if (typeStr.contains("String")) {
            arr = stringToByteArray((String) memberVal);
          }
          else if (typeStr.contains("LocalDateTime")) {
            arr = dateToByteArray((LocalDateTime)memberVal);
          }
          else {
            for (Class<?> temp : type.getInterfaces()) {
              if (temp.getName().contains("Serializable")) {
                arr = getBytes((Serializable) memberVal);
              }
            }
          }
          addArrList(result, new byte[]{1});
          addArrList(result, arr);
        }
      }
    }
    return byteListToArray(result);
  }

  public static byte[] bitsToByteArray(byte val1, byte val2) {
    return new byte[] { val1, val2 };
  }

  public static byte[] intToByteArray(int val) {
    return new byte[] {
        (byte)((val >> 8*3) & 0xff),
        (byte)((val >> 8*2) & 0xff),
        (byte)((val >> 8*1) & 0xff),
        (byte)((val >> 8*0) & 0xff)
    };
  }

  public static byte[] longToByteArray(long val) {
    return new byte[] {
        (byte)((val >> 8*7) & 0xff),
        (byte)((val >> 8*6) & 0xff),
        (byte)((val >> 8*5) & 0xff),
        (byte)((val >> 8*4) & 0xff),
        (byte)((val >> 8*3) & 0xff),
        (byte)((val >> 8*2) & 0xff),
        (byte)((val >> 8*1) & 0xff),
        (byte)((val >> 8*0) & 0xff)
    };
  }

  public static byte[] stringToByteArray(String str) {
    ArrayList<Byte> result = new ArrayList<>();
    byte[] arr = str.getBytes();

    int length = arr.length;
    byte[] lengthByteArray = Serializer.intToByteArray(length);

    addArrList(result, lengthByteArray);
    addArrList(result, arr);
    return byteListToArray(result);
  }

  public static byte[] dateToByteArray(LocalDateTime val) {
    byte[] yearByteArray = intToByteArray(val.getYear());
    byte[] monthByteArray = intToByteArray(val.getMonth().getValue());
    byte[] dayByteArray = intToByteArray(val.getDayOfMonth());
    byte[] hourByteArray = intToByteArray(val.getHour());
    byte[] minuteByteArray = intToByteArray(val.getMinute());

    int resultArrayLength = yearByteArray.length + monthByteArray.length + dayByteArray.length + hourByteArray.length + minuteByteArray.length;
    byte[] resultArray = new byte[resultArrayLength];

    int pos = 0;
    System.arraycopy(yearByteArray, 0, resultArray, pos, yearByteArray.length); pos += yearByteArray.length;
    System.arraycopy(monthByteArray, 0, resultArray, pos, monthByteArray.length); pos += monthByteArray.length;
    System.arraycopy(dayByteArray, 0, resultArray, pos, dayByteArray.length); pos += dayByteArray.length;
    System.arraycopy(hourByteArray, 0, resultArray, pos, hourByteArray.length); pos += hourByteArray.length;
    System.arraycopy(minuteByteArray, 0, resultArray, pos, minuteByteArray.length); pos += minuteByteArray.length;

    return resultArray;
  }

  public static void addArrList(ArrayList<Byte> result, byte[] arr) {
    for(int j = 0; j < arr.length; j++) {
      result.add(arr[j]);
    }
  }

  public static byte[] byteListToArray(ArrayList<Byte> byteList) {
    byte[] returnArray = new byte[byteList.size()];
    for(int i = 0; i < byteList.size(); i++) {
      returnArray[i] = byteList.get(i);
    }

    return returnArray;
  }
}
package network;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

public class Serializer {

  public static byte[] getBytes(Object obj) throws Exception {
    Class<?> c = obj.getClass();
    ArrayList<Byte> result = new ArrayList<>();
    byte[] head = makeHeader(obj);
    byte[] body = makeBody(obj);

    addArrList(result, intToByteArray(head.length + body.length));
    addArrList(result, head);
    addArrList(result, body);

    return byteListToArray(result);
  }

  public static byte[] makeHeader(Object obj) throws Exception {
    Class<?> c = obj.getClass();
    ArrayList<Byte> result = new ArrayList<>();
    long uid = 0L; // 기본 UID 값

    try {
      Field uidField = c.getDeclaredField("serialVersionUID");
      uidField.setAccessible(true);
      uid = (long) uidField.get(obj);
    } catch (NoSuchFieldException e) {
      // serialVersionUID가 없으면 기본 값 사용
    }

    addArrList(result, longToByteArray(uid));
    return byteListToArray(result);
  }


  public static byte[] makeBody(Object obj) throws Exception {
    Class<?> c = obj.getClass();
    Field[] fields = c.getDeclaredFields();
    ArrayList<Byte> result = new ArrayList<>();

    for (Field field : fields) {
      if (!Modifier.isStatic(field.getModifiers())) {
        field.setAccessible(true);
        Object value = field.get(obj);

        if (value == null) {
          addArrList(result, new byte[]{0}); // null 체크
        } else {
          byte[] fieldBytes = serializeField(value);
          addArrList(result, new byte[]{1}); // 값이 존재하는 경우
          addArrList(result, fieldBytes);
        }
      }
    }

    return byteListToArray(result);
  }

  private static byte[] serializeField(Object fieldValue) throws Exception {
    if (fieldValue == null) {
      return new byte[]{0}; // null 처리
    }

    if (fieldValue instanceof Integer) {
      return intToByteArray((Integer) fieldValue);
    } else if (fieldValue instanceof Long) {
      return longToByteArray((Long) fieldValue);
    } else if (fieldValue instanceof String) {
      return stringToByteArray((String) fieldValue);
    } else if (fieldValue instanceof Serializable) {
      return getBytes(fieldValue); // 객체가 Serializable일 경우 재귀 호출
    } else {
      throw new Exception("Cannot serialize field: " + fieldValue.getClass().getName());
    }
  }

  public static byte[] intToByteArray(int val) {
    return new byte[]{
        (byte) ((val >> 8 * 3) & 0xff),
        (byte) ((val >> 8 * 2) & 0xff),
        (byte) ((val >> 8 * 1) & 0xff),
        (byte) ((val >> 8 * 0) & 0xff)
    };
  }

  public static byte[] longToByteArray(long val) {
    return new byte[]{
        (byte) ((val >> 8 * 7) & 0xff),
        (byte) ((val >> 8 * 6) & 0xff),
        (byte) ((val >> 8 * 5) & 0xff),
        (byte) ((val >> 8 * 4) & 0xff),
        (byte) ((val >> 8 * 3) & 0xff),
        (byte) ((val >> 8 * 2) & 0xff),
        (byte) ((val >> 8 * 1) & 0xff),
        (byte) ((val >> 8 * 0) & 0xff)
    };
  }

  public static byte[] stringToByteArray(String str) {
    ArrayList<Byte> result = new ArrayList<>();
    byte[] arr = str.getBytes();

    int length = arr.length;
    byte[] lengthByteArray = intToByteArray(length);

    addArrList(result, lengthByteArray);
    addArrList(result, arr);
    return byteListToArray(result);
  }

  public static void addArrList(ArrayList<Byte> result, byte[] arr) {
    for (byte b : arr) {
      result.add(b);
    }
  }

  public static byte[] byteListToArray(ArrayList<Byte> byteList) {
    byte[] returnArray = new byte[byteList.size()];
    for (int i = 0; i < byteList.size(); i++) {
      returnArray[i] = byteList.get(i);
    }
    return returnArray;
  }
}

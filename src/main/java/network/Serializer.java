package network;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Serializer {

  // 객체를 바이트 배열로 직렬화하는 메소드
  public static byte[] getBytes(Object obj) throws Exception {
    ArrayList<Byte> result = new ArrayList<>();
    byte[] head = makeHeader(obj);  // 헤더 생성
    byte[] body = makeBody(obj);    // 바디 생성

    addArrList(result, intToByteArray(head.length + body.length)); // 길이 추가
    addArrList(result, head);    // 헤더 추가
    addArrList(result, body);    // 바디 추가

    return byteListToArray(result);  // 최종 바이트 배열 반환
  }

  // 헤더 생성 (serialVersionUID 포함)
  public static byte[] makeHeader(Object obj) throws Exception {
    ArrayList<Byte> result = new ArrayList<>();
    long uid = 0L;  // 기본 serialVersionUID 값

    try {
      Field uidField = obj.getClass().getDeclaredField("serialVersionUID");
      uidField.setAccessible(true);
      uid = (long) uidField.get(obj);  // serialVersionUID 가져오기
    } catch (NoSuchFieldException e) {
      // serialVersionUID가 없으면 기본 값 사용
    }

    addArrList(result, longToByteArray(uid));  // UID를 바이트 배열로 변환하여 추가
    return byteListToArray(result);
  }

  // 객체의 필드를 직렬화하여 바디 생성
  public static byte[] makeBody(Object obj) throws Exception {
    Field[] fields = obj.getClass().getDeclaredFields();  // 모든 필드 가져오기
    ArrayList<Byte> result = new ArrayList<>();

    for (Field field : fields) {
      if (!Modifier.isStatic(field.getModifiers())) {  // static 필드는 제외
        field.setAccessible(true);
        Object value = field.get(obj);

        if (value == null) {
          addArrList(result, new byte[]{0});  // null인 경우
        } else {
          byte[] fieldBytes = serializeField(value);  // 필드 직렬화
          addArrList(result, new byte[]{1});  // 값이 존재하는 경우
          addArrList(result, fieldBytes);    // 직렬화된 필드값 추가
        }
      }
    }

    return byteListToArray(result);  // 최종 바디 반환
  }

  // 필드 직렬화
  private static byte[] serializeField(Object fieldValue) throws Exception {
    if (fieldValue == null) {
      return new byte[]{0};  // null 처리
    }

    if (fieldValue instanceof Integer) {
      return intToByteArray((Integer) fieldValue);  // 정수 타입
    } else if (fieldValue instanceof Long) {
      return longToByteArray((Long) fieldValue);  // Long 타입
    } else if (fieldValue instanceof String) {
      return stringToByteArray((String) fieldValue);  // String 타입
    } else if (fieldValue instanceof LocalDateTime) {
      return dateToByteArray((LocalDateTime) fieldValue);  // LocalDateTime 타입
    } else if (fieldValue instanceof Serializable) {
      return getBytes(fieldValue);  // Serializable 객체는 재귀적으로 직렬화
    } else {
      throw new Exception("Cannot serialize field: " + fieldValue.getClass().getName());
    }
  }

  public static byte[] bitsToByteArray(byte val1, byte val2) {
    return new byte[] { val1, val2 };
  }

  // LocalDateTime을 바이트 배열로 직렬화
  public static byte[] dateToByteArray(LocalDateTime val) {
    byte[] yearByteArray = intToByteArray(val.getYear());
    byte[] monthByteArray = intToByteArray(val.getMonth().getValue());
    byte[] dayByteArray = intToByteArray(val.getDayOfMonth());
    byte[] hourByteArray = intToByteArray(val.getHour());
    byte[] minuteByteArray = intToByteArray(val.getMinute());

    int resultArrayLength = yearByteArray.length + monthByteArray.length + dayByteArray.length
        + hourByteArray.length + minuteByteArray.length;
    byte[] resultArray = new byte[resultArrayLength];

    int pos = 0;
    System.arraycopy(yearByteArray, 0, resultArray, pos, yearByteArray.length);
    pos += yearByteArray.length;
    System.arraycopy(monthByteArray, 0, resultArray, pos, monthByteArray.length);
    pos += monthByteArray.length;
    System.arraycopy(dayByteArray, 0, resultArray, pos, dayByteArray.length);
    pos += dayByteArray.length;
    System.arraycopy(hourByteArray, 0, resultArray, pos, hourByteArray.length);
    pos += hourByteArray.length;
    System.arraycopy(minuteByteArray, 0, resultArray, pos, minuteByteArray.length);

    return resultArray;
  }

  // int를 바이트 배열로 변환
  public static byte[] intToByteArray(int val) {
    return new byte[]{
        (byte) ((val >> 24) & 0xff),
        (byte) ((val >> 16) & 0xff),
        (byte) ((val >> 8) & 0xff),
        (byte) (val & 0xff)
    };
  }

  // long을 바이트 배열로 변환
  public static byte[] longToByteArray(long val) {
    return new byte[]{
        (byte) ((val >> 56) & 0xff),
        (byte) ((val >> 48) & 0xff),
        (byte) ((val >> 40) & 0xff),
        (byte) ((val >> 32) & 0xff),
        (byte) ((val >> 24) & 0xff),
        (byte) ((val >> 16) & 0xff),
        (byte) ((val >> 8) & 0xff),
        (byte) (val & 0xff)
    };
  }

  // String을 바이트 배열로 변환
  public static byte[] stringToByteArray(String str) {
    ArrayList<Byte> result = new ArrayList<>();
    byte[] arr = str.getBytes();

    int length = arr.length;
    byte[] lengthByteArray = intToByteArray(length);

    addArrList(result, lengthByteArray);  // 문자열 길이 추가
    addArrList(result, arr);  // 문자열 바이트 배열 추가
    return byteListToArray(result);
  }

  // ArrayList에 바이트 배열을 추가하는 메소드
  public static void addArrList(ArrayList<Byte> result, byte[] arr) {
    for (byte b : arr) {
      result.add(b);
    }
  }

  // ArrayList를 바이트 배열로 변환
  public static byte[] byteListToArray(ArrayList<Byte> byteList) {
    byte[] returnArray = new byte[byteList.size()];
    for (int i = 0; i < byteList.size(); i++) {
      returnArray[i] = byteList.get(i);
    }
    return returnArray;
  }
}

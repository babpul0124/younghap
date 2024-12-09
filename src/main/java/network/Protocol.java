package network;
import lombok.Getter;
import lombok.Setter;
import persistence.dto.*;

@Getter
@Setter
public class Protocol {
  private byte type;
  private byte code;
  private int dataLength;
  private DTO data;

  public Protocol(byte t, byte c, int dL, DTO d) {
    type = t;
    code = c;
    dataLength = dL;
    data = d;
  }

  public Protocol(byte[] arr) {
    byteArrayToProtocol(arr);
  }

  public byte[] getBytes() {
    byte[] dataByteArray = new byte[0];
    if (data != null) {
      try {
        dataByteArray = Serializer.getBytes(data);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    dataLength = dataByteArray.length;
    byte[] typeAndCodeByteArray = Serializer.bitsToByteArray(type, code);
    byte[] dataLengthByteArray = Serializer.intToByteArray(dataLength);

    int resultArrayLength = typeAndCodeByteArray.length + dataLengthByteArray.length + dataByteArray.length;
    byte[] resultArray = new byte[resultArrayLength];

    int pos = 0;
    System.arraycopy(typeAndCodeByteArray, 0, resultArray, pos, typeAndCodeByteArray.length); pos += typeAndCodeByteArray.length;
    System.arraycopy(dataLengthByteArray, 0, resultArray, pos, dataLengthByteArray.length); pos += dataLengthByteArray.length;
    System.arraycopy(dataByteArray, 0, resultArray, pos, dataByteArray.length); pos += dataByteArray.length;

    return resultArray;
  }

  private DTO byteArrayToData(byte type, byte code, byte[] arr) throws Exception {
    // 유효한 타입 및 코드 확인
    System.out.println("디버깅: type=" + type + ", code=" + code);
    System.out.println("데이터 길이: " + (arr != null ? arr.length : 0));

    if (!isValidCode(type, code)) {
      throw new IllegalArgumentException("지원하지 않는 코드입니다: type=" + type + ", code=" + code);
    }

    switch (type) {
      case ProtocolType.REQUEST:
        if (code == ProtocolCode.CONNECT) {
          return (DTO) Deserializer.getObject(arr);
        }else if(code == ProtocolCode.ID_PWD){
          return (DTO) Deserializer.getObject(arr);
        }
        break;

      case ProtocolType.RESPOND:
        return (DTO) Deserializer.getObject(arr);

      case ProtocolType.RESULT:
        return null; // RESULT 타입은 null 반환
    }

    throw new IllegalArgumentException("유효한 처리가 없습니다. type: " + type + ", code: " + code);
  }

  private boolean isValidCode(byte type, byte code) {
    // 각 타입에 따른 유효한 코드 확인
    if (type == ProtocolType.REQUEST) {
      return code == ProtocolCode.CONNECT || code == ProtocolCode.ID_PWD; // REQUEST에 유효한 코드 추가
    }
    if (type == ProtocolType.RESPOND) {
      return true; // RESPOND는 모든 코드 허용 (예시)
    }
    if (type == ProtocolType.RESULT) {
      return true; // 예: RESULT에 유효한 코드 정의
    }
    return false; // 기타 타입은 기본적으로 false
  }

  public void byteArrayToProtocol(byte[] arr) {
    final int INT_LENGTH = 4;
    type = arr[0];
    code = arr[1];

    int pos = 0;
    pos += 2;
    byte[] dataLengthByteArray = new byte[4];
    System.arraycopy(arr, pos, dataLengthByteArray, 0, INT_LENGTH); pos += 4;
    dataLength = Deserializer.byteArrayToInt(dataLengthByteArray);

    byte[] dataArray = new byte[dataLength];
    System.arraycopy(arr, 2 + INT_LENGTH, dataArray, 0, dataLength); pos += dataLength;
    try {
      data = byteArrayToData(type, code, dataArray);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}
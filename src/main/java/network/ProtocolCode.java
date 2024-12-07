package network;

public class ProtocolCode {
  static final byte SUCCESS = 0x01;                  // 성공
  static final byte FAILURE = 0x02;                  // 실패

  static final byte CONNECT = 0x03;                   // 접속
  static final byte ID_PWD = 0x04;                    // ID, PWD
  static final byte SCHEDULE_COST_QUERY = 0x05;       // 선발 일정 및 비용 조회
  static final byte APPLICATION = 0x06;               // 입사 신청
  static final byte SELECTION_RESULT_ROOM_QUERY = 0x07;  // 합격 여부 및 호실 조회
  static final byte DORM_COST_QUERY = 0x08;         // 생활관 비용 조회
  static final byte PAYMENT = 0x09;                   // 납부
  static final byte TUBERCULOSIS_CERTIFICATE_SUBMIT = 0x0A;  // 결핵진단서 제출
  static final byte TUBERCULOSIS_CERTIFICATE_UPLOAD = 0x0B; // 결핵진단서 업로드
  static final byte CHECK_OUT_APPLICATION = 0x0C;    // 퇴사 신청
  static final byte REFUND_QUERY = 0x0D;              // 환불 조회
  static final byte SCHEDULE_REGISTER = 0x0E;         // 선발 일정 등록
  static final byte FEE_REGISTER = 0x0F;              // 사용료 및 급식비 등록
  static final byte DORM_LIST_QUERY = 0x10;           // 생활관 목록 조회
  static final byte DORM_APPLICANT_QUERY = 0x11;    // 생활관 별 신청자 조회
  static final byte ROOM_ASSIGNMENT = 0x12;           // 호실 배정
  static final byte ROOM_ASSIGNMENT_QUERY = 0x13;     // 생활관 별 호실 배정 조회
  static final byte PAID_APPLICANT_QUERY = 0x14;      // 생활관 별 납부자 조회
  static final byte UNPAID_APPLICANT_QUERY = 0x15;    // 생활관 별 미납부자 조회
  static final byte TUBERCULOSIS_CERTIFICATE_QUERY = 0x16; // 생활관 별 결핵진단서 조회
  static final byte WITHDRAWAL_APPLICANT_QUERY = 0x17; // 생활관 별 퇴사신청자 조회
  static final byte REFUND_REQUEST = 0x18;            // 환불 요청
  static final byte RESPONSE_APPLICATION_INFO = 0x19;  // 입사 신청 정보
  static final byte RESPONSE_WITHDRAWAL_APPLICATION_INFO = 0x1A;  // 퇴사 신청 정보
  static final byte RESPONSE_SCHEDULE_INFO = 0x1B;    // 선발 일정 정보
  static final byte RESPONSE_SCHEDULE = 0x1C;        // 선발 일정 등록
  static final byte RESPONSE_FEE_INFO = 0x1D;        // 사용료 및 급식비 정보
  static final byte RESPONSE_FEE = 0x1E;             // 사용료 및 급식비 등록
  static final byte SCHEDULE_QUERY = 0x1F;
  static final byte COST_QUERY = 0x20;
}

package com.eztech.fitrans.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCodeEnum implements Message {
  ER0000,
  ER0001,
  ER0002,
  ER0003,//{0} không được để trống
  ER0004,
  ER0005,//{0} không đúng định dạng
  ER0006,
  ER0007,
  ER0008,
  ER0009,
  ER0010,//{0} chỉ được phép nhập tối đa {1} ký tự
  ER0011,
  ER0012,//{0} không tồn tại trên hệ thống hoặc đang ở trạng thái không có hiệu lực
  ER0013,
  ER0014,
  ER0015, //{0} is duplicate
  ER9999, //{0} => dùng để custom thông báo không có sẵn
}

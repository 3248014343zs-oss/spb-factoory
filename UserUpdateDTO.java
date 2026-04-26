package com.factory.dto;

import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Data
public class UserUpdateDTO {
    @Size(min = 2, max = 20, message = "昵称长度2-20")
    private String nickname;

    @Email(message = "邮箱格式不正确")
    private String email;

    private String phone;
}
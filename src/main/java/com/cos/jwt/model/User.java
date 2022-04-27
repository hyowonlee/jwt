package com.cos.jwt.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String username;
    private String password;
    private String roles; // USER, ADMIN 이런식으로 ,로 구분할거기에 getter가 하나 필요

    public List<String> getRoleList()
    {
        if(this.roles.length() > 0)
        {
            return Arrays.asList(this.roles.split(",")); // , 로 구별된 role을 구분할것
        }
        return new ArrayList<>(); // 빈상태면 null이 안뜨게만 만들어줄것
    }
}

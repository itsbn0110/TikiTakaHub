package com.manageleaguefootball.demo.dto;

import lombok.Data;


@Data
public class PlayerDTO {
  private  String id;
  private String name;
  private String role;
  private int age;
  private int goal;
  private int assist;
  private String idTeam;
  private String avatar;
}
